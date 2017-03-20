package com.fsh.poc.cfr.repos;

import java.util.List;

/**
 * Created by fshamim on 23/01/2017.
 */

public interface IEntityRepo<V> {

    void insert(V value);

    List<V> list();

    void clear();

    void delete(V value);

    V getById(long id);

    void update(V value);
}
