package org.stool.myserver.core.file.impl;

import java.io.File;

public class FileResolver {

    static {
        System.setProperty("server.cwd", "./");
    }

    private final File cwd;

    public FileResolver() {

        String cwdOverride = System.getProperty("server.cwd");
        if (cwdOverride != null) {
            cwd = new File(cwdOverride).getAbsoluteFile();
        } else {
            cwd = null;
        }

    }

    public File resolveFile(String fileName) {
        File file = new File(fileName);
        if (cwd != null && !file.isAbsolute()) {
            file = new File(cwd, fileName);
        }

        return file;
    }

}
