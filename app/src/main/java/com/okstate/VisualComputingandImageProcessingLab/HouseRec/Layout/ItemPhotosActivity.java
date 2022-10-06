package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AddToDo.AddToDoFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.ClassifierActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main.MainActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.ModelActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.content.AppContent;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.content.HomeContext;
import com.vuzix.connectivity.sdk.Connectivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemPhotosActivity<progressBar> extends AppCompatActivity {

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

    public static final String LAYOUT_ITEM = "LAYOUT_ITEM";

    public static final String LAYOUT_NAME = "LAYOUT_NAME";

    public static final String LAYOUT_DETAILS = "LAYOUT_DETAILS";

    public static final String ARG_ITEM_ID = "ITEM_ID";

    private int locationnum = 0;

    private final ViewHolder mViewHolder = new ViewHolder();

//    private boolean mUserHasLayout;

    private long linesnum;

    private File locationfile;
    private File layoutfile;
    private File tempLayoutfile;
    private FileWriter writerlocation = null;

    private Button Home;

    private String layoutname;


    private File layoutpath;
    private Button train;
    private ProgressBar progressBar;

    public static void setContext(Context cntxt) {
        context = cntxt;
    }

    public static Context getContext() {
        return context;
    }

    public boolean isFinish = false;

    private static final int MSG_PROGRESS_BAR_FULL = 1;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        // Set Context
        ItemPhotosActivity.setContext(getApplicationContext());




        connection = Connectivity.get(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar=(ProgressBar)findViewById(R.id.progressBarLarge);


        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if ( (ToDoItem) b.get(ItemListActivity.PhotoLayout) != null){
            mUserLayoutItem = (ToDoItem) b.get(ItemListActivity.PhotoLayout);
        }else if((ToDoItem) b.get(AddToDoFragment.ITEMLayout)!= null){
            mUserLayoutItem = (ToDoItem) b.get(AddToDoFragment.ITEMLayout);
        }else{
            mUserLayoutItem = (ToDoItem) b.get(ItemDetailActivity.LAYOUT_ITEM);
        }


//        mUserHasLayout = mUserLayoutItem.hasLayout();
        layoutname = mUserLayoutItem.getToDoText().replaceAll(" ","");


        layoutpath = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" );

        AppContent.findOrSave(layoutpath);


        layoutfile = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" + layoutname + ".jpg" );
        locationfile = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" + layoutname + ".txt" );
        if (layoutfile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(layoutfile));
            BitmapDrawable bd= new BitmapDrawable(getResources(), bitmap);
            FrameLayout FullLayout = (FrameLayout) findViewById(R.id.photoLayoutList);
            FullLayout.setBackground(bd);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    BufferedReader in=new BufferedReader(new FileReader(locationfile));
                    String line;
                    FrameLayout layoutframe = (FrameLayout) findViewById(R.id.photoLayoutList);
                    while((line = in.readLine() )!=null){
                        String locationArray[] = line.split("-");
                        String button_id = locationArray[0];
                        Button locationButton = new Button(ItemPhotosActivity.this);
                        locationButton.setBackgroundResource(R.drawable.location_button_pressed);
                        layoutframe.addView(locationButton);
                        FrameLayout.LayoutParams layoutParams = ( FrameLayout.LayoutParams ) locationButton.getLayoutParams();
                        locationButton.setId(Integer.parseInt(button_id));
                        layoutParams.height = 200;
                        layoutParams.width = 200;
                        locationButton.setX(Float.parseFloat(locationArray[1]));
                        locationButton.setY(Float.parseFloat(locationArray[2]));
                        locationButton.setId(Integer.parseInt(button_id));
                        if(AppContent.getInstance().LAYOUT_MAP.get(layoutname) == null || AppContent.getInstance().LAYOUT_MAP.get(layoutname).size() < Integer.parseInt(button_id)+1){
                            locationButton.setText("Position" + button_id);
                            locationButton.setTextColor(Color.parseColor("#000000"));
                        }else{
                            HomeContext item = AppContent.getInstance().LAYOUT_MAP.get(layoutname).get(Integer.parseInt(button_id));
                            locationButton.setText(item.name);
                            locationButton.setTextColor(Color.parseColor("#000000"));
                        }

                        locationButton.setOnClickListener(v -> {
                            if(AppContent.getInstance().LAYOUT_MAP.get(layoutname) == null || AppContent.getInstance().LAYOUT_MAP.get(layoutname).size() < Integer.parseInt(button_id)+1){
                                AppContent.getInstance().newItem(Integer.parseInt(button_id),layoutname);
                                AppContent.getInstance().addLayout();
                                AppContent.findOrSave(layoutpath);
                            }
//                            if(AppContent.getInstance().LAYOUT_MAP.get(layoutname).size() < Integer.parseInt(button_id)+1){
//                                AppContent.getInstance().newItem(Integer.parseInt(button_id),layoutname);
////                                recyclerView.getAdapter().notifyDataSetChanged();
////                                AppContent.findOrSave(layoutpath);
//                            }
//                            HomeContext item = AppContent.getInstance().LAYOUT_MAP.get(layoutname).get(Integer.parseInt(button_id));
                            Context context = v.getContext();
                            Intent intent = new Intent(context, ItemDetailActivity.class);
                            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, button_id);
                            intent.putExtra(ARG_ITEM_ID, button_id);
                            intent.putExtra(LAYOUT_ITEM, mUserLayoutItem);
                            context.startActivity(intent);
                        });

                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }



        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }




        Home =  (Button) findViewById(R.id.homeBack);
        Home.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
        });

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

    public void manageVideo(View view){
        Intent intent = new Intent(this, ManageVideoActivity.class);
        intent.putExtra(LAYOUT_NAME, layoutname);
        startActivity(intent);
    }

    public void startTest(View view){
        Intent intent = new Intent(this, ClassifierActivity.class);
        intent.putExtra(LAYOUT_NAME, layoutname);
        startActivity(intent);
    }

    public void TandUModel(View view) throws InterruptedException {
        progressBar.setVisibility(View.VISIBLE);
        ModelActivity ma = new ModelActivity();
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    ma.trainModel(layoutname);
                    while(!ma.isFinish){}
                    ma.updateModel(layoutname);
                    handler.sendEmptyMessage(MSG_PROGRESS_BAR_FULL);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    // 处理当ProcessBar的进度完成的情况
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PROGRESS_BAR_FULL: {
                    Log.d(TAG, "make the ProgressBar Gone!");
                    // 隐藏ProcessBar。
                    progressBar.setVisibility(View.GONE);
                    // 终止线程
                    Toast toast = Toast.makeText(getContext(), "Train Model Successfully!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                default:
                    break;
            }
        }
    };


//    private static class ViewHolder {
//        ImageView mButtonEdit;
//        ImageView mButtonFinish;
//        ImageView mButtonRemove;
//        LinearLayout mLinearControllPanel;
//    }




}
