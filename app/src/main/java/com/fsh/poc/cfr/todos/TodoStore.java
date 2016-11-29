package com.fsh.poc.cfr.todos;

import com.fsh.poc.cfr.framework.IAction;
import com.fsh.poc.cfr.framework.IStore;

import java.io.Serializable;
import java.util.List;

import rx.Observable;

/**
 * Created by fshamim on 26/11/2016.
 */

public interface TodoStore extends IStore {

    Observable<State> asObservable();

    enum TodoFilter {
        ALL, COMPLETED, INCOMPLETE
    }

    public static class State {
        public final boolean isProcessing;
        public final List<TodoPoJo> todos;
        public final TodoFilter filter;

        public State(boolean isProcessing, List<TodoPoJo> todos, TodoFilter filter) {
            this.isProcessing = isProcessing;
            this.todos = todos;
            this.filter = filter;
        }

        @Override
        public String toString() {
            return "State{" +
                    "isProcessing=" + isProcessing +
                    ", todos=" + todos +
                    ", filter=" + filter +
                    '}';
        }
    }

    public static class onNext implements Serializable {
        final String inputEventClassName;
        final State state;

        public onNext(String inputEventClassName, State state) {
            this.inputEventClassName = inputEventClassName;
            this.state = state;
        }
    }

    public static class onError extends onNext {
        final Throwable e;

        public onError(String inputEventClassName, State state, Throwable e) {
            super(inputEventClassName, state);
            this.e = e;
        }
    }

    public static abstract class TodoStoreAction implements IAction {
        @Override
        public Class getAssociatedStore() {
            return TodoStore.class;
        }
    }

    public static class ToggleTodoAction extends TodoStoreAction {
        public final String todoId;

        public ToggleTodoAction(String todoId) {
            this.todoId = todoId;
        }
    }

    public static class AddTodoAction extends TodoStoreAction {
        public final String text;

        public AddTodoAction(String text) {
            this.text = text;
        }
    }

    public static class UpdateTodoAction extends TodoStoreAction {
        public final TodoPoJo todo;

        public UpdateTodoAction(TodoPoJo todo) {
            this.todo = todo;
        }
    }

    public static class ApplyFilterAction extends TodoStoreAction {
        public final TodoFilter filter;

        public ApplyFilterAction(TodoFilter filter) {
            this.filter = filter;
        }
    }

    public static class ClearAllCompletedAction extends TodoStoreAction {

    }

    public static class ClearAllAction extends TodoStoreAction {

    }

    public static class RefreshTodosAction extends TodoStoreAction {

    }
}
