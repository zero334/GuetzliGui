package com;

/**
 * Created by zero334 on 31.03.17.
 */
public class Utils {

    static String getOsType() {
        final String operatingSystem = System.getProperty("os.name").toLowerCase();

        if (operatingSystem.indexOf( "win" ) >= 0) {
            return "Windows";
        }
        else if (operatingSystem.indexOf( "mac" ) >= 0) {
            return "MacOS";
        } else if (operatingSystem.indexOf( "nix") >=0 || operatingSystem.indexOf( "nux") >=0) {
            return  "Linux";
        } else {
            return "Unknown";
        }
    }
}
