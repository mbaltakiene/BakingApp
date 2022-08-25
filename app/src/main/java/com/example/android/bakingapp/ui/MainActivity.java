package com.example.android.bakingapp.ui;

import android.os.Bundle;
import android.util.Log;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.ActivityMainBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.test.espresso.IdlingResource;


public class MainActivity extends AppCompatActivity {

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MAIN activity", "MAIN activity");
        ActivityMainBinding mBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

}
