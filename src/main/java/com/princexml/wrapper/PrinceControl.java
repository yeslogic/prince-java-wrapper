/*
 * Copyright (C) 2015, 2018, 2021-2022, 2026 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import com.princexml.wrapper.enums.InputType;
import com.princexml.wrapper.enums.PdfEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.princexml.wrapper.CommandLine.toCommand;

/**
 * Class for creating persistent Prince control processes that can be used
 * for multiple consecutive document conversions.
 */
public class PrinceControl extends PrinceBase {
    private Process process;
    private String version;

    private PrinceControl(Builder builder) {
        super(builder);
    }

    /**
     * Returns the builder for the {@link #PrinceControl} object.
     * @param princePath The path of the Prince executable. For example, this may be
     *                   <code>C:\Program&#xA0;Files\Prince\engine\bin\prince.exe</code>
     *                   on Windows or <code>/usr/bin/prince</code> on Linux.
     */
    public static Builder builder(String princePath) {
        return new Builder(princePath);
    }

    /**
     * Create a new job for {@link #PrinceControl}.
     */
    public Job newJob() {
        return new Job(this);
    }

    /**
     * Duplicate an existing job for {@link #PrinceControl}.
     * @param job The job to duplicate.
     */
    public Job newJob(Job job) {
        return new Job(job);
    }

    /**
     * Stop the Prince control process.
     * @throws IOException If an I/O error occurs.
     */
    public void stop() throws IOException {
        if (process == null) {
            throw new RuntimeException("control process has not been started");
        }

        try (OutputStream toPrince = process.getOutputStream()) {
            Chunk.writeChunk(toPrince, "end", "");
        }
        process.getInputStream().close();

        process.destroy();
    }

    /**
     * Get the version string for the running Prince process.
     * @return The version string.
     */
    public String getVersion() {
        return version;
    }

    private String buildJson(Job j) {
        Json json = new Json();
        json.beginObj();

        json.beginObj("input");
            json.beginList("src");
                j.inputPaths.forEach(json::value);
            json.endList();

            if (j.inputType != null) { json.field("type", j.inputType.toString()); }
            if (j.baseUrl != null) { json.field("base", j.baseUrl); }
            if (j.media != null) { json.field("media", j.media); }

            json.beginList("styles");
                j.styleSheets.forEach(json::value);
            json.endList();

            json.beginList("scripts");
                j.scripts.forEach(json::value);
            json.endList();

            json.field("default-style", !j.noDefaultStyle);
            json.field("author-style", !j.noAuthorStyle);
            json.field("javascript", j.javaScript);
            if (j.maxPasses > 0) { json.field("max-passes", j.maxPasses); }
            json.field("iframes", j.iframes);
            json.field("xinclude", j.xInclude);
            json.field("xml-external-entities", j.xmlExternalEntities);
        json.endObj();

        json.beginObj("pdf");
            json.field("embed-fonts", !j.noEmbedFonts);
            json.field("subset-fonts", !j.noSubsetFonts);
            json.field("artificial-fonts", !j.noArtificialFonts);
            json.field("force-identity-encoding", j.forceIdentityEncoding);
            json.field("compress", !j.noCompress);
            json.field("object-streams", !j.noObjectStreams);

            if (j.encrypt) {
                json.beginObj("encrypt");
                    if (j.keyBits != null) { json.field("key-bits", j.keyBits.getValue()); }
                    if (j.userPassword != null) { json.field("user-password", j.userPassword); }
                    if (j.ownerPassword != null) { json.field("owner-password", j.ownerPassword); }
                    json.field("disallow-print", j.disallowPrint);
                    json.field("disallow-modify", j.disallowModify);
                    json.field("disallow-copy", j.disallowCopy);
                    json.field("disallow-annotate", j.disallowAnnotate);
                    json.field("allow-copy-for-accessibility", j.allowCopyForAccessibility);
                    json.field("allow-assembly", j.allowAssembly);
                json.endObj();
            }

            if (j.pdfProfile != null) { json.field("pdf-profile", j.pdfProfile.toString()); }
            if (j.pdfOutputIntent != null) { json.field("pdf-output-intent", j.pdfOutputIntent); }
            if (j.pdfScript != null) {
                json.beginObj("pdf-script");
                    json.field("url", j.pdfScript);
                json.endObj();
            }
            if (!j.pdfEventScripts.isEmpty()) {
                json.beginObj("pdf-event-scripts");
                    j.pdfEventScripts.forEach((k, v) -> {
                        json.beginObj(k.toString());
                            json.field("url", v);
                        json.endObj();
                    });
                json.endObj();
            }
            if (j.fallbackCmykProfile != null) {
                json.field("fallback-cmyk-profile", j.fallbackCmykProfile);
            }
            json.field("color-conversion", j.convertColors ? "output-intent" : "none");
            if (j.pdfId != null) { json.field("pdf-id", j.pdfId); }
            if (j.pdfLang != null) { json.field("pdf-lang", j.pdfLang); }
            if (j.xmp != null) { json.field("pdf-xmp", j.xmp); }
            json.field("tagged-pdf", j.taggedPdf);
            json.field("pdf-forms", j.pdfForms);

            json.beginList("attach");
                for (FileAttachment fa : j.fileAttachments) {
                    json.beginObj();
                        json.field("url", fa.url);
                        if (fa.filename != null) { json.field("filename", fa.filename); }
                        if (fa.description != null) { json.field("description", fa.description); }
                    json.endObj();
                }
            json.endList();
        json.endObj();

        json.beginObj("metadata");
            if (j.pdfTitle != null) { json.field("title", j.pdfTitle); }
            if (j.pdfSubject != null) { json.field("subject", j.pdfSubject); }
            if (j.pdfAuthor != null) { json.field("author", j.pdfAuthor); }
            if (j.pdfKeywords != null) { json.field("keywords", j.pdfKeywords); }
            if (j.pdfCreator != null) { json.field("creator", j.pdfCreator); }
        json.endObj();

        json.field("job-resource-count", j.resources.size());

        json.endObj();
        return json.toString();
    }

    private boolean convert(String inputPath, OutputStream output, Job job) throws IOException {
        // XXX We're mutating a copy of `job`, but ideally we should avoid this.
        job.inputPaths.add(inputPath);
        return convert(output, job);
    }

    private boolean convert(List<String> inputPaths, OutputStream output, Job job)
            throws IOException {
        // XXX We're mutating a copy of `job`, but ideally we should avoid this.
        job.inputPaths.addAll(inputPaths);
        return convert(output, job);
    }

    private boolean convert(InputStream input, OutputStream output, Job job) throws IOException {
        if (job.inputType == null || job.inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Util.copyInputToOutput(input, baos);
            // XXX We're mutating a copy of `job`, but ideally we should avoid this.
            job.addResource(baos.toByteArray());
        }

        return convert(output, job);
    }

    private boolean convertString(String input, OutputStream output, Job job) throws IOException {
        if (job.inputType == null || job.inputType == InputType.AUTO) {
            throw new RuntimeException("inputType has to be set to XML or HTML");
        }

        // XXX We're mutating a copy of `job`, but ideally we should avoid this.
        job.addResource(input.getBytes(StandardCharsets.UTF_8));
        return convert(output, job);
    }

    private boolean convert(OutputStream output, Job job) throws IOException {
        if (process == null) {
            throw new RuntimeException("control process has not been started");
        }

        // These streams are closed in stop().
        OutputStream toPrince = process.getOutputStream();
        InputStream fromPrince = process.getInputStream();

        Chunk.writeChunk(toPrince, "job", buildJson(job));
        for (byte[] r : job.resources) {
            Chunk.writeChunk(toPrince, "dat", r);
        }
        toPrince.flush();

        Chunk chunk = Chunk.readChunk(fromPrince);
        if (chunk.getTag().equals("pdf")) {
            output.write(chunk.getBytes());
            chunk = Chunk.readChunk(fromPrince);
        }

        if (chunk.getTag().equals("log")) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(chunk.getBytes())))) {
                return readMessages(br);
            }
        } else if (chunk.getTag().equals("err")) {
            throw new IOException("error: " + chunk.getString());
        } else {
            throw new IOException("unknown chunk: " + chunk.getTag());
        }
    }

    private void start() throws IOException {
        if (process != null) {
            throw new RuntimeException("control process has already been started");
        }

        List<String> cmdLine = buildCommandLine();
        cmdLine.add(toCommand("control"));

        process = Util.invokeProcess(cmdLine);

        // This stream is closed in stop().
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

    public static class Builder extends PrinceBase.Builder<Builder> {
        private final List<String> inputPaths = new ArrayList<>();
        private final List<byte[]> resources = new ArrayList<>();

        private Builder(String princePath) {
            super(princePath);
        }

        /**
         * Start a Prince control process that can be used for multiple consecutive
         * document conversions.
         * @throws IOException If an I/O error occurs.
         */
        public PrinceControl start() throws IOException {
            PrinceControl control = new PrinceControl(this);

            control.start();

            return control;
        }
    }

    public static class Job extends PrinceBase.Job<Job> {
        private final PrinceControl control;

        private final List<String> inputPaths = new ArrayList<>();
        private final List<byte[]> resources = new ArrayList<>();

        private Job(PrinceControl control) {
            super();
            this.control = control;
        }

        private Job(Job job) {
            super(job);
            this.control = job.control;

            this.inputPaths.addAll(job.inputPaths);
            for (byte[] bytes : job.resources) {
                this.resources.add(bytes.clone());
            }
        }

        /**
         * See {@link #script(String)}.
         * @param script The script to run.
         */
        public Job script(byte[] script) {
            this.resources.add(script);
            super.script("job-resource:" + (resources.size() - 1));
            return this;
        }

        /**
         * See {@link #styleSheet(String)}.
         * @param styleSheet The stylesheet to apply.
         */
        public Job styleSheet(byte[] styleSheet) {
            this.resources.add(styleSheet);
            super.styleSheet("job-resource:" + (resources.size() - 1));
            return this;
        }

        /**
         * See {@link #pdfScript(String)}.
         * @param pdfScript The AcroJS script.
         */
        public Job pdfScript(byte[] pdfScript) {
            this.resources.add(pdfScript);
            super.pdfScript("job-resource:" + (resources.size() - 1));
            return this;
        }

        /**
         * See {@link #pdfEventScript(PdfEvent, String)}.
         * @param pdfEvent The PDF event.
         * @param pdfScript The AcroJS script.
         */
        public Job pdfEventScript(PdfEvent pdfEvent, byte[] pdfScript) {
            this.resources.add(pdfScript);
            super.pdfEventScript(pdfEvent, "job-resource:" + (resources.size() - 1));
            return this;
        }

        /**
         * See {@link #fileAttachment(String)}.
         * @param attachment The file to attach.
         * @param filename The file name.
         * @param description The file's description.
         */
        public Job fileAttachment(byte[] attachment, String filename, String description) {
            this.resources.add(attachment);
            super.fileAttachments.add(new FileAttachment(
                    "job-resource:" + (resources.size() - 1),
                    filename,
                    description
            ));
            return this;
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
            return control.convert(inputPath, output, new Job(this));
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
            return control.convert(inputPaths, output, new Job(this));
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
            return control.convert(input, output, new Job(this));
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
            return control.convertString(input, output, new Job(this));
        }

        private void addResource(byte[] resource) {
            this.resources.add(resource);
            this.inputPaths.add("job-resource:" + (this.resources.size() - 1));
        }
    }
}
