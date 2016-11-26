package com.fsh.poc.cfr.framework;

import java.io.Serializable;
import java.util.AbstractMap;

/**
 * A simplified interface for a queue manager that allows adding, retrieving and removing events
 * from FIFO queues
 * Created by fshamim on 22.08.15.
 */
public interface IEventQueue {

    /**
     * Add an event to the processor
     *
     * @param event to be added in the processor
     * @return true if added false otherwise.
     */
    boolean addEvent(String processor, Serializable event);

    /**
     * Retrieve an event from the processor
     *
     * @return top of the processor
     */
    AbstractMap.SimpleImmutableEntry<String, Serializable> getEvent();

    /**
     * Remove an event from the processor
     *
     * @return removed event
     */
    AbstractMap.SimpleImmutableEntry<String, Serializable> removeEvent();

    void clear();
}
