package com.sqasquared.toolkit.connection;

import com.google.gson.JsonObject;
import com.sqasquared.toolkit.UserSession;

import java.io.IOException;

/**
 * Created by JTran on 11/30/2016.
 */
public class TfsWrapper {
    private static TfsConnection tfsConnection;

    protected TfsWrapper(){}

    public static void initialize(){
        tfsConnection = new TfsConnection(UserSession.getProperty("ASM_username"),
                UserSession.getProperty("ASM_password"));
    }

    public static JsonObject getWorkingTree(String apiUrl, String parentWit, String parentId, String childWit) throws IOException {
        return tfsConnection.getWorkingTree(apiUrl, parentWit, parentId,childWit);
    }

    public static boolean isValidCredentials() throws IOException {
        initialize();
        return tfsConnection.isValidCredentials();
    }

}
