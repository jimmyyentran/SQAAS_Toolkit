package com.sqasquared.toolkit.connection;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimmytran on 11/28/16.
 */
public class TfsConnection {

    public TfsConnection(String username, String password) {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password
                        .toCharArray());
            }
        });
        @SuppressWarnings("Since15") CookieManager cookieManager = new
                CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    public String formatPostForm(String parentId) {
        String formFormatter = "wiql=" +
                "SELECT [System.Id],[System.WorkItemType],[System.Title],[System.AssignedTo]," +
                "[System.State],[System.Tags] FROM WorkItemLinks WHERE ([Source].[System" +
                ".TeamProject] = @project AND [Source].[System.Id] = " + "%1s" + ") AND ([Target]" +
                " .[System" +
                ".TeamProject] = @project) ORDER BY [System.Id] mode(MustContain)";
        return String.format(formFormatter, parentId);
    }

    public JsonObject getWorkingTree(String apiUrl, String parentWit, String parentId, String
            childWit) throws IOException {
        URL obj = new URL(ASM.ASM_URL + apiUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        con.setDoInput(true);
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(formatPostForm(parentId));
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + obj
                .toString());
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return mapToJsonObjects(response.toString());
    }

    /**
     * Execute simple GET request to base URL
     *
     * @return boolean whether or not login is successful
     * @throws IOException
     */
    public boolean isValidCredentials() throws IOException {
        URL obj = new URL(ASM.ASM_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println(responseCode);
        return responseCode == 200;
    }

    /**
     * Map TFS response to HashMap of tfsObjects determined by the object ID
     * Sample:
     * <p>
     * {
     * "25544": {
     * "Id": "25544",
     * "WorkItemType": "Product Backlog Item",
     * "TeamProject": "PPS",
     * "Title": "Rename Current \\\"Fr/Sa/Su/Mo (reschedule)\\\" and Default
     * ALBCO Program Type to other",
     * "State": "QA Accepted"
     * },
     * "25706": {
     * "Id": "25706",
     * "WorkItemType": "Test Case",
     * "TeamProject": "PPS",
     * "Title": "Verify \\\"Fr/Sa/Su/Mo (6d reschedule)\\\" Period Type exists",
     * "State": "Design"
     * },...
     *
     * @param response
     * @return
     */
    private JsonObject mapToJsonObjects(String response) {
        // Convert string response to JsonObject
        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(response).getAsJsonObject();

        // Get column headers
        JsonArray jsonColumn = jo.get("payload").getAsJsonObject().get
                ("columns").getAsJsonArray();
        List<String> columnHeader = createColumnHeaders(jsonColumn);

        // Convert JsonObject to JsonArray of TfsObjects
        JsonObject mappedObject = new JsonObject();
        JsonArray jsonRows = jo.get("payload").getAsJsonObject().get("rows")
                .getAsJsonArray();
        for (JsonElement row : jsonRows) {
            JsonObject mappedRow = new JsonObject();
            JsonArray rowArray = (JsonArray) row;
            for (int i = 0; i < columnHeader.size(); i++) {
                // Strip quotes and add property
                String stripQuote = rowArray.get(i).toString().replaceAll
                        ("^\"|\"$", "");
                mappedRow.addProperty(columnHeader.get(i), stripQuote);
            }
            mappedObject.add(mappedRow.get("Id").getAsString(), mappedRow);
        }

        return mappedObject;


    }

    /**
     * Map TFS response to list of tfsObjects
     *
     * @param response
     * @return
     */
    private List<TfsObject> mapToObjectList(String response) {
        // Convert string response to JsonObject
        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(response).getAsJsonObject();

        // Get column headers
        JsonArray jsonColumn = jo.get("payload").getAsJsonObject().get
                ("columns").getAsJsonArray();
        List<String> columnHeader = createColumnHeaders(jsonColumn);

        // Convert JsonObject to JsonArray of TfsObjects
        JsonArray mappedArray = new JsonArray();
        JsonArray jsonRows = jo.get("payload").getAsJsonObject().get("rows")
                .getAsJsonArray();
        for (JsonElement row : jsonRows) {
            JsonObject mappedRow = new JsonObject();
            JsonArray rowArray = (JsonArray) row;
            for (int i = 0; i < columnHeader.size(); i++) {
                mappedRow.addProperty(columnHeader.get(i),
                        rowArray.get(i).toString());
            }
            mappedArray.add(mappedRow);
        }


        // Convert JsonArray to list of TfsObjects
        Gson gson = new GsonBuilder().create();
        Type tfsObjectType = new TypeToken<List<TfsObject>>() {
        }.getType();
        List<TfsObject> tfsObjectList = gson.fromJson(mappedArray,
                tfsObjectType);
        for (TfsObject object : tfsObjectList) {
            object.print();
        }
        return tfsObjectList;
    }


    /**
     * Map column names to list of strings
     *
     * @param jsonColumn
     * @return
     */
    private List<String> createColumnHeaders(JsonArray jsonColumn) {
        List<String> tfsColumnHeaders = new ArrayList<>();
        for (JsonElement column : jsonColumn) {
            String col = column.getAsString();
            String parsedCol = col.substring(col.lastIndexOf(".") + 1);
            tfsColumnHeaders.add(parsedCol);
        }
        return tfsColumnHeaders;
    }
}
