package com.okstate.VisualComputingandImageProcessingLab.HouseRec.AddToDo;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AppDefault.AppDefaultFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutContract;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemListActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main.MainFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddToDoFragment extends AppDefaultFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "AddToDoFragment";
    private Date mLastEdited;

    private LayoutDbHelper HouseRec;
    private EditText mToDoTextBodyEditText;
    private EditText mToDoTextBodyDescription;

    public static final String ITEMLayout = "com.okstate.VisualComputingandImageProcessingLab.HouseRec.ItemLayout";

    private SwitchCompat mToDoDateSwitch;
    //    private TextView mLastSeenTextView;
    private LinearLayout mUserDateSpinnerContainingLinearLayout;
    private TextView mReminderTextView;

    private String CombinationText;

    private EditText mDateEditText;
    private EditText mTimeEditText;
    private Button mCopyClipboard;

    private ToDoItem mUserToDoItem;
    private FloatingActionButton mToDoSendFloatingActionButton;
    private FloatingActionButton mLayoutAddEdit;
    private FloatingActionButton mToDoPhotoLayoutActionButton;


    private String mUserEnteredText;
    private String mUserEnteredDescription;
    private boolean mUserHasReminder;
    private Toolbar mToolbar;
    private Date mUserReminderDate;
    private int mUserColor;

    private LinearLayout mContainerLayout;
    private String theme;
    private boolean isNet = false;
    private String oriName;
//    AnalyticsApplication app;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        app = (AnalyticsApplication) getActivity().getApplication();
//        setContentView(R.layout.new_to_do_layout);
        //Need references to these to change them during light/dark mode
        ImageButton reminderIconImageButton;
        TextView reminderRemindMeTextView;

        theme = getActivity().getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
        if (theme.equals(MainFragment.LIGHTTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_LightTheme);
            Log.d("OskarSchindler", "Light Theme");
        } else {
            getActivity().setTheme(R.style.CustomStyle_DarkTheme);
        }


        //Show an X in place of <-
        final Drawable cross = getResources().getDrawable(R.drawable.ic_clear_white_24dp);
        if (cross != null) {
            cross.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        }

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(cross);

        }


        mUserToDoItem = (ToDoItem) getActivity().getIntent().getSerializableExtra(MainFragment.TODOITEM);

        mUserEnteredText = mUserToDoItem.getToDoText();
        mUserEnteredDescription = mUserToDoItem.getmToDoDescription();
        mUserHasReminder = mUserToDoItem.hasReminder();
        mUserReminderDate = mUserToDoItem.getToDoDate();
        mUserColor = mUserToDoItem.getTodoColor();

        oriName = mUserEnteredText;

//        if(mUserToDoItem.getLastEdited()==null) {
//            mLastEdited = new Date();
//        }
//        else{
//            mLastEdited = mUserToDoItem.getLastEdited();
//        }


        reminderIconImageButton = (ImageButton) view.findViewById(R.id.userToDoReminderIconImageButton);
        reminderRemindMeTextView = (TextView) view.findViewById(R.id.userToDoRemindMeTextView);
        if (theme.equals(MainFragment.DARKTHEME)) {
            reminderIconImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm_add_white_24dp));
            reminderRemindMeTextView.setTextColor(Color.WHITE);
        }



        //Button for Copy to Clipboard
        mCopyClipboard = (Button) view.findViewById(R.id.copyclipboard);

        mContainerLayout = (LinearLayout) view.findViewById(R.id.todoReminderAndDateContainerLayout);
        mUserDateSpinnerContainingLinearLayout = (LinearLayout) view.findViewById(R.id.toDoEnterDateLinearLayout);
        mToDoTextBodyEditText = (EditText) view.findViewById(R.id.userToDoEditText);
        mToDoTextBodyDescription= (EditText) view.findViewById(R.id.userToDoDescription);
        mToDoDateSwitch = (SwitchCompat) view.findViewById(R.id.toDoHasDateSwitchCompat);
//        mLastSeenTextView = (TextView)findViewById(R.id.toDoLastEditedTextView);
        mToDoSendFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.makeToDoFloatingActionButton);

        mLayoutAddEdit = (FloatingActionButton) view.findViewById(R.id.LayoutAddEdit);
        mReminderTextView = (TextView) view.findViewById(R.id.newToDoDateTimeReminderTextView);

        mToDoPhotoLayoutActionButton = (FloatingActionButton) view.findViewById(R.id.makePhotoLayoutActionButton);


        //OnClickListener for CopyClipboard Button
        mCopyClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toDoTextContainer = mToDoTextBodyEditText.getText().toString();
                String toDoTextBodyDescriptionContainer = mToDoTextBodyDescription.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                CombinationText = "Title : " + toDoTextContainer + "\nDescription : " + toDoTextBodyDescriptionContainer + "\n -Copied From HouseRec";
                ClipData clip = ClipData.newPlainText("text", CombinationText);
                clipboard.setPrimaryClip(clip);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), "Copied To Clipboard!", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        Button ItemButton = view.findViewById(R.id.items);
//        ItemButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Intent intent = new Intent(this, AddItems_TempTest.class);
//                //intent.putExtra(ARG_ITEM_ID,ITEM_ID);
//                //intent.putExtra(LAYOUT_ITEM, mPhotoLayoutPath);
//                //startActivity(intent);
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), AddItems_TempTest.class);
//                intent.putExtra(ITEMLayout, mUserToDoItem);
//                startActivity(intent);
//            }
//
//        });




        mContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(mToDoTextBodyEditText);
                hideKeyboard(mToDoTextBodyDescription);
            }
        });


        if (mUserHasReminder && (mUserReminderDate != null)) {
//            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
            setReminderTextView();
            setEnterDateLayoutVisibleWithAnimations(true);
        }
        if (mUserReminderDate == null) {
            mToDoDateSwitch.setChecked(false);
            mReminderTextView.setVisibility(View.INVISIBLE);
        }

//        TextInputLayout til = (TextInputLayout)findViewById(R.id.toDoCustomTextInput);
//        til.requestFocus();
        mToDoTextBodyEditText.requestFocus();
        mToDoTextBodyEditText.setText(mUserEnteredText);
        mToDoTextBodyDescription.setText(mUserEnteredDescription);
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(INPUT_METHOD_SERVICE);
//        imm.showSoftInput(mToDoTextBodyEditText, InputMethodManager.SHOW_IMPLICIT);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        mToDoTextBodyEditText.setSelection(mToDoTextBodyEditText.length());


        mToDoTextBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserEnteredText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mToDoTextBodyDescription.setText(mUserEnteredDescription);
        mToDoTextBodyDescription.setSelection(mToDoTextBodyDescription.length());
        mToDoTextBodyDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserEnteredDescription = s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


//        String lastSeen = formatDate(DATE_FORMAT, mLastEdited);
//        mLastSeenTextView.setText(String.format(getResources().getString(R.string.last_edited), lastSeen));

        setEnterDateLayoutVisible(mToDoDateSwitch.isChecked());

        mToDoDateSwitch.setChecked(mUserHasReminder && (mUserReminderDate != null));
        mToDoDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    app.send(this, "Action", "Reminder Set");
//                } else {
//                    app.send(this, "Action", "Reminder Removed");
//
//                }

                if (!isChecked) {
                    mUserReminderDate = null;
                }
                mUserHasReminder = isChecked;
                setDateAndTimeEditText();
                setEnterDateLayoutVisibleWithAnimations(isChecked);
                hideKeyboard(mToDoTextBodyEditText);
                hideKeyboard(mToDoTextBodyDescription);
            }
        });


        mToDoSendFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mToDoTextBodyEditText.length() <= 0) {
                    mToDoTextBodyEditText.setError(getString(R.string.todo_error));
                } else if (mUserReminderDate != null && mUserReminderDate.before(new Date())) {
//                    app.send(this, "Action", "Date in the Past");
                    try {
                        makeResult(RESULT_CANCELED);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
//                    app.send(this, "Action", "Make Todo");
                    try {
                        hideKeyboard(mToDoTextBodyEditText);
                        hideKeyboard(mToDoTextBodyDescription);
                        String layoutname =  mToDoTextBodyEditText.getText().toString();
                        String postUrl = "mklayout/"+ layoutname;
                        new Thread(new Runnable(){
                            @Override
                            public void run(){
                                try{
                                    getRequest(postUrl);
                                    Thread.sleep(1000);
                                    if(!isNet){
                                        Looper.prepare();
                                        Toast.makeText(getContext(), "Failed to Connect to Server.", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }else{
                                        makeResult(RESULT_OK);
                                        File dir = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/");
                                        if (!dir.exists() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            dir.mkdirs();
                                        }
                                        getActivity().finish();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                hideKeyboard(mToDoTextBodyEditText);
                hideKeyboard(mToDoTextBodyDescription);
            }
        });

        mLayoutAddEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(mToDoTextBodyEditText);
                hideKeyboard(mToDoTextBodyDescription);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                    return;
                }

                Intent intent = new Intent();
                intent.setClass(getActivity(), ItemListActivity.class);
                intent.putExtra(ITEMLayout, mUserToDoItem);
                startActivity(intent);
            }
        });


        mToDoPhotoLayoutActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(mToDoTextBodyEditText);
                hideKeyboard(mToDoTextBodyDescription);
                Intent intent = new Intent();
                intent.setClass(getActivity(), ItemPhotosActivity.class);
                intent.putExtra(ITEMLayout, mUserToDoItem);
                startActivity(intent);
            }
        });


        mDateEditText = (EditText) view.findViewById(R.id.newTodoDateEditText);
        mTimeEditText = (EditText) view.findViewById(R.id.newTodoTimeEditText);

        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date;
                hideKeyboard(mToDoTextBodyEditText);
                if (mUserToDoItem.getToDoDate() != null) {
//                    date = mUserToDoItem.getToDoDate();
                    date = mUserReminderDate;
                } else {
                    date = new Date();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddToDoFragment.this, year, month, day);
                if (theme.equals(MainFragment.DARKTHEME)) {
                    datePickerDialog.setThemeDark(true);
                }
                datePickerDialog.show(getActivity().getFragmentManager(), "DateFragment");

            }
        });


        mTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date;
                hideKeyboard(mToDoTextBodyEditText);
                if (mUserToDoItem.getToDoDate() != null) {
//                    date = mUserToDoItem.getToDoDate();
                    date = mUserReminderDate;
                } else {
                    date = new Date();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddToDoFragment.this, hour, minute, DateFormat.is24HourFormat(getContext()));
                if (theme.equals(MainFragment.DARKTHEME)) {
                    timePickerDialog.setThemeDark(true);
                }
                timePickerDialog.show(getActivity().getFragmentManager(), "TimeFragment");
            }
        });

        setDateAndTimeEditText();

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
                } finally {
                    if (response != null){
                        response.close();
                    }
                }
            }
        });
    }

    private void setDateAndTimeEditText() {

        if (mUserToDoItem.hasReminder() && mUserReminderDate != null) {
            String userDate = formatDate("d MMM, yyyy", mUserReminderDate);
            String formatToUse;
            if (DateFormat.is24HourFormat(getContext())) {
                formatToUse = "k:mm";
            } else {
                formatToUse = "h:mm a";

            }
            String userTime = formatDate(formatToUse, mUserReminderDate);
            mTimeEditText.setText(userTime);
            mDateEditText.setText(userDate);

        } else {
            mDateEditText.setText(getString(R.string.date_reminder_default));
//            mUserReminderDate = new Date();
            boolean time24 = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                time24 = DateFormat.is24HourFormat(getContext());
            }
            Calendar cal = Calendar.getInstance();
            if (time24) {
                cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
            } else {
                cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1);
            }
            cal.set(Calendar.MINUTE, 0);
            mUserReminderDate = cal.getTime();
            Log.d("OskarSchindler", "Imagined Date: " + mUserReminderDate);
            String timeString;
            if (time24) {
                timeString = formatDate("k:mm", mUserReminderDate);
            } else {
                timeString = formatDate("h:mm a", mUserReminderDate);
            }
            mTimeEditText.setText(timeString);
        }
    }

    private String getThemeSet() {
        return getActivity().getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
    }

    public void hideKeyboard(EditText et) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }


    public void setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int hour = 0, minute;


        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);

        if (reminderCalendar.before(calendar)) {
            //    Toast.makeText(this, "My time-machine is a bit rusty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (DateFormat.is24HourFormat(getContext())) {
                hour = calendar.get(Calendar.HOUR_OF_DAY);
            } else {
    
                hour = calendar.get(Calendar.HOUR);
            }
        }
        minute = calendar.get(Calendar.MINUTE);

        calendar.set(year, month, day, hour, minute);
        mUserReminderDate = calendar.getTime();
        setReminderTextView();
//        setDateAndTimeEditText();
        setDateEditText();
    }

    public void setTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }

//        if(DateFormat.is24HourFormat(this) && hour == 0){
//            //done for 24h time
//                hour = 24;
//        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("OskarSchindler", "Time set: " + hour);
        calendar.set(year, month, day, hour, minute, 0);
        mUserReminderDate = calendar.getTime();

        setReminderTextView();
//        setDateAndTimeEditText();
        setTimeEditText();
    }

    public void setDateEditText() {
        String dateFormat = "d MMM, yyyy";
        mDateEditText.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public void setTimeEditText() {
        String dateFormat = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (DateFormat.is24HourFormat(getContext())) {
                dateFormat = "k:mm";
            } else {
                dateFormat = "h:mm a";
    
            }
        }
        mTimeEditText.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public void setReminderTextView() {
        if (mUserReminderDate != null) {
            mReminderTextView.setVisibility(View.VISIBLE);
            if (mUserReminderDate.before(new Date())) {
                Log.d("OskarSchindler", "DATE is " + mUserReminderDate);
                mReminderTextView.setText(getString(R.string.date_error_check_again));
                mReminderTextView.setTextColor(Color.RED);
                return;
            }
            Date date = mUserReminderDate;
            String dateString = formatDate("d MMM, yyyy", date);
            String timeString;
            String amPmString = "";

            if (DateFormat.is24HourFormat(getContext())) {
                timeString = formatDate("k:mm", date);
            } else {
                timeString = formatDate("h:mm", date);
                amPmString = formatDate("a", date);
            }
            String finalString = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPmString);
            mReminderTextView.setTextColor(getResources().getColor(R.color.secondary_text));
            mReminderTextView.setText(finalString);
        } else {
            mReminderTextView.setVisibility(View.INVISIBLE);

        }
    }

    public void makeResult(int result) throws IOException {
        Log.d(TAG, "makeResult - ok : in");
        Intent i = new Intent();
        HouseRec = new LayoutDbHelper(getContext());
        SQLiteDatabase House = HouseRec.getWritableDatabase();
        ContentValues values = new ContentValues();
        Integer flag;
        if (mUserEnteredText.length() > 0) {
            String capitalizedString = Character.toUpperCase(mUserEnteredText.charAt(0)) + mUserEnteredText.substring(1);
            mUserToDoItem.setToDoText(capitalizedString);
            capitalizedString = capitalizedString.trim();
            values.put(LayoutContract.LayoutTable.COLUMN_NAME, capitalizedString);
            Log.d(TAG, "Description: " + mUserEnteredDescription);
            mUserToDoItem.setmToDoDescription(mUserEnteredDescription);
            flag = 1;
        } else {
            mUserToDoItem.setToDoText(mUserEnteredText);
            mUserEnteredText = mUserEnteredText.trim();
            values.put(LayoutContract.LayoutTable.COLUMN_NAME, mUserEnteredText);
            Log.d(TAG, "Description: " + mUserEnteredDescription);
            mUserToDoItem.setmToDoDescription(mUserEnteredDescription);
            flag = 2;
        }
//        mUserToDoItem.setLastEdited(mLastEdited);
        if (mUserReminderDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mUserReminderDate);
            calendar.set(Calendar.SECOND, 0);
            mUserReminderDate = calendar.getTime();
        }
        mUserToDoItem.setHasReminder(mUserHasReminder);
        mUserToDoItem.setToDoDate(mUserReminderDate);
        mUserToDoItem.setTodoColor(mUserColor);
        i.putExtra(MainFragment.TODOITEM, mUserToDoItem);
        getActivity().setResult(result, i);
        values.put(LayoutContract.LayoutTable.COLUMN_DESCRIPTION, mUserEnteredDescription);
        values.put(LayoutContract.LayoutTable.COLUMN_REMINDTIME, mUserReminderDate.toString());
        // Insert the new row, returning the primary key value of the new row
        try {
            if (flag == 1){
                if (oriName.isEmpty()){
                    long newRowId = House.insert(LayoutContract.LayoutTable.TABLE_NAME, null, values);
                }else{
                    String selection = LayoutContract.LayoutTable.COLUMN_NAME + " = ?";
                    String[] selectionArgs = { oriName };
                    long newRowId = House.update(
                            LayoutContract.LayoutTable.TABLE_NAME,
                            values,
                            selection,
                            selectionArgs);
                }
            }
        }finally {
            House.close();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
//                    app.send(this, "Action", "Discard Todo");
                    try {
                        makeResult(RESULT_CANCELED);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                hideKeyboard(mToDoTextBodyEditText);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String formatDate(String formatString, Date dateToFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString);
        return simpleDateFormat.format(dateToFormat);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        setTime(hour, minute);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        setDate(year, month, day);
    }

    public void setEnterDateLayoutVisible(boolean checked) {
        if (checked) {
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void setEnterDateLayoutVisibleWithAnimations(boolean checked) {
        if (checked) {
            setReminderTextView();
            mUserDateSpinnerContainingLinearLayout.animate().alpha(1.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }
            );
        } else {
            mUserDateSpinnerContainingLinearLayout.animate().alpha(0.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }
            );
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getContext(), "Sorry,storage permission is necessary.", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_add_to_do;
    }

    public static AddToDoFragment newInstance() {
        return new AddToDoFragment();
    }



}
