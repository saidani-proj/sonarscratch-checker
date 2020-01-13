/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.Mockito;

import sonarscratch.checker.config.Project;
import sonarscratch.checker.issues.ComponentsCollection;
import sonarscratch.checker.issues.IssuesCollection;
import sonarscratch.checker.log.SystemLogger;
import sonarscratch.checker.report.HtmlTest.HtmlWithIssue;
import sonarscratch.checker.test.TestException;
import sonarscratch.checker.util.ExceptionUtil;

final class HtmlTestUtil {
    private HtmlTestUtil() {
    }

    static Html mockFilePreview(Range range, String content, Html html) throws IOException {
        var mockedFilePreviewGetPreviewDependency = Mockito.mock(FilePreviewGetPreviewDependency.class);

        Mockito.doReturn(new BufferedReader(new StringReader(content))).when(mockedFilePreviewGetPreviewDependency)
                .bufferedReader(Mockito.anyString(), Mockito.any());

        var mockedFilePreview = Mockito.spy(new FilePreview(null, null, range));

        Mockito.doReturn(mockedFilePreviewGetPreviewDependency).when(mockedFilePreview)
                .getFilePreviewGetPreviewDependency();

        var mockedHtmlWriteDependency = Mockito.mock(HtmlWriteDependency.class);

        Mockito.doReturn(mockedFilePreview).when(mockedHtmlWriteDependency).filePreview(Mockito.anyString(),
                Mockito.any(), Mockito.any());

        var mockedHtml = Mockito.spy(html);

        Mockito.doReturn(mockedHtmlWriteDependency).when(mockedHtml).getHtmlWriteDependency();

        return mockedHtml;
    }

    static String getResourceContent(String resourceName) throws IOException {
        return Files.readString(Paths.get(Thread.currentThread().getContextClassLoader()
                .getResource(HtmlTest.SONARSCRATCH_CHECKER_REPORT_HTML + resourceName).getFile()));
    }

    static String getWriterString(BufferedWriter bufferedWriter, StringWriter stringWriter) throws IOException {
        bufferedWriter.flush();
        return stringWriter.toString();
    }

    static String getPreviewContent(String lineSeparator) {
        return HtmlTest.LINE1 + lineSeparator + HtmlTest.LINE2 + lineSeparator + HtmlTest.LINE3 + lineSeparator
                + HtmlTest.LINE4;
    }

    static void assertHtmlWithIssue(String issueContent, String componentContent, String assertFileName)
            throws TestException {
        try {
            var htmlWithIssue = getHtmlWithIssue(issueContent, componentContent);
            htmlWithIssue.getHtml().write();
            assertEquals(getResourceContent(assertFileName),
                    getWriterString(htmlWithIssue.getBufferedWriter(), htmlWithIssue.getStringWriter()));
        } catch (JSONException | IOException | HtmlException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    static HtmlWithIssue getHtmlWithIssue(String issueContent, String componentContent) throws JSONException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(issueContent));
        components
                .addComponent(new JSONObject(componentContent == null ? "{key:\"test-component\"}" : componentContent));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        return new HtmlWithIssue(stringWriter, bufferedWriter,
                new Html(issues, components, Arrays.asList(), null, bufferedWriter, new SystemLogger()));
    }

    static void assertMockedHtmlWithIssue(int startLine, int endLine, int startOffset, int endOffset,
            String previewContent, String assertFileName) throws TestException {
        try {
            var htmlWithIssue = getMockedHtmlWithIssue(startLine, endLine, startOffset, endOffset, previewContent);

            htmlWithIssue.getHtml().write();
            assertEquals(getResourceContent(assertFileName),
                    getWriterString(htmlWithIssue.getBufferedWriter(), htmlWithIssue.getStringWriter()));
        } catch (IOException | HtmlException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    static HtmlWithIssue getMockedHtmlWithIssue(int startLine, int endLine, int startOffset, int endOffset,
            String previewContent) throws TestException {
        try {
            var issues = new IssuesCollection(1);
            var components = new ComponentsCollection();

            issues.addIssue(new JSONObject(HtmlTest.MESSAGE_TEST_ISSUE_WITH_PREVIEW + HtmlTest.COMPONENT_PROJECT
                    + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                    + startOffset + ", endOffset : " + endOffset + "}" + "}"));
            components.addComponent(new JSONObject(HtmlTest.KEY_NAME_LONG_NAME_SRC));

            var stringWriter = new StringWriter();
            var bufferedWriter = new BufferedWriter(stringWriter);

            return new HtmlWithIssue(stringWriter, bufferedWriter,
                    mockFilePreview(new Range(startLine, endLine, startOffset, endOffset), previewContent,
                            new Html(issues, components, Arrays.asList(new Project(HtmlTest.TEST_COMPONENT, "src")),
                                    null, bufferedWriter, new SystemLogger())));
        } catch (JSONException | IOException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }
}
