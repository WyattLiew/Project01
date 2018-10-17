package com.step.id.project01.sqlitedata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.step.id.project01.sqlitedata.projectAddOnContract.ProjectAddOnEntry;
import static com.step.id.project01.sqlitedata.projectContract.ProjectEntry;

public class ProjectDbHelper extends SQLiteOpenHelper {


    public static final String LOG_TAG = ProjectDbHelper.class.getSimpleName();

    /**Name of the database file*/
    private static final String DATABASE_NAME = "ProjectManager.db";

    //Database version. If you chage the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public final static String TABLE_NAME_PENDING = "defects";

    public final static String _ID = "_ID";
    public final static String COLUMN_CONTACT_NAME = "con_Name";
    public final static String COLUMN_CONTACT_NUMBER = "con_Num";
    public final static String COLUMN_PROJECT_LOCATION ="location";
    public final static String COLUMN_PROJECT_MANAGER = "projManager";
    public final static String COLUMN_PROJECT_DATE = "projDate";
    public final static String COLUMN_DEFECT_1 = "defect_1";
    public final static String COLUMN_DEFECT_2 = "defect_2";
    public final static String COLUMN_DEFECT_3 = "defect_3";
    public final static String COLUMN_DEFECT_COMMENTS = "comments";
    public final static String COLUMN_DEFECT_IMG = "defect_img";

    public static final int PROJECT_TYPE_1 = 0;
    public static final int PROJECT_TYPE_2 = 1;
    public static final int PROJECT_TYPE_3 = 2;

    // Pending create table statement
    private String CREATE_TABLE_PENDING = "CREATE TABLE " + TABLE_NAME_PENDING + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CONTACT_NAME + " TEXT NOT NULL,"
            + COLUMN_PROJECT_LOCATION + " TEXT NOT NULL,"
            + COLUMN_CONTACT_NUMBER + " INTEGER NOT NULL,"
            + COLUMN_PROJECT_MANAGER + " TEXT NOT NULL,"
            + COLUMN_PROJECT_DATE + " TEXT NOT NULL,"
            + COLUMN_DEFECT_1 + " TEXT,"
            + COLUMN_DEFECT_2 + " TEXT,"
            + COLUMN_DEFECT_3 + " TEXT,"
            + COLUMN_DEFECT_COMMENTS + " TEXT,"
            + COLUMN_DEFECT_IMG + " BLOB" + ")";

    // Project create table statement
    private String CREATE_TABLE_PROJECT = "CREATE TABLE " + ProjectEntry.TABLE_NAME_PROJECT + "("
            + ProjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ProjectEntry.COLUMN_PROJECT_TITLE + " TEXT NOT NULL,"
            + ProjectEntry.COLUMN_PROJECT_DESCRIPTION + " TEXT,"
            + ProjectEntry.COLUMN_CONTACT_NAME + " TEXT NOT NULL,"
            + ProjectEntry.COLUMN_CONTACT_NUMBER + " INTEGER NOT NULL,"
            + ProjectEntry.COLUMN_PROJECT_DATE + " TEXT NOT NULL,"
            + ProjectEntry.COLUMN_PROJECT_LOCATION + " TEXT NOT NULL,"
            + ProjectEntry.COLUMN_PROJECT_NOTES + " TEXT" + ")";

    // Project add on create table statement
    private String CREATE_TABLE_PROJECT_ADD_ON = "CREATE TABLE " + ProjectAddOnEntry.TABLE_NAME_PROJECT + "("
            + ProjectAddOnEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ProjectAddOnEntry.COLUMN_PROJECT_Status + " INTEGER NOT NULL,"
            + ProjectAddOnEntry.COLUMN_PROJECT_DATE + " TEXT NOT NULL,"
            + ProjectAddOnEntry.COLUMN_PROJECT_NOTES + " TEXT,"
            + ProjectAddOnEntry.COLUMN_ADDON_IMG + " BLOB,"
            + ProjectAddOnEntry.COLUMN_PROJECT_ID + " INTEGER" + ")";

    public ProjectDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PENDING);
        db.execSQL(CREATE_TABLE_PROJECT);
        db.execSQL(CREATE_TABLE_PROJECT_ADD_ON);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PENDING);
        db.execSQL("DROP TABLE IF EXISTS " + ProjectEntry.TABLE_NAME_PROJECT);
        db.execSQL("DROP TABLE IF EXISTS " + ProjectAddOnEntry.TABLE_NAME_PROJECT);

        //create new tables
        onCreate(db);
    }

    /** pending Editor Start> **/

    public void insert_pending(String location, String conName, String conNum, String projManager,String date,String defect1, String defect2, String defect3, String comments, byte[] image) throws SQLException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PROJECT_LOCATION, location);
        values.put(COLUMN_CONTACT_NAME, conName);
        values.put(COLUMN_CONTACT_NUMBER, conNum);
        values.put(COLUMN_PROJECT_MANAGER,projManager);
        values.put(COLUMN_PROJECT_DATE,date);
        values.put(COLUMN_DEFECT_1, defect1);
        values.put(COLUMN_DEFECT_2, defect2);
        values.put(COLUMN_DEFECT_3, defect3);
        values.put(COLUMN_DEFECT_COMMENTS, comments);
        values.put(COLUMN_DEFECT_IMG,image);

        db.insert(TABLE_NAME_PENDING,null,values);
        db.close();
    }

    public void update_pending(int id,String location, String conName, String conNum, String projManager,String date,String defect1, String defect2, String defect3, String comments, byte[] image) throws SQLException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PROJECT_LOCATION, location);
        values.put(COLUMN_CONTACT_NAME, conName);
        values.put(COLUMN_CONTACT_NUMBER, conNum);
        values.put(COLUMN_PROJECT_MANAGER,projManager);
        values.put(COLUMN_PROJECT_DATE,date);
        values.put(COLUMN_DEFECT_1, defect1);
        values.put(COLUMN_DEFECT_2, defect2);
        values.put(COLUMN_DEFECT_3, defect3);
        values.put(COLUMN_DEFECT_COMMENTS, comments);
        values.put(COLUMN_DEFECT_IMG,image);

        db.update(TABLE_NAME_PENDING,values,"_id="+id, null);
        db.close();
    }

    public void delete_pending(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME_PENDING,"_id="+id, null);
        db.close();
    }

    public Cursor viewData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + TABLE_NAME_PENDING;
        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getItemID(String pos){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_PENDING +
                " WHERE " + _ID + " = '" + pos + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /** pending Editor End> **/

    /** Project Editor Start> **/
    public void insert_project(String location, String conName, String conNum,String date,String description,String title,String note) throws SQLException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ProjectEntry.COLUMN_PROJECT_TITLE, title);
        values.put(ProjectEntry.COLUMN_PROJECT_DESCRIPTION, description);
        values.put(ProjectEntry.COLUMN_PROJECT_DATE, date);
        values.put(ProjectEntry.COLUMN_PROJECT_LOCATION, location);
        values.put(ProjectEntry.COLUMN_CONTACT_NAME, conName);
        values.put(ProjectEntry.COLUMN_CONTACT_NUMBER, conNum);
        values.put(ProjectEntry.COLUMN_PROJECT_NOTES, note);


        db.insert(ProjectEntry.TABLE_NAME_PROJECT,null,values);
        db.close();
    }
    public void update_project(int id,String location, String conName, String conNum,String date,String description,String title,String note) throws SQLException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ProjectEntry.COLUMN_PROJECT_TITLE, title);
        values.put(ProjectEntry.COLUMN_PROJECT_DESCRIPTION, description);
        values.put(ProjectEntry.COLUMN_PROJECT_DATE, date);
        values.put(ProjectEntry.COLUMN_PROJECT_LOCATION, location);
        values.put(ProjectEntry.COLUMN_CONTACT_NAME, conName);
        values.put(ProjectEntry.COLUMN_CONTACT_NUMBER, conNum);
        values.put(ProjectEntry.COLUMN_PROJECT_NOTES, note);

        db.update(ProjectEntry.TABLE_NAME_PROJECT,values,"_id="+id,null);
        db.close();
    }

    public void delete_project(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ProjectEntry.TABLE_NAME_PROJECT,"_id="+id, null);
        db.close();
    }

    public Cursor viewProjectData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + ProjectEntry.TABLE_NAME_PROJECT;
        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }
    public Cursor getProjectItemID(String pos){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + ProjectEntry.TABLE_NAME_PROJECT +
                " WHERE " + ProjectEntry._ID + " = '" + pos + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    /** Project Editor END> **/

    /** Project Addon Start> **/
    public void insert_projectAddOn(int status,String date,String note,byte[] image,int projectid) throws SQLException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ProjectAddOnEntry.COLUMN_PROJECT_Status,status);
        values.put(ProjectAddOnEntry.COLUMN_PROJECT_DATE,date);
        values.put(ProjectAddOnEntry.COLUMN_PROJECT_NOTES,note);
        values.put(ProjectAddOnEntry.COLUMN_ADDON_IMG,image);
        values.put(ProjectAddOnEntry.COLUMN_PROJECT_ID,projectid);

        db.insert(ProjectAddOnEntry.TABLE_NAME_PROJECT,null,values);
        db.close();
    }

    public void update_projectAddOn(int id,int status,String date,String note,byte[] image,int projectid) throws SQLException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ProjectAddOnEntry.COLUMN_PROJECT_Status,status);
        values.put(ProjectAddOnEntry.COLUMN_PROJECT_DATE,date);
        values.put(ProjectAddOnEntry.COLUMN_PROJECT_NOTES,note);
        values.put(ProjectAddOnEntry.COLUMN_ADDON_IMG,image);
        values.put(ProjectAddOnEntry.COLUMN_PROJECT_ID,projectid);

        db.update(ProjectAddOnEntry.TABLE_NAME_PROJECT,values,"_id="+id,null);
        db.close();
    }

    public Cursor viewProjectList(int pos){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + ProjectAddOnEntry.TABLE_NAME_PROJECT +
                " WHERE " + ProjectAddOnEntry.COLUMN_PROJECT_ID + " = '" + pos + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getProjectAddOnItemID(long pos){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + ProjectAddOnEntry.TABLE_NAME_PROJECT +
                " WHERE " + ProjectAddOnEntry._ID + " = '" + pos + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void delete_projectAddOn(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ProjectAddOnEntry.TABLE_NAME_PROJECT,"_id="+id, null);
        db.close();
    }

    public void delete_allProjectAddOn(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ProjectAddOnEntry.TABLE_NAME_PROJECT,"project_ID="+id, null);
        db.close();
    }

    /** Project Addon END> **/
}
