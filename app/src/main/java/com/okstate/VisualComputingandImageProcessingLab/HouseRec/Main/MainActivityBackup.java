package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.facebook.stetho.Stetho;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AppDefault.AppDefaultActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;

import java.io.File;

import io.github.skyhacker2.sqliteonweb.SQLiteOnWeb;

public class MainActivityBackup extends AppDefaultActivity {
    //Button testButton;
    private File appPath;
    private LayoutDbHelper HouseRec;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
//        checkModel();

        appPath = new File(Environment.getExternalStorageDirectory() + "/" + R.string.app_name);
        if (!appPath.exists()&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            appPath.mkdirs();
        }
        Stetho.initializeWithDefaults(this);
        SQLiteOnWeb.init(this).start();



    }



    @Override
    protected int contentViewLayoutRes() {
        return R.layout.activity_main;
    }

    @NonNull
    @Override
    protected Fragment createInitialFragment() {
        return MainFragment.newInstance();
    }


}


