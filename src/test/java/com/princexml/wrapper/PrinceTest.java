package com.princexml.wrapper;

import com.princexml.wrapper.enums.InputType;
import com.princexml.wrapper.enums.RasterFormat;
import com.princexml.wrapper.events.MessageType;
import com.princexml.wrapper.events.PrinceEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("for local testing")
class PrinceTest {
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

        Prince p;
        Events e;

        @BeforeEach
        void setUp() {
            e = new Events();
            p = Prince.builder(PRINCE_PATH)
                    .events(e)
                    .failSafe(true)
                    .noNetwork(true)
                    .build();
        }

        @Test
        void testConvert1() throws IOException {
            boolean result = p.newJob()
                    .javaScript(true)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css")
                    .convert(INPUT_PATH1);
            assertTrue(result, e.message);
        }

        @Test
        void testConvert2() throws IOException {
            boolean result = p.newJob()
                    .javaScript(true)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css")
                    .convert(INPUT_PATH1, RESOURCES_DIR + "convert-2.pdf");
            assertTrue(result, e.message);
        }

        @Test
        void testConvert3() throws IOException {
            List<String> inputPaths = Arrays.asList(INPUT_PATH1, INPUT_PATH2);
            boolean result = p.newJob()
                    .javaScript(true)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css")
                    .convert(inputPaths, RESOURCES_DIR + "convert-3.pdf");
            assertTrue(result, e.message);
        }

        @Test
        void testConvert4() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convert-4.pdf")) {
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .convert(INPUT_PATH1, fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testConvert5() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convert-5.pdf")) {
                List<String> inputPaths = Arrays.asList(INPUT_PATH1, INPUT_PATH2);
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .convert(inputPaths, fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testConvert6() throws IOException {
            try (FileInputStream fis = new FileInputStream(INPUT_PATH1);
                 FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convert-6.pdf")
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
        void testConvertMultiple1() throws IOException {
            boolean result1 = p.newJob()
                    .styleSheet(RESOURCES_DIR + "convert-1a.css")
                    .convert(INPUT_PATH1, RESOURCES_DIR + "convert-mult1a.pdf");
            assertTrue(result1, e.message);

            boolean result2 = p.newJob()
                    .javaScript(true)
                    .convert(INPUT_PATH2, RESOURCES_DIR + "convert-mult1b.pdf");
            assertTrue(result2, e.message);
        }

        @Test
        void testConvertMultiple2() throws IOException {
            Prince.Job job = p.newJob()
                    .javaScript(true)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css");

            boolean result1 = job
                    .convert(INPUT_PATH1, RESOURCES_DIR + "convert-mult2a.pdf");
            assertTrue(result1, e.message);

            boolean result2 = job
                    .convert(INPUT_PATH2, RESOURCES_DIR + "convert-mult2b.pdf");
            assertTrue(result2, e.message);
        }

        @Test
        void testConvertMultiple3() throws IOException {
            Prince.Job job1 = p.newJob()
                    .javaScript(true);
            // job2's stylesheet shouldn't leak into job1.
            Prince.Job job2 = p.newJob(job1)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css");
            // job2's options should carry over into job3.
            Prince.Job job3 = p.newJob(job2)
                    .styleSheet(RESOURCES_DIR + "convert-1b.css");

            boolean result1 = job1
                    .convert(INPUT_PATH1, RESOURCES_DIR + "convert-mult3a.pdf");
            assertTrue(result1, e.message);

            boolean result2 = job2
                    .convert(INPUT_PATH2, RESOURCES_DIR + "convert-mult3b.pdf");
            assertTrue(result2, e.message);

            boolean result3 = job3
                    .convert(INPUT_PATH1, RESOURCES_DIR + "convert-mult3c.pdf");
            assertTrue(result3, e.message);
        }

        @Test
        void testConvertInputList1() throws IOException {
            File inputListFile = createInputListFile();
            boolean result = p.newJob()
                    .javaScript(true)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css")
                    .convertInputList(inputListFile.getAbsolutePath(),
                            RESOURCES_DIR + "convertinputlist-1.pdf");
            assertTrue(result, e.message);
        }

        @Test
        void testConvertInputList2() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convertinputlist-2.pdf")) {
                File inputListFile = createInputListFile();
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .convertInputList(inputListFile.getAbsolutePath(), fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testConvertString1() throws IOException {
            String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH1)), StandardCharsets.UTF_8);
            boolean result = p.newJob()
                    .javaScript(true)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css")
                    .inputType(InputType.HTML)
                    .convertString(input, RESOURCES_DIR + "convertstring-1.pdf");
            assertTrue(result, e.message);
        }

        @Test
        void testConvertString2() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convertstring-2.pdf")) {
                String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH1)), StandardCharsets.UTF_8);
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .inputType(InputType.HTML)
                        .convertString(input, fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testRasterize1() throws IOException {
            boolean result = p.newJob()
                    .javaScript(true)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css")
                    .rasterize(INPUT_PATH1, RESOURCES_DIR + "rasterize-1-page%d.png");
            assertTrue(result, e.message);
        }

        @Test
        void testRasterize2() throws IOException {
            List<String> inputPaths = Arrays.asList(INPUT_PATH1, INPUT_PATH2);
            boolean result = p.newJob()
                    .javaScript(true)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css")
                    .rasterize(inputPaths, RESOURCES_DIR + "rasterize-2-page%d.png");
            assertTrue(result, e.message);
        }

        @Test
        void testRasterize3() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterize-3.png")) {
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .rasterPage(1)
                        .rasterFormat(RasterFormat.PNG)
                        .rasterize(INPUT_PATH1, fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testRasterize4() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterize-4.jpeg")) {
                List<String> inputPaths = Arrays.asList(INPUT_PATH1, INPUT_PATH2);
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .rasterPage(2)
                        .rasterFormat(RasterFormat.JPEG)
                        .rasterize(inputPaths, fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testRasterize5() throws IOException {
            try (FileInputStream fis = new FileInputStream(INPUT_PATH2);
                 FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterize-5.jpeg")
            ) {
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .inputType(InputType.HTML)
                        .rasterPage(1)
                        .rasterFormat(RasterFormat.JPEG)
                        .rasterize(fis, fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testRasterizeInputList1() throws IOException {
            File inputListFile = createInputListFile();
            boolean result = p.newJob()
                    .javaScript(true)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css")
                    .rasterizeInputList(inputListFile.getAbsolutePath(), RESOURCES_DIR +
                                    "rasterizeinputlist-1-page%d.png");
            assertTrue(result, e.message);
        }

        @Test
        void testRasterizeInputList2() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterizeinputlist-2.png")) {
                File inputListFile = createInputListFile();
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .inputType(InputType.HTML)
                        .rasterPage(2)
                        .rasterFormat(RasterFormat.PNG)
                        .rasterizeInputList(inputListFile.getAbsolutePath(), fos);
                assertTrue(result, e.message);
            }
        }

        @Test
        void testRasterizeString1() throws IOException {
            String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH1)),
                    StandardCharsets.UTF_8);
            boolean result = p.newJob()
                    .javaScript(true)
                    .styleSheet(RESOURCES_DIR + "convert-1a.css")
                    .inputType(InputType.HTML)
                    .rasterizeString(input, RESOURCES_DIR + "rasterizestring-1-page%d.png");
            assertTrue(result, e.message);
        }

        @Test
        void testRasterizeString2() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterizestring-2.png")) {
                String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH2)),
                        StandardCharsets.UTF_8);
                boolean result = p.newJob()
                        .javaScript(true)
                        .styleSheet(RESOURCES_DIR + "convert-1a.css")
                        .inputType(InputType.HTML)
                        .rasterPage(1)
                        .rasterFormat(RasterFormat.PNG)
                        .rasterizeString(input, fos);
                assertTrue(result, e.message);
            }
        }

        File createInputListFile() throws IOException {
            File inputListFile = File.createTempFile("input-list", ".txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(inputListFile));
            bw.write(INPUT_PATH1);
            bw.newLine();
            bw.write(INPUT_PATH2);
            bw.newLine();
            bw.close();
            inputListFile.deleteOnExit();

            return inputListFile;
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
                public void onDataMessage(String name, String value) {}
            };

            Prince p = Prince.builder(PRINCE_PATH)
                    .events(e)
                    .noLocalFiles(true)
                    .build();

            boolean result = p.newJob()
                    .convert(RESOURCES_DIR + "convert-nolocal.html");
            assertTrue(result);
        }
    }
}
