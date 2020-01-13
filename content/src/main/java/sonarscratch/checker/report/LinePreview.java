/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

class LinePreview {
    private int number;
    private String line;
    private boolean issueLine;
    private int start;
    private int end;

    LinePreview(int number, String line, int start, int end) {
        this.number = number;
        this.line = line;
        this.start = start;
        this.end = end;
        this.issueLine = true;
    }

    LinePreview(int number, String line) {
        this.number = number;
        this.line = line;
    }

    int getNumber() {
        return number;
    }

    String getLine() {
        return line;
    }

    boolean isIssueLine() {
        return issueLine;
    }

    int getStart() {
        return start;
    }

    int getEnd() {
        return end;
    }
}
