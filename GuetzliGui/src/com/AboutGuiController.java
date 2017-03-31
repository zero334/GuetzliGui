package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Created by zero334 on 31.03.17.
 */
public class AboutGuiController {

    @FXML
    public void closeAboutWindow(final ActionEvent event) {
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }
}