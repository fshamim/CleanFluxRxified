package com.fsh.poc.cfr.repos.todorepo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fsh.poc.cfr.model.TodoModel;

/**
 * Created by fshamim on 14/03/2017.
 */
public final class DbOpenHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    private static final String DB_NAME = "todo.db";

    public static DbOpenHelper instance;

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context, DB_NAME);
        }
        return instance;
    }

    public static DbOpenHelper getInMemoryInstance(Context context) {
        return new DbOpenHelper(context, null);
    }


    private DbOpenHelper(Context context, String dbname) {
        super(context, dbname, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TodoModel.CREATE_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
