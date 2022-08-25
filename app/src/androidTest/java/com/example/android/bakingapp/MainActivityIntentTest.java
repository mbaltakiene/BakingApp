package com.example.android.bakingapp;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.widget.GridView;

import com.example.android.bakingapp.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by margarita baltakiene on 30/06/2018.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentTest {

    private static final String EXTRA_KEY = "recipe";

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mActivityScenarioRule.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                mIdlingResource = activity.getIdlingResource();
            }
        });
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
    }

    /**
     * Test waits for the data load to be complete,
     * positions the element on {@link GridView} and clicks on it.
     * The intent should have a correct extra added.
     */
    @Test
    public void clickGridViewElement_OpensDetailsActivity() {
        onData(anything()).inAdapterView(withId(R.id.master_list_grid_view)).atPosition(0).
                onChildView(withId(R.id.recipe_name_text_view)).perform(click());
        intended(allOf(hasExtraWithKey(EXTRA_KEY)));
    }


    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
