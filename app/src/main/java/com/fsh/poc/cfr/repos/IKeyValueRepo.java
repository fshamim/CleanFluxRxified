package com.fsh.poc.cfr.repos;

/**
 * Simple interface for a key value repository.
 * Created by fshamim on 14.08.15.
 */
public interface IKeyValueRepo {

    String name = "prefs";

    <T> T insert(String key, Object value);

    <T> T get(String key, T defaultValue);

    <T> T delete(String key);

    void clear();
}
