package com.example.android.bakingapp.ui;


import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeContract.RecipeEntry;
import com.example.android.bakingapp.databinding.ActivityDetailsBinding;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.viewmodel.DetailsActivityViewModel;
import com.example.android.bakingapp.widget.RecipeWidgetService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.test.espresso.IdlingResource;

/**
 * Created by margarita baltakiene on 24/06/2018.
 */

public class DetailsActivity extends AppCompatActivity implements
        IngredientsAdapter.OnItemClickListener, VideoStepsFragment.OnButtonClickListener {

    /**
     * Extra key from MainActivity
     */
    private static final String EXTRA_KEY = "recipe";

    /**
     * Recipe object from MainActivity
     */
    Recipe mRecipe;

    /**
     * Variable to distinguish whether the device is a tablet or a phone
     */
    private boolean mTwoPane;

    /**
     * Variable to distinguish whether the recipe has been added to widget
     */
    private boolean addedToWidget = false;

    /**
     * Idling resource used for data loading testing
     */
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Binding views object
     */
    private ActivityDetailsBinding mBinding;

    /**
     * View model for DetailsActivity
     */
    private DetailsActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_details);

        mRecipe = getIntent().getParcelableExtra(EXTRA_KEY);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        mViewModel = new DetailsActivityViewModel(getApplication(), mRecipe.getRecipeId());
        mBinding.setDetailActivityViewModel(mViewModel);

        mIdlingResource = (SimpleIdlingResource) getIdlingResource();

        final Observer<Boolean> deleteInsertObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean isDeleted) {
                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(true);
                }
                addedToWidget = !Boolean.TRUE.equals(mViewModel.isDeleted.getValue());
            }
        };
        mViewModel.isDeleted.observe(this, deleteInsertObserver);

        if (mBinding.stepsContainer != null) {
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
        if (findViewById(R.id.steps_container) != null && !mTwoPane) {
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
                videoStepsFragment.setTwoPane(true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.steps_container, videoStepsFragment)
                        .commit();
            } else {
                setTitle(mRecipe.getSteps().get(position -
                        startingPositionInList).getShortDescription());
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
            if (findViewById(R.id.video_steps_container) != null && !mTwoPane) {
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
        // Call the ContentResolver to delete the recipe at the given content recipe id.
        int rowsDeleted = mViewModel.deleteRecipe(RecipeEntry.COLUMN_RECIPE_ID + "=" +
                mRecipe.getRecipeId());
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
        //Uri newUri = getContentResolver().insert(RecipeEntry.CONTENT_URI, values);
        Uri newUri = mViewModel.insertRecipe(mRecipe);
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

}
