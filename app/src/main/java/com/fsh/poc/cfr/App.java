package com.fsh.poc.cfr;

import android.app.Application;
import android.content.Intent;

import com.fsh.poc.cfr.framework.Dispatcher;
import com.fsh.poc.cfr.framework.IActionQueue;
import com.fsh.poc.cfr.framework.SerialActionProcessingService;
import com.fsh.poc.cfr.framework.UseCaseStore;
import com.fsh.poc.cfr.framework.impl.ActionQueue;
import com.fsh.poc.cfr.todos.TodoStore;
import com.fsh.poc.cfr.todos.impl.TodoStoreImpl;

import org.greenrobot.eventbus.EventBus;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;

/**
 * Created by fshamim on 26/11/2016.
 */

public class App extends Application {

    private UseCaseStore useCaseStore;
    private Dispatcher dispatcher;
    private ActionQueue eventQueue;
    private DB db;

    @Override
    public void onCreate() {
        super.onCreate();
        getEventQueue();
        getDispatcher();
        getUseCaseStore();
        startService(new Intent(this, SerialActionProcessingService.class));
    }

    public UseCaseStore getUseCaseStore() {
        if (useCaseStore == null) {
            useCaseStore = new UseCaseStore();
            useCaseStore.registerStore(TodoStore.class, new TodoStoreImpl());
        }
        return useCaseStore;
    }

    public Dispatcher getDispatcher() {
        if (dispatcher == null) {
            dispatcher = new Dispatcher(this, EventBus.getDefault(), getEventQueue());
        }
        return dispatcher;
    }

    public IActionQueue getEventQueue() {
        if (eventQueue == null) {
            eventQueue = new ActionQueue();
        }
        return eventQueue;
    }

    public DB getDb() {
        if (db == null) {
            File dir = getFilesDir();
            File dbDir = new File(dir.getAbsolutePath() + "/db");
            dbDir.mkdir();
            File dbFile = new File(dir.getAbsolutePath() + "/db/tododb");
            db = DBMaker.fileDB(dbFile)
                    .fileMmapEnableIfSupported()
                    .make();
        }

        return db;
    }

}
