/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

final class Css {
    public static final String SONARSCRATCH_CHECKER = ".sonarscratch-checker"
            + "{font-family: arial, helvetica, clean, sans-serif; font-size: 15px;}";
    public static final String SONARSCRATCH_CHECKER_HEADER = ".sonarscratch-checker-header"
            + "{background-color: #ECECEC; border: 1px solid #DDD; padding: 4px; margin-bottom: 20px;}";
    public static final String SONARSCRATCH_CHECKER_HEADER_WARNING = ".sonarscratch-checker-header-warning"
            + "{color: #E91313}";
    public static final String SONARSCRATCH_CHECKER_HEADER_ISSUES = ".sonarscratch-checker-header-issues" + "{}";
    public static final String SONARSCRATCH_CHECKER_ISSUE = ".sonarscratch-checker-issue"
            + "{border: 1px solid #DDD; margin-top: 10px;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_SUMMARY = ".sonarscratch-checker-issue-summary"
            + "{background-color: #E4ECF3; padding : 4px;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_SUMMARY_SEP = ".sonarscratch-checker-issue-summary-sep"
            + "{border-left: 2px solid #61c940; height: 13px;"
            + " display: inline-block; margin-left: 5px; margin-right: 5px;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_SUMMARY_MESSAGE = "."
            + "sonarscratch-checker-issue-summary-message" + "{}";
    private static final String EXTRA_INFOS_COLOR = "{color: #777;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_SUMMARY_TYPE = ".sonarscratch-checker-issue-summary-type"
            + EXTRA_INFOS_COLOR;
    public static final String SONARSCRATCH_CHECKER_ISSUE_SUMMARY_SEVERITY = "."
            + "sonarscratch-checker-issue-summary-severity" + EXTRA_INFOS_COLOR;
    public static final String SONARSCRATCH_CHECKER_ISSUE_SUMMARY_FILE = ".sonarscratch-checker-issue-summary-file"
            + EXTRA_INFOS_COLOR;
    public static final String SONARSCRATCH_CHECKER_ISSUE_SUMMARY_MORE = ".sonarscratch-checker-issue-summary-more"
            + "{cursor: pointer; font-size: 10px;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_SUMMARY_LESS = ".sonarscratch-checker-issue-summary-less"
            + "{cursor: pointer; font-size: 10px;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_INFOS = "." + "sonarscratch-checker-issue-infos"
            + "{padding: 4px; background-color: #F0E5E4;"
            + " font-family: sans-serif; font-size: 13px; border: 0px solid #DDD; border-top-width: 1px;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_INFOS_CONTENT = "."
            + "sonarscratch-checker-issue-infos-content" + "{}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_INFOS_CONTENT_KEY = "."
            + "sonarscratch-checker-issue-infos-content-key" + "{font-weight: bold;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_INFOS_CONTENT_VALUE = "."
            + "sonarscratch-checker-issue-infos-content-value" + "{}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_PREVIEW = ".sonarscratch-checker-issue-preview"
            + "{background-color: #F4F4F4; font-family: monospace;"
            + " border-collapse: collapse; border: 0px solid #DDD; border-top-width: 1px; width:100%;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_LINE_PREVIEW = ".sonarscratch-checker-issue-line-preview"
            + "{}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_LINE_PREVIEW_NUMBER = "."
            + "sonarscratch-checker-issue-line-preview-number"
            + "{padding-left: 5px; padding-right: 5px; width:0px; font-weight: bold;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_LINE_PREVIEW_CONTENT = "."
            + "sonarscratch-checker-issue-line-preview-content" + "{padding: 2px;}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_LINE_PREVIEW_CONTENT_NONE = "."
            + "sonarscratch-checker-issue-line-preview-content-none" + "{}";
    public static final String SONARSCRATCH_CHECKER_ISSUE_LINE_PREVIEW_CONTENT_CODE = "."
            + "sonarscratch-checker-issue-line-preview-content-code" + "{background-color: #F6AFAF;}";

    private Css() {
    }
}
