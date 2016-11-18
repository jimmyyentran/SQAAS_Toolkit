package com.sqasquared.toolkit;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by JTran on 11/17/2016.
 */
public class MainController implements Initializable, ControlledScreen{
    ScreensController screensController;

    public void setScreenParent(ScreensController screenParent) {
        screensController = screenParent;
    }

    public void initialize(URL location, ResourceBundle resources) {

    }


}
