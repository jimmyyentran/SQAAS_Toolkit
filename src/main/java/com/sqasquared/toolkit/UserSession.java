package com.sqasquared.toolkit;

import com.sqasquared.toolkit.email.EmailGenerator;
import com.sqasquared.toolkit.rally.RallyObject;
import com.sqasquared.toolkit.rally.TaskRallyObject;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
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

    static Preferences prop;
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

        loadPreferences();
        setAlg(new TimeAlgorithm());
    }

    private void loadPreferences(){
        prop = Preferences.userNodeForPackage(UserSession.class);

        if(prop.getBoolean("first", true)){
            prop.putBoolean("first", false);
            prop.put("user", "");
            prop.put("api_key", "");
            prop.put("server", "https://rally1.rallydev.com");
            prop.put("cc", "sqaas@sqasquared.com");
            prop.put("DEFAULT_to", "sqaas@sqasquared.com");
            prop.put("ASM_EOD_to", "jramos@sqasquared.com,abyrum@sqasquared.com,jdeleon@sqasquared.com");
            prop.put("ASM_SSU_to", "seth.labrum@advantagesolutions.net,patricia.liu@advantagesolutions.net," +
                    "joel.ramos@advantagesolutions.net,lynnyrd.raymundo@advantagesolutions.net");
        }else {
//            try {
//                String[] keys = prop.keys();
//                for (int i = 0; i < keys.length; i++) {
//                    System.out.println(keys[i] + " = " + prop.get(keys[i], ""));
//                }
//            } catch (BackingStoreException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void run(){
        topNode = this.alg.constructTree(taskContainer);
    }

    public void setProperty(String property, String value){
        prop.put(property, value);
    }

    public boolean isUserPreferencesValid(){
        if(prop.get("firstName","").equals("") || prop.get("lastName", "").equals("")
                || prop.get("email", "").equals("")){
            return false;
        }
        return true;
    }

    public boolean isAPIKeySet(){
        System.out.println(prop.get("api_key", ""));
        if(prop.get("api_key", "").equals("")){
            return false;
        }
        return true;
    }

    public void setAPIKey(String value){
        prop.put("api_key", value);
    }

    public static String getProperty(String property){
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

    public String generateHtml(String template) throws Exception {
        run();
        EmailGenerator gen = new EmailGenerator();
        return gen.generate(this, template);
    }

    public String getFullName(){
        return getProperty("firstName")+ " " + getProperty("lastName");
    }

    public void setFirstName(String firstName) {
        prop.put("firstName", firstName);
    }

    public void setLastName(String lastName) {
        prop.put("lastName", lastName);
    }

    public String getEmail() {
        return getProperty("email");
    }

    public void setEmail(String email) {
        prop.put("email", email);
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
}
