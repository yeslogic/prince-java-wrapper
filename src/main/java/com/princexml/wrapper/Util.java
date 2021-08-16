/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Utility methods.
 */
final class Util {
    static final int BUFFER_SIZE = 65536;

    /**
     * Invoke a process from a list of command line arguments.
     */
    static Process invokeProcess(List<String> cmdLine) throws IOException {
        String[] cmdLineArray = cmdLine.toArray(new String[0]);
        return Runtime.getRuntime().exec(cmdLineArray);
    }

    /**
     * Read all the available data from an InputStream and write it to an
     * OutputStream. The data is copied in chunks of 4096 bytes. There is no
     * return value, as an IOException will be thrown if a read or write
     * operation fails.
     */
    static void copyInputToOutput(InputStream in, OutputStream out) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        do {
            bytesRead = in.read(buffer);
            if (bytesRead > 0) {
                out.write(buffer, 0, bytesRead);
            }
        } while (bytesRead != -1);
    }
}
