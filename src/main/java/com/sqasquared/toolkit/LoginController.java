package com.sqasquared.toolkit;

import com.sqasquared.toolkit.rally.RallyWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    ScreensController screensController;
    Loader loader;
    public Button loginButton;
    public TextField apiField;
    public Label errorMessage;

    public LoginController() {
        loader = new Loader();
    }

    public void launchToolkit(ActionEvent actionEvent) {
        String api_key = apiField.getText();
        App.userSession.setAPIKey(api_key);
        try {
            RallyWrapper.initialize();
            loader.loadUserSession(App.userSession);
            goToMain();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setText("Invalid API Key!");
        } catch (URISyntaxException e) {
            errorMessage.setText("Something went wrong!");
        }
    }


    public void setScreenParent(ScreensController screenParent) {
        screensController = screenParent;
    }

    public void active() {

    }

    public void initialize(URL location, ResourceBundle resources) {

    }

    private void goToMain() {
        screensController.setScreen(App.mainScreen);
    }
}
