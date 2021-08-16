package com.princexml.wrapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonTest {

    @Test
    void beginObj1() {
        Json j = new Json()
                .beginObj()
                .endObj();
        assertEquals("{}", j.toString());
    }

    @Test
    void beginObj2() {
        Json j = new Json()
                .beginObj("name")
                .endObj();
        assertEquals("\"name\":{}", j.toString());
    }

    @Test
    void beginList() {
        Json j = new Json()
                .beginList("list")
                .endList();
        assertEquals("\"list\":[]", j.toString());
    }

    @Test
    void field1() {
        Json j = new Json()
                .field("name", "string");
        assertEquals("\"name\":\"string\"", j.toString());
    }

    @Test
    void field2() {
        Json j = new Json()
                .field("name", 12345);
        assertEquals("\"name\":12345", j.toString());
    }

    @Test
    void field3() {
        Json j = new Json()
                .field("name1", true)
                .field("name2", false);
        assertEquals("\"name1\":true,\"name2\":false", j.toString());
    }

    @Test
    void quoteAndEscape() {
        Json j = new Json()
                .beginObj("ab\\cd\"ef\"gh\\ij")
                .endObj();
        assertEquals("\"ab\\\\cd\\\"ef\\\"gh\\\\ij\":{}", j.toString());
    }
}
