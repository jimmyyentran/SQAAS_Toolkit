package com.sqasquared.toolkit.rally;

import sun.reflect.annotation.ExceptionProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jimmytran on 11/1/16.
 */
public class RallyObject {
    //    private ArrayList<RallyObject> children;
    public static String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private HashMap<String, RallyObject> children;
    private RallyObject parent;
    protected String type;
    protected String id;
    protected String name;

    public RallyObject(String type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
//        this.children = new ArrayList<RallyObject>();
        this.children = new HashMap<String, RallyObject>();
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

    public HashMap<String, RallyObject> getChildren() {
        return children;
    }

    public void addChild(RallyObject... children){
        for(RallyObject child: children) {
            this.children.put(child.getId(), child);
            child.setParent(this);
        }
    }

    public void setParent(RallyObject par){
        this.parent = par;
    }

    public RallyObject getParent(){
        return parent;
    }

    public void clearChildren(){
        this.children.clear();
    }

    public void print(int indentation){
        print(indentation, 0);
    }

    public void print(int indentation, int relictIndentation){
        String indent;
        try {
            indent = String.format("%" + relictIndentation + "s", "");
        }catch (Exception ex){
            indent = "";
        }
        String toBePrinted = indent + "Type: " + type + ", ID: " + id + ", Name: " + name;
//        if(this.getType().equals("task")){
//            toBePrinted = toBePrinted + ", LastUpdated: " + ((TaskRallyObject)this).getLastUpdateDate();
//        }
        System.out.println(toBePrinted);
        for(RallyObject obj : children.values()){
                obj.print(indentation, indentation + relictIndentation);
        }
    }

//    public ArrayList<RallyObject> getChildren() {
//        return children;
//    }
//
//    public void setChildren(ArrayList<RallyObject> children) {
//        this.children = children;
//    }
}
