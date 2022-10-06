package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;

import java.util.ArrayList;

public class DataBaseModifier extends AppCompatActivity {
    public static final int SYSTEM_ALERT_WINDOW_PERMISSION = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.databasetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar_items);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        //video_to_play = R.raw.matrix;
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        //showSystemUI();


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            RuntimePermissionForUser();
        }

        fillListview();
}


    public void RuntimePermissionForUser() {

        Intent PermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(PermissionIntent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    public void fillListview() {
        ListView myListview = findViewById(R.id.list_dogs);
        LayoutDbHelper dbhelper = new LayoutDbHelper(this);

        //ArrayList<Dog> dogList = dbhelper.getAllData_Dog();

        //Custom_Adaptor_Example myAdapter = new Custom_Adaptor_Example(dogList, this);
       // myListview.setAdapter(myAdapter);
    }
}
