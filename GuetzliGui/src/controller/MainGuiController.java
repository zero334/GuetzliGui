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
import java.util.Locale;
import java.util.ResourceBundle;

public class MainGuiController implements Initializable {

    private ResourceBundle bundle;
    private Locale locale;
    private UserValueStore userValueStore = new UserValueStore();

    @FXML
    private Button btnInputFile;

    @FXML
    private TextField txtFieldInputFile, txtFieldOutputFile;

    @FXML
    private Button btnStartEncode;

    @FXML
    private ProgressIndicator progIndEncodeingProgress;

    @FXML
    private ListView<String> lstViewImageProperties;

    @FXML
    private Spinner<Integer> spnEncodeingQuality;

    @FXML
    private RadioMenuItem radioLanguageEnglish, radioLanguageGerman;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Start!");
        IntegerStringConverter.createFor(spnEncodeingQuality);
        // TODO Write config

        RegisterTransferMode.registerMode(btnInputFile);
        RegisterTransferMode.registerMode(txtFieldInputFile);

        radioLanguageEnglish.setSelected(true);
    }

    @FXML
    void changeGuiLangToEnglish(ActionEvent event) {

    }

    @FXML
    void changeGuiLangToGerman(ActionEvent event) {

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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.png, *.jpg)", "*.png", "*.jpg");

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(extFilter);

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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Jpg files", "*.jpg");
        final FileChooser fileChooser = new FileChooser();
        fileChooser .getExtensionFilters().add(extFilter);

        final File selectedOutputFile = fileChooser.showSaveDialog(null);

        if (selectedOutputFile != null && selectedOutputFile.exists()) {
            txtFieldInputFile.setText(selectedOutputFile.getAbsolutePath());
            this.userValueStore.setOutput(selectedOutputFile);
        }
    }

    @FXML
    void openAboutWindow(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(Main.class.getResource("../view/About.fxml"));
            Stage stage = new Stage();
            stage.setTitle("About");
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
    void startEncodeing(ActionEvent event) {

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

            if (userValueStore.getOutput() == null || !userValueStore.getOutput().exists()) {
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
            final String guetzliPath = "C:\\Gue\\bin.exe";
            final String qualityString = "--quality " + userValueStore.getQuality();
            final String input = userValueStore.getInput().getAbsolutePath();
            final String output = userValueStore.getInput().getAbsolutePath();

            final CommandLine cmdLine = CommandLine.parse(guetzliPath + ' ' + qualityString + ' ' + input + ' ' + output);

            final Executor executor = new DefaultExecutor();
            btnStartEncode.setDisable(true);
            progIndEncodeingProgress.setVisible(true);
            DefaultExecuteResultHandler resultHandler = new PrintResultHandler(btnStartEncode, progIndEncodeingProgress);

            try {
                executor.execute(cmdLine, resultHandler);
            } catch (final IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }


    private void loadLangGer() {
        this.loadLang("ger");
    }

    private void loadLangEn() {
        this.loadLang("en");
    }

    void loadLang(final String lang) {
        locale = new Locale(lang);
        bundle = ResourceBundle.getBundle("languages.language", locale);
        // label.setText(bundle.getString("label"));
    }
}