package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.DataObject;
import com.sqasquared.toolkit.connection.TaskRallyObject;
import com.sqasquared.toolkit.connection.TfsConnection;

import java.util.HashMap;

/**
 * Created by JTran on 11/28/2016.
 */
public class ContainerAlgorithm implements TreeAlgorithmInterface<DataObject>{
    private String topNodeWit;

    public ContainerAlgorithm(){}

    public void setTopNodeWit(String topNodeWit){
        this.topNodeWit = topNodeWit;
    }

    @Override
    public DataObject constructTree(HashMap<String, DataObject> container) {
        HashMap<String, DataObject> containerDeepCopy = new HashMap<>(container);
        DataObject top = null;

        for (DataObject obj : containerDeepCopy.values()) {
            if(obj.getType().equals(removeAdditionSigns(topNodeWit))){
                top = obj;
                containerDeepCopy.remove(obj.getId());
                break;
            }
        }

        if(top == null){
            throw new RuntimeException(String.format("No working item type with %1s name",topNodeWit));
        }

        top.setChildren(containerDeepCopy);

        return top;
    }

    private String removeAdditionSigns(String wit){
        return wit.replace("+", " ");
    }
}
