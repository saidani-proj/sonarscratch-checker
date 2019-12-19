/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.issues;

public class FinderException extends Exception {
    private static final long serialVersionUID = 1L;

    public FinderException(String message) {
        super(message);
    }

    public FinderException(String message, Throwable cause) {
        super(message, cause);
    }
}
