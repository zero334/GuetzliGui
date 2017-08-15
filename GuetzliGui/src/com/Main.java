package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.mainWindow();
    }

    private void mainWindow() {
        try {
            // View
            final FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainGui.fxml"));
            final AnchorPane pane = loader.load();

            // Scene on stage
            final Scene scene = new Scene(pane);
            scene.getStylesheets().add(getClass().getResource("/view/validation.css").toExternalForm());

            primaryStage.setTitle("Guetzli Gui");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}