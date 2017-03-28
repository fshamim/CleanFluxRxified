package com.fsh.poc.cfr.repos.todorepo;

import android.database.Cursor;

import com.fsh.poc.cfr.model.Todo;
import com.fsh.poc.cfr.model.TodoModel;
import com.fsh.poc.cfr.repos.IEntityRepo;
import com.squareup.sqldelight.SqlDelightStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fshamim on 23/01/2017.
 */

public class TodoRepoSQL implements IEntityRepo<Todo> {

    DbOpenHelper helper;

    public TodoRepoSQL(DbOpenHelper helper) {
        this.helper = helper;
    }

    @Override
    public void insert(Todo value) {
        TodoModel.Insert_todo insert = new TodoModel.Insert_todo(helper.getWritableDatabase());
        insert.bind(value.text(), value.is_completed());
        insert.program.executeInsert();
    }

    @Override
    public List<Todo> list() {
        List<Todo> result = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery(Todo.FACTORY.select_all().statement, new String[0]);
        while (cursor.moveToNext()) {
            result.add(Todo.FACTORY.select_allMapper().map(cursor));
        }
        return result;
    }

    @Override
    public void clear() {
        helper.getWritableDatabase().delete(Todo.TABLE_NAME, null, null);
    }

    @Override
    public void delete(Todo value) {
        TodoModel.Delete_todo_by_id delete = new TodoModel.Delete_todo_by_id(helper.getWritableDatabase());
        delete.bind(value._id());
        delete.program.executeUpdateDelete();
    }

    @Override
    public Todo getById(long id) {
        SqlDelightStatement query = Todo.FACTORY.select_by_id(id);
        Cursor cursor = helper.getReadableDatabase().rawQuery(query.statement, query.args);
        Todo todo = null;
        if (cursor.moveToNext()) {
            todo = Todo.FACTORY.select_by_idMapper().map(cursor);
        }
        return todo;
    }

    @Override
    public void update(Todo value) {
        TodoModel.Update_by_id update = new Todo.Update_by_id(helper.getWritableDatabase());
        update.bind(value.text(), value.is_completed(), value._id());
        update.program.executeUpdateDelete();
    }
}
