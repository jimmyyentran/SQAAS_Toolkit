package com.sqasquared.toolkit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sqasquared.toolkit.connection.DataObject;
import com.sqasquared.toolkit.connection.RallyWrapper;
import com.sqasquared.toolkit.connection.TaskRallyObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JTran on 10/31/2016.
 */
class Loader {
    private static Logger LOG = Logger.getLogger(Loader.class.getName());

    private void loadUserInfo(UserSession userSession) throws IOException {
        LOG.log(Level.FINE, "Loading user info");
        if (userSession.isUserPreferencesValid()) {
            return;
        }
        JsonObject info = RallyWrapper.getUserInfo();
        String firstName = info.get("FirstName").getAsString();
        String lastName = info.get("LastName").getAsString();
        String email = info.get("EmailAddress").getAsString();

        userSession.setFirstName(firstName);
        userSession.setLastName(lastName);
        userSession.setEmail(email);
    }

    public void loadTasks(UserSession userSession) throws IOException {
        LOG.log(Level.FINE, "Loading tasks");
        JsonArray response = RallyWrapper.getTasks(userSession.getEmail());
        for (JsonElement result : response) {
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
     *
     * @param userSession current session persistent variables
     * @throws IOException
     */
    public void loadUserStory(UserSession userSession) throws IOException {
        LOG.log(Level.FINE, "Loading user stories");
        Map<String, String> storyIds = new HashMap<String, String>();

        // If children is not of TaskRallyObject, throw
        if(!(userSession.getTaskContainer().values().iterator().next() instanceof TaskRallyObject)){
            throw new IOException("Expected values to be of TaskRallyObject!");
        }

        // Get story ID's and insert as keys into hash-map
        for (DataObject obj : userSession.getTaskContainer().values()) {
            storyIds.put(((TaskRallyObject)obj).getStoryID(), null);
        }

        // Append formattedID's into map
        JsonArray response = RallyWrapper.getUserStory(storyIds, null, null);
        for (JsonElement res : response) {
            JsonObject userStory = res.getAsJsonObject();
            storyIds.put(userStory.get("ObjectID").toString(), userStory.get("FormattedID").toString().replace("\"", ""));
        }

        // Loop over tasks and set storyFormattedID
        for (DataObject obj : userSession.getTaskContainer().values()) {
            ((TaskRallyObject)obj).setStoryFormattedID(storyIds.get(((TaskRallyObject)obj).getStoryID()));
        }
    }

    private void loadTemplates(UserSession userSession) throws IOException {
        LOG.log(Level.FINE, "Loading templates");
        InputStream in = getClass().getResourceAsStream("/template/end_of_day.html");
        String eod = IOUtils.toString(in);
        userSession.addTemplate(UserSession.EOD, eod);

        in = getClass().getResourceAsStream("/template/story_status_update.html");
        String ssu = IOUtils.toString(in);
        userSession.addTemplate(UserSession.SSU, ssu);
    }

    public void loadUserSession(UserSession userSession) throws IOException {
        loadUserInfo(userSession);
        loadTasks(userSession);
        loadUserStory(userSession);
        loadTemplates(userSession);
    }
}
