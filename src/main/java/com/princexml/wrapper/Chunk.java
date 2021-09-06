/*
 * Copyright (C) 2015-2016, 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility class used by the Prince control interface.
 */
final class Chunk {
    private final String tag;
    private final byte[] bytes;

    Chunk(String tag, byte[] bytes) {
        this.tag = tag;
        this.bytes = bytes;
    }

    String getTag() {
        return tag;
    }

    byte[] getBytes() {
        return bytes;
    }

    String getString() {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    static Chunk readChunk(InputStream in) throws IOException {
        byte[] tagBytes = new byte[3];
        if (!readBytes(in, tagBytes)) {
            throw new IOException("failed to read chunk tag");
        }
        String tag = new String(tagBytes, StandardCharsets.US_ASCII);

        if (in.read() != ' ') {
            throw new IOException("expected space after chunk tag");
        }

        int length = 0;
        int max_num_length = 9;
        int num_length = 0;

        for (; num_length < max_num_length + 1; num_length++) {
            int b = in.read();

            if (b == '\n') {
                break;
            }

            if (b < '0' || b > '9') {
                throw new IOException("unexpected character in chunk length");
            }

            length *= 10;
            length += b - '0';
        }

        if (num_length < 1 || num_length > max_num_length) {
            throw new IOException("invalid chunk length");
        }

        byte[] dataBytes = new byte[length];
        if (!readBytes(in, dataBytes)) {
            throw new IOException("failed to read chunk data");
        }

        if (in.read() != '\n') {
            throw new IOException("expected newline after chunk data");
        }

        return new Chunk(tag, dataBytes);
    }

    static boolean readBytes(InputStream in, byte[] buf) throws IOException {
        int length = buf.length;
        int offset = 0;

        while (length > 0) {
            int count = in.read(buf, offset, length);

            if (count < 0) {
                return false;
            }

            if (count > length) {
                throw new IOException("unexpected read overrun");
            }

            length -= count;
            offset += count;
        }

        return true;
    }

    static void writeChunk(OutputStream out, String tag, String data) throws IOException {
        writeChunk(out, tag, data.getBytes(StandardCharsets.UTF_8));
    }

    static void writeChunk(OutputStream out, String tag, byte[] data) throws IOException {
        String s = tag + " " + data.length + "\n";

        out.write(s.getBytes(StandardCharsets.UTF_8));
        out.write(data);
        out.write('\n');
    }
}
