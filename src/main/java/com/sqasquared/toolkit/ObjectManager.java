package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.DataObject;

import java.util.HashMap;

/**
 * Created by jimmytran on 11/29/16.
 */
public abstract class ObjectManager {
    protected HashMap<String, DataObject> objectContainer;
    protected DataObject topNode;

    public ObjectManager(){
        objectContainer = new HashMap<>();
        topNode = null;
    }

    public HashMap<String, DataObject> getObjectContainer() {
        return objectContainer;
    }

    public DataObject getTopNode() {
        return topNode;
    }

    public void clearObjectContainer(){
        objectContainer.clear();
    }

    public abstract void addObject(DataObject task);
}
