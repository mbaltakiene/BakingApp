package com.example.android.bakingapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by margarita baltakiene on 29/06/2018.
 */

public class RecipeContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.bakingapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPES = "recipes";

    private RecipeContract() {
    }

    public static final class RecipeEntry implements BaseColumns {

        /* The MIME type of the {@link #CONTENT_URI} for a list of recipes. */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/"
                + PATH_RECIPES;

        /* The MIME type of the {@link #CONTENT_URI} for a single recipes. */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/"
                + PATH_RECIPES;

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        public static final String TABLE_NAME = "recipes";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_RECIPE_ID = "id";
        public static final String COLUMN_RECIPE_NAME = "title";
        public static final String COLUMN_RECIPE_INGREDIENTS = "ingredients";
    }
}
