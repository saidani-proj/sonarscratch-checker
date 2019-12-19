/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.junit.Test;
import org.mockito.Mockito;

public class FilePreviewTest {
    private final static String LINUX_LINE_SEPARATOR = "\n";
    private final static String MACOS_LINE_SEPARATOR = "\r";
    private final static String WINDOWS_LINE_SEPARATOR = "\r\n";
    private final static String LINE1 = "var x=f(2*z+10);";
    private final static String LINE2 = "f2();";
    private final static String LINE3 = "var y = f2(2*z);";
    private final static String LINE4 = "f3();";

    @Test
    public void test_GetPreview_1Line_AtStart() throws FileNotFoundException, IOException {
        var preview = getArrayList(
                mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2, new FilePreview(null, null, getRange(1, 1)))
                        .getPreview());
        assertEquals(2, preview.size());

        var firstPreview = preview.get(0);

        assertEquals(LINE1, firstPreview.getLine());
        assertEquals(1, firstPreview.getNumber());
        assertTrue(firstPreview.isIssueLine());
        assertEquals(2, firstPreview.getStart());
        assertEquals(4, firstPreview.getEnd());

        assertEquals(LINE2, preview.get(1).getLine());
        assertEquals(2, preview.get(1).getNumber());
        assertFalse(preview.get(1).isIssueLine());
    }

    @Test
    public void test_GetPreview_1Line_AtEnd() throws FileNotFoundException, IOException {
        var preview = getArrayList(
                mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2, new FilePreview(null, null, getRange(2, 2)))
                        .getPreview());
        assertEquals(2, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());
        assertEquals(LINE2, preview.get(1).getLine());
        assertTrue(preview.get(1).isIssueLine());
    }

    @Test
    public void test_GetPreview_1Line_() throws FileNotFoundException, IOException {
        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3,
                new FilePreview(null, null, getRange(2, 2))).getPreview());
        assertEquals(3, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());
        assertEquals(LINE2, preview.get(1).getLine());
        assertTrue(preview.get(1).isIssueLine());
        assertEquals(LINE3, preview.get(2).getLine());
        assertFalse(preview.get(2).isIssueLine());
    }

    @Test
    public void test_GetPreview_2Lines_SecondLine1Char() throws FileNotFoundException, IOException {
        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR,
                new FilePreview(null, null, new Range(1, 2, 0, LINE1.length() + 1))).getPreview());
        assertEquals(2, preview.size());

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
    public void test_GetPreview_2Lines_() throws FileNotFoundException, IOException {
        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3,
                new FilePreview(null, null, new Range(2, 3, 2, LINE2.length() + 4))).getPreview());
        assertEquals(3, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());

        var previewLine = preview.get(1);

        assertEquals(LINE2, previewLine.getLine());
        assertTrue(previewLine.isIssueLine());
        assertEquals(2, previewLine.getStart());
        assertEquals(5, previewLine.getEnd());

        previewLine = preview.get(2);

        assertEquals(LINE3, previewLine.getLine());
        assertTrue(previewLine.isIssueLine());
        assertEquals(0, previewLine.getStart());
        assertEquals(3, previewLine.getEnd());
    }

    @Test
    public void test_GetPreview_FileWith4Lines() throws FileNotFoundException, IOException {
        var preview = getArrayList(mockFilePreview(
                LINE1 + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3 + LINUX_LINE_SEPARATOR + LINE4,
                new FilePreview(null, null, getRange(4, 4))).getPreview());
        assertEquals(2, preview.size());

        assertEquals(LINE3, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());
        assertEquals(LINE4, preview.get(1).getLine());
        assertTrue(preview.get(1).isIssueLine());
    }

    @Test
    public void test_GetPreview_SurroundedLines_2() throws FileNotFoundException, IOException {
        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3,
                new FilePreview(null, null, getRange(3, 3), 2)).getPreview());
        assertEquals(3, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());
        assertEquals(LINE2, preview.get(1).getLine());
        assertFalse(preview.get(1).isIssueLine());
        assertEquals(LINE3, preview.get(2).getLine());
        assertTrue(preview.get(2).isIssueLine());
    }

    @Test
    public void test_GetPreview_SurroundedLines_() throws FileNotFoundException, IOException {
        var preview = getArrayList(mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2 + LINUX_LINE_SEPARATOR + LINE3,
                new FilePreview(null, null, getRange(2, 2), 1)).getPreview());
        assertEquals(3, preview.size());

        assertEquals(LINE1, preview.get(0).getLine());
        assertFalse(preview.get(0).isIssueLine());
        assertEquals(LINE2, preview.get(1).getLine());
        assertTrue(preview.get(1).isIssueLine());
        assertEquals(LINE3, preview.get(2).getLine());
        assertFalse(preview.get(2).isIssueLine());
    }

    @Test
    public void test_GetPreview_WrongRange_() throws FileNotFoundException, IOException {
        var preview = getArrayList(
                mockFilePreview(LINE1 + LINUX_LINE_SEPARATOR + LINE2, new FilePreview(null, null, getRange(20, 20)))
                        .getPreview());
        assertEquals(0, preview.size());
    }

    @Test
    public void test_GetPreview_LineSeparator_Linux() throws FileNotFoundException, IOException {
        getPreviewLineSeparator(LINUX_LINE_SEPARATOR);
    }

    @Test
    public void test_GetPreview_LineSeparator_MacOS() throws FileNotFoundException, IOException {
        getPreviewLineSeparator(MACOS_LINE_SEPARATOR);
    }

    @Test
    public void test_GetPreview_LineSeparator_Windows() throws FileNotFoundException, IOException {
        getPreviewLineSeparator(WINDOWS_LINE_SEPARATOR);
    }

    private static void getPreviewLineSeparator(String lineSeparator) throws FileNotFoundException, IOException {
        var preview = getArrayList(
                mockFilePreview(LINE1 + lineSeparator + LINE2, new FilePreview(null, null, getRange(1, 1)))
                        .getPreview());
        assertEquals(2, preview.size());

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

    private static Range getRange(int startLine, int endLine) {
        return new Range(startLine, endLine, 2, 4);
    }
}
