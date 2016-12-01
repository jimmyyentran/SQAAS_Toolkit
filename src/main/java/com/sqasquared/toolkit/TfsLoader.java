package com.sqasquared.toolkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sqasquared.toolkit.connection.DataObject;
import com.sqasquared.toolkit.connection.TfsObject;
import com.sqasquared.toolkit.connection.TfsWrapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by JTran on 11/30/2016.
 */
public class TfsLoader extends Loader<DataObject>{

    public void loadWorkingTree(String apiUrl, String parentWit, String parentId, String childWit) throws IOException {
        JsonObject response = TfsWrapper.getWorkingTree(apiUrl, parentWit, parentId, childWit);
        // Convert JsonArray to HashMap of TfsObjects
        Gson gson = new GsonBuilder().create();
        Type tfsObjectType = new TypeToken<HashMap<String, TfsObject>>(){}.getType();
        HashMap<String, DataObject> tfsObjectMap = gson.fromJson(response, tfsObjectType);
        objectManager.setObjectContainer(tfsObjectMap);
        for(DataObject obj : tfsObjectMap.values()){
            obj.print();
        }
    }

}
