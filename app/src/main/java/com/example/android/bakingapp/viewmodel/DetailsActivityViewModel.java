package com.example.android.bakingapp.viewmodel;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.bakingapp.data.RecipeContract;
import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Recipe;

import java.util.ArrayList;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class DetailsActivityViewModel extends AndroidViewModel {
    /**
     * Live Data object to track changes in database
     */
    public MutableLiveData<Boolean> isDeleted;

    /**
     * Object to query and update the database
     */
    ContentResolver mContentResolver;

    /**
     * Selected recipe ID
     */
    int mRecipeId;

    public DetailsActivityViewModel(Application application, int recipeId) {
        super(application);
        mContentResolver = application.getContentResolver();
        isDeleted = new MutableLiveData<Boolean>();
        this.mRecipeId = recipeId;
        getDbContentsForRecipe();
    }

    public int deleteRecipe(String selection) {
        // Call the ContentResolver to delete the recipe at the given content recipe id.
        int rowsDeleted = mContentResolver.delete(RecipeContract.RecipeEntry.CONTENT_URI, selection,
                null);
        isDeleted.setValue(true);
        return rowsDeleted;
    }

    public void getDbContentsForRecipe() {
        String[] projection = {
                RecipeContract.RecipeEntry._ID,
                RecipeContract.RecipeEntry.COLUMN_RECIPE_ID};

        String selection = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + "=" + mRecipeId;
        Cursor cursor = mContentResolver.query(RecipeContract.RecipeEntry.CONTENT_URI, projection,
                selection, null, null);
        if (cursor == null || cursor.getCount() < 1) {
            isDeleted.setValue(true);
            return;
        }
        if (cursor.moveToFirst()) {
            isDeleted.setValue(false);
        }
        cursor.close();
    }

    public Uri insertRecipe(Recipe mRecipe) {
        ContentValues values = new ContentValues();

        int id = mRecipe.getRecipeId();
        String title = mRecipe.getRecipeName();
        ArrayList<Ingredient> ingredients = (ArrayList<Ingredient>) mRecipe.getIngredients();

        String ingredientList = "";
        for (Ingredient i : ingredients) {
            String name = i.getName();
            String quantity = i.getQuantity();
            String measure = i.getMeasure();
            ingredientList = ingredientList + name + ": " + quantity + " " + measure + "\n";
        }

        values.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID, id);
        values.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, title);
        values.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_INGREDIENTS, ingredientList);

        Uri newUri = mContentResolver.insert(RecipeContract.RecipeEntry.CONTENT_URI, values);
        isDeleted.setValue(false);
        return newUri;
    }
}
