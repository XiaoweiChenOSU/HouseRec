package com.okstate.VisualComputingandImageProcessingLab.HouseRec.AppDefault;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;

//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;



public abstract class AppDefaultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentViewLayoutRes());
        setUpInitialFragment(savedInstanceState);

    }

    private void setUpInitialFragment(@Nullable Bundle savedInstanceState) {
        try {
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, createInitialFragment())
                        .commit();
            }
        }catch (Exception e){
            Log.d("setUpInitialFragment", "initial wrong");
        }

    }

    @LayoutRes
    protected abstract int contentViewLayoutRes();

    @NonNull
    protected abstract Fragment createInitialFragment();
}
