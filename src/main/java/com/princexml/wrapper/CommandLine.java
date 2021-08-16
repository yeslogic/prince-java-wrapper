/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command line utility class.
 */
final class CommandLine {
    static String toCommand(String key) {
        return "--" + key;
    }

    static <T> String toCommand(String key, T value) {
        return toCommand(key) + "=" + value;
    }

    static <T> String toCommand(String key, List<T> values) {
        String csv = values
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return toCommand(key, csv);
    }

    static <T> List<String> toCommands(String key, List<T> values) {
        List<String> repeatingCommands = new ArrayList<>();
        values.forEach(v -> repeatingCommands.add(toCommand(key, v)));
        return repeatingCommands;
    }
}
