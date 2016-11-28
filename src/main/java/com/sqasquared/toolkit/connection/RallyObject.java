package com.sqasquared.toolkit.connection;

import java.util.HashMap;

/**
 * Created by jimmytran on 11/1/16.
 */
public class RallyObject {
    public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String COMPLETED = "Completed";
    public static final String DEFINED = "Defined";
    public static final String INPROGRESS = "In-Progress";


    private final HashMap<String, RallyObject> children;
    private RallyObject parent;
    final String type;
    final String id;
    final String name;

    public RallyObject(String type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.children = new HashMap<String, RallyObject>();
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

    public HashMap<String, RallyObject> getChildren() {
        return children;
    }

    public void addChild(RallyObject... children) {
        for (RallyObject child : children) {
            this.children.put(child.getId(), child);
            child.setParent(this);
        }
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    private void setParent(RallyObject par) {
        this.parent = par;
    }

    public RallyObject getParent() {
        return parent;
    }

    public void clearChildren() {
        this.children.clear();
    }

    public void print(int indentation) {
        print(indentation, 0);
    }

    void print(int indentation, int relictIndentation) {
        String indent;
        try {
            indent = String.format("%" + relictIndentation + "s", "");
        } catch (Exception ex) {
            indent = "";
        }
        String toBePrinted = indent + "Type: " + type + ", ID: " + id + ", Name: " + name;
        System.out.println(toBePrinted);
        for (RallyObject obj : children.values()) {
            obj.print(indentation, indentation + relictIndentation);
        }
    }
}
