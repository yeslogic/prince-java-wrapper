/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.events;

/**
 * The type of message received from Prince.
 */
public enum MessageType {
    /** Error message. */
    ERR,
    /** Warning message. */
    WRN,
    /** Information message. */
    INF,
    /** Debug message. */
    DBG,
    /** Console output from {@code console.log()}. */
    OUT
}
