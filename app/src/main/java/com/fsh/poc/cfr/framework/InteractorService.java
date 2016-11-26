package com.fsh.poc.cfr.framework;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


import com.fsh.poc.cfr.App;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.AbstractMap;

import rx.subscriptions.CompositeSubscription;

public class InteractorService extends Service {
    public static final String TAG = InteractorService.class.getSimpleName();

    App app;
    private CompositeSubscription s;
    private IEventQueue queue;
    private EventBus eventBus;
    private volatile boolean running;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        app = (App) getApplication();
        queue = app.getEventQueue();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "<-------------onDestroy");
        eventBus.unregister(this);
        running = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand");
        if (!running) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    running = true;
                    execute();
                }
            }).start();

        } else {
            Log.d(TAG, "Service already running");
        }

        return START_STICKY;
    }

    private void execute() {
        Log.d(TAG, "onExecute");
        AbstractMap.SimpleImmutableEntry<String, Serializable> entry = queue.getEvent();
        if (entry == null) {
            Log.d(TAG, "Stopping Service");
            stopSelf();
            return;
        }
        boolean executing = false;
        String key = entry.getKey();
        Log.d(TAG, "processing");
//        if (key.equals(TaskStore.class.getSimpleName())) {
//            executing = true;
//            TaskStore interactor = app.getUseCaseStore().getTaskStore();
//            interactor.processEvent(entry.getValue());
//            eventBus.post(new TryFinishServiceEvent());
//        }
        if (!executing) {
            Log.d(TAG, "no further event for processing, finishing...");
            stopSelf();
        }
    }

    @Subscribe
    public void onEvent(TryFinishServiceEvent event) {
        Log.d(TAG, "onTryFinishServiceEvent: ");
        stopServiceIfFinished();
    }

    private void stopServiceIfFinished() {
        Log.d(TAG, "onStopServiceIfFinished: ");
        queue.removeEvent();
        execute();
    }

    private class TryFinishServiceEvent {

    }
}
