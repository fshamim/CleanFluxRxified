package com.fsh.poc.cfr.todos.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.UUID;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class TodoActivity extends AppCompatActivity {

    private static final String TAG = TodoActivity.class.getSimpleName();
    TodoStore store;
    RecyclerView rvTodos;
    TodoAdapter todoAdapter;
    CompositeSubscription subs;
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
        subs = new CompositeSubscription();
        Observable<TodoStore.State> o = store.asObservable();
        subs.add(o
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TodoStore.State>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(final TodoStore.State state) {
                        Log.d(TAG, "onNext: ");
                        final boolean isProcessing = state.isProcessing;
                        ptrLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ptrLayout.setRefreshing(isProcessing);
                            }
                        }, 50);
                        if (!isProcessing) {
                            todoAdapter.updateTodos(state.todos);
                            filter = state.filter;
                            if (menu != null) {
                                menu.findItem(R.id.menu_sort_by_all).setChecked(filter.equals(TodoStore.TodoFilter.ALL));
                                menu.findItem(R.id.menu_sort_by_completed).setChecked(filter.equals(TodoStore.TodoFilter.COMPLETED));
                                menu.findItem(R.id.menu_sort_by_incompleted).setChecked(filter.equals(TodoStore.TodoFilter.INCOMPLETE));
                            }
                        }
                    }
                }));

    }

    @Override
    protected void onPause() {
        super.onPause();
        subs.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        this.menu = menu;
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
}
