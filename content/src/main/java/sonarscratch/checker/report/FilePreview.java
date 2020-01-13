/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

class FilePreview {
    private static final int WINDOWS_END_LINE_COUNT = 2;
    private static final int END_OF_FILE = -2;
    private static final int DEFAULT_SURROUNDED_LINES_COUNT = 1;
    private String filePath;
    private Charset encoding;
    private Range range;
    private int surroundedLinesCount;

    FilePreview(String filePath, Charset encoding, Range range, int surroundedLinesCount) {
        this.filePath = filePath;
        this.encoding = encoding;
        this.range = range;
        this.surroundedLinesCount = surroundedLinesCount;
    }

    FilePreview(String filePath, Charset encoding, Range range) {
        this(filePath, encoding, range, DEFAULT_SURROUNDED_LINES_COUNT);
    }

    FilePreviewGetPreviewDependency getFilePreviewGetPreviewDependency() {
        return new FilePreviewGetPreviewDependency();
    }

    Iterable<LinePreview> getPreview() throws IOException {
        var linePreviews = new ArrayList<LinePreview>();
        var bufferedReader = getFilePreviewGetPreviewDependency().bufferedReader(filePath, this.encoding);

        try {
            addLinePreviews(linePreviews, bufferedReader);
        } finally {
            bufferedReader.close();
        }

        return linePreviews;
    }

    private void addLinePreviews(ArrayList<LinePreview> linePreviews, BufferedReader bufferedReader)
            throws IOException {
        int c = -1;
        int lineNumber = 1;
        int lineStartOffset = -1;

        do {
            var isBeforeLine = range.getStartLine() - lineNumber > 0
                    && range.getStartLine() - lineNumber <= surroundedLinesCount;
            var isIssueLine = lineNumber >= range.getStartLine() && lineNumber <= range.getEndLine();
            var isAfterLine = lineNumber - range.getEndLine() > 0
                    && lineNumber - range.getEndLine() <= surroundedLinesCount;
            StringBuilder lineBuilder = null;

            if (isBeforeLine || isIssueLine || isAfterLine) {
                lineBuilder = new StringBuilder();
                lineStartOffset = getLineStartOffset(isIssueLine, lineStartOffset);
            }

            c = getLine(bufferedReader, lineBuilder, c);
            lineStartOffset = addLinePreview(lineNumber, lineNumber == range.getStartLine(), lineBuilder, isIssueLine,
                    lineStartOffset, linePreviews);
            lineNumber++;
        } while (c != END_OF_FILE);
    }

    private int getLineStartOffset(boolean isIssueLine, int lastLineStartOffset) {
        var lineStartOffset = lastLineStartOffset;

        if (isIssueLine) {
            if (lineStartOffset == -1) {
                lineStartOffset = range.getStartOffset();
            }
        } else {
            lineStartOffset = -1;
        }

        return lineStartOffset;
    }

    private int addLinePreview(int lineNumber, boolean isFirstLine, StringBuilder lineBuilder, boolean isIssueLine,
            int lineStartOffset, ArrayList<LinePreview> linePreviews) {
        if (lineBuilder != null) {
            var line = cleanLine(lineBuilder.toString());
            LinePreview linePreview;

            if (isIssueLine) {
                var offset = isFirstLine ? lineStartOffset : 0;
                var isLastLine = range.getEndOffset() < lineStartOffset + (lineBuilder.length() - offset);
                var linePreviewLength = isLastLine ? (range.getEndOffset() - lineStartOffset)
                        : (line.length() - offset);

                linePreview = new LinePreview(lineNumber, line, offset, offset + linePreviewLength);
                lineStartOffset = lineStartOffset + linePreviewLength
                        + (isLastLine ? 0 : (lineBuilder.length() - line.length()));
            } else {
                linePreview = new LinePreview(lineNumber, line);
            }

            linePreviews.add(linePreview);
        }

        return lineStartOffset;
    }

    private static int getLine(BufferedReader bufferedReader, StringBuilder lineBuilder, int addChar)
            throws IOException {
        if (addChar != -1 && lineBuilder != null) {
            lineBuilder.append((char) addChar);
        }

        int c;
        int lastChar = -1;

        while ((c = bufferedReader.read()) != -1) {
            if (lastChar == '\r') {
                return windowsEndOfLine(c, lineBuilder);
            } else {
                if (lineBuilder != null) {
                    lineBuilder.append((char) c);
                }

                if (c == '\n') {
                    return -1;
                }
            }

            lastChar = c;
        }

        return END_OF_FILE;

    }

    private static String cleanLine(String line) {
        int length = line.length();

        if (length > 1 && line.charAt(length - 1) == '\n' && line.charAt(length - WINDOWS_END_LINE_COUNT) == '\r') {
            return line.substring(0, length - WINDOWS_END_LINE_COUNT);
        } else if (length > 0 && (line.charAt(length - 1) == '\n' || line.charAt(length - 1) == '\r')) {
            return line.substring(0, length - 1);
        } else {
            return line;
        }
    }

    private static int windowsEndOfLine(int c, StringBuilder lineBuilder) {
        if (c == '\n') {
            if (lineBuilder != null) {
                lineBuilder.append('\n');
            }

            return -1;
        } else {
            return c;
        }
    }
}
