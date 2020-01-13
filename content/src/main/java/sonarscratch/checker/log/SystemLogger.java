/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.log;

import java.io.PrintStream;

public class SystemLogger {
    public static final String BLOCK_END = "-----------------------------------------";
    private static final String LINE_SEPARATOR = System.lineSeparator();

    public PrintStream outStream() {
        return System.out;
    }

    public PrintStream errStream() {
        return System.err;
    }

    public void info(String message) {
        outStream().print("[INFO] : " + message + LINE_SEPARATOR);
    }

    public void err(String message) {
        errStream().print("[ERR ] : " + message + LINE_SEPARATOR);
    }
}
