package controller;

import com.*;
import javafx.animation.FadeTransition;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainGuiController implements Initializable {

    private UserValueStore userValueStore = new UserValueStore();

    @FXML
    private Button btnInputFile;

    @FXML
    private TextField txtFieldInputFile, txtFieldOutputFile;

    @FXML
    private Button btnStartEncode;

    @FXML
    private ProgressIndicator progressIndEncodingProgress;

    @FXML
    private ListView<String> lstViewImageProperties;

    @FXML
    private Spinner<Integer> spnEncodingQuality;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Start!");
        IntegerStringConverter.createFor(spnEncodingQuality);

        RegisterTransferMode.registerMode(btnInputFile);
        RegisterTransferMode.registerMode(txtFieldInputFile);

        // Add event listeners
        txtFieldInputFile.textProperty().addListener((observable, oldValue, newValue) -> {
            userValueStore.setInput(new File(newValue));
        });
        txtFieldOutputFile.textProperty().addListener((observable, oldValue, newValue) -> {
            userValueStore.setOutput(new File(newValue));
        });
    }


    @FXML
    void inputFileDropped(final DragEvent event) {

        final List<File> droppedElement = event.getDragboard().getFiles();
        if (droppedElement.isEmpty()) {
            return;
        }

        final String fileExtention = Utils.getExtension(droppedElement.get(0).getName().toLowerCase());
        if (!fileExtention.equals("png") && !fileExtention.equals("jpg")) {
            return;
        }

        txtFieldInputFile.setText(droppedElement.get(0).getAbsolutePath());
        this.userValueStore.setInput(droppedElement.get(0));
        Utils.getFileProperties(droppedElement.get(0), this.lstViewImageProperties);
    }

    @FXML
    void openInputFile(ActionEvent event) {
        System.out.println("Open input file!");
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files (*.png, *.jpg)", "*.png", "*.jpg"));

        final File selectedInputFile = fileChooser.showOpenDialog(null);

        if (selectedInputFile != null && selectedInputFile.exists()) {
            txtFieldInputFile.setText(selectedInputFile.getAbsolutePath());
            this.userValueStore.setInput(selectedInputFile);
            Utils.getFileProperties(selectedInputFile, this.lstViewImageProperties);
        }
    }

    @FXML
    void openOutputFile(ActionEvent event) {
        System.out.println("Open output file!");
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.jpg"));

        final File selectedOutputFile = fileChooser.showSaveDialog(null);

        if (selectedOutputFile != null) {

            String absolutePath = selectedOutputFile.getAbsolutePath();
            if (absolutePath.endsWith(".jpg")) {
                this.userValueStore.setOutput(selectedOutputFile);
            } else {
                absolutePath += ".jpg";
                this.userValueStore.setOutput(new File(absolutePath));
            }
            txtFieldOutputFile.setText(absolutePath);
        }
    }

    @FXML
    void openAboutWindow(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(Main.class.getResource("/view/About.fxml"));
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openProjectPage(ActionEvent event) {
        final OpenWebSite projectPage = new OpenWebSite("https://github.com/zero334/GuetzliGui");
        projectPage.openSite();
    }

    @FXML
    void openGuetzliPage(ActionEvent event) {
        final OpenWebSite guetzliPage = new OpenWebSite("https://github.com/google/guetzli");
        guetzliPage.openSite();
    }


    @FXML
    void startEncoding(ActionEvent event) {

        if (!userValueStore.isComplete()) {
            // Maybe user has c&p the path
            userValueStore.setInput(new File(txtFieldInputFile.getText()));
            userValueStore.setOutput(new File(txtFieldOutputFile.getText()));
        }

        if (!userValueStore.isComplete()) {

            final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

            if (userValueStore.getInput() == null || !userValueStore.getInput().exists()) {
                txtFieldInputFile.pseudoClassStateChanged(errorClass, true);
                final FadeTransition ftInputFile = new FadeTransition(Duration.millis(450), txtFieldInputFile);
                ftInputFile.setFromValue(1.0);
                ftInputFile.setToValue(0.1);
                ftInputFile.setCycleCount(4);
                ftInputFile.setAutoReverse(true);
                ftInputFile.play();
                ftInputFile.onFinishedProperty().set(e -> txtFieldInputFile.pseudoClassStateChanged(errorClass, false));
            }

            if (userValueStore.getOutput() == null) {
                txtFieldOutputFile.pseudoClassStateChanged(errorClass, true);
                final FadeTransition ftOutputFile = new FadeTransition(Duration.millis(450), txtFieldOutputFile);
                ftOutputFile.setFromValue(1.0);
                ftOutputFile.setToValue(0.1);
                ftOutputFile.setCycleCount(4);
                ftOutputFile.setAutoReverse(true);
                ftOutputFile.play();
                ftOutputFile.onFinishedProperty().set(e -> txtFieldOutputFile.pseudoClassStateChanged(errorClass, false));
            }
            return;
        }

        if (Utils.getOsType().equals("Windows")) { // TODO Add support for mac and Linux

            String binaryName;
            if (Utils.windowsIs64Bit()) {
                binaryName = "guetzli_windows_x86-64.exe";
            } else {
                binaryName = "guetzli_windows_x86.exe";
            }

            final String guetzliPath = Utils.getExecPath() + binaryName;

            while (! new File(guetzliPath).exists()) {
                final Optional<ButtonType> redownloadGuetzli = Utils.alertBox("Guetzli not found", "Guetzli was not found. Download Guetzli?", Alert.AlertType.CONFIRMATION);
                if (redownloadGuetzli.isPresent() && redownloadGuetzli.get() == ButtonType.OK) {
                    // TODO: Test
                    DownloadLatestVersion.download(true);
                } else {
                    return;
                }
            }

            final String qualityString = "--quality " + userValueStore.getQuality();
            final String input = userValueStore.getInput().getAbsolutePath();
            String output = userValueStore.getOutput().getAbsolutePath();
            if (!output.endsWith(".jpg") && !output.endsWith(".jpeg")) {
                output += ".jpg";
            }

            final CommandLine cmdLine = CommandLine.parse(guetzliPath + ' ' + qualityString + ' ' + input + ' ' + output);

            final Executor executor = new DefaultExecutor();
            btnStartEncode.setDisable(true);
            progressIndEncodingProgress.setVisible(true);
            final DefaultExecuteResultHandler resultHandler = new PrintResultHandler(btnStartEncode, progressIndEncodingProgress);

            try {
                executor.execute(cmdLine, resultHandler);
            } catch (final IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public void checkForUpdate(ActionEvent actionEvent) {
        DownloadLatestVersion.download(true);
    }
}