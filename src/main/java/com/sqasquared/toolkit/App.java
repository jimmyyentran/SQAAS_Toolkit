package com.sqasquared.toolkit;

import com.sqasquared.toolkit.email.EmailGenerator;
import com.sqasquared.toolkit.rally.RallyWrapper;
import javafx.scene.Group;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.EmailException;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * Created by jimmytran on 10/29/16.
 */
public class App extends Application{

    public static String loginScreen = "login";
    public static String loginScreenFile = "login.fxml";
    public static String mainScreen = "main";
    public static String mainScreenFile = "main.fxml";
    public static UserSession userSession;

    @Override
    public void start(Stage primaryStage) throws Exception {
        userSession = new UserSession();

        ScreensController mainContainer = new ScreensController();

        mainContainer.loadScreen(App.loginScreen, App.loginScreenFile);
        mainContainer.loadScreen(App.mainScreen, App.mainScreenFile);

        if(userSession.isAPIKeySet() == true){
            mainContainer.setScreen(App.mainScreen);
        }else{
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
//        UserSession userSession = new UserSession();
//        Loader loader = new Loader();
//        EmailGenerator gen = new EmailGenerator();
//        loader.loadUserSession(userSession);
//        userSession.setAlg(new TimeAlgorithm()).run();
//
////        userSession.getTopNode().print(4);
////        System.out.println("userSession = " + userSession);
//        loader.loadTemplates(userSession);
//        try {
////            gen.generate(userSession, UserSession.SSU);
//            gen.generate(userSession, UserSession.EOD);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
