package com.sqasquared.toolkit.ui;

import javafx.application.HostServices;

/**
 * Created by JTran on 11/17/2016.
 */
interface ControlledScreen {
    void setScreenParent(ScreensController screenPage);

    void setHostController(HostServices hostServices);

    void active();
}
