package com.example.android.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by margarita baltakiene on 23/06/2018.
 */

public class Step implements Parcelable {

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };


    /**
     * The preparation step id
     */
    private int mId;

    /**
     * The preparation step short description
     */
    private String mShortDescription;

    /**
     * The preparation step description
     */
    private String mDescription;

    /**
     * The preparation step video url
     */
    private String mVideoUrl;

    /**
     * The preparation step thumbnail url
     */
    private String mThumbnailUrl;

    public Step(int id, String shortDescription, String description, String videoUrl,
                String thumbnailUrl) {
        mId = id;
        mShortDescription = shortDescription;
        mDescription = description;
        mVideoUrl = videoUrl;
        mThumbnailUrl = thumbnailUrl;
    }

    protected Step(Parcel in) {
        mId = in.readInt();
        mShortDescription = in.readString();
        mDescription = in.readString();
        mVideoUrl = in.readString();
        mThumbnailUrl = in.readString();
    }

    /**
     * Getter method for the preparation step id
     *
     * @return preparation step id
     */
    public int getStepId() {
        return mId;
    }

    /**
     * Getter method for the preparation step short description
     *
     * @return short description
     */
    public String getShortDescription() {
        return mShortDescription;
    }

    /**
     * Getter method for the preparation step description
     *
     * @return description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Getter method for the preparation step thumbnail
     *
     * @return thumbnail
     */
    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    /**
     * Getter method for the preparation step video url
     *
     * @return video url
     */
    public String getVideoUrl() {
        return mVideoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mShortDescription);
        parcel.writeString(mDescription);
        parcel.writeString(mVideoUrl);
        parcel.writeString(mThumbnailUrl);
    }
}
