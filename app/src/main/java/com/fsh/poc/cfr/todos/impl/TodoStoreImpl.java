package com.fsh.poc.cfr.todos.impl;

import com.fsh.poc.cfr.framework.IAction;
import com.fsh.poc.cfr.todos.TodoPoJo;
import com.fsh.poc.cfr.todos.TodoStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.BehaviorProcessor;

/**
 * Created by fshamim on 27/11/2016.
 */

public class TodoStoreImpl implements TodoStore {

    BehaviorProcessor<State> relay;
    List<TodoPoJo> todos;
    boolean isProcessing;
    TodoFilter filter;
    int counter = 0;

    public TodoStoreImpl() {
        todos = new ArrayList<>();
        isProcessing = false;
        filter = TodoFilter.ALL;
        relay = BehaviorProcessor.createDefault(getState());
    }

    private State getState() {
        if (filter == TodoFilter.ALL) {
            List<TodoPoJo> copy = new ArrayList<>();
            copy.addAll(todos);
            return new State(isProcessing, copy, filter);
        } else {
            List<TodoPoJo> filteredList = new ArrayList<>();
            for (int i = 0; i < todos.size(); ++i) {
                TodoPoJo todo = todos.get(i);
                if (filter == TodoFilter.COMPLETED && todo.isCompleted()) {
                    filteredList.add(todo);
                } else if (filter == TodoFilter.INCOMPLETE && !todo.isCompleted()) {
                    filteredList.add(todo);
                }
            }
            return new State(isProcessing, filteredList, filter);
        }
    }


    private void clearAllCompletedTodos(ClearAllCompletedAction action) {
        List<TodoPoJo> completedTodos = Observable.fromIterable(todos)
                .filter(new Predicate<TodoPoJo>() {
                    @Override
                    public boolean test(TodoPoJo todoPoJo) throws Exception {
                        return todoPoJo.isCompleted();
                    }
                })
                .toList()
                .blockingGet();
        todos.removeAll(completedTodos);
    }

    private void updateTodo(UpdateTodoAction action) {
        for (int i = 0; i < todos.size(); ++i) {
            TodoPoJo todo = todos.get(i);
            if (todo.getId().equals(action.todo.getId())) {
                TodoPoJo updatedTodo = new TodoPoJo(todo.getId(), todo.getText(), todo.isCompleted());
                todos.set(i, updatedTodo);
            }
        }
    }


    private void publishState() {
        relay.onNext(getState());
    }

    private void toggleTodo(final ToggleTodoAction action) {
        for (int i = 0; i < todos.size(); ++i) {
            TodoPoJo todo = todos.get(i);
            if (todo.getId().equals(action.todoId)) {
                TodoPoJo toggledTodo = new TodoPoJo(todo.getId(), todo.getText(), !todo.isCompleted());
                todos.set(i, toggledTodo);
                break;
            }
        }
    }

    private void updateAndPublishState(boolean isProcessing) {
        this.isProcessing = isProcessing;
        publishState();
    }

    @Override
    public void processAction(IAction action) {
        updateAndPublishState(true);
        //simulate work
        int randomDelay = new Random().nextInt(10);
        Observable.just(1).delay(randomDelay, TimeUnit.SECONDS).blockingSubscribe();
        if (action instanceof ToggleTodoAction) {
            toggleTodo((ToggleTodoAction) action);
        } else if (action instanceof AddTodoAction) {
            String id = counter++ + " Todo";
            todos.add(new TodoPoJo(id, id, false));
        } else if (action instanceof UpdateTodoAction) {
            updateTodo((UpdateTodoAction) action);
        } else if (action instanceof ClearAllCompletedAction) {
            clearAllCompletedTodos((ClearAllCompletedAction) action);
        } else if (action instanceof ApplyFilterAction) {
            this.filter = ((ApplyFilterAction) action).filter;
        } else if (action instanceof ClearAllAction) {
            todos.clear();
        }
        updateAndPublishState(false);
    }

    @Override
    public Flowable<State> asFlowable() {
        return relay;
    }
}
