package com.fsh.poc.cfr.framework.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fsh.poc.cfr.framework.IAction;
import com.fsh.poc.cfr.framework.IActionQueue;

import java.util.AbstractMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * An implementation of IEventQueueManager based on the mapdb
 * Created by fshamim on 22.08.15.
 */
public class ActionQueue implements IActionQueue {


    private static final java.lang.String TAG = ActionQueue.class.getSimpleName();
    private BlockingQueue<AbstractMap.SimpleImmutableEntry<String, IAction>> queue;

    /**
     * EventQueueManager depends upon the mapdb
     */
    public ActionQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    @Override
    public boolean addAction(@NonNull String processor, @NonNull IAction event) {
        AbstractMap.SimpleImmutableEntry<String, IAction> entry = new AbstractMap.SimpleImmutableEntry<>(processor, event);
        BlockingQueue<AbstractMap.SimpleImmutableEntry<String, IAction>> q = getQueue();
        boolean ret = q.add(entry);
        Log.d(TAG, "------------->Action Added: " + event + " -> " + processor);
        return ret;
    }

    @Override
    public AbstractMap.SimpleImmutableEntry<String, IAction> getAction() {
        BlockingQueue<AbstractMap.SimpleImmutableEntry<String, IAction>> q = getQueue();
        return q.peek();
    }

    @Override
    public AbstractMap.SimpleImmutableEntry<String, IAction> removeAction() {
        BlockingQueue<AbstractMap.SimpleImmutableEntry<String, IAction>> q = getQueue();
        AbstractMap.SimpleImmutableEntry<String, IAction> entry = q.poll();
        Log.d(TAG, "Action removed: " + entry.getValue());
        return entry;
    }

    @Override
    public void clear() {
        getQueue().clear();
    }

    private BlockingQueue<AbstractMap.SimpleImmutableEntry<String, IAction>> getQueue() {
        return this.queue;
    }
}
