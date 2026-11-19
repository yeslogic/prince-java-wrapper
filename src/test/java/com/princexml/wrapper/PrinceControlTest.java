package com.princexml.wrapper;

import com.princexml.wrapper.enums.InputType;
import com.princexml.wrapper.events.MessageType;
import com.princexml.wrapper.events.PrinceEvents;
import org.junit.jupiter.api.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("for local testing")
class PrinceControlTest {
    static class Events implements PrinceEvents {
        String message;

        @Override
        public void onMessage(MessageType msgType, String msgLocation, String msgText) {
            message += msgType.name() + " " + msgLocation + " " + msgText + "\n";
        }

        @Override
        public void onDataMessage(String name, String value) {
            message += "DAT " + name + " " + value + "\n";
        }
    }

    // Edit path accordingly.
    static final String PRINCE_PATH = "path/to/prince";
    static final String RESOURCES_DIR = System.getProperty("user.dir") +
            "/src/test/java/com/princexml/wrapper/resources/";

    @Nested
    class InitialisedTests {
        final String INPUT_PATH1 = RESOURCES_DIR + "convert-1a.html";
        final String INPUT_PATH2 = RESOURCES_DIR + "convert-1b.html";

        PrinceControl p;
        Events e;

        @BeforeEach
        void setUp() throws IOException {
            e = new Events();
            p = PrinceControl.builder(PRINCE_PATH)
                    .events(e)
                    .failSafe(true)
                    .noNetwork(true)
                    .start();
        }

        @AfterEach
        void tearDown() throws IOException {
            p.stop();
        }

        @Test
        void testConvert1() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "control-convert-1.pdf")) {
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .convert(INPUT_PATH1, fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testConvert2() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "control-convert-2.pdf")) {
                List<String> inputPaths = Arrays.asList(INPUT_PATH1, INPUT_PATH2);
                byte[] styleSheet1 = Files.readAllBytes(Paths.get(RESOURCES_DIR +
                        "convert-1a.css"));
                byte[] styleSheet2 = Files.readAllBytes(Paths.get(RESOURCES_DIR +
                        "convert-1b.css"));
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(styleSheet1)
                        .styleSheet(styleSheet2)
                        .convert(inputPaths, fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testConvert3() throws IOException {
            try (FileInputStream fis = new FileInputStream(INPUT_PATH2);
                 FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "control-convert-3.pdf")
            ) {
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .inputType(InputType.HTML)
                        .convert(fis, fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testConvertString() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "control-convertstring.pdf")) {
                String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH1)),
                        StandardCharsets.UTF_8);
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1b.css")
                        .inputType(InputType.HTML)
                        .convertString(input, fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testConvertMultiple1() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR +
                    "control-convert-mult1a.pdf")) {
                String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH1)),
                        StandardCharsets.UTF_8);
                boolean result1 = p.newJob()
                        .inputType(InputType.HTML)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .convertString(input, fos);
                assertTrue(result1, e.message);
            }

            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR +
                    "control-convert-mult1b.pdf")) {
                String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH2)),
                        StandardCharsets.UTF_8);
                // job1's resources shouldn't leak into job2.
                boolean result2 = p.newJob()
                        .inputType(InputType.HTML)
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1b.css")
                        .convertString(input, fos);
                assertTrue(result2, e.message);
            }
        }

        @Test
        void testConvertMultiple2() throws IOException {
            byte[] styleSheet = Files.readAllBytes(Paths.get(RESOURCES_DIR +
                    "convert-1a.css"));

            PrinceControl.Job job = p.newJob()
                    .javaScript(true);

            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR +
                    "control-convert-mult2a.pdf")) {
                boolean result1 = job.convert(INPUT_PATH1, fos);
                assertTrue(result1, e.message);
            }

            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR +
                    "control-convert-mult2b.pdf")) {
                boolean result2 = job
                        .styleSheet(styleSheet)
                        .convert(INPUT_PATH2, fos);
                assertTrue(result2, e.message);
            }

            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR +
                    "control-convert-mult2c.pdf")) {
                String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH1)),
                        StandardCharsets.UTF_8);
                boolean result3 = job
                        .inputType(InputType.HTML)
                        .styleSheet(RESOURCES_DIR + "convert-1b.css")
                        .convertString(input, fos);
                assertTrue(result3, e.message);
            }
        }

        @Test
        void testConvertMultiple3() throws IOException {
            String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH1)),
                    StandardCharsets.UTF_8);

            byte[] styleSheet1 = Files.readAllBytes(Paths.get(RESOURCES_DIR +
                    "convert-1a.css"));
            byte[] styleSheet2 = Files.readAllBytes(Paths.get(RESOURCES_DIR +
                    "convert-1b.css"));

            PrinceControl.Job job1 = p.newJob()
                    .inputType(InputType.HTML)
                    .javaScript(true);
            // job2's stylesheet shouldn't leak into job1.
            PrinceControl.Job job2 = p.newJob(job1)
                    .styleSheet(styleSheet1);
            // job2's options should carry over into job3.
            PrinceControl.Job job3 = p.newJob(job2)
                    .styleSheet(styleSheet2);

            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR +
                    "control-convert-mult3a.pdf")) {
                boolean result1 = job1.convertString(input, fos);
                assertTrue(result1, e.message);
            }

            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR +
                    "control-convert-mult3b.pdf")) {
                boolean result2 = job2.convertString(input, fos);
                assertTrue(result2, e.message);
            }

            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR +
                    "control-convert-mult3c.pdf")) {
                boolean result3 = job3.convertString(input, fos);
                assertTrue(result3, e.message);
            }
        }
    }

    @Nested
    class StandaloneTests {
        @Test
        void testNoLocal() throws IOException {
            PrinceEvents e = new PrinceEvents() {
                @Override
                public void onMessage(MessageType msgType, String msgLocation, String msgText) {
                    assertEquals(MessageType.WRN, msgType);
                    assertTrue(msgLocation.contains("convert-1a.css"));
                    assertEquals("not loading local file", msgText);
                }

                @Override
                public void onDataMessage(String name, String value) {
                }
            };

            PrinceControl p = PrinceControl.builder(PRINCE_PATH)
                    .events(e)
                    .noLocalFiles(true)
                    .start();

            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "control-nolocal.pdf")) {
                boolean result = p.newJob()
                        .convert(RESOURCES_DIR + "convert-nolocal.html", fos);
                assertTrue(result);
            } finally {
                p.stop();
            }
        }
    }
}
