package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.DataObject;
import com.sqasquared.toolkit.connection.TfsWrapper;

import java.io.IOException;

/**
 * Created by JTran on 11/30/2016.
 */
public class TfsManager extends DataManager<DataObject>{

    public TfsManager() {
        super();
    }

    public void loadWorkingTree(String apiUrl, String parentWit,
                                String parentId, String childWit) throws IOException {
        ((TfsLoader)loader).loadWorkingTree(apiUrl, parentWit, parentId, childWit);
        run();
    }

    public boolean isValidCredentials() throws IOException {
        return TfsWrapper.isValidCredentials();
    }


    @Override
    public void add(DataObject item) {

    }
}
