package com.sqasquared.toolkit;

import com.sqasquared.toolkit.email.EmailGeneratorException;
import org.apache.commons.mail.EmailException;
import org.apache.http.auth.InvalidCredentialsException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by jimmytran on 10/30/16.
 */
public class UserSession {

    public static final String EOD = "end_of_day";
    public static final String SSU = "story_status_update";
    public static final String SSUP = "story_status_update_progress";
    public static final String TCR = "test_case_realized";
    public static final String SSU_TAG = "[STORY STATUS UPDATE]";
    public static final String EOD_TAG = "[END OF DAY UPDATE]";
    public static final String EMAIL_SEPARATOR = ",";
    private static final String SSU_KEY = "SSU";
    private static final String EOD_KEY = "EOD";
    private static final String TO = "to";
    private static final String CC = "cc";
    private static final String SEPARATOR = "_";
    public static Date TODAY_WORK_HOUR;
    public static Date YESTERDAY_WORK_HOUR;
    private static Preferences prop;
    private static AppDirector appDirector;

    public UserSession() {
        Calendar today = Calendar.getInstance();
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        today.clear(Calendar.MILLISECOND);
        today.set(Calendar.HOUR_OF_DAY, 6);
        TODAY_WORK_HOUR = today.getTime();
        today.add(Calendar.DATE, -1);
        YESTERDAY_WORK_HOUR = today.getTime();

        loadPreferences();
    }

    public static String getProperty(String property) {
        String val = prop.get(property, "");
        if (val.equals("")) {
            System.err.println("Unset property: " + property);
            prop.put(property, "");
        }
        return val;
    }

    public static String getFullName() {
        return getProperty("firstName") + " " + getProperty("lastName");
    }

    public static String getEmail() {
        return getProperty("email");
    }

    public void setEmail(String email) {
        prop.put("email", email);
        if (getProperty("sqaas_username").length() == 0) {
            prop.put("sqaas_username", email);
        }
    }

    public static String getTemplate(String template) {
        return appDirector.getTemplate(template);
    }

    public void setAppDirector(AppDirector appDirector) {
        UserSession.appDirector = appDirector;
    }

    private void loadPreferences() {
        prop = Preferences.userNodeForPackage(UserSession.class);
        if (prop.getBoolean("first", true)) {
            prop.putBoolean("first", false);
            prop.put("api_key", "");
            prop.put("server", "https://rally1.rallydev.com");
            prop.put("cc", "sqaas@sqasquared.com");
            prop.put("DEFAULT_to", "sqaas@sqasquared.com");
            prop.put("ASM_EOD_to", "jramos@sqasquared.com,abyrum@sqasquared" +
                    ".com,jdeleon@sqasquared.com");
            prop.put("ASM_SSU_to", "seth.labrum@advantagesolutions.net," +
                    "patricia.liu@advantagesolutions.net," +
                    "joel.ramos@advantagesolutions.net,lynnyrd" +
                    ".raymundo@advantagesolutions.net");
            prop.put("business_partner", "ASM");
            prop.put("ASM_username", "");
            prop.put("ASM_password", "");
            prop.put("version", "v1.1.0-alpha");
            prop.put("lastName", "");
            prop.put("firstName", "");
            prop.put("sqaas_username", "");
            prop.put("sqaas_password", "");
        } else {
            try {
                String[] keys = prop.keys();
                for (String key : keys) {
                    System.out.println(key + " = " + prop.get(key, ""));
                }
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
        }
    }

    public void setProperty(String property, String value) {
        prop.put(property, value);
    }

    public boolean isUserPreferencesValid() {
        return !(prop.get("firstName", "").equals("") || prop.get("lastName",
                "").equals("")
                || prop.get("email", "").equals(""));
    }

    public boolean isAPIKeySet() {
        return !prop.get("api_key", "").equals("");
    }

    public void setAPIKey(String value) {
        prop.put("api_key", value);
    }

    public String getEmailTo(String emailType) {
        String key = null;
        if (emailType.equals(EOD)) {
            key = EOD_KEY;
        } else if (emailType.equals(SSU) || emailType.equals(SSUP)) {
            key = SSU_KEY;
        }
        String business_partner = getProperty("business_partner");
        if (business_partner == null || business_partner.length() == 0) {
            business_partner = "ASM";
        }
        String keyTo = formatKey(business_partner, key, TO);
        return getProperty(keyTo);
    }

    public String getEmailCC() {
        return getProperty(CC);
    }

    private String formatKey(String... str) {
        String formatted = "";
        for (String s : str) {
            if (formatted.equals("")) {
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

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field : fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    public void setFirstName(String firstName) {
        prop.put("firstName", firstName);
    }

    public void setLastName(String lastName) {
        prop.put("lastName", lastName);
    }

    /******************************************

     Load session methods here

     ******************************************/

    public void loadRallyTasks() throws IOException {
        appDirector.loadRallyTasks();
    }

    public void loadUserInfo() throws IOException {
        appDirector.loadUserInfo();
    }

    /******************************************

     The following are events triggered by the view

     ******************************************/

    public String generateHtml(String template) throws EmailGeneratorException {
        return appDirector.generateHtml(template);
    }

    public void generateEmail(String to, String cc, String subject, String
            html, String loc) throws EmailException, MessagingException,
            IOException {
        appDirector.generateEmail(to, cc, subject, html, getEmail(), loc);
    }

    public void sendEmail(String to, String cc, String subject, String
            html) throws EmailException, MessagingException,
            IOException, InvalidCredentialsException {
        String username = getEmail();
        String password = getProperty("sqaas_password");
        if (username.length() * password.length() == 0) {
            throw new InvalidCredentialsException("SQAAS email or password is unset! Fix in File " +
                    "> Settings");
        }
//        try {
        appDirector.sendEmail(to, cc, subject, html, getEmail(), username, password);
//        } catch ()
    }

    public String getEmailSubject() {
        return appDirector.getLastEmailSubject();
    }

    public void refreshTasks() throws IOException {
        appDirector.refreshTasks();
    }

    public String generateTestCases(String pbi, String project, String template) throws
            IOException {
        return appDirector.generateTestCases(pbi, project, template);
    }

    public void loginASM() throws IOException, InvalidCredentialsException {
        appDirector.loginASM();
    }

    public void loginASM(String username, String password) throws
            IOException, InvalidCredentialsException {
        setProperty("ASM_username", "ASM\\" + username);
        setProperty("ASM_password", password);
        appDirector.loginASM();
    }

    public void resetToDefault() throws BackingStoreException {
        Preferences prop = Preferences.userNodeForPackage(UserSession.class);
        prop.clear();

        loadPreferences();
    }

}
