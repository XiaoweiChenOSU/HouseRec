package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Reminder;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AppDefault.AppDefaultActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;

public class ReminderActivity extends AppDefaultActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int contentViewLayoutRes() {
        return R.layout.reminder_layout;
    }

    @NonNull
    @Override
    protected Fragment createInitialFragment() {
        return ReminderFragment.newInstance();
    }


}
