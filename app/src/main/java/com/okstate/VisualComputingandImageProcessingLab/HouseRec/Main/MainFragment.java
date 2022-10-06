package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AddToDo.AddToDoActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AddToDo.AddToDoFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AppDefault.AppDefaultFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.ClassifierActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutContract;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemListActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.ModelActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Reminder.ReminderFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ItemTouchHelperClass;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.RecyclerViewEmptySupport;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.StoreRetrieveData;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.TodoNotificationService;
import com.wei.library.DialogBuildUtil;
import com.wei.library.Interface.FDialogInterface;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.ClassifierActivity;





import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainFragment extends AppDefaultFragment {
    private RecyclerViewEmptySupport mRecyclerView;
    private FloatingActionButton mAddToDoItemFAB;
    private ArrayList<ToDoItem> mToDoItemsArrayList;
    private CoordinatorLayout mCoordLayout;
    public static final String TODOITEM = "com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main.MainActivity";
    private BasicListAdapter adapter;
    private static final int REQUEST_ID_TODO_ITEM = 100;
    private ToDoItem mJustDeletedToDoItem;
    private int mIndexOfDeletedToDoItem;
    public static final String DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a";
    public static final String DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm";
    public static final String FILENAME = "todoitems.json";
    private StoreRetrieveData storeRetrieveData;
    public ItemTouchHelper itemTouchHelper;
    private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;
    public static final String SHARED_PREF_DATA_SET_CHANGED = "com.avjindersekhon.datasetchanged";
    public static final String CHANGE_OCCURED = "com.avjinder.changeoccured";
    private int mTheme = -1;
    private String theme = "name_of_the_theme";
    public static final String THEME_PREFERENCES = "com.avjindersekhon.themepref";
    public static final String RECREATE_ACTIVITY = "com.avjindersekhon.recreateactivity";
    public static final String THEME_SAVED = "com.avjindersekhon.savedtheme";
    public static final String DARKTHEME = "com.avjindersekon.darktheme";
    public static final String LIGHTTHEME = "com.avjindersekon.lighttheme";
    public static final String LAYOUT_NAME = "LAYOUT_NAME";

    private String allLayouts;
    private LayoutDbHelper HouseRec;
    private ToDoItem mUserToDoItem;
    private Button LayoutChoose;
    private TextView ReminderTextView1;
    private TextView ReminderTextView2;
    private String layoutname;

    private boolean isNet = false;
//    private AnalyticsApplication app;
    private String[] testStrings = {"Clean my room",
            "Water the plants",
            "Get car washed",
            "Get my dry cleaning"
    };




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        app = (AnalyticsApplication) getActivity().getApplication();
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Aller_Regular.tff").setFontAttrId(R.attr.fontPath).build());

        //We recover the theme we've set and setTheme accordingly
        theme = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME);

        if (theme.equals(LIGHTTHEME)) {
            mTheme = R.style.CustomStyle_LightTheme;
        } else {
            mTheme = R.style.CustomStyle_DarkTheme;
        }
        this.getActivity().setTheme(mTheme);


        super.onCreate(savedInstanceState);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHANGE_OCCURED, false);
        editor.apply();

        storeRetrieveData = new StoreRetrieveData(getContext(), FILENAME);
        mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
        adapter = new BasicListAdapter(mToDoItemsArrayList);
        setAlarms();

        LayoutChoose = (Button) view.findViewById(R.id.LayoutName);
        ReminderTextView1 = (TextView) view.findViewById(R.id.text1);
        ReminderTextView2 = (TextView) view.findViewById(R.id.text2);
        mCoordLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        mAddToDoItemFAB = (FloatingActionButton) view.findViewById(R.id.addToDoItemFAB);
        LayoutDbHelper db = new LayoutDbHelper(getContext());
        if(!db.getLayout()){
            mAddToDoItemFAB.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onClick(View v) {

                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                            return;
                        }

                        ReminderTextView1.setVisibility(View.VISIBLE);
                        ReminderTextView2.setVisibility(View.VISIBLE);
                        String postUrl = "getLayouts";
                        new Thread(new Runnable(){
                            @Override
                            public void run(){
                                try{
                                    getRequest(postUrl);
                                    Thread.sleep(500);
                                    final String[] listLayouts = allLayouts.split(",");
                                    DialogBuildUtil.showListDialog(getFragmentManager(), "House List", listLayouts, new FDialogInterface.OnDataCallbackListener() {
                                        @Override
                                        public boolean onDataCallback(int which) {
                                            layoutname = listLayouts[which];
                                            mUserToDoItem = new ToDoItem(listLayouts[which],"Test", false, null,false);
                                            ModelActivity modelget = new ModelActivity();
                                            HouseRec = new LayoutDbHelper(getContext());
                                            SQLiteDatabase House = HouseRec.getWritableDatabase();
                                            ContentValues values = new ContentValues();
                                            LayoutDbHelper db = new LayoutDbHelper(getContext());
                                            values.put(LayoutContract.LayoutTable.COLUMN_NAME, mUserToDoItem.getToDoText());
                                            values.put(LayoutContract.LayoutTable.COLUMN_DESCRIPTION, mUserToDoItem.getmToDoDescription());
                                            values.put(LayoutContract.LayoutTable.COLUMN_REMINDTIME, String.valueOf(Calendar.getInstance().getTime()));
                                            // Insert the new row, returning the primary key value of the new row
                                            try {
                                                long newRowId = House.insert(LayoutContract.LayoutTable.TABLE_NAME, null, values);
                                            }finally {
                                                House.close();
                                            }

                                            modelget.updateModel(listLayouts[which]);
                                            ReminderTextView1.setVisibility(View.GONE);
                                            ReminderTextView2.setVisibility(View.GONE);
                                            LayoutChoose.setText(listLayouts[which]);
                                            LayoutChoose.setVisibility(View.VISIBLE);
                                            mAddToDoItemFAB.setVisibility(View.GONE);
                                            return false;
                                        }
                                    });
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();

    ////                app.send(this, "Action", "FAB pressed");
    //                Intent newTodo = new Intent(getContext(), AddToDoActivity.class);
    //                ToDoItem item = new ToDoItem("","", false, null,false);
    //                int color = ColorGenerator.MATERIAL.getRandomColor();
    //                item.setTodoColor(color);
    //                //noinspection ResourceType
    ////                String color = getResources().getString(R.color.primary_ligher);
    //                newTodo.putExtra(TODOITEM, item);
    //                startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM);
                }
            });
        }else{
                    String layoutN = db.getLayoutName();
                    layoutname = layoutN;
                    mUserToDoItem = new ToDoItem(layoutN,"Test", false, null,false);
                    try {
                        ReminderTextView1.setVisibility(View.GONE);
                        ReminderTextView2.setVisibility(View.GONE);
                        LayoutChoose.setText(layoutN);
                        LayoutChoose.setVisibility(View.VISIBLE);
                        mAddToDoItemFAB.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
        }

        LayoutChoose.setOnClickListener(v -> {


            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }

            Context context = v.getContext();
            Intent newTodo = new Intent(getContext(), ClassifierActivity.class);
            newTodo.putExtra(LAYOUT_NAME, layoutname);
//            startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM);
            context.startActivity(newTodo);
        });



//        mRecyclerView = (RecyclerView) view.findViewById(R.id.toDoRecyclerView);
        mRecyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.toDoRecyclerView);
        if (theme.equals(LIGHTTHEME)) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        }
        mRecyclerView.setEmptyView(view.findViewById(R.id.toDoEmptyView));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
            @Override
            public void show() {
                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }

            @Override
            public void hide() {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mAddToDoItemFAB.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                mAddToDoItemFAB.animate().translationY(mAddToDoItemFAB.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }
        };

        mRecyclerView.addOnScrollListener(customRecyclerScrollViewListener);


        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        mRecyclerView.setAdapter(adapter);
//        setUpTransitions();



    }

    public static ArrayList<ToDoItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData) {
        ArrayList<ToDoItem> items = null;

        try {
            items = storeRetrieveData.loadFromFile();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if (items == null) {
            items = new ArrayList<>();
        }
        return items;

    }

    @Override
    public void onResume() {
        super.onResume();
//        app.send(this);

        onCreate(null);
    }

    @Override
    public void onStart() {
//        app = (AnalyticsApplication) getActivity().getApplication();
        super.onStart();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(CHANGE_OCCURED, false)) {

            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
            adapter = new BasicListAdapter(mToDoItemsArrayList);
            mRecyclerView.setAdapter(adapter);
            setAlarms();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CHANGE_OCCURED, false);
//            editor.commit();
            editor.apply();


        }
    }



    private void setAlarms() {
        if (mToDoItemsArrayList != null) {
            for (ToDoItem item : mToDoItemsArrayList) {
                if (item.hasReminder() && item.getToDoDate() != null) {
                    if (item.getToDoDate().before(new Date())) {
                        item.setToDoDate(null);
                        continue;
                    }
                    Intent i = new Intent(getContext(), TodoNotificationService.class);
                    i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                    i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                    createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
                }
            }
        }
    }


    public void addThemeToSharedPreferences(String theme) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME_SAVED, theme);
        editor.apply();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
//        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM) {
            ToDoItem item = (ToDoItem) data.getSerializableExtra(TODOITEM);
            if (item.getToDoText().length() <= 0) {
                return;
            }
            boolean existed = false;

            if (item.hasReminder() && item.getToDoDate() != null) {
                Intent i = new Intent(getContext(), TodoNotificationService.class);
                i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
//                Log.d("OskarSchindler", "Alarm Created: "+item.getToDoText()+" at "+item.getToDoDate());
            }

            for (int i = 0; i < mToDoItemsArrayList.size(); i++) {
                if (item.getIdentifier().equals(mToDoItemsArrayList.get(i).getIdentifier())) {
                    mToDoItemsArrayList.set(i, item);
                    existed = true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
            if (!existed) {
                addToDataStore(item);
            }
        }
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
    }

    private boolean doesPendingIntentExist(Intent i, int requestCode) {
        PendingIntent pi = PendingIntent.getService(getContext(), requestCode, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private void createAlarm(Intent i, int requestCode, long timeInMillis) {
        AlarmManager am = getAlarmManager();
        PendingIntent pi = PendingIntent.getService(getContext(), requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
//        Log.d("OskarSchindler", "createAlarm "+requestCode+" time: "+timeInMillis+" PI "+pi.toString());
    }

    private void deleteAlarm(Intent i, int requestCode) {
        if (doesPendingIntentExist(i, requestCode)) {
            PendingIntent pi = PendingIntent.getService(getContext(), requestCode, i, PendingIntent.FLAG_NO_CREATE);
            pi.cancel();
            getAlarmManager().cancel(pi);
            Log.d("OskarSchindler", "PI Cancelled " + doesPendingIntentExist(i, requestCode));
        }
    }

    private void addToDataStore(ToDoItem item) {
        mToDoItemsArrayList.add(item);
        adapter.notifyItemInserted(mToDoItemsArrayList.size() - 1);

    }


//    public void makeUpItems(ArrayList<ToDoItem> items, int len) {
//        for (String testString : testStrings) {
//            ToDoItem item = new ToDoItem(testString,testString, false, new Date(),false);
//            //noinspection ResourceType
////            item.setTodoColor(getResources().getString(R.color.red_secondary));
//            items.add(item);
//        }
//
//    }

    public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter {
        private ArrayList<ToDoItem> items;

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(items, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(items, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRemoved(final int position) {
            //Remove this line if not using Google Analytics
//            app.send(this, "Action", "Swiped Todo Away");

            mJustDeletedToDoItem = items.remove(position);
            mIndexOfDeletedToDoItem = position;
            Intent i = new Intent(getContext(), TodoNotificationService.class);
            deleteAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode());
            notifyItemRemoved(position);

//            String toShow = (mJustDeletedToDoItem.getToDoText().length()>20)?mJustDeletedToDoItem.getToDoText().substring(0, 20)+"...":mJustDeletedToDoItem.getToDoText();
            String toShow = "Todo";
            Snackbar.make(mCoordLayout, "Deleted " + toShow, Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Comment the line below if not using Google Analytics
//                            app.send(this, "Action", "UNDO Pressed");
                            items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem);
                            if (mJustDeletedToDoItem.getToDoDate() != null && mJustDeletedToDoItem.hasReminder()) {
                                Intent i = new Intent(getContext(), TodoNotificationService.class);
                                i.putExtra(TodoNotificationService.TODOTEXT, mJustDeletedToDoItem.getToDoText());
                                i.putExtra(TodoNotificationService.TODOUUID, mJustDeletedToDoItem.getIdentifier());
                                createAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode(), mJustDeletedToDoItem.getToDoDate().getTime());
                            }
                            notifyItemInserted(mIndexOfDeletedToDoItem);
                        }
                    }).show();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            ToDoItem item = items.get(position);
//            if(item.getToDoDate()!=null && item.getToDoDate().before(new Date())){
//                item.setToDoDate(null);
//            }

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
            //Background color for each to-do item. Necessary for night/day mode
            int bgColor;
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            int todoTextColor;
            if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTTHEME)) {
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.primary);
            } else {
                bgColor = Color.DKGRAY;
                todoTextColor = Color.WHITE;
            }
            holder.linearLayout.setBackgroundColor(bgColor);

            if (item.hasReminder() && item.getToDoDate() != null) {
                holder.mToDoTextview.setMaxLines(1);
                holder.mTimeTextView.setVisibility(View.VISIBLE);
//                holder.mToDoTextview.setVisibility(View.GONE);
            } else {
                holder.mTimeTextView.setVisibility(View.GONE);
                holder.mToDoTextview.setMaxLines(2);
            }
            holder.mToDoTextview.setText(item.getToDoText());
            holder.mToDoTextview.setTextColor(todoTextColor);
//            holder.mColorTextView.setBackgroundColor(Color.parseColor(item.getTodoColor()));

//            TextDrawable myDrawable = TextDrawable.builder().buildRoundRect(item.getToDoText().substring(0,1),Color.RED, 10);
            //We check if holder.color is set or not
//            if(item.getTodoColor() == null){
//                ColorGenerator generator = ColorGenerator.MATERIAL;
//                int color = generator.getRandomColor();
//                item.setTodoColor(color+"");
//            }
//            Log.d("OskarSchindler", "Color: "+item.getTodoColor());
            TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.getToDoText().substring(0, 1), item.getTodoColor());

//            TextDrawable myDrawable = TextDrawable.builder().buildRound(item.getToDoText().substring(0,1),holder.color);
            holder.mColorImageView.setImageDrawable(myDrawable);
            if (item.getToDoDate() != null) {
                String timeToShow;
                if (android.text.format.DateFormat.is24HourFormat(getContext())) {
                    timeToShow = AddToDoFragment.formatDate(MainFragment.DATE_TIME_FORMAT_24_HOUR, item.getToDoDate());
                } else {
                    timeToShow = AddToDoFragment.formatDate(MainFragment.DATE_TIME_FORMAT_12_HOUR, item.getToDoDate());
                }
                holder.mTimeTextView.setText(timeToShow);
            }


        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        BasicListAdapter(ArrayList<ToDoItem> items) {

            this.items = items;
        }


        @SuppressWarnings("deprecation")
        public class ViewHolder extends RecyclerView.ViewHolder {

            View mView;
            LinearLayout linearLayout;
            TextView mToDoTextview;
            //            TextView mColorTextView;
            ImageView mColorImageView;
            TextView mTimeTextView;
//            int color = -1;

            public ViewHolder(View v) {
                super(v);
                mView = v;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToDoItem item = items.get(ViewHolder.this.getAdapterPosition());
                        Intent i = new Intent(getContext(), AddToDoActivity.class);
                        i.putExtra(TODOITEM, item);
                        startActivityForResult(i, REQUEST_ID_TODO_ITEM);
                    }
                });
                mToDoTextview = (TextView) v.findViewById(R.id.toDoListItemTextview);
                mTimeTextView = (TextView) v.findViewById(R.id.todoListItemTimeTextView);
//                mColorTextView = (TextView)v.findViewById(R.id.toDoColorTextView);
                mColorImageView = (ImageView) v.findViewById(R.id.toDoListItemColorImageView);
                linearLayout = (LinearLayout) v.findViewById(R.id.listItemLinearLayout);
            }


        }
    }



    private void saveDate() {
        try {
            storeRetrieveData.saveToFile(mToDoItemsArrayList);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            storeRetrieveData.saveToFile(mToDoItemsArrayList);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        mRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener);
    }



    public void getRequest(String postUrl) {

        String murl = "http://10.203.8.185:5001/";

        String finalUrl =  murl + postUrl;
        final String[] result = {null};

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());
                isNet = false;
                Looper.prepare();
                Toast.makeText(getContext(), "Failed to Connect to Server. Please Try Again.", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                try{
                    Log.d("Success","Server Operate Successfully");
                    isNet = true;
                    allLayouts = response.body().string();
                } finally {
                    if (response != null){
                        response.close();
                    }
                }
            }
        });
    }



    @Override
    protected int layoutRes() {
        return R.layout.fragment_main;
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }
}
