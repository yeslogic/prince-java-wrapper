package com.princexml.wrapper;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineTest {

    @Test
    void toCommand1() {
        String actual = CommandLine.toCommand("test");
        assertEquals("--test", actual);
    }

    @Test
    void toCommand2() {
        String actual = CommandLine.toCommand("test", 1);
        assertEquals("--test=1", actual);
    }

    @Test
    void toCommand3() {
        List<Integer> values = Arrays.asList(1, 2);
        String actual = CommandLine.toCommand("test", values);
        assertEquals("--test=1,2", actual);
    }

    @Test
    void toRepeatingCommands() {
        List<Integer> values = Arrays.asList(1, 2);
        List<String> actual = CommandLine.toCommands("test", values);
        List<String> expected = Arrays.asList("--test=1", "--test=2");
        assertEquals(expected, actual);
    }
}
