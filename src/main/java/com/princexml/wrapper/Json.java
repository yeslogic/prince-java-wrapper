/*
 * Copyright (C) 2015, 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

/**
 * JSON utility class.
 */
final class Json {
    private final StringBuilder builder = new StringBuilder();
    private boolean comma;

    Json beginObj() {
        return beginObj(null);
    }

    Json beginObj(String name) {
        maybeAppendComma();
        if (name != null) {
            builder.append(quoteAndEscape(name));
            builder.append(':');
        }
        builder.append('{');
        comma = false;
        return this;
    }

    Json endObj() {
        builder.append('}');
        comma = true;
        return this;
    }

    Json beginList(String name) {
        maybeAppendComma();
        builder.append(quoteAndEscape(name));
        builder.append(':');
        builder.append('[');
        comma = false;
        return this;
    }

    Json endList() {
        builder.append(']');
        comma = true;
        return this;
    }

    Json field(String name) {
        maybeAppendComma();
        builder.append(quoteAndEscape(name));
        builder.append(':');
        comma = false;
        return this;
    }

    Json field(String name, String value) {
        return field(name).value(value);
    }

    Json field(String name, int value) {
        return field(name).value(value);
    }

    Json field(String name, boolean value) {
        return field(name).value(value);
    }

    Json value(String value) {
        return valueInternal(quoteAndEscape(value));
    }

    Json value(int value) {
        return valueInternal(value);
    }

    Json value(boolean value) {
        return valueInternal(value);
    }

    private <T> Json valueInternal(T value) {
        maybeAppendComma();
        builder.append(value);
        comma = true;
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    private String quoteAndEscape(String s) {
        String escaped = s.replace("\\", "\\\\").replace("\"", "\\\"");
        return '"' + escaped + '"';
    }

    private void maybeAppendComma() {
        if (comma) {
            builder.append(',');
        }
    }
}
