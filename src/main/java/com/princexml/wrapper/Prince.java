/*
 * Copyright (C) 2005-2006, 2010, 2012, 2014-2015, 2018, 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import com.princexml.wrapper.enums.*;
import com.princexml.wrapper.events.PrinceEvents;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.princexml.wrapper.CommandLine.*;

/**
 * Class that provides the default interface to Prince, where each document
 * conversion invokes a new Prince process.
 */
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
    private RasterFormat rasterFormat;
    private int rasterJpegQuality = -1;
    private int rasterPage;
    private int rasterDpi;
    private int rasterThreads = -1;
    private RasterBackground rasterBackground;

    // Additional options.
    private final Map<String, String> options = new LinkedHashMap<>();

    /**
     * Constructor for {@code Prince}.
     * @param princePath The path of the Prince executable. For example, this may be
     *                   <code>C:\Program&#xA0;Files\Prince\engine\bin\prince.exe</code>
     *                   on Windows or <code>/usr/bin/prince</code> on Linux.
     */
    public Prince(String princePath) {
        super(princePath);
    }

    /**
     * Constructor for {@code Prince}.
     * @param princePath The path of the Prince executable. For example, this may be
     *                   <code>C:\Program&#xA0;Files\Prince\engine\bin\prince.exe</code>
     *                   on Windows or <code>/usr/bin/prince</code> on Linux.
     * @param events An implementation of {@link com.princexml.wrapper.events.PrinceEvents}
     *               that will receive messages returned from Prince.
     */
    public Prince(String princePath, PrinceEvents events) {
        super(princePath, events);
    }

    /**
     * Convert an XML or HTML file to a PDF file. The name of the output PDF
     * file will be the same as the name of the input file, but with the
     * {@code .pdf} extension.
     * @param inputPath The filename of the input XML or HTML document.
     * @return true if a PDF file was generated successfully.
     * @throws IOException If an I/O error occurs.
     */
    public boolean convert(String inputPath) throws IOException {
        return convertInternal(Collections.singletonList(inputPath), null);
    }

    /**
     * Convert an XML or HTML file to a PDF file.
     * @param inputPath The filename of the input XML or HTML document.
     * @param outputPath The filename of the output PDF file.
     * @return true if a PDF file was generated successfully.
     * @throws IOException If an I/O error occurs.
     */
    public boolean convert(String inputPath, String outputPath) throws IOException {
        return convertInternal(Collections.singletonList(inputPath), outputPath);
    }

    /**
     * Convert multiple XML or HTML files to a PDF file.
     * @param inputPaths The filenames of the input XML or HTML documents.
     * @param outputPath The filename of the output PDF file.
     * @return true if a PDF file was generated successfully.
     * @throws IOException If an I/O error occurs.
     */
    public boolean convert(List<String> inputPaths, String outputPath) throws IOException {
        return convertInternal(inputPaths, outputPath);
    }

    private boolean convertInternal(List<String> inputPaths, String outputPath) throws IOException {
        List<String> cmdLine = getJobCommandLine("normal");
        cmdLine.addAll(inputPaths);
        if (outputPath != null) {
            cmdLine.add(toCommand("output", outputPath));
        }

        Process process = Util.invokeProcess(cmdLine);

        return readMessagesFromStderr(process);
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(String inputPath, OutputStream output) throws IOException {
        return convert(Collections.singletonList(inputPath), output);
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(List<String> inputPaths, OutputStream output) throws IOException {
        List<String> cmdLine = getJobCommandLine("buffered");
        cmdLine.addAll(inputPaths);
        cmdLine.add(toCommand("output", "-"));

        Process process = Util.invokeProcess(cmdLine);
        InputStream fromPrince = process.getInputStream();

        Util.copyInputToOutput(fromPrince, output);
        fromPrince.close();

        return readMessagesFromStderr(process);
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(InputStream input, OutputStream output) throws IOException {
        if (inputType == null || inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }

        List<String> cmdLine = getJobCommandLine("buffered");
        cmdLine.add("-");

        Process process = Util.invokeProcess(cmdLine);
        OutputStream toPrince = process.getOutputStream();
        InputStream fromPrince = process.getInputStream();

        Util.copyInputToOutput(input, toPrince);
        toPrince.close();

        Util.copyInputToOutput(fromPrince, output);
        fromPrince.close();

        return readMessagesFromStderr(process);
    }

    /**
     * Convert an XML or HTML string to a PDF file.
     * @param input The XML or HTML document in the form of a String.
     * @param outputPath The filename of the output PDF file.
     * @return true if a PDF file was generated successfully.
     * @throws IOException If an I/O error occurs.
     */
    public boolean convertString(String input, String outputPath) throws IOException {
        if (inputType == null || inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }

        List<String> cmdLine = getJobCommandLine("buffered");
        cmdLine.add(toCommand("output", outputPath));
        cmdLine.add("-");

        Process process = Util.invokeProcess(cmdLine);
        OutputStream toPrince = process.getOutputStream();

        toPrince.write(input.getBytes(StandardCharsets.UTF_8));
        toPrince.close();

        return readMessagesFromStderr(process);
    }

    /** {@inheritDoc} */
    @Override
    public boolean convertString(String input, OutputStream output) throws IOException {
        InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        boolean result = convert(in, output);
        in.close();

        return result;
    }

    /**
     * Rasterize an XML or HTML file.
     * @param inputPath The filename of the input XML or HTML document.
     * @param outputPath A template string from which the raster files will be named
     *                   (e.g. "page_%02d.png" will cause Prince to generate
     *                   page_01.png, page_02.png, ..., page_10.png etc.).
     * @return true if the input was successfully rasterized.
     * @throws IOException If an I/O error occurs.
     */
    public boolean rasterize(String inputPath, String outputPath) throws IOException {
        return rasterizeInternal(Collections.singletonList(inputPath), outputPath);
    }

    /**
     * Rasterize multiple XML or HTML files.
     * @param inputPaths The filenames of the input XML or HTML documents.
     * @param outputPath A template string from which the raster files will be named
     *                   (e.g. "page_%02d.png" will cause Prince to generate
     *                   page_01.png, page_02.png, ..., page_10.png etc.).
     * @return true if the input was successfully rasterized.
     * @throws IOException If an I/O error occurs.
     */
    public boolean rasterize(List<String> inputPaths, String outputPath) throws IOException {
        return rasterizeInternal(inputPaths, outputPath);
    }

    private boolean rasterizeInternal(List<String> inputPaths, String outputPath) throws IOException {
        List<String> cmdLine = getJobCommandLine("normal");
        cmdLine.addAll(inputPaths);
        cmdLine.add(toCommand("raster-output", outputPath));

        Process process = Util.invokeProcess(cmdLine);

        return readMessagesFromStderr(process);
    }

    /**
     * Rasterize an XML or HTML file. This method is useful for servlets as it
     * allows Prince to write the raster output directly to the {@code OutputStream}
     * of the servlet response.
     * @param inputPath The filename of the input XML or HTML document.
     * @param output The OutputStream to which Prince will write the raster output.
     * @return true if the input was successfully rasterized.
     * @throws IOException If an I/O error occurs.
     */
    public boolean rasterize(String inputPath, OutputStream output) throws IOException {
        return rasterize(Collections.singletonList(inputPath), output);
    }

    /**
     * Rasterize multiple XML or HTML files. This method is useful for servlets as it
     * allows Prince to write the raster output directly to the {@code OutputStream}
     * of the servlet response.
     * @param inputPaths The filenames of the input XML or HTML documents.
     * @param output The OutputStream to which Prince will write the raster output.
     * @return true if the input was successfully rasterized.
     * @throws IOException If an I/O error occurs.
     */
    public boolean rasterize(List<String> inputPaths, OutputStream output) throws IOException {
        if (rasterPage < 1) {
            throw new RuntimeException("rasterPage has to be set to a value > 0");
        }
        if (rasterFormat == null || rasterFormat == RasterFormat.AUTO) {
            throw new RuntimeException("rasterFormat has to be set to JPEG or PNG");
        }

        List<String> cmdLine = getJobCommandLine("buffered");
        cmdLine.addAll(inputPaths);
        cmdLine.add(toCommand("raster-output", "-"));

        Process process = Util.invokeProcess(cmdLine);
        InputStream fromPrince = process.getInputStream();

        Util.copyInputToOutput(fromPrince, output);
        fromPrince.close();

        return readMessagesFromStderr(process);
    }

    /**
     * Rasterize an XML or HTML stream. This method is useful for servlets as it
     * allows Prince to write the raster output directly to the {@code OutputStream}
     * of the servlet response.
     * @param input The InputStream from which Prince will read the XML or HTML
     *              document.
     * @param output The OutputStream to which Prince will write the raster output.
     * @return true if the input was successfully rasterized.
     * @throws IOException If an I/O error occurs.
     */
    public boolean rasterize(InputStream input, OutputStream output) throws IOException {
        if (inputType == null || inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }
        if (rasterPage < 1) {
            throw new RuntimeException("rasterPage has to be set to a value > 0");
        }
        if (rasterFormat == null || rasterFormat == RasterFormat.AUTO) {
            throw new RuntimeException("rasterFormat has to be set to JPEG or PNG");
        }

        List<String> cmdLine = getJobCommandLine("buffered");
        cmdLine.add(toCommand("raster-output", "-"));
        cmdLine.add("-");

        Process process = Util.invokeProcess(cmdLine);
        OutputStream toPrince = process.getOutputStream();
        InputStream fromPrince = process.getInputStream();

        Util.copyInputToOutput(input, toPrince);
        toPrince.close();

        Util.copyInputToOutput(fromPrince, output);
        fromPrince.close();

        return readMessagesFromStderr(process);
    }

    /**
     * Rasterize an XML or HTML string.
     * @param input The XML or HTML document in the form of a String.
     * @param outputPath A template string from which the raster files will be named
     *                   (e.g. "page_%02d.png" will cause Prince to generate
     *                   page_01.png, page_02.png, ..., page_10.png etc.).
     * @return true if the input was successfully rasterized.
     * @throws IOException If an I/O error occurs.
     */
    public boolean rasterizeString(String input, String outputPath) throws IOException {
        if (inputType == null || inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }

        List<String> cmdLine = getJobCommandLine("buffered");
        cmdLine.add(toCommand("raster-output", outputPath));
        cmdLine.add("-");

        Process process = Util.invokeProcess(cmdLine);
        OutputStream toPrince = process.getOutputStream();

        toPrince.write(input.getBytes(StandardCharsets.UTF_8));
        toPrince.close();

        return readMessagesFromStderr(process);
    }

    /**
     * Rasterize an XML or HTML string. This method is useful for servlets as it
     * allows Prince to write the raster output directly to the {@code OutputStream}
     * of the servlet response.
     * @param input The XML or HTML document in the form of a String.
     * @param output The OutputStream to which Prince will write the raster output.
     * @return true if the input was successfully rasterized.
     * @throws IOException If an I/O error occurs.
     */
    public boolean rasterizeString(String input, OutputStream output) throws IOException {
        InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        boolean result = rasterize(in, output);
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
        if (!fileAttachments.isEmpty()) { cmdLine.addAll(toCommands("attach",
                fileAttachments.stream().map(a -> a.url).collect(Collectors.toList()))); }
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

        if (rasterFormat != null) { cmdLine.add(toCommand("raster-format", rasterFormat)); }
        if (rasterJpegQuality > -1) { cmdLine.add(toCommand("raster-jpeg-quality", rasterJpegQuality)); }
        if (rasterPage > 0) { cmdLine.add(toCommand("raster-pages", rasterPage)); }
        if (rasterDpi > 0) { cmdLine.add(toCommand("raster-dpi", rasterDpi)); }
        if (rasterThreads > -1) { cmdLine.add(toCommand("raster-threads", rasterThreads)); }
        if (rasterBackground != null) { cmdLine.add(toCommand("raster-background", rasterBackground)); }

        options.forEach((k, v) -> cmdLine.add(v == null ? toCommand(k) : toCommand(k, v)));

        return cmdLine;
    }

    private boolean readMessagesFromStderr(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        boolean result = readMessages(reader);
        reader.close();
        return result;
    }

    //region Input options.
    /**
     * Rather than retrieving documents beginning with {@code url}, get them
     * from the local directory {@code dir}. This method can be called more than
     * once to add multiple remappings.
     * @param url The URL to map to a directory.
     * @param directory The directory that a URL is mapped to.
     */
    public void addRemap(String url, String directory) {
        this.remaps.add(url + "=" + directory);
    }

    /**
     * Clear all of the remappings accumulated by calling {@link #addRemap(String, String)}.
     */
    public void clearRemaps() {
        this.remaps.clear();
    }

    /**
     * Disable access to local files. Default value is {@code false}.
     * @param noLocalFiles true to disable local files.
     */
    public void setNoLocalFiles(boolean noLocalFiles) {
        this.noLocalFiles = noLocalFiles;
    }
    //endregion

    //region CSS options.
    /**
     * Specify the page size.
     * @param pageSize The page size to use (e.g. "A4").
     */
    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Specify the page margin.
     * @param pageMargin The page margin to use (e.g. "20mm").
     */
    public void setPageMargin(String pageMargin) {
        this.pageMargin = pageMargin;
    }
    //endregion

    //region PDF output options.
    /**
     * Disable the use of system fonts. Only fonts defined with {@code font-face}
     * rules in CSS will be available. Default value is {@code false}.
     * @param noSystemFonts true to disable system fonts.
     */
    public void setNoSystemFonts(boolean noSystemFonts) {
        this.noSystemFonts = noSystemFonts;
    }

    /**
     * Changes the DPI of the "px" units in CSS. Default value is 96 dpi.
     * @param cssDpi The DPI of the "px" units. Value must be greater than 0.
     */
    public void setCssDpi(int cssDpi) {
        if (cssDpi < 1) {
            throw new IllegalArgumentException("invalid cssDpi value (must be > 0)");
        }
        this.cssDpi = cssDpi;
    }
    //endregion

    //region Raster output options.
    /**
     * Specify the format for the raster output. Default value is
     * {@link com.princexml.wrapper.enums.RasterFormat#AUTO}.
     * @param rasterFormat The format for the raster output.
     */
    public void setRasterFormat(RasterFormat rasterFormat) {
        this.rasterFormat = rasterFormat;
    }

    /**
     * Specify the level of JPEG compression when generating raster output in JPEG format.
     * Default value is 92 percent.
     * @param rasterJpegQuality The level of JPEG compression. Valid range is between 0
     *                          and 100 inclusive.
     */
    public void setRasterJpegQuality(int rasterJpegQuality) {
        if (rasterJpegQuality < 0 || rasterJpegQuality > 100) {
            throw new IllegalArgumentException("invalid rasterJpegQuality value (must be [0, 100])");
        }
        this.rasterJpegQuality = rasterJpegQuality;
    }

    /**
     * Set the page number to be rasterized. Defaults to rasterizing all pages.
     * @param rasterPage The page number to be rasterized. Value must be greater than 0.
     */
    public void setRasterPage(int rasterPage) {
        if (rasterPage < 1) {
            throw new IllegalArgumentException("invalid rasterPage value (must be > 0)");
        }
        this.rasterPage = rasterPage;
    }

    /**
     * Specify the resolution of raster output. Default value is 96 dpi.
     * @param rasterDpi The raster output resolution. Value must be greater than 0.
     */
    public void setRasterDpi(int rasterDpi) {
        if (rasterDpi < 1) {
            throw new IllegalArgumentException("invalid rasterDpi value (must be > 0)");
        }
        this.rasterDpi = rasterDpi;
    }

    /**
     * Set the number of threads to use for multi-threaded rasterization. Default
     * value is the number of cores and hyperthreads the system provides.
     * @param rasterThreads The number of threads to use.
     */
    public void setRasterThreads(int rasterThreads) {
        this.rasterThreads = rasterThreads;
    }

    /**
     * Set the background. Can be used when rasterizing to an image format that
     * supports transparency. Default value is {@link com.princexml.wrapper.enums.RasterBackground#WHITE}.
     * @param rasterBackground The raster background.
     */
    public void setRasterBackground(RasterBackground rasterBackground) {
        this.rasterBackground = rasterBackground;
    }
    //endregion

    //region Additional options.
    /**
     * Specify additional Prince command-line options.
     * @param key The command-line option.
     */
    public void addOption(String key) {
        this.options.put(key, null);
    }

    /**
     * Specify additional Prince command-line options.
     * @param key The command-line option key.
     * @param value The command-line option value.
     */
    public void addOption(String key, String value) {
        this.options.put(key, value);
    }

    /**
     * Clear the additional command-line options accumulated by calling
     * {@link #addOption(String)} or {@link #addOption(String, String)}.
     */
    public void clearOptions() {
        this.options.clear();
    }
    //endregion
}
