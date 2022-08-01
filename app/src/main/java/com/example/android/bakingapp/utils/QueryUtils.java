package com.example.android.bakingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.bakingapp.ui.Ingredient;
import com.example.android.bakingapp.ui.Recipe;
import com.example.android.bakingapp.ui.Step;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by margarita baltakiene on 23/06/2018.
 */

public final class QueryUtils {
    /**
     * Constant value of the recipe id key from JSON object
     */
    static final String RECIPE_ID = "id";

    /**
     * Constant value of the recipe name key from JSON object
     */
    static final String RECIPE_NAME = "name";

    /**
     * Constant value of the ingredients key from JSON object
     */
    static final String RECIPE_INGREDIENTS = "ingredients";

    /**
     * Constant value of the quantity key from JSON object
     */
    static final String INGREDIENT_QUANTITY = "quantity";

    /**
     * Constant value of the measure key from JSON object
     */
    static final String INGREDIENT_MEASURE = "measure";

    /**
     * Constant value of the ingredient key from JSON object
     */
    static final String INGREDIENT_NAME = "ingredient";

    /**
     * Constant value of the recipe steps key from JSON object
     */
    static final String RECIPE_STEPS = "steps";

    /**
     * Constant value of the step id key from JSON object
     */
    static final String STEP_ID = "id";

    /**
     * Constant value of the step short description key from JSON object
     */
    static final String STEP_SHORT_DESCRIPTION = "shortDescription";

    /**
     * Constant value of the step description key from JSON object
     */
    static final String STEP_DESCRIPTION = "description";

    /**
     * Constant value of the video url key from JSON object
     */
    static final String VIDEO_URL = "videoURL";

    /**
     * Constant value of the thumbnail url key from JSON object
     */
    static final String THUMBNAIL_URL = "thumbnailURL";


    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
        throw new AssertionError("No QueryUtils instances allowed!");
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     *
     * @param url is a given URL
     * @return String output of the response
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    Log.e(LOG_TAG, "Service unavailable: " + urlConnection.getResponseCode());
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the recipes JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Reads the input stream of byte code and converts it to string data object
     *
     * @param inputStream stream of byte code
     * @return String object
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * The method checks if device is connected to the Internet.
     *
     * @param context of the Activity
     * @return true is Internet is connected
     */
    public static boolean isConnected(Context context) {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public static List<Recipe> fetchRecipeData(URL url) {


        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Recipe} object
        List<Recipe> recipes = extractFeaturesFromJson(jsonResponse);

        // Return the {@link Recipe}
        return recipes;
    }

    /**
     * Extract features from JSON object
     *
     * @param recipesJSON string JSON response from the server
     * @return list of Recipe objects
     */
    private static List<Recipe> extractFeaturesFromJson(String recipesJSON) {
        // If the JSON string is empty or null, then return early.

        if (TextUtils.isEmpty(recipesJSON)) {
            return null;
        }
        List<Recipe> recipes = new ArrayList();
        try {
            JSONArray resultsArray = new JSONArray(recipesJSON);

            for (int i = 0; i < resultsArray.length(); i++) {
                int id = resultsArray.getJSONObject(i).optInt(RECIPE_ID);
                String title = resultsArray.getJSONObject(i).optString(RECIPE_NAME);
                List<Ingredient> ingredients = null;
                JSONArray ingredientsJSON = resultsArray.getJSONObject(i).optJSONArray(RECIPE_INGREDIENTS);
                if (ingredientsJSON != null) {
                    ingredients = new ArrayList();
                    for (int j = 0; j < ingredientsJSON.length(); j++) {
                        String quantity = ingredientsJSON.getJSONObject(j).optString(INGREDIENT_QUANTITY);
                        String measure = ingredientsJSON.getJSONObject(j).optString(INGREDIENT_MEASURE);
                        String ingredientName = ingredientsJSON.getJSONObject(j).optString(INGREDIENT_NAME);
                        ingredients.add(new Ingredient(quantity, measure, ingredientName));
                    }
                }
                List<Step> steps = null;
                JSONArray stepsJSON = resultsArray.getJSONObject(i).optJSONArray(RECIPE_STEPS);
                if (stepsJSON != null) {
                    steps = new ArrayList();
                    for (int k = 0; k < stepsJSON.length(); k++) {
                        int stepid = stepsJSON.getJSONObject(k).optInt(STEP_ID);
                        String shortDescription = stepsJSON.getJSONObject(k).optString(STEP_SHORT_DESCRIPTION);
                        String description = stepsJSON.getJSONObject(k).optString(STEP_DESCRIPTION);
                        String videoUrl = stepsJSON.getJSONObject(k).optString(VIDEO_URL);
                        String thumbnailUrl = stepsJSON.getJSONObject(k).optString(THUMBNAIL_URL);
                        steps.add(new Step(stepid, shortDescription, description, videoUrl, thumbnailUrl));
                    }
                }

                recipes.add(new Recipe(id, title, ingredients, steps));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the recipes JSON results", e);
        }
        return recipes;
    }


}
