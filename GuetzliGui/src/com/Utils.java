package com;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by zero334 on 31.03.17.
 */
public class Utils {

    public static String getOsType() {
        final String operatingSystem = System.getProperty("os.name").toLowerCase();

        if (operatingSystem.contains("win")) {
            return "Windows";
        }
        else if (operatingSystem.contains("mac")) {
            return "MacOS";
        } else if (operatingSystem.contains("nix") || operatingSystem.contains("nux")) {
            return  "Linux";
        } else {
            return "Unknown";
        }
    }

    public static String getExtension(final String filename) {
        if (filename == null) {
            return null;
        }

        final String afterLastSlash = filename.substring(filename.lastIndexOf('/') + 1);
        final int afterLastBackslash = afterLastSlash.lastIndexOf('\\') + 1;
        final int dotIndex = afterLastSlash.indexOf('.', afterLastBackslash);

        if (dotIndex == -1) {
            return "";
        }
        return afterLastSlash.substring(dotIndex + 1);
    }

    public static void getFileProperties(final File fileToCheck, final ListView<String> displayContainer) {

        int width, height;
        try {
            final BufferedImage bufferImg = ImageIO.read(fileToCheck);
            width = bufferImg.getWidth();
            height = bufferImg.getHeight();
        } catch (final IOException ex) {
            ex.printStackTrace();
            return;
        }

        final long pixelOverall = width * height;
        double megaPixel = pixelOverall / 1024000.0f;
        megaPixel = Math.round(megaPixel * 10) / 10.0;

        final double estimatedTime = megaPixel;
        final int memoryUsage = (int)(megaPixel * 300); // In MB

        final ObservableList<String> items = FXCollections.observableArrayList (
                "Image Width: " + width + "px",
                "Image Height: " + height + "px",
                "Megapixel(s): " + megaPixel,
                "Estimated time: " + estimatedTime + " Minutes",
                "Memory usage: " + memoryUsage + " MB");

        displayContainer.setItems(items);
    }
}