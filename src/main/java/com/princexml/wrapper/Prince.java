/*
 * Copyright (C) 2021 YesLogic Pty. Ltd.
 * All rights reserved.
 */

package com.princexml.wrapper;

import com.princexml.wrapper.enums.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.princexml.wrapper.CommandLine.*;

public class Prince {
    private final String princePath;

    // Logging options.
    private boolean verbose;
    private boolean debug;
    private String log;
    private boolean noWarnCssUnknown;
    private boolean noWarnCssUnsupported;

    // Input options.
    protected InputType inputType;
    protected String baseUrl;
    private final List<String> remaps = new ArrayList<>();
    protected boolean xInclude;
    protected boolean xmlExternalEntities;
    private boolean noLocalFiles;

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
    private String pageSize;
    private String pageMargin;
    protected boolean noAuthorStyle;
    protected boolean noDefaultStyle;

    // Output options.
    protected String pdfId;
    protected String pdfLang;
    protected PdfProfile pdfProfile;
    protected String pdfOutputIntent;
    protected final List<String> fileAttachments = new ArrayList<>();
    protected boolean noArtificialFonts;
    protected boolean noEmbedFonts;
    protected boolean noSubsetFonts;
    private boolean noSystemFonts;
    protected boolean forceIdentityEncoding;
    protected boolean noCompress;
    protected boolean noObjectStreams;
    protected boolean convertColors;
    protected String fallbackCmykProfile;
    protected boolean taggedPdf;
    private int cssDpi;

    // Metadata options.
    protected String pdfTitle;
    protected String pdfSubject;
    protected String pdfAuthor;
    protected final List<String> pdfKeywords = new ArrayList<>();
    protected String pdfCreator;
    protected String xmp;

    // Encryption options.
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

    // Raster options.
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
        this.princePath = princePath;
    }

    protected List<String> getBaseCommandLine() {
        List<String> cmdLine = new ArrayList<>();

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

    private List<String> getJobCommandLine() {
        List<String> cmdLine = new ArrayList<>();

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
        if (!pdfKeywords.isEmpty()) { cmdLine.add(toCommand("pdf-keywords", pdfKeywords)); }
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

    public void addRemap(String remap) {
        this.remaps.add(remap);
    }

    public void clearRemaps() {
        this.remaps.clear();
    }

    public void setXInclude(boolean xInclude) {
        this.xInclude = xInclude;
    }

    public void setXmlExternalEntities(boolean xmlExternalEntities) {
        this.xmlExternalEntities = xmlExternalEntities;
    }

    public void setNoLocalFiles(boolean noLocalFiles) {
        this.noLocalFiles = noLocalFiles;
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

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageMargin(String pageMargin) {
        this.pageMargin = pageMargin;
    }

    public void setNoAuthorStyle(boolean noAuthorStyle) {
        this.noAuthorStyle = noAuthorStyle;
    }

    public void setNoDefaultStyle(boolean noDefaultStyle) {
        this.noDefaultStyle = noDefaultStyle;
    }
    //endregion

    //region Output options.
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

    public void setNoSystemFonts(boolean noSystemFonts) {
        this.noSystemFonts = noSystemFonts;
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

    public void setCssDpi(int cssDpi) {
        if (cssDpi < 1) {
            throw new IllegalArgumentException("invalid cssDpi value (must be > 0)");
        }
        this.cssDpi = cssDpi;
    }
    //endregion

    //region Metadata options.
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

    //region Encryption options.
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

    //region Raster options.
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
