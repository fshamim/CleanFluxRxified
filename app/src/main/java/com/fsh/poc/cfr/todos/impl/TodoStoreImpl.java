package com.fsh.poc.cfr.todos.impl;

import com.fsh.poc.cfr.framework.IAction;
import com.fsh.poc.cfr.todos.TodoPoJo;
import com.fsh.poc.cfr.todos.TodoStore;
import com.jakewharton.rxrelay.BehaviorRelay;

import org.pcollections.TreePVector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.BlockingObservable;

/**
 * Created by fshamim on 27/11/2016.
 */

public class TodoStoreImpl implements TodoStore {

    BehaviorRelay<State> relay;
    TreePVector<TodoPoJo> todos;
    boolean isProcessing;
    TodoFilter filter;

    public TodoStoreImpl() {
        todos = TreePVector.empty();
        isProcessing = false;
        filter = TodoFilter.ALL;
        relay = BehaviorRelay.create(getState());
    }

    private State getState() {
        if (filter == TodoFilter.ALL) {
            return new State(isProcessing, TreePVector.from(todos), filter);
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
        Observable.from(todos)
                .filter(new Func1<TodoPoJo, Boolean>() {
                    @Override
                    public Boolean call(TodoPoJo todoPoJo) {
                        return todoPoJo.isCompleted();
                    }
                })
                .toList()
                .toBlocking()
                .subscribe(new Action1<List<TodoPoJo>>() {
                    @Override
                    public void call(List<TodoPoJo> todoPoJos) {
                        todos = todos.minusAll(todoPoJos);
                    }
                });
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
        relay.call(getState());
    }

    private void toggleTodo(final ToggleTodoAction action) {
        for (int i = 0; i < todos.size(); ++i) {
            TodoPoJo todo = todos.get(i);
            if (todo.getId().equals(action.todoId)) {
                TodoPoJo toggledTodo = new TodoPoJo(todo.getId(), todo.getText(), !todo.isCompleted());
                todos = (TreePVector<TodoPoJo>) todos.with(i, toggledTodo);
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
        if (action instanceof ToggleTodoAction) {
            toggleTodo((ToggleTodoAction) action);
        } else if (action instanceof AddTodoAction) {
            todos = todos.plus(new TodoPoJo(UUID.randomUUID().toString(), ((AddTodoAction) action).text, false));
        } else if (action instanceof UpdateTodoAction) {
            updateTodo((UpdateTodoAction) action);
        } else if (action instanceof ClearAllCompletedAction) {
            clearAllCompletedTodos((ClearAllCompletedAction) action);
        } else if (action instanceof ApplyFilterAction) {
            this.filter = ((ApplyFilterAction) action).filter;
        } else if (action instanceof ClearAllAction) {
            todos = TreePVector.empty();
        }
        updateAndPublishState(false);
    }

    @Override
    public Observable<State> asObservable() {
        return relay.asObservable();
    }
}
