package com.fsh.poc.cfr;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by fshamim on 10/03/2017.
 */

public class RxBus {
    private PublishProcessor<Object> bus = PublishProcessor.create();

    public RxBus() {

    }

    public void send(Object event) {
        bus.onNext(event);
    }

    public Flowable<Object> toFlowable() {
        return bus;
    }
}
