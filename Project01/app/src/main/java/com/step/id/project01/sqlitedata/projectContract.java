package com.step.id.project01.sqlitedata;

import android.provider.BaseColumns;

public class projectContract {

    private projectContract() {}

    public static final class ProjectEntry implements BaseColumns{

        public final static String TABLE_NAME_PROJECT = "projects";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PROJECT_TITLE = "projTitle";
        public final static String COLUMN_PROJECT_DESCRIPTION = "description";
        public final static String COLUMN_CONTACT_NAME = "con_Name";
        public final static String COLUMN_CONTACT_NUMBER = "con_Num";
        public final static String COLUMN_PROJECT_LOCATION ="location";
        public final static String COLUMN_PROJECT_DATE = "projDate";
        public final static String COLUMN_PROJECT_NOTES = "notes";



    }
}
