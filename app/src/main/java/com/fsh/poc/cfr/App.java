package com.fsh.poc.cfr;

import android.app.Application;

import com.fsh.poc.cfr.framework.UseCaseStore;
import com.fsh.poc.cfr.todos.TodoStore;
import com.fsh.poc.cfr.todos.impl.TodoStoreImpl;

/**
 * Created by fshamim on 26/11/2016.
 */

public class App extends Application {

    private UseCaseStore useCaseStore;

    @Override
    public void onCreate() {
        super.onCreate();
        getUseCaseStore();
    }

    public UseCaseStore getUseCaseStore() {
        if (useCaseStore == null) {
            useCaseStore = new UseCaseStore();
            useCaseStore.registerStore(TodoStore.class, new TodoStoreImpl());
        }
        return useCaseStore;
    }
}
