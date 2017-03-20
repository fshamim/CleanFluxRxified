package com.fsh.poc.cfr.todos.ui;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fsh.poc.cfr.R;
import com.fsh.poc.cfr.model.Todo;
import com.fsh.poc.cfr.todos.TodoUseCase;

import java.util.List;

/**
 * Created by fshamim on 28/11/2016.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoVH> {

    private static final String TAG = TodoAdapter.class.getSimpleName();
    private final TodoUseCase store;
    List<Todo> todos;

    public TodoAdapter(List<Todo> todos, TodoUseCase store) {
        this.todos = todos;
        this.store = store;
    }

    @Override
    public TodoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item_todo, parent, false);
        return new TodoVH(view);
    }

    @Override
    public void onBindViewHolder(final TodoVH holder, int position) {
        final Todo todo = todos.get(position);
        holder.tvText.setText(todo.text());
        holder.chkIsDone.setChecked(todo.is_completed());
        holder.chkIsDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                store.toggleTodo(todo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }


    public static class TodoVH extends RecyclerView.ViewHolder {

        TextView tvText;
        CheckBox chkIsDone;

        public TodoVH(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tv_text);
            chkIsDone = (CheckBox) itemView.findViewById(R.id.chk_is_done);
        }
    }

    public static class TodoDiffCallback extends DiffUtil.Callback {

        public static final String TODO_TEXT = "TODO_TEXT";
        public static final String TODO_IS_COMPLETED = "TODO_IS_COMPLETED";
        final List<Todo> oldList;
        final List<Todo> newList;

        public TodoDiffCallback(List<Todo> oldList, List<Todo> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }


        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition)._id() == newList.get(newItemPosition)._id();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }
}
