package com.step.id.project01.sqlitedata;

import android.provider.BaseColumns;

public class projectAddOnContract {
    private projectAddOnContract(){}

    public static final class ProjectAddOnEntry implements BaseColumns {

        public final static String TABLE_NAME_PROJECT = "projectAddOn";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PROJECT_Status = "projectStatus";
        public final static String COLUMN_PROJECT_DATE = "projectDate";
        public final static String COLUMN_PROJECT_NOTES = "notes";
        public final static String COLUMN_PROJECT_ID = "project_ID";
        public final static String COLUMN_ADDON_IMG = "projectAddOn_img";

        public static final int COMPLETED = 0;
        public static final int IN_PROGRESS = 1;
        public static final int DEFERRED = 2;

    }
}
