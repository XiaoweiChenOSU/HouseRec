package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AddToDo.AddToDoFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.OnDoubleClickListener;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;
import com.vuzix.connectivity.sdk.Connectivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private RecyclerView recyclerView;

    private Connectivity connection;

    private String postUrl;

    private String checkLayoutUrl;

    private static Context context;

    private ToDoItem mUserLayoutItem;

//    private CameraFragmentViewModel viewModel;

    private static final int LOWER_BYTE_MASK = 0xFF;

    private static final String TAG = "TrainActivity";
    public static final String PhotoLayout = "com.okstate.VisualComputingandImageProcessingLab.HouseRec.PhotoLayout";

    private int locationnum = 0;

    private final ViewHolder mViewHolder = new ViewHolder();

//    private boolean mUserHasLayout;
    private String mUserEnteredText;
    private String mUserEnteredDescription;
    private boolean mUserHasReminder;

    private Toolbar mToolbar;
    private Date mUserReminderDate;
    private int mUserColor;
    private long linesnum;

    private File locationfile;
    private File layoutfile;
    private File layoutpath;
    private File tempLayoutfile;
    private FileWriter writerlocation = null;

    private String layoutname;

    public static void setContext(Context cntxt) {
        context = cntxt;
    }

    public static Context getContext() {
        return context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // Set Context
        com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemListActivity.setContext(getApplicationContext());

        connection = Connectivity.get(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if ( (ToDoItem) b.get(AddToDoFragment.ITEMLayout) != null){
            mUserLayoutItem = (ToDoItem) b.get(AddToDoFragment.ITEMLayout);

        }else{
            mUserLayoutItem = (ToDoItem) b.get(ItemListActivity.PhotoLayout);
        }

//        mUserHasLayout = mUserLayoutItem.hasLayout();
        layoutname = mUserLayoutItem.getToDoText().replaceAll(" ","");



        layoutpath = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" );



        TextView thisLayout = (TextView) findViewById(R.id.thislayouname);
        thisLayout.setText(layoutname);


        layoutfile = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" + layoutname + ".jpg" );
        locationfile = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" + layoutname + ".txt" );
        tempLayoutfile = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/templayout.txt" );
        if (layoutfile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(layoutfile));
            BitmapDrawable bd= new BitmapDrawable(getResources(), bitmap);
            ImageView FullLayout = (ImageView)findViewById(R.id.layoutBackground);
            TextView texthint = (TextView) findViewById(R.id.textHint);
            texthint.setVisibility(View.GONE);
            FullLayout.setBackground(bd);
        }



        Button mLayoutPhoto =  findViewById(R.id.addPhotoLayout);
        mLayoutPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, ItemPhotosActivity.class);
            intent.putExtra(PhotoLayout, mUserLayoutItem);
            startActivity(intent);
        });


//        Button testButton = findViewById(R.id.test);
//        testButton.setOnClickListener(v -> {
////            Intent intent = new Intent(this, TestActivity.class);
////            startActivity(intent);
//        });


        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }



        Button layoutUpload = findViewById(R.id.layout);
        layoutUpload.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
                } else {
                    if (locationfile.exists()){
                        locationfile.delete();
                    }
                    getPhoto();
                }
            }
        });


        Button locationRemove = findViewById(R.id.eraseLocation);
        locationRemove.setOnClickListener(v -> {
            if (locationfile.exists()){
                locationfile.delete();
            }
        });


        FrameLayout layoutframe = findViewById(R.id.frameLayout);
        layoutframe.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onDoubleClick() {
                if(!locationfile.exists()){
                    try {
                        locationfile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                final ImageView image = new ImageView(ItemListActivity.this );
//                image.setBackgroundResource(R.drawable.layout_location);
                final Button buttonl = new Button(ItemListActivity.this );
                buttonl.setBackgroundResource(R.drawable.ic_baseline_location_on_24);
                layoutframe.addView(buttonl);

                FrameLayout.LayoutParams layoutParams = ( FrameLayout.LayoutParams ) buttonl.getLayoutParams();

                layoutParams.leftMargin = 300;
                layoutParams.height = 170;
                layoutParams.width = 170;
                buttonl.setLayoutParams(layoutParams);
                buttonl.setId(locationnum);
                locationnum = locationnum + 1;


//                toogleControllPanel(true );

                buttonl.setOnTouchListener(new View.OnTouchListener(){

                    @SuppressLint("ResourceType")
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent )
                    {

                        float x, y;

                        switch ( motionEvent.getAction() )
                        {
                            case MotionEvent.ACTION_DOWN:
//                                mImageSelected = image;
                                Log.e(TAG, "HAHAHA TEST DOWN.");
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int coords[] = { 0,0 };
                                layoutframe.getLocationOnScreen( coords );
                                x = ( motionEvent.getRawX() - (buttonl.getWidth() / 2) );
                                y = motionEvent.getRawY() - ((coords[1] + 100 ) + (buttonl.getHeight() / 2) );
                                buttonl.setX( x );
                                buttonl.setY( y );
                                Log.e(TAG, "HAHAHA TEST MOVE.");
                                break;
                            case MotionEvent.ACTION_UP:
                                Float positionX = buttonl.getX();
                                Float positionY = buttonl.getY();
                                Log.e(TAG, "HAHAHA TEST UP.");
                                try {
                                    int selectline = buttonl.getId() + 1;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        linesnum = Files.lines(Paths.get(locationfile.getPath())).count();
                                    }
                                    if (linesnum >=  selectline){
                                        BufferedReader in=new BufferedReader(new FileReader(locationfile));
                                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tempLayoutfile)));
                                        int count = 1;
                                        String line;
                                        while((line = in.readLine() )!=null){
                                            if(count == selectline){
                                                out.println(line.replace(line, buttonl.getId() + "-"  +positionX.toString() + "-" + positionY.toString()));
                                            }else{
                                                out.println(line);
                                            }
                                            count ++;
                                        }
                                        in.close();
                                        out.close();
                                        locationfile.delete();
                                        tempLayoutfile.renameTo(locationfile);
                                    }else{
                                        writerlocation = new FileWriter(locationfile,true);
                                        writerlocation.append(buttonl.getId() + "-"  +positionX.toString() + "-" + positionY.toString() + "\n");
                                        writerlocation.flush();
                                        writerlocation.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                break;
                        }
                        return true;
                    }
                });
            }

            @Override
            public void onClick(View v) {
//                HomeContext item = (HomeContext) AppContent.getInstance().ITEMS.get(0);
//                Context context = v.getContext();
//                Intent intent = new Intent(context, ItemDetailActivity.class);
//                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);
//                context.startActivity(intent);
            }
        }));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                BitmapDrawable bd= new BitmapDrawable(getResources(), bitmap);
                ImageView FullLayout = (ImageView)findViewById(R.id.layoutBackground);
                FullLayout.setBackground(bd);

                TextView texthint = (TextView) findViewById(R.id.textHint);
                texthint.setVisibility(View.GONE);

                File layoutdir = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" );

                if (!layoutdir.exists() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    layoutdir.mkdirs();
                }

                File layoutfile = new File(layoutdir + "/" + layoutname + ".jpg");
                FileOutputStream out = new FileOutputStream(layoutfile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }



}
