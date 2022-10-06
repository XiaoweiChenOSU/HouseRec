package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AddToDo.AddToDoActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AppDefault.AppDefaultFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.Material;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main.CustomRecyclerScrollViewListener;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Reminder.ReminderFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ItemTouchHelperClass;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.RecyclerViewEmptySupport;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.TodoNotificationService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ManageVideoFrame extends AppDefaultFragment {
    private RecyclerViewEmptySupport mRecyclerView;
    private ArrayList<Material> mVideoArrayList;
    private CoordinatorLayout mCoordLayout;
    public static final String LAYOUTNAME = "com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main.MainActivity";
    private BasicListAdapter adapter;
    private static final int REQUEST_ID_TODO_ITEM = 100;
    private Material mJustDeletedToDoItem;
    private int mIndexOfDeletedToDoItem;
    public static final String FILENAME = "todoitems.json";
    public ItemTouchHelper itemTouchHelper;
    private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;
    public static final String SHARED_PREF_DATA_SET_CHANGED = "com.avjindersekhon.datasetchanged";
    public static final String CHANGE_OCCURED = "com.avjinder.changeoccured";
    private int mTheme = -1;
    private String theme = "name_of_the_theme";
    public static final String THEME_PREFERENCES = "com.avjindersekhon.themepref";
    public static final String RECREATE_ACTIVITY = "com.avjindersekhon.recreateactivity";
    public static final String THEME_SAVED = "com.avjindersekhon.savedtheme";
    public static final String LIGHTTHEME = "com.avjindersekon.lighttheme";
    private Material videoInfo;
    private LayoutDbHelper HouseRec;
    private String layoutName;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        if (getArguments().containsKey(LAYOUTNAME)) {
            // Load the content content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            layoutName = getArguments().getString(LAYOUTNAME);

        }

//        videoInfo = new Material();
        mVideoArrayList = getLocallyStoredData(layoutName);
        adapter = new BasicListAdapter(mVideoArrayList);


        mCoordLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);

        mRecyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.videoRecyclerView);
        if (theme.equals(LIGHTTHEME)) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        }
        mRecyclerView.setEmptyView(view.findViewById(R.id.videoEmptyView));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
            @Override
            public void show() {
            }

            @Override
            public void hide() {
            }
        };

        mRecyclerView.addOnScrollListener(customRecyclerScrollViewListener);


        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        mRecyclerView.setAdapter(adapter);

    }

    public ArrayList<Material> getLocallyStoredData(String layoutName) {
        ArrayList<Material> items = null;

        try {
            HouseRec = new LayoutDbHelper(getContext());

            items = HouseRec.loadFromDatabase(layoutName);

        } catch (JSONException e) {
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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(ReminderFragment.EXIT, false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ReminderFragment.EXIT, false);
            editor.apply();
            getActivity().finish();
        }
        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we woudl have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR

         */
        if (getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).edit();
            editor.putBoolean(RECREATE_ACTIVITY, false);
            editor.apply();
            getActivity().recreate();
        }


    }

    @Override
    public void onStart() {
//        app = (AnalyticsApplication) getActivity().getApplication();
        super.onStart();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(CHANGE_OCCURED, false)) {

            mVideoArrayList = getLocallyStoredData(layoutName);
            adapter = new BasicListAdapter(mVideoArrayList);
            mRecyclerView.setAdapter(adapter);


            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CHANGE_OCCURED, false);
//            editor.commit();
            editor.apply();


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
            Material item = (Material) data.getSerializableExtra(LAYOUTNAME);
            if (item.getKeywords().length() <= 0) {
                return;
            }
            boolean existed = false;


            for (int i = 0; i < mVideoArrayList.size(); i++) {
                mVideoArrayList.set(i, item);
                existed = true;
                adapter.notifyDataSetChanged();
                break;
            }

        }
    }



    public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter {
        private ArrayList<Material> items;

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

            HouseRec = new LayoutDbHelper(getContext());
            HouseRec.Delete_Video(items.get(position).getKeywords().toString());
            mJustDeletedToDoItem = items.remove(position);
            mIndexOfDeletedToDoItem = position;
            Intent i = new Intent(getContext(), TodoNotificationService.class);
//            deleteAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode());
            notifyItemRemoved(position);
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText((getContext()), (CharSequence)"Delete Video Info Successfully", Toast.LENGTH_SHORT).show();
            Looper.loop();

//            String toShow = (mJustDeletedToDoItem.getToDoText().length()>20)?mJustDeletedToDoItem.getToDoText().substring(0, 20)+"...":mJustDeletedToDoItem.getToDoText();
//            String toShow = "Todo";
//            Snackbar.make(mCoordLayout, "Deleted " + toShow, Snackbar.LENGTH_LONG)
//                    .setAction("UNDO", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            //Comment the line below if not using Google Analytics
////                            app.send(this, "Action", "UNDO Pressed");
//                            items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem);
//                            notifyItemInserted(mIndexOfDeletedToDoItem);
//                        }
//                    }).show();


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Material item = items.get(position);
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

            int color = ColorGenerator.MATERIAL.getRandomColor();
            item.setTodoColor(color);
            holder.mToDoTextview.setText(item.getKeywords());
            holder.mToDoTextview.setTextColor(todoTextColor);
            holder.mTimeTextView.setText(item.getLocation());
            holder.mTimeTextView.setTextColor(todoTextColor);

            TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.getKeywords().substring(0, 1), item.getTodoColor());

//            TextDrawable myDrawable = TextDrawable.builder().buildRound(item.getToDoText().substring(0,1),holder.color);
            holder.mColorImageView.setImageDrawable(myDrawable);

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        BasicListAdapter(ArrayList<Material> items) {
            this.items = items;
        }


        @SuppressWarnings("deprecation")
        public class ViewHolder extends RecyclerView.ViewHolder {

            View mView;
            LinearLayout linearLayout;
            TextView mToDoTextview;
            ImageView mColorImageView;
            TextView mTimeTextView;


            public ViewHolder(View v) {
                super(v);
                mView = v;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Material item = items.get(ViewHolder.this.getAdapterPosition());
                        Intent i = new Intent(getContext(), AddToDoActivity.class);
//                        i.putExtra(TODOITEM, item);
//                        startActivityForResult(i, REQUEST_ID_TODO_ITEM);
                    }
                });
                mToDoTextview = (TextView) v.findViewById(R.id.toDoListItemTextview);
                mTimeTextView = (TextView) v.findViewById(R.id.todoListItemTimeTextView);
                mColorImageView = (ImageView) v.findViewById(R.id.toDoListItemColorImageView);
                linearLayout = (LinearLayout) v.findViewById(R.id.listItemLinearLayout);
            }


        }
    }



    @Override
    public void onPause() {
        super.onPause();
//        try {
//            storeRetrieveData.saveToFile(mVideoArrayList);
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener);
    }

    @Override
    protected int layoutRes() {
        return R.layout.manage_frame;
    }

    public static ManageVideoFrame newInstance() {
        return new ManageVideoFrame();
    }
}