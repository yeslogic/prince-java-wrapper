/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import com.princexml.wrapper.enums.*;
import com.princexml.wrapper.events.PrinceEvents;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.princexml.wrapper.CommandLine.*;

public class Prince extends AbstractPrince {
    // Input options.
    private final List<String> remaps = new ArrayList<>();
    private boolean noLocalFiles;

    // CSS options.
    private String pageSize;
    private String pageMargin;

    // PDF output options.
    private boolean noSystemFonts;
    private int cssDpi;

    // Raster output options.
    private String rasterOutput;
    private RasterFormat rasterFormat;
    private int rasterJpegQuality = -1;
    private int rasterPages;
    private int rasterDpi;
    private int rasterThreads = -1;
    private RasterBackground rasterBackground;

    // Additional options.
    private final Map<String, String> options = new LinkedHashMap<>();

    public Prince(String princePath) {
        super(princePath);
    }

    public Prince(String princePath, PrinceEvents events) {
        super(princePath, events);
    }

    public boolean convert(String xmlPath) throws IOException {
        return convertInternal(Collections.singletonList(xmlPath), null);
    }

    public boolean convert(String xmlPath, String pdfPath) throws IOException {
        return convertInternal(Collections.singletonList(xmlPath), pdfPath);
    }

    public boolean convert(List<String> xmlPaths, String pdfPath) throws IOException {
        return convertInternal(xmlPaths, pdfPath);
    }

    private boolean convertInternal(List<String> xmlPaths, String pdfPath) throws IOException {
        List<String> cmdLine = getJobCommandLine("normal");
        cmdLine.addAll(xmlPaths);
        if (pdfPath != null) {
            cmdLine.add(toCommand("output", pdfPath));
        }

        Process process = Util.invokeProcess(cmdLine);

        return readMessagesFromStderr(process);
    }

    @Override
    public boolean convert(String xmlPath, OutputStream out) throws IOException {
        return convert(Collections.singletonList(xmlPath), out);
    }

    @Override
    public boolean convert(List<String> xmlPaths, OutputStream out) throws IOException {
        List<String> cmdLine = getJobCommandLine("buffered");
        cmdLine.addAll(xmlPaths);
        cmdLine.add(toCommand("output", "-"));

        Process process = Util.invokeProcess(cmdLine);
        InputStream fromPrince = process.getInputStream();

        Util.copyInputToOutput(fromPrince, out);
        fromPrince.close();

        return readMessagesFromStderr(process);
    }

    @Override
    public boolean convert(InputStream in, OutputStream out) throws IOException {
        List<String> cmdLine = getJobCommandLine("buffered");
        cmdLine.add("-");

        Process process = Util.invokeProcess(cmdLine);
        OutputStream toPrince = process.getOutputStream();
        InputStream fromPrince = process.getInputStream();

        Util.copyInputToOutput(in, toPrince);
        toPrince.close();

        Util.copyInputToOutput(fromPrince, out);
        fromPrince.close();

        return readMessagesFromStderr(process);
    }

    public boolean convertString(String xml, String pdfPath) throws IOException {
        List<String> cmdLine = getJobCommandLine("buffered");
        cmdLine.add(toCommand("output", pdfPath));
        cmdLine.add("-");

        Process process = Util.invokeProcess(cmdLine);
        OutputStream toPrince = process.getOutputStream();

        toPrince.write(xml.getBytes(StandardCharsets.UTF_8));
        toPrince.close();

        return readMessagesFromStderr(process);
    }

    @Override
    public boolean convertString(String xml, OutputStream out) throws IOException {
        InputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

        boolean result = convert(in, out);
        in.close();

        return result;
    }

    private List<String> getJobCommandLine(String logType) {
        List<String> cmdLine = getBaseCommandLine();

        cmdLine.add(toCommand("structured-log", logType));

        if (inputType != null) { cmdLine.add(toCommand("input", inputType)); }
        if (baseUrl != null) { cmdLine.add(toCommand("baseurl", baseUrl)); }
        if (!remaps.isEmpty()) { cmdLine.addAll(toCommands("remap", remaps)); }
        if (xInclude) { cmdLine.add(toCommand("xinclude")); }
        if (xmlExternalEntities) { cmdLine.add(toCommand("xml-external-entities")); }
        if (noLocalFiles) { cmdLine.add(toCommand("no-local-files")); }

        if (javaScript) { cmdLine.add(toCommand("javascript")); }
        if (!scripts.isEmpty()) { cmdLine.addAll(toCommands("script", scripts)); }
        if (maxPasses > 0) { cmdLine.add(toCommand("max-passes", maxPasses)); }

        if (!styleSheets.isEmpty()) { cmdLine.addAll(toCommands("style", styleSheets)); }
        if (media != null) { cmdLine.add(toCommand("media", media)); }
        if (pageSize != null) { cmdLine.add(toCommand("page-size", pageSize)); }
        if (pageMargin != null) { cmdLine.add(toCommand("page-margin", pageMargin)); }
        if (noAuthorStyle) { cmdLine.add(toCommand("no-author-style")); }
        if (noDefaultStyle) { cmdLine.add(toCommand("no-default-style")); }

        if (pdfId != null) { cmdLine.add(toCommand("pdf-id", pdfId)); }
        if (pdfLang != null) { cmdLine.add(toCommand("pdf-lang", pdfLang)); }
        if (pdfProfile != null) { cmdLine.add(toCommand("pdf-profile", pdfProfile)); }
        if (pdfOutputIntent != null) { cmdLine.add(toCommand("pdf-output-intent", pdfOutputIntent)); }
        if (!fileAttachments.isEmpty()) { cmdLine.addAll(toCommands("attach", fileAttachments)); }
        if (noArtificialFonts) { cmdLine.add(toCommand("no-artificial-fonts")); }
        if (noEmbedFonts) { cmdLine.add(toCommand("no-embed-fonts")); }
        if (noSubsetFonts) { cmdLine.add(toCommand("no-subset-fonts")); }
        if (noSystemFonts) { cmdLine.add(toCommand("no-system-fonts")); }
        if (forceIdentityEncoding) { cmdLine.add(toCommand("force-identity-encoding")); }
        if (noCompress) { cmdLine.add(toCommand("no-compress")); }
        if (noObjectStreams) { cmdLine.add(toCommand("no-object-streams")); }
        if (convertColors) { cmdLine.add(toCommand("convert-colors")); }
        if (fallbackCmykProfile != null) { cmdLine.add(toCommand("fallback-cmyk-profile", fallbackCmykProfile)); }
        if (taggedPdf) { cmdLine.add(toCommand("tagged-pdf")); }
        if (cssDpi > 0) {cmdLine.add(toCommand("css-dpi", cssDpi)); }

        if (pdfTitle != null) { cmdLine.add(toCommand("pdf-title", pdfTitle)); }
        if (pdfSubject != null) { cmdLine.add(toCommand("pdf-subject", pdfSubject)); }
        if (pdfAuthor != null) { cmdLine.add(toCommand("pdf-author", pdfAuthor)); }
        if (pdfKeywords != null) { cmdLine.add(toCommand("pdf-keywords", pdfKeywords)); }
        if (pdfCreator != null) { cmdLine.add(toCommand("pdf-creator", pdfCreator)); }
        if (xmp != null) { cmdLine.add(toCommand("pdf-xmp", xmp)); }

        if (encrypt) { cmdLine.add(toCommand("encrypt")); }
        if (keyBits != null) { cmdLine.add(toCommand("key-bits", keyBits)); }
        if (userPassword != null) { cmdLine.add(toCommand("user-password", userPassword)); }
        if (ownerPassword != null) { cmdLine.add(toCommand("owner-password", ownerPassword)); }
        if (disallowPrint) { cmdLine.add(toCommand("disallow-print")); }
        if (disallowCopy) { cmdLine.add(toCommand("disallow-copy")); }
        if (allowCopyForAccessibility) { cmdLine.add(toCommand("allow-copy-for-accessibility")); }
        if (disallowAnnotate) { cmdLine.add(toCommand("disallow-annotate")); }
        if (disallowModify) { cmdLine.add(toCommand("disallow-modify")); }
        if (allowAssembly) { cmdLine.add(toCommand("allow-assembly")); }

        if (rasterOutput != null) { cmdLine.add(toCommand("raster-output", rasterOutput)); }
        if (rasterFormat != null) { cmdLine.add(toCommand("raster-format", rasterFormat)); }
        if (rasterJpegQuality > -1) { cmdLine.add(toCommand("raster-jpeg-quality", rasterJpegQuality)); }
        if (rasterPages > 0) { cmdLine.add(toCommand("raster-pages", rasterPages)); }
        if (rasterDpi > 0) { cmdLine.add(toCommand("raster-dpi", rasterDpi)); }
        if (rasterThreads > -1) { cmdLine.add(toCommand("raster-threads", rasterThreads)); }
        if (rasterBackground != null) { cmdLine.add(toCommand("raster-background", rasterBackground)); }

        options.forEach((k, v) -> cmdLine.add(toCommand(k, v)));

        return cmdLine;
    }

    private boolean readMessagesFromStderr(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        return readMessages(reader);
    }

    //region Input options.
    public void addRemap(String remap) {
        this.remaps.add(remap);
    }

    public void clearRemaps() {
        this.remaps.clear();
    }

    public void setNoLocalFiles(boolean noLocalFiles) {
        this.noLocalFiles = noLocalFiles;
    }
    //endregion

    //region CSS options.
    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageMargin(String pageMargin) {
        this.pageMargin = pageMargin;
    }
    //endregion

    //region PDF output options.
    public void setNoSystemFonts(boolean noSystemFonts) {
        this.noSystemFonts = noSystemFonts;
    }

    public void setCssDpi(int cssDpi) {
        if (cssDpi < 1) {
            throw new IllegalArgumentException("invalid cssDpi value (must be > 0)");
        }
        this.cssDpi = cssDpi;
    }
    //endregion

    //region Raster output options.
    public void setRasterOutput(String rasterOutput) {
        this.rasterOutput = rasterOutput;
    }

    public void setRasterFormat(RasterFormat rasterFormat) {
        this.rasterFormat = rasterFormat;
    }

    public void setRasterJpegQuality(int rasterJpegQuality) {
        if (rasterJpegQuality < 0 || rasterJpegQuality > 100) {
            throw new IllegalArgumentException("invalid rasterJpegQuality value (must be [0, 100])");
        }
        this.rasterJpegQuality = rasterJpegQuality;
    }

    public void setRasterPages(int rasterPages) {
        if (rasterPages < 1) {
            throw new IllegalArgumentException("invalid rasterPages value (must be > 0)");
        }
        this.rasterPages = rasterPages;
    }

    public void setRasterDpi(int rasterDpi) {
        if (rasterDpi < 1) {
            throw new IllegalArgumentException("invalid rasterDpi value (must be > 0)");
        }
        this.rasterDpi = rasterDpi;
    }

    public void setRasterThreads(int rasterThreads) {
        this.rasterThreads = rasterThreads;
    }

    public void setRasterBackground(RasterBackground rasterBackground) {
        this.rasterBackground = rasterBackground;
    }
    //endregion

    //region Additional options.
    public void addOption(String key, String value) {
        this.options.put(key, value);
    }

    public void clearOptions() {
        this.options.clear();
    }
    //endregion
}
