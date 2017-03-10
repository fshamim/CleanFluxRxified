package com.fsh.poc.cfr.todos.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fsh.poc.cfr.App;
import com.fsh.poc.cfr.R;
import com.fsh.poc.cfr.todos.TodoPoJo;
import com.fsh.poc.cfr.todos.TodoStore;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class TodoActivity extends AppCompatActivity {

    private static final String TAG = TodoActivity.class.getSimpleName();
    TodoStore store;
    RecyclerView rvTodos;
    TodoAdapter todoAdapter;
    DisposableSubscriber disposable;
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
                store.refreshTodos();

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onFabClick: ");
                store.insertTodo(new TodoPoJo(null, UUID.randomUUID().toString(), false));
            }
        });

        rvTodos = (RecyclerView) findViewById(R.id.rv_todo_items);
        rvTodos.setLayoutManager(new LinearLayoutManager(this));
        rvTodos.setItemAnimator(new DefaultItemAnimator());
        todoAdapter = new TodoAdapter(new ArrayList<TodoPoJo>(), store);
        rvTodos.setAdapter(todoAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Flowable<TodoStore.State> f = store.asFlowable();

        disposable = f.observeOn(AndroidSchedulers.mainThread())
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
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();

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
        final int id = item.getItemId();
        if (id == R.id.action_clear_all) {
            store.clearAllTodos();
            return true;
        } else if (id == R.id.action_clear_all_completed) {
            store.clearCompletedTodos();
            return true;
        } else if (id == R.id.menu_sort_by_all) {
            item.setChecked(!item.isChecked());
            store.applyFilter(TodoStore.TodoFilter.ALL);
            return true;
        } else if (id == R.id.menu_sort_by_completed) {
            item.setChecked(!item.isChecked());
            store.applyFilter(TodoStore.TodoFilter.COMPLETED);
            return true;
        } else if (id == R.id.menu_sort_by_incompleted) {
            item.setChecked(!item.isChecked());
            store.applyFilter(TodoStore.TodoFilter.INCOMPLETE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
