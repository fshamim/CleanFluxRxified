package com.fsh.poc.cfr.todos;

import com.fsh.poc.cfr.framework.IStore;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fshamim on 26/11/2016.
 */

public interface TodoStore extends IStore {

    enum TodoFilter {
        ALL, COMPLETED, NOT_COMPLETED
    }

    public static class State {
        final boolean isProcessing;
        final List<TodoPoJo> todos;
        final TodoFilter filter;

        public State(boolean isProcessing, List<TodoPoJo> todos, TodoFilter filter) {
            this.isProcessing = isProcessing;
            this.todos = todos;
            this.filter = filter;
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

    public static class ToggleTodoAction implements Serializable {
        public final String todoId;

        public ToggleTodoAction(String todoId) {
            this.todoId = todoId;
        }
    }

    public static class AddTodoAction implements Serializable {
        public final String text;

        public AddTodoAction(String text) {
            this.text = text;
        }
    }

    public static class UpdateTodoAction implements Serializable {
        public final TodoPoJo todo;

        public UpdateTodoAction(TodoPoJo todo) {
            this.todo = todo;
        }
    }

    public static class ApplyFilterAction implements Serializable {
        public final TodoFilter filter;

        public ApplyFilterAction(TodoFilter filter) {
            this.filter = filter;
        }
    }

    public static class ClearAllCompletedAction implements Serializable {

    }
}
