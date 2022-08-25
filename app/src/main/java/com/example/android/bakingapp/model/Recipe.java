package com.example.android.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by margarita baltakiene on 23/06/2018.
 */

public class Recipe implements Parcelable {

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };


    /**
     * The recipe id
     */
    private int mId;

    /**
     * The recipe name
     */
    private String mRecipeName;


    /**
     * The list of ingredients
     */
    private List<Ingredient> mIngredients;


    /**
     * The list of steps
     */
    private List<Step> mSteps;


    public Recipe(int id, String name, List<Ingredient> ingredients, List<Step> steps) {
        mId = id;
        mRecipeName = name;
        mIngredients = ingredients;
        mSteps = steps;
    }

    protected Recipe(Parcel in) {
        mId = in.readInt();
        mRecipeName = in.readString();
        mIngredients = in.createTypedArrayList(Ingredient.CREATOR);
        mSteps = in.createTypedArrayList(Step.CREATOR);
    }

    /**
     * Mock object for the UI testing
     *
     * @return mock object populated with data
     */
    @Nullable
    public static Recipe mockObject() {
        ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
        Ingredient ingredient = new Ingredient("5", "TBSP", "sugar");
        ingredientList.add(ingredient);
        ArrayList<Step> stepList = new ArrayList<Step>();
        Step stepOne = new Step(0, "preheat the oven", "preheat the oven",
                null, null);
        Step stepTwo = new Step(1, "bake", "bake", null,
                null);
        stepList.add(stepOne);
        stepList.add(stepTwo);
        return new Recipe(0, "Test Recipe", ingredientList, stepList);
    }

    /**
     * Getter method for the recipe id
     *
     * @return the id of the recipe
     */
    public int getRecipeId() {
        return mId;
    }

    /**
     * Getter method for the recipe name
     *
     * @return the id of the recipe
     */
    public String getRecipeName() {
        return mRecipeName;
    }

    /**
     * Getter method for the
     *
     * @return the ingredients of the recipe
     */
    public List<Ingredient> getIngredients() {
        return mIngredients;
    }

    /**
     * Getter method for the
     *
     * @return the ingredients of the recipe
     */
    public List<Step> getSteps() {
        return mSteps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mRecipeName);
        parcel.writeTypedList(mIngredients);
        parcel.writeTypedList(mSteps);
    }
}
