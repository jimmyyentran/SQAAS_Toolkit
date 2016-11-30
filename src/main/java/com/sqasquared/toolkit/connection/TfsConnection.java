package com.sqasquared.toolkit.connection;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sqasquared.toolkit.ContainerAlgorithm;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jimmytran on 11/28/16.
 */
public class TfsConnection {

    public static final String ASM_URL = "https://tfs.asmnet.com/";
    public static final String INSTEP2 = "Custom%20Dev%20-%20Team%20El%20Segundo/6bc4d2b6-ee1c-4d01-b0b6-769c1127f56f" +
            "/_api/_wit/query?__v=5";
    public static final String INSTEP1 = "CRM/PPS/_api/_wit/query?__v=5";
    private static final String USER_AGENT = "Mozilla/5.0";
    public static final String PRODUCT_BACKLOG_ITEM = "Product Backlog Item";
    public static final String PRODUCT_BACKLOG_ITEM_WIT = "Product+Backlog+Item";
    public static final String TEST_CASE = "Test Case";
    public static final String TEST_CASE_WIT = "Test+Case";

    public TfsConnection(String username, String password){
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
        @SuppressWarnings("Since15") CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    public String formatPostForm(String parentWit, String parentId, String childWit){
        String formFormatter = "wiql=" +
            "SELECT+%%5BSystem.Id%%5D%%2C%%5BSystem.Title%%5D+%%2C%%5BSystem.State%%5D" +
            "FROM+WorkItemLinks+" +
            "WHERE" +
                "(%%5BSource%%5D.%%5BSystem.TeamProject%%5D+%%3D+%%40project+" +
                "AND+%%5BSource%%5D.%%5BSystem.WorkItemType%%5D+%%3D+" + "'%1s'" + "+" +
                "AND+%%5BSource%%5D.%%5BSystem.Id%%5D+%%3D+" + "%2s" + ")+" +
                "AND+" +
                "(%%5BTarget%%5D.%%5BSystem.TeamProject%%5D+%%3D+%%40project+" +
                "AND+%%5BTarget%%5D.%%5BSystem.WorkItemType%%5D+%%3D+" + "'%3s'" + ")+" +
            "ORDER+BY+%%5BSystem.Id%%5D+" +
            "mode(MustContain)";
        String formattedPostForm = String.format(formFormatter, parentWit, parentId, childWit);
        return formattedPostForm;
    }

    public DataObject getWorkingTree(String apiUrl, String parentWit, String parentId, String childWit) throws IOException {
        URL obj = new URL(TfsConnection.ASM_URL + apiUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setDoInput(true);
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(formatPostForm(parentWit, parentId, childWit));
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + obj.toString());
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        ContainerAlgorithm cAlg = new ContainerAlgorithm(PRODUCT_BACKLOG_ITEM);
        HashMap<String, DataObject> map = mapToObjects(response.toString());
        return cAlg.constructTree(map);
    }

    /**
     * Map TFS response to HashMap of tfsObjects
     * @param response
     * @return
     */
    private HashMap<String, DataObject> mapToObjects(String response){
        // Convert string response to JsonObject
        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(response).getAsJsonObject();

        // Get column headers
        JsonArray jsonColumn = jo.get("payload").getAsJsonObject().get("columns").getAsJsonArray();
        List<String> columnHeader = createColumnHeaders(jsonColumn);

        // Convert JsonObject to JsonArray of TfsObjects
        JsonObject mappedObject = new JsonObject();
        JsonArray jsonRows = jo.get("payload").getAsJsonObject().get("rows").getAsJsonArray();
        for(JsonElement row : jsonRows){
            JsonObject mappedRow = new JsonObject();
            JsonArray rowArray = (JsonArray)row;
            for (int i = 0; i < columnHeader.size(); i++) {
                // Strip quotes and add property
                String stripQuote = rowArray.get(i).toString().replaceAll("^\"|\"$","");
                mappedRow.addProperty(columnHeader.get(i), stripQuote);
            }
            mappedObject.add(mappedRow.get("Id").getAsString(), mappedRow);
        }


        // Convert JsonArray to HashMap of TfsObjects
        Gson gson = new GsonBuilder().create();
        Type tfsObjectType = new TypeToken<HashMap<String, TfsObject>>(){}.getType();
        HashMap<String, DataObject> tfsObjectMap = gson.fromJson(mappedObject, tfsObjectType);
//        for(DataObject obj : tfsObjectMap.values()){
//            obj.print();
//        }
        return tfsObjectMap;
    }

    /**
     * Map TFS response to list of tfsObjects
     * @param response
     * @return
     */
    private List<TfsObject> mapToObjectList(String response){
        // Convert string response to JsonObject
        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(response).getAsJsonObject();

        // Get column headers
        JsonArray jsonColumn = jo.get("payload").getAsJsonObject().get("columns").getAsJsonArray();
        List<String> columnHeader = createColumnHeaders(jsonColumn);

        // Convert JsonObject to JsonArray of TfsObjects
        JsonArray mappedArray = new JsonArray();
        JsonArray jsonRows = jo.get("payload").getAsJsonObject().get("rows").getAsJsonArray();
        for(JsonElement row : jsonRows){
            JsonObject mappedRow = new JsonObject();
            JsonArray rowArray = (JsonArray)row;
            for (int i = 0; i < columnHeader.size(); i++) {
                mappedRow.addProperty(columnHeader.get(i),
                        rowArray.get(i).toString());
            }
            mappedArray.add(mappedRow);
        }


        // Convert JsonArray to list of TfsObjects
        Gson gson = new GsonBuilder().create();
        Type tfsObjectType = new TypeToken<List<TfsObject>>(){}.getType();
        List<TfsObject> tfsObjectList = gson.fromJson(mappedArray, tfsObjectType);
        for(TfsObject object : tfsObjectList){
            object.print();
        }
        return tfsObjectList;
    }


    /**
     * Map column names to list of strings
     * @param jsonColumn
     * @return
     */
    private List<String> createColumnHeaders(JsonArray jsonColumn){
        List<String> tfsColumnHeaders = new ArrayList<String>();
        for(JsonElement column : jsonColumn){
            String col = column.getAsString();
            String parsedCol = col.substring(col.lastIndexOf(".") + 1);
            tfsColumnHeaders.add(parsedCol);
        }
        return tfsColumnHeaders;
    }
}
