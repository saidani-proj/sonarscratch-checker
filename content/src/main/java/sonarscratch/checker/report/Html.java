/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import sonarscratch.checker.config.Project;
import sonarscratch.checker.issues.ComponentsCollection;
import sonarscratch.checker.issues.IssuesCollection;
import sonarscratch.checker.log.SystemLogger;
import sonarscratch.checker.util.ExceptionUtil;

public class Html {
    private static final int JSON_PROPERTY_LEFT_MARGIN = 10;
    private static final String NO_PREVIEW_START_BLOCK = "<span class=\""
            + "sonarscratch-checker-issue-line-preview-content-none\">";
    private static final String ISSUE_SUMMARY_SEPARATOR = "<div class=\""
            + "sonarscratch-checker-issue-summary-sep\"></div>";
    private static final String END_SPAN_BLOCK = "</span>";
    private static final String END_DIV_BLOCK = "</div>";
    private IssuesCollection issuesCollection;
    private HashMap<String, JSONObject> componentsByKey = new HashMap<>();
    private HashMap<String, Project> projectsByName = new HashMap<>();
    private BufferedWriter bufferedWriter;
    private SystemLogger logger;
    private Charset encoding;

    public Html(IssuesCollection issuesCollection, ComponentsCollection componentsCollection,
            Iterable<Project> projects, Charset encoding, BufferedWriter bufferedWriter, SystemLogger logger) {
        this.issuesCollection = issuesCollection;

        for (Project project : projects) {
            this.projectsByName.put(project.getName(), project);
        }

        for (JSONObject component : componentsCollection) {
            this.componentsByKey.put(component.optString("key"), component);
        }

        this.encoding = encoding;
        this.bufferedWriter = bufferedWriter;
        this.logger = logger;
    }

    public void write() throws HtmlException {
        try {
            logger.info("Writing HTML report");
            writeln("<html>");
            writeln("<head><meta charset=\"UTF-8\">");
            writeln("<style>");
            writeln(Css.SONARSCRATCH_CHECKER);
            writeln(Css.SONARSCRATCH_CHECKER_HEADER);
            writeln(Css.SONARSCRATCH_CHECKER_HEADER_WARNING);
            writeln(Css.SONARSCRATCH_CHECKER_HEADER_ISSUES);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_SUMMARY);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_SUMMARY_SEP);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_SUMMARY_MESSAGE);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_SUMMARY_TYPE);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_SUMMARY_SEVERITY);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_SUMMARY_FILE);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_SUMMARY_MORE);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_SUMMARY_LESS);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_INFOS);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_INFOS_CONTENT);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_INFOS_CONTENT_KEY);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_INFOS_CONTENT_VALUE);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_PREVIEW);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_LINE_PREVIEW);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_LINE_PREVIEW_NUMBER);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_LINE_PREVIEW_CONTENT);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_LINE_PREVIEW_CONTENT_NONE);
            writeln(Css.SONARSCRATCH_CHECKER_ISSUE_LINE_PREVIEW_CONTENT_CODE);
            writeln("</style>");
            writeln("</head>");
            writeln("<body class=\"sonarscratch-checker\">");

            writeHeader();

            for (var issue : issuesCollection) {
                writeIssue(getIssue(issue));
            }

            writeln("</body>");
            writeln("</html>");

            logger.info("HTML report written");
            logger.info(SystemLogger.BLOCK_END);
        } catch (IOException | JSONException exception) {
            throw new HtmlException(ExceptionUtil.getDefaultMessage(Html.class), exception);
        }
    }

    HtmlWriteDependency getHtmlWriteDependency() {
        return new HtmlWriteDependency();
    }

    private Issue getIssue(JSONObject jsonObject) throws JSONException, HtmlException {
        var componentName = jsonObject.optString("component");

        if (componentName.isEmpty()) {
            throw new HtmlException("Issue with unspecified component");
        }

        JSONObject component = componentsByKey.get(componentName);

        if (component == null) {
            throw new HtmlException("Unfound component '" + componentName + "'");
        }

        String path = component.optString("longName");
        FilePreview filePreview = null;

        if (jsonObject.has("textRange")) {
            var projectName = jsonObject.optString("project");

            if (projectName.isEmpty()) {
                throw new HtmlException("Issue with unspecified project");
            }

            var project = projectsByName.get(projectName);

            if (project == null) {
                throw new HtmlException("Unfound project '" + projectName + "'");
            }

            var textRange = jsonObject.getJSONObject("textRange");
            filePreview = getHtmlWriteDependency().filePreview(project.getRootPath() + "/" + path, encoding,
                    new Range(textRange.optInt("startLine"), textRange.optInt("endLine"),
                            textRange.optInt("startOffset"), textRange.optInt("endOffset")));
        }

        return new Issue(jsonObject.optString("message"), jsonObject.optString("type"),
                jsonObject.optString("severity"), new IssueFile(component.optString("name"), path), filePreview,
                jsonObject);
    }

    private void writeHeader() throws IOException {
        writeln("<div class=\"sonarscratch-checker-header\">");

        write("<div class=\"sonarscratch-checker-header-warning\">");
        write(getHtmlContent("This is a basic report, see SonarQube report for more details."));
        writeln(END_DIV_BLOCK);

        write("<div class=\"sonarscratch-checker-header-issues\">");
        write(getHtmlContent(
                "This report shows " + issuesCollection.count() + " issues from " + issuesCollection.total() + "."));
        writeln(END_DIV_BLOCK);

        writeln(END_DIV_BLOCK);
    }

    private void writeIssue(Issue issue) throws IOException, JSONException, HtmlException {
        writeln("<div class=\"sonarscratch-checker-issue\">");
        writeIssueSummary(issue);
        writeIssueInfos(issue);
        writeIssuePreview(issue);
        writeln(END_DIV_BLOCK);
    }

    private void writeIssueSummary(Issue issue) throws IOException {
        write("<div class=\"sonarscratch-checker-issue-summary\">");

        write("<span class=\"sonarscratch-checker-issue-summary-message\">");
        write(getHtmlContent(issue.getMessage()));
        write(END_SPAN_BLOCK);

        if (!issue.getType().isEmpty()) {
            write(ISSUE_SUMMARY_SEPARATOR);

            write("<span class=\"sonarscratch-checker-issue-summary-type\">");
            write(getHtmlContent(issue.getType()));
            write(END_SPAN_BLOCK);
        }

        if (!issue.getSeverity().isEmpty()) {
            write(ISSUE_SUMMARY_SEPARATOR);

            write("<span class=\"sonarscratch-checker-issue-summary-severity\">");
            write(getHtmlContent(issue.getSeverity()));
            write(END_SPAN_BLOCK);
        }

        if (!issue.getFile().getName().isEmpty()) {
            write(ISSUE_SUMMARY_SEPARATOR);

            write("<span class=\"sonarscratch-checker-issue-summary-file\" title=\""
                    + getHtmlProperty(issue.getFile().getPath()) + "\">");
            write(getHtmlContent(issue.getFile().getName()));
            write(END_SPAN_BLOCK);
        }

        writeln(ISSUE_SUMMARY_SEPARATOR);

        write("<span class=\"sonarscratch-checker-issue-summary-more\" ");
        write("onclick=\"this.parentNode.querySelector('.sonarscratch-checker-issue-summary-less').style.display='';");
        write("this.parentNode.querySelector('.sonarscratch-checker-issue-summary-more').style.display='none';");
        write("this.parentNode.parentNode.querySelector('.sonarscratch-checker-issue-infos').style.display='';\">");
        write(getHtmlContent("MORE"));
        write(END_SPAN_BLOCK);

        write("<span class=\"sonarscratch-checker-issue-summary-less\" style=\"display:none\" ");
        write("onclick=\"this.parentNode.querySelector('.sonarscratch-checker-issue-summary-more').style.display='';");
        write("this.parentNode.querySelector('.sonarscratch-checker-issue-summary-less').style.display='none';");
        write("this.parentNode.parentNode.querySelector('.sonarscratch-checker-issue-infos').style.display='none';\">");
        write(getHtmlContent("LESS"));
        write(END_SPAN_BLOCK);

        writeln(END_DIV_BLOCK);
    }

    private void writeIssueInfos(Issue issue) throws IOException, JSONException {
        writeln("<div class=\"sonarscratch-checker-issue-infos\" style=\"display:none\">");
        writeIssueNode(issue.getInfos(), 0);
        writeln(END_DIV_BLOCK);
    }

    private void writeIssueNode(JSONObject node, int depth) throws IOException, JSONException {
        var keys = node.keys();

        while (keys.hasNext()) {
            var key = (String) keys.next();
            var value = node.get(key);

            write("<div class=\"sonarscratch-checker-issue-infos-content\" style=\"margin-left:"
                    + (depth * JSON_PROPERTY_LEFT_MARGIN) + "px\">");

            write("<span class=\"sonarscratch-checker-issue-infos-content-key\">");
            boolean isNode = value instanceof JSONObject;
            write(getHtmlContent(key + (isNode ? "" : " : ")));
            write(END_SPAN_BLOCK);

            if (isNode) {
                writeIssueNode((JSONObject) value, depth + 1);
            } else {
                write("<span class=\"sonarscratch-checker-issue-infos-content-value\">");
                write(getHtmlContent(value.toString()));
                write(END_SPAN_BLOCK);
            }

            writeln(END_DIV_BLOCK);
        }
    }

    private void writeIssuePreview(Issue issue) throws IOException, HtmlException {
        var filePreview = issue.getFilePreview();

        if (filePreview != null) {
            var preview = filePreview.getPreview();

            writeln("<table class=\"sonarscratch-checker-issue-preview\">");

            for (LinePreview linePreview : preview) {
                writeLinePreview(linePreview);
            }

            writeln("</table>");
        }
    }

    private void writeLinePreview(LinePreview linePreview) throws IOException, HtmlException {
        writeln("<tr class=\"sonarscratch-checker-issue-line-preview\">");

        write("<td class=\"sonarscratch-checker-issue-line-preview-number\">");
        write(getHtmlContent(Integer.toString(linePreview.getNumber())));
        writeln("</td>");

        write("<td class=\"sonarscratch-checker-issue-line-preview-content\">");

        if (linePreview.isIssueLine()) {
            try {
                if (linePreview.getStart() > 0) {
                    write(NO_PREVIEW_START_BLOCK);
                    write(getPreviewHtmlContent(linePreview.getLine().substring(0, linePreview.getStart())));
                    write(END_SPAN_BLOCK);
                }
                write("<span class=\"sonarscratch-checker-issue-line-preview-content-code\">");
                write(getPreviewHtmlContent(
                        linePreview.getLine().substring(linePreview.getStart(), linePreview.getEnd())));
                write(END_SPAN_BLOCK);

                if (linePreview.getEnd() < linePreview.getLine().length()) {
                    write(NO_PREVIEW_START_BLOCK);
                    write(getPreviewHtmlContent(linePreview.getLine().substring(linePreview.getEnd())));
                    write(END_SPAN_BLOCK);
                }
            } catch (StringIndexOutOfBoundsException exception) {
                throw new HtmlException("Can not create preview. Synchronize SonarQube informations with sources",
                        exception);
            }
        } else {
            write(NO_PREVIEW_START_BLOCK);
            write(getPreviewHtmlContent(linePreview.getLine()));
            write(END_SPAN_BLOCK);
        }
        writeln("</td>");

        writeln("</tr>");
    }

    private void writeln(String str) throws IOException {
        bufferedWriter.write(str);
        bufferedWriter.newLine();
    }

    private void write(String str) throws IOException {
        bufferedWriter.write(str);
    }

    private static String getHtmlContent(String content) {
        return content.replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String getPreviewHtmlContent(String content) {
        return getHtmlContent(content).replace(" ", "&nbsp;").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }

    private static String getHtmlProperty(String content) {
        return content.replace("\"", "&quot;");
    }
}
