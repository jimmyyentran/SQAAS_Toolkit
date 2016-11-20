package com.sqasquared.toolkit;

import com.sqasquared.toolkit.rally.RallyWrapper;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by jimmytran on 10/29/16.
 */
public class App extends Application {

    public static final String loginScreen = "login";
    public static final String loginScreenFile = "login.fxml";
    public static final String mainScreen = "main";
    public static final String mainScreenFile = "main.fxml";
    public static UserSession userSession;

    @Override
    public void start(Stage primaryStage) throws Exception {
        userSession = new UserSession();

        ScreensController mainContainer = new ScreensController();

        mainContainer.loadScreen(App.loginScreen, App.loginScreenFile);
        mainContainer.loadScreen(App.mainScreen, App.mainScreenFile);

        if (userSession.isAPIKeySet() && userSession.isUserPreferencesValid()) {
            RallyWrapper.initialize();
            mainContainer.setScreen(App.mainScreen);
            new Loader().loadUserSession(userSession);
        } else {
            mainContainer.setScreen(App.loginScreen);
        }

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SQAAS Toolkit");
        primaryStage.show();
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch(args);
    }

}
