package com.sqasquared.toolkit.rally;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jimmytran on 10/29/16.
 */
public class RallyWrapper {

    private static RallyRestApi rallyAPIConnection;
    private UserSession user;

    public RallyWrapper(String server, String api_key) throws URISyntaxException, IOException {
        rallyAPIConnection = new RallyRestApi(new URI(server), api_key);
        getUserInfo();
        getTasks();
        for(TaskObject task: user.taskContainer.values()){
            System.out.println(task);
        }
    }

    public static RallyRestApi getConnection(){
        //TODO: Check if connection is closed
        return rallyAPIConnection;
    }

    public static void closeConnection(){
        try {
            rallyAPIConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo() throws IOException {
        GetResponse response = rallyAPIConnection.get(new GetRequest("user"));
        if(response.wasSuccessful()) {
            String firstName = response.getObject().get("FirstName").getAsString();
            String lastName = response.getObject().get("LastName").getAsString();
            String email = response.getObject().get("EmailAddress").getAsString();
            user = new UserSession(firstName, lastName, email);
        } else {
            System.err.println("The following errors occurred: ");
            for (String err : response.getErrors()) {
                System.err.println("\t" + err);
            }
        }
    }

    // Get tasks updated within a time frame
    private void getTasks() throws IOException {
        QueryRequest tasks = new QueryRequest("tasks");

        tasks.setQueryFilter(new QueryFilter("Owner.name", "=", user.email)
                .and(new QueryFilter("LastUpdateDate", ">", "2016-10-27")));

        QueryResponse response = rallyAPIConnection.query(tasks);
        if(response.wasSuccessful()) {
            for(JsonElement result: response.getResults()){
                JsonObject task = result.getAsJsonObject();
                String taskName = task.get("Name").getAsString();
                String formattedID = task.get("FormattedID").getAsString();
                String state = task.get("State").getAsString();
                String story = task.getAsJsonObject("WorkProduct").get("_refObjectName").getAsString();
                String creationDate = task.get("CreationDate").getAsString();
                String lastUpdateDate = task.get("LastUpdateDate").getAsString();
                double estimate = task.get("Estimate").getAsDouble();
                user.taskContainer.put(formattedID, new TaskObject(taskName, formattedID, state, story, creationDate,
                        lastUpdateDate, estimate));
            }
        } else {
            System.err.println("The following errors occurred: ");
            for (String err : response.getErrors()) {
                System.err.println("\t" + err);
            }
        }
    }
}
