package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.DataObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by JTran on 11/28/2016.
 */
public class ContainerAlgorithm implements TreeAlgorithmInterface<DataObject> {
    private String topNodeWit;
    private String childNodeWit;
    private String topNodeID;

    public ContainerAlgorithm() {
    }

    public void setTopNodeWit(String topNodeWit) {
        this.topNodeWit = topNodeWit;
    }

    public void setTopNodeID(String topNodeID) {
        this.topNodeID = topNodeID;
    }

    public void setChildNodeWit(String childNodeWit){
        this.childNodeWit = childNodeWit;
    }

    @Override
    public DataObject constructTree(HashMap<String, DataObject> container) {
        HashMap<String, DataObject> containerDeepCopy = new HashMap<>(container);
        DataObject top = null;

        Iterator<Map.Entry<String, DataObject>> iter = containerDeepCopy.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, DataObject> entry = iter.next();
            DataObject obj = entry.getValue();

            if(obj.getId().equals(topNodeID)){
                top=obj;
                iter.remove();
                continue;
            }

            // Remove all irrelevant items
            if(!obj.getType().equals(removeAdditionSigns(childNodeWit))){
                System.out.println("obj.getType() = " + obj.getType());
                iter.remove();
            }
        }

        if (top == null) {
            throw new RuntimeException(String.format("No item with id \"%1s\" was found in the " +
                            "given project", topNodeID));
        }

        if (containerDeepCopy.size() == 0){
            throw new RuntimeException(String.format("No %1s related to item \"%2s\"",
                    removeAdditionSigns(childNodeWit), topNodeID));
        }

        top.setChildren(containerDeepCopy);

        return top;
    }

    private String removeAdditionSigns(String wit) {
        return wit.replace("+", " ");
    }
}
