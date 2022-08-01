package com.example.android.bakingapp;

import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingapp.ui.DetailsActivity;
import com.example.android.bakingapp.ui.Recipe;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;


/**
 * Created by margarita baltakiene on 30/06/2018.
 */

public class DetailsActivityUITest {

    private static final String EXTRA_KEY = "recipe";

    @Rule
    public ActivityTestRule<DetailsActivity> mActivityTestRule =
            new ActivityTestRule<DetailsActivity>(DetailsActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Recipe recipeMock = Recipe.mockObject();
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_KEY, recipeMock);

                    return intent;
                }
            };

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }


    /**
     * Test positions the element on {@link RecyclerView} and clicks on it.
     * The new fragment should be displayed with the description {@link TextView} and Next {@link Button}.
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
