package com.fsh.poc.cfr.framework;

import android.content.Intent;
import android.util.Log;

import com.fsh.poc.cfr.App;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by fshamim on 10/10/2016.
 */

public class Dispatcher {

    static final String TAG = Dispatcher.class.getSimpleName();
    final App app;
    final IActionQueue queue;

    public Dispatcher(App app, EventBus eventBus, IActionQueue queue) {
        this.app = app;
        this.queue = queue;
        eventBus.register(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(IAction action) {
        Log.d(TAG, "onEvent:" + action);
        this.queue.addAction(action.getAssociatedStore().getCanonicalName(), action);
        notifyService();
    }

    /**
     * Notify the service hosting the interactors so that they start working by taking the events
     * from the queue
     */
    private void notifyService() {
        Intent intent = new Intent(app, SerialActionProcessingService.class);
        app.startService(intent);
    }

}
