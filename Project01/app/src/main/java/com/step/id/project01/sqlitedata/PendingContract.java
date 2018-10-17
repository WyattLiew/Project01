package com.step.id.project01.sqlitedata;

import android.provider.BaseColumns;

public class PendingContract {

    private PendingContract() {}

    public static final class PendingEntry implements BaseColumns{

        public final static String TABLE_NAME_PENDING = "defects";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_CONTACT_NAME = "con_Name";
        public final static String COLUMN_CONTACT_NUMBER = "con_Num";
        public final static String COLUMN_PROJECT_LOCATION ="location";
        public final static String COLUMN_PROJECT_MANAGER = "projManager";
        public final static String COLUMN_PROJECT_DATE = "projDate";
        public final static String COLUMN_DEFECT_1 = "defect_1";
        public final static String COLUMN_DEFECT_2 = "defect_2";
        public final static String COLUMN_DEFECT_3 = "defect_3";
        public final static String COLUMN_DEFECT_COMMENTS = "comments";

        public static final int PROJECT_TYPE_1 = 0;
        public static final int PROJECT_TYPE_2 = 1;
        public static final int PROJECT_TYPE_3 = 2;



    }
}
