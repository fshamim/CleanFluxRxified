package com.fsh.poc.cfr.repos;

import java.util.List;
import java.util.Set;

/**
 * Created by fshamim on 15.08.15.
 */
public interface IEntityRepo<K, V> {

    V insert(V entity);

    V update(V entity);

    V delete(K key);

    V get(K key);

    List<V> list();

    int size();

    Set<K> keys();


    void clear();
}
