/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.junit.Test;
import org.mockito.Mockito;

public class FilePreviewTest {
    private static final int THIRD_LINE_PREVIEW = 2;
    private static final int STANDARD_RANGE_START = 2;
    private static final int STANDARD_RANGE_END = 2;
    private static final int STANDARD_PREVIEW_SIZE = 2;
    private static final String LINUX_LINE_SEPARATOR = "\n";
    private static final String MACOS_LINE_SEPARATOR = "\r";
    private static final String WINDOWS_LINE_SEPARATOR = "\r\n";
    private static final String LINE1 = "var x=f(2*z+10);";
    private static final String LINE2 = "f2();";
    private static final String LINE3 = "var y = f2(2*z);";
    private static final String LINE4 = "f3();";

    @Test
    public void test_GetPreview_1Line_AtStart() throws IOException {
        var preview = getArrayList(
                mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2, new FilePreview(null, null, getRange2Chars(1, 1)))
                        .getPreview());
        assertEquals(STANDARD_PREVIEW_SIZE, preview.size());

        var firstPreview = preview.get(0);

        assertEquals(LINE1, firstPreview.getLine());
        assertEquals(1, firstPreview.getNumber());
        assertTrue(firstPreview.isIssueLine());

        final int PREVIEW_START = 2;
        final int PREVIEW_END = 4;

        assertEquals(PREVIEW_START, firstPreview.getStart());
        assertEquals(PREVIEW_END, firstPreview.getEnd());

        assertEquals(LINE2, preview.get(1).getLine());

        final int PREVIEW_NUMBER = 2;

        assertEquals(PREVIEW_NUMBER, preview.get(1).getNumber());
        assertFalse(preview.get(1).isIssueLine());
    }

    @Test
    public void test_GetPreview_1Line_AtEnd() throws IOException {
        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2,
                new FilePreview(null, null, getRange2Chars(STANDARD_RANGE_START, STANDARD_RANGE_END))).getPreview());
        assertEquals(STANDARD_PREVIEW_SIZE, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());
        assertEquals(LINE2, preview.get(1).getLine());
        assertTrue(preview.get(1).isIssueLine());
    }

    @Test
    public void test_GetPreview_1Line_() throws IOException {
        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3,
                new FilePreview(null, null, getRange2Chars(STANDARD_RANGE_START, STANDARD_RANGE_END))).getPreview());
        assertEquals(STANDARD_PREVIEW_SIZE + 1, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());
        assertEquals(LINE2, preview.get(1).getLine());
        assertTrue(preview.get(1).isIssueLine());

        assertEquals(LINE3, preview.get(THIRD_LINE_PREVIEW).getLine());
        assertFalse(preview.get(THIRD_LINE_PREVIEW).isIssueLine());
    }

    @Test
    public void test_GetPreview_2Lines_SecondLine1Char() throws IOException {
        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR,
                new FilePreview(null, null, new Range(1, STANDARD_RANGE_END, 0, LINE1.length() + 1))).getPreview());
        assertEquals(STANDARD_PREVIEW_SIZE, preview.size());

        var previewLine = preview.get(0);

        assertEquals(LINE1, previewLine.getLine());
        assertTrue(previewLine.isIssueLine());
        assertEquals(0, previewLine.getStart());
        assertEquals(LINE1.length(), previewLine.getEnd());

        previewLine = preview.get(1);

        assertEquals("", previewLine.getLine());
        assertTrue(previewLine.isIssueLine());
        assertEquals(0, previewLine.getStart());
        assertEquals(0, previewLine.getEnd());
    }

    @Test
    public void test_GetPreview_2Lines_() throws IOException {
        final int START_OFFSET = 2;
        final int LINE2_TO_ADD = 4;

        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3,
                new FilePreview(null, null, new Range(STANDARD_RANGE_START, STANDARD_RANGE_START + 1, START_OFFSET,
                        LINE2.length() + LINE2_TO_ADD))).getPreview());
        assertEquals(STANDARD_PREVIEW_SIZE + 1, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());

        var previewLine = preview.get(1);

        assertEquals(LINE2, previewLine.getLine());
        assertTrue(previewLine.isIssueLine());
        assertEquals(STANDARD_RANGE_START, previewLine.getStart());

        final int LINE2_PREVIEW_LINE_END = 5;

        assertEquals(LINE2_PREVIEW_LINE_END, previewLine.getEnd());

        previewLine = preview.get(THIRD_LINE_PREVIEW);

        assertEquals(LINE3, previewLine.getLine());
        assertTrue(previewLine.isIssueLine());
        assertEquals(0, previewLine.getStart());

        final int LINE3_PREVIEW_LINE_END = 3;

        assertEquals(LINE3_PREVIEW_LINE_END, previewLine.getEnd());
    }

    @Test
    public void test_GetPreview_FileWith4Lines() throws IOException {
        final int START_LINE = 4;
        final int END_LINE = 4;

        var preview = getArrayList(mockFilePreview(
                LINE1 + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3 + LINUX_LINE_SEPARATOR + LINE4,
                new FilePreview(null, null, getRange2Chars(START_LINE, END_LINE))).getPreview());
        assertEquals(STANDARD_PREVIEW_SIZE, preview.size());

        assertEquals(LINE3, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());
        assertEquals(LINE4, preview.get(1).getLine());
        assertTrue(preview.get(1).isIssueLine());
    }

    @Test
    public void test_GetPreview_SurroundedLines_2() throws IOException {
        final int SURROUNDED_LINES_COUNT = 2;

        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3,
                new FilePreview(null, null, getRange2Chars(STANDARD_RANGE_START + 1, STANDARD_RANGE_END + 1),
                        SURROUNDED_LINES_COUNT)).getPreview());
        assertEquals(STANDARD_PREVIEW_SIZE + 1, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());
        assertEquals(LINE2, preview.get(1).getLine());
        assertFalse(preview.get(1).isIssueLine());
        assertEquals(LINE3, preview.get(THIRD_LINE_PREVIEW).getLine());
        assertTrue(preview.get(THIRD_LINE_PREVIEW).isIssueLine());
    }

    @Test
    public void test_GetPreview_SurroundedLines_() throws IOException {
        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3,
                new FilePreview(null, null, getRange2Chars(STANDARD_RANGE_START, STANDARD_RANGE_END), 1)).getPreview());
        assertEquals(STANDARD_PREVIEW_SIZE + 1, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());
        assertEquals(LINE2, preview.get(1).getLine());
        assertTrue(preview.get(1).isIssueLine());
        assertEquals(LINE3, preview.get(THIRD_LINE_PREVIEW).getLine());
        assertFalse(preview.get(THIRD_LINE_PREVIEW).isIssueLine());
    }

    @Test
    public void test_GetPreview_WrongRange_() throws IOException {
        final int START_LINE = 20;
        final int END_LINE = 20;

        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2,
                new FilePreview(null, null, getRange2Chars(START_LINE, END_LINE))).getPreview());
        assertEquals(0, preview.size());
    }

    @Test
    public void test_GetPreview_LineSeparator_Linux() throws IOException {
        getPreviewLineSeparator(LINUX_LINE_SEPARATOR);
    }

    @Test
    public void test_GetPreview_LineSeparator_MacOS() throws IOException {
        getPreviewLineSeparator(MACOS_LINE_SEPARATOR);
    }

    @Test
    public void test_GetPreview_LineSeparator_Windows() throws IOException {
        getPreviewLineSeparator(WINDOWS_LINE_SEPARATOR);
    }

    private static void getPreviewLineSeparator(String lineSeparator) throws IOException {
        var preview = getArrayList(
                mockFilePreview(LINE1 + lineSeparator + LINE2, new FilePreview(null, null, getRange2Chars(1, 1)))
                        .getPreview());
        assertEquals(STANDARD_PREVIEW_SIZE, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertTrue(preview.get(0).isIssueLine());
        assertEquals(LINE2, preview.get(1).getLine());
        assertFalse(preview.get(1).isIssueLine());
    }

    private static FilePreview mockFilePreview(String content, FilePreview filePreview) throws IOException {
        var mockedFilePreviewGetPreviewDependency = Mockito.mock(FilePreviewGetPreviewDependency.class);

        Mockito.doReturn(new BufferedReader(new StringReader(content))).when(mockedFilePreviewGetPreviewDependency)
                .bufferedReader(Mockito.anyString(), Mockito.any());

        var mockedFilePreview = Mockito.spy(filePreview);

        Mockito.doReturn(mockedFilePreviewGetPreviewDependency).when(mockedFilePreview)
                .getFilePreviewGetPreviewDependency();
        return mockedFilePreview;
    }

    private static ArrayList<LinePreview> getArrayList(Iterable<LinePreview> linePreviews) {
        var list = new ArrayList<LinePreview>();

        for (LinePreview linePreview : linePreviews) {
            list.add(linePreview);
        }

        return list;
    }

    private static Range getRange2Chars(int startLine, int endLine) {
        final int START_OFFSET = 2;
        final int END_OFFSET = 4;

        return new Range(startLine, endLine, START_OFFSET, END_OFFSET);
    }
}
