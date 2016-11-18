package com.sqasquared.toolkit;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.HTMLEditor;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by JTran on 11/17/2016.
 */
public class MainController implements Initializable, ControlledScreen{
    public Button SSUButton;
    public HTMLEditor editor;
    public Label fullName;
    ScreensController screensController;
    private UserSession userSession = App.userSession;

    public void setScreenParent(ScreensController screenParent) {
        screensController = screenParent;
    }

    public void active() {
        fullName.setText(userSession.getFullName());
    }

    public void initialize(URL location, ResourceBundle resources) {

    }

    public void ssuClick(ActionEvent actionEvent) {
        try {
            String html = App.userSession.generateHtml(UserSession.SSU);
            editor.setHtmlText(html);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eodClick(ActionEvent actionEvent) {
        try {
            String html = App.userSession.generateHtml(UserSession.EOD);
            editor.setHtmlText(html);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
