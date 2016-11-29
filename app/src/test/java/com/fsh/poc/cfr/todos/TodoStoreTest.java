package com.fsh.poc.cfr.todos;

import com.fsh.poc.cfr.todos.impl.TodoStoreImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

/**
 * Created by fshamim on 30/11/2016.
 */

public class TodoStoreTest {

    TodoStore store;
    Flowable<TodoStore.State> flowable;

    int idCounter;

    @Before
    public void setup() {
        store = new TodoStoreImpl();
        flowable = store.asFlowable();
        idCounter = 0;
    }

    @After
    public void after() {
    }

    @Test
    public void emptyState() {
        TodoStore.State expectedState = new TodoStore.State(false, new ArrayList<TodoPoJo>(), TodoStore.TodoFilter.ALL);
        TestSubscriber<TodoStore.State> subscriber = flowable.test();
        System.out.println("Events" + subscriber.getEvents());
        subscriber.assertValue(expectedState);
    }

    @Test
    public void addTodoToEmptyStore() {
        ArrayList<TodoPoJo> emptyList = new ArrayList<>();
        TestSubscriber<TodoStore.State> subscriber = flowable.test();

        store.processAction(new TodoStore.AddTodoAction("Todo"));
        ArrayList<TodoPoJo> oneElemList = new ArrayList<>();
        String id = "0 Todo";
        oneElemList.add(new TodoPoJo(id, id, false));

        TodoStore.State emptyState = new TodoStore.State(false, emptyList, TodoStore.TodoFilter.ALL);
        TodoStore.State workingState = new TodoStore.State(true, emptyList, TodoStore.TodoFilter.ALL);
        TodoStore.State newTodoState = new TodoStore.State(false, oneElemList, TodoStore.TodoFilter.ALL);

        subscriber.assertValues(emptyState, workingState, newTodoState);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();
    }

    @Test
    public void addMultipleTodos() {
        ArrayList<TodoPoJo> emptyList = new ArrayList<>();
        TestSubscriber<TodoStore.State> subscriber = flowable.test();

        store.processAction(new TodoStore.AddTodoAction("Todo"));
        ArrayList<TodoPoJo> oneElemList = new ArrayList<>();
        String id = getId();
        oneElemList.add(new TodoPoJo(id, id, false));
        store.processAction(new TodoStore.AddTodoAction("Todo"));
        ArrayList<TodoPoJo> twoElemList = new ArrayList<>();
        twoElemList.add(new TodoPoJo(id, id, false));
        id = getId();
        twoElemList.add(new TodoPoJo(id, id, false));
        TodoStore.State emptyState = new TodoStore.State(false, emptyList, TodoStore.TodoFilter.ALL);
        TodoStore.State workingState = new TodoStore.State(true, emptyList, TodoStore.TodoFilter.ALL);
        TodoStore.State newTodoState = new TodoStore.State(false, oneElemList, TodoStore.TodoFilter.ALL);


        subscriber.assertValues(emptyState,
                workingState,
                newTodoState,
                new TodoStore.State(true, oneElemList, TodoStore.TodoFilter.ALL), //working
                new TodoStore.State(false, twoElemList, TodoStore.TodoFilter.ALL) //newlist
        );
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();
    }

    private String getId() {
        return idCounter++ + " Todo";
    }
}
