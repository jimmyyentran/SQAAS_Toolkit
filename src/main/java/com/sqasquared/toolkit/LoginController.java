package com.sqasquared.toolkit;

import com.rallydev.rest.RallyRestApi;
import com.sqasquared.toolkit.rally.RallyWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by JTran on 11/16/2016.
 */
public class LoginController implements Initializable, ControlledScreen{
    ScreensController screensController;
    public Button loginButton;
    public TextField apiField;
    public Label errorMessage;

    public void LaunchToolkit(ActionEvent actionEvent) {
        String api_key = apiField.getText();
        try {
            new RallyWrapper("https://rally1.rallydev.com", api_key);
            Loader.loadUserInfo(App.userSession);
//            goToMain();
        } catch (IOException e) {
            System.err.println(e);
            errorMessage.setText("");
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
            errorMessage.setText("Invalid API Key!");
        }
    }


    public void setScreenParent(ScreensController screenParent) {
        screensController = screenParent;
    }

    public void initialize(URL location, ResourceBundle resources) {

    }

    private void goToMain(){
        screensController.setScreen(App.mainScreen);
    }
}
