package com;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;

/**
 * Created by zero334 on 30.03.2017.
 */
public class PrintResultHandler extends DefaultExecuteResultHandler {

    private Button btn;
    private ProgressIndicator progressIndicator;

    public PrintResultHandler(final Button button, final ProgressIndicator progressIndicator) {

       this.btn = button;
       this.progressIndicator = progressIndicator;
    }

    @Override
    public void onProcessComplete(final int exitValue) {
       super.onProcessComplete(exitValue);
       System.out.println("The program was successfully executed...");

       this.btn.setDisable(false);
       this.progressIndicator.setVisible(false);
    }

    @Override
    public void onProcessFailed(final ExecuteException e) {
       super.onProcessFailed(e);
       System.err.println(e.getMessage());
    }
}