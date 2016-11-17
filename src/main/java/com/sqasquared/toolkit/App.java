package com.sqasquared.toolkit;

import com.sqasquared.toolkit.email.EmailGenerator;
import com.sqasquared.toolkit.rally.RallyWrapper;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ui.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
//        launch(args);
        UserSession userSession = new UserSession();
        Loader loader = new Loader();
        EmailGenerator gen = new EmailGenerator();
        Preferences prefs = Preferences.userNodeForPackage(App.class);
        String text = prefs.get("A", "a");
        System.out.println("text = " + text);
        prefs.put("A", new Date().toString());
//        loader.loadUserSession(userSession);
//        userSession.setAlg(new TimeAlgorithm()).run();
//
////        userSession.getTopNode().print(4);
////        System.out.println("userSession = " + userSession);
        loader.loadTemplates(userSession);
//        try {
////            gen.generate(userSession, UserSession.SSU);
//            gen.generate(userSession, UserSession.EOD);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
