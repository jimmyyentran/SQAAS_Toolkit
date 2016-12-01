package com.sqasquared.toolkit;

import java.util.HashMap;

/**
 * Created by jimmytran on 11/29/16.
 */
public abstract class ObjectManager<T> implements ContainerInterface<T> {
    protected HashMap<String, T> objectContainer;

    public ObjectManager() {
        objectContainer = new HashMap<>();
    }

    public HashMap<String, T> getObjectContainer() {
        return objectContainer;
    }

    public void setObjectContainer(HashMap<String, T> objectContainer) {
        this.objectContainer = objectContainer;
    }

    public void clearObjectContainer() {
        objectContainer.clear();
    }

    public abstract void add(T item);
}
