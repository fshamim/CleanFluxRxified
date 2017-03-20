package com.fsh.poc.cfr.todos;

import com.fsh.poc.cfr.framework.IUseCase;
import com.fsh.poc.cfr.model.Todo;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by fshamim on 26/11/2016.
 */

public interface TodoUseCase extends IUseCase {

    Flowable<State> asFlowable();

    void insertTodo(Todo todo);

    void toggleTodo(Todo todo);

    void applyFilter(TodoFilter filter);

    void clearAllTodos();

    void clearCompletedTodos();

    void refreshTodos();

    enum TodoFilter {
        ALL, COMPLETED, INCOMPLETE
    }

    public static class State {
        public final boolean isProcessing;
        public final List<Todo> todos;
        public final TodoFilter filter;

        public State(boolean isProcessing, List<Todo> todos, TodoFilter filter) {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            State state = (State) o;

            if (isProcessing != state.isProcessing) return false;
            if (todos != null ? !todos.equals(state.todos) : state.todos != null) return false;
            return filter == state.filter;
        }

        @Override
        public int hashCode() {
            int result = (isProcessing ? 1 : 0);
            result = 31 * result + (todos != null ? todos.hashCode() : 0);
            result = 31 * result + (filter != null ? filter.hashCode() : 0);
            return result;
        }
    }
}
