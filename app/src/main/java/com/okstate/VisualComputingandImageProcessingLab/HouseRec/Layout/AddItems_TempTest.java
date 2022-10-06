package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.source.UriSource;
import com.google.android.youtube.player.internal.p;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.CourseAdapter;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.CourseModal;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;
import com.pierfrancescosoffritti.androidyoutubeplayer.utils.YouTubePlayerTracker;
import com.pipvideo.youtubepipvideoplayer.FlyingVideo;
import com.pipvideo.youtubepipvideoplayer.TaskCoffeeVideo;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import io.hamed.floatinglayout.FloatingLayout;
import io.hamed.floatinglayout.callback.FloatingListener;

import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemListActivity.getContext;
import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity.LAYOUT_NAME;
import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.LayoutWrapContentUpdater.wrapContentAgain;


public class AddItems_TempTest extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    public static final int SYSTEM_ALERT_WINDOW_PERMISSION = 7;
    private Uri Video_Location;
    private Spinner spinner_video;
    private ToDoItem mPhotoLayoutPath;
    private String ITEM_ID;
    public static final String LAYOUT_ITEM = "LAYOUT_ITEM";
    public static final String ARG_ITEM_ID = "ITEM_ID";
    public EditText Url;
    //LinearLayout videoContainer;
    //ArrayList<DummyContents> dummyContents = new ArrayList<>();
    ScrollView scrollView;
    YouTubePlayerTracker mTracker = null;
    private Boolean IsFullScreen;
    private ArrayList<CourseModal> courseModalArrayList;

   // private EditText Url = findViewById(R.id.url_prompt);
    String TestType = "Working";
    private FloatingLayout floatingLayout;
    private VideoView videoView;
    public int video_to_play;
    private RelativeLayout videoContainer;
    private FrameLayout RelativelayoutContainer;

   private Button addBtn, saveBtn;
    private EditText courseNameEdt, courseDescEdt;
    //private Button addBtn, saveBtn;
    private RecyclerView courseRV;
    private int seekForwardTime; // 5000 milliseconds
    private int seekBackwardTime; // 5000 milliseconds
    // variable for our adapter class and array list
    private CourseAdapter adapter;
    //MediaController m;
    private ImageButton btn_close, resume, btnfoward, btnback, pause, fullscreen;
    private MediaPlayer mp;
    private boolean IsFloating = false;
    private View test_view;
    private MediaController mediaControls;
    private MediaPlayer mediaPlayer;

    private FloatingListener floatingListener = new FloatingListener() {
        @Override
        public void onCreateListener(View view) {
            initVideoView(view);
            btn_close = view.findViewById(R.id.btn_close);
            pause = view.findViewById(R.id.test_button);
            resume = view.findViewById(R.id.resume_play);
            btnfoward = view.findViewById(R.id.fowardbtn);
            btnback = view.findViewById(R.id.backbtn);
            fullscreen = view.findViewById(R.id.FullscreenButton);
            test_view = view;
            IsFullScreen = false;


            view.findViewById(R.id.backbtn).setOnClickListener(AddItems_TempTest.this);
            view.findViewById(R.id.btn_close).setOnClickListener(AddItems_TempTest.this);
            view.findViewById(R.id.fowardbtn).setOnClickListener(AddItems_TempTest.this);
            view.findViewById(R.id.test_button).setOnClickListener(AddItems_TempTest.this);
           view.findViewById(R.id.resume_play).setOnClickListener(AddItems_TempTest.this);
            //view.findViewById(R.id.fullscreen_2).setOnClickListener(AddItems_TempTest.this);
            view.findViewById(R.id.replay).setOnClickListener(AddItems_TempTest.this);
            view.findViewById(R.id.FullscreenButton).setOnClickListener(AddItems_TempTest.this);
            resume.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);

          //  loop();

            IsFloating = true;

        }

        @Override
        public void onCloseListener() {
            //videoView.stopPlayback();
        }



    };
    public void bottomVideoStart(View view, String videoId) {

        FlyingVideo.get(AddItems_TempTest.this)
                .setFloatMode(TaskCoffeeVideo.FLOAT_MOVE.FREE)
                .setFullScreenToggleEnabled(true, "-YOUR-YOUTUBE-API-KEY-")
                .setVideoStartSecond((mTracker == null) ? 0 : mTracker.getCurrentSecond())
                .coffeeVideoSetup(videoId)
                .setFlyGravity(TaskCoffeeVideo.FLY_GRAVITY.BOTTOM)
                .show(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_sorting_layout);
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
        //Button Play = findViewById(R.id.play_button);



        //YouTubePlayerTracker mTracker = null;
        /*
        view.setOnClickListener(v -> {
            FlyingVideo.get(AddItems_TempTest.this)
                    .setFloatMode(TaskCoffeeVideo.FLOAT_MOVE.FREE)
                    .setVideoStartSecond((mTracker == null) ? 0 : mTracker.getCurrentSecond())
                    .coffeeVideoSetup(String.valueOf(Url.getText()))
                    .show(view);
        });
*/

        //Button ItemButton = view.findViewById(R.id.items);
     //   int width = 100;
       // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.setMargins(15, 15, 15, 15);
        //View view = LayoutInflater.from(AddItems_TempTest.this).inflate(R.layout.youtube_pip_layout, null);

         Url = findViewById(R.id.url_prompt);

        CoordinatorLayout item = (CoordinatorLayout)findViewById(R.id.item_sorting);
        View child = getLayoutInflater().inflate(R.layout.youtube_pip_layout, null);
        //item.addView(child);
        View view = getLayoutInflater().inflate(R.layout.item_sorting_layout,null);
        FrameLayout frame = (FrameLayout)findViewById(R.id.root_container_item);

        Button Play = findViewById(R.id.play_button);

        String Url_String = String.valueOf(Url.getText());

        courseNameEdt = findViewById(R.id.idEdtCourseName);
        courseDescEdt = findViewById(R.id.idEdtCourseDescription);
        //  addBtn = findViewById(R.id.idBtnAdd);
        //saveBtn = findViewById(R.id.idBtnSave);


    findViewById(R.id.play_local).setOnClickListener(this);



        findViewById(R.id.input).setOnClickListener(this);




      //  loadData();

    //    buildRecyclerView();

        initializeUI();

        hideKeyboard(view);

        /*

        Testing UI initialization
        spinner_video = findViewById(R.id.local_video_list);

        spinner_video.setOnItemSelectedListener(this);
        String[] textSizes = getResources().getStringArray(R.array.local_video);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, textSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_video.setAdapter(adapter);
*/





    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       TextView String_test = findViewById(R.id.String_Test);


        if (parent.getId() == R.id.local_video_list) {

            String valueFromSpinner = parent.getItemAtPosition(position).toString();

            LayoutDbHelper db = new LayoutDbHelper(getApplicationContext());

            String directory = db.getDirectory(valueFromSpinner,LAYOUT_NAME);
            if (directory.equals("null")) {
                Toast.makeText((getApplicationContext()), (CharSequence) "No Directory Found. Using Sample Video.", Toast.LENGTH_SHORT).show();
                video_to_play = R.raw.matrix;
                Video_Location = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
            } else {
                Toast.makeText((getApplicationContext()), (CharSequence) "Video Ready to Play", Toast.LENGTH_SHORT).show();

                Video_Location = Uri.parse(directory);


            }
        }

        /*
        if (parent.getId() == R.id.local_video_list) {
            String valueFromSpinner = parent.getItemAtPosition(position).toString();


            if(valueFromSpinner.equals("sample")){


                video_to_play = R.raw.matrix;
                Video_Location  = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
                //String_test.setText(valueFromSpinner);
//                Video_Location  = Uri.parse(Environment.getExternalStorageDirectory()+"/Movies/screen-20210528-144622.mp4");
            }
            else if (valueFromSpinner.equals("input")){
                //String_test.setText(valueFromSpinner);

            }
            else if (valueFromSpinner.equals("sample_2")){

                //video_to_play = R.raw.sample_2;
                //int temp_int = Integer.parseInt(valueFromSpinner);
                video_to_play = getResources().getIdentifier(valueFromSpinner,
                        "raw", getPackageName());
                Video_Location = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
            }
            else if (valueFromSpinner.equals("sample_3")){
                //change this to the location of the video you want to test


//                Video_Location = Uri.parse("/storage/emulated/0/Download/EP.2.1080p.mp4");
               // Video_Location = Uri.parse(Environment.getExternalStorageDirectory()+"/Download/EP.7.1080p.mp4");
            }
            else{
                //Work in progress will be used to play other videos if they existed.
               // video_to_play = getResources().getIdentifier(valueFromSpinner,
               //         "raw", getPackageName());




/*
                if(ItemDetailFragment.Get_Path().equals("")){

                    Toast.makeText((getApplicationContext()), (CharSequence)"No Video Selected Using Sample Video", Toast.LENGTH_LONG).show();
                    video_to_play = R.raw.matrix;
                    Video_Location  = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
                }
                else{
                    Toast.makeText((getApplicationContext()), (CharSequence)"Selected Video Ready to Play", Toast.LENGTH_LONG).show();

                    Video_Location = Uri.parse(ItemDetailFragment.Get_Path());
                }



              //  Video_Location = ItemDetailFragment.Get_Uri_Video();
                //Video_Location = Uri.parse(Environment.getExternalStorageDirectory()+"/Download/" + ItemDetailFragment.Get_File_Name());

            }

        }
*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    //public void onClick(View view) {
       // switch (view.getId()) {
        //    case R.id.play_button:
        //        if (!isNeedPermission())
           //         showFloating();
         //       else
          //          requestPermission();
         //       break;
         //   case R.id.btn_close:
        //        floatingLayout.destroy();
        //        break;
      // }
   // }

    private void initVideoView(View view) {
        videoView = view.findViewById(R.id.video_player);

        videoContainer = view.findViewById(R.id.root_container);
        RelativelayoutContainer = view.findViewById(R.id.FrameLayout_VideoView_container);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
        //Changed uri to Video_Location for testing
        videoView.setVideoURI(Video_Location);
        videoView.start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_local:
                hideKeyboard(view);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (IsFloating) {
                    floatingLayout.destroy();
                }
                if (!isNeedPermission()) {

                    showFloating();

                } else {
                    requestPermission();
                }

                break;
            case R.id.btn_close:
                floatingLayout.destroy();
                IsFloating = false;
                showSystemUI();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.replay:

                RestartVideo();
                Play();
                break;
            case R.id.fullscreen_2:
                //FullScreen();
                //hideSystemUI();

                //findViewById(R.id.fullscreen_2).setVisibility(view.INVISIBLE);
                //findViewById(R.id.minimize_2).setVisibility(View.VISIBLE);
                break;

            case R.id.minimize_2:
                //showSystemUI();
                //findViewById(R.id.fullscreen_2).setVisibility(view.VISIBLE);
                //findViewById(R.id.minimize_2).setVisibility(view.INVISIBLE);
                break;
            case R.id.test_button:
                //hideKeyboard(view);
                //FullScreen(view);

                Pause();
                pause.setVisibility(View.INVISIBLE);
                resume.setVisibility(View.VISIBLE);



                break;
            case R.id.resume_play:
                Play();
                resume.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);
                break;
            case R.id.input:
                onButtonShowPopupWindowClick(view);

                break;

            case R.id.idBtnSave:

                // calling method to save data in shared prefs.
                saveData();



                break;
            case R.id.idBtnAdd:

                courseModalArrayList.add(new CourseModal(courseNameEdt.getText().toString(), courseDescEdt.getText().toString()));
                // notifying adapter when new data added.
                adapter.notifyItemInserted(courseModalArrayList.size());

                break;

            case R.id.fowardbtn:
                Foward();
                Play();
                break;

            case R.id.backbtn:
                Back();
                Play();
                break;
            case R.id.FullscreenButton:
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                //hideSystemUI();
                FullScreen();
                break;
        }

    }


    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);



    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }


    private boolean isNeedPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this);
    }

    private void requestPermission() {
        Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
        );
        startActivityForResult(intent, 25);
    }

    private void showFloating() {
        floatingLayout = new FloatingLayout(getApplicationContext(), R.layout.video_layout3);
        floatingLayout.setFloatingListener(floatingListener);
        floatingLayout.create();
    }


    private void FullScreen(){



        if(!(IsFullScreen)) {

            hideSystemUI();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
         //  DisplayMetrics metrics = new DisplayMetrics();
        //    getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //    videoContainer.setLayoutParams(new FrameLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));


            DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) videoContainer.getLayoutParams();
            params.height =  (metrics.widthPixels-(int) (50 * this.getResources().getDisplayMetrics().density));
            params.width = (metrics.heightPixels-(int) (10 * this.getResources().getDisplayMetrics().density));
            //params.leftMargin = 0;
            videoContainer.setLayoutParams(params);


            IsFullScreen = true;

        }
        else if(IsFullScreen){
            showSystemUI();
            //float pixels =  dp * this.getResources().getDisplayMetrics().density;
           FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoContainer.getLayoutParams();
          //  float factor = holder.itemView.getContext().getResources().getDisplayMetrics().density;
            params.height = (int) (200 * this.getResources().getDisplayMetrics().density);
            params.width = (int) (250 * this.getResources().getDisplayMetrics().density);
            videoContainer.setLayoutParams(params);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            IsFullScreen = false;
        }
        wrapContentAgain(RelativelayoutContainer);
    }




    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
    public void RuntimePermissionForUser() {

        Intent PermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(PermissionIntent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    private void initializeUI() {

        if(TestType.equals("Test1")) {

            spinner_video = findViewById(R.id.local_video_list);

            spinner_video.setOnItemSelectedListener(this);
            String[] textSizes = getResources().getStringArray(R.array.local_video);
            ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, textSizes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_video.setAdapter(adapter);
        }
        else if (TestType.equals("Test2")){
            spinner_video = (Spinner) findViewById(R.id.local_video_list);


            //for (int i = 0; i < 10; i++) {
            //    courseModalArrayList.add(new CourseModal("Name_" + i, "Id_" + i));
            //}

            ArrayAdapter<CourseModal> adapter =
                    new ArrayAdapter<CourseModal>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, courseModalArrayList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner_video.setAdapter(adapter);
        }
        else {
            spinner_video = (Spinner) findViewById(R.id.local_video_list);
            spinner_video.setOnItemSelectedListener(this);
            LayoutDbHelper db = new LayoutDbHelper(getApplicationContext());

            //List<String> labels = db.getAllData();
            List<String> labels = db.getAllLabels();
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spinner_video.setAdapter(dataAdapter);
            db.close();
        }
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

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwinodw, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(true);
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                popupWindow.dismiss();
                return true;
            }
        });

        courseNameEdt = (EditText) popupView.findViewById(R.id.idEdtCourseName);
        courseDescEdt = (EditText) popupView.findViewById(R.id.idEdtCourseDescription);
        //popupView.findViewById(R.id.idBtnSave).setOnClickListener(this);
       // popupView.findViewById(R.id.idBtnAdd).setOnClickListener(this);

        saveBtn = popupView.findViewById(R.id.idBtnSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // calling method to save data in shared prefs.
                saveData();

            }




        });

        addBtn = popupView.findViewById(R.id.idBtnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is use to add data to array list.
                courseModalArrayList.add(new CourseModal(courseNameEdt.getText().toString(), courseDescEdt.getText().toString()));
                // notifying adapter when new data added.
                adapter.notifyItemInserted(courseModalArrayList.size());

            }
        });


    }
    private void buildRecyclerView() {
        // initializing our adapter class.
        adapter = new CourseAdapter(courseModalArrayList, AddItems_TempTest.this);

        // adding layout manager to our recycler view.
        //LinearLayoutManager manager = new LinearLayoutManager(this);
       // courseRV.setHasFixedSize(true);

        // setting layout manager to our recycler view.
       // courseRV.setLayoutManager(manager);

        // setting adapter to our recycler view.
       // courseRV.setAdapter(adapter);
    }

    private void Pause(){

        videoView.pause();

    }
    private void Play(){

        videoView.start();

    }

    private void Back(){

        videoView.seekTo(videoView.getCurrentPosition());
        int currentPosition = videoView.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        seekForwardTime = 10000 ;
        // forward song
        videoView.seekTo(currentPosition - seekForwardTime);

    }

    private void Foward(){

        videoView.seekTo(videoView.getCurrentPosition());
        int currentPosition = videoView.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        seekForwardTime = 10000 ;
        // forward song
        videoView.seekTo(currentPosition + seekForwardTime);

    }
    private void RestartVideo(){

        videoView.seekTo(0);

    }


    private void PopupSave(){
        // below line is use to add data to array list.
        courseModalArrayList.add(new CourseModal(courseNameEdt.getText().toString(), courseDescEdt.getText().toString()));
        // notifying adapter when new data added.
        adapter.notifyItemInserted(courseModalArrayList.size());
        // calling method to save data in shared prefs.
        saveData();



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

/*
    final void loop() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    */

    }





/*

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                Uri video = Uri.parse("http://www.servername.com/projects/projectname/videos/1361439400.mp4");
                videoView.setVideoURI(video);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        videoView.start();


            }
        });

*/



        /*
        Play.setOnClickListener(v1 -> {

            //YouTubePlayerView youtube =  findViewById(R.id.dummy_youtube_view);
           // youtube.setVisibility(View.VISIBLE);
            bottomVideoStart(v1, Url_String);
        });
        */
/*
        ImageView play_2 = findViewById(R.id.start_play);
        play_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String Url_String = String.valueOf(Url.getText());
                //YouTubePlayerView youtube =  findViewById(R.id.dummy_youtube_view);
                // youtube.setVisibility(View.VISIBLE);
                bottomVideoStart(v, Url_String);

                return true;
            }


        });
*/
// view.setLayoutParams(params);


/*
        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {

                bottomVideoStart(v1, Url_String);
            }

        });
*/

/*
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        mPhotoLayoutPath = (ToDoItem) b.get(ItemPhotosActivity.LAYOUT_ITEM);
        ITEM_ID = (String) b.get(ItemPhotosActivity.ARG_ITEM_ID);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }

*/
