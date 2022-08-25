package com.example.android.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.bakingapp.data.RecipeContract;

import androidx.annotation.Nullable;

/**
 * Created by margarita baltakiene on 29/06/2018.
 */

public class RecipeWidgetService extends IntentService {


    public static final String ACTION_SET_RECIPE_INGREDIENTS_TO_WIDGET = "com.example.android.mygarden.action.set_recipe_ingredients_to_widget";


    public RecipeWidgetService() {
        super("RecipeWidgetService");
    }


    public static void startActionSetRecipeIngredientsToWidget(Context context) {
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.setAction(ACTION_SET_RECIPE_INGREDIENTS_TO_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SET_RECIPE_INGREDIENTS_TO_WIDGET.equals(action)) {
                handleActionSetRecipeIngredientsToWidget();
            }
        }

    }

    /**
     * The method loads the recipes from the data base and selects the first.
     * Then the data is passed to the widget.
     */
    private void handleActionSetRecipeIngredientsToWidget() {

        Uri RECIPE_URI = RecipeContract.BASE_CONTENT_URI.buildUpon().appendPath(RecipeContract.PATH_RECIPES).build();
        String[] columns = {
                RecipeContract.RecipeEntry.COLUMN_RECIPE_ID,
                RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME,
                RecipeContract.RecipeEntry.COLUMN_RECIPE_INGREDIENTS};

        Cursor cursor = getContentResolver().query(
                RECIPE_URI,
                columns,
                null,
                null,
                null);
        String recipeName = "";
        String recipeIngredients = "";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int recipeNameIndex = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME);
            int recipeIngredientsIndex = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_INGREDIENTS);

            recipeName = cursor.getString(recipeNameIndex);
            recipeIngredients = cursor.getString(recipeIngredientsIndex);
            cursor.close();
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        RecipeWidgetProvider.updateRecipeWidgets(this, appWidgetManager, recipeName, recipeIngredients, appWidgetIds);
    }

}
