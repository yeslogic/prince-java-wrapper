/*
 * Copyright (C) 2005-2006, 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper.events;

/**
 * Can be used to receive messages from Prince.
 */
public interface PrinceEvents {
    /**
     * This method will be called when a message is received from Prince.
     * @param msgType The type of message.
     * @param msgLocation The name of the file that the message refers to.
     * @param msgText The text of the message.
     */
    void onMessage(MessageType msgType, String msgLocation, String msgText);

    /**
     * This method will be called when a data message is received from Prince
     * via the use of {@code Log.data("name", "value")}.
     * @param name The name of the data message.
     * @param value The value of the data message.
     */
    void onDataMessage(String name, String value);
}
