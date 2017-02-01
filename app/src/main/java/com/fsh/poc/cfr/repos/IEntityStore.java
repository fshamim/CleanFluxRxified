package com.fsh.poc.cfr.repos;

import java.util.List;

/**
 * Created by fshamim on 23/01/2017.
 */

public interface IEntityStore<V> {

    void insert(V value);

    List<V> list();
}
