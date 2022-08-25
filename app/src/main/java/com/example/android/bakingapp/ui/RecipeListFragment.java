package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.FragmentRecipeListBinding;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.utils.QueryUtils;
import com.example.android.bakingapp.viewmodel.RecipeListViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by margarita baltakiene on 23/08/2022.
 */
public class RecipeListFragment extends Fragment {

    /**
     * Constant String value for the extra key
     */
    private static final String EXTRA_KEY = "recipe";

    /**
     * Constant to set GridView Adapter layout parameter onSaveInstanceState
     */
    private static final String ADAPTER_ITEMS = "adapterData";

    /**
     * ViewModel object
     */
    private RecipeListViewModel mViewModel;

    /**
     * Data binding object
     */
    private FragmentRecipeListBinding mBinding;

    /**
     * Adapter used in grid view
     */
    MasterListAdapter mAdapter;

    /**
     * Idling resource used for data loading testing
     */
    @Nullable
    SimpleIdlingResource mIdlingResource;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
    mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container,
            false);
    mAdapter = new MasterListAdapter(getContext());
        mBinding.masterListGridView.setAdapter(mAdapter);
        mBinding.masterListGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
            Recipe currentRecipe = mAdapter.getItem(position);
            Intent detailsIntent = new Intent(mBinding.masterListGridView.getContext(),
                    DetailsActivity.class);
            detailsIntent.putExtra(EXTRA_KEY, currentRecipe);
            startActivity(detailsIntent);
        }
    });
    if (savedInstanceState != null) {
        ArrayList<Recipe> recipes = savedInstanceState.getParcelableArrayList(ADAPTER_ITEMS);
        if (recipes != null && recipes.size() > 0) {
            mAdapter.setRecipeData(recipes);
        } else {
            reloadData();
        }
    } else {
        reloadData();
    }
    return mBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ADAPTER_ITEMS, (ArrayList<Recipe>) mAdapter.getRecipeData());
    }

    /**
     * Reloading data from internet source
     */
    private void reloadData() {
        mViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class);
        mBinding.setRecipeListViewModel(mViewModel);
        if (QueryUtils.isConnected(getContext())) {
            showRecipeDataView();
            mIdlingResource =
                    (SimpleIdlingResource) ((MainActivity) getActivity()).getIdlingResource();
            mViewModel.getRecipes().observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
                @Override
                public void onChanged(List<Recipe> recipes) {
                    if (recipes != null) {
                        mAdapter.setRecipeData(recipes);
                        RecipeListFragment.this.showRecipeDataView();
                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(true);
                        }
                    } else {
                        RecipeListFragment.this.showErrorMessage(RecipeListFragment.this.getString(R.string.no_recipes));
                    }
                }
            });
        } else {
            showErrorMessage(getString(R.string.no_internet));
        }
    }


    /**
     * The method shows the error message on unsuccessful load or
     * when the device has no Internet connection.
     *
     * @param errorType is the error message to be displayed to the user
     */
    private void showErrorMessage(String errorType) {
        mBinding.masterListGridView.setVisibility(View.GONE);
        mBinding.errorTextView.setText(errorType);
        mBinding.errorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * The method shows the recipes in the grid view on successful data load
     */
    private void showRecipeDataView() {
        mBinding.errorTextView.setVisibility(View.GONE);
        mBinding.masterListGridView.setVisibility(View.VISIBLE);
    }

}