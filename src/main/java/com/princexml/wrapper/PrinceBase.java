/*
 * Copyright (C) 2026 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import com.princexml.wrapper.enums.*;
import com.princexml.wrapper.events.MessageType;
import com.princexml.wrapper.events.PrinceEvents;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static com.princexml.wrapper.CommandLine.toCommand;
import static com.princexml.wrapper.CommandLine.toCommands;

abstract class PrinceBase {
    protected final String princePath;
    protected final PrinceEvents events;

    // Logging options.
    protected final boolean verbose;
    protected final boolean debug;
    protected final String log;
    protected final boolean noWarnCssUnknown;
    protected final boolean noWarnCssUnsupported;

    // Input options.
    protected final boolean noLocalFiles;

    // Network options.
    protected final boolean noNetwork;
    protected final boolean noRedirects;
    protected final String authUser;
    protected final String authPassword;
    protected final String authServer;
    protected final AuthScheme authScheme;
    protected final List<AuthMethod> authMethods;
    protected final boolean noAuthPreemptive;
    protected final String httpProxy;
    protected final int httpTimeout;
    protected final List<String> cookies;
    protected final String cookieJar;
    protected final String sslCaCert;
    protected final String sslCaPath;
    protected final String sslCert;
    protected final SslType sslCertType;
    protected final String sslKey;
    protected final SslType sslKeyType;
    protected final String sslKeyPassword;
    protected final SslVersion sslVersion;
    protected final boolean insecure;
    protected final boolean noParallelDownloads;

    // License options.
    protected final String licenseFile;
    protected final String licenseKey;

    // Advanced options.
    protected final boolean failDroppedContent;
    protected final boolean failMissingResources;
    protected final boolean failStrippedTransparency;
    protected final boolean failMissingGlyphs;
    protected final boolean failPdfProfileError;
    protected final boolean failPdfTagError;
    protected final boolean failInvalidLicense;

    protected PrinceBase(Builder<?> builder) {
        this.princePath = builder.princePath;
        this.events = builder.events;

        // Logging options.
        this.verbose = builder.verbose;
        this.debug = builder.debug;
        this.log = builder.log;
        this.noWarnCssUnknown = builder.noWarnCssUnknown;
        this.noWarnCssUnsupported = builder.noWarnCssUnsupported;

        // Input options.
        this.noLocalFiles = builder.noLocalFiles;

        // Network options.
        this.noNetwork = builder.noNetwork;
        this.noRedirects = builder.noRedirects;
        this.authUser = builder.authUser;
        this.authPassword = builder.authPassword;
        this.authServer = builder.authServer;
        this.authScheme = builder.authScheme;
        this.authMethods = Collections.unmodifiableList(new ArrayList<>(builder.authMethods));
        this.noAuthPreemptive = builder.noAuthPreemptive;
        this.httpProxy = builder.httpProxy;
        this.httpTimeout = builder.httpTimeout;
        this.cookies = Collections.unmodifiableList(new ArrayList<>(builder.cookies));
        this.cookieJar = builder.cookieJar;
        this.sslCaCert = builder.sslCaCert;
        this.sslCaPath = builder.sslCaPath;
        this.sslCert = builder.sslCert;
        this.sslCertType = builder.sslCertType;
        this.sslKey = builder.sslKey;
        this.sslKeyType = builder.sslKeyType;
        this.sslKeyPassword = builder.sslKeyPassword;
        this.sslVersion = builder.sslVersion;
        this.insecure = builder.insecure;
        this.noParallelDownloads = builder.noParallelDownloads;

        // License options.
        this.licenseFile = builder.licenseFile;
        this.licenseKey = builder.licenseKey;

        // Advanced options.
        this.failDroppedContent = builder.failDroppedContent;
        this.failMissingResources = builder.failMissingResources;
        this.failStrippedTransparency = builder.failStrippedTransparency;
        this.failMissingGlyphs = builder.failMissingGlyphs;
        this.failPdfProfileError = builder.failPdfProfileError;
        this.failPdfTagError = builder.failPdfTagError;
        this.failInvalidLicense = builder.failInvalidLicense;
    }

    protected List<String> buildCommandLine() {
        List<String> cmdLine = new ArrayList<>();

        cmdLine.add(princePath);

        // Logging options.
        if (verbose) { cmdLine.add(toCommand("verbose")); }
        if (debug) { cmdLine.add(toCommand("debug")); }
        if (log != null) { cmdLine.add(toCommand("log", log)); }
        if (noWarnCssUnknown) { cmdLine.add(toCommand("no-warn-css-unknown")); }
        if (noWarnCssUnsupported) { cmdLine.add(toCommand("no-warn-css-unsupported")); }

        // Input options.
        if (noLocalFiles) { cmdLine.add(toCommand("no-local-files")); }

        // Network options.
        if (noNetwork) { cmdLine.add(toCommand("no-network")); }
        if (noRedirects) { cmdLine.add(toCommand("no-redirects")); }
        if (authUser != null) { cmdLine.add(toCommand("auth-user", authUser)); }
        if (authPassword != null) { cmdLine.add(toCommand("auth-password", authPassword)); }
        if (authServer != null) { cmdLine.add(toCommand("auth-server", authServer)); }
        if (authScheme != null) { cmdLine.add(toCommand("auth-scheme", authScheme)); }
        if (!authMethods.isEmpty()) { cmdLine.add(toCommand("auth-method", authMethods)); }
        if (noAuthPreemptive) { cmdLine.add(toCommand("no-auth-preemptive")); }
        if (httpProxy != null) { cmdLine.add(toCommand("http-proxy", httpProxy)); }
        if (httpTimeout > 0) { cmdLine.add(toCommand("http-timeout", httpTimeout)); }
        if (!cookies.isEmpty()) { cmdLine.addAll(toCommands("cookie", cookies)); }
        if (cookieJar != null) { cmdLine.add(toCommand("cookiejar", cookieJar)); }
        if (sslCaCert != null) { cmdLine.add(toCommand("ssl-cacert", sslCaCert)); }
        if (sslCaPath != null) { cmdLine.add(toCommand("ssl-capath", sslCaPath)); }
        if (sslCert != null) { cmdLine.add(toCommand("ssl-cert", sslCert)); }
        if (sslCertType != null) { cmdLine.add(toCommand("ssl-cert-type", sslCertType)); }
        if (sslKey != null) { cmdLine.add(toCommand("ssl-key", sslKey)); }
        if (sslKeyType != null) { cmdLine.add(toCommand("ssl-key-type", sslKeyType)); }
        if (sslKeyPassword != null) { cmdLine.add(toCommand("ssl-key-password", sslKeyPassword)); }
        if (sslVersion != null) { cmdLine.add(toCommand("ssl-version", sslVersion)); }
        if (insecure) { cmdLine.add(toCommand("insecure")); }
        if (noParallelDownloads) { cmdLine.add(toCommand("no-parallel-downloads")); }

        // License options.
        if (licenseFile != null) { cmdLine.add(toCommand("license-file", licenseFile)); }
        if (licenseKey != null) { cmdLine.add(toCommand("license-key", licenseKey)); }

        // Advanced options.
        if (failDroppedContent) { cmdLine.add(toCommand("fail-dropped-content")); }
        if (failMissingResources) { cmdLine.add(toCommand("fail-missing-resources")); }
        if (failStrippedTransparency) { cmdLine.add(toCommand("fail-stripped-transparency")); }
        if (failMissingGlyphs) { cmdLine.add(toCommand("fail-missing-glyphs")); }
        if (failPdfProfileError) { cmdLine.add(toCommand("fail-pdf-profile-error")); }
        if (failPdfTagError) { cmdLine.add(toCommand("fail-pdf-tag-error")); }
        if (failInvalidLicense) { cmdLine.add(toCommand("fail-invalid-license")); }

        return cmdLine;
    }

    protected boolean readMessages(BufferedReader reader) throws IOException {
        String result = "";
        String line = reader.readLine();

        while (line != null) {
            String[] tokens = line.split("\\|", 2);
            if (tokens.length == 2) {
                String msgTag = tokens[0];
                String msgBody = tokens[1];

                switch (msgTag) {
                    case "msg":
                        if (events != null) {
                            handleMessage(msgBody);
                        }
                        break;
                    case "dat":
                        if (events != null) {
                            handleDataMessage(msgBody);
                        }
                        break;
                    case "fin":
                        result = msgBody;
                        break;
                }
            } else {
                handleNonStructuredMessage(line);
            }

            line = reader.readLine();
        }

        return result.equals("success");
    }

    private void handleMessage(String msgBody) {
        if (events == null) { return; }

        String[] tokens = msgBody.split("\\|", 3);
        if (tokens.length == 3) {
            MessageType msgType = MessageType.valueOf(tokens[0].toUpperCase());
            String msgLocation = tokens[1];
            String msgText = tokens[2];

            events.onMessage(msgType, msgLocation, msgText);
        }
    }

    private void handleDataMessage(String msgBody) {
        if (events == null) { return; }

        String[] tokens = msgBody.split("\\|", 2);
        if (tokens.length == 2) {
            String name = tokens[0];
            String value = tokens[1];

            events.onDataMessage(name, value);
        }
    }

    private void handleNonStructuredMessage(String msg) {
        if (events == null) { return; }

        String princeWrn = "prince: warning: ";
        String princeErr = "prince: error: ";

        if (msg.startsWith(princeWrn)) {
            String msgText = msg.substring(princeWrn.length());
            events.onMessage(MessageType.WRN, "", msgText);
        } else if (msg.startsWith(princeErr)) {
            String msgText = msg.substring(princeErr.length());
            events.onMessage(MessageType.ERR, "", msgText);
        } else {
            // Just treat everything else as debug messages.
            events.onMessage(MessageType.DBG, "", msg);
        }
    }

    abstract static class Builder<T extends Builder<T>> {
        private final String princePath;
        private PrinceEvents events = null;

        // Logging options.
        private boolean verbose = false;
        private boolean debug = false;
        private String log = null;
        private boolean noWarnCssUnknown = false;
        private boolean noWarnCssUnsupported = false;

        // Input options.
        private boolean noLocalFiles = false;

        // Network options.
        private boolean noNetwork = false;
        private boolean noRedirects = false;
        private String authUser = null;
        private String authPassword = null;
        private String authServer = null;
        private AuthScheme authScheme = null;
        private final List<AuthMethod> authMethods = new ArrayList<>();
        private boolean noAuthPreemptive = false;
        private String httpProxy = null;
        private int httpTimeout = 0;
        private final List<String> cookies = new ArrayList<>();
        private String cookieJar = null;
        private String sslCaCert = null;
        private String sslCaPath = null;
        private String sslCert = null;
        private SslType sslCertType = null;
        private String sslKey = null;
        private SslType sslKeyType = null;
        private String sslKeyPassword = null;
        private SslVersion sslVersion = null;
        private boolean insecure = false;
        private boolean noParallelDownloads = false;

        // License options.
        private String licenseFile = null;
        private String licenseKey = null;

        // Advanced options.
        private boolean failDroppedContent = false;
        private boolean failMissingResources = false;
        private boolean failStrippedTransparency = false;
        private boolean failMissingGlyphs = false;
        private boolean failPdfProfileError = false;
        private boolean failPdfTagError = false;
        private boolean failInvalidLicense = false;

        protected Builder(String princePath) {
            this.princePath = princePath;
        }

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        /**
         * A handler to receive messages from Prince.
         * @param events An implementation of {@link com.princexml.wrapper.events.PrinceEvents}.
         */
        public T events(PrinceEvents events) {
            this.events = events;
            return self();
        }

        /**
         * Enable logging of informative messages. Default value is {@code false}.
         * @param verbose true to enable verbose logging.
         */
        public T verbose(boolean verbose) {
            this.verbose = verbose;
            return self();
        }

        /**
         * Enable logging of debug messages. Default value is {@code false}.
         * @param debug true to enable debug logging.
         */
        public T debug(boolean debug) {
            this.debug = debug;
            return self();
        }

        /**
         * Specify a file that Prince should use to log messages. If this method
         * is not called then Prince will not write to any log. This method does
         * not affect the operation of {@link com.princexml.wrapper.events.PrinceEvents},
         * which will also receive messages from Prince.
         * @param log The filename that Prince should use to log messages.
         */
        public T log(String log) {
            this.log = log;
            return self();
        }

        /**
         * Disable warnings about unknown CSS features. Default value is {@code false}.
         * @param noWarnCssUnknown true to disable warnings.
         */
        public T noWarnCssUnknown(boolean noWarnCssUnknown) {
            this.noWarnCssUnknown = noWarnCssUnknown;
            return self();
        }

        /**
         * Disable warnings about unsupported CSS features. Default value is {@code false}.
         * @param noWarnCssUnsupported true to disable warnings.
         */
        public T noWarnCssUnsupported(boolean noWarnCssUnsupported) {
            this.noWarnCssUnsupported = noWarnCssUnsupported;
            return self();
        }

        /**
         * Disable access to local files. Default value is {@code false}.
         * @param noLocalFiles true to disable local files.
         */
        public T noLocalFiles(boolean noLocalFiles) {
            this.noLocalFiles = noLocalFiles;
            return self();
        }

        /**
         * Disable network access (prevents HTTP downloads). Default value is {@code false}.
         * @param noNetwork true to disable network access.
         */
        public T noNetwork(boolean noNetwork) {
            this.noNetwork = noNetwork;
            return self();
        }

        /**
         * Disable all HTTP and HTTPS redirects. Default value is {@code false}.
         * @param noRedirects true to disable redirects.
         */
        public T noRedirects(boolean noRedirects) {
            this.noRedirects = noRedirects;
            return self();
        }

        /**
         * Specify the username for HTTP authentication.
         * @param authUser The username for authentication.
         */
        public T authUser(String authUser) {
            this.authUser = authUser;
            return self();
        }

        /**
         * Specify the password for HTTP authentication.
         * @param authPassword The password for authentication.
         */
        public T authPassword(String authPassword) {
            this.authPassword = authPassword;
            return self();
        }

        /**
         * Send username and password credentials to the specified server only.
         * The default is to send them to any server which challenges for authentication.
         * @param authServer The server to send credentials to (e.g. "localhost:8001").
         */
        public T authServer(String authServer) {
            this.authServer = authServer;
            return self();
        }

        /**
         * Send username and password credentials only for requests with the given
         * scheme.
         * @param authScheme The authentication scheme.
         */
        public T authScheme(AuthScheme authScheme) {
            this.authScheme = authScheme;
            return self();
        }

        /**
         * Specify an HTTP authentication method to enable. This method can be called
         * more than once to add multiple authentication methods.
         * @param authMethod The authentication method to enable.
         */
        public T authMethod(AuthMethod authMethod) {
            this.authMethods.add(authMethod);
            return self();
        }

        /**
         * Do not authenticate with named servers until asked. Default value is {@code false}.
         * @param noAuthPreemptive true to disable authentication preemptive.
         */
        public T noAuthPreemptive(boolean noAuthPreemptive) {
            this.noAuthPreemptive = noAuthPreemptive;
            return self();
        }

        /**
         * Specify the URL for the HTTP proxy server, if needed.
         * @param httpProxy The URL for the HTTP proxy server.
         */
        public T httpProxy(String httpProxy) {
            this.httpProxy = httpProxy;
            return self();
        }

        /**
         * Specify the timeout for HTTP requests. Default value is 60 seconds.
         * @param httpTimeout The HTTP timeout in seconds. Value must be greater than 0.
         */
        public T httpTimeout(int httpTimeout) {
            if (httpTimeout < 1) {
                throw new IllegalArgumentException("invalid httpTimeout value (must be > 0)");
            }
            this.httpTimeout = httpTimeout;
            return self();
        }

        /**
         * Add a value for the {@code Set-Cookie} HTTP header value. This method can
         * be called more than once to add multiple cookies.
         * @param cookie The cookie to be added.
         */
        public T cookie(String cookie) {
            this.cookies.add(cookie);
            return self();
        }

        /**
         * Specify a file containing HTTP cookies.
         * @param cookieJar The filename of the file containing HTTP cookies.
         */
        public T cookieJar(String cookieJar) {
            this.cookieJar = cookieJar;
            return self();
        }

        /**
         * Specify an SSL certificate file.
         * @param sslCaCert The filename of the SSL certificate file.
         */
        public T sslCaCert(String sslCaCert) {
            this.sslCaCert = sslCaCert;
            return self();
        }

        /**
         * Specify an SSL certificate directory.
         * @param sslCaPath The SSL certificate directory.
         */
        public T sslCaPath(String sslCaPath) {
            this.sslCaPath = sslCaPath;
            return self();
        }

        /**
         * Specify an SSL client certificate file. On macOS, specify a PKCS#12 file
         * containing a client certificate and private key. Client authentication is
         * not supported on Windows.
         * @param sslCert The filename of the SSL client certificate file.
         */
        public T sslCert(String sslCert) {
            this.sslCert = sslCert;
            return self();
        }

        /**
         * Specify the SSL client certificate file type. This option is not supported
         * on macOS or Windows.
         * @param sslCertType The SSL client certificate file type.
         */
        public T sslCertType(SslType sslCertType) {
            this.sslCertType = sslCertType;
            return self();
        }

        /**
         * Specify an SSL private key file. This option is not supported on macOS or
         * Windows.
         * @param sslKey The filename of the SSL private key file.
         */
        public T sslKey(String sslKey) {
            this.sslKey = sslKey;
            return self();
        }

        /**
         * Specify the SSL private key file type. This option is not supported on macOS
         * or Windows.
         * @param sslKeyType The SSL private key file type.
         */
        public T sslKeyType(SslType sslKeyType) {
            this.sslKeyType = sslKeyType;
            return self();
        }

        /**
         * Specify a password for the SSL private key.
         * @param sslKeyPassword The password for the SSL private key.
         */
        public T sslKeyPassword(String sslKeyPassword) {
            this.sslKeyPassword = sslKeyPassword;
            return self();
        }

        /**
         * Set the minimum version of SSL to allow. Default value is
         * {@link com.princexml.wrapper.enums.SslVersion#DEFAULT}.
         * @param sslVersion The minimum version to allow.
         */
        public T sslVersion(SslVersion sslVersion) {
            this.sslVersion = sslVersion;
            return self();
        }

        /**
         * Specify whether to disable SSL verification. Default value is {@code false}.
         * @param insecure true to disable SSL verification (not recommended).
         */
        public T insecure(boolean insecure) {
            this.insecure = insecure;
            return self();
        }

        /**
         * Disable downloading multiple HTTP resources at once. Default value is {@code false}.
         * @param noParallelDownloads true to disable parallel downloads.
         */
        public T noParallelDownloads(boolean noParallelDownloads) {
            this.noParallelDownloads = noParallelDownloads;
            return self();
        }

        /**
         * Specify the license file.
         * @param licenseFile The filename of the license file.
         */
        public T licenseFile(String licenseFile) {
            this.licenseFile = licenseFile;
            return self();
        }

        /**
         * Specify the license key. This is the {@code <signature>} field in the
         * license file.
         * @param licenseKey The license key.
         */
        public T licenseKey(String licenseKey) {
            this.licenseKey = licenseKey;
            return self();
        }

        /**
         * Fail-safe option that aborts the creation of a PDF if any content is
         * dropped. Default value is {@code false}.
         * @param failDroppedContent true to enable fail-safe option.
         */
        public T failDroppedContent(boolean failDroppedContent) {
            this.failDroppedContent = failDroppedContent;
            return self();
        }

        /**
         * Fail-safe option that aborts the creation of a PDF if any resources
         * cannot be loaded. Default value is {@code false}.
         * @param failMissingResources true to enable fail-safe option.
         */
        public T failMissingResources(boolean failMissingResources) {
            this.failMissingResources = failMissingResources;
            return self();
        }

        /**
         * Fail-safe option that aborts the creation of a PDF if transparent
         * images are used with a PDF profile that does not support opacity.
         * Default value is {@code false}.
         * @param failStrippedTransparency true to enable fail-safe option.
         */
        public T failStrippedTransparency(boolean failStrippedTransparency) {
            this.failStrippedTransparency = failStrippedTransparency;
            return self();
        }

        /**
         * Fail-safe option that aborts the creation of a PDF if glyphs cannot
         * be found for any characters. Default value is {@code false}.
         * @param failMissingGlyphs true to enable fail-safe option.
         */
        public T failMissingGlyphs(boolean failMissingGlyphs) {
            this.failMissingGlyphs = failMissingGlyphs;
            return self();
        }

        /**
         * Fail-safe option that aborts the creation of a PDF if there are
         * problems complying with the specified PDF profile. Default value is
         * {@code false}.
         * @param failPdfProfileError true to enable fail-safe option.
         */
        public T failPdfProfileError(boolean failPdfProfileError) {
            this.failPdfProfileError = failPdfProfileError;
            return self();
        }

        /**
         * Fail-safe option that aborts the creation of a PDF if there are
         * problems tagging the PDF for accessibility. Default value is {@code false}.
         * @param failPdfTagError true to enable fail-safe option.
         */
        public T failPdfTagError(boolean failPdfTagError) {
            this.failPdfTagError = failPdfTagError;
            return self();
        }

        /**
         * Fail-safe option that aborts the creation of a PDF if the Prince
         * license is invalid or not readable. Default value is {@code false}.
         * @param failInvalidLicense true to enable fail-safe option.
         */
        public T failInvalidLicense(boolean failInvalidLicense) {
            this.failInvalidLicense = failInvalidLicense;
            return self();
        }

        /**
         * Enables/disables all fail-safe options.
         * @param failSafe true to enable all fail-safe options.
         */
        public T failSafe(boolean failSafe) {
            this.failDroppedContent = failSafe;
            this.failMissingResources = failSafe;
            this.failStrippedTransparency = failSafe;
            this.failMissingGlyphs = failSafe;
            this.failPdfProfileError = failSafe;
            this.failPdfTagError = failSafe;
            this.failInvalidLicense = failSafe;
            return self();
        }
    }

    abstract static class Job<T extends Job<T>> {
        // Input options.
        InputType inputType = null;
        String baseUrl = null;
        boolean iframes = false;
        boolean xInclude = false;
        boolean xmlExternalEntities = false;

        // JavaScript options.
        boolean javaScript = false;
        final List<String> scripts = new ArrayList<>();
        int maxPasses = 0;

        // CSS options.
        final List<String> styleSheets = new ArrayList<>();
        String media = null;
        boolean noAuthorStyle = false;
        boolean noDefaultStyle = false;

        // PDF output options.
        String pdfId = null;
        String pdfLang = null;
        PdfProfile pdfProfile = null;
        String pdfOutputIntent = null;
        String pdfScript = null;
        final Map<PdfEvent, String> pdfEventScripts = new HashMap<>();
        final List<FileAttachment> fileAttachments = new ArrayList<>();
        boolean noArtificialFonts = false;
        boolean noEmbedFonts = false;
        boolean noSubsetFonts = false;
        boolean forceIdentityEncoding = false;
        boolean noCompress = false;
        boolean noObjectStreams = false;
        boolean convertColors = false;
        String fallbackCmykProfile = null;
        boolean taggedPdf = false;
        boolean pdfForms = false;

        // PDF metadata options.
        String pdfTitle = null;
        String pdfSubject = null;
        String pdfAuthor = null;
        String pdfKeywords = null;
        String pdfCreator = null;
        String xmp = null;

        // PDF encryption options.
        boolean encrypt = false;
        KeyBits keyBits = null;
        String userPassword = null;
        String ownerPassword = null;
        boolean disallowPrint = false;
        boolean disallowCopy = false;
        boolean allowCopyForAccessibility = false;
        boolean disallowAnnotate = false;
        boolean disallowModify = false;
        boolean allowAssembly = false;

        protected Job() {}

        protected Job(Job<?> job) {
            // Input options.
            this.inputType = job.inputType;
            this.baseUrl = job.baseUrl;
            this.iframes = job.iframes;
            this.xInclude = job.xInclude;
            this.xmlExternalEntities = job.xmlExternalEntities;

            // JavaScript options.
            this.javaScript = job.javaScript;
            this.scripts.addAll(job.scripts);
            this.maxPasses = job.maxPasses;

            // CSS options.
            this.styleSheets.addAll(job.styleSheets);
            this.media = job.media;
            this.noAuthorStyle = job.noAuthorStyle;
            this.noDefaultStyle = job.noDefaultStyle;

            // PDF output options.
            this.pdfId = job.pdfId;
            this.pdfLang = job.pdfLang;
            this.pdfProfile = job.pdfProfile;
            this.pdfOutputIntent = job.pdfOutputIntent;
            this.pdfScript = job.pdfScript;
            this.pdfEventScripts.putAll(job.pdfEventScripts);
            this.fileAttachments.addAll(job.fileAttachments);
            this.noArtificialFonts = job.noArtificialFonts;
            this.noEmbedFonts = job.noEmbedFonts;
            this.noSubsetFonts = job.noSubsetFonts;
            this.forceIdentityEncoding = job.forceIdentityEncoding;
            this.noCompress = job.noCompress;
            this.noObjectStreams = job.noObjectStreams;
            this.convertColors = job.convertColors;
            this.fallbackCmykProfile = job.fallbackCmykProfile;
            this.taggedPdf = job.taggedPdf;
            this.pdfForms = job.pdfForms;

            // PDF metadata options.
            this.pdfTitle = job.pdfTitle;
            this.pdfSubject = job.pdfSubject;
            this.pdfAuthor = job.pdfAuthor;
            this.pdfKeywords = job.pdfKeywords;
            this.pdfCreator = job.pdfCreator;
            this.xmp = job.xmp;

            // PDF encryption options.
            this.encrypt = job.encrypt;
            this.keyBits = job.keyBits;
            this.userPassword = job.userPassword;
            this.ownerPassword = job.ownerPassword;
            this.disallowPrint = job.disallowPrint;
            this.disallowCopy = job.disallowCopy;
            this.allowCopyForAccessibility = job.allowCopyForAccessibility;
            this.disallowAnnotate = job.disallowAnnotate;
            this.disallowModify = job.disallowModify;
            this.allowAssembly = job.allowAssembly;
        }

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        /**
         * Specify the input type of the document. Default value is
         * {@link com.princexml.wrapper.enums.InputType#AUTO}.
         * <p>
         * Setting this to {@link com.princexml.wrapper.enums.InputType#XML} or
         * {@link com.princexml.wrapper.enums.InputType#HTML} is required if a
         * document is provided via an {@code InputStream} or {@code String}, as
         * the types of these documents cannot be determined.
         * @param inputType The document's input type.
         */
        public T inputType(InputType inputType) {
            this.inputType = inputType;
            return self();
        }

        /**
         * Specify the base URL of the input document. This can be used to override
         * the path of the input document, which is convenient when processing local
         * copies of a document from a website.
         * <p>
         * It is also helpful for specifying a base URL for documents that are
         * provided via an {@code InputStream} or {@code String}, as these documents
         * have no natural base URL.
         * @param baseUrl The base URL or path of the input document.
         */
        public T baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return self();
        }

        /**
         * Enable HTML iframes. Default value is {@code false}.
         * @param iframes true to enable HTML iframes.
         */
        public T iframes(boolean iframes) {
            this.iframes = iframes;
            return self();
        }

        /**
         * Enable XInclude and XML external entities (XXE). Note that XInclude only
         * applies to XML files. To apply it to HTML files, the input format needs
         * to be specified with {@link #inputType(InputType)}. Default value is
         * {@code false}.
         * @param xInclude true to enable XInclude and XXE.
         */
        public T xInclude(boolean xInclude) {
            this.xInclude = xInclude;
            return self();
        }

        /**
         * Enable XML external entities (XXE). Default value is {@code false}.
         * @param xmlExternalEntities true to enable XXE.
         */
        public T xmlExternalEntities(boolean xmlExternalEntities) {
            this.xmlExternalEntities = xmlExternalEntities;
            return self();
        }

        /**
         * Specify whether JavaScript found in documents should be run. Default value is
         * {@code false}.
         * @param javaScript true if document scripts should be run.
         */
        public T javaScript(boolean javaScript) {
            this.javaScript = javaScript;
            return self();
        }

        /**
         * Add a JavaScript script that will be run before conversion. This
         * method can be called more than once to add multiple scripts.
         * @param script The filename of the script to run.
         */
        public T script(String script) {
            this.scripts.add(script);
            return self();
        }

        /**
         * Defines the maximum number of consequent layout passes. Default value is
         * unlimited passes.
         * @param maxPasses The number of maximum passes. Value must be greater than 0.
         */
        public T maxPasses(int maxPasses) {
            if (maxPasses < 1) {
                throw new IllegalArgumentException("invalid maxPasses value (must be > 0)");
            }
            this.maxPasses = maxPasses;
            return self();
        }

        /**
         * Add a CSS style sheet that will be applied to each input document. This
         * method can be called more than once to add multiple style sheets.
         * @param styleSheet The filename of the CSS style sheet to apply.
         */
        public T styleSheet(String styleSheet) {
            this.styleSheets.add(styleSheet);
            return self();
        }

        /**
         * Specify the media type. Default value is {@code "print"}.
         * @param media The media type (e.g. "print", "screen").
         */
        public T media(String media) {
            this.media = media;
            return self();
        }

        /**
         * Ignore author style sheets. Default value is {@code false}.
         * @param noAuthorStyle true to ignore author style sheets.
         */
        public T noAuthorStyle(boolean noAuthorStyle) {
            this.noAuthorStyle = noAuthorStyle;
            return self();
        }

        /**
         * Ignore default style sheets. Default value is {@code false}.
         * @param noDefaultStyle true to ignore default style sheets.
         */
        public T noDefaultStyle(boolean noDefaultStyle) {
            this.noDefaultStyle = noDefaultStyle;
            return self();
        }

        /**
         * Specify the PDF ID to use.
         * @param pdfId The PDF ID.
         */
        public T pdfId(String pdfId) {
            this.pdfId = pdfId;
            return self();
        }

        /**
         * Specify the PDF document's Lang entry in the document catalog.
         * @param pdfLang The PDF document's lang entry.
         */
        public T pdfLang(String pdfLang) {
            this.pdfLang = pdfLang;
            return self();
        }

        /**
         * Specify the PDF profile to use.
         * @param pdfProfile The PDF profile.
         */
        public T pdfProfile(PdfProfile pdfProfile) {
            this.pdfProfile = pdfProfile;
            return self();
        }

        /**
         * Specify the ICC profile to use.
         * @param pdfOutputIntent The ICC profile.
         */
        public T pdfOutputIntent(String pdfOutputIntent) {
            this.pdfOutputIntent = pdfOutputIntent;
            return self();
        }

        /**
         * Include an AcroJS script to run when the PDF is opened.
         * @param pdfScript The filename or URL of the AcroJS script.
         */
        public T pdfScript(String pdfScript) {
            this.pdfScript = pdfScript;
            return self();
        }

        /**
         * Include an AcroJS script to run on a specific event.
         * @param pdfEvent The PDF event.
         * @param pdfScript The filename or URL of the AcroJS script.
         */
        public T pdfEventScript(PdfEvent pdfEvent, String pdfScript) {
            this.pdfEventScripts.put(pdfEvent, pdfScript);
            return self();
        }

        /**
         * Add a file attachment that will be attached to the PDF file. This method
         * can be called more than once to add multiple file attachments.
         * @param fileAttachment The filename of the file attachment.
         */
        public T fileAttachment(String fileAttachment) {
            this.fileAttachments.add(new FileAttachment(fileAttachment));
            return self();
        }

        /**
         * Specify whether artificial bold/italic fonts should be generated if
         * necessary. Default value is {@code false}.
         * @param noArtificialFonts true to disable artificial bold/italic fonts.
         */
        public T noArtificialFonts(boolean noArtificialFonts) {
            this.noArtificialFonts = noArtificialFonts;
            return self();
        }

        /**
         * Specify whether fonts should be embedded in the output PDF file. Default
         * value is {@code false}.
         * @param noEmbedFonts true to disable PDF font embedding.
         */
        public T noEmbedFonts(boolean noEmbedFonts) {
            this.noEmbedFonts = noEmbedFonts;
            return self();
        }

        /**
         * Specify whether embedded fonts should be subset in the output PDF file.
         * Default value is {@code false}.
         * @param noSubsetFonts true to disable PDF font subsetting.
         */
        public T noSubsetFonts(boolean noSubsetFonts) {
            this.noSubsetFonts = noSubsetFonts;
            return self();
        }

        /**
         * Ensure that all fonts are encoded in the PDF using their identity encoding
         * (directly mapping to glyph indices), even if they could have used MacRoman
         * or some other encoding. Default value is {@code false}.
         * @param forceIdentityEncoding true to force identity encoding.
         */
        public T forceIdentityEncoding(boolean forceIdentityEncoding) {
            this.forceIdentityEncoding = forceIdentityEncoding;
            return self();
        }

        /**
         * Specify whether compression should be applied to the output PDF file.
         * Default value is {@code false}.
         * @param noCompress true to disable PDF compression.
         */
        public T noCompress(boolean noCompress) {
            this.noCompress = noCompress;
            return self();
        }

        /**
         * Disable PDF object streams. Default value is {@code false}.
         * @param noObjectStreams true to disable PDF object streams.
         */
        public T noObjectStreams(boolean noObjectStreams) {
            this.noObjectStreams = noObjectStreams;
            return self();
        }

        /**
         * Convert colors to output intent color space. Default value is {@code false}.
         * @param convertColors true to convert colors to output intent color space.
         */
        public T convertColors(boolean convertColors) {
            this.convertColors = convertColors;
            return self();
        }

        /**
         * Set fallback ICC profile for uncalibrated CMYK.
         * @param fallbackCmykProfile The fallback ICC profile.
         */
        public T fallbackCmykProfile(String fallbackCmykProfile) {
            this.fallbackCmykProfile = fallbackCmykProfile;
            return self();
        }

        /**
         * Enable tagged PDF. Default value is {@code false}.
         * @param taggedPdf true to enable tagged PDF.
         */
        public T taggedPdf(boolean taggedPdf) {
            this.taggedPdf = taggedPdf;
            return self();
        }

        /**
         * Enable PDF forms by default. Default value is {@code false}.
         * @param pdfForms true to enable PDF forms.
         */
        public T pdfForms(boolean pdfForms) {
            this.pdfForms = pdfForms;
            return self();
        }

        /**
         * Specify the document title for PDF metadata.
         * @param pdfTitle The document title.
         */
        public T pdfTitle(String pdfTitle) {
            this.pdfTitle = pdfTitle;
            return self();
        }

        /**
         * Specify the document subject for PDF metadata.
         * @param pdfSubject The document subject.
         */
        public T pdfSubject(String pdfSubject) {
            this.pdfSubject = pdfSubject;
            return self();
        }

        /**
         * Specify the document author for PDF metadata.
         * @param pdfAuthor The document author.
         */
        public T pdfAuthor(String pdfAuthor) {
            this.pdfAuthor = pdfAuthor;
            return self();
        }

        /**
         * Specify the document keywords for PDF metadata.
         * @param pdfKeywords The document keywords.
         */
        public T pdfKeywords(String pdfKeywords) {
            this.pdfKeywords = pdfKeywords;
            return self();
        }

        /**
         * Specify the document creator for PDF metadata.
         * @param pdfCreator The document creator.
         */
        public T pdfCreator(String pdfCreator) {
            this.pdfCreator = pdfCreator;
            return self();
        }

        /**
         * Specify an XMP file that contains XMP metadata to be included in the
         * output PDF file.
         * @param xmp The filename of the XMP file.
         */
        public T xmp(String xmp) {
            this.xmp = xmp;
            return self();
        }

        /**
         * Specify whether encryption should be applied to the output file.
         * Default value is {@code false}.
         * @param encrypt true to enable PDF encryption.
         */
        public T encrypt(boolean encrypt) {
            this.encrypt = encrypt;
            return self();
        }

        /**
         * Specify the size of the encryption key. Default value is
         * {@link com.princexml.wrapper.enums.KeyBits#BITS128}.
         * @param keyBits The size of the encryption key.
         */
        public T keyBits(KeyBits keyBits) {
            this.keyBits = keyBits;
            return self();
        }

        /**
         * Specify the user password for the PDF file.
         * @param userPassword The user password.
         */
        public T userPassword(String userPassword) {
            this.userPassword = userPassword;
            return self();
        }

        /**
         * Specify the owner password for the PDF file.
         * @param ownerPassword The owner password.
         */
        public T ownerPassword(String ownerPassword) {
            this.ownerPassword = ownerPassword;
            return self();
        }

        /**
         * Disallow printing of the PDF file. Default value is {@code false}.
         * @param disallowPrint true to disallow printing.
         */
        public T disallowPrint(boolean disallowPrint) {
            this.disallowPrint = disallowPrint;
            return self();
        }

        /**
         * Disallow modification of the PDF file. Default value is {@code false}.
         * @param disallowCopy true to disallow modification.
         */
        public T disallowCopy(boolean disallowCopy) {
            this.disallowCopy = disallowCopy;
            return self();
        }

        /**
         * Used together with {@link #disallowCopy(boolean)}, which creates an
         * exception by enabling text access for screen reader devices for the
         * visually impaired. Default value is {@code false}.
         * @param allowCopyForAccessibility true to allow text access.
         */
        public T allowCopyForAccessibility(boolean allowCopyForAccessibility) {
            this.allowCopyForAccessibility = allowCopyForAccessibility;
            return self();
        }

        /**
         * Disallow annotation of the PDF file. Default value is {@code false}.
         * @param disallowAnnotate true to disallow annotation.
         */
        public T disallowAnnotate(boolean disallowAnnotate) {
            this.disallowAnnotate = disallowAnnotate;
            return self();
        }

        /**
         * Disallow modification of the PDF file. Default value is {@code false}.
         * @param disallowModify true to disallow modification.
         */
        public T disallowModify(boolean disallowModify) {
            this.disallowModify = disallowModify;
            return self();
        }

        /**
         * Used together with {@link #disallowModify(boolean)}, which creates an
         * exception. It allows the document to be inserted into another document or
         * other pages to be added, but the content of the document cannot be modified.
         * Default value is {@code false}.
         * @param allowAssembly true to allow assembly.
         */
        public T allowAssembly(boolean allowAssembly) {
            this.allowAssembly = allowAssembly;
            return self();
        }
    }
}
