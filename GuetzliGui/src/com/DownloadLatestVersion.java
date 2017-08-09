package com;

import com.JSON.Json;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


/**
 * Created by zero334 on 08.08.2017.
 */
public class DownloadLatestVersion {

    public static boolean download(final boolean override) {
        com.HttpRequest request = new com.HttpRequest("https://api.github.com/repos/google/guetzli/releases/latest");
        final String response = request.httpGet();

        String binaryName = "";
        if (Utils.getOsType().equals("Windows")) {
            if (Utils.windowsBitness()) {
                binaryName = "guetzli_windows_x86-64.exe";
            } else {
                binaryName = "guetzli_windows_x86.exe";
            }
        } else if (Utils.getOsType().equals("Linux")) {
            binaryName = "guetzli_linux_x86-64";
        }


        // Get the download link from the JSON.
        String downloadLink = "";
        final Json json = Json.read(response);
        final int downloadableElementsCount = json.at("assets").asList().size();
        for (int iter = 0; iter < downloadableElementsCount; iter++) {

            final String possibleBinaryName = json.at("assets").at(iter).at("name").asString();
            if (possibleBinaryName.equals(binaryName)) {
                downloadLink = json.at("assets").at(iter).at("browser_download_url").asString();
                break;
            }
        }

        // Was not able to find the download link.
        if(downloadLink.isEmpty()) {
            return false;
        }

        try {
            final File pathOfRunningJar =  new File(DownloadLatestVersion.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            final String guetzliDownloadPath = pathOfRunningJar.getParent() + File.separator + binaryName;

            System.out.println(guetzliDownloadPath);
            downloadFileFromURL(downloadLink, new File(guetzliDownloadPath));

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }

        return true;
    }

    public static void downloadFileFromURL(String urlString, File destination) throws Throwable {

        final URL website = new URL(urlString);
        try (
                final ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                final FileOutputStream fos = new FileOutputStream(destination)
        ){
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}