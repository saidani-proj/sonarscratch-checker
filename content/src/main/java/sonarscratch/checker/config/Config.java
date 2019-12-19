/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.config;

import java.util.ArrayList;

public class Config {
    public static final String DEFAULT_SONAR_URL = "http://localhost:9000";
    public static final int DEFAULT_SONAR_ATTEMPTS_COUNT = 20;
    public static final int DEFAULT_SONAR_ATTEMPT_SLEEP_MILLISECONDS = 5000;
    public static final String DEFAULT_REPORT_PATH = "sonarqube-issues.html";
    public static final String DEFAULT_ENCODING = "UTF-8";

    private boolean showHelp;
    private boolean showVersion;
    private String sonarUrl = DEFAULT_SONAR_URL;
    private Integer sonarAttemptsCount = DEFAULT_SONAR_ATTEMPTS_COUNT;
    private Integer sonarAttemptSleepMilliseconds = DEFAULT_SONAR_ATTEMPT_SLEEP_MILLISECONDS;
    private boolean writeReport;
    private String reportPath = DEFAULT_REPORT_PATH;
    private Iterable<Project> projects = new ArrayList<>();
    private String encoding = DEFAULT_ENCODING;
    private boolean check;

    Config() {
        showHelp = false;
        showVersion = false;
        writeReport = false;
    }

    public boolean showHelp() {
        return showHelp;
    }

    void setShowHelp(Boolean value) {
        showHelp = value;
    }

    public boolean showVersion() {
        return showVersion;
    }

    void setShowVersion(Boolean value) {
        showVersion = value;
    }

    public String getSonarUrl() {
        return sonarUrl;
    }

    void setSonarUrl(String value) {
        sonarUrl = value;
    }

    public int getSonarAttemptsCount() {
        return sonarAttemptsCount;
    }

    void setSonarAttemptsCount(int value) {
        sonarAttemptsCount = value;
    }

    public int getSonarAttemptSleepMilliseconds() {
        return sonarAttemptSleepMilliseconds;
    }

    void setSonarAttemptSleepMilliseconds(int value) {
        sonarAttemptSleepMilliseconds = value;
    }

    public boolean writeReport() {
        return writeReport;
    }

    void setWriteReport(Boolean value) {
        writeReport = value;
    }

    public String getReportPath() {
        return reportPath;
    }

    void setReportPath(String value) {
        reportPath = value;
    }

    public Iterable<Project> getProjects() {
        return projects;
    }

    void setProjects(Iterable<Project> value) {
        projects = value;
    }

    public String getEncoding() {
        return encoding;
    }

    void setEncoding(String value) {
        encoding = value;
    }

    public boolean check() {
        return check;
    }

    void setCheck(boolean value) {
        check = value;
    }
}
