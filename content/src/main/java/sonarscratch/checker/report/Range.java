/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

class Range {
    private int startLine;
    private int endLine;
    private int startOffset;
    private int endOffset;

    Range(int startLine, int endLine, int startOffset, int endOffset) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    int getStartLine() {
        return startLine;
    }

    int getEndLine() {
        return endLine;
    }

    int getStartOffset() {
        return startOffset;
    }

    int getEndOffset() {
        return endOffset;
    }
}
