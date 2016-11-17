package com.sqasquared.toolkit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sqasquared.toolkit.rally.RallyWrapper;
import com.sqasquared.toolkit.rally.TaskRallyObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by JTran on 10/31/2016.
 */
public class Loader {
    InputStream inputStream;

    public void loadPropValues(UserSession userSession) throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "resources/config/config.properties";
            File file = new File(propFileName);
            inputStream = FileUtils.openInputStream(file);


            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            // get the property value and print it out
            String user = prop.getProperty("user");
            String api_key = prop.getProperty("api_key");
            String server  = prop.getProperty("server");

            userSession.setUser(user);
            userSession.setApi_key(api_key);
            userSession.setServer(server);
            userSession.setProp(prop);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
    }

    public static void loadUserInfo(UserSession userSession) throws IOException {
        JsonObject info = RallyWrapper.getUserInfo();
//        JsonObject info = RallyWrapper.getUserInfo("resources/json/", null);
//        JsonObject info = RallyWrapper.getUserInfo(null, "resources/json/");
        String firstName = info.get("FirstName").getAsString();
        String lastName = info.get("LastName").getAsString();
        String email = info.get("EmailAddress").getAsString();

        userSession.setFirstName(firstName);
        userSession.setLastName(lastName);
        userSession.setEmail(email);
    }

    public void loadRally(UserSession userSession) throws IOException, URISyntaxException {
        new RallyWrapper(userSession.getServer(), userSession.getApi_key());
    }

    public void loadTasks(UserSession userSession) throws IOException {
        JsonArray response = RallyWrapper.getTasks(userSession.getEmail());
//        JsonArray response = RallyWrapper.getTasks(userSession.getEmail(), "resources/json/", null);
//        JsonArray response = RallyWrapper.getTasks(userSession.getEmail(), null, "resources/json/");
//        JsonArray response = RallyWrapper.getTasks(userSession.getEmail(), "resources/json/", "resources/json/");
        for(JsonElement result: response){
            JsonObject task = result.getAsJsonObject();
            String taskName = task.get("Name").getAsString();
            String objectID = task.get("ObjectID").getAsString();
            String formattedID = task.get("FormattedID").getAsString();
            String state = task.get("State").getAsString();

            JsonObject storyObject = task.getAsJsonObject("WorkProduct");
            String storyName = storyObject.get("_refObjectName").getAsString();
            String storyRef = storyObject.get("_ref").getAsString();

            JsonObject projectObject = task.getAsJsonObject("Project");
            String projectName = projectObject.get("_refObjectName").getAsString();
            String projectRef = projectObject.get("_ref").getAsString();

            String creationDate = task.get("CreationDate").getAsString();
            String lastUpdateDate = task.get("LastUpdateDate").getAsString();
            String estimate;
            try {
                estimate = task.get("Estimate").getAsString();
            } catch (UnsupportedOperationException ex) {
                estimate = "0.0";
            }
            userSession.addTask(new TaskRallyObject(taskName, objectID, formattedID, state, storyName,
                    storyRef, projectName, projectRef, creationDate, lastUpdateDate, estimate));
        }
    }

    /**
     * Takes each task's userStoryID and insert userStoryFormattedID
     * @param userSession   current session persistent variables
     * @throws IOException
     */
    public void loadUserStory(UserSession userSession) throws IOException {
        Map<String, String> storyIds = new HashMap<String, String>();

        // Get story ID's and insert as keys into hash-map
        for(TaskRallyObject obj : userSession.getTaskContainer().values()){
            storyIds.put(obj.getStoryID(), null);
        }

        // Append formattedID's into map
        JsonArray response = RallyWrapper.getUserStory(storyIds, null, null);
        for(JsonElement res : response){
            JsonObject userStory = res.getAsJsonObject();
            storyIds.put(userStory.get("ObjectID").toString(), userStory.get("FormattedID").toString().replace("\"", ""));
        }

        // Loop over tasks and set storyFormattedID
        for(TaskRallyObject obj : userSession.getTaskContainer().values()){
            obj.setStoryFormattedID(storyIds.get(obj.getStoryID()));
        }
    }

    public void loadTemplates (UserSession userSession) throws IOException {
        String dirName = "resources/template/";
        File directory = new File(dirName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile()){
                String baseName = FilenameUtils.getBaseName(file.getName());
                String htmlEmailTemplate = FileUtils.readFileToString(file);
                userSession.addTemplate(baseName, htmlEmailTemplate);
            }
        }
    }

    public void loadUserSession(UserSession userSession) throws IOException, URISyntaxException {
        loadPropValues(userSession);
        loadRally(userSession);
        loadUserInfo(userSession);
        loadTasks(userSession);
        loadUserStory(userSession);
    }
}
