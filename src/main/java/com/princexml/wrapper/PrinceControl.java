/*
 * Copyright (C) 2015, 2018, 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import com.princexml.wrapper.enums.InputType;
import com.princexml.wrapper.events.PrinceEvents;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.princexml.wrapper.CommandLine.toCommand;

public class PrinceControl extends AbstractPrince {
    private Process process;
    private String version;
    private final List<String> inputPaths;
    private final List<byte[]> resources;

    public PrinceControl(String princePath) {
        this(princePath, null);
    }

    public PrinceControl(String princePath, PrinceEvents events) {
        super(princePath, events);
        this.inputPaths = new ArrayList<>();
        this.resources = new ArrayList<>();
    }

    @Override
    public boolean convert(String inputPath, OutputStream output) throws IOException {
        inputPaths.add(inputPath);
        return convert(output);
    }

    @Override
    public boolean convert(List<String> inputPaths, OutputStream output) throws IOException {
        this.inputPaths.addAll(inputPaths);
        return convert(output);
    }

    @Override
    public boolean convert(InputStream input, OutputStream output) throws IOException {
        if (inputType == null || inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Util.copyInputToOutput(input, baos);
        addResource(baos.toByteArray());

        return convert(output);
    }

    @Override
    public boolean convertString(String input, OutputStream output) throws IOException {
        if (inputType == null || inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }

        addResource(input.getBytes(StandardCharsets.UTF_8));
        return convert(output);
    }

    private boolean convert(OutputStream output) throws IOException {
        if (process == null) {
            throw new RuntimeException("control process has not been started");
        }

        OutputStream toPrince = process.getOutputStream();
        InputStream fromPrince = process.getInputStream();

        Chunk.writeChunk(toPrince, "job", getJobJson());
        for (byte[] r : resources) {
            Chunk.writeChunk(toPrince, "dat", r);
        }
        toPrince.flush();

        Chunk chunk = Chunk.readChunk(fromPrince);
        if (chunk.getTag().equals("pdf")) {
            output.write(chunk.getBytes());
            chunk = Chunk.readChunk(fromPrince);
        }

        if (chunk.getTag().equals("log")) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(chunk.getBytes())));
            return readMessages(br);
        } else if (chunk.getTag().equals("err")) {
            throw new IOException("error: " + chunk.getString());
        } else {
            throw new IOException("unknown chunk: " + chunk.getTag());
        }
    }

    public void start() throws IOException {
        if (process != null) {
            throw new RuntimeException("control process has already been started");
        }

        List<String> cmdLine = getBaseCommandLine();
        cmdLine.add(toCommand("control"));

        process = Util.invokeProcess(cmdLine);

        InputStream fromPrince = process.getInputStream();
        Chunk chunk = Chunk.readChunk(fromPrince);

        if (chunk.getTag().equals("ver")) {
            version = chunk.getString();
        } else if (chunk.getTag().equals("err")) {
            throw new IOException("error: " + chunk.getString());
        } else {
            throw new IOException("unknown chunk: " + chunk.getTag());
        }
    }

    public void stop() throws IOException {
        if (process == null) {
            throw new RuntimeException("control process has not been started");
        }

        OutputStream toPrince = process.getOutputStream();
        InputStream fromPrince = process.getInputStream();

        Chunk.writeChunk(toPrince, "end", "");
        toPrince.close();
        fromPrince.close();
        process.destroy();
    }

    private String getJobJson() {
        Json json = new Json();

        json.beginObj();

        json.beginObj("input");

        json.beginList("src");
        inputPaths.forEach(json::value);
        json.endList();

        if (inputType != null) { json.field("type", inputType.toString()); }
        if (baseUrl != null) { json.field("base", baseUrl); }
        if (media != null) { json.field("media", media); }

        json.beginList("styles");
        styleSheets.forEach(json::value);
        json.endList();

        json.beginList("scripts");
        scripts.forEach(json::value);
        json.endList();

        json.field("default-style", !noDefaultStyle);
        json.field("author-style", !noAuthorStyle);
        json.field("javascript", javaScript);
        if (maxPasses > 0) { json.field("max-passes", maxPasses); }
        json.field("xinclude", xInclude);
        json.field("xml-external-entities", xmlExternalEntities);
        json.endObj();

        json.beginObj("pdf");
        json.field("embed-fonts", !noEmbedFonts);
        json.field("subset-fonts", !noSubsetFonts);
        json.field("artificial-fonts", !noArtificialFonts);
        json.field("force-identity-encoding", forceIdentityEncoding);
        json.field("compress", !noCompress);
        json.field("object-streams", !noObjectStreams);

        json.beginObj("encrypt");
        if (keyBits != null) { json.field("key-bits", keyBits.toString()); }
        if (userPassword != null) { json.field("user-password", userPassword); }
        if (ownerPassword != null) { json.field("owner-password", ownerPassword); }
        json.field("disallow-print", disallowPrint);
        json.field("disallow-modify", disallowModify);
        json.field("disallow-copy", disallowCopy);
        json.field("disallow-annotate", disallowAnnotate);
        json.field("allow-copy-for-accessibility", allowCopyForAccessibility);
        json.field("allow-assembly", allowAssembly);
        json.endObj();

        if (pdfProfile != null) { json.field("pdf-profile", pdfProfile.toString()); }
        if (pdfOutputIntent != null) { json.field("pdf-output-intent", pdfOutputIntent); }
        if (fallbackCmykProfile != null) { json.field("fallback-cmyk-profile", fallbackCmykProfile); }
        json.field("color-conversion", convertColors ? "output-intent" : "none");
        if (pdfId != null) { json.field("pdf-id", pdfId); }
        if (pdfLang != null) { json.field("pdf-lang", pdfLang); }
        if (xmp != null) { json.field("pdf-xmp", xmp); }
        json.field("tagged-pdf", taggedPdf);

        json.beginList("attach");
        for (FileAttachment fa : fileAttachments) {
            json.beginObj();
            json.field("url", fa.url);
            if (fa.filename != null) { json.field("filename", fa.filename); }
            if (fa.description != null) { json.field("description", fa.description); }
            json.endObj();
        }
        json.endList();

        json.endObj();

        json.beginObj("metadata");
        if (pdfTitle != null) { json.field("title", pdfTitle); }
        if (pdfSubject != null) { json.field("subject", pdfSubject); }
        if (pdfAuthor != null) { json.field("author", pdfAuthor); }
        if (pdfKeywords != null) { json.field("keywords", pdfKeywords); }
        if (pdfCreator != null) { json.field("creator", pdfCreator); }
        json.endObj();

        json.field("job-resource-count", resources.size());

        json.endObj();

        return json.toString();
    }

    public String getVersion() {
        return version;
    }

    public void addScript(byte[] script) {
        resources.add(script);
        super.addScript("job-resource:" + (resources.size() - 1));
    }

    public void addStyleSheet(byte[] styleSheet) {
        resources.add(styleSheet);
        super.addStyleSheet("job-resource:" + (resources.size() - 1));
    }

    public void addFileAttachment(byte[] attachment, String filename, String description) {
        resources.add(attachment);
        super.fileAttachments.add(new FileAttachment(
                "job-resource:" + (resources.size() - 1),
                filename,
                description
        ));
    }

    private void addResource(byte[] resource) {
        resources.add(resource);
        inputPaths.add("job-resource:" + (resources.size() - 1));
    }
}
