package com.sqasquared.toolkit;

import com.rallydev.rest.RallyRestApi;
import com.sqasquared.toolkit.rally.RallyWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by JTran on 11/16/2016.
 */
public class LoginController {
    public Button loginButton;
    public TextField apiField;
    public Label errorMessage;

    public void LaunchToolkit(ActionEvent actionEvent) {
        String api_key = apiField.getText();
        UserSession userSession = new UserSession();
        try {
            new RallyWrapper("https://rally1.rallydev.com", api_key);
            Loader.loadUserInfo(userSession);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            errorMessage.setText("Invalid API Key!");
        }
    }


}
