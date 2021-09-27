/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

final class Pair {
    final String fst;
    final String snd;

    Pair(String fst) {
        this(fst, null);
    }

    Pair(String fst, String snd) {
        this.fst = fst;
        this.snd = snd;
    }
}
