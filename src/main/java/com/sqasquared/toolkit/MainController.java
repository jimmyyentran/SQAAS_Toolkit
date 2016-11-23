package com.sqasquared.toolkit;

import com.sqasquared.toolkit.email.EmailGeneratorException;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.TouchPoint;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.apache.commons.mail.EmailException;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by JTran on 11/17/2016.
 */
public class MainController implements Initializable, ControlledScreen {
    public Button SSUButton;
    public HTMLEditor editor;
    public Label fullName;
    public TextField textFieldCc;
    public TextField textFieldTo;
    public TextField textFieldSubject;
    public Button genEmailButton;
    private ScreensController screensController;
    private HostServices hostServices;
    private final UserSession userSession = App.userSession;

    public void setScreenParent(ScreensController screenParent) {
        screensController = screenParent;
    }

    public void setHostController(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void active() {
        fullName.setText(userSession.getFullName());
    }

    public void initialize(URL location, ResourceBundle resources) {
        WebView webview = (WebView) editor.lookup(".web-view");

        // Removing internal loader
//        webview.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
//            @Override
//            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
//                Platform.runLater(() -> webview.getEngine().getLoadWorker().cancel());
//            }
//        });
//
//        // Adding external host services
//        webview.getEngine().locationProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                System.out.println("observable = " + observable);
//                System.out.println("oldValue = " + oldValue);
//                System.out.println("newValue = " + newValue);
//                hostServices.showDocument(newValue);
//            }
//        });
    }

    public void ssuClick(ActionEvent actionEvent) {
        try {
            String html = App.userSession.generateHtml(UserSession.SSU);
            String to =  App.userSession.getEmailTo(UserSession.SSU);
//            String[] to =  App.userSession.getEmailTo(UserSession.SSU);
//            String toStr = "";
//            for (int i = 0; i < to.length; i++) {
//                toStr.concat(to[i] + ", ");
//            }
            String cc = App.userSession.getEmailCC();
//            String[] cc = App.userSession.getEmailCC();
//            String ccStr = "";
//            for (int i = 0; i < cc.length; i++) {
//                ccStr.concat(cc[i] + ", ");
//            }
            String subject = App.userSession.getEmailSubject(UserSession.SSU);
//            textFieldTo.setText(toStr);
//            textFieldCc.setText(ccStr);
            textFieldTo.setText(to);
            textFieldCc.setText(cc);
            textFieldSubject.setText(subject);
            editor.setHtmlText(html);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Generator Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return;
    }

    public void eodClick(ActionEvent actionEvent) {
        try {
//            System.out.println("@@@@@@@@");
            String html = App.userSession.generateHtml(UserSession.EOD);
            editor.setHtmlText(html);
//            System.out.println("@@@@@@@@");
        } catch (EmailGeneratorException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Generator Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return;
    }

    public void refreshClick(ActionEvent actionEvent) {
        clearFields();
        try {
            App.userSession.refreshTasks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateEmail(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Email");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Email", "*.eml"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator")
                + "/Desktop"));
        File selectedFile = fileChooser.showSaveDialog(App.stage);
        if(selectedFile == null){
            return;
        }

        String to = textFieldTo.getText();
        String cc = textFieldCc.getText();
        String subject = textFieldSubject.getText();
        String html = editor.getHtmlText();
        String loc = selectedFile.getAbsolutePath();

        try {
            App.userSession.generateEmail(to, cc, subject, html, loc);
        } catch (EmailException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Generator Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (MessagingException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Generator Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Generator Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return;
    }

    private void clearFields(){
        textFieldTo.clear();
        textFieldSubject.clear();
        textFieldCc.clear();
    }
}
