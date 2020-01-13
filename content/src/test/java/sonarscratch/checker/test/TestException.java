/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.test;

public class TestException extends Exception {
    private static final long serialVersionUID = 1L;

    public TestException(String message, Throwable cause) {
        super(message, cause);
    }
}
