package com;

import java.io.File;

/**
 * Created by zero334 on 30.03.2017.
 */
public class UserValueStore {

    File input = null, output = null;
    int quality = 84;

    public boolean isComplete() {
        return (input != null && input.exists()) && (output != null && output.exists());
    }

    public File getInput() {
        return input;
    }

    public void setInput(final File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(final File output) {
        this.output = output;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(final int quality) {
        this.quality = quality;
    }
}
