package com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase;

import android.provider.BaseColumns;

public final class LayoutContract {

    private LayoutContract() {
    }

    public static class LayoutTable implements BaseColumns {
        public static final String TABLE_NAME = "Layout";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_DESCRIPTION = "Description";
        public static final String COLUMN_REMINDTIME = "Remind_Time";
    }

    public static class LayoutMaterial implements BaseColumns {
        public static final String TABLE_NAME = "LayoutMaterial";
        public static final String COLUMN_LAYOUTID = "LayoutId";
        public static final String COLUMN_LOCATION = "Location";
        public static final String COLUMN_DIRECTORY = "Directory";
        public static final String COLUMN_KEYWORDS = "Keywords";
        public static final String COLUMN_CREATETIME = "Create_Time";
    }
}
