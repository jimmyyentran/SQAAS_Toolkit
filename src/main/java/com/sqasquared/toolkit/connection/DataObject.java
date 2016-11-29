package com.sqasquared.toolkit.connection;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by jimmytran on 11/1/16.
 */
public class DataObject {
    public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String COMPLETED = "Completed";
    public static final String DEFINED = "Defined";
    public static final String INPROGRESS = "In-Progress";


    HashMap<String, DataObject> children = new HashMap<String, DataObject>();
    DataObject parent;
//    String type;
//    String id;
//    String name;
    @SerializedName("WorkItemType") String type;
    @SerializedName("Id") String id;
    @SerializedName("Title") String name;

    public DataObject(String type, String id, String name) {
        System.out.println("OBJECT CREATED");
        this.type = type;
        this.id = id;
        this.name = name;
        this.children = new HashMap<String, DataObject>();
        this.parent = null;
    }

    public DataObject(){
        this.type = "";
        this.id = "";
        this.name = "";
        this.children = new HashMap<String, DataObject>();
        this.parent = null;
    }

    public String getType() {
        return type;
    }

    private String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, DataObject> getChildren() {
        return children;
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

    private void setParent(DataObject par) {
        this.parent = par;
    }

    public DataObject getParent() {
        return parent;
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
        String toBePrinted = indent + "Type: " + type + ", ID: " + id + ", Name: " + name;
        System.out.println(toBePrinted);
        for (DataObject obj : children.values()) {
            System.out.println("obj = " + obj);
            obj.print(indentation, indentation + relictIndentation);
        }
    }
}
