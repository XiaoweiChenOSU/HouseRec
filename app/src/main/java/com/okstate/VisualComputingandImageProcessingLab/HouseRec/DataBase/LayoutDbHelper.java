package com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutContract.LayoutMaterial;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutContract.LayoutTable;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LayoutDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HouseRec.db";
    private static final int DATABASE_VERSION =12;



    private SQLiteDatabase db;

    public LayoutDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.db =sqLiteDatabase;
        final String SQL_CREATE_LAYOUT_TABLE = "CREATE TABLE " +
                LayoutTable.TABLE_NAME + " ( " +
                LayoutTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LayoutTable.COLUMN_NAME + " TEXT UNIQUE, " +
                LayoutTable.COLUMN_DESCRIPTION + " TEXT, " +
                LayoutTable.COLUMN_REMINDTIME + " TIMESTAMP NOT NULL DEFAULT (DATETIME('NOW','LOCALTIME'))" +
                " ) ";

        final String SQL_CREATE_LAYOUTMATERAIL_TABLE = "CREATE TABLE " +
                LayoutMaterial.TABLE_NAME + " ( " +
                LayoutMaterial._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LayoutMaterial.COLUMN_LAYOUTID + " INTEGER NOT NULL, " +
                LayoutMaterial.COLUMN_LOCATION + " TEXT, " +
                LayoutMaterial.COLUMN_DIRECTORY + " TEXT, " +
                LayoutMaterial.COLUMN_KEYWORDS + " TEXT, " +
                LayoutMaterial.COLUMN_CREATETIME + " TIMESTAMP NOT NULL DEFAULT (DATETIME('NOW','LOCALTIME'))" +
                " ) ";

        db.execSQL(SQL_CREATE_LAYOUT_TABLE);
        db.execSQL(SQL_CREATE_LAYOUTMATERAIL_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        this.db =sqLiteDatabase;
        db.execSQL("DROP TABLE IF EXISTS " + LayoutTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LayoutMaterial.TABLE_NAME);
        onCreate(db);
    }


    public Long addLayout(Layout Layout){
        ContentValues cv = new ContentValues();
        cv.put(LayoutTable.COLUMN_NAME, Layout.getName());
        cv.put(LayoutTable.COLUMN_DESCRIPTION, Layout.getDescription());
        cv.put(LayoutTable.COLUMN_REMINDTIME, Layout.getTime());
        long rowid = db.insert(LayoutTable.TABLE_NAME, null, cv);
        return rowid;
    }

    public Long addLayoutMaterial(Material material){
        ContentValues cv = new ContentValues();
        cv.put(LayoutMaterial.COLUMN_LAYOUTID, material.getLayout());
        cv.put(LayoutMaterial.COLUMN_LOCATION, material.getLocation());
        cv.put(LayoutMaterial.COLUMN_DIRECTORY, material.getDirectory());
        cv.put(LayoutMaterial.COLUMN_KEYWORDS, material.getKeywords());
        cv.put(LayoutMaterial.COLUMN_CREATETIME, material.getKeywords());
        long rowid = db.insert(LayoutMaterial.TABLE_NAME, null, cv);
        return rowid;
    }

    public Long updateLayout(Layout Layout, String layout_name){
        ContentValues cv = new ContentValues();
        cv.put(LayoutTable.COLUMN_NAME, Layout.getName());
        cv.put(LayoutTable.COLUMN_DESCRIPTION, Layout.getDescription());
        cv.put(LayoutTable.COLUMN_REMINDTIME, Layout.getTime());
        String where = "Name LIKE ";
        String[] selectionArgs = { layout_name };
        long rowid = db.update(LayoutTable.TABLE_NAME, cv, where, selectionArgs);
        return rowid;
    }

    public Long updateLayoutMaterial(Material material, Integer material_id){
        ContentValues cv = new ContentValues();
        cv.put(LayoutMaterial.COLUMN_LAYOUTID, material.getLayout());
        cv.put(LayoutMaterial.COLUMN_LOCATION, material.getLocation());
        cv.put(LayoutMaterial.COLUMN_DIRECTORY, material.getDirectory());
        cv.put(LayoutMaterial.COLUMN_KEYWORDS, material.getKeywords());
        cv.put(LayoutMaterial.COLUMN_CREATETIME, material.getKeywords());
        String where = "_id = " + material_id;
        long rowid = db.update(LayoutMaterial.TABLE_NAME, cv, where, null);
        return rowid;
    }

    public Long delLayout(Integer layout_id){
        String where = "_id = " + layout_id;
        long rowid = db.delete(LayoutTable.TABLE_NAME, where, null);
        return rowid;
    }

    public long delLayoutMaterial(Material material, Integer material_id){
        String where = "_id = " + material_id;
        long rowid = db.delete(LayoutMaterial.TABLE_NAME, where, null);
        return rowid;
    }


//    public List<Material> getAllMaterials(Integer layout_id){
//        List<Material> MaterialList = new ArrayList<>();
//
//        if(c.moveToFirst()){
//            do{
//                Material material = new Material();
//                material.setLayout(c.getInt(c.getColumnIndex(LayoutMaterial.COLUMN_LAYOUTID)));
//                material.setLocation(c.getString(c.getColumnIndex(LayoutMaterial.COLUMN_LOCATION)));
//                material.setDirectory(c.getString(c.getColumnIndex(LayoutMaterial.COLUMN_DIRECTORY)));
//                material.setKeywords(c.getString(c.getColumnIndex(LayoutMaterial.COLUMN_KEYWORDS)));
//                MaterialList.add(material);
//            } while (c.moveToNext());
//        }
//
//        c.close();
//        return MaterialList;
//    }




    /**
     * Getting all labels
     * returns list of labels
     * */
    public void Delete_Video(String keyword){
        db = getWritableDatabase();
        try {
            String[] delArgs = { keyword };
            db.delete(LayoutMaterial.TABLE_NAME, LayoutMaterial.COLUMN_KEYWORDS + "=?", delArgs);
        }finally {
            db.close();
        }

//        Cursor cursor2 = db.rawQuery("SELECT  * FROM "+ LayoutMaterial.TABLE_NAME, null);
//        if (cursor2.moveToFirst()) {
//            do {
//                if(cursor2.getString(cursor2.getColumnIndex(LayoutMaterial.COLUMN_KEYWORDS)) == keyword){
//
//                }
//
//            } while (cursor2.moveToNext());
//        }

    }

    public List<String> getAllLabels(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM LayoutMaterial";

        db = getReadableDatabase();
        Cursor cursor2 = db.rawQuery("SELECT  * FROM "+ LayoutMaterial.TABLE_NAME, null);
        //Cursor cursor2 = db.rawQuery("SELECT * FROM " + LayoutMaterial.TABLE_NAME + "WHERE " + LayoutMaterial.COLUMN_LAYOUTID + "=" + layout_id, null);



        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {
                labels.add(cursor2.getString(4));
            } while (cursor2.moveToNext());
        }

        // closing connection
        cursor2.close();
        //db.close();

        // returning lables
        return labels;
    }


    public boolean getLayout(){
        try{
            db = getReadableDatabase();
            Cursor cursor2 = db.query(LayoutTable.TABLE_NAME, new String[]{}, null, null,null,null,null,null);
            if (cursor2.moveToFirst()) {
                return true;
            }
        }finally {
            db.close();
        }
        return false;
    }

    public String getLayoutName(){
        String layoutname = "";
        try{
            db = getReadableDatabase();
            Cursor cursor2 = db.query(LayoutTable.TABLE_NAME, new String[]{}, null, null,null,null,null,null);
            if (cursor2.moveToFirst()) {
                layoutname = cursor2.getString(1);
            }
        }finally {
            db.close();
        }
        return layoutname;
    }


    public int getLayoutId(String layout){
        int layoutid = -1;
        Log.d("LayoutDbHelper", layout);
        try{
        db = getReadableDatabase();

        String query = LayoutTable.COLUMN_NAME + "= ?";
        String[] selArgs = {layout};
        Cursor cursor2 = db.query(LayoutTable.TABLE_NAME, new String[]{}, query, selArgs,null,null,null,null);
        if (cursor2.moveToFirst()) {
            do {
                layoutid = cursor2.getInt(0);
            } while (cursor2.moveToNext());
        }
    }finally {
        //db.close();
    }
        return layoutid;
    }
    public String getDirectory(String keyword, String layout ) {
        String directory = "null";
        try {
            db = getReadableDatabase();
            int layoutid = getLayoutId(layout);
            keyword = keyword.replace(" ","");
            String query = LayoutMaterial.COLUMN_KEYWORDS + "= ?"+ " AND " + LayoutMaterial.COLUMN_LAYOUTID+"= ?";
            String[] selArgs = { keyword, String.valueOf(layoutid)};
            Cursor cursor2 = db.query(LayoutMaterial.TABLE_NAME, new String[]{}, query, selArgs,null,null,null,null);
            Log.d("LayoutDbHelper", String.valueOf(layoutid));
            if (cursor2.moveToFirst()) {
                do {
                    directory = cursor2.getString(3);
                } while (cursor2.moveToNext());
            }
        }finally {
            db.close();
        }
        return directory;
    }

    public String getLocation(String keyword, String layout) {
        String location = "null";
        try {
            db = getReadableDatabase();
            int layoutid = getLayoutId(layout);
            String query = LayoutMaterial.COLUMN_KEYWORDS + "= ?"+" AND " +LayoutMaterial.COLUMN_LAYOUTID+"= ?";
            String[] selArgs = { keyword, String.valueOf(layoutid)};
            Cursor cursor2 = db.query(LayoutMaterial.TABLE_NAME, new String[]{}, query, selArgs,null,null,null,null);
            if (cursor2.moveToFirst()) {
                do {
                    location = cursor2.getString(2);
                } while (cursor2.moveToNext());
            }

        }finally {
            db.close();
        }
        return location;
    }

    public List<String>  getKeyword(String location, String layout) {
        List<String> keywords = new ArrayList<String>();
        try {
            db = getReadableDatabase();
            int layoutid = getLayoutId(layout);
            //location = location.substring(0,1).toUpperCase()+location.substring(1);
            String query = LayoutMaterial.COLUMN_LOCATION + "= ?"+" AND " + LayoutMaterial.COLUMN_LAYOUTID+"= ?";
            String[] selArgs = { location, String.valueOf(layoutid) };
            Cursor cursor2 = db.query(LayoutMaterial.TABLE_NAME, new String[]{}, query, selArgs,null,null,null,null);
            if (cursor2.moveToFirst()) {
                do {
                    keywords.add(cursor2.getString(4));
                } while (cursor2.moveToNext());
            }

        }finally {
            db.close();
        }
        return keywords;
    }


//    public List<String> getLocatoinLabels(String location){
//        List<String> keywords = new ArrayList<String>();
//
//        return keywords;
//    }



    public List<String> getAllData(){
        //SQLiteDatabase db = this.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                LayoutMaterial.COLUMN_DIRECTORY,
                LayoutMaterial.COLUMN_KEYWORDS
        };

// Filter results WHERE "title" = 'My Title'
        String selection = LayoutMaterial.COLUMN_DIRECTORY + " = ?";
        String[] selectionArgs = { "My Title" };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                LayoutMaterial.COLUMN_KEYWORDS + " DESC";

        Cursor cursor = db.query(
                LayoutMaterial.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(LayoutMaterial.COLUMN_KEYWORDS));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds;

    }


    public ArrayList<Material> loadFromDatabase(String layout_name) throws JSONException {
        ArrayList<Material> items = new ArrayList<>();
        try {
            db = getReadableDatabase();
//            Cursor c_layout = db.query("SELECT"+ LayoutTable._ID +"  FROM " + LayoutTable.TABLE_NAME + "WHERE " + LayoutTable.COLUMN_NAME + "=" + layout_name, null);
//
            String query = LayoutTable.COLUMN_NAME + " = ? ";
            String[] selArgs = { layout_name };
            Cursor c_layout = db.query(LayoutTable.TABLE_NAME, new String[]{"_id"}, query, selArgs,null,null,null,null);
            if(c_layout.moveToFirst()){
                Integer LayoutId = c_layout.getInt(0);
                query = LayoutMaterial.COLUMN_LAYOUTID + " = ? ";
                String[] selDetailsArgs = { LayoutId.toString() };
                Cursor c = db.query(LayoutMaterial.TABLE_NAME, new String[]{}, query, selDetailsArgs,null,null,null,null);
                if(c.moveToFirst()){
                    do{
                        Material item = new Material(c);
                        items.add(item);
                    } while (c.moveToNext());
                }
            }
        } catch (JSONException e) {
            //do nothing about it
            //file won't exist first time app is run
        } finally {
            db.close();
        }
        return items;
    }


}

