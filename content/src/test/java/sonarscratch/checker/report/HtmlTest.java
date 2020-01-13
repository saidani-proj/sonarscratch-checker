/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;

import org.hamcrest.core.IsInstanceOf;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import sonarscratch.checker.config.Project;
import sonarscratch.checker.issues.ComponentsCollection;
import sonarscratch.checker.issues.IssuesCollection;
import sonarscratch.checker.log.SystemLogger;
import sonarscratch.checker.test.ExceptionAssert;
import sonarscratch.checker.test.StandardTest;
import sonarscratch.checker.test.TestException;
import sonarscratch.checker.util.ExceptionUtil;

public class HtmlTest extends StandardTest {
    private static final String KEY_NAME_LONG_NAME_JAVA = "{key:\"test-component\", name : \"issue-file.java\""
            + ", longName : \"java/issue-file.java\"}";
    public static final String SONARSCRATCH_CHECKER_REPORT_HTML = "sonarscratch/checker/report/html/";
    public static final String TEST_COMPONENT = "test-component";
    public static final String COMPONENT_PROJECT = "component:\"test-component\", project : \"test-component\"";
    public static final String MESSAGE_TEST_ISSUE_WITH_PREVIEW = "{message : \"This is an test issue with preview\", ";
    public static final String KEY_NAME_LONG_NAME_SRC = "{key:\"test-component\", name : \"issue-file.java\", "
            + "longName : \"src/issue-file.java\"}";
    private static final String LINUX_LINE_SEPARATOR = "\n";
    private static final String MACOS_LINE_SEPARATOR = "\r";
    private static final String WINDOWS_LINE_SEPARATOR = "\r\n";
    public static final String LINE1 = "f2();";
    public static final String LINE2 = "var x=f(2*z+10);";
    public static final String LINE3 = "var y = f2(2*z);";
    public static final String LINE4 = "f3();";
    private static final int STANDARD_START_LINE = 2;
    private static final int STANDARD_END_LINE = 2;

    @Test
    public void test_Write_Log() throws HtmlException {
        new Html(new IssuesCollection(1), new ComponentsCollection(), Arrays.asList(), null,
                new BufferedWriter(new StringWriter()), new SystemLogger()).write();
        assertEquals("[INFO] : Writing HTML report" + System.lineSeparator() + "[INFO] : HTML report written"
                + System.lineSeparator() + "[INFO] : -----------------------------------------"
                + System.lineSeparator(), getOutContent().toString(StandardCharsets.UTF_8));
    }

    @Test
    public void test_Write_Total() throws TestException {
        try {
            var stringWriter = new StringWriter();
            var bufferedWriter = new BufferedWriter(stringWriter);
            final int TOTAL = 126;
            new Html(new IssuesCollection(TOTAL), new ComponentsCollection(), Arrays.asList(), null, bufferedWriter,
                    new SystemLogger()).write();
            assertEquals(HtmlTestUtil.getResourceContent("write-total.html"),
                    HtmlTestUtil.getWriterString(bufferedWriter, stringWriter));
        } catch (IOException | HtmlException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    @Test
    public void test_Write_Issues_0() throws TestException {
        try {
            var stringWriter = new StringWriter();
            var bufferedWriter = new BufferedWriter(stringWriter);
            new Html(new IssuesCollection(1), new ComponentsCollection(), Arrays.asList(), null, bufferedWriter,
                    new SystemLogger()).write();
            assertEquals(HtmlTestUtil.getResourceContent("write-issues-0.html"),
                    HtmlTestUtil.getWriterString(bufferedWriter, stringWriter));
        } catch (IOException | HtmlException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    @Test
    public void test_Write_Issues_1_Message_() throws TestException {
        HtmlTestUtil.assertHtmlWithIssue("{message : \"This is an test issue\", component:\"test-component\"}", null,
                "write-issues-1-message-.html");
    }

    @Test
    public void test_Write_Issues_1_Message_ContainsHtmlTag() throws TestException {
        HtmlTestUtil.assertHtmlWithIssue("{message : \"This is an test issue with html tag <div>HTML</div>\", "
                + "component:\"test-component\"}", null, "write-issues-1-message-containshtmltag.html");
    }

    @Test
    public void test_Write_Issues_1_Message_ContainsHtmlProperty() throws TestException {
        HtmlTestUtil.assertHtmlWithIssue(
                "{message : \"This is an test issue with html property "
                        + "<div \\\"style\\\"=\\\"color:red\\\">HTML</div>\", component:\"test-component\"}",
                null, "write-issues-1-message-containshtmlproperty.html");
    }

    @Test
    public void test_Write_Issues_1_Type_Null() throws TestException {
        HtmlTestUtil.assertHtmlWithIssue(
                "{message : \"This is an test issue with type\", type : null, component:\"test-component\"}", null,
                "write-issues-1-type-null.html");
    }

    @Test
    public void test_Write_Issues_1_Type_() throws TestException {
        HtmlTestUtil.assertHtmlWithIssue("{message : \"This is an test issue with type\", "
                + "type : \"CODE_SMELL\", component:\"test-component\"}", null, "write-issues-1-type-.html");
    }

    @Test
    public void test_Write_Issues_1_Severity() throws TestException {
        HtmlTestUtil.assertHtmlWithIssue("{message : \"This is an test issue with severity\", severity : \"IMPORTANT\""
                + ", component:\"test-component\"}", null, "write-issues-1-severity.html");
    }

    @Test
    public void test_Write_Issues_1_File_() throws TestException {
        HtmlTestUtil.assertHtmlWithIssue(
                "{message : \"This is an test issue with file\", component:\"test-component\"}", KEY_NAME_LONG_NAME_SRC,
                "write-issues-1-file-.html");
    }

    @Test
    public void test_Write_Issues_1_File_WithSeverity() throws TestException {
        HtmlTestUtil.assertHtmlWithIssue(
                "{message : \"This is an test issue with file and severity\", "
                        + "severity : \"IMPORTANT\", component:\"test-component\"}",
                KEY_NAME_LONG_NAME_SRC, "write-issues-1-file-withseverity.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_EmptyFile() throws TestException {
        final int START_LINE = 0;
        final int END_LINE = 0;
        final int START_OFFSET = 0;
        final int END_OFFSET = 0;

        HtmlTestUtil.assertMockedHtmlWithIssue(START_LINE, END_LINE, START_OFFSET, END_OFFSET, "",
                "write-issues-1-preview-emptyfile.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line_AtStart() throws TestException {
        final int START_OFFSET = 0;
        final int END_OFFSET = 5;

        HtmlTestUtil.assertMockedHtmlWithIssue(STANDARD_START_LINE, STANDARD_END_LINE, START_OFFSET, END_OFFSET,
                HtmlTestUtil.getPreviewContent(LINUX_LINE_SEPARATOR), "write-issues-1-preview-1line-atstart.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line_AtEnd() throws TestException {
        final int START_OFFSET = 6;

        HtmlTestUtil.assertMockedHtmlWithIssue(STANDARD_START_LINE, STANDARD_END_LINE, START_OFFSET, LINE2.length(),
                HtmlTestUtil.getPreviewContent(LINUX_LINE_SEPARATOR), "write-issues-1-preview-1line-atend.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line__UnicodeEncoding() throws TestException {
        try {
            var issues = new IssuesCollection(1);
            var components = new ComponentsCollection();

            issues.addIssue(new JSONObject(MESSAGE_TEST_ISSUE_WITH_PREVIEW + COMPONENT_PROJECT
                    + ", textRange:{startLine : 5, endLine : 5, startOffset : 13, endOffset : 21}}"));
            final String FILE_NAME = "write-issues-1-preview-1line--unicodeencoding.java";
            components.addComponent(new JSONObject(
                    "{key:\"test-component\", name : \"" + FILE_NAME + "\", longName : \"" + FILE_NAME + "\"}"));

            var stringWriter = new StringWriter();
            var bufferedWriter = new BufferedWriter(stringWriter);

            var html = new Html(issues, components,
                    Arrays.asList(new Project(TEST_COMPONENT,
                            Paths.get(Thread.currentThread().getContextClassLoader()
                                    .getResource(SONARSCRATCH_CHECKER_REPORT_HTML + FILE_NAME).getFile()).getParent()
                                    .toAbsolutePath().toString())),
                    StandardCharsets.UTF_8, bufferedWriter, new SystemLogger());

            html.write();
            assertEquals(HtmlTestUtil.getResourceContent("write-issues-1-preview-1line--unicodeencoding.html"),
                    HtmlTestUtil.getWriterString(bufferedWriter, stringWriter));
        } catch (IOException | JSONException | HtmlException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line__Windows1252Encoding() throws TestException {
        try {
            var issues = new IssuesCollection(1);
            var components = new ComponentsCollection();

            issues.addIssue(new JSONObject(MESSAGE_TEST_ISSUE_WITH_PREVIEW + COMPONENT_PROJECT
                    + ", textRange:{startLine : 5, endLine : 5, startOffset : 13, endOffset : 20}}"));
            final String FILE_NAME = "write-issues-1-preview-1line--windows1252encoding.java";
            components.addComponent(new JSONObject(
                    "{key:\"test-component\", name : \"" + FILE_NAME + "\", longName : \"" + FILE_NAME + "\"}"));

            var stringWriter = new StringWriter();
            var bufferedWriter = new BufferedWriter(stringWriter);

            var html = new Html(issues, components,
                    Arrays.asList(new Project(TEST_COMPONENT,
                            Paths.get(Thread.currentThread().getContextClassLoader()
                                    .getResource(SONARSCRATCH_CHECKER_REPORT_HTML + FILE_NAME).getFile()).getParent()
                                    .toAbsolutePath().toString())),
                    Charset.forName("windows-1252"), bufferedWriter, new SystemLogger());

            html.write();
            assertEquals(HtmlTestUtil.getResourceContent("write-issues-1-preview-1line--windows1252encoding.html"),
                    HtmlTestUtil.getWriterString(bufferedWriter, stringWriter));
        } catch (IOException | JSONException | HtmlException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line__OutOfPreview_Linux() throws TestException {
        final int START_LINE = 4;
        final int END_LINE = 4;
        final int START_OFFSET = 0;
        final int END_OFFSET = 2;

        HtmlTestUtil.assertMockedHtmlWithIssue(START_LINE, END_LINE, START_OFFSET, END_OFFSET,
                HtmlTestUtil.getPreviewContent(WINDOWS_LINE_SEPARATOR),
                "write-issues-1-preview-1line--outofpreview-linux.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line__OutOfPreview_() throws TestException {
        final int START_LINE = 4;
        final int END_LINE = 4;
        final int START_OFFSET = 0;
        final int END_OFFSET = 2;

        HtmlTestUtil.assertMockedHtmlWithIssue(START_LINE, END_LINE, START_OFFSET, END_OFFSET,
                HtmlTestUtil.getPreviewContent(MACOS_LINE_SEPARATOR),
                "write-issues-1-preview-1line--outofpreview-.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line___EmptyLine() throws TestException {
        final int START_OFFSET = 0;
        final int END_OFFSET = 0;

        HtmlTestUtil.assertMockedHtmlWithIssue(STANDARD_START_LINE, STANDARD_END_LINE, START_OFFSET, END_OFFSET,
                LINE1 + LINUX_LINE_SEPARATOR + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3,
                "write-issues-1-preview-1line---emptyline.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line____() throws TestException {
        final int START_OFFSET = 5;
        final int END_OFFSET = 6;

        HtmlTestUtil.assertMockedHtmlWithIssue(STANDARD_START_LINE, STANDARD_END_LINE, START_OFFSET, END_OFFSET,
                HtmlTestUtil.getPreviewContent(LINUX_LINE_SEPARATOR), "write-issues-1-preview-1line----.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line_White_Space() throws TestException {
        final int START_OFFSET = 5;
        final int END_OFFSET = 6;

        HtmlTestUtil.assertMockedHtmlWithIssue(STANDARD_START_LINE, STANDARD_END_LINE, START_OFFSET, END_OFFSET,
                HtmlTestUtil.getPreviewContent(LINUX_LINE_SEPARATOR), "write-issues-1-preview-1line-white-space.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_1Line_White_Tab() throws TestException {
        final int START_OFFSET = 5;
        final int END_OFFSET = 6;

        HtmlTestUtil.assertMockedHtmlWithIssue(STANDARD_START_LINE, STANDARD_END_LINE, START_OFFSET, END_OFFSET,
                HtmlTestUtil.getPreviewContent(LINUX_LINE_SEPARATOR).replace(" ", "\t"),
                "write-issues-1-preview-1line-white-tab.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_2Lines_Linux() throws TestException {
        final int END_LINE = 3;
        final int START_OFFSET = 5;
        final int END_OFFSET = 20;

        HtmlTestUtil.assertMockedHtmlWithIssue(STANDARD_START_LINE, END_LINE, START_OFFSET, END_OFFSET,
                HtmlTestUtil.getPreviewContent(LINUX_LINE_SEPARATOR), "write-issues-1-preview-2lines-linux.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_2Lines_MacOS() throws TestException {
        final int END_LINE = 3;
        final int START_OFFSET = 5;
        final int END_OFFSET = 20;

        HtmlTestUtil.assertMockedHtmlWithIssue(STANDARD_START_LINE, END_LINE, START_OFFSET, END_OFFSET,
                HtmlTestUtil.getPreviewContent(MACOS_LINE_SEPARATOR), "write-issues-1-preview-2lines-macos.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_2Lines_Windows() throws TestException {
        final int END_LINE = 3;
        final int START_OFFSET = 5;
        final int END_OFFSET = 21;

        HtmlTestUtil.assertMockedHtmlWithIssue(STANDARD_START_LINE, END_LINE, START_OFFSET, END_OFFSET,
                HtmlTestUtil.getPreviewContent(WINDOWS_LINE_SEPARATOR), "write-issues-1-preview-2lines-windows.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_3Lines() throws TestException {
        final int END_LINE = 4;
        final int START_OFFSET = 5;
        final int END_OFFSET = 36;

        HtmlTestUtil.assertMockedHtmlWithIssue(STANDARD_START_LINE, END_LINE, START_OFFSET, END_OFFSET,
                HtmlTestUtil.getPreviewContent(LINUX_LINE_SEPARATOR), "write-issues-1-preview-3lines.html");
    }

    @Test
    public void test_Write_Issues_1_Preview_Component_Unspecified() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(HtmlException.class));

        try {
            HtmlTestUtil.getHtmlWithIssue("{message : \"This is an test issue without project\"}", null).html.write();
        } catch (JSONException | HtmlException exception) {
            ExceptionAssert.checkMessage("Issue with unspecified component", exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    @Test
    public void test_Write_Issues_1_Preview_Component_NotFound() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(HtmlException.class));

        try {
            var issues = new IssuesCollection(1);

            issues.addIssue(new JSONObject(
                    "{message : \"This is an test issue with unfound project\", component : \"test-component\"}"));
            new Html(issues, new ComponentsCollection(), Arrays.asList(), null, new BufferedWriter(new StringWriter()),
                    new SystemLogger()).write();
        } catch (JSONException | HtmlException exception) {
            ExceptionAssert.checkMessage("Unfound component 'test-component'", exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    @Test
    public void test_Write_Issues_1_Preview_Project_Unspecified() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(HtmlException.class));

        try {
            var issues = new IssuesCollection(1);
            var components = new ComponentsCollection();

            issues.addIssue(new JSONObject(
                    "{message : \"This is an test issue\", component:\"test-component\", textRange : {}}"));
            components.addComponent(new JSONObject(KEY_NAME_LONG_NAME_JAVA));

            new Html(issues, components, Arrays.asList(), null, new BufferedWriter(new StringWriter()),
                    new SystemLogger()).write();
        } catch (JSONException | HtmlException exception) {
            ExceptionAssert.checkMessage("Issue with unspecified project", exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    @Test
    public void test_Write_Issues_1_Preview_Project_NotFound() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(HtmlException.class));

        try {
            var issues = new IssuesCollection(1);
            var components = new ComponentsCollection();

            issues.addIssue(new JSONObject("{message : \"This is an test issue\", component:\"test-component\", "
                    + "project : \"test-project\", textRange : {}}"));
            components.addComponent(new JSONObject(KEY_NAME_LONG_NAME_JAVA));

            new Html(issues, components, Arrays.asList(), null, new BufferedWriter(new StringWriter()),
                    new SystemLogger()).write();
        } catch (JSONException | HtmlException exception) {
            ExceptionAssert.checkMessage("Unfound project 'test-project'", exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    @Test
    public void test_Write_Issues_1_Preview_Project__FileNotFound() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(HtmlException.class));

        try {
            var issues = new IssuesCollection(1);
            var components = new ComponentsCollection();

            issues.addIssue(new JSONObject("{message : \"This is an test issue with file\", "
                    + "component:\"test-component\", project:\"test-component\", textRange : {}}"));
            components.addComponent(new JSONObject(KEY_NAME_LONG_NAME_JAVA));

            new Html(issues, components, Arrays.asList(new Project(TEST_COMPONENT, "src")), null,
                    new BufferedWriter(new StringWriter()), new SystemLogger()).write();
        } catch (JSONException | HtmlException exception) {
            ExceptionAssert.checkType(FileNotFoundException.class, exception.getCause());
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    @Test
    public void test_Write_Issues_1_Preview_Project__ContentChanged() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(HtmlException.class));

        try {
            final int START_OFFSET = 5;
            final int END_OFFSET = 4;

            HtmlTestUtil.getMockedHtmlWithIssue(STANDARD_START_LINE, STANDARD_END_LINE, START_OFFSET, END_OFFSET,
                    HtmlTestUtil.getPreviewContent(LINUX_LINE_SEPARATOR)).html.write();
        } catch (HtmlException exception) {
            ExceptionAssert.checkMessage("Can not create preview. Synchronize SonarQube informations with sources",
                    exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    @Test
    public void test_Write_Issues_2() throws TestException {
        try {
            final int TOTAL = 5;
            var issues = new IssuesCollection(TOTAL);
            var components = new ComponentsCollection();

            issues.addIssue(new JSONObject("{message : \"This is an test issue (1)\", component:\"test-component\"}"));
            issues.addIssue(new JSONObject("{message : \"This is an test issue (2)\", component:\"test-component\"}"));
            components.addComponent(new JSONObject("{key:\"test-component\"}"));

            var stringWriter = new StringWriter();
            var bufferedWriter = new BufferedWriter(stringWriter);
            new Html(issues, components, Arrays.asList(), null, bufferedWriter, new SystemLogger()).write();
            assertEquals(HtmlTestUtil.getResourceContent("write-issues-2.html"),
                    HtmlTestUtil.getWriterString(bufferedWriter, stringWriter));
        } catch (IOException | JSONException | HtmlException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(HtmlTest.class), exception);
        }
    }

    static final class HtmlWithIssue {
        private StringWriter stringWriter;
        private BufferedWriter bufferedWriter;
        private Html html;

        HtmlWithIssue(StringWriter stringWriter, BufferedWriter bufferedWriter, Html html) {
            this.stringWriter = stringWriter;
            this.bufferedWriter = bufferedWriter;
            this.html = html;
        }

        public BufferedWriter getBufferedWriter() {
            return bufferedWriter;
        }

        public StringWriter getStringWriter() {
            return stringWriter;
        }

        public Html getHtml() {
            return html;
        }
    }
}
