package com.example.android.bakingapp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;


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
        int layoutItem = 0;
        switch (viewType) {
            case INGREDIENTS_VIEW:
                layoutItem = R.layout.ingredients_item;
                break;
            case STEPS_VIEW:
                layoutItem = R.layout.steps_item;
                break;
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutItem, viewGroup, shouldAttachToParentImmediately);
        final IngredientsAdapterViewHolder viewHolder = new IngredientsAdapterViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(viewHolder.getAdapterPosition());
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

                holder.mIngredientTV.setText(ingredient);
                String measureAndQuantity = quantity + " " + measure;
                holder.mMeasureTV.setText(measureAndQuantity);
                break;
            case STEPS_VIEW:
                String description = mRecipe.getSteps().get(position - mRecipe.getIngredients().size()).getShortDescription();

                if (description == null) {
                    description = "";
                }

                holder.mStepLabelTV.setText(description);
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


    public class IngredientsAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mIngredientTV;
        public final TextView mMeasureTV;
        public final TextView mStepLabelTV;
        public IngredientsAdapterViewHolder(View view) {
            super(view);
            mIngredientTV = (TextView) view.findViewById(R.id.ingredients_label_text_view);
            mMeasureTV = (TextView) view.findViewById(R.id.ingredients_measure_text_view);
            mStepLabelTV = (TextView) view.findViewById(R.id.step_label_text_view);

        }

    }

}
