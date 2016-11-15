package com.sqasquared.toolkit.rally;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.QueryFilter;
import com.sqasquared.toolkit.UserSession;
import org.apache.commons.io.FileUtils;

import javax.jws.soap.SOAPBinding;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
        return getUserInfo(null, null);
    }

    public static JsonObject getUserInfo(String saveJsonToDir, String jsonLoc) throws IOException {
        if(jsonLoc != null){
            File file = getLatestFileWithPrefix("UserInfo", jsonLoc);
            String raw = FileUtils.readFileToString(file);
            JsonObject result = (JsonObject)(new JsonParser()).parse(raw);
            return result;
        }
        GetResponse response = rallyAPIConnection.get(new GetRequest("user"));
        if(response.wasSuccessful()) {
            if(saveJsonToDir != null){
                DateFormat df = new SimpleDateFormat("MM_dd_yyyy'T'HH_mm_ss");
                Date today = Calendar.getInstance().getTime();
                String reportDate = df.format(today);
                File file = new File(saveJsonToDir + "/UserInfo_" + reportDate + ".json");
                FileUtils.writeStringToFile(file, response.getObject().toString());
            }
            return response.getObject();
        } else {
            System.err.println("The following errors occurred: ");
            for (String err : response.getErrors()) {
                System.err.println("\t" + err);
            }
        }
        return null;
    }

    public static JsonArray getUserStory(Map<String, String> storyIds, String saveJsonToDir, String jsonLoc) throws IOException {
//        if(jsonLoc != null){
//            File file = getLatestFileWithPrefix("UserInfo", jsonLoc);
//            String raw = FileUtils.readFileToString(file);
//            JsonObject result = (JsonObject)(new JsonParser()).parse(raw);
//            return result;
//        }
        List<QueryFilter> filters = new ArrayList<QueryFilter>();

        // Create query for each story id
        for(String storyId : storyIds.keySet()){
            filters.add(new QueryFilter("ObjectId", "=", storyId));
        }

        // Append QueryFilters to QueryRequest
        QueryRequest userStory = new QueryRequest("HierarchicalRequirement");
        QueryFilter queryFilter = null;
        for(QueryFilter qf : filters){
            if(queryFilter == null){
                queryFilter = qf;
            }else{
                queryFilter = queryFilter.or(qf);
            }
        }
        userStory.setQueryFilter(queryFilter);

        QueryResponse response = rallyAPIConnection.query(userStory);
        if(response.wasSuccessful()) {
//            if(saveJsonToDir != null){
//                DateFormat df = new SimpleDateFormat("MM_dd_yyyy'T'HH_mm_ss");
//                Date today = Calendar.getInstance().getTime();
//                String reportDate = df.format(today);
//                File file = new File(saveJsonToDir + "/UserInfo_" + reportDate + ".json");
//                FileUtils.writeStringToFile(file, response.getObject().toString());
//            }
            return response.getResults();
        } else {
            System.err.println("The following errors occurred: ");
            for (String err : response.getErrors()) {
                System.err.println("\t" + err);
            }
        }
        return null;
    }

    public static JsonArray getTasks(String email) throws IOException {
        return getTasks(email, null, null);
    }

//     Get tasks updated within a time frame
    public static JsonArray getTasks(String email, String saveJsonToDir, String jsonLoc) throws IOException {
        if(jsonLoc != null){
            File file = getLatestFileWithPrefix("Tasks", jsonLoc);
            String raw = FileUtils.readFileToString(file);
            JsonArray result = (JsonArray)(new JsonParser()).parse(raw);
            return result;
        }
        QueryRequest tasks = new QueryRequest("tasks");

        String past = new SimpleDateFormat(RallyObject.DATEFORMAT).format(UserSession.YESTERDAY_WORK_HOUR);
        tasks.setQueryFilter(new QueryFilter("Owner.name", "=", email)
                .and(new QueryFilter("LastUpdateDate", ">", past)));

        QueryResponse response = rallyAPIConnection.query(tasks);
        if(response.wasSuccessful()) {
            if(saveJsonToDir != null){
                DateFormat df = new SimpleDateFormat("MM_dd_yyyy'T'HH_mm_ss");
                Date today = Calendar.getInstance().getTime();
                String reportDate = df.format(today);
                File file = new File(saveJsonToDir + "/Tasks_" + reportDate + ".json");
                FileUtils.writeStringToFile(file, response.getResults().toString());
            }
            return response.getResults();
        } else {
            System.err.println("The following errors occurred: ");
            for (String err : response.getErrors()) {
                System.err.println("\t" + err);
            }
        }
        return null;
    }

    public static File getLatestFileWithPrefix(String prefix, String dir){
        File directory = new File(dir);
        File[] listOfFiles = directory.listFiles();
        for(int i = 0; i < listOfFiles.length; i++){
            File file = listOfFiles[i];
            if(file.isFile()){
                if(file.getName().startsWith(prefix)){
                    return file;
                }
            }
        }
        return null;
    }
}
