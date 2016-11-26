package com.fsh.poc.cfr;

import android.app.Application;

import com.fsh.poc.cfr.framework.Dispatcher;
import com.fsh.poc.cfr.framework.IEventQueue;
import com.fsh.poc.cfr.framework.UseCaseStore;
import com.fsh.poc.cfr.framework.impl.EventQueue;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by fshamim on 26/11/2016.
 */

public class App extends Application {

    private UseCaseStore useCaseStore;
    private Dispatcher dispatcher;
    private EventQueue eventQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        getEventQueue();
        getDispatcher();
        getUseCaseStore();
    }

    public UseCaseStore getUseCaseStore() {
        if (useCaseStore == null) {
            useCaseStore = new UseCaseStore();
        }
        return useCaseStore;
    }

    public Dispatcher getDispatcher() {
        if (dispatcher == null) {
            dispatcher = new Dispatcher(this, EventBus.getDefault(), getEventQueue());
        }
        return dispatcher;
    }

    public IEventQueue getEventQueue() {
        if (eventQueue == null) {
            eventQueue = new EventQueue();
        }
        return eventQueue;
    }
}
