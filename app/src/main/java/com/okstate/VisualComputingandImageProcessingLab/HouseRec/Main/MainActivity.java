package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.TextView;


import com.facebook.stetho.Stetho;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.ClassifierActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutContract;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.ModelActivity;
import com.vuzix.hud.actionmenu.ActionMenuActivity;
import android.app.Activity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;

import java.io.File;
import java.util.Calendar;




import io.github.skyhacker2.sqliteonweb.SQLiteOnWeb;

public class MainActivity extends ActionMenuActivity {

    private MenuItem StartItem;
    private MenuItem UpdateItem;
    private File appPath;
    private String layoutname;
    private LayoutDbHelper HouseRec;
    private LayoutDbHelper db = new LayoutDbHelper(getContext());
    ModelActivity modelget = new ModelActivity();
    public static final String LAYOUT_NAME = "LAYOUT_NAME";
    protected TextView startText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appPath = new File(Environment.getExternalStorageDirectory() + "/" + R.string.app_name);
        if (!appPath.exists()&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            appPath.mkdirs();
        }
        Stetho.initializeWithDefaults(this);
        SQLiteOnWeb.init(this).start();

        startText = findViewById(R.id.mainTextView3);

        if(db.getLayout()){
            layoutname = "Office";
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
            startText.setText(null);
            Context context = this.getContext();
            Intent newTodo = new Intent(getContext(), ClassifierActivity.class);
            newTodo.putExtra(LAYOUT_NAME, layoutname);
//            startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM);
            context.startActivity(newTodo);
        }


    }

    @Override
    protected boolean onCreateActionMenu(Menu menu) {
        super.onCreateActionMenu(menu);

        getMenuInflater().inflate(R.menu.menu, menu);

        UpdateItem = menu.findItem(R.id.item1);
        updateMenuItems();

        return true;
    }

    @Override
    protected boolean alwaysShowActionMenu() {
        return false;
    }

    private void updateMenuItems() {
        if (StartItem == null) {
            return;
        }

        UpdateItem.setEnabled(true);
    }


    //Action Menu Click events
    //This events where register via the XML for the menu definitions.
    public boolean Start(MenuItem item){

        showToast("Hello World!");
        return false;
    }

    public void Update(MenuItem item){
        layoutname = "Office";
        if(!db.getLayout()){
            HouseRec = new LayoutDbHelper(getContext());
            SQLiteDatabase House = HouseRec.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(LayoutContract.LayoutTable.COLUMN_NAME, layoutname);
            values.put(LayoutContract.LayoutTable.COLUMN_DESCRIPTION, layoutname);
            values.put(LayoutContract.LayoutTable.COLUMN_REMINDTIME, String.valueOf(Calendar.getInstance().getTime()));
            // Insert the new row, returning the primary key value of the new row
            try {
                long newRowId = House.insert(LayoutContract.LayoutTable.TABLE_NAME, null, values);
            }finally {
                House.close();
            }
            modelget.updateModel(layoutname);
        }else{
            modelget.updateModel(layoutname);
        }
        showToast("Update successfully!");
        this.closeActionMenu(true);
    }


    private void showToast(final String text){

        final Activity activity = this;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
