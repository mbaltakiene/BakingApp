package com.example.android.bakingapp.ui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by margarita baltakiene on 23/06/2018.
 */

public class Ingredient implements Parcelable {

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    /**
     * The ingredient quantity
     */
    private String mQuantity;

    /**
     * The ingredient measure
     */
    private String mMeasure;

    /**
     * The ingredient name
     */
    private String mName;

    public Ingredient(String quantity, String measure, String name) {
        mQuantity = quantity;
        mMeasure = measure;
        mName = name;
    }

    protected Ingredient(Parcel in) {
        mQuantity = in.readString();
        mMeasure = in.readString();
        mName = in.readString();
    }

    /**
     * Getter method for the quantity
     *
     * @return quantity
     */
    public String getQuantity() {
        return mQuantity;
    }

    /**
     * Getter method for the measure
     *
     * @return measure
     */
    public String getMeasure() {
        return mMeasure;
    }

    /**
     * Getter method for the ingredient name
     *
     * @return ingredient name
     */
    public String getName() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mQuantity);
        parcel.writeString(mMeasure);
        parcel.writeString(mName);
    }
}
