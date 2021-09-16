package com.princexml.wrapper;

import com.princexml.wrapper.enums.InputType;
import com.princexml.wrapper.enums.KeyBits;
import com.princexml.wrapper.enums.PdfProfile;
import com.princexml.wrapper.events.MessageType;
import com.princexml.wrapper.events.PrinceEvents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Only used for end-to-end testing")
class PrinceControlTest {
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

    PrinceControl p;
    Events e;

    @BeforeEach
    void setUp() throws IOException {
        e = new Events();
        p = new PrinceControl(PRINCE_PATH, e);

        p.addStyleSheet(RESOURCES_DIR + "convert-1.css");
        p.setJavaScript(true);
        p.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        p.stop();
    }

    @Test
    void testConvert1() throws IOException {
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "control-convert-1.pdf");
        boolean result = p.convert(INPUT_PATH, fos);
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testConvert2() throws IOException {
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "control-convert-2.pdf");
        List<String> inputPaths = Arrays.asList(INPUT_PATH, INPUT_PATH);
        boolean result = p.convert(inputPaths, fos);
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testConvert3() throws IOException {
        FileInputStream fis = new FileInputStream(INPUT_PATH);
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "control-convert-3.pdf");
        p.setInputType(InputType.HTML);
        boolean result = p.convert(fis, fos);
        fis.close();
        fos.close();
        assertTrue(result, e.message);
    }

    @Test
    void testConvertString() throws IOException {
        byte[] bs = Files.readAllBytes(Paths.get(INPUT_PATH));
        FileOutputStream fos = new FileOutputStream(RESOURCES_DIR + "control-convertstring.pdf");
        p.setInputType(InputType.HTML);
        boolean result = p.convertString(new String(bs, StandardCharsets.UTF_8), fos);
        assertTrue(result, e.message);
    }

    // Check JSON.
    @Test
    void testJobOptionKeys() throws IOException {
        p.setInputType(InputType.HTML);
        p.setBaseUrl("x");
        p.setMedia("x");
        p.addStyleSheet("x");
        p.addStyleSheet("y");
        p.addScript("x");
        p.addScript("y");
        p.setNoDefaultStyle(true);
        p.setNoAuthorStyle(true);
        p.setJavaScript(true);
        p.setMaxPasses(5);
        p.setXInclude(true);
        p.setXmlExternalEntities(true);

        p.setNoEmbedFonts(true);
        p.setNoSubsetFonts(true);
        p.setNoArtificialFonts(true);
        p.setForceIdentityEncoding(true);
        p.setNoCompress(true);
        p.setNoObjectStreams(true);

        p.setKeyBits(KeyBits.BITS40);
        p.setUserPassword("x");
        p.setOwnerPassword("x");
        p.setDisallowPrint(true);
        p.setDisallowModify(true);
        p.setDisallowCopy(true);
        p.setDisallowAnnotate(true);
        p.setAllowCopyForAccessibility(true);
        p.setAllowAssembly(true);

        p.setPdfProfile(PdfProfile.PDFA_1A_AND_PDFUA_1);
        p.setPdfOutputIntent("x");
        p.setFallbackCmykProfile("x");
        p.setConvertColors(true);
        p.setPdfId("x");
        p.setPdfLang("x");
        p.setXmp("x");
        p.setTaggedPdf(true);

        p.addFileAttachment("x");
        p.addFileAttachment(new byte[] {0}, "x", "y");

        p.setPdfTitle("x");
        p.setPdfSubject("x");
        p.setPdfAuthor("x");
        p.setPdfKeywords("x");
        p.setPdfCreator("x");

        ByteArrayOutputStream os = new ByteArrayOutputStream(); // Doesn't matter.
        boolean result = p.convert(INPUT_PATH, os);
        os.close();
        assertTrue(result, e.message);
    }
}
