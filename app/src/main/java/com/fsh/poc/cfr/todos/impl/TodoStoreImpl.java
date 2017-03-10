package com.fsh.poc.cfr.todos.impl;

import com.fsh.poc.cfr.RxBus;
import com.fsh.poc.cfr.todos.TodoPoJo;
import com.fsh.poc.cfr.todos.TodoStore;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by fshamim on 27/11/2016.
 */

public class TodoStoreImpl implements TodoStore {

    BehaviorProcessor<State> relay;
    List<TodoPoJo> todos;
    boolean isProcessing;
    TodoFilter filter;
    int counter = 0;
    RxBus localBus;

    public TodoStoreImpl() {
        localBus = new RxBus();
        todos = new ArrayList<>();
        isProcessing = false;
        filter = TodoFilter.ALL;
        relay = BehaviorProcessor.createDefault(getState());
        initBus();
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

    private void clearAllCompletedTodos() {
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

    private void publishState() {
        relay.onNext(getState());
    }

    private void updateAndPublishState(boolean isProcessing) {
        this.isProcessing = isProcessing;
        publishState();
    }

    private void initBus() {
        localBus.toFlowable()
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        updateAndPublishState(true);
                    }
                })
                .subscribeWith(new DisposableSubscriber<Object>() {
                    @Override
                    protected void onStart() {
                        request(1);
                    }

                    @Override
                    public void onNext(Object o) {
//                        int randomDelay = new Random().nextInt(1200);
//                        Observable.just(1).delay(randomDelay, TimeUnit.MILLISECONDS).blockingSubscribe();
                        if (o instanceof InserTodoEvent) {
                            TodoPoJo t = ((InserTodoEvent) o).todo;
                            todos.add(new TodoPoJo(counter++ + "", t.getText(), t.isCompleted()));
                        } else if (o instanceof ClearAllCompletedEvent) {
                            clearAllCompletedTodos();
                        } else if (o instanceof ClearAllEvent) {
                            todos.clear();
                        } else if (o instanceof ApplyFilterEvent) {
                            filter = ((ApplyFilterEvent) o).filter;
                        } else if (o instanceof ToggleTodoEvent) {
                            for (int i = 0; i < todos.size(); ++i) {
                                TodoPoJo todo = todos.get(i);
                                if (todo.getId().equals(((ToggleTodoEvent) o).todo.getId())) {
                                    TodoPoJo toggledTodo = new TodoPoJo(todo.getId(), todo.getText(), !todo.isCompleted());
                                    todos.set(i, toggledTodo);
                                    break;
                                }
                            }
                        }
                        updateAndPublishState(false);
                        request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public Flowable<State> asFlowable() {
        return relay;
    }

    @Override
    public void insertTodo(TodoPoJo todo) {
        localBus.send(new InserTodoEvent(todo));
    }

    @Override
    public void applyFilter(TodoFilter filter) {
        localBus.send(new ApplyFilterEvent(filter));
    }

    @Override
    public void clearAllTodos() {
        localBus.send(new ClearAllEvent());
    }

    @Override
    public void clearCompletedTodos() {
        localBus.send(new ClearAllCompletedEvent());
    }

    @Override
    public void refreshTodos() {
        localBus.send(new RefreshTodosEvent());
    }

    @Override
    public void toggleTodo(TodoPoJo todo) {
        localBus.send(new ToggleTodoEvent(todo));
    }

    private class InserTodoEvent {
        public final TodoPoJo todo;

        public InserTodoEvent(TodoPoJo todo) {
            this.todo = todo;
        }
    }

    private class ApplyFilterEvent {
        public final TodoFilter filter;

        public ApplyFilterEvent(TodoFilter filter) {
            this.filter = filter;
        }
    }

    private class ClearAllEvent {
    }

    private class ClearAllCompletedEvent {
    }

    private class RefreshTodosEvent {
    }

    private class ToggleTodoEvent {
        public final TodoPoJo todo;

        public ToggleTodoEvent(TodoPoJo todo) {
            this.todo = todo;
        }
    }
}
