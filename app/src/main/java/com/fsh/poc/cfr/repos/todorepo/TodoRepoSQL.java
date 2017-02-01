package com.fsh.poc.cfr.repos.todorepo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fsh.poc.cfr.model.Todo;
import com.fsh.poc.cfr.model.TodoModel;
import com.fsh.poc.cfr.repos.IEntityRepo;
import com.fsh.poc.cfr.repos.IEntityStore;

import java.util.List;

/**
 * Created by fshamim on 23/01/2017.
 */

public class TodoRepoSQL implements IEntityStore<TodoModel> {

    @Override
    public void insert(TodoModel value) {

    }

    @Override
    public List<TodoModel> list() {
        return null;
    }

    private final class DbOpenHelper extends SQLiteOpenHelper{

        public static final int DB_VERSION = 1;

        DbOpenHelper instance;

        public DbOpenHelper(Context context) {
            super(context, null, null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TodoModel.CREATE_TABLE);
            TodoModel.Insert_todo insert = new TodoModel.Insert_todo(db);
            insert.bind("Todo test 1", false);
            long id = insert.program.executeInsert();
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
