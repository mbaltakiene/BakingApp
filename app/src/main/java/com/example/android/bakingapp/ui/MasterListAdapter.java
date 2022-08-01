package com.example.android.bakingapp.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.bakingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margarita baltakiene on 24/06/2018.
 */

public class MasterListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Recipe> mRecipeData;

    /**
     * Constructor to create MasterListAdapter
     *
     * @param context
     */
    public MasterListAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getCount() {
        if (null == mRecipeData) return 0;
        return mRecipeData.size();
    }

    @Override
    public Recipe getItem(int position) {
        return mRecipeData.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        TextView recipeNameTV;
        if (convertView == null) {
            grid = inflater.inflate(R.layout.grid_element, null);

        } else {
            grid = (View) convertView;
        }

        recipeNameTV = (TextView) grid.findViewById(R.id.recipe_name_text_view);

        if (mRecipeData.get(position).getRecipeName() != null) {
            recipeNameTV.setText(mRecipeData.get(position).getRecipeName());
        } else {
            recipeNameTV.setText(R.string.untitled_recipe);
        }

        return grid;
    }

    public void setRecipeData(List<Recipe> recipes) {
        mRecipeData = recipes;
        notifyDataSetChanged();
    }

    public List<Recipe> getRecipeData() {
        return mRecipeData;
    }

}
