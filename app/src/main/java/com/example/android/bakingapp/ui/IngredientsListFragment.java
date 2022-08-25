package com.example.android.bakingapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.FragmentIngredientsListBinding;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.ui.IngredientsAdapter.OnItemClickListener;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created by margarita baltakiene on 24/06/2018.
 */

public class IngredientsListFragment extends Fragment {

    /**
     * Extra key from MainActivity
     */
    private static final String EXTRA_KEY = "recipe";

    /**
     * Constant to set RecycleView layout parameter onSaveInstanceState
     */
    private static final String RECYCLER_VIEW_STATE = "viewState";

    /**
     * Ingredient list click listener callback
     */
    OnItemClickListener mCallback;

     /**
     * Adapter for the elements in RecyclerView
     */
    IngredientsAdapter mIngredientsAdapter;

    /**
     * Binding views object
     */
    FragmentIngredientsListBinding mBinding;

    /**
     * Mandatory constructor for instantiating the fragment
     */
    public IngredientsListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ingredients_list,
                container, false);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mBinding.ingredientsListItem.setLayoutManager(layoutManager);

        Bundle bundle = getArguments();
        Recipe recipe = (Recipe) bundle.getParcelable(EXTRA_KEY);

        mIngredientsAdapter = new IngredientsAdapter(getContext(), recipe,
                new IngredientsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mCallback.onItemClick(position);
            }
        });
        mBinding.ingredientsListItem.setAdapter(mIngredientsAdapter);

        if (savedInstanceState != null) {
            Parcelable state = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mBinding.ingredientsListItem.getLayoutManager().onRestoreInstanceState(state);
        }
        return mBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable state = mBinding.ingredientsListItem.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE, state);
    }
}
