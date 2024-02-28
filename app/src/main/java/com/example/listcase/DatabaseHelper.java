package com.example.listcase;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH;
    private static String DB_NAME = "db_task.db";
    private static final int SCHEMA = 2;
    static final String TABLE_TASK = "Task";
    static final String COLUMN_ID_TASK = "Id_Task";
    static final String COLUMN_ID_CATEGORY_TASK = "Id_Category";
    static final String COLUMN_TITLE_TASK = "Title";
    static final String COLUMN_DESCRIPTION_TASK = "Description";
    static final String COLUMN_COMPLETE_TASK = "Complete";
    static final String COLUMN_DATE_CREATE_TASK = "DateCreate";
    static final String COLUMN_DATE_DEAD_LINE_TASK = "DateDeadLine";
    static final String TABLE_CATEGORY = "Category";
    static final String COLUMN_ID_CATEGORY_CATEGORY = "Id_Category";
    static final String COLUMN_TITLE_CATEGORY = "Title";
    static final String COLUMN_BLOB_CATEGORY = "Image";


    private Context myContext;

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        this.myContext=context;
        DB_PATH =context.getFilesDir().getPath() + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) { }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) { }

    void create_db(){

        File file = new File(DB_PATH);
        if (!file.exists()) {
            try(InputStream myInput = myContext.getAssets().open(DB_NAME);
                OutputStream myOutput = new FileOutputStream(DB_PATH)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            }
            catch(IOException ex){
                Log.d("DatabaseHelper", ex.getMessage());
            }
        }
    }
    public SQLiteDatabase open()throws SQLException {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
