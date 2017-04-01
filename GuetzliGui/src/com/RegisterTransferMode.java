package com;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.TransferMode;

/**
 * Created by zero334 on 01.04.17.
 */
public class RegisterTransferMode {

    public static void registerMode(final Button controlElement) {
        controlElement.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });
    }

    public static void registerMode(final TextField controlElement) {
        controlElement.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });
    }
}