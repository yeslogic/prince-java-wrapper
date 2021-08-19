/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
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
    protected final List<String> fileAttachments = new ArrayList<>();
    protected boolean noArtificialFonts;
    protected boolean noEmbedFonts;
    protected boolean noSubsetFonts;
    protected boolean forceIdentityEncoding;
    protected boolean noCompress;
    protected boolean noObjectStreams;
    protected boolean convertColors;
    protected String fallbackCmykProfile;
    protected boolean taggedPdf;

    // PDF metadata options.
    protected String pdfTitle;
    protected String pdfSubject;
    protected String pdfAuthor;
    protected final List<String> pdfKeywords = new ArrayList<>();
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

    protected AbstractPrince(String princePath) {
        this(princePath, null);
    }

    protected AbstractPrince(String princePath, PrinceEvents events) {
        this.princePath = princePath;
        this.events = events;
    }

    public abstract boolean convert(String xmlPath, OutputStream out) throws IOException;

    public abstract boolean convert(List<String> xmlPaths, OutputStream out) throws IOException;

    public abstract boolean convert(InputStream in, OutputStream out) throws IOException;

    public abstract boolean convertString(String xml, OutputStream out) throws IOException;

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

                if (events != null && msgTag.equals("msg")) {
                    handleMessage(msgBody);
                } else if (msgTag.equals("dat")) {
                    // TODO Data messages?
                } else if (msgTag.equals("fin")) {
                    result = msgBody;
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

    //region Logging options.
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void setNoWarnCssUnknown(boolean noWarnCssUnknown) {
        this.noWarnCssUnknown = noWarnCssUnknown;
    }

    public void setNoWarnCssUnsupported(boolean noWarnCssUnsupported) {
        this.noWarnCssUnsupported = noWarnCssUnsupported;
    }
    //endregion

    //region Input options.
    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setXInclude(boolean xInclude) {
        this.xInclude = xInclude;
    }

    public void setXmlExternalEntities(boolean xmlExternalEntities) {
        this.xmlExternalEntities = xmlExternalEntities;
    }
    //endregion

    //region Network options.
    public void setNoNetwork(boolean noNetwork) {
        this.noNetwork = noNetwork;
    }

    public void setNoRedirects(boolean noRedirects) {
        this.noRedirects = noRedirects;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public void setAuthServer(String authServer) {
        this.authServer = authServer;
    }

    public void setAuthScheme(AuthScheme authScheme) {
        this.authScheme = authScheme;
    }

    public void addAuthMethod(AuthMethod authMethod) {
        this.authMethods.add(authMethod);
    }

    public void clearAuthMethods() {
        this.authMethods.clear();
    }

    public void setNoAuthPreemptive(boolean noAuthPreemptive) {
        this.noAuthPreemptive = noAuthPreemptive;
    }

    public void setHttpProxy(String httpProxy) {
        this.httpProxy = httpProxy;
    }

    public void setHttpTimeout(int httpTimeout) {
        if (httpTimeout < 1) {
            throw new IllegalArgumentException("invalid httpTimeout value (must be > 0)");
        }
        this.httpTimeout = httpTimeout;
    }

    public void addCookie(String cookie) {
        this.cookies.add(cookie);
    }

    public void clearCookies() {
        this.cookies.clear();
    }

    public void setCookieJar(String cookieJar) {
        this.cookieJar = cookieJar;
    }

    public void setSslCaCert(String sslCaCert) {
        this.sslCaCert = sslCaCert;
    }

    public void setSslCaPath(String sslCaPath) {
        this.sslCaPath = sslCaPath;
    }

    public void setSslCert(String sslCert) {
        this.sslCert = sslCert;
    }

    public void setSslCertType(SslType sslCertType) {
        this.sslCertType = sslCertType;
    }

    public void setSslKey(String sslKey) {
        this.sslKey = sslKey;
    }

    public void setSslKeyType(SslType sslKeyType) {
        this.sslKeyType = sslKeyType;
    }

    public void setSslKeyPassword(String sslKeyPassword) {
        this.sslKeyPassword = sslKeyPassword;
    }

    public void setSslVersion(SslVersion sslVersion) {
        this.sslVersion = sslVersion;
    }

    public void setInsecure(boolean insecure) {
        this.insecure = insecure;
    }

    public void setNoParallelDownloads(boolean noParallelDownloads) {
        this.noParallelDownloads = noParallelDownloads;
    }
    //endregion

    //region JavaScript options.
    public void setJavaScript(boolean javaScript) {
        this.javaScript = javaScript;
    }

    public void addScript(String script) {
        this.scripts.add(script);
    }

    public void clearScripts() {
        this.scripts.clear();
    }

    public void setMaxPasses(int maxPasses) {
        if (maxPasses < 1) {
            throw new IllegalArgumentException("invalid maxPasses value (must be > 0)");
        }
        this.maxPasses = maxPasses;
    }
    //endregion

    //region CSS options.
    public void addStyleSheet(String styleSheet) {
        this.styleSheets.add(styleSheet);
    }

    public void clearStyleSheets() {
        this.styleSheets.clear();
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public void setNoAuthorStyle(boolean noAuthorStyle) {
        this.noAuthorStyle = noAuthorStyle;
    }

    public void setNoDefaultStyle(boolean noDefaultStyle) {
        this.noDefaultStyle = noDefaultStyle;
    }
    //endregion

    //region PDF output options.
    public void setPdfId(String pdfId) {
        this.pdfId = pdfId;
    }

    public void setPdfLang(String pdfLang) {
        this.pdfLang = pdfLang;
    }

    public void setPdfProfile(PdfProfile pdfProfile) {
        this.pdfProfile = pdfProfile;
    }

    public void setPdfOutputIntent(String pdfOutputIntent) {
        this.pdfOutputIntent = pdfOutputIntent;
    }

    public void addFileAttachment(String fileAttachment) {
        this.fileAttachments.add(fileAttachment);
    }

    public void clearFileAttachments() {
        this.fileAttachments.clear();
    }

    public void setNoArtificialFonts(boolean noArtificialFonts) {
        this.noArtificialFonts = noArtificialFonts;
    }

    public void setNoEmbedFonts(boolean noEmbedFonts) {
        this.noEmbedFonts = noEmbedFonts;
    }

    public void setNoSubsetFonts(boolean noSubsetFonts) {
        this.noSubsetFonts = noSubsetFonts;
    }

    public void setForceIdentityEncoding(boolean forceIdentityEncoding) {
        this.forceIdentityEncoding = forceIdentityEncoding;
    }

    public void setNoCompress(boolean noCompress) {
        this.noCompress = noCompress;
    }

    public void setNoObjectStreams(boolean noObjectStreams) {
        this.noObjectStreams = noObjectStreams;
    }

    public void setConvertColors(boolean convertColors) {
        this.convertColors = convertColors;
    }

    public void setFallbackCmykProfile(String fallbackCmykProfile) {
        this.fallbackCmykProfile = fallbackCmykProfile;
    }

    public void setTaggedPdf(boolean taggedPdf) {
        this.taggedPdf = taggedPdf;
    }
    //endregion

    //region PDF metadata options.
    public void setPdfTitle(String pdfTitle) {
        this.pdfTitle = pdfTitle;
    }

    public void setPdfSubject(String pdfSubject) {
        this.pdfSubject = pdfSubject;
    }

    public void setPdfAuthor(String pdfAuthor) {
        this.pdfAuthor = pdfAuthor;
    }

    public void addPdfKeyword(String pdfKeyword) {
        this.pdfKeywords.add(pdfKeyword);
    }

    public void clearPdfKeywords() {
        this.pdfKeywords.clear();
    }

    public void setPdfCreator(String pdfCreator) {
        this.pdfCreator = pdfCreator;
    }

    public void setXmp(String xmp) {
        this.xmp = xmp;
    }
    //endregion

    //region PDF encryption options.
    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public void setKeyBits(KeyBits keyBits) {
        this.keyBits = keyBits;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setOwnerPassword(String ownerPassword) {
        this.ownerPassword = ownerPassword;
    }

    public void setDisallowPrint(boolean disallowPrint) {
        this.disallowPrint = disallowPrint;
    }

    public void setDisallowCopy(boolean disallowCopy) {
        this.disallowCopy = disallowCopy;
    }

    public void setAllowCopyForAccessibility(boolean allowCopyForAccessibility) {
        this.allowCopyForAccessibility = allowCopyForAccessibility;
    }

    public void setDisallowAnnotate(boolean disallowAnnotate) {
        this.disallowAnnotate = disallowAnnotate;
    }

    public void setDisallowModify(boolean disallowModify) {
        this.disallowModify = disallowModify;
    }

    public void setAllowAssembly(boolean allowAssembly) {
        this.allowAssembly = allowAssembly;
    }
    //endregion
}
