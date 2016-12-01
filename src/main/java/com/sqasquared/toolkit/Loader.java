package com.sqasquared.toolkit;

/**
 * Created by JTran on 11/30/2016.
 */
public abstract class Loader<T>{
    protected ObjectManager<T> objectManager;

    public Loader() {
        objectManager = null;
    }

    public void setObjectManager(ObjectManager<T> objectManager) {
        this.objectManager = objectManager;
    }
}
