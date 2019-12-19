/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.app;

public class AppException extends Exception {
    private static final long serialVersionUID = 1L;

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
