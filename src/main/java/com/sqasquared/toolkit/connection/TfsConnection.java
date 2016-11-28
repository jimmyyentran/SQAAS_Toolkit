package com.sqasquared.toolkit.connection;

import java.net.*;

/**
 * Created by jimmytran on 11/28/16.
 */
public class TfsConnection {

    public static final String ASM_URL = "https://tfs.asmnet.com/";
    public static final String INSTEP2 = "Custom%20Dev%20-%20Team%20El%20Segundo/6bc4d2b6-ee1c-4d01-b0b6-769c1127f56f" +
            "/_api/_wit/query?__v=5";

    public TfsConnection(String username, String password){
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
        @SuppressWarnings("Since15") CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

//    public void get(String api, String )
}
