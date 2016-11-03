package com.sqasquared.toolkit.rally;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.QueryFilter;
import com.sqasquared.toolkit.UserSession;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

/**
 * Created by jimmytran on 10/29/16.
 */
public class RallyWrapper {

    private static RallyRestApi rallyAPIConnection;

    public RallyWrapper(String server, String api_key) throws URISyntaxException, IOException {
        rallyAPIConnection = new RallyRestApi(new URI(server), api_key);
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

    public static JsonObject getUserInfo() throws IOException {
        GetResponse response = rallyAPIConnection.get(new GetRequest("user"));
        if(response.wasSuccessful()) {
            return response.getObject();
        } else {
            System.err.println("The following errors occurred: ");
            for (String err : response.getErrors()) {
                System.err.println("\t" + err);
            }
        }
        return null;
    }

//     Get tasks updated within a time frame
    public static JsonArray getTasks(String email) throws IOException {
        QueryRequest tasks = new QueryRequest("tasks");

        String past = new SimpleDateFormat(RallyObject.DATEFORMAT).format(UserSession.YESTERDAY_WORK_HOUR);
        tasks.setQueryFilter(new QueryFilter("Owner.name", "=", email)
                .and(new QueryFilter("LastUpdateDate", ">", past)));

        QueryResponse response = rallyAPIConnection.query(tasks);
        if(response.wasSuccessful()) {
            return response.getResults();
        } else {
            System.err.println("The following errors occurred: ");
            for (String err : response.getErrors()) {
                System.err.println("\t" + err);
            }
        }
        return null;
    }
}
