/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.report;

public class HtmlException extends Exception {
    private static final long serialVersionUID = 1L;

    public HtmlException(String message) {
        super(message);
    }

    public HtmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
