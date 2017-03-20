package com.fsh.poc.cfr;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.fsh.poc.cfr.framework.UseCaseStore;
import com.fsh.poc.cfr.model.Todo;
import com.fsh.poc.cfr.repos.IEntityRepo;
import com.fsh.poc.cfr.repos.todorepo.DbOpenHelper;
import com.fsh.poc.cfr.repos.todorepo.TodoRepoSQL;
import com.fsh.poc.cfr.todos.TodoUseCase;
import com.fsh.poc.cfr.todos.impl.TodoUseCaseImpl;

/**
 * Created by fshamim on 26/11/2016.
 */

public class App extends Application {

    private UseCaseStore useCaseStore;
    private TodoRepoSQL todoRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        getUseCaseStore();
    }

    public UseCaseStore getUseCaseStore() {
        if (useCaseStore == null) {
            useCaseStore = new UseCaseStore();
            useCaseStore.registerStore(TodoUseCase.class, new TodoUseCaseImpl(getTodoRepo()));
        }
        return useCaseStore;
    }

    public IEntityRepo<Todo> getTodoRepo() {
        if (todoRepo == null) {
            todoRepo = new TodoRepoSQL(DbOpenHelper.getInstance(this));
        }
        return todoRepo;
    }
}
