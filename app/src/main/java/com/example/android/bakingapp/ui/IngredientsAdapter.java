package com.example.android.bakingapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.IngredientsItemBinding;
import com.example.android.bakingapp.databinding.StepsItemBinding;
import com.example.android.bakingapp.model.Recipe;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by margarita baltakiene on 24/06/2018.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsAdapterViewHolder> {

    private final static int INGREDIENTS_VIEW = 0;
    private final static int STEPS_VIEW = 1;
    private final OnItemClickListener mListener;
    private Recipe mRecipe;
    private Context mContext;

    public IngredientsAdapter(Context context, Recipe recipe, OnItemClickListener listener) {
        mContext = context;
        mRecipe = recipe;
        mListener = listener;
    }

    @Override
    public IngredientsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final IngredientsAdapterViewHolder viewHolder;
        boolean shouldAttachToParentImmediately = false;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        IngredientsItemBinding iBinding = null;
        StepsItemBinding sBinding = null;
        View view;

        int layoutItem = 0;
        switch (viewType) {
            case INGREDIENTS_VIEW:
                layoutItem = R.layout.ingredients_item;
                iBinding = DataBindingUtil.inflate(inflater, layoutItem,
                        viewGroup, shouldAttachToParentImmediately);
                break;
            case STEPS_VIEW:
                layoutItem = R.layout.steps_item;
                sBinding = DataBindingUtil.inflate(inflater, layoutItem,
                        viewGroup, shouldAttachToParentImmediately);

                break;
        }
        if (iBinding != null) {
            view = iBinding.getRoot();
            viewHolder = new IngredientsAdapterViewHolder(iBinding);
        } else {
            view = sBinding.getRoot();
            viewHolder = new IngredientsAdapterViewHolder(sBinding);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(viewHolder.getAbsoluteAdapterPosition());
            }
        });

        return viewHolder;
    }

    /**
     * Depending on the position, the view layout will be different
     *
     * @param position of the element on the RecyclerView
     * @return the constant for element layout selection
     */
    @Override
    public int getItemViewType(int position) {
        if (position >= 0 && position < mRecipe.getIngredients().size()) {
            return INGREDIENTS_VIEW;
        }
        return STEPS_VIEW;
    }

    @Override
    public void onBindViewHolder(IngredientsAdapterViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case INGREDIENTS_VIEW:
                String ingredient = mRecipe.getIngredients().get(position).getName();
                String measure = mRecipe.getIngredients().get(position).getMeasure();
                String quantity = mRecipe.getIngredients().get(position).getQuantity();

                if (ingredient == null) {
                    ingredient = "";
                }

                if (measure == null) {
                    measure = "";
                }

                if (quantity == null) {
                    quantity = "";
                }

                holder.ingredientsItemBinding.ingredientsLabelTextView.setText(ingredient);
                String measureAndQuantity = quantity + " " + measure;
                holder.ingredientsItemBinding.ingredientsMeasureTextView.setText(measureAndQuantity);
                break;
            case STEPS_VIEW:
                String description = mRecipe.getSteps().get(position - mRecipe.getIngredients().size()).getShortDescription();

                if (description == null) {
                    description = "";
                }

                holder.stepsItemBinding.stepLabelTextView.setText(description);
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (null == mRecipe) return 0;
        return mRecipe.getIngredients().size() + mRecipe.getSteps().size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class IngredientsAdapterViewHolder extends RecyclerView.ViewHolder {
        private IngredientsItemBinding ingredientsItemBinding;
        private StepsItemBinding stepsItemBinding;

        public IngredientsAdapterViewHolder(IngredientsItemBinding ingredientsItemBinding) {
            super(ingredientsItemBinding.getRoot());
            this.ingredientsItemBinding = ingredientsItemBinding;
        }

        public IngredientsAdapterViewHolder(StepsItemBinding stepsItemBinding) {
            super(stepsItemBinding.getRoot());
            this.stepsItemBinding = stepsItemBinding;
        }
    }

}