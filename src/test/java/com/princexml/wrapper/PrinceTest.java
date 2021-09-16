package com.princexml.wrapper;

import com.princexml.wrapper.enums.*;
import com.princexml.wrapper.events.MessageType;
import com.princexml.wrapper.events.PrinceEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
            message = msgType.name() + " " + msgLocation + " " + msgText;
        }

        @Override
        public void onDataMessage(String name, String value) {
            message = "DAT " + name + " " + value;
        }
    }

    // Edit path accordingly.
    static final String PRINCE_PATH = "path/to/prince";
    static final String RESOURCES_DIR = System.getProperty("user.dir") +
            "/src/test/java/com/princexml/wrapper/resources/";
    static final String INPUT_PATH = RESOURCES_DIR + "convert-1.html";

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
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convert-4.pdf");
        boolean result = p.convert(INPUT_PATH, fos);
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testConvert5() throws IOException {
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convert-5.pdf");
        List<String> inputPaths = Arrays.asList(INPUT_PATH, INPUT_PATH);
        boolean result = p.convert(inputPaths, fos);
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testConvert6() throws IOException {
        FileInputStream fis = new FileInputStream(INPUT_PATH);
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convert-6.pdf");
        p.setInputType(InputType.HTML);
        boolean result = p.convert(fis, fos);
        fis.close();
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testConvertString1() throws IOException {
        byte[] bs = Files.readAllBytes(Paths.get(INPUT_PATH));
        p.setInputType(InputType.HTML);
        boolean result = p.convertString(new String(bs, StandardCharsets.UTF_8), RESOURCES_DIR + "convertstring-1.pdf");
        assertTrue(result, e.message);
    }

    @Test
    void testConvertString2() throws IOException {
        byte[] bs = Files.readAllBytes(Paths.get(INPUT_PATH));
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "convertstring-2.pdf");
        p.setInputType(InputType.HTML);
        boolean result = p.convertString(new String(bs, StandardCharsets.UTF_8), fos);
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testRasterize1() throws IOException {
        boolean result = p.rasterize(INPUT_PATH, RESOURCES_DIR + "rasterize-1-page%d.png");
        assertTrue(result, e.message);
    }

    @Test
    void testRasterize2() throws IOException {
        List<String> inputPaths = Arrays.asList(INPUT_PATH, INPUT_PATH);
        boolean result = p.rasterize(inputPaths, RESOURCES_DIR + "rasterize-2-page%d.png");
        assertTrue(result, e.message);
    }

    @Test
    void testRasterize3() throws IOException {
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterize-3.png");
        p.setRasterPage(1);
        p.setRasterFormat(RasterFormat.PNG);
        boolean result = p.rasterize(INPUT_PATH, fos);
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testRasterize4() throws IOException {
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterize-4.png");
        List<String> inputPaths = Arrays.asList(INPUT_PATH, INPUT_PATH);
        p.setRasterPage(2);
        p.setRasterFormat(RasterFormat.PNG);
        boolean result = p.rasterize(inputPaths, fos);
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testRasterize5() throws IOException {
        FileInputStream fis = new FileInputStream(INPUT_PATH);
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterize-5.png");
        p.setInputType(InputType.HTML);
        p.setRasterPage(1);
        p.setRasterFormat(RasterFormat.PNG);
        boolean result = p.rasterize(fis, fos);
        fis.close();
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testRasterizeString1() throws IOException {
        byte[] bs = Files.readAllBytes(Paths.get(INPUT_PATH));
        p.setInputType(InputType.HTML);
        boolean result = p.rasterizeString(new String(bs, StandardCharsets.UTF_8), RESOURCES_DIR + "rasterizestring-1-page%d.png");
        assertTrue(result, e.message);
    }

    @Test
    void testRasterizeString2() throws IOException {
        byte[] bs = Files.readAllBytes(Paths.get(INPUT_PATH));
        p.setInputType(InputType.HTML);
        p.setRasterPage(1);
        p.setRasterFormat(RasterFormat.PNG);
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "rasterizestring-2.png");
        boolean result = p.rasterizeString(new String(bs, StandardCharsets.UTF_8), fos);
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testBaseOptionKeys() throws IOException {
        p.setVerbose(true);
        p.setDebug(true);
        // Creates a log file in project root.
        // p.setLog("x");
        p.setNoWarnCssUnknown(true);
        p.setNoWarnCssUnsupported(true);

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

        ByteArrayOutputStream os = new ByteArrayOutputStream(); // Doesn't matter.
        boolean result = p.convert(INPUT_PATH, os);
        os.close();
        assertTrue(result, e.message);
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
        p.setNoLocalFiles(true);

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

        ByteArrayOutputStream os = new ByteArrayOutputStream(); // Doesn't matter.
        boolean result = p.convert(INPUT_PATH, os);
        os.close();
        assertTrue(result, e.message);
    }
}
