package com.fsh.poc.cfr.todos.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fsh.poc.cfr.App;
import com.fsh.poc.cfr.R;
import com.fsh.poc.cfr.todos.TodoPoJo;
import com.fsh.poc.cfr.todos.TodoStore;

import org.greenrobot.eventbus.EventBus;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.operators.flowable.FlowableOnBackpressureDrop;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DefaultSubscriber;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.SerializedSubscriber;

public class TodoActivity extends AppCompatActivity {

    private static final String TAG = TodoActivity.class.getSimpleName();
    TodoStore store;
    RecyclerView rvTodos;
    TodoAdapter todoAdapter;
    CompositeDisposable subs;
    SwipeRefreshLayout ptrLayout;
    TodoStore.TodoFilter filter = TodoStore.TodoFilter.ALL;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        App app = (App) getApplication();
        store = app.getUseCaseStore().getStore(TodoStore.class);


        ptrLayout = (SwipeRefreshLayout) findViewById(R.id.ptr_layout);
        ptrLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: ");
                EventBus.getDefault().post(new TodoStore.RefreshTodosAction());
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onFabClick: ");
                EventBus.getDefault().post(new TodoStore.AddTodoAction(UUID.randomUUID().toString()));
            }
        });

        rvTodos = (RecyclerView) findViewById(R.id.rv_todo_items);
        rvTodos.setLayoutManager(new LinearLayoutManager(this));
        rvTodos.setItemAnimator(new DefaultItemAnimator());
        todoAdapter = new TodoAdapter(new ArrayList<TodoPoJo>());
        rvTodos.setAdapter(todoAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        subs = new CompositeDisposable();
        Flowable<TodoStore.State> o = store.asFlowable();

        subs.add(o.observeOn(AndroidSchedulers.mainThread())
                .map(new Function<TodoStore.State, TodoStore.State>() {
                    @Override
                    public TodoStore.State apply(TodoStore.State state) throws Exception {
                        final boolean isProcessing = state.isProcessing;
                        ptrLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ptrLayout.setRefreshing(isProcessing);
                            }
                        }, 50);
                        filter = state.filter;
                        supportInvalidateOptionsMenu();
                        return state;
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Function<TodoStore.State, Pair<TodoStore.State, DiffUtil.DiffResult>>() {
                    @Override
                    public Pair<TodoStore.State, DiffUtil.DiffResult> apply(TodoStore.State state) throws Exception {
                        DiffUtil.DiffResult result = null;
                        final TodoAdapter.TodoDiffCallback callback = new TodoAdapter.TodoDiffCallback(todoAdapter.todos, state.todos);
                        result = DiffUtil.calculateDiff(callback);
                        todoAdapter.todos.clear();
                        todoAdapter.todos.addAll(state.todos);
                        return new Pair<>(state, result);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<Pair<TodoStore.State, DiffUtil.DiffResult>>() {

                    @Override
                    protected void onStart() {
                        request(1);
                    }

                    @Override
                    public void onNext(Pair<TodoStore.State, DiffUtil.DiffResult> pair) {
                        DiffUtil.DiffResult diffResult = pair.getValue1();
                        if (diffResult != null) {
                            diffResult.dispatchUpdatesTo(todoAdapter);
                        }
                        request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    @Override
    protected void onPause() {
        super.onPause();
        subs.dispose();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_sort_by_all).setChecked(filter == TodoStore.TodoFilter.ALL);
        menu.findItem(R.id.menu_sort_by_completed).setChecked(filter == TodoStore.TodoFilter.COMPLETED);
        menu.findItem(R.id.menu_sort_by_incompleted).setChecked(filter == TodoStore.TodoFilter.INCOMPLETE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_clear_all:
                EventBus.getDefault().post(new TodoStore.ClearAllAction());
                return true;
            case R.id.action_clear_all_completed:
                EventBus.getDefault().post(new TodoStore.ClearAllCompletedAction());
                return true;
            case R.id.menu_sort_by_all:
                item.setChecked(!item.isChecked());
                EventBus.getDefault().post(new TodoStore.ApplyFilterAction(TodoStore.TodoFilter.ALL));
                return true;
            case R.id.menu_sort_by_completed:
                item.setChecked(!item.isChecked());
                EventBus.getDefault().post(new TodoStore.ApplyFilterAction(TodoStore.TodoFilter.COMPLETED));
                return true;
            case R.id.menu_sort_by_incompleted:
                item.setChecked(!item.isChecked());
                EventBus.getDefault().post(new TodoStore.ApplyFilterAction(TodoStore.TodoFilter.INCOMPLETE));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }
}
