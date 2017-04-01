package com;

import java.awt.*;
import java.net.URI;


/**
 * Created by zero334 on 31.03.17.
 */
public class OpenWebSite {

    private final String url;

    public OpenWebSite(final String url) {
        this.url = url;
    }

    public void openSite() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(this.url));
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
