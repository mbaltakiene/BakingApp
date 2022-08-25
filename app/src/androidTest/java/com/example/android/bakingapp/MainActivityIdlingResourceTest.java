package com.example.android.bakingapp;

import android.widget.GridView;

import com.example.android.bakingapp.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Created by margarita baltakiene on 30/06/2018.
 */

public class MainActivityIdlingResourceTest {


    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mActivityScenarioRule.getScenario().onActivity(activity -> {
            mIdlingResource = activity.getIdlingResource();
        });
        // To prove that the test fails, omit this call:
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    /**
     * Test waits for the data load to be complete,
     * positions the element on {@link GridView} and clicks on it.
     */
    @Test
    public void idlingResourceTest() {
        onData(anything()).inAdapterView(withId(R.id.master_list_grid_view)).atPosition(0).
                onChildView(withId(R.id.recipe_name_text_view)).perform(click());
    }


    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
