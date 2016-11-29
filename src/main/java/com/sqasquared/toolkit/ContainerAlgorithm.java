package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.DataObject;
import com.sqasquared.toolkit.connection.TaskRallyObject;

import java.util.HashMap;

/**
 * Created by JTran on 11/28/2016.
 */
public class ContainerAlgorithm implements TreeAlgorithmInterface{
    private String topNodeWit;

    public ContainerAlgorithm(String topNodeWit){
        this.topNodeWit = topNodeWit;
    }

    @Override
    public DataObject constructTree(HashMap<String, DataObject> container) {
        DataObject top = new DataObject("root", "0", "root");

        for (DataObject obj : container.values()) {
            top.addChild(obj);
        }

//        buildTree(top);
//        return top;

        return null;
    }
}
