package com.test.report;

public class ExtentReportBean {

    private String theme;
    private String encoding;
    private String protocol;
    private boolean timelineEnabled;
    private boolean offlineMode;
    private boolean thumbnailForBase64;
    private String documentTitle;
    private String reportName;
    private String timeStampFormat;
    private String js;
    private String css;


    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean isTimelineEnabled() {
        return timelineEnabled;
    }

    public void setTimelineEnabled(boolean timelineEnabled) {
        this.timelineEnabled = timelineEnabled;
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    public boolean isThumbnailForBase64() {
        return thumbnailForBase64;
    }

    public void setThumbnailForBase64(boolean thumbnailForBase64) {
        this.thumbnailForBase64 = thumbnailForBase64;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getTimeStampFormat() {
        return timeStampFormat;
    }

    public void setTimeStampFormat(String timeStampFormat) {
        this.timeStampFormat = timeStampFormat;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }
}