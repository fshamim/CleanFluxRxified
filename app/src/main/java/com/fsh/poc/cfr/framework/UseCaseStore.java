package com.fsh.poc.cfr.framework;


import java.util.HashMap;

/**
 * Created by fshamim on 10/10/2016.
 */

public class UseCaseStore {

    HashMap<String, IUseCase> map = new HashMap<>();

    public void registerStore(Class clazz, IUseCase store) {
        map.put(clazz.getCanonicalName(), store);
    }

    public <T> T getStore(String canonicalName) {
        IUseCase store = map.get(canonicalName);
        return (T) store;
    }

    public <T> T getStore(Class clazz) {
        IUseCase store = map.get(clazz.getCanonicalName());
        return (T) store;
    }
}
