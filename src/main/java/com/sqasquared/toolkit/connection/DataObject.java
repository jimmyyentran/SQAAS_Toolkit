package com.sqasquared.toolkit.connection;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by jimmytran on 11/1/16.
 */
public class DataObject {

    HashMap<String, DataObject> children = new HashMap<String, DataObject>();
    DataObject parent;
    @SerializedName("WorkItemType")
    String type;
    @SerializedName("Id")
    String id;
    @SerializedName("Title")
    String name;

    public DataObject(String type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.children = new HashMap<String, DataObject>();
        this.parent = null;
    }

    public DataObject() {
        this.type = null;
        this.id = null;
        this.name = null;
        this.children = new HashMap<String, DataObject>();
        this.parent = null;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, DataObject> getChildren() {
        return children;
    }

    public void setChildren(HashMap<String, DataObject> children) {
        this.children = children;
    }

    public void addChild(DataObject... children) {
        for (DataObject child : children) {
            this.children.put(child.getId(), child);
            child.setParent(this);
        }
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public DataObject getParent() {
        return parent;
    }

    private void setParent(DataObject par) {
        this.parent = par;
    }

    public void clearChildren() {
        this.children.clear();
    }

    public void print() {
        print(4, 0);
    }

    public void print(int indentation, int relictIndentation) {
        String indent;
        try {
            indent = String.format("%" + relictIndentation + "s", "");
        } catch (Exception ex) {
            indent = "";
        }
        String toBePrinted = indent + "Type: " + type + ", ID: " + id + ", " +
                "Name: " + name;
        System.out.println(toBePrinted);
        for (DataObject obj : children.values()) {
//            System.out.println("obj = " + obj);
            obj.print(indentation, indentation + relictIndentation);
        }
    }
}
