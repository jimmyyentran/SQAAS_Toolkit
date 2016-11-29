package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.DataObject;
import com.sqasquared.toolkit.connection.TaskRallyObject;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by jimmytran on 11/1/16.
 */
public class TimeAlgorithm implements TreeAlgorithmInterface {
    public TimeAlgorithm() {
    }

    public DataObject constructTree(HashMap<String, DataObject> container) {
        DataObject top = new DataObject("root", "0", "root");

        for (DataObject obj : container.values()) {
            top.addChild(obj);
        }

        buildTree(top);
        return top;
    }

    /*
    * Recursion to build tree
    * Tree heirarchy:
    * top -> time -> -> state -> story -> task
    * */
    private void buildTree(DataObject node) {
        if (node.getType().equals("root")) {
            Date workTime = UserSession.TODAY_WORK_HOUR;

//            DataObject tdy = new DataObject("time", Long.toString(workTime.getTime()), "today");
//            DataObject past = new DataObject("time", Long.toString(workTime.getTime() - 1), "past");
            DataObject tdy = new DataObject("time", "today", "today");
            DataObject past = new DataObject("time", "past", "past");
            for (DataObject obj : node.getChildren().values()) {
                if (obj.getType().equals("task")) {
                    if (((TaskRallyObject) obj).getLastUpdateDate().after(workTime)) {
                        tdy.addChild(obj);
                    } else {
                        past.addChild(obj);
                    }
                }
            }
            node.clearChildren();
            node.addChild(tdy, past);
            buildTree(tdy);
            buildTree(past);
        } else if (node.getType().equals("time")) {
            DataObject completed = new DataObject("state", DataObject.COMPLETED, null);
            DataObject inProgress = new DataObject("state", DataObject.INPROGRESS, null);
            DataObject defined = new DataObject("state", DataObject.DEFINED, null);
            for (DataObject obj : node.getChildren().values()) {
                if (obj.getType().equals("task")) {
                    TaskRallyObject taskRallyObject = ((TaskRallyObject) obj);
                    String storyState = taskRallyObject.getState();
                    if (storyState.equals(DataObject.COMPLETED)) {
                        completed.addChild(obj);
                    } else if (storyState.equals(DataObject.INPROGRESS)) {
                        inProgress.addChild(obj);
                    } else if (storyState.equals(DataObject.DEFINED)) {
                        defined.addChild(obj);
                    }
                }
            }
            node.clearChildren();
            node.addChild(completed, inProgress, defined);
            buildTree(completed);
            buildTree(inProgress);
            buildTree(defined);
        } else if (node.getType().equals("state")) {
            HashMap<String, DataObject> objectContainer = new HashMap<String, DataObject>();
            for (DataObject obj : node.getChildren().values()) {
                if (obj.getType().equals("task")) {
                    String storyID = ((TaskRallyObject) obj).getStoryID();
                    if (!objectContainer.containsKey(storyID)) {
                        String storyName = ((TaskRallyObject) obj).getStoryName();
                        objectContainer.put(storyID, new DataObject("story", storyID, storyName));
                    }
                    objectContainer.get(storyID).addChild(obj);
                }
            }
            node.clearChildren();
            for (DataObject obj : objectContainer.values()) {
                node.addChild(obj);
            }
        }
    }
}
