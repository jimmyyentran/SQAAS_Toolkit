package com.sqasquared.toolkit.connection;

import com.google.gson.JsonArray;
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jimmytran on 10/29/16.
 */
public class RallyWrapper {
    private static Logger LOG = Logger.getLogger(RallyWrapper.class.getName());

    private static RallyRestApi rallyAPIConnection;

    protected RallyWrapper() {
    }

    /**
     * Initializes the RallyRestApi by using the server and api key. No
     * actual network connections are made here.
     *
     * @throws URISyntaxException Malformed URI from server preferences
     */
    public static void initialize() throws URISyntaxException {
        rallyAPIConnection = new RallyRestApi(new URI(UserSession.getProperty
                ("server")), UserSession.getProperty("api_key"));
    }

    public static void closeConnection() {
        try {
            rallyAPIConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Query Rally for the user information such as full name and email address
     *
     * @return JsonObject containing the Rally user information
     * @throws IOException
     */
    public static JsonObject getUserInfo() throws IOException {
        return getUserInfo(null, null);
    }

    /**
     * Query Rally for the user information such as full name and email address.
     *
     * @param saveJsonToDir Directory to save JSON responses with a time tag
     * @param jsonLoc       Directory to load JSON test files from (latest file)
     * @return JsonObject containing the Rally user information
     * @throws IOException
     */
    private static JsonObject getUserInfo(String saveJsonToDir, String
            jsonLoc) throws IOException {
        LOG.log(Level.FINE, "Fetching user info");
        if (jsonLoc != null) {
            File file = getLatestFileWithPrefix("UserInfo", jsonLoc);
            String raw = FileUtils.readFileToString(file);
            return (JsonObject) (new JsonParser()).parse(raw);
        }
        GetResponse response = rallyAPIConnection.get(new GetRequest("user"));
        if (response.wasSuccessful()) {
            if (saveJsonToDir != null) {
                DateFormat df = new SimpleDateFormat("MM_dd_yyyy'T'HH_mm_ss");
                Date today = Calendar.getInstance().getTime();
                String reportDate = df.format(today);
                File file = new File(saveJsonToDir + "/UserInfo_" +
                        reportDate + ".json");
                FileUtils.writeStringToFile(file, response.getObject()
                        .toString());
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

    /**
     * Query formattedID of Rally stories using it's objectID. Formatted
     * story ID takes the form of US#####.
     * Example: US17781
     *
     * @param storyIds      ObjectIDs of stories to be fetched
     * @param saveJsonToDir Directory to save JSON response with time tag
     * @param jsonLoc       Directory to load JSON test files form (latest file)
     * @return JsonArray containing information of story objects
     * @throws IOException File IO
     */
    public static JsonArray getUserStory(Map<String, String> storyIds, String
            saveJsonToDir, String jsonLoc) throws IOException {
        LOG.log(Level.FINE, "Fetching user story info");
//        if(jsonLoc != null){
//            File file = getLatestFileWithPrefix("UserInfo", jsonLoc);
//            String raw = FileUtils.readFileToString(file);
//            JsonObject result = (JsonObject)(new JsonParser()).parse(raw);
//            return result;
//        }
        List<QueryFilter> filters = new ArrayList<>();

        // Create query for each story id
        for (String storyId : storyIds.keySet()) {
            filters.add(new QueryFilter("ObjectId", "=", storyId));
        }

        // Append QueryFilters to QueryRequest
        QueryRequest userStory = new QueryRequest("HierarchicalRequirement");

        userStory.setWorkspace(RALLY.SQAR_WORKPLACE);

        QueryFilter queryFilter = null;
        for (QueryFilter qf : filters) {
            if (queryFilter == null) {
                queryFilter = qf;
            } else {
                queryFilter = queryFilter.or(qf);
            }
        }
        userStory.setQueryFilter(queryFilter);

        QueryResponse response = rallyAPIConnection.query(userStory);
        if (response.wasSuccessful()) {
//            if(saveJsonToDir != null){
//                DateFormat df = new SimpleDateFormat("MM_dd_yyyy'T'HH_mm_ss");
//                Date today = Calendar.getInstance().getTime();
//                String reportDate = df.format(today);
//                File file = new File(saveJsonToDir + "/UserInfo_" +
// reportDate + ".json");
//                FileUtils.writeStringToFile(file, response.getObject()
// .toString());
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

    /**
     * Query Rally for today's and yesterday's modified tasks.
     *
     * @param email Email of the current user
     * @return JsonArray containing JSON tasks
     * @throws IOException File IO
     */
    public static JsonArray getTasks(String email) throws IOException {
        return getTasks(email, null, null);
    }

    /**
     * Query Rally for today's and yesterday's modified tasks.
     *
     * @param email         Email of the current user
     * @param saveJsonToDir Directory to save JSON response with time tag
     * @param jsonLoc       Directory to load JSON test files form (latest file)
     * @return JsonArray containing JSON tasks
     * @throws IOException File IO
     */
    private static JsonArray getTasks(String email, String saveJsonToDir,
                                      String jsonLoc) throws IOException {
        if (jsonLoc != null) {
            File file = getLatestFileWithPrefix("Tasks", jsonLoc);
            String raw = FileUtils.readFileToString(file);
            return (JsonArray) (new JsonParser()).parse(raw);
        }
        QueryRequest tasks = new QueryRequest("tasks");

        tasks.setWorkspace(RALLY.SQAR_WORKPLACE);

        String past = new SimpleDateFormat(RALLY.DATEFORMAT).format
                (UserSession.YESTERDAY_WORK_HOUR);
        tasks.setQueryFilter(new QueryFilter("Owner.name", "=", email)
                .and(new QueryFilter("LastUpdateDate", ">", past)));

        QueryResponse response = rallyAPIConnection.query(tasks);
        if (response.wasSuccessful()) {
            if (saveJsonToDir != null) {
                DateFormat df = new SimpleDateFormat("MM_dd_yyyy'T'HH_mm_ss");
                Date today = Calendar.getInstance().getTime();
                String reportDate = df.format(today);
                File file = new File(saveJsonToDir + "/Tasks_" + reportDate +
                        ".json");
                FileUtils.writeStringToFile(file, response.getResults()
                        .toString());
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

    /**
     * Get latest file from a directory with the prefix
     *
     * @param prefix Prefix of the file
     * @param dir    Directory of the target file
     * @return File
     */
    private static File getLatestFileWithPrefix(String prefix, String dir) {
        File directory = new File(dir);
        File[] listOfFiles = directory.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().startsWith(prefix)) {
                    return file;
                }
            }
        }
        return null;
    }

    public RallyRestApi getConnection() {
        return rallyAPIConnection;
    }
}
