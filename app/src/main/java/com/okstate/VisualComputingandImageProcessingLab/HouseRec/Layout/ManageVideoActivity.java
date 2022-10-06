package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AppDefault.AppDefaultActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;

public class ManageVideoActivity extends AppDefaultActivity {
    //Button testButton;
    private String layout_name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_main);
        final androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
//        checkModel();
    }



    @Override
    protected int contentViewLayoutRes() {
        return R.layout.manage_main;
    }

    @NonNull
    @Override
    protected Fragment createInitialFragment() {
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        layout_name = (String) b.get(ItemPhotosActivity.LAYOUT_NAME);
//        ManageVideoFrame m = ManageVideoFrame.newInstance();
        ManageVideoFrame m = new ManageVideoFrame();
        Bundle args = new Bundle();
        args.putString(ManageVideoFrame.LAYOUTNAME, layout_name);
        m.setArguments(args);
        return m;
    }

}


