package com.fsh.poc.cfr.test;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.fsh.poc.cfr.model.Todo;
import com.fsh.poc.cfr.model.TodoModel;
import com.fsh.poc.cfr.repos.todorepo.DbOpenHelper;
import com.fsh.poc.cfr.repos.todorepo.TodoRepoSQL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by fshamim on 14/03/2017.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class TodoRepoSQLiteTest {


    private TodoRepoSQL repo;
    private DbOpenHelper inmemDB;

    @Before
    public void setup() {
        inmemDB = DbOpenHelper.getInMemoryInstance(InstrumentationRegistry.getTargetContext());
        repo = new TodoRepoSQL(inmemDB);
        initTestData();
    }

    private void initTestData() {
        TodoModel.Insert_todo insert = new TodoModel.Insert_todo(inmemDB.getWritableDatabase());
        insert.bind("Todo test 1", false);
        insert.program.executeInsert();
        insert.bind("Todo test 2", false);
        insert.program.executeInsert();
        insert.bind("Todo test 3", false);
        insert.program.executeInsert();
        insert.bind("Todo test 4", false);
        insert.program.executeInsert();
    }

    @After
    public void tearDown() {
        inmemDB.close();
    }

    @Test
    public void initialDataShouldBeThere() {
        List<Todo> list = repo.list();
        assertNotNull(list);
        assertThat(list.size(), is(4));
    }

    @Test
    public void insertStressTest() {
        repo.clear();
        final int SIZE = 10;
        for (int i = 0; i < SIZE; ++i) {
            Todo todo = createRandomTodo();
            repo.insert(todo);
        }

        List<Todo> list = repo.list();
        assertNotNull(list);
        assertThat(list.size(), is(SIZE));
    }

    @Test
    public void deleteTestPositives() {
        List<Todo> list = repo.list();
        assertNotNull(list);
        assertThat(list.size(), is(4));
        for (int i = 0; i < list.size(); ++i) {
            Todo todo = list.get(i);
            repo.delete(todo);
        }
        list = repo.list();
        assertNotNull(list);
        assertThat(list.size(), is(0));
        assertTrue(list.isEmpty());

        //deleted again should be allowed and shouldn't change the result
        for (int i = 0; i < list.size(); ++i) {
            Todo todo = list.get(i);
            repo.delete(todo);
        }
        list = repo.list();
        assertNotNull(list);
        assertThat(list.size(), is(0));
        assertTrue(list.isEmpty());
    }

    @Test
    public void getByIdTest() {
        List<Todo> list = repo.list();
        // positives
        assertThat(list, notNullValue());
        assertThat(list.size(), is(4));
        assertThat(repo.getById(1), is(list.get(0)));
        assertThat(repo.getById(2), is(list.get(1)));
        assertThat(repo.getById(3), is(list.get(2)));
        assertThat(repo.getById(4), is(list.get(3)));

        //negatives
        assertThat(repo.getById(5), nullValue());
        assertThat(repo.getById(0), nullValue());
        assertThat(repo.getById(-1), nullValue());

        //after deleting get should also return null
        repo.delete(list.get(3));
        assertThat(repo.getById(4), nullValue());

    }

    @Test
    public void updateTest() {
        //Given initial data is there
        final List<Todo> list = repo.list();
        assertNotNull(list);
        assertThat(list.size(), is(4));
        //we want to toggle all the  to completion
        for (int i = 0; i < list.size(); ++i) {
            repo.update(Todo.create(list.get(i)._id(), list.get(i).text(), Boolean.TRUE));
        }

        List<Todo> list2 = repo.list();
        assertThat(list2, notNullValue());
        assertThat(list2.size(), is(4));
        for (int i = 0; i < list2.size(); ++i) {
            assertThat(list2.get(i).is_completed(), is(Boolean.TRUE));
        }
    }

    private Todo createRandomTodo() {
        return Todo.create(0, UUID.randomUUID().toString(), new Random().nextInt(1) == 1 ? Boolean.TRUE : Boolean.FALSE);
    }
}
