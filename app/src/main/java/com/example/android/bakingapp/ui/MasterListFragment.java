package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.utils.QueryUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by margarita baltakiene on 23/06/2018.
 */

public class MasterListFragment extends Fragment {


    /**
     * Constant String value for the Recipe API path
     */
    private static final String RECIPE_API_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    /**
     * Constant String value for the extra key
     */
    private static final String EXTRA_KEY = "recipe";

    /**
     * Constant to set GridView Adapter layout parameter onSaveInstanceState
     */
    private static final String ADAPTER_ITEMS = "adapterData";

    /**
     * Error message Text View
     */
    @BindView(R.id.error_text_view)
    TextView mErrorTextView;

    /**
     * Grid view of recipes
     */
    @BindView(R.id.master_list_grid_view)
    GridView mGridView;

    /**
     * Adapter used in grid view
     */
    MasterListAdapter mAdapter;

    /**
     * Idling resource used for data loading testing
     */
    @Nullable
    SimpleIdlingResource mIdlingResource;

    /**
     * Mandatory constructor for instantiating the fragment
     */
    public MasterListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new MasterListAdapter(getContext());
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Recipe currentRecipe = mAdapter.getItem(position);
                Intent detailsIntent = new Intent(mGridView.getContext(), DetailsActivity.class);
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
        return rootView;
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
        if (QueryUtils.isConnected(getContext())) {
            showRecipeDataView();
            mIdlingResource = (SimpleIdlingResource) ((MainActivity) getActivity()).getIdlingResource();
            new FetchRecipeTask().execute();
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
        mGridView.setVisibility(View.GONE);
        mErrorTextView.setText(errorType);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * The method shows the recipes in the grid view on successful data load
     */
    private void showRecipeDataView() {
        mErrorTextView.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);
    }


    public class FetchRecipeTask extends AsyncTask<String, Void, List<Recipe>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Recipe> doInBackground(String... params) {

            URL requestUrl = null;
            try {
                requestUrl = new URL(RECIPE_API_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                List<Recipe> recipies = QueryUtils
                        .fetchRecipeData(requestUrl);

                return recipies;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            if (recipes != null) {
                mAdapter.setRecipeData(recipes);
                showRecipeDataView();
                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(true);
                }

            } else {
                showErrorMessage(getString(R.string.no_recipes));
            }
        }
    }
}



