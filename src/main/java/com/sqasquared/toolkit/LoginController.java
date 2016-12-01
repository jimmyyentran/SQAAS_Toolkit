package com.sqasquared.toolkit;

import com.sqasquared.toolkit.connection.RallyWrapper;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by JTran on 11/16/2016.
 */
public class LoginController implements Initializable, ControlledScreen {
    private final RallyLoader rallyLoader;
    @FXML
    public Hyperlink hyperlink;
    public Button loginButton;
    public TextField apiField;
    public Label errorMessage;
    private ScreensController screensController;
    private HostServices hostServices;

    public LoginController() {
        rallyLoader = new RallyLoader();
    }

    public void launchToolkit(ActionEvent actionEvent) {
        String api_key = apiField.getText();
        App.userSession.setAPIKey(api_key);
        try {
            RallyWrapper.initialize();
            App.userSession.loadUserInfo();
            goToMain();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setText("Invalid API Key!");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            errorMessage.setText("Invalid API Key!");
        }
    }

    @FXML
    private void openURL() {
        hostServices.showDocument("https://rally1.rallydev" +
                ".com/login/accounts/index.html#/keys");
    }


    public void setScreenParent(ScreensController screenParent) {
        screensController = screenParent;
    }

    public void setHostController(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void active() {

    }

    public void initialize(URL location, ResourceBundle resources) {

    }

    private void goToMain() {
        screensController.setScreen(App.mainScreen);
    }
}
