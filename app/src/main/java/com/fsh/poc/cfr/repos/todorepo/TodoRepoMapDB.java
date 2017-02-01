package com.fsh.poc.cfr.repos.todorepo;

import com.fsh.poc.cfr.repos.IEntityRepo;
import com.fsh.poc.cfr.todos.TodoPoJo;

import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.util.List;
import java.util.Set;

/**
 * Created by fshamim on 08/12/2016.
 */

public class TodoRepoMapDB implements IEntityRepo<String, TodoPoJo> {

    private final DB db;
    private final String name = TodoRepoMapDB.class.getSimpleName();
    private final String mapName = name + "_map";
    private final String idName = name + "_id";

    public TodoRepoMapDB(DB db) {
        this.db = db;
        this.db.hashMap(mapName)
                .counterEnable()
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.ELSA)
                .createOrOpen();
        this.db.atomicLong(idName).createOrOpen();
    }

    HTreeMap<String, TodoPoJo> map() {
        return (HTreeMap<String, TodoPoJo>) db.hashMap(mapName).open();
    }

    String id() {
        return db.atomicLong(idName).open().getAndIncrement() + "";
    }

    @Override
    public TodoPoJo insert(TodoPoJo entity) {
        TodoPoJo ret = null;

        HTreeMap<String, TodoPoJo> map = map();
        if (entity.getId() == null) {
            entity = new TodoPoJo(id(), entity.getText(), entity.isCompleted());
        }
        if (entity.getId() != null && !map.containsKey(entity.getId())) {
            map.put(entity.getId(), entity);
            ret = entity;
        }
        return ret;
    }

    @Override
    public TodoPoJo update(TodoPoJo entity) {
        return null;
    }

    @Override
    public TodoPoJo delete(String key) {
        return null;
    }

    @Override
    public TodoPoJo get(String key) {
        return map().get(key);
    }

    @Override
    public List<TodoPoJo> list() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<String> keys() {
        return null;
    }

    @Override
    public void clear() {

    }
}
