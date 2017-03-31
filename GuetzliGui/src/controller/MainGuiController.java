package controller;

import com.*;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainGuiController implements Initializable {

    private ResourceBundle bundle;
    private Locale locale;
    private UserValueStore userValueStore = new UserValueStore();

    @FXML
    private Button btnInputFile;

    @FXML
    private Button btnOutputFile;

    @FXML
    private TextField lblInputFile;

    @FXML
    private TextField lblOutputFile;

    @FXML
    private Button btnStartEncode;

    @FXML
    private ProgressIndicator progIndEncodeingProgress;

    @FXML
    private ListView<String> lstViewImageProperties;

    @FXML
    private Spinner<Integer> spnEncodeingQuality;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Start!");
        IntegerStringConverter.createFor(spnEncodeingQuality);
        // TODO Write config
    }

    @FXML
    void changeGuiLangToEnglish(ActionEvent event) {

    }

    @FXML
    void changeGuiLangToGerman(ActionEvent event) {

    }

    @FXML
    void inputFileDropped(DragEvent event) {

    }

    @FXML
    void openInputFile(ActionEvent event) {
        System.out.println("Open input file!");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.png, *.jpg)", "*.png", "*.jpg");

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(extFilter);

        final File selectedInputFile = fileChooser.showOpenDialog(null);

        if (selectedInputFile != null && selectedInputFile.exists()) {
            lblInputFile.setText(selectedInputFile.getAbsolutePath());
            this.userValueStore.setInput(selectedInputFile);
            this.getFileProperties(selectedInputFile);
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
            lblInputFile.setText(selectedOutputFile.getAbsolutePath());
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
        catch (IOException e) {
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
    void outputFileDropped(DragEvent event) {
        System.out.println("outputFileDropped");
    }

    @FXML
    void startEncodeing(ActionEvent event) {

        if (!userValueStore.isComplete()) {

            final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

            if (userValueStore.getInput() == null || !userValueStore.getInput().exists()) {
                lblInputFile.pseudoClassStateChanged(errorClass, true);
                final FadeTransition ftInputFile = new FadeTransition(Duration.millis(450), lblInputFile);
                ftInputFile.setFromValue(1.0);
                ftInputFile.setToValue(0.1);
                ftInputFile.setCycleCount(4);
                ftInputFile.setAutoReverse(true);
                ftInputFile.play();
                ftInputFile.onFinishedProperty().set(e -> lblInputFile.pseudoClassStateChanged(errorClass, false));
            }

            if (userValueStore.getOutput() == null || !userValueStore.getOutput().exists()) {
                lblOutputFile.pseudoClassStateChanged(errorClass, true);
                final FadeTransition ftOutputFile = new FadeTransition(Duration.millis(450), lblOutputFile);
                ftOutputFile.setFromValue(1.0);
                ftOutputFile.setToValue(0.1);
                ftOutputFile.setCycleCount(4);
                ftOutputFile.setAutoReverse(true);
                ftOutputFile.play();
                ftOutputFile.onFinishedProperty().set(e -> lblOutputFile.pseudoClassStateChanged(errorClass, false));
            }
            return;
        }


        final String guetzliPath = "C:\\Gue\\bin.exe"; // TODO
        final String qualityString = "--quality " + userValueStore.getQuality();
        final String input = userValueStore.getInput().getAbsolutePath();
        final String output = userValueStore.getInput().getAbsolutePath();

        final CommandLine cmdLine = CommandLine.parse(guetzliPath + ' ' + qualityString + ' ' + input + ' ' + output);

        final Executor executor = new DefaultExecutor();
        System.out.println("[print] Executing non-blocking print job  ...");
        btnStartEncode.setDisable(true);
        progIndEncodeingProgress.setVisible(true);
        DefaultExecuteResultHandler resultHandler = new PrintResultHandler(btnStartEncode, progIndEncodeingProgress);

        try {
            executor.execute(cmdLine, resultHandler);
        } catch (final IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    void getFileProperties(final File fileTocheck) {
        final double bytes = fileTocheck.length();
        final double kilobytes = (bytes / 1024);

        int width = 0, height = 0;
        try {
            BufferedImage bimg = ImageIO.read(fileTocheck);
            width = bimg.getWidth();
            height = bimg.getHeight();

        } catch (IOException e) {
            e.printStackTrace();
        }


        final long pixelOverall = width * height;
        double megaPixel = pixelOverall / 1024000.0f;
        megaPixel = Math.round(megaPixel * 10) / 10.0;

        final double estimatedTime = megaPixel;
        final int memoryUsage = (int)(megaPixel * 300); // In MB

        final ObservableList<String> items = FXCollections.observableArrayList (
                "Image Width: " + width,
                        "Image Height: " + height,
                        "Megapixel(s): " + megaPixel,
                        "Estimated time: " + estimatedTime + " Minutes",
                        "Memory usage: " + memoryUsage + " MB");

        lstViewImageProperties.setItems(items);
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