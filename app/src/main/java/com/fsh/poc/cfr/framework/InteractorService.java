package com.fsh.poc.cfr.framework;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.fsh.poc.cfr.App;

import java.util.AbstractMap;

public class InteractorService extends Service {
    public static final String TAG = InteractorService.class.getSimpleName();

    App app;
    IActionQueue queue;
    volatile boolean running;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        app = (App) getApplication();
        queue = app.getEventQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "<-------------onDestroy");
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
        AbstractMap.SimpleImmutableEntry<String, IAction> entry = queue.getAction();
        if (entry == null) {
            Log.d(TAG, "Stopping Service");
            stopSelf();
            return;
        }
        boolean executing = false;
        String key = entry.getKey();
        Log.d(TAG, "processing");
        IStore store = app.getUseCaseStore().getStore(key);
        store.processAction(entry.getValue());
        stopServiceIfFinished();
    }

    private void stopServiceIfFinished() {
        Log.d(TAG, "onStopServiceIfFinished: ");
        queue.removeAction();
        execute();
    }
}
