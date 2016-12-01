package com.sqasquared.toolkit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sqasquared.toolkit.connection.RallyWrapper;
import com.sqasquared.toolkit.connection.TaskRallyObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JTran on 10/31/2016.
 */
public class RallyLoader extends Loader<TaskRallyObject> {
    private static Logger LOG = Logger.getLogger(RallyLoader.class.getName());

    /**
     * Load name and email into static UserSession
     *
     * @throws IOException
     */
    public void loadUserInfo() throws IOException {
        LOG.log(Level.FINE, "Loading user info");
        if (App.userSession.isUserPreferencesValid()) {
            return;
        }
        JsonObject info = RallyWrapper.getUserInfo();
        String firstName = info.get("FirstName").getAsString();
        String lastName = info.get("LastName").getAsString();
        String email = info.get("EmailAddress").getAsString();

        App.userSession.setFirstName(firstName);
        App.userSession.setLastName(lastName);
        App.userSession.setEmail(email);
    }

    public void loadTasks() throws IOException {
        LOG.log(Level.FINE, "Loading tasks");
        JsonArray response = RallyWrapper.getTasks(UserSession.getEmail());
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
            objectManager.add(new TaskRallyObject(taskName, objectID, formattedID, state, storyName,
                    storyRef, projectName, projectRef, creationDate, lastUpdateDate, estimate));
        }
    }

    /**
     * Takes each task's userStoryID and insert userStoryFormattedID
     *
     * @throws IOException
     */
    public void loadUserStory() throws IOException {
        LOG.log(Level.FINE, "Loading user stories");
        Map<String, String> storyIds = new HashMap<>();

        // If children is not of TaskRallyObject, throw
        if (!(objectManager.getObjectContainer().values().iterator().next() instanceof TaskRallyObject)) {
            throw new IOException("Expected values to be of TaskRallyObject!");
        }

        // Get story ID's and insert as keys into hash-map
        for (TaskRallyObject obj : objectManager.getObjectContainer().values()) {
            storyIds.put(obj.getStoryID(), null);
        }

        // Append formattedID's into map
        JsonArray response = RallyWrapper.getUserStory(storyIds, null, null);
        for (JsonElement res : response) {
            JsonObject userStory = res.getAsJsonObject();
            storyIds.put(userStory.get("ObjectID").toString(), userStory.get("FormattedID").toString().replace("\"", ""));
        }

        // Loop over tasks and set storyFormattedID
        for (TaskRallyObject obj : objectManager.getObjectContainer().values()) {
            obj.setStoryFormattedID(storyIds.get(obj.getStoryID()));
        }
    }
}
