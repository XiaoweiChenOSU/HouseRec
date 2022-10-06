package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;



import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubePlayerView;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main.MainActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;
import com.pierfrancescosoffritti.androidyoutubeplayer.utils.YouTubePlayerTracker;
import com.pipvideo.youtubepipvideoplayer.FlyingVideo;
import com.pipvideo.youtubepipvideoplayer.TaskCoffeeVideo;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import java.net.URI;
import java.util.ArrayList;
import java.util.zip.Inflater;




import io.hamed.floatinglayout.FloatingLayout;
import io.hamed.floatinglayout.callback.FloatingListener;


public class AddItems_TempTest2 extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    public static final int SYSTEM_ALERT_WINDOW_PERMISSION = 7;
    public Uri Video_Location;
    private Spinner spinner_video;
    private ToDoItem mPhotoLayoutPath;
    private String ITEM_ID;
    public static final String LAYOUT_ITEM = "LAYOUT_ITEM";
    public static final String ARG_ITEM_ID = "ITEM_ID";
    public EditText Url;
    LinearLayout videoContainer;
    //ArrayList<DummyContents> dummyContents = new ArrayList<>();
    ScrollView scrollView;
    YouTubePlayerTracker mTracker = null;
    // private EditText Url = findViewById(R.id.url_prompt);

    private FloatingLayout floatingLayout;
    private VideoView videoView;
    public int video_to_play;
    private FloatingListener floatingListener = new FloatingListener() {
        @Override
        public void onCreateListener(View view) {
            initVideoView(view);
            view.findViewById(R.id.btn_close).setOnClickListener(AddItems_TempTest2.this);
            view.findViewById(R.id.test_button).setOnClickListener(AddItems_TempTest2.this);


        }

        @Override
        public void onCloseListener() {
            //videoView.stopPlayback();
        }
    };
    public void bottomVideoStart(View view, String videoId) {

        FlyingVideo.get(AddItems_TempTest2.this)
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
        int width = 100;
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

        findViewById(R.id.play_local).setOnClickListener(this);
        spinner_video = findViewById(R.id.local_video_list);

        spinner_video.setOnItemSelectedListener(this);
        String[] textSizes = getResources().getStringArray(R.array.local_video);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, textSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_video.setAdapter(adapter);





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



    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView String_test = findViewById(R.id.String_Test);
        if (parent.getId() == R.id.local_video_list) {
            String valueFromSpinner = parent.getItemAtPosition(position).toString();
            if(valueFromSpinner.equals("sample")){


                video_to_play = R.raw.matrix;
                Video_Location = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
                //String_test.setText(valueFromSpinner);

            }
            else if (valueFromSpinner.equals("input")){
                //String_test.setText(valueFromSpinner);
                Intent popup = new Intent(this, PopUpClass.class);
                startActivity(popup);
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
                Video_Location = Uri.parse("/storage/emulated/0/DCIM/Camera/PXL_20210603_180921056.mp4");
            }
            else{
                //Work in progress will be used to play other videos if they existed.
                video_to_play = getResources().getIdentifier(valueFromSpinner,
                        "raw", getPackageName());
                Video_Location = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);



            }

        }

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

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
        //Changed uri to Video_Location for testing
        videoView.setVideoURI(Video_Location);
        videoView.start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_local:
                if (!isNeedPermission()) {

                    showFloating();

                }
                else {
                    requestPermission();
                }

                break;
            case R.id.btn_close:
                floatingLayout.destroy();
                break;

            case R.id.fullscreen_2:
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
                hideSystemUI();
                videoView = findViewById(R.id.video_player);
                DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
                android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) videoView.getLayoutParams();
                params.width =  metrics.widthPixels;
                params.height = metrics.heightPixels;
                params.leftMargin = 0;
                videoView.setLayoutParams(params);

                /*






                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) videoView.getLayoutParams();
                params.width = metrics.widthPixels;
                params.height = metrics.heightPixels;
                params.leftMargin = 0;
                videoView.setLayoutParams(params);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

                    startService(new Intent(AddItems_TempTest.this, FloatingWidgetShowService.class));

                    finish();

                } else if (Settings.canDrawOverlays(AddItems_TempTest.this)) {

                    startService(new Intent(AddItems_TempTest.this, FloatingWidgetShowService.class));

                    finish();

                } else {
                    RuntimePermissionForUser();

                    Toast.makeText(AddItems_TempTest.this, "System Alert Window Permission Is Required For Floating Widget.", Toast.LENGTH_LONG).show();
                }
                   */



                break;
        }
    }


    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
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
        floatingLayout = new FloatingLayout(getApplicationContext(), R.layout.video_layout2);
        floatingLayout.setFloatingListener(floatingListener);
        floatingLayout.create();
    }

    public void RuntimePermissionForUser() {

        Intent PermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(PermissionIntent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }


}




/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent = new Intent(this, ItemPhotosActivity.class);
            intent.putExtra(LAYOUT_ITEM, mPhotoLayoutPath);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/


