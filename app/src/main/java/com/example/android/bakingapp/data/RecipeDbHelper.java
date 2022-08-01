package com.example.android.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.bakingapp.data.RecipeContract.RecipeEntry;


/**
 * Created by margarita baltakiene on 29/06/2018.
 */

public class RecipeDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = RecipeDbHelper.class.getSimpleName();
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "recipes.db";

    /**
     * Database version
     */
    private static final int DATABASE_VERSION = 1;

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the recipes table
        String SQL_CREATE_RECIPES_TABLE = "CREATE TABLE "
                + RecipeEntry.TABLE_NAME + " ("
                + RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RecipeEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL,"
                + RecipeEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, "
                + RecipeEntry.COLUMN_RECIPE_INGREDIENTS + " TEXT NOT NULL);";

        Log.v(LOG_TAG, SQL_CREATE_RECIPES_TABLE);

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                RecipeEntry.TABLE_NAME + "'");
        // re-create database
        onCreate(db);
    }
}
