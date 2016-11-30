package com.fsh.poc.cfr.todos.ui;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.fsh.poc.cfr.R;
import com.fsh.poc.cfr.todos.TodoPoJo;
import com.fsh.poc.cfr.todos.TodoStore;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by fshamim on 28/11/2016.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoVH> {

    private static final String TAG = TodoAdapter.class.getSimpleName();
    List<TodoPoJo> todos;

    public TodoAdapter(List<TodoPoJo> todos) {
        this.todos = todos;
    }

    @Override
    public TodoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item_todo, parent, false);
        return new TodoVH(view);
    }

    @Override
    public void onBindViewHolder(TodoVH holder, int position) {
        final TodoPoJo todo = todos.get(position);
        holder.tvText.setText(todo.getText());
        holder.chkIsDone.setChecked(todo.isCompleted());
        holder.chkIsDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new TodoStore.ToggleTodoAction(todo.getId()));

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
        final List<TodoPoJo> oldList;
        final List<TodoPoJo> newList;

        public TodoDiffCallback(List<TodoPoJo> oldList, List<TodoPoJo> newList) {
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
            return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }

//        @Nullable
//        @Override
//        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
//            TodoPoJo oldTodo = oldList.get(oldItemPosition);
//            TodoPoJo newTodo = newList.get(oldItemPosition);
//            Bundle bundle = new Bundle();
//            if (!oldTodo.getText().equals(newTodo.getText())) {
//                bundle.putString(TODO_TEXT, newTodo.getText());
//            }
//
//            if (oldTodo.isCompleted() != newTodo.isCompleted()) {
//                bundle.putBoolean(TODO_IS_COMPLETED, newTodo.isCompleted());
//            }
//            if (bundle.size() == 0) bundle = null;
//            return bundle;
//        }
    }
}
