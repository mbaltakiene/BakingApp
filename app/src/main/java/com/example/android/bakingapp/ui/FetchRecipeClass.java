package com.example.android.bakingapp.ui;

import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.utils.QueryUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by margarita baltakiene on 23/08/2022.
 */

public class FetchRecipeClass implements Callable<List<Recipe>> {
    private final String requestURL;

    //store String for URL
    public FetchRecipeClass(String input) {
        this.requestURL = input;
    }

    // similar to "do in background" of the former AsyncTask
    @Override
    public List<Recipe> call() {
        URL requestUrl = null;
        try {
            requestUrl = new URL(this.requestURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            List<Recipe> recipes = QueryUtils.fetchRecipeData(requestUrl);
            return recipes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
