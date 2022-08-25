package com.example.android.bakingapp.viewmodel;

import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.ui.FetchRecipeClass;
import com.example.android.bakingapp.ui.TaskRunner;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecipeListViewModel extends ViewModel {
    /**
     * Constant String value for the Recipe API path
     */
    private static final String RECIPE_API_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    /**
     * Live Data object to track if the data was loaded or updated
     */
    public final MutableLiveData<List<Recipe>> mLiveData = new MutableLiveData<>();

    /**
     * AsyncTask class to load the data in the background
     */
    private TaskRunner mTaskRunner = new TaskRunner();

    public MutableLiveData<List<Recipe>> getRecipes() {
        mTaskRunner.executeAsync(new FetchRecipeClass(RECIPE_API_URL),
                new TaskRunner.Callback<List<Recipe>>() {
                    @Override
                    public void onComplete(List<Recipe> recipes) {
                        mLiveData.setValue(recipes);
                    }
                });
        return mLiveData;
    }


}