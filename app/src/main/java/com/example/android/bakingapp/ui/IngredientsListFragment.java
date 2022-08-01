package com.example.android.bakingapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.ui.IngredientsAdapter.OnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

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
     * RecyclerView with the ingredients
     */
    @BindView(R.id.ingredients_list_item)
    RecyclerView mRecyclerView;

    /**
     * Adapter for the elements in RecyclerView
     */
    IngredientsAdapter mIngredientsAdapter;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ingredients_list, container, false);
        ButterKnife.bind(this, rootView);


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);


        Bundle bundle = getArguments();
        Recipe recipe = (Recipe) bundle.getParcelable(EXTRA_KEY);

        mIngredientsAdapter = new IngredientsAdapter(getContext(), recipe, new IngredientsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mCallback.onItemClick(position);
            }
        });
        mRecyclerView.setAdapter(mIngredientsAdapter);

        if (savedInstanceState != null) {
            Parcelable state = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(state);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable state = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE, state);
    }
}
