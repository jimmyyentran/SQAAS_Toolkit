package com.sqasquared.toolkit.rally;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jimmytran on 10/30/16.
 */
public class UserSession {

    String firstName, lastName, email;
    HashMap<String, TaskObject> taskContainer = new HashMap();

    public UserSession(String fname, String lname, String email){
        firstName = fname;
        lastName = lname;
        this.email = email;
    }
}
