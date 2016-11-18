package com.sqasquared.toolkit;

import com.sqasquared.toolkit.rally.RallyObject;
import com.sqasquared.toolkit.rally.TaskRallyObject;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * Created by jimmytran on 10/30/16.
 */
public class UserSession {

    public static Date TODAY_WORK_HOUR;
    public static Date YESTERDAY_WORK_HOUR;
    public static String EOD = "end_of_day";
    public static String SSU = "story_status_update";
    public static String SSU_TAG = "[STORY STATUS UPDATE]";
    public static String EOD_TAG = "[END OF DAY UPDATE]";
    public static String SSU_KEY = "SSU";
    public static String EOD_KEY = "EOD";
    public static String TO = "to";
    public static String CC = "cc";
    public static String SEPARATOR = "_";
    public static String EMAIL_SEPARATOR = ",";

    String firstName, lastName, email;
    String user, api_key, server;
//    Properties prop;
    Preferences prop;

    TreeAlgorithmInterface alg;
    HashMap<String, TaskRallyObject> taskContainer = new HashMap();
    HashMap<String, String> templateContainer = new HashMap();
    RallyObject topNode = null;

    public UserSession() {
        Calendar today = Calendar.getInstance();
        today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND); today.clear(Calendar.MILLISECOND);
        today.set(Calendar.HOUR_OF_DAY, 6);
        TODAY_WORK_HOUR = today.getTime();
        today.add(Calendar.DATE, -1);
        YESTERDAY_WORK_HOUR = today.getTime();

        //Load preferences
        prop = Preferences.userNodeForPackage(UserSession.class);
        this.user = prop.get("user", "");
        this.api_key = prop.get("api_key", "");
        this.server  = prop.get("server", "https://rally1.rallydev.com");

        prop.get("cc", "sqaas@sqasquared.com");
        prop.get("DEFAULT_to", "sqaas@sqasquared.com");
        prop.get("ASM_EOD_to", "jramos@sqasquared.com,abyrum@sqasquared.com,jdeleon@sqasquared.com");
        prop.get("ASM_SSU_to", "seth.labrum@advantagesolutions.net,patricia.liu@advantagesolutions.net,joel.ramos@advantagesolutions.net,lynnyrd.raymundo@advantagesolutions.net");
    }

    public void run(){
        topNode = this.alg.constructTree(taskContainer);
    }

    public boolean isUserPreferencesValid(){
        if(prop.get("firstName","") == "" || prop.get("lastName", "") == ""
                || prop.get("email", "") == ""){
            return false;
        }
        return true;
    }

    public boolean isAPIKeySet(){
        if(prop.get("api_key", "") == ""){
            return false;
        }
        return true;
    }

    public String getProperty(String property){
        String val = prop.get(property, "");
        if(val == ""){
            System.err.println("Unset property: " + property);
        }
        return val;
    }

    public String[] getEmailTo(String emailType){
        String key = null;
        if(emailType.equals(EOD)){
            key = EOD_KEY;
        }else if(emailType.equals(SSU)){
            key = SSU_KEY;
        }
        String keyTo = formatKey(getBusinessPartner(), key, TO);
        String emailTo = getProperty(keyTo);
        String[] emails = emailTo.split(EMAIL_SEPARATOR);
//        for (int i = 0; i < emails.length; i++) {
//            System.out.println("emails[i] = " + emails[i]);
//        }
//        if(emailType.equals(SSU)){
//            String keyTo = formatKey(getBusinessPartner(), SSU_KEY, TO);
//            String emailTo = prop.getProperty(keyTo);
//            String[] emails = emailTo.split(EMAIL_SEPARATOR);
//            for (int i = 0; i < emails.length; i++) {
//                System.out.println("emails[i] = " + emails[i]);
//            }
//            return emails;
//        }else if(emailType.equals(EOD)){
//
//        }
//        return null;
        return emails;
    }

    public String[] getEmailCC(){
        String emailTo = getProperty(CC);
        String[] emails = emailTo.split(EMAIL_SEPARATOR);
        return emails;
    }

    public String getBusinessPartner(){
        String bp = getProperty("business_partner");
        if(bp != null && bp.length() != 0){
            return bp;
        }
        return "DEFAULT";
    }

    public String formatKey(String... str){
        String formatted = "";
        for(String s : str){
            if(formatted.equals("")){
                formatted += s;
            } else {
                formatted += (SEPARATOR + s);
            }
        }
        return formatted;
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

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        prop.put("firstName", firstName);
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        prop.put("lastName", lastName);
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        prop.put("email", email);
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

    public RallyObject getTopNode(){
        return topNode;
    }

    public HashMap<String, TaskRallyObject> getTaskContainer(){return taskContainer;}

//    public Properties getProp() {
//        return prop;
//    }

}
