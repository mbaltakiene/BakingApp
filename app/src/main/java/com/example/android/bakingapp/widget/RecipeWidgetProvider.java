package com.example.android.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.example.android.bakingapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                String recipeName, String recipeIngredients, int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        if (TextUtils.isEmpty(recipeName) && TextUtils.isEmpty(recipeIngredients)) {
            // If there is nothing returned from the data base, a default text message will appear on the widget.
            CharSequence widgetText = context.getString(R.string.default_widget_text);
            views.setTextViewText(R.id.widget_recipe_text_view, "");
            views.setTextViewText(R.id.widget_ingredients_label_text_view, widgetText);

        } else {
            // If there is a recipe returned from the data base, its text will appear on the widget.
            views.setTextViewText(R.id.widget_recipe_text_view, recipeName);
            views.setTextViewText(R.id.widget_ingredients_label_text_view, recipeIngredients);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager,
                                           String recipeName, String recipeIngredients, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeName, recipeIngredients, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        RecipeWidgetService.startActionSetRecipeIngredientsToWidget(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}