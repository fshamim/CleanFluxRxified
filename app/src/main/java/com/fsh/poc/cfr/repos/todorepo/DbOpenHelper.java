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

    public static DbOpenHelper instance;

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context);
        }
        return instance;
    }

    private DbOpenHelper(Context context) {
        super(context, null, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TodoModel.CREATE_TABLE);
        TodoModel.Insert_todo insert = new TodoModel.Insert_todo(db);
        insert.bind("Todo test 1", false);
        insert.program.executeInsert();
        insert.bind("Todo test 2", false);
        insert.program.executeInsert();
        insert.bind("Todo test 3", false);
        insert.program.executeInsert();
        insert.bind("Todo test 4", false);
        insert.program.executeInsert();
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
