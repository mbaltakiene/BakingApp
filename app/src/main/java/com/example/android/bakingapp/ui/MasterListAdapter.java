package com.example.android.bakingapp.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.GridElementBinding;
import com.example.android.bakingapp.model.Recipe;

import java.util.List;

import androidx.databinding.DataBindingUtil;

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

        LayoutInflater inflater = LayoutInflater.from(mContext);

        GridElementBinding binding;

        if(convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.grid_element,
                    parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        }
        else {
            binding = (GridElementBinding) convertView.getTag();
        }

        if (mRecipeData.get(position).getRecipeName() != null) {
            binding.recipeNameTextView.setText(mRecipeData.get(position).getRecipeName());
        } else {
            binding.recipeNameTextView.setText(R.string.untitled_recipe);
        }
        return binding.getRoot();
    }

    public List<Recipe> getRecipeData() {
        return mRecipeData;
    }

    public void setRecipeData(List<Recipe> recipes) {
        mRecipeData = recipes;
        notifyDataSetChanged();
    }

}
