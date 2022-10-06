package com.okstate.VisualComputingandImageProcessingLab.HouseRec.AddToDo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AppDefault.AppDefaultActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;

public class AddToDoActivity extends AppDefaultActivity {
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int contentViewLayoutRes() {
        return R.layout.activity_add_to_do;
    }

    @NonNull
    @Override
    protected Fragment createInitialFragment() { return AddToDoFragment.newInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}

