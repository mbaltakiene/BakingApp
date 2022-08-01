package com.example.android.bakingapp.ui;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeWidgetService;
import com.example.android.bakingapp.data.RecipeContract.RecipeEntry;

import java.util.ArrayList;

/**
 * Created by margarita baltakiene on 24/06/2018.
 */

public class DetailsActivity extends AppCompatActivity implements
        IngredientsAdapter.OnItemClickListener, VideoStepsFragment.OnButtonClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Extra key from MainActivity
     */
    private static final String EXTRA_KEY = "recipe";

    /**
     * Identifier for the recipe data loader
     */
    private static final int RECIPE_DETAILS_LOADER = 0;

    /**
     * Recipe object from MainActivity
     */
    Recipe mRecipe;

    /**
     * Variable to distinguish whether the device is a tablet or a phone
     */
    private boolean mTwoPane;

    /**
     * Variable to whether the recipe has been added to widget
     */
    private boolean addedToWidget = false;

    /**
     * Idling resource used for data loading testing
     */
    @Nullable
    private SimpleIdlingResource mIdlingResource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle(R.string.title_activity_details);


        mRecipe = getIntent().getParcelableExtra(EXTRA_KEY);

        mIdlingResource = (SimpleIdlingResource) getIdlingResource();

        getLoaderManager().initLoader(RECIPE_DETAILS_LOADER, null, this);

        if (findViewById(R.id.steps_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
        if (savedInstanceState == null) {

            IngredientsListFragment ingredientsFragment = new IngredientsListFragment();
            ingredientsFragment.setArguments(getIntent().getExtras());
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(R.id.ingredients_container, ingredientsFragment)
                    .commit();
        }

    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }


    @Override
    public void onBackPressed() {
        // If the back is pressed when Video Steps Container displayed,
        // the ingredients list will be shown
        if ((findViewById(R.id.video_steps_container) != null && !mTwoPane)) {
            setTitle(R.string.title_activity_details);
            IngredientsListFragment ingredientsFragment = new IngredientsListFragment();
            ingredientsFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ingredients_container, ingredientsFragment)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(int position) {
        int startingPositionInList = mRecipe.getIngredients().size();

        if (position >= startingPositionInList) {
            VideoStepsFragment videoStepsFragment = new VideoStepsFragment();
            videoStepsFragment.setArguments(getIntent().getExtras());
            videoStepsFragment.setListIndex(position - startingPositionInList);
            if (mTwoPane) {
                videoStepsFragment.setTwoPane(mTwoPane);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.steps_container, videoStepsFragment)
                        .commit();
            } else {
                setTitle(mRecipe.getSteps().get(position - startingPositionInList).getShortDescription());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ingredients_container, videoStepsFragment)
                        .commit();
            }
        }
    }


    @Override
    public void onButtonClick(int position) {
        setTitle(mRecipe.getSteps().get(position).getShortDescription());
        VideoStepsFragment videoStepsFragment = new VideoStepsFragment();
        videoStepsFragment.setArguments(getIntent().getExtras());
        videoStepsFragment.setListIndex(position);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ingredients_container, videoStepsFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem addWidget = menu.findItem(R.id.action_add);
        MenuItem removeWidget = menu.findItem(R.id.action_remove);
        if (addedToWidget) {
            addWidget.setVisible(false);
            removeWidget.setVisible(true);
        } else {
            addWidget.setVisible(true);
            removeWidget.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            insertRecipe();
            addedToWidget = true;
            return true;
        } else if (id == R.id.action_remove) {
            addedToWidget = false;
            deleteRecipe();
            return true;
        } else if (id == android.R.id.home) {
            if ((findViewById(R.id.video_steps_container) != null && !mTwoPane)) {
                setTitle(R.string.title_activity_details);
                IngredientsListFragment ingredientsFragment = new IngredientsListFragment();
                ingredientsFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ingredients_container, ingredientsFragment)
                        .commit();
                return true;
            } else {
                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                return true;
            }


        }
        return false;
    }

    private void deleteRecipe() {
        String selection = RecipeEntry.COLUMN_RECIPE_ID + "=" + mRecipe.getRecipeId();
        // Call the ContentResolver to delete the recipe at the given content recipe id.
        int rowsDeleted = getContentResolver().delete(RecipeEntry.CONTENT_URI, selection, null);
        if (rowsDeleted == 0) {
            // No rows were deleted.
            Toast.makeText(this, getString(R.string.delete_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful
            Toast.makeText(this, getString(R.string.delete_successful),
                    Toast.LENGTH_SHORT).show();
            RecipeWidgetService.startActionSetRecipeIngredientsToWidget(this);
        }
    }


    private void insertRecipe() {
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

        values.put(RecipeEntry.COLUMN_RECIPE_ID, id);
        values.put(RecipeEntry.COLUMN_RECIPE_NAME, title);
        values.put(RecipeEntry.COLUMN_RECIPE_INGREDIENTS, ingredientList);


        Uri newUri = getContentResolver().insert(RecipeEntry.CONTENT_URI, values);
        if (newUri == null) {
            // There was an error with insertion.
            Toast.makeText(this, getString(R.string.insert_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful
            Toast.makeText(this, getString(R.string.insert_successful),
                    Toast.LENGTH_SHORT).show();
            RecipeWidgetService.startActionSetRecipeIngredientsToWidget(this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that contains recipe id
        String[] projection = {
                RecipeEntry._ID,
                RecipeEntry.COLUMN_RECIPE_ID};

        String selection = RecipeEntry.COLUMN_RECIPE_ID + "=" + mRecipe.getRecipeId();

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                RecipeEntry.CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
        if (cursor == null || cursor.getCount() < 1) {
            addedToWidget = false;
            return;
        }
        if (cursor.moveToFirst()) {
            addedToWidget = true;
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        addedToWidget = false;
    }
}
