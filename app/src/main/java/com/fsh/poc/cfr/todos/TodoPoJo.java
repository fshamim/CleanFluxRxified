package com.fsh.poc.cfr.todos;

/**
 * Created by fshamim on 26/11/2016.
 */
public class TodoPoJo {

    final private String id;
    final private String text;
    final private boolean isCompleted;

    public TodoPoJo(String id, String text, boolean isCompleted) {
        this.id = id;
        this.text = text;
        this.isCompleted = isCompleted;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoPoJo todoPoJo = (TodoPoJo) o;

        if (isCompleted != todoPoJo.isCompleted) return false;
        if (id != null ? !id.equals(todoPoJo.id) : todoPoJo.id != null) return false;
        return text != null ? text.equals(todoPoJo.text) : todoPoJo.text == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (isCompleted ? 1 : 0);
        return result;
    }
}
