/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.hamcrest.core.IsInstanceOf;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import sonarscratch.checker.config.Project;
import sonarscratch.checker.issues.ComponentsCollection;
import sonarscratch.checker.issues.IssuesCollection;
import sonarscratch.checker.log.SystemLogger;

public class HtmlTest {
    private final ByteArrayOutputStream OUT_CONTENT = new ByteArrayOutputStream();
    private final PrintStream ORIGINAL_OUT = System.out;
    private final static String LINUX_LINE_SEPARATOR = "\n";
    private final static String MACOS_LINE_SEPARATOR = "\r";
    private final static String WINDOWS_LINE_SEPARATOR = "\r\n";
    private final static String LINE1 = "f2();";
    private final static String LINE2 = "var x=f(2*z+10);";
    private final static String LINE3 = "var y = f2(2*z);";
    private final static String LINE4 = "f3();";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void before() {
        System.setOut(new PrintStream(OUT_CONTENT));
    }

    @After
    public void after() {
        System.setOut(ORIGINAL_OUT);
    }

    @Test
    public void test_Write_Log() throws IOException, JSONException, HtmlException {
        new Html(new IssuesCollection(1), new ComponentsCollection(), Arrays.asList(new Project[0]), null,
                new BufferedWriter(new StringWriter()), new SystemLogger()).write();
        assertEquals("[INFO] : Writing HTML report" + System.lineSeparator() + "[INFO] : HTML report written"
                + System.lineSeparator() + "[INFO] : -----------------------------------------"
                + System.lineSeparator(), OUT_CONTENT.toString());
    }

    @Test
    public void test_Write_Total() throws IOException, JSONException, HtmlException {
        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(new IssuesCollection(126), new ComponentsCollection(), Arrays.asList(new Project[0]), null,
                bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-total.html"), getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_0() throws IOException, JSONException, HtmlException {
        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(new IssuesCollection(1), new ComponentsCollection(), Arrays.asList(new Project[0]), null,
                bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-issues-0.html"), getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Message_() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject("{message : \"This is an test issue\", component:\"test-component\"}"));
        components.addComponent(new JSONObject("{key:\"test-component\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(issues, components, Arrays.asList(new Project[0]), null, bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-issues-1-message-.html"), getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Message_ContainsHtmlTag() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with html tag <div>HTML</div>\", component:\"test-component\"}"));
        components.addComponent(new JSONObject("{key:\"test-component\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(issues, components, Arrays.asList(new Project[0]), null, bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-issues-1-message-containshtmltag.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Message_ContainsHtmlProperty() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with html property <div \\\"style\\\"=\\\"color:red\\\">HTML</div>\", component:\"test-component\"}"));
        components.addComponent(new JSONObject("{key:\"test-component\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(issues, components, Arrays.asList(new Project[0]), null, bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-issues-1-message-containshtmlproperty.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Type_Null() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with type\", type : null, component:\"test-component\"}"));
        components.addComponent(new JSONObject("{key:\"test-component\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(issues, components, Arrays.asList(new Project[0]), null, bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-issues-1-type-null.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Type_() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with type\", type : \"CODE_SMELL\", component:\"test-component\"}"));
        components.addComponent(new JSONObject("{key:\"test-component\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(issues, components, Arrays.asList(new Project[0]), null, bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-issues-1-type-.html"), getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Severity() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with severity\", severity : \"IMPORTANT\", component:\"test-component\"}"));
        components.addComponent(new JSONObject("{key:\"test-component\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(issues, components, Arrays.asList(new Project[0]), null, bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-issues-1-severity.html"), getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_File_() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(
                new JSONObject("{message : \"This is an test issue with file\", component:\"test-component\"}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(issues, components, Arrays.asList(new Project[0]), null, bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-issues-1-file-.html"), getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_File_WithSeverity() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with file and severity\", severity : \"IMPORTANT\", component:\"test-component\"}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(issues, components, Arrays.asList(new Project[0]), null, bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-issues-1-file-withseverity.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_EmptyFile() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 0;
        final int endLine = 0;
        final int startOffset = 0;
        final int endOffset = 0;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset), "",
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-emptyfile.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line_AtStart() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 2;
        final int startOffset = 0;
        final int endOffset = 5;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(LINUX_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-1line-atstart.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line_AtEnd() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 2;
        final int startOffset = 6;
        final int endOffset = LINE2.length();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(LINUX_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-1line-atend.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line__UnicodeEncoding() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : 5, endLine : 5, startOffset : 13, endOffset : 21}}"));
        final String fileName = "write-issues-1-preview-1line--unicodeencoding.java";
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"" + fileName + "\", longName : \"" + fileName + "\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", Paths
                .get(getClass().getClassLoader().getResource("sonarscratch/checker/report/html/" + fileName).getFile())
                .getParent().toAbsolutePath().toString()) }), StandardCharsets.UTF_8, bufferedWriter,
                new SystemLogger());

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-1line--unicodeencoding.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line__Windows1252Encoding()
            throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : 5, endLine : 5, startOffset : 13, endOffset : 20}}"));
        final String fileName = "write-issues-1-preview-1line--windows1252encoding.java";
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"" + fileName + "\", longName : \"" + fileName + "\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", Paths
                .get(getClass().getClassLoader().getResource("sonarscratch/checker/report/html/" + fileName).getFile())
                .getParent().toAbsolutePath().toString()) }), Charset.forName("windows-1252"), bufferedWriter,
                new SystemLogger());

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-1line--windows1252encoding.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line__OutOfPreview_Linux()
            throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 4;
        final int endLine = 4;
        final int startOffset = 0;
        final int endOffset = 2;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(WINDOWS_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-1line--outofpreview-linux.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line__OutOfPreview_() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 4;
        final int endLine = 4;
        final int startOffset = 0;
        final int endOffset = 2;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(MACOS_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-1line--outofpreview-.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line___EmptyLine() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 2;
        final int startOffset = 0;
        final int endOffset = 0;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                LINE1 + LINUX_LINE_SEPARATOR + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3,
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-1line---emptyline.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line____() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 2;
        final int startOffset = 5;
        final int endOffset = 6;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(LINUX_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-1line----.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line_White_Space() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 2;
        final int startOffset = 5;
        final int endOffset = 6;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(LINUX_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-1line-white-space.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line_White_Tab() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 2;
        final int startOffset = 5;
        final int endOffset = 6;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(LINUX_LINE_SEPARATOR).replace(" ", "\t"),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-1line-white-tab.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_2Lines_Linux() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 3;
        final int startOffset = 5;
        final int endOffset = 20;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(LINUX_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-2lines-linux.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_2Lines_MacOS() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 3;
        final int startOffset = 5;
        final int endOffset = 20;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(MACOS_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-2lines-macos.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_2Lines_Windows() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 3;
        final int startOffset = 5;
        final int endOffset = 21;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(WINDOWS_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-2lines-windows.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_3Lines() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 4;
        final int startOffset = 5;
        final int endOffset = 36;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        var html = mockFilePreview(new Range(startLine, endLine, startOffset, endOffset),
                getPreviewContent(LINUX_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, bufferedWriter, new SystemLogger()));

        html.write();
        assertEquals(getResourceContent("write-issues-1-preview-3lines.html"),
                getWriterString(bufferedWriter, stringWriter));
    }

    @Test
    public void test_Write_Issues_1_Preview_Component_Unspecified() throws IOException, JSONException, HtmlException {
        expectedException.expect(HtmlException.class);
        expectedException.expectMessage("Issue with unspecified component");

        var issues = new IssuesCollection(1);

        issues.addIssue(new JSONObject("{message : \"This is an test issue without project\"}"));

        new Html(issues, new ComponentsCollection(), Arrays.asList(new Project[0]), null,
                new BufferedWriter(new StringWriter()), new SystemLogger()).write();
    }

    @Test
    public void test_Write_Issues_1_Preview_Component_NotFound() throws IOException, JSONException, HtmlException {
        expectedException.expect(HtmlException.class);
        expectedException.expectMessage("Unfound component 'test-component'");

        var issues = new IssuesCollection(1);

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with unfound project\", component : \"test-component\"}"));

        new Html(issues, new ComponentsCollection(), Arrays.asList(new Project[0]), null,
                new BufferedWriter(new StringWriter()), new SystemLogger()).write();
    }

    @Test
    public void test_Write_Issues_1_Preview_Project_Unspecified() throws IOException, JSONException, HtmlException {
        expectedException.expect(HtmlException.class);
        expectedException.expectMessage("Issue with unspecified project");

        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(
                new JSONObject("{message : \"This is an test issue\", component:\"test-component\", textRange : {}}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"java/issue-file.java\"}"));

        new Html(issues, components, Arrays.asList(new Project[0]), null, new BufferedWriter(new StringWriter()),
                new SystemLogger()).write();

    }

    @Test
    public void test_Write_Issues_1_Preview_Project_NotFound() throws IOException, JSONException, HtmlException {
        expectedException.expect(HtmlException.class);
        expectedException.expectMessage("Unfound project 'test-project'");

        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue\", component:\"test-component\", project : \"test-project\", textRange : {}}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"java/issue-file.java\"}"));

        new Html(issues, components, Arrays.asList(new Project[0]), null, new BufferedWriter(new StringWriter()),
                new SystemLogger()).write();

    }

    @Test
    public void test_Write_Issues_1_Preview_Project__FileNotFound() throws IOException, JSONException, HtmlException {
        expectedException.expect(HtmlException.class);
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(FileNotFoundException.class));

        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with file\", component:\"test-component\", project:\"test-component\", textRange : {}}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"java/issue-file.java\"}"));

        new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }), null,
                new BufferedWriter(new StringWriter()), new SystemLogger()).write();
    }

    @Test
    public void test_Write_Issues_1_Preview_Project__ContentChanged() throws IOException, JSONException, HtmlException {
        expectedException.expect(HtmlException.class);
        expectedException.expectMessage("Can not create preview. Synchronize SonarQube informations with sources");

        var issues = new IssuesCollection(1);
        var components = new ComponentsCollection();

        final int startLine = 2;
        final int endLine = 2;
        final int startOffset = 5;
        final int endOffset = 4;

        issues.addIssue(new JSONObject(
                "{message : \"This is an test issue with preview\", component:\"test-component\", project : \"test-component\""
                        + ", textRange:{startLine : " + startLine + ", endLine : " + endLine + ", startOffset : "
                        + startOffset + ", endOffset : " + endOffset + "}" + "}"));
        components.addComponent(new JSONObject(
                "{key:\"test-component\", name : \"issue-file.java\", longName : \"src/issue-file.java\"}"));

        mockFilePreview(new Range(startLine, endLine, startOffset, endOffset), getPreviewContent(LINUX_LINE_SEPARATOR),
                new Html(issues, components, Arrays.asList(new Project[] { new Project("test-component", "src") }),
                        null, new BufferedWriter(new StringWriter()), new SystemLogger())).write();

    }

    @Test
    public void test_Write_Issues_2() throws IOException, JSONException, HtmlException {
        var issues = new IssuesCollection(5);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject("{message : \"This is an test issue (1)\", component:\"test-component\"}"));
        issues.addIssue(new JSONObject("{message : \"This is an test issue (2)\", component:\"test-component\"}"));
        components.addComponent(new JSONObject("{key:\"test-component\"}"));

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);
        new Html(issues, components, Arrays.asList(new Project[0]), null, bufferedWriter, new SystemLogger()).write();
        assertEquals(getResourceContent("write-issues-2.html"), getWriterString(bufferedWriter, stringWriter));
    }

    private static Html mockFilePreview(Range range, String content, Html html) throws IOException {
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

    private String getResourceContent(String resourceName) throws IOException {
        return Files.readString(Paths.get(
                getClass().getClassLoader().getResource("sonarscratch/checker/report/html/" + resourceName).getFile()));
    }

    private static String getWriterString(BufferedWriter bufferedWriter, StringWriter stringWriter) throws IOException {
        bufferedWriter.flush();
        return stringWriter.toString();
    }

    private static String getPreviewContent(String lineSeparator) {
        return LINE1 + lineSeparator + LINE2 + lineSeparator + LINE3 + lineSeparator + LINE4;
    }
}
