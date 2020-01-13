/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.http;

public class ClientException extends Exception {
    private static final long serialVersionUID = 1L;

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
