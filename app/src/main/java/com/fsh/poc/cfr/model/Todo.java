package com.fsh.poc.cfr.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

/**
 * Created by fshamim on 23/01/2017.
 */

@AutoValue
public abstract class Todo implements TodoModel {
    public static final Factory<Todo> FACTORY = new Factory<>(new Creator<Todo>() {
        @Override
        public Todo create(long _id, @NonNull String text, @Nullable Boolean is_completed) {
            return new AutoValue_Todo(_id, text, is_completed);
        }
    });

    public static Todo create(long _id, @NonNull String text, @Nullable Boolean is_completed) {
        return FACTORY.creator.create(_id, text, is_completed);
    }
}
