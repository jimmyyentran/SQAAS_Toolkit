package com.sqasquared.toolkit.rally;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jimmytran on 11/1/16.
 */
public class RallyObject {
    //    private ArrayList<RallyObject> children;
    private HashMap<String, RallyObject> children;
    private String type;
    private String id;
    private String name;

    public RallyObject(String type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
//        this.children = new ArrayList<RallyObject>();
        this.children = new HashMap<String, RallyObject>();
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
        }
    }

    public void clearChildren(){
        this.children.clear();
    }

//    public ArrayList<RallyObject> getChildren() {
//        return children;
//    }
//
//    public void setChildren(ArrayList<RallyObject> children) {
//        this.children = children;
//    }
}
