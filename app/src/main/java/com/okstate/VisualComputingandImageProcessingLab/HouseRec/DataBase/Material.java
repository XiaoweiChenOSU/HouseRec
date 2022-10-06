package com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase;

import android.database.Cursor;

import org.json.JSONException;

public class Material {
    private int layout_id;
    private String location;
    private String directory;
    private String keywords;
    private String createTime;
    private LayoutDbHelper HouseRec;
    private int mTodoColor;
    private static final String LOCATION = "location";
    private static final String KEYWORDS = "keywords";



    public Material(int layout_id, String location, String directory, String keywords) {
        this.layout_id = layout_id;
        this.location = location;
        this.directory = directory;
        this.keywords = keywords;
        this.mTodoColor = 1677725;
    }

    public Material(Cursor c) throws JSONException {
        this.keywords = c.getString(c.getColumnIndex(LayoutContract.LayoutMaterial.COLUMN_KEYWORDS));
        this.location = c.getString(c.getColumnIndex(LayoutContract.LayoutMaterial.COLUMN_LOCATION));
    }

    public Integer getLayout() {
        return layout_id;
    }

    public void setLayout(Integer layout_id) {
        this.layout_id = layout_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTime() {
        return createTime;
    }

    public void setTime(String createTime) {
        this.createTime = createTime;
    }

    public int getTodoColor() {
        return mTodoColor;
    }

    public void setTodoColor(int mTodoColor) {
        this.mTodoColor = mTodoColor;
    }
}
