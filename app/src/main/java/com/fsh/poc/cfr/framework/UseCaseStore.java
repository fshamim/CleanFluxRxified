package com.fsh.poc.cfr.framework;


import java.util.HashMap;

/**
 * Created by fshamim on 10/10/2016.
 */

public class UseCaseStore {

    HashMap<String, IStore> map = new HashMap<>();

    public void registerStore(Class clazz, IStore store) {
        map.put(clazz.getCanonicalName(), store);
    }

    public <T> T getStore(String canonicalName) {
        IStore store = map.get(canonicalName);
        return (T) store;
    }

    public <T> T getStore(Class clazz) {
        IStore store = map.get(clazz.getCanonicalName());
        return (T) store;
    }
}
