/*
 * Copyright (C) 2005-2006, 2010, 2012, 2014-2015, 2018, 2021-2022, 2026 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import com.princexml.wrapper.enums.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.princexml.wrapper.CommandLine.toCommand;
import static com.princexml.wrapper.CommandLine.toCommands;

/**
 * Class that provides the default interface to Prince, where each document
 * conversion invokes a new Prince process.
 */
public class Prince extends PrinceBase {
    private Prince(Builder builder) {
        super(builder);
    }

    /**
     * Returns the builder for the {@link #Prince} object.
     * @param princePath The path of the Prince executable. For example, this may be
     *                   <code>C:\Program&#xA0;Files\Prince\engine\bin\prince.exe</code>
     *                   on Windows or <code>/usr/bin/prince</code> on Linux.
     */
    public static Builder builder(String princePath) {
        return new Builder(princePath);
    }

    /**
     * Create a new job for {@link #Prince}.
     */
    public Job newJob() {
        return new Job(this);
    }

    /**
     * Duplicate an existing job for {@link #Prince}.
     * @param job The job to duplicate.
     */
    public Job newJob(Job job) {
        return new Job(job);
    }

    private List<String> buildCommandLine(String logType, Job j) {
        List<String> cmdLine = super.buildCommandLine();

        cmdLine.add(toCommand("structured-log", logType));

        // Input options.
        if (j.inputType != null) { cmdLine.add(toCommand("input", j.inputType)); }
        if (j.baseUrl != null) { cmdLine.add(toCommand("baseurl", j.baseUrl)); }
        if (j.iframes) { cmdLine.add(toCommand("iframes")); }
        if (j.xInclude) { cmdLine.add(toCommand("xinclude")); }
        if (j.xmlExternalEntities) { cmdLine.add(toCommand("xml-external-entities")); }
        if (!j.remaps.isEmpty()) { cmdLine.addAll(toCommands("remap", j.remaps)); }

        // JavaScript options.
        if (j.javaScript) { cmdLine.add(toCommand("javascript")); }
        if (!j.scripts.isEmpty()) { cmdLine.addAll(toCommands("script", j.scripts)); }
        if (j.maxPasses > 0) { cmdLine.add(toCommand("max-passes", j.maxPasses)); }

        // CSS options.
        if (!j.styleSheets.isEmpty()) { cmdLine.addAll(toCommands("style", j.styleSheets)); }
        if (j.media != null) { cmdLine.add(toCommand("media", j.media)); }
        if (j.noAuthorStyle) { cmdLine.add(toCommand("no-author-style")); }
        if (j.noDefaultStyle) { cmdLine.add(toCommand("no-default-style")); }
        if (j.pageSize != null) { cmdLine.add(toCommand("page-size", j.pageSize)); }
        if (j.pageMargin != null) { cmdLine.add(toCommand("page-margin", j.pageMargin)); }

        // PDF output options.
        if (j.pdfId != null) { cmdLine.add(toCommand("pdf-id", j.pdfId)); }
        if (j.pdfLang != null) { cmdLine.add(toCommand("pdf-lang", j.pdfLang)); }
        if (j.pdfProfile != null) { cmdLine.add(toCommand("pdf-profile", j.pdfProfile)); }
        if (j.pdfOutputIntent != null) {
            cmdLine.add(toCommand("pdf-output-intent", j.pdfOutputIntent));
        }
        if (j.pdfScript != null) { cmdLine.add(toCommand("pdf-script", j.pdfScript)); }
        j.pdfEventScripts.forEach((k, v) ->
                cmdLine.add(toCommand("pdf-event-script", k + ":" + v)));
        if (!j.fileAttachments.isEmpty()) {
            cmdLine.addAll(toCommands(
                    "attach",
                    j.fileAttachments
                            .stream()
                            .map(a -> a.url)
                            .collect(Collectors.toList())
                    )
            );
        }
        if (j.noArtificialFonts) { cmdLine.add(toCommand("no-artificial-fonts")); }
        if (j.noEmbedFonts) { cmdLine.add(toCommand("no-embed-fonts")); }
        if (j.noSubsetFonts) { cmdLine.add(toCommand("no-subset-fonts")); }
        if (j.forceIdentityEncoding) { cmdLine.add(toCommand("force-identity-encoding")); }
        if (j.noCompress) { cmdLine.add(toCommand("no-compress")); }
        if (j.noObjectStreams) { cmdLine.add(toCommand("no-object-streams")); }
        if (j.convertColors) { cmdLine.add(toCommand("convert-colors")); }
        if (j.fallbackCmykProfile != null) {
            cmdLine.add(toCommand("fallback-cmyk-profile", j.fallbackCmykProfile));
        }
        if (j.taggedPdf) { cmdLine.add(toCommand("tagged-pdf")); }
        if (j.pdfForms) { cmdLine.add(toCommand("pdf-forms")); }
        if (j.noSystemFonts) { cmdLine.add(toCommand("no-system-fonts")); }
        if (j.cssDpi > 0) {cmdLine.add(toCommand("css-dpi", j.cssDpi)); }

        // PDF metadata options.
        if (j.pdfTitle != null) { cmdLine.add(toCommand("pdf-title", j.pdfTitle)); }
        if (j.pdfSubject != null) { cmdLine.add(toCommand("pdf-subject", j.pdfSubject)); }
        if (j.pdfAuthor != null) { cmdLine.add(toCommand("pdf-author", j.pdfAuthor)); }
        if (j.pdfKeywords != null) { cmdLine.add(toCommand("pdf-keywords", j.pdfKeywords)); }
        if (j.pdfCreator != null) { cmdLine.add(toCommand("pdf-creator", j.pdfCreator)); }
        if (j.xmp != null) { cmdLine.add(toCommand("pdf-xmp", j.xmp)); }

        // PDF encryption options.
        if (j.encrypt) { cmdLine.add(toCommand("encrypt")); }
        if (j.keyBits != null) { cmdLine.add(toCommand("key-bits", j.keyBits)); }
        if (j.userPassword != null) { cmdLine.add(toCommand("user-password", j.userPassword)); }
        if (j.ownerPassword != null) { cmdLine.add(toCommand("owner-password", j.ownerPassword)); }
        if (j.disallowPrint) { cmdLine.add(toCommand("disallow-print")); }
        if (j.disallowCopy) { cmdLine.add(toCommand("disallow-copy")); }
        if (j.allowCopyForAccessibility) {
            cmdLine.add(toCommand("allow-copy-for-accessibility"));
        }
        if (j.disallowAnnotate) { cmdLine.add(toCommand("disallow-annotate")); }
        if (j.disallowModify) { cmdLine.add(toCommand("disallow-modify")); }
        if (j.allowAssembly) { cmdLine.add(toCommand("allow-assembly")); }

        // Raster output options.
        if (j.rasterFormat != null) { cmdLine.add(toCommand("raster-format", j.rasterFormat)); }
        if (j.rasterJpegQuality > -1) {
            cmdLine.add(toCommand("raster-jpeg-quality", j.rasterJpegQuality));
        }
        if (j.rasterPage > 0) { cmdLine.add(toCommand("raster-pages", j.rasterPage)); }
        if (j.rasterDpi > 0) { cmdLine.add(toCommand("raster-dpi", j.rasterDpi)); }
        if (j.rasterThreads > -1) { cmdLine.add(toCommand("raster-threads", j.rasterThreads)); }
        if (j.rasterBackground != null) {
            cmdLine.add(toCommand("raster-background", j.rasterBackground));
        }

        // Additional options.
        j.options.forEach(o ->
                cmdLine.add(o.snd == null ? toCommand(o.fst) : toCommand(o.fst, o.snd)));

        return cmdLine;
    }

    private boolean convert(String inputPath, Job job) throws IOException {
        return convertInternal(Collections.singletonList(inputPath), null, job);
    }

    private boolean convert(String inputPath, String outputPath, Job job) throws IOException {
        return convertInternal(Collections.singletonList(inputPath), outputPath, job);
    }

    private boolean convert(List<String> inputPaths, String outputPath, Job job)
            throws IOException {
        return convertInternal(inputPaths, outputPath, job);
    }

    private boolean convertInternal(List<String> inputPaths, String outputPath, Job job)
            throws IOException {
        List<String> cmdLine = buildCommandLine("normal", job);
        cmdLine.addAll(inputPaths);
        if (outputPath != null) {
            cmdLine.add(toCommand("output", outputPath));
        }
        Process process = Util.invokeProcess(cmdLine);

        return readMessagesFromStderr(process);
    }

    private boolean convert(String inputPath, OutputStream output, Job job) throws IOException {
        return convert(Collections.singletonList(inputPath), output, job);
    }

    private boolean convert(List<String> inputPaths, OutputStream output, Job job)
            throws IOException {
        List<String> cmdLine = buildCommandLine("buffered", job);
        cmdLine.addAll(inputPaths);
        cmdLine.add(toCommand("output", "-"));

        Process process = Util.invokeProcess(cmdLine);

        try (InputStream fromPrince = process.getInputStream()) {
            Util.copyInputToOutput(fromPrince, output);
        }

        return readMessagesFromStderr(process);
    }

    private boolean convert(InputStream input, OutputStream output, Job job) throws IOException {
        if (job.inputType == null || job.inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }

        List<String> cmdLine = buildCommandLine("buffered", job);
        cmdLine.add("-");

        Process process = Util.invokeProcess(cmdLine);

        try (OutputStream toPrince = process.getOutputStream()) {
            Util.copyInputToOutput(input, toPrince);
        }

        try (InputStream fromPrince = process.getInputStream()) {
            Util.copyInputToOutput(fromPrince, output);
        }

        return readMessagesFromStderr(process);
    }

    private boolean convertInputList(String inputListPath, String outputPath, Job job)
            throws IOException {
        List<String> cmdLine = buildCommandLine("normal", job);
        cmdLine.add(toCommand("input-list", inputListPath));
        if (outputPath != null) {
            cmdLine.add(toCommand("output", outputPath));
        }

        Process process = Util.invokeProcess(cmdLine);

        return readMessagesFromStderr(process);
    }

    private boolean convertInputList(String inputListPath, OutputStream output, Job job)
            throws IOException {
        List<String> cmdLine = buildCommandLine("buffered", job);
        cmdLine.add(toCommand("input-list", inputListPath));
        cmdLine.add(toCommand("output", "-"));

        Process process = Util.invokeProcess(cmdLine);

        try (InputStream fromPrince = process.getInputStream()) {
            Util.copyInputToOutput(fromPrince, output);
        }

        return readMessagesFromStderr(process);
    }

    private boolean convertString(String input, String outputPath, Job job) throws IOException {
        if (job.inputType == null || job.inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }

        List<String> cmdLine = buildCommandLine("buffered", job);
        cmdLine.add(toCommand("output", outputPath));
        cmdLine.add("-");

        Process process = Util.invokeProcess(cmdLine);

        try (OutputStream toPrince = process.getOutputStream()) {
            toPrince.write(input.getBytes(StandardCharsets.UTF_8));
        }

        return readMessagesFromStderr(process);
    }

    private boolean convertString(String input, OutputStream output, Job job) throws IOException {
        try (InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
            return convert(in, output, job);
        }
    }

    private boolean rasterize(String inputPath, String outputPath, Job job) throws IOException {
        return rasterizeInternal(Collections.singletonList(inputPath), outputPath, job);
    }

    private boolean rasterize(List<String> inputPaths, String outputPath, Job job)
            throws IOException {
        return rasterizeInternal(inputPaths, outputPath, job);
    }

    private boolean rasterizeInternal(List<String> inputPaths, String outputPath, Job job)
            throws IOException {
        List<String> cmdLine = buildCommandLine("normal", job);
        cmdLine.addAll(inputPaths);
        cmdLine.add(toCommand("raster-output", outputPath));

        Process process = Util.invokeProcess(cmdLine);

        return readMessagesFromStderr(process);
    }

    private boolean rasterize(String inputPath, OutputStream output, Job job) throws IOException {
        return rasterize(Collections.singletonList(inputPath), output, job);
    }

    private boolean rasterize(List<String> inputPaths, OutputStream output, Job job)
            throws IOException {
        if (job.rasterPage < 1) {
            throw new RuntimeException("rasterPage has to be set to a value > 0");
        }
        if (job.rasterFormat == null || job.rasterFormat == RasterFormat.AUTO) {
            throw new RuntimeException("rasterFormat has to be set to JPEG or PNG");
        }

        List<String> cmdLine = buildCommandLine("buffered", job);
        cmdLine.addAll(inputPaths);
        cmdLine.add(toCommand("raster-output", "-"));

        Process process = Util.invokeProcess(cmdLine);

        try (InputStream fromPrince = process.getInputStream()) {
            Util.copyInputToOutput(fromPrince, output);
        }

        return readMessagesFromStderr(process);
    }

    private boolean rasterize(InputStream input, OutputStream output, Job job) throws IOException {
        if (job.inputType == null || job.inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }
        if (job.rasterPage < 1) {
            throw new RuntimeException("rasterPage has to be set to a value > 0");
        }
        if (job.rasterFormat == null || job.rasterFormat == RasterFormat.AUTO) {
            throw new RuntimeException("rasterFormat has to be set to JPEG or PNG");
        }

        List<String> cmdLine = buildCommandLine("buffered", job);
        cmdLine.add(toCommand("raster-output", "-"));
        cmdLine.add("-");

        Process process = Util.invokeProcess(cmdLine);

        try (OutputStream toPrince = process.getOutputStream()) {
            Util.copyInputToOutput(input, toPrince);
        }

        try (InputStream fromPrince = process.getInputStream()) {
            Util.copyInputToOutput(fromPrince, output);
        }

        return readMessagesFromStderr(process);
    }

    private boolean rasterizeInputList(String inputListPath, String outputPath, Job job)
            throws IOException {
        List<String> cmdLine = buildCommandLine("normal", job);
        cmdLine.add(toCommand("input-list", inputListPath));
        cmdLine.add(toCommand("raster-output", outputPath));

        Process process = Util.invokeProcess(cmdLine);

        return readMessagesFromStderr(process);
    }

    private boolean rasterizeInputList(String inputListPath, OutputStream output, Job job)
            throws IOException {
        if (job.rasterPage < 1) {
            throw new RuntimeException("rasterPage has to be set to a value > 0");
        }
        if (job.rasterFormat == null || job.rasterFormat == RasterFormat.AUTO) {
            throw new RuntimeException("rasterFormat has to be set to JPEG or PNG");
        }

        List<String> cmdLine = buildCommandLine("buffered", job);
        cmdLine.add(toCommand("input-list", inputListPath));
        cmdLine.add(toCommand("raster-output", "-"));

        Process process = Util.invokeProcess(cmdLine);

        try (InputStream fromPrince = process.getInputStream()) {
            Util.copyInputToOutput(fromPrince, output);
        }

        return readMessagesFromStderr(process);
    }

    private boolean rasterizeString(String input, String outputPath, Job job) throws IOException {
        if (job.inputType == null || job.inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }

        List<String> cmdLine = buildCommandLine("buffered", job);
        cmdLine.add(toCommand("raster-output", outputPath));
        cmdLine.add("-");

        Process process = Util.invokeProcess(cmdLine);

        try (OutputStream toPrince = process.getOutputStream()) {
            toPrince.write(input.getBytes(StandardCharsets.UTF_8));
        }

        return readMessagesFromStderr(process);
    }

    private boolean rasterizeString(String input, OutputStream output, Job job) throws IOException {
        try (InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))) {
            return rasterize(in, output, job);
        }
    }

    private boolean readMessagesFromStderr(Process process) throws IOException {
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            return readMessages(reader);
        }
    }

    public static class Builder extends PrinceBase.Builder<Builder> {
        private Builder(String princePath) {
            super(princePath);
        }

        /**
         * Finalize the {@link #Prince} object.
         */
        public Prince build() {
            return new Prince(this);
        }
    }

    public static class Job extends PrinceBase.Job<Job> {
        private final Prince cmdLine;

        // Input options.
        private final List<String> remaps = new ArrayList<>();

        // CSS options.
        private String pageSize = null;
        private String pageMargin = null;

        // PDF output options.
        private boolean noSystemFonts = false;
        private int cssDpi = 0;

        // Raster output options.
        private RasterFormat rasterFormat = null;
        private int rasterJpegQuality = -1;
        private int rasterPage = 0;
        private int rasterDpi = 0;
        private int rasterThreads = -1;
        private RasterBackground rasterBackground = null;

        // Additional options.
        private final List<Pair> options = new ArrayList<>();

        private Job(Prince cmdLine) {
            super();
            this.cmdLine = cmdLine;
        }

        private Job(Job job) {
            super(job);
            this.cmdLine = job.cmdLine;

            this.remaps.addAll(job.remaps);

            this.pageSize = job.pageSize;
            this.pageMargin = job.pageMargin;

            this.noSystemFonts = job.noSystemFonts;
            this.cssDpi = job.cssDpi;

            this.rasterFormat = job.rasterFormat;
            this.rasterJpegQuality = job.rasterJpegQuality;
            this.rasterPage = job.rasterPage;
            this.rasterDpi = job.rasterDpi;
            this.rasterThreads = job.rasterThreads;
            this.rasterBackground = job.rasterBackground;

            this.options.addAll(job.options);
        }

        /**
         * Rather than retrieving documents beginning with {@code url}, get them
         * from the local directory {@code dir}. This method can be called more than
         * once to add multiple remappings.
         * @param url The URL to map to a directory.
         * @param directory The directory that a URL is mapped to.
         */
        public Job remap(String url, String directory) {
            this.remaps.add(url + "=" + directory);
            return this;
        }

        /**
         * Specify the page size.
         * @param pageSize The page size to use (e.g. "A4").
         */
        public Job pageSize(String pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        /**
         * Specify the page margin.
         * @param pageMargin The page margin to use (e.g. "20mm").
         */
        public Job pageMargin(String pageMargin) {
            this.pageMargin = pageMargin;
            return this;
        }

        /**
         * Disable the use of system fonts. Only fonts defined with {@code font-face}
         * rules in CSS will be available. Default value is {@code false}.
         * @param noSystemFonts true to disable system fonts.
         */
        public Job noSystemFonts(boolean noSystemFonts) {
            this.noSystemFonts = noSystemFonts;
            return this;
        }

        /**
         * Changes the DPI of the "px" units in CSS. Default value is 96 dpi.
         * @param cssDpi The DPI of the "px" units. Value must be greater than 0.
         */
        public Job cssDpi(int cssDpi) {
            if (cssDpi < 1) {
                throw new IllegalArgumentException("invalid cssDpi value (must be > 0)");
            }
            this.cssDpi = cssDpi;
            return this;
        }

        /**
         * Specify the format for the raster output. Default value is
         * {@link com.princexml.wrapper.enums.RasterFormat#AUTO}.
         * @param rasterFormat The format for the raster output.
         */
        public Job rasterFormat(RasterFormat rasterFormat) {
            this.rasterFormat = rasterFormat;
            return this;
        }

        /**
         * Specify the level of JPEG compression when generating raster output in JPEG format.
         * Default value is 92 percent.
         * @param rasterJpegQuality The level of JPEG compression. Valid range is between 0
         *                          and 100 inclusive.
         */
        public Job rasterJpegQuality(int rasterJpegQuality) {
            if (rasterJpegQuality < 0 || rasterJpegQuality > 100) {
                throw new IllegalArgumentException(
                        "invalid rasterJpegQuality value (must be [0, 100])");
            }
            this.rasterJpegQuality = rasterJpegQuality;
            return this;
        }

        /**
         * Set the page number to be rasterized. Defaults to rasterizing all pages.
         * @param rasterPage The page number to be rasterized. Value must be greater than 0.
         */
        public Job rasterPage(int rasterPage) {
            if (rasterPage < 1) {
                throw new IllegalArgumentException("invalid rasterPage value (must be > 0)");
            }
            this.rasterPage = rasterPage;
            return this;
        }

        /**
         * Specify the resolution of raster output. Default value is 96 dpi.
         * @param rasterDpi The raster output resolution. Value must be greater than 0.
         */
        public Job rasterDpi(int rasterDpi) {
            if (rasterDpi < 1) {
                throw new IllegalArgumentException("invalid rasterDpi value (must be > 0)");
            }
            this.rasterDpi = rasterDpi;
            return this;
        }

        /**
         * Set the number of threads to use for multithreaded rasterization. Default
         * value is the number of cores and hyperthreads the system provides.
         * @param rasterThreads The number of threads to use.
         */
        public Job rasterThreads(int rasterThreads) {
            this.rasterThreads = rasterThreads;
            return this;
        }

        /**
         * Set the background. Can be used when rasterizing to an image format that
         * supports transparency. Default value is
         * {@link com.princexml.wrapper.enums.RasterBackground#WHITE}.
         * @param rasterBackground The raster background.
         */
        public Job rasterBackground(RasterBackground rasterBackground) {
            this.rasterBackground = rasterBackground;
            return this;
        }

        /**
         * Specify additional Prince command-line options.
         * @param key The command-line option.
         */
        public Job option(String key) {
            this.options.add(new Pair(key));
            return this;
        }

        /**
         * Specify additional Prince command-line options.
         * @param key The command-line option key.
         * @param value The command-line option value.
         */
        public Job option(String key, String value) {
            this.options.add(new Pair(key, value));
            return this;
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
            return cmdLine.convert(inputPath, new Job(this));
        }

        /**
         * Convert an XML or HTML file to a PDF file.
         * @param inputPath The filename of the input XML or HTML document.
         * @param outputPath The filename of the output PDF file.
         * @return true if a PDF file was generated successfully.
         * @throws IOException If an I/O error occurs.
         */
        public boolean convert(String inputPath, String outputPath) throws IOException {
            return cmdLine.convert(inputPath, outputPath, new Job(this));
        }

        /**
         * Convert multiple XML or HTML files to a PDF file.
         * @param inputPaths The filenames of the input XML or HTML documents.
         * @param outputPath The filename of the output PDF file.
         * @return true if a PDF file was generated successfully.
         * @throws IOException If an I/O error occurs.
         */
        public boolean convert(List<String> inputPaths, String outputPath) throws IOException {
            return cmdLine.convert(inputPaths, outputPath, new Job(this));
        }

        /**
         * Convert an XML or HTML file to a PDF file. This method is useful for
         * servlets as it allows Prince to write the PDF output directly to the
         * {@code OutputStream} of the servlet response.
         * @param inputPath The filename of the input XML or HTML document.
         * @param output The OutputStream to which Prince will write the PDF output.
         * @return true if a PDF file was generated successfully.
         * @throws IOException If an I/O error occurs.
         */
        public boolean convert(String inputPath, OutputStream output) throws IOException {
            return cmdLine.convert(inputPath, output, new Job(this));
        }

        /**
         * Convert multiple XML or HTML files to a PDF file. This method is useful
         * for servlets as it allows Prince to write the PDF output directly to the
         * {@code OutputStream} of the servlet response.
         * @param inputPaths The filenames of the input XML or HTML documents.
         * @param output The OutputStream to which Prince will write the PDF output.
         * @return true if a PDF file was generated successfully.
         * @throws IOException If an I/O error occurs.
         */
        public boolean convert(List<String> inputPaths, OutputStream output) throws IOException {
            return cmdLine.convert(inputPaths, output, new Job(this));
        }

        /**
         * Convert an XML or HTML stream to a PDF file. This method is useful for
         * servlets as it allows Prince to write the PDF output directly to the
         * {@code OutputStream} of the servlet response.
         * <p>
         * Note that it may be helpful to specify a base URL or path for the input
         * document using {@link #baseUrl(String)}. This allows relative URLs and
         * paths in the document (e.g. for images) to be resolved correctly.
         * @param input The InputStream from which Prince will read the XML or HTML
         *              document.
         * @param output The OutputStream to which Prince will write the PDF output.
         * @return true if a PDF file was generated successfully.
         * @throws IOException If an I/O error occurs.
         */
        public boolean convert(InputStream input, OutputStream output) throws IOException {
            return cmdLine.convert(input, output, new Job(this));
        }

        /**
         * Convert multiple XML or HTML files to a PDF file by reading an input list
         * from a specified file. (An input list is a newline-separated sequence of
         * file paths / URLs.)
         * @param inputListPath The path to the input list file.
         * @param outputPath The filename of the output PDF file.
         * @return true if a PDF file was generated successfully.
         * @throws IOException If an I/O error occurs.
         */
        public boolean convertInputList(String inputListPath, String outputPath)
                throws IOException {
            return cmdLine.convertInputList(inputListPath, outputPath, new Job(this));
        }

        /**
         * Convert multiple XML or HTML files to a PDF file by reading an input list
         * from a specified file. (An input list is a newline-separated sequence of
         * file paths / URLs.) This method is useful for servlets as it allows
         * Prince to write the PDF output directly to the {@code OutputStream} of
         * the servlet response.
         * @param inputListPath The path to the input list file.
         * @param output The OutputStream to which Prince will write the PDF output.
         * @return true if a PDF file was generated successfully.
         * @throws IOException If an I/O error occurs.
         */
        public boolean convertInputList(String inputListPath, OutputStream output)
                throws IOException {
            return cmdLine.convertInputList(inputListPath, output, new Job(this));
        }

        /**
         * Convert an XML or HTML string to a PDF file.
         * @param input The XML or HTML document in the form of a String.
         * @param outputPath The filename of the output PDF file.
         * @return true if a PDF file was generated successfully.
         * @throws IOException If an I/O error occurs.
         */
        public boolean convertString(String input, String outputPath) throws IOException {
            return cmdLine.convertString(input, outputPath, new Job(this));
        }

        /**
         * Convert an XML or HTML string to a PDF file. This method is useful for
         * servlets as it allows Prince to write the PDF output directly to the
         * {@code OutputStream} of the servlet response.
         * @param input The XML or HTML document in the form of a String.
         * @param output The OutputStream to which Prince will write the PDF output.
         * @return true if a PDF file was generated successfully.
         * @throws IOException If an I/O error occurs.
         */
        public boolean convertString(String input, OutputStream output) throws IOException {
            return cmdLine.convertString(input, output, new Job(this));
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
            return cmdLine.rasterize(inputPath, outputPath, new Job(this));
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
            return cmdLine.rasterize(inputPaths, outputPath, new Job(this));
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
            return cmdLine.rasterize(inputPath, output, new Job(this));
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
            return cmdLine.rasterize(inputPaths, output, new Job(this));
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
            return cmdLine.rasterize(input, output, new Job(this));
        }

        /**
         * Rasterize multiple XML or HTML files by reading an input list from a
         * specified file. (An input list is a newline-separated sequence of file
         * paths / URLs.)
         * @param inputListPath The path to the input list file.
         * @param outputPath A template string from which the raster files will be named
         *                   (e.g. "page_%02d.png" will cause Prince to generate
         *                   page_01.png, page_02.png, ..., page_10.png etc.).
         * @return true if the input was successfully rasterized.
         * @throws IOException If an I/O error occurs.
         */
        public boolean rasterizeInputList(String inputListPath, String outputPath)
                throws IOException {
            return cmdLine.rasterizeInputList(inputListPath, outputPath, new Job(this));
        }

        /**
         * Rasterize multiple XML or HTML files by reading an input list from a
         * specified file. (An input list is a newline-separated sequence of file
         * paths / URLs.) This method is useful for servlets as it allows Prince to
         * write the raster output directly to the {@code OutputStream} of the
         * servlet response.
         * @param inputListPath The path to the input list file.
         * @param output The OutputStream to which Prince will write the raster output.
         * @return true if the input was successfully rasterized.
         * @throws IOException If an I/O error occurs.
         */
        public boolean rasterizeInputList(String inputListPath, OutputStream output)
                throws IOException {
            return cmdLine.rasterizeInputList(inputListPath, output, new Job(this));
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
            return cmdLine.rasterizeString(input, outputPath, new Job(this));
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
            return cmdLine.rasterizeString(input, output, new Job(this));
        }
    }
}
