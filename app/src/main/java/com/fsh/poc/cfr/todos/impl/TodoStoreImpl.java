package com.fsh.poc.cfr.todos.impl;

import com.fsh.poc.cfr.todos.TodoPoJo;
import com.fsh.poc.cfr.todos.TodoStore;
import com.jakewharton.rxrelay.BehaviorRelay;

import org.pcollections.TreePVector;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.BlockingObservable;

/**
 * Created by fshamim on 27/11/2016.
 */

public class TodoStoreImpl implements TodoStore {

    Set<String> actions;
    BehaviorRelay<State> relay;
    TreePVector<TodoPoJo> todos;
    boolean isProcessing;
    TodoFilter filter;

    public TodoStoreImpl() {
        relay = BehaviorRelay.create(getState());
        todos = TreePVector.empty();
        isProcessing = false;
        filter = TodoFilter.ALL;

    }

    private State getState() {
        Observable.from(todos)
                .filter(new Func1<TodoPoJo, Boolean>() {
                    @Override
                    public Boolean call(TodoPoJo todoPoJo) {
                        switch (filter) {
                            case COMPLETED:
                                return todoPoJo.isCompleted();
                            case NOT_COMPLETED:
                                return !todoPoJo.isCompleted();
                            case ALL:
                            default:
                                return true;
                        }
                    }
                })
                .toList()
                .toBlocking()
                .subscribe(new Action1<List<TodoPoJo>>() {
                    @Override
                    public void call(List<TodoPoJo> todoPoJos) {
                        todos = TreePVector.from(todoPoJos);
                    }
                });
        return new State(isProcessing, TreePVector.from(todos), filter);
    }

    @Override
    public Set<String> getActions() {
        if (actions == null) {
            actions = new HashSet<>();
            actions.add(ToggleTodoAction.class.getName());
            actions.add(AddTodoAction.class.getName());
            actions.add(UpdateTodoAction.class.getName());
            actions.add(ClearAllCompletedAction.class.getName());
            actions.add(ApplyFilterAction.class.getName());
        }
        return null;
    }

    @Override
    public void processAction(Serializable action) {
        if (action instanceof ToggleTodoAction) {
            toggleTodo((ToggleTodoAction) action);
        } else if (action instanceof AddTodoAction) {
            addTodo((AddTodoAction) action);
        } else if (action instanceof UpdateTodoAction) {
            updateTodo((UpdateTodoAction) action);
        } else if (action instanceof ClearAllCompletedAction) {
            clearAllCompletedTodos((ClearAllCompletedAction) action);
        }else if (action instanceof ApplyFilterAction){
            applyFilter((ApplyFilterAction) action);
        }

    }

    private void applyFilter(ApplyFilterAction action) {
        this.filter = action.filter;
        publishState();
    }

    private void clearAllCompletedTodos(ClearAllCompletedAction action) {
        updateAndPublishState(true);
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
        updateAndPublishState(false);

    }

    private void updateTodo(UpdateTodoAction action) {
        updateAndPublishState(true);
        for (int i = 0; i < todos.size(); ++i) {
            TodoPoJo todo = todos.get(i);
            if (todo.getId().equals(action.todo.getId())) {
                TodoPoJo updatedTodo = new TodoPoJo(todo.getId(), todo.getText(), todo.isCompleted());
                todos.set(i, updatedTodo);
            }
        }
        updateAndPublishState(false);
    }

    private void addTodo(AddTodoAction action) {
        updateAndPublishState(true);
        todos.add(new TodoPoJo(UUID.randomUUID().toString(), action.text, false));
        updateAndPublishState(false);
    }


    private void publishState() {
        relay.call(getState());
    }

    private void toggleTodo(final ToggleTodoAction action) {
        updateAndPublishState(true);
        for (int i = 0; i < todos.size(); ++i) {
            TodoPoJo todo = todos.get(i);
            if (todo.getId().equals(action.todoId)) {
                TodoPoJo toggledTodo = new TodoPoJo(todo.getId(), todo.getText(), !todo.isCompleted());
                todos.set(i, toggledTodo);
            }
        }
        updateAndPublishState(false);
    }

    private void updateAndPublishState(boolean isProcessing) {
        this.isProcessing = isProcessing;
        publishState();
    }
}
