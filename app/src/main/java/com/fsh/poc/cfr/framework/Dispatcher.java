package com.fsh.poc.cfr.framework;

import android.content.Intent;

import com.fsh.poc.cfr.App;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by fshamim on 10/10/2016.
 */

public class Dispatcher {

    private final App app;
    private final IEventQueue queue;

    public Dispatcher(App app, EventBus eventBus, IEventQueue queue) {
        this.app = app;
        this.queue = queue;
        eventBus.register(this);
    }


    /**
     * Notify the service hosting the interactors so that they start working by taking the events
     * from the queue
     */
    private void notifyService() {
        Intent intent = new Intent(app, InteractorService.class);
        app.startService(intent);
    }

}
