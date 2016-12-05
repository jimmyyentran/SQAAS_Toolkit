package com.sqasquared.toolkit.connection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by JTran on 12/2/2016.
 */
public class GithubConnection {

    private static String REPO = "https://api.github" +
            ".com/repos/jtran064/SQAAS_Toolkit/releases/latest";

    public static String getLastestRelease() throws IOException {
        URL url = new URL(REPO);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

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

        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(response.toString()).getAsJsonObject();
        return jo.get("tag_name").getAsString();
    }
}
