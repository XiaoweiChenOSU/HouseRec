package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.CourseAdapter;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.CourseModal;

import java.lang.reflect.Type;
import java.util.ArrayList;

import kotlin.jvm.internal.Intrinsics;

public class PopUpClass extends AppCompatActivity {
    private EditText courseNameEdt, courseDescEdt;
    private Button addBtn, saveBtn;
    private RecyclerView courseRV;

    // variable for our adapter class and array list
    private CourseAdapter adapter;
    public ArrayList<CourseModal> courseModalArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwinodw);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager var10000 = this.getWindowManager();
        Intrinsics.checkExpressionValueIsNotNull(var10000, "windowManager");
        var10000.getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        this.getWindow().setLayout(900, 500);

        courseNameEdt = findViewById(R.id.idEdtCourseName);
        courseDescEdt = findViewById(R.id.idEdtCourseDescription);
      //  addBtn = findViewById(R.id.idBtnAdd);
        saveBtn = findViewById(R.id.idBtnSave);
       // courseRV = findViewById(R.id.idRVCourses);

        loadData();
        //buildRecyclerView();
       // Intent back = new Intent(this, AddItems_TempTest.class);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is use to add data to array list.
                courseModalArrayList.add(new CourseModal(courseNameEdt.getText().toString(), courseDescEdt.getText().toString()));
                // notifying adapter when new data added.
                adapter.notifyItemInserted(courseModalArrayList.size());
                // calling method to save data in shared prefs.
                saveData();

               // startActivity(back);
            }
        });


    }

    private void buildRecyclerView() {
        // initializing our adapter class.
        adapter = new CourseAdapter(courseModalArrayList, PopUpClass.this);

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        courseRV.setHasFixedSize(true);

        // setting layout manager to our recycler view.
        courseRV.setLayoutManager(manager);

        // setting adapter to our recycler view.
        courseRV.setAdapter(adapter);
    }

    private void loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("courses", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<CourseModal>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        courseModalArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (courseModalArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            courseModalArrayList = new ArrayList<>();
        }
    }

    private void saveData() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(courseModalArrayList);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("courses", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();

        // after saving data we are displaying a toast message.
        Toast.makeText(this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
    }

}
