package com.sqasquared.toolkit;

import com.sqasquared.toolkit.rally.TaskRallyObject;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by jimmytran on 10/30/16.
 */
public class UserSession {

    String firstName, lastName, email;
    String user, api_key, server;

    TreeAlgorithmInterface alg;
    HashMap<String, TaskRallyObject> taskContainer = new HashMap();
    HashMap<String, String> templateContainer = new HashMap();

    public void run(){
        this.alg.constructTree(taskContainer);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void addTask(TaskRallyObject task) {
        taskContainer.put(task.getFormattedID(), task);
    }

    public void addTemplate(String baseName, String template) {
        templateContainer.put(baseName, template);
    }

    public String getTemplate(String template){
        return templateContainer.get(template);
    }

    public UserSession setAlg(TreeAlgorithmInterface alg) {
        this.alg = alg;
        return this;
    }

}
