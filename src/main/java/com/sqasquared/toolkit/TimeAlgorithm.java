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
        return null;
    }

    private void buildTree(RallyObject node){
        if(node.getType().equals("root")){
            Calendar today = Calendar.getInstance();
            today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
            today.set(Calendar.HOUR_OF_DAY, 6);

            Date workTime = today.getTime();

            RallyObject tdy = new RallyObject("time", Long.toString(workTime.getTime()), "today");
            RallyObject past = new RallyObject("time", Long.toString(workTime.getTime() - 1), "past");
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
            

        }


    }

}
