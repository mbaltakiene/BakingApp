package com.example.android.bakingapp;

import android.content.Intent;


import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingapp.ui.DetailsActivity;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;


/**
 * Created by margarita baltakiene on 30/06/2018.
 */

public class DetailsActivityUITest {

    private static final String EXTRA_KEY = "recipe";

    @Rule
    public ActivityScenarioRule<DetailsActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(DetailsActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mActivityScenarioRule.getScenario().onActivity(activity -> {
            Recipe recipeMock = Recipe.mockObject();
            activity.getIntent().putExtra(EXTRA_KEY, recipeMock);
            mIdlingResource = activity.getIdlingResource();
        });

        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    /**
     * Test positions the element on {@link RecyclerView} and clicks on it.
     * The new fragment should be displayed with the
     * description {@link TextView} and Next {@link Button}.
     * The {@link Button} Prev is hidden.
     */
    @Test
    public void clickOnRecyclerViewItem_showsRecipeStepsFragment() {

        onView(withId(R.id.ingredients_list_item))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.step_description_text_view))
                .check(matches(isDisplayed()));

        onView(withId(R.id.next_button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.prev_button))
                .check(matches(not(isDisplayed())));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}
