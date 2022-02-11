/*
 * Copyright (C) 2021-2022 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import com.princexml.wrapper.enums.*;
import com.princexml.wrapper.events.MessageType;
import com.princexml.wrapper.events.PrinceEvents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.princexml.wrapper.CommandLine.toCommand;
import static com.princexml.wrapper.CommandLine.toCommands;

abstract class AbstractPrince {
    private final String princePath;
    private final PrinceEvents events;

    // Logging options.
    private boolean verbose;
    private boolean debug;
    private String log;
    private boolean noWarnCssUnknown;
    private boolean noWarnCssUnsupported;

    // Input options.
    protected InputType inputType;
    protected String baseUrl;
    protected boolean iframes;
    protected boolean xInclude;
    protected boolean xmlExternalEntities;

    // Network options.
    private boolean noNetwork;
    private boolean noRedirects;
    private String authUser;
    private String authPassword;
    private String authServer;
    private AuthScheme authScheme;
    private final List<AuthMethod> authMethods = new ArrayList<>();
    private boolean noAuthPreemptive;
    private String httpProxy;
    private int httpTimeout;
    private final List<String> cookies = new ArrayList<>();
    private String cookieJar;
    private String sslCaCert;
    private String sslCaPath;
    private String sslCert;
    private SslType sslCertType;
    private String sslKey;
    private SslType sslKeyType;
    private String sslKeyPassword;
    private SslVersion sslVersion;
    private boolean insecure;
    private boolean noParallelDownloads;

    // JavaScript options.
    protected boolean javaScript;
    protected final List<String> scripts = new ArrayList<>();
    protected int maxPasses;

    // CSS options.
    protected final List<String> styleSheets = new ArrayList<>();
    protected String media;
    protected boolean noAuthorStyle;
    protected boolean noDefaultStyle;

    // PDF output options.
    protected String pdfId;
    protected String pdfLang;
    protected PdfProfile pdfProfile;
    protected String pdfOutputIntent;
    protected final List<FileAttachment> fileAttachments = new ArrayList<>();
    protected boolean noArtificialFonts;
    protected boolean noEmbedFonts;
    protected boolean noSubsetFonts;
    protected boolean forceIdentityEncoding;
    protected boolean noCompress;
    protected boolean noObjectStreams;
    protected boolean convertColors;
    protected String fallbackCmykProfile;
    protected boolean taggedPdf;
    protected boolean pdfForms;

    // PDF metadata options.
    protected String pdfTitle;
    protected String pdfSubject;
    protected String pdfAuthor;
    protected String pdfKeywords;
    protected String pdfCreator;
    protected String xmp;

    // PDF encryption options.
    protected boolean encrypt;
    protected KeyBits keyBits;
    protected String userPassword;
    protected String ownerPassword;
    protected boolean disallowPrint;
    protected boolean disallowCopy;
    protected boolean allowCopyForAccessibility;
    protected boolean disallowAnnotate;
    protected boolean disallowModify;
    protected boolean allowAssembly;

    // License options.
    private String licenseFile;
    private String licenseKey;

    protected AbstractPrince(String princePath) {
        this(princePath, null);
    }

    protected AbstractPrince(String princePath, PrinceEvents events) {
        this.princePath = princePath;
        this.events = events;
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
    public abstract boolean convert(String inputPath, OutputStream output) throws IOException;

    /**
     * Convert multiple XML or HTML files to a PDF file. This method is useful
     * for servlets as it allows Prince to write the PDF output directly to the
     * {@code OutputStream} of the servlet response.
     * @param inputPaths The filenames of the input XML or HTML documents.
     * @param output The OutputStream to which Prince will write the PDF output.
     * @return true if a PDF file was generated successfully.
     * @throws IOException If an I/O error occurs.
     */
    public abstract boolean convert(List<String> inputPaths, OutputStream output) throws IOException;

    /**
     * Convert an XML or HTML stream to a PDF file. This method is useful for
     * servlets as it allows Prince to write the PDF output directly to the
     * {@code OutputStream} of the servlet response.
     * <p>
     * Note that it may be helpful to specify a base URL or path for the input
     * document using {@link #setBaseUrl(String)}. This allows relative URLs and
     * paths in the document (e.g. for images) to be resolved correctly.
     * @param input The InputStream from which Prince will read the XML or HTML
     *              document.
     * @param output The OutputStream to which Prince will write the PDF output.
     * @return true if a PDF file was generated successfully.
     * @throws IOException If an I/O error occurs.
     */
    public abstract boolean convert(InputStream input, OutputStream output) throws IOException;

    /**
     * Convert an XML or HTML string to a PDF file. This method is useful for
     * servlets as it allows Prince to write the PDF output directly to the
     * {@code OutputStream} of the servlet response.
     * @param input The XML or HTML document in the form of a String.
     * @param output The OutputStream to which Prince will write the PDF output.
     * @return true if a PDF file was generated successfully.
     * @throws IOException If an I/O error occurs.
     */
    public abstract boolean convertString(String input, OutputStream output) throws IOException;

    protected List<String> getBaseCommandLine() {
        List<String> cmdLine = new ArrayList<>();

        cmdLine.add(princePath);

        if (verbose) { cmdLine.add(toCommand("verbose")); }
        if (debug) { cmdLine.add(toCommand("debug")); }
        if (log != null) { cmdLine.add(toCommand("log", log)); }
        if (noWarnCssUnknown) { cmdLine.add(toCommand("no-warn-css-unknown")); }
        if (noWarnCssUnsupported) { cmdLine.add(toCommand("no-warn-css-unsupported")); }

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

        if (licenseFile != null) { cmdLine.add(toCommand("license-file", licenseFile)); }
        if (licenseKey != null) { cmdLine.add(toCommand("license-key", licenseKey)); }

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
            }

            line = reader.readLine();
        }

        return result.equals("success");
    }

    private void handleMessage(String msgBody) {
        String[] tokens = msgBody.split("\\|", 3);
        if (tokens.length == 3) {
            MessageType msgType = MessageType.valueOf(tokens[0].toUpperCase());
            String msgLocation = tokens[1];
            String msgText = tokens[2];

            events.onMessage(msgType, msgLocation, msgText);
        }
    }

    private void handleDataMessage(String msgBody) {
        String[] tokens = msgBody.split("\\|", 2);
        if (tokens.length == 2) {
            String name = tokens[0];
            String value = tokens[1];

            events.onDataMessage(name, value);
        }
    }

    //region Logging options.
    /**
     * Enable logging of informative messages. Default value is {@code false}.
     * @param verbose true to enable verbose logging.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Enable logging of debug messages. Default value is {@code false}.
     * @param debug true to enable debug logging.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Specify a file that Prince should use to log messages. If this method
     * is not called then Prince will not write to any log. This method does
     * not affect the operation of {@link com.princexml.wrapper.events.PrinceEvents},
     * which will also receive messages from Prince.
     * @param log The filename that Prince should use to log messages.
     */
    public void setLog(String log) {
        this.log = log;
    }

    /**
     * Disable warnings about unknown CSS features. Default value is {@code false}.
     * @param noWarnCssUnknown true to disable warnings.
     */
    public void setNoWarnCssUnknown(boolean noWarnCssUnknown) {
        this.noWarnCssUnknown = noWarnCssUnknown;
    }

    /**
     * Disable warnings about unsupported CSS features. Default value is {@code false}.
     * @param noWarnCssUnsupported true to disable warnings.
     */
    public void setNoWarnCssUnsupported(boolean noWarnCssUnsupported) {
        this.noWarnCssUnsupported = noWarnCssUnsupported;
    }
    //endregion

    //region Input options.
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
    public void setInputType(InputType inputType) {
        this.inputType = inputType;
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
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Enable HTML iframes. Default value is {@code false}.
     * @param iframes true to enable HTML iframes.
     */
    public void setIframes(boolean iframes) {
        this.iframes = iframes;
    }

    /**
     * Enable XInclude and XML external entities (XXE). Note that XInclude only
     * applies to XML files. To apply it to HTML files, the input format needs
     * to be specified with {@link #setInputType(InputType)}. Default value is
     * {@code false}.
     * @param xInclude true to enable XInclude and XXE.
     */
    public void setXInclude(boolean xInclude) {
        this.xInclude = xInclude;
    }

    /**
     * Enable XML external entities (XXE). Default value is {@code false}.
     * @param xmlExternalEntities true to enable XXE.
     */
    public void setXmlExternalEntities(boolean xmlExternalEntities) {
        this.xmlExternalEntities = xmlExternalEntities;
    }
    //endregion

    //region Network options.
    /**
     * Disable network access (prevents HTTP downloads). Default value is {@code false}.
     * @param noNetwork true to disable network access.
     */
    public void setNoNetwork(boolean noNetwork) {
        this.noNetwork = noNetwork;
    }

    /**
     * Disable all HTTP and HTTPS redirects. Default value is {@code false}.
     * @param noRedirects true to disable redirects.
     */
    public void setNoRedirects(boolean noRedirects) {
        this.noRedirects = noRedirects;
    }

    /**
     * Specify the username for HTTP authentication.
     * @param authUser The username for authentication.
     */
    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    /**
     * Specify the password for HTTP authentication.
     * @param authPassword The password for authentication.
     */
    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    /**
     * Send username and password credentials to the specified server only.
     * The default is to send them to any server which challenges for authentication.
     * @param authServer The server to send credentials to (e.g. "localhost:8001").
     */
    public void setAuthServer(String authServer) {
        this.authServer = authServer;
    }

    /**
     * Send username and password credentials only for requests with the given
     * scheme.
     * @param authScheme The authentication scheme.
     */
    public void setAuthScheme(AuthScheme authScheme) {
        this.authScheme = authScheme;
    }

    /**
     * Specify a HTTP authentication method to enable. This method can be called
     * more than once to add multiple authentication methods.
     * @param authMethod The authentication method to enable.
     */
    public void addAuthMethod(AuthMethod authMethod) {
        this.authMethods.add(authMethod);
    }

    /**
     * Clear all the enabled authentication methods accumulated by calling
     * {@link #addAuthMethod(AuthMethod)}.
     */
    public void clearAuthMethods() {
        this.authMethods.clear();
    }

    /**
     * Do not authenticate with named servers until asked. Default value is {@code false}.
     * @param noAuthPreemptive true to disable authentication preemptive.
     */
    public void setNoAuthPreemptive(boolean noAuthPreemptive) {
        this.noAuthPreemptive = noAuthPreemptive;
    }

    /**
     * Specify the URL for the HTTP proxy server, if needed.
     * @param httpProxy The URL for the HTTP proxy server.
     */
    public void setHttpProxy(String httpProxy) {
        this.httpProxy = httpProxy;
    }

    /**
     * Specify the timeout for HTTP requests. Default value is 60 seconds.
     * @param httpTimeout The HTTP timeout in seconds. Value must be greater than 0.
     */
    public void setHttpTimeout(int httpTimeout) {
        if (httpTimeout < 1) {
            throw new IllegalArgumentException("invalid httpTimeout value (must be > 0)");
        }
        this.httpTimeout = httpTimeout;
    }

    /**
     * Add a value for the {@code Set-Cookie} HTTP header value. This method can
     * be called more than once to add multiple cookies.
     * @param cookie The cookie to be added.
     */
    public void addCookie(String cookie) {
        this.cookies.add(cookie);
    }

    /**
     * Clear all the cookies accumulated by calling {@link #addCookie(String)}.
     */
    public void clearCookies() {
        this.cookies.clear();
    }

    /**
     * Specify a file containing HTTP cookies.
     * @param cookieJar The filename of the file containing HTTP cookies.
     */
    public void setCookieJar(String cookieJar) {
        this.cookieJar = cookieJar;
    }

    /**
     * Specify an SSL certificate file.
     * @param sslCaCert The filename of the SSL certificate file.
     */
    public void setSslCaCert(String sslCaCert) {
        this.sslCaCert = sslCaCert;
    }

    /**
     * Specify an SSL certificate directory.
     * @param sslCaPath The SSL certificate directory.
     */
    public void setSslCaPath(String sslCaPath) {
        this.sslCaPath = sslCaPath;
    }

    /**
     * Specify an SSL client certificate file. On MacOS, specify a PKCS#12 file
     * containing a client certificate and private key. Client authentication is
     * not supported on Windows.
     * @param sslCert The filename of the SSL client certificate file.
     */
    public void setSslCert(String sslCert) {
        this.sslCert = sslCert;
    }

    /**
     * Specify the SSL client certificate file type. This option is not supported
     * on MacOS or Windows.
     * @param sslCertType The SSL client certificate file type.
     */
    public void setSslCertType(SslType sslCertType) {
        this.sslCertType = sslCertType;
    }

    /**
     * Specify an SSL private key file. This option is not supported on MacOS or
     * Windows.
     * @param sslKey The filename of the SSL private key file.
     */
    public void setSslKey(String sslKey) {
        this.sslKey = sslKey;
    }

    /**
     * Specify the SSL private key file type. This option is not supported on MacOS
     * or Windows.
     * @param sslKeyType The SSL private key file type.
     */
    public void setSslKeyType(SslType sslKeyType) {
        this.sslKeyType = sslKeyType;
    }

    /**
     * Specify a password for the SSL private key.
     * @param sslKeyPassword The password for the SSL private key.
     */
    public void setSslKeyPassword(String sslKeyPassword) {
        this.sslKeyPassword = sslKeyPassword;
    }

    /**
     * Set the minimum version of SSL to allow. Default value is
     * {@link com.princexml.wrapper.enums.SslVersion#DEFAULT}.
     * @param sslVersion The minimum version to allow.
     */
    public void setSslVersion(SslVersion sslVersion) {
        this.sslVersion = sslVersion;
    }

    /**
     * Specify whether to disable SSL verification. Default value is {@code false}.
     * @param insecure true to disable SSL verification (not recommended).
     */
    public void setInsecure(boolean insecure) {
        this.insecure = insecure;
    }

    /**
     * Disable downloading multiple HTTP resources at once. Default value is {@code false}.
     * @param noParallelDownloads true to disable parallel downloads.
     */
    public void setNoParallelDownloads(boolean noParallelDownloads) {
        this.noParallelDownloads = noParallelDownloads;
    }
    //endregion

    //region JavaScript options.
    /**
     * Specify whether JavaScript found in documents should be run. Default value is
     * {@code false}.
     * @param javaScript true if document scripts should be run.
     */
    public void setJavaScript(boolean javaScript) {
        this.javaScript = javaScript;
    }

    /**
     * Add a JavaScript script that will be run before conversion. This
     * method can be called more than once to add multiple scripts.
     * @param script The filename of the script to run.
     */
    public void addScript(String script) {
        this.scripts.add(script);
    }

    /**
     * Clear all of the scripts accumulated by calling {@link #addScript(String)}.
     */
    public void clearScripts() {
        this.scripts.clear();
    }

    /**
     * Defines the maximum number of consequent layout passes. Default value is
     * unlimited passes.
     * @param maxPasses The number of maximum passes. Value must be greater than 0.
     */
    public void setMaxPasses(int maxPasses) {
        if (maxPasses < 1) {
            throw new IllegalArgumentException("invalid maxPasses value (must be > 0)");
        }
        this.maxPasses = maxPasses;
    }
    //endregion

    //region CSS options.
    /**
     * Add a CSS style sheet that will be applied to each input document. This
     * method can be called more than once to add multiple style sheets.
     * @param styleSheet The filename of the CSS style sheet to apply.
     */
    public void addStyleSheet(String styleSheet) {
        this.styleSheets.add(styleSheet);
    }

    /**
     * Clear all of the CSS style sheets accumulated by calling
     * {@link #addStyleSheet(String)}.
     */
    public void clearStyleSheets() {
        this.styleSheets.clear();
    }

    /**
     * Specify the media type. Default value is {@code "print"}.
     * @param media The media type (e.g. "print", "screen").
     */
    public void setMedia(String media) {
        this.media = media;
    }

    /**
     * Ignore author style sheets. Default value is {@code false}.
     * @param noAuthorStyle true to ignore author style sheets.
     */
    public void setNoAuthorStyle(boolean noAuthorStyle) {
        this.noAuthorStyle = noAuthorStyle;
    }

    /**
     * Ignore default style sheets. Default value is {@code false}.
     * @param noDefaultStyle true to ignore default style sheets.
     */
    public void setNoDefaultStyle(boolean noDefaultStyle) {
        this.noDefaultStyle = noDefaultStyle;
    }
    //endregion

    //region PDF output options.
    /**
     * Specify the PDF ID to use.
     * @param pdfId The PDF ID.
     */
    public void setPdfId(String pdfId) {
        this.pdfId = pdfId;
    }

    /**
     * Specify the PDF document's Lang entry in the document catalog.
     * @param pdfLang The PDF document's lang entry.
     */
    public void setPdfLang(String pdfLang) {
        this.pdfLang = pdfLang;
    }

    /**
     * Specify the PDF profile to use.
     * @param pdfProfile The PDF profile.
     */
    public void setPdfProfile(PdfProfile pdfProfile) {
        this.pdfProfile = pdfProfile;
    }

    /**
     * Specify the ICC profile to use.
     * @param pdfOutputIntent The ICC profile.
     */
    public void setPdfOutputIntent(String pdfOutputIntent) {
        this.pdfOutputIntent = pdfOutputIntent;
    }

    /**
     * Add a file attachment that will be attached to the PDF file. This method
     * can be called more than once to add multiple file attachments.
     * @param fileAttachment The filename of the file attachment.
     */
    public void addFileAttachment(String fileAttachment) {
        this.fileAttachments.add(new FileAttachment(fileAttachment));
    }

    /**
     * Clear all of the file attachments accumulated by calling {@link #addFileAttachment(String)}.
     */
    public void clearFileAttachments() {
        this.fileAttachments.clear();
    }

    /**
     * Specify whether artificial bold/italic fonts should be generated if
     * necessary. Default value is {@code false}.
     * @param noArtificialFonts true to disable artificial bold/italic fonts.
     */
    public void setNoArtificialFonts(boolean noArtificialFonts) {
        this.noArtificialFonts = noArtificialFonts;
    }

    /**
     * Specify whether fonts should be embedded in the output PDF file. Default
     * value is {@code false}.
     * @param noEmbedFonts true to disable PDF font embedding.
     */
    public void setNoEmbedFonts(boolean noEmbedFonts) {
        this.noEmbedFonts = noEmbedFonts;
    }

    /**
     * Specify whether embedded fonts should be subset in the output PDF file.
     * Default value is {@code false}.
     * @param noSubsetFonts true to disable PDF font subsetting.
     */
    public void setNoSubsetFonts(boolean noSubsetFonts) {
        this.noSubsetFonts = noSubsetFonts;
    }

    /**
     * Ensure that all fonts are encoded in the PDF using their identity encoding
     * (directly mapping to glyph indices), even if they could have used MacRoman
     * or some other encoding. Default value is {@code false}.
     * @param forceIdentityEncoding true to force identity encoding.
     */
    public void setForceIdentityEncoding(boolean forceIdentityEncoding) {
        this.forceIdentityEncoding = forceIdentityEncoding;
    }

    /**
     * Specify whether compression should be applied to the output PDF file.
     * Default value is {@code false}.
     * @param noCompress true to disable PDF compression.
     */
    public void setNoCompress(boolean noCompress) {
        this.noCompress = noCompress;
    }

    /**
     * Disable PDF object streams. Default value is {@code false}.
     * @param noObjectStreams true to disable PDF object streams.
     */
    public void setNoObjectStreams(boolean noObjectStreams) {
        this.noObjectStreams = noObjectStreams;
    }

    /**
     * Convert colors to output intent color space. Default value is {@code false}.
     * @param convertColors true to convert colors to output intent color space.
     */
    public void setConvertColors(boolean convertColors) {
        this.convertColors = convertColors;
    }

    /**
     * Set fallback ICC profile for uncalibrated CMYK.
     * @param fallbackCmykProfile The fallback ICC profile.
     */
    public void setFallbackCmykProfile(String fallbackCmykProfile) {
        this.fallbackCmykProfile = fallbackCmykProfile;
    }

    /**
     * Enable tagged PDF. Default value is {@code false}.
     * @param taggedPdf true to enable tagged PDF.
     */
    public void setTaggedPdf(boolean taggedPdf) {
        this.taggedPdf = taggedPdf;
    }

    /**
     * Enable PDF forms by default. Default value is {@code false}.
     * @param pdfForms true to enable PDF forms.
     */
    public void setPdfForms(boolean pdfForms) {
        this.pdfForms = pdfForms;
    }
    //endregion

    //region PDF metadata options.
    /**
     * Specify the document title for PDF metadata.
     * @param pdfTitle The document title.
     */
    public void setPdfTitle(String pdfTitle) {
        this.pdfTitle = pdfTitle;
    }

    /**
     * Specify the document subject for PDF metadata.
     * @param pdfSubject The document subject.
     */
    public void setPdfSubject(String pdfSubject) {
        this.pdfSubject = pdfSubject;
    }

    /**
     * Specify the document author for PDF metadata.
     * @param pdfAuthor The document author.
     */
    public void setPdfAuthor(String pdfAuthor) {
        this.pdfAuthor = pdfAuthor;
    }

    /**
     * Specify the document keywords for PDF metadata.
     * @param pdfKeywords The document keywords.
     */
    public void setPdfKeywords(String pdfKeywords) {
        this.pdfKeywords = pdfKeywords;
    }

    /**
     * Specify the document creator for PDF metadata.
     * @param pdfCreator The document creator.
     */
    public void setPdfCreator(String pdfCreator) {
        this.pdfCreator = pdfCreator;
    }

    /**
     * Specify an XMP file that contains XMP metadata to be included in the
     * output PDF file.
     * @param xmp The filename of the XMP file.
     */
    public void setXmp(String xmp) {
        this.xmp = xmp;
    }
    //endregion

    //region PDF encryption options.
    /**
     * Specify whether encryption should be applied to the output file.
     * Default value is {@code false}.
     * @param encrypt true to enable PDF encryption.
     */
    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    /**
     * Specify the size of the encryption key. Default value is
     * {@link com.princexml.wrapper.enums.KeyBits#BITS128}.
     * @param keyBits The size of the encryption key.
     */
    public void setKeyBits(KeyBits keyBits) {
        this.keyBits = keyBits;
    }

    /**
     * Specify the user password for the PDF file.
     * @param userPassword The user password.
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * Specify the owner password for the PDF file.
     * @param ownerPassword The owner password.
     */
    public void setOwnerPassword(String ownerPassword) {
        this.ownerPassword = ownerPassword;
    }

    /**
     * Disallow printing of the PDF file. Default value is {@code false}.
     * @param disallowPrint true to disallow printing.
     */
    public void setDisallowPrint(boolean disallowPrint) {
        this.disallowPrint = disallowPrint;
    }

    /**
     * Disallow modification of the PDF file. Default value is {@code false}.
     * @param disallowCopy true to disallow modification.
     */
    public void setDisallowCopy(boolean disallowCopy) {
        this.disallowCopy = disallowCopy;
    }

    /**
     * Used together with {@link #setDisallowCopy(boolean)}, which creates an
     * exception by enabling text access for screen reader devices for the
     * visually impaired. Default value is {@code false}.
     * @param allowCopyForAccessibility true to allow text access.
     */
    public void setAllowCopyForAccessibility(boolean allowCopyForAccessibility) {
        this.allowCopyForAccessibility = allowCopyForAccessibility;
    }

    /**
     * Disallow annotation of the PDF file. Default value is {@code false}.
     * @param disallowAnnotate true to disallow annotation.
     */
    public void setDisallowAnnotate(boolean disallowAnnotate) {
        this.disallowAnnotate = disallowAnnotate;
    }

    /**
     * Disallow modification of the PDF file. Default value is {@code false}.
     * @param disallowModify true to disallow modification.
     */
    public void setDisallowModify(boolean disallowModify) {
        this.disallowModify = disallowModify;
    }

    /**
     * Used together with {@link #setDisallowModify(boolean)}, which creates an
     * exception. It allows the document to be inserted into another document or
     * other pages to be added, but the content of the document cannot be modified.
     * Default value is {@code false}.
     * @param allowAssembly true to allow assembly.
     */
    public void setAllowAssembly(boolean allowAssembly) {
        this.allowAssembly = allowAssembly;
    }
    //endregion

    //region License options.
    /**
     * Specify the license file.
     * @param licenseFile The filename of the license file.
     */
    public void setLicenseFile(String licenseFile) {
        this.licenseFile = licenseFile;
    }

    /**
     * Specify the license key. This is the {@code <signature>} field in the
     * license file.
     * @param licenseKey The license key.
     */
    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }
    //endregion
}
