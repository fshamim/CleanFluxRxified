package com.fsh.poc.cfr.repo.todorepo;

import com.fsh.poc.cfr.repos.todorepo.TodoRepoMapDB;
import com.fsh.poc.cfr.todos.TodoPoJo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Created by fshamim on 08/12/2016.
 */

public class TodoRepoMapDBTest {
    public static class data {
        static int counter = 0;

        static TodoPoJo nullIdTodo = new TodoPoJo(null, counter + "", false);
        static TodoPoJo t1 = new TodoPoJo(counter++ + "", counter + "", false);
        static TodoPoJo t2 = new TodoPoJo(counter++ + "", counter + "", false);
        static TodoPoJo t3 = new TodoPoJo(counter++ + "", counter + "", false);
        static TodoPoJo t4 = new TodoPoJo(counter++ + "", counter + "", false);
        static TodoPoJo t5 = new TodoPoJo(counter++ + "", counter + "", false);
        static TodoPoJo t6 = new TodoPoJo(counter++ + "", counter + "", false);
        static TodoPoJo t7 = new TodoPoJo(counter++ + "", counter + "", false);

    }

    TodoRepoMapDB repo;
    DB db;

    @After
    public void tearDown() {
        db.close();
    }

    @Before
    public void setup() {
        db = DBMaker.heapDB().make();
        repo = new TodoRepoMapDB(db);
    }

    @Test
    public void simpleInsertionWithKeyShouldSucceed() {
        TodoPoJo todo = repo.insert(data.t1);
        assertNotNull(todo);
        assertEquals(data.t1, todo);
        assertEquals(data.t1, repo.get(data.t1.getId()));
    }

    @Test
    public void simpleIinsertionWithNullKeyShouldSucceed() {
        TodoPoJo todo = repo.insert(data.nullIdTodo);
        assertNotNull(todo);
        assertNotNull(todo.getId());

    }

    @Test
    public void doubleInsertionShouldFail() {
        TodoPoJo todo = repo.insert(data.t1);
        assertNotNull(todo);
        assertEquals(data.t1, todo);
        assertEquals(data.t1, repo.get(data.t1.getId()));
        todo = repo.insert(data.t1);
        assertNull(todo);
    }

}
