package com.fsh.poc.cfr.todos.impl;

import com.fsh.poc.cfr.RxBus;
import com.fsh.poc.cfr.model.Todo;
import com.fsh.poc.cfr.repos.IEntityRepo;
import com.fsh.poc.cfr.todos.TodoUseCase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by fshamim on 27/11/2016.
 */

public class TodoUseCaseImpl implements TodoUseCase {

    private final IEntityRepo<Todo> todoStore;
    BehaviorProcessor<State> relay;
    boolean isProcessing;
    TodoFilter filter;
    RxBus localBus;

    public TodoUseCaseImpl(IEntityRepo<Todo> todoStore) {
        this.todoStore = todoStore;
        localBus = new RxBus();
        isProcessing = false;
        filter = TodoFilter.ALL;
        relay = BehaviorProcessor.createDefault(getState());
        initBus();
    }

    private State getState() {
        if (filter == TodoFilter.ALL) {
            return new State(isProcessing, new ArrayList<>(todoStore.list()), filter);
        } else {
            List<Todo> filteredList = new ArrayList<>();
            List<Todo> todos = todoStore.list();
            for (int i = 0; i < todos.size(); ++i) {
                Todo todo = todos.get(i);
                if (filter == TodoFilter.COMPLETED && todo.is_completed()) {
                    filteredList.add(todo);
                } else if (filter == TodoFilter.INCOMPLETE && !todo.is_completed()) {
                    filteredList.add(todo);
                }
            }
            return new State(isProcessing, filteredList, filter);
        }
    }

    private void clearAllCompletedTodos() {
        for (Todo todo : todoStore.list()) {
            if (todo.is_completed()) {
                todoStore.delete(todo);
            }
        }
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
                            Todo t = ((InserTodoEvent) o).todo;
                            todoStore.insert(Todo.create(-1, t.text(), t.is_completed()));
                        } else if (o instanceof ClearAllCompletedEvent) {
                            clearAllCompletedTodos();
                        } else if (o instanceof ClearAllEvent) {
                            todoStore.clear();
                        } else if (o instanceof ApplyFilterEvent) {
                            filter = ((ApplyFilterEvent) o).filter;
                        } else if (o instanceof ToggleTodoEvent) {
                            long id = ((ToggleTodoEvent) o).todo._id();
                            Todo todo = todoStore.getById(id);
                            todoStore.update(Todo.create(todo._id(), todo.text(), !todo.is_completed()));
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
    public void insertTodo(Todo todo) {
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
    public void toggleTodo(Todo todo) {
        localBus.send(new ToggleTodoEvent(todo));
    }

    private class InserTodoEvent {
        public final Todo todo;

        public InserTodoEvent(Todo todo) {
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
        public final Todo todo;

        public ToggleTodoEvent(Todo todo) {
            this.todo = todo;
        }
    }
}
