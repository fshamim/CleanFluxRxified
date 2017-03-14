package com.fsh.poc.cfr.test;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.fsh.poc.cfr.model.Todo;
import com.fsh.poc.cfr.repos.todorepo.TodoRepoSQL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
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

    @Before
    public void setup() {
        repo = new TodoRepoSQL(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() {
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
            final int finalI = i;
            repo.update(new Todo() {
                @Override
                public long _id() {
                    return list.get(finalI)._id();
                }

                @NonNull
                @Override
                public String text() {
                    return list.get(finalI).text();
                }

                @Nullable
                @Override
                public Boolean is_completed() {
                    return Boolean.TRUE;
                }
            });
        }

        List<Todo> list2 = repo.list();
        assertThat(list2, notNullValue());
        assertThat(list2.size(), is(4));
        for (int i = 0; i < list2.size(); ++i) {
            assertThat(list2.get(i).is_completed(), is(Boolean.TRUE));
        }
    }

    private Todo createRandomTodo() {
        return new Todo() {
            @Override
            public long _id() {
                return 0;
            }

            @NonNull
            @Override
            public String text() {
                return UUID.randomUUID().toString();
            }

            @Nullable
            @Override
            public Boolean is_completed() {
                int i = new Random().nextInt(1);
                return i == 1 ? Boolean.TRUE : Boolean.FALSE;
            }
        };
    }
}
