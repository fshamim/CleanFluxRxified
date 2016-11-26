package com.fsh.poc.cfr.framework.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fsh.poc.cfr.framework.IEventQueue;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * An implementation of IEventQueueManager based on the mapdb
 * Created by fshamim on 22.08.15.
 */
public class EventQueue implements IEventQueue {


    private static final java.lang.String TAG = EventQueue.class.getSimpleName();
    private BlockingQueue<AbstractMap.SimpleImmutableEntry<String, Serializable>> queue;

    /**
     * EventQueueManager depends upon the mapdb
     */
    public EventQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    @Override
    public boolean addEvent(@NonNull String processor, @NonNull Serializable event) {
        AbstractMap.SimpleImmutableEntry<String, Serializable> entry = new AbstractMap.SimpleImmutableEntry<>(processor, event);
        BlockingQueue<AbstractMap.SimpleImmutableEntry<String, Serializable>> q = getQueue();
        boolean ret = q.add(entry);
        Log.d(TAG, "------------->Event Added: " + event + " -> " + processor);
        return ret;
    }

    @Override
    public AbstractMap.SimpleImmutableEntry<String, Serializable> getEvent() {
        BlockingQueue<AbstractMap.SimpleImmutableEntry<String, Serializable>> q = getQueue();
        return q.peek();
    }

    @Override
    public AbstractMap.SimpleImmutableEntry<String, Serializable> removeEvent() {
        BlockingQueue<AbstractMap.SimpleImmutableEntry<String, Serializable>> q = getQueue();
        AbstractMap.SimpleImmutableEntry<String, Serializable> entry = q.poll();
        Log.d(TAG, "Event removed: " + entry.getValue());
        return entry;
    }

    @Override
    public void clear() {
        getQueue().clear();
    }

    private BlockingQueue<AbstractMap.SimpleImmutableEntry<String, Serializable>> getQueue() {
        return this.queue;
    }


}
