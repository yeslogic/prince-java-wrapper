package com.princexml.wrapper;

import com.princexml.wrapper.enums.*;
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

import static org.junit.jupiter.api.Assertions.*;

// Note: running these tests creates an .attach_pid file in the project root.
// Appears to be a known Java bug: https://bugs.openjdk.java.net/browse/JDK-8214300.
@Disabled("Only used for end-to-end testing")
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
        final String INPUT_PATH = RESOURCES_DIR + "convert-1.html";

        Prince p;
        Events e;

        @BeforeEach
        void setUp() {
            e = new Events();
            p = new Prince(PRINCE_PATH, e);

            p.addStyleSheet(RESOURCES_DIR + "convert-1.css");
            p.setJavaScript(true);
        }

        @Test
        void testConvert1() throws IOException {
            boolean result = p.convert(INPUT_PATH);
            assertTrue(result, e.message);
        }

        @Test
        void testConvert2() throws IOException {
            boolean result = p.convert(INPUT_PATH, RESOURCES_DIR + "convert-2.pdf");
            assertTrue(result, e.message);
        }

        @Test
        void testConvert3() throws IOException {
            List<String> inputPaths = Arrays.asList(INPUT_PATH, INPUT_PATH);
            boolean result = p.convert(inputPaths, RESOURCES_DIR + "convert-3.pdf");
            assertTrue(result, e.message);
        }

        @Test
        void testConvert4() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convert-4.pdf")) {
                assertTrue(p.convert(INPUT_PATH, fos), e.message);
            }
        }

        @Test
        void testConvert5() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convert-5.pdf")) {
                List<String> inputPaths = Arrays.asList(INPUT_PATH, INPUT_PATH);
                assertTrue(p.convert(inputPaths, fos), e.message);
            }
        }

        @Test
        void testConvert6() throws IOException {
            try (FileInputStream fis = new FileInputStream(INPUT_PATH);
                 FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convert-6.pdf")
            ) {
                p.setInputType(InputType.HTML);
                assertTrue(p.convert(fis, fos), e.message);
            }
        }

        @Test
        void testConvertInputList1() throws IOException {
            File inputListFile = createInputListFile();
            boolean result = p.convertInputList(inputListFile.getAbsolutePath(),
                    RESOURCES_DIR + "convertinputlist-1.pdf");
            assertTrue(result, e.message);
        }

        @Test
        void testConvertInputList2() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convertinputlist-2.pdf")) {
                File inputListFile = createInputListFile();
                p.setInputType(InputType.HTML);
                assertTrue(p.convertInputList(inputListFile.getAbsolutePath(), fos), e.message);
            }
        }

        @Test
        void testConvertString1() throws IOException {
            String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH)), StandardCharsets.UTF_8);
            p.setInputType(InputType.HTML);
            assertTrue(p.convertString(input, RESOURCES_DIR + "convertstring-1.pdf"), e.message);
        }

        @Test
        void testConvertString2() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convertstring-2.pdf")) {
                String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH)), StandardCharsets.UTF_8);
                p.setInputType(InputType.HTML);
                assertTrue(p.convertString(input, fos), e.message);
            }
        }

        @Test
        void testRasterize1() throws IOException {
            assertTrue(p.rasterize(INPUT_PATH, RESOURCES_DIR + "rasterize-1-page%d.png"), e.message);
        }

        @Test
        void testRasterize2() throws IOException {
            List<String> inputPaths = Arrays.asList(INPUT_PATH, INPUT_PATH);
            assertTrue(p.rasterize(inputPaths, RESOURCES_DIR + "rasterize-2-page%d.png"), e.message);
        }

        @Test
        void testRasterize3() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterize-3.png")) {
                p.setRasterPage(1);
                p.setRasterFormat(RasterFormat.PNG);
                assertTrue(p.rasterize(INPUT_PATH, fos), e.message);
            }
        }

        @Test
        void testRasterize4() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterize-4.png")) {
                List<String> inputPaths = Arrays.asList(INPUT_PATH, INPUT_PATH);
                p.setRasterPage(2);
                p.setRasterFormat(RasterFormat.PNG);
                assertTrue(p.rasterize(inputPaths, fos), e.message);
            }
        }

        @Test
        void testRasterize5() throws IOException {
            try (FileInputStream fis = new FileInputStream(INPUT_PATH);
                 FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterize-5.png")
            ) {
                p.setInputType(InputType.HTML);
                p.setRasterPage(1);
                p.setRasterFormat(RasterFormat.PNG);
                assertTrue(p.rasterize(fis, fos), e.message);
            }
        }

        @Test
        void testRasterizeInputList1() throws IOException {
            File inputListFile = createInputListFile();
            assertTrue(p.rasterizeInputList(inputListFile.getAbsolutePath(), RESOURCES_DIR +
                    "rasterizeinputlist-1-page%d.png"), e.message);
        }

        @Test
        void testRasterizeInputList2() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterizeinputlist-2.png")) {
                File inputListFile = createInputListFile();
                p.setInputType(InputType.HTML);
                p.setRasterPage(2);
                p.setRasterFormat(RasterFormat.PNG);
                assertTrue(p.rasterizeInputList(inputListFile.getAbsolutePath(), fos), e.message);
            }
        }

        @Test
        void testRasterizeString1() throws IOException {
            String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH)), StandardCharsets.UTF_8);
            p.setInputType(InputType.HTML);
            assertTrue(p.rasterizeString(input, RESOURCES_DIR + "rasterizestring-1-page%d.png"), e.message);
        }

        @Test
        void testRasterizeString2() throws IOException {
            try (FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterizestring-2.png")) {
                String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH)), StandardCharsets.UTF_8);
                p.setInputType(InputType.HTML);
                p.setRasterPage(1);
                p.setRasterFormat(RasterFormat.PNG);
                assertTrue(p.rasterizeString(input, fos), e.message);
            }
        }

        @Test
        void testBaseOptionKeys() throws IOException {
            p.setVerbose(true);
            p.setDebug(true);
            // Creates a log file in project root.
            // p.setLog("x");
            p.setNoWarnCssUnknown(true);
            p.setNoWarnCssUnsupported(true);

            p.setNoLocalFiles(true);

            p.setNoNetwork(true);
            p.setNoRedirects(true);
            p.setAuthUser("x");
            p.setAuthPassword("x");
            p.setAuthServer("x");
            p.setAuthScheme(AuthScheme.HTTPS);
            p.addAuthMethod(AuthMethod.BASIC);
            p.addAuthMethod(AuthMethod.DIGEST);
            p.setNoAuthPreemptive(true);
            p.setHttpProxy("x");
            p.setHttpTimeout(100);
            p.addCookie("x");
            p.addCookie("y");
            p.setCookieJar("x");
            p.setSslCaCert("x");
            p.setSslCaPath("x");
            p.setSslCert("x");
            p.setSslCertType(SslType.DER);
            p.setSslKey("x");
            p.setSslKeyType(SslType.PEM);
            p.setSslKeyPassword("x");
            p.setSslVersion(SslVersion.TLSV1_3);
            p.setInsecure(true);
            p.setNoParallelDownloads(true);

            p.setLicenseFile("x");
            p.setLicenseKey("x");

            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) { // Doesn't matter.
                assertTrue(p.convert(INPUT_PATH, os), e.message);
            }
        }

        @Test
        void testFailSafe() throws IOException {
            p.setFailSafe(true);

            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) { // Doesn't matter.
                assertTrue(p.convert(INPUT_PATH, os), e.message);
            }
        }

        // Check keys.
        @Test
        void testJobOptionKeys() throws IOException {
            p.setInputType(InputType.HTML);
            p.setBaseUrl(".");
            p.addRemap("x", "y");
            p.addRemap("i", "j");
            p.setXInclude(true);
            p.setXmlExternalEntities(true);
            p.setIframes(true);

            p.setJavaScript(true);
            p.addScript("x");
            p.addScript("y");
            p.setMaxPasses(5);

            p.addStyleSheet("x");
            p.addStyleSheet("y");
            p.setMedia("x");
            p.setPageSize("x");
            p.setPageMargin("x");
            p.setNoAuthorStyle(true);
            p.setNoDefaultStyle(true);

            p.setPdfId("x");
            p.setPdfLang("x");
            p.setPdfProfile(PdfProfile.PDFA_1A);
            p.setPdfOutputIntent("x");
            p.setPdfScript("x");
            p.addPdfEventScript(PdfEvent.WILL_PRINT, "w");
            p.clearPdfEventScripts();
            p.addPdfEventScript(PdfEvent.WILL_CLOSE, "x");
            p.addPdfEventScript(PdfEvent.WILL_CLOSE, "y");
            p.addPdfEventScript(PdfEvent.DID_PRINT, "z");
            p.addFileAttachment("x");
            p.addFileAttachment("y");
            p.setNoArtificialFonts(true);
            p.setNoEmbedFonts(true);
            p.setNoSubsetFonts(true);
            // Fails conversion if enabled, due to being unable to find any fonts.
            // p.setNoSystemFonts(true);
            p.setForceIdentityEncoding(true);
            p.setNoCompress(true);
            p.setNoObjectStreams(true);
            p.setConvertColors(true);
            p.setFallbackCmykProfile("x");
            p.setTaggedPdf(true);
            p.setPdfForms(true);
            p.setCssDpi(100);

            p.setPdfTitle("x");
            p.setPdfSubject("x");
            p.setPdfAuthor("x");
            p.setPdfKeywords("x");
            p.setPdfCreator("x");
            p.setXmp("x");

            p.setEncrypt(true);
            p.setKeyBits(KeyBits.BITS40);
            p.setUserPassword("x");
            p.setOwnerPassword("x");
            p.setDisallowPrint(true);
            p.setDisallowCopy(true);
            p.setAllowCopyForAccessibility(true);
            p.setDisallowAnnotate(true);
            p.setDisallowModify(true);
            p.setAllowAssembly(true);

            p.setRasterFormat(RasterFormat.JPEG);
            p.setRasterJpegQuality(100);
            p.setRasterPage(100);
            p.setRasterDpi(100);
            p.setRasterThreads(100);
            p.setRasterBackground(RasterBackground.WHITE);

            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) { // Doesn't matter.
                assertTrue(p.convert(INPUT_PATH, os), e.message);
            }
        }

        File createInputListFile() throws IOException {
            File inputListFile = File.createTempFile("input-list", ".txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(inputListFile));
            bw.write(INPUT_PATH);
            bw.newLine();
            bw.write(INPUT_PATH);
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
                    assertTrue(msgLocation.contains("convert-1.css"));
                    assertEquals("not loading local file", msgText);
                }

                @Override
                public void onDataMessage(String name, String value) {}
            };
            Prince p = new Prince(PRINCE_PATH, e);
            p.setNoLocalFiles(true);

            boolean result = p.convert(RESOURCES_DIR + "convert-nolocal.html");
            assertTrue(result);
        }
    }
}
