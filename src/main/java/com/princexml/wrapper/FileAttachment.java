/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

final class FileAttachment {
    final String url;
    final String filename;
    final String description;

    FileAttachment(String url) {
        this(url, null, null);
    }

    FileAttachment(String url, String filename, String description) {
        this.url = url;
        this.filename = filename;
        this.description = description;
    }
}
