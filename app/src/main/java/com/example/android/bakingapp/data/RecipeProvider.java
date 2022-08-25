package com.example.android.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bakingapp.data.RecipeContract.RecipeEntry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by margarita baltakiene on 29/06/2018.
 */

public class RecipeProvider extends ContentProvider {

    /* Tag for the log messages */
    public static final String LOG_TAG = RecipeProvider.class.getSimpleName();
    /* URI matcher code for the content URI for the recipes table */
    private static final int RECIPES = 100;
    /* URI matcher code for the content URI for a single recipe in the recipes table */
    private static final int RECIPE_ID = 101;
    /* UriMatcher object to match a content URI to a corresponding code.*/
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // URI patterns that the provider should recognize.
        sUriMatcher.addURI(RecipeContract.CONTENT_AUTHORITY, RecipeContract.PATH_RECIPES, RECIPES);
        sUriMatcher.addURI(RecipeContract.CONTENT_AUTHORITY, RecipeContract.PATH_RECIPES
                + "/#", RECIPE_ID);
    }

    /* Database helper object */
    private RecipeDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create and initialize a RecipeDbHelper object to gain access to the recipe database.
        mDbHelper = new RecipeDbHelper(getContext());
        return true;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                return RecipeEntry.CONTENT_LIST_TYPE;
            case RECIPE_ID:
                return RecipeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        switch (match) {
            case RECIPES:
                long id = database.insert(RecipeEntry.TABLE_NAME, null, contentValues);
                // If the ID is -1, then the insertion failed. Log an error and return null.
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);

                // Return the new URI with the ID (of the newly inserted row) appended at the end
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case RECIPES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(RecipeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RECIPE_ID:
                // Delete a single row given by the ID in the URI
                selection = RecipeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(RecipeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }
        switch (match) {
            case RECIPES:
                numUpdated = database.update(RecipeEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case RECIPE_ID:
                numUpdated = database.update(RecipeEntry.TABLE_NAME,
                        contentValues,
                        RecipeEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
                cursor = database.query(RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case RECIPE_ID:
                // Extract rows for given ID from the URI.
                selection = RecipeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
}
