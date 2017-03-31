package com;

import java.awt.Desktop;
import java.net.URI;


/**
 * Created by zero334 on 31.03.17.
 */
public class OpenWebSite {

    private String url;

    public OpenWebSite(final String url) {
        this.url = url;
    }

    public void openSite() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(this.url));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
