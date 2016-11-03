package com.sqasquared.toolkit;

import com.sqasquared.toolkit.rally.RallyObject;
import com.sqasquared.toolkit.rally.TaskRallyObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by jimmytran on 11/1/16.
 */
public class TimeAlgorithm implements TreeAlgorithmInterface{
    public TimeAlgorithm(){
    }

    public RallyObject constructTree(HashMap<String, TaskRallyObject> container) {
        RallyObject top = new RallyObject("root", "0", "root");

        for(RallyObject obj: container.values()){
            top.addChild(obj);
        }

        buildTree(top);
//        top.print(4);
        return top;
    }

    /*
    * Recursion to build tree
    * Tree heirarchy:
    * top -> time -> -> state -> story -> task
    * */
    private void buildTree(RallyObject node){
        if(node.getType().equals("root")){
            Date workTime = UserSession.TODAY_WORK_HOUR;

//            RallyObject tdy = new RallyObject("time", Long.toString(workTime.getTime()), "today");
//            RallyObject past = new RallyObject("time", Long.toString(workTime.getTime() - 1), "past");
            RallyObject tdy = new RallyObject("time", "today", "today");
            RallyObject past = new RallyObject("time", "past", "past");
            for(RallyObject obj: node.getChildren().values()){
                if(obj.getType().equals("task")){
                    if(((TaskRallyObject)obj).getLastUpdateDate().after(workTime)){
                        tdy.addChild(obj);
                    }else{
                        past.addChild(obj);
                    }
                }
            }
            node.clearChildren();
            node.addChild(tdy, past);
            buildTree(tdy);
            buildTree(past);
        } else if(node.getType().equals("time")){
//            RallyObject completed = new RallyObject("state", "1", "completed");
//            RallyObject notCompleted = new RallyObject("state", "0", "notCompleted");
            RallyObject completed = new RallyObject("state", "completed", "completed");
            RallyObject notCompleted = new RallyObject("state", "notCompleted", "notCompleted");
            for(RallyObject obj: node.getChildren().values()){
                if(obj.getType().equals("task")){
                    TaskRallyObject taskRallyObject = ((TaskRallyObject)obj);
                    String storyID = taskRallyObject.getState();
                    if(storyID.equals("Completed")){
                        completed.addChild(obj);
                    }else{
                        notCompleted.addChild(obj);
                    }
                }
            }
            node.clearChildren();
            node.addChild(completed, notCompleted);
            buildTree(completed);
            buildTree(notCompleted);
        } else if(node.getType().equals("state")){
            HashMap<String, RallyObject> objectContainer = new HashMap<String, RallyObject>();
            for(RallyObject obj: node.getChildren().values()){
                if(obj.getType().equals("task")){
                    String storyID = ((TaskRallyObject)obj).getStoryID();
                    if(!objectContainer.containsKey(storyID)){
                        String storyName = ((TaskRallyObject)obj).getStoryName();
                        objectContainer.put(storyID, new RallyObject("story", storyID, storyName));
                    }
                    objectContainer.get(storyID).addChild(obj);
                }
            }
            node.clearChildren();
            for(RallyObject obj: objectContainer.values()){
                node.addChild(obj);
            }
        }
    }

}
