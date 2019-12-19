/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.log;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sonarscratch.checker.log.SystemLogger;

public class SystemLoggerTest {
    private final ByteArrayOutputStream OUT_CONTENT = new ByteArrayOutputStream();
    private final ByteArrayOutputStream ERR_CONTENT = new ByteArrayOutputStream();
    private final PrintStream ORIGINAL_OUT = System.out;
    private final PrintStream ORIGINAL_ERR = System.err;

    @Before
    public void before() {
        System.setOut(new PrintStream(OUT_CONTENT));
        System.setErr(new PrintStream(ERR_CONTENT));
    }

    @After
    public void after() {
        System.setOut(ORIGINAL_OUT);
        System.setErr(ORIGINAL_ERR);
    }

    @Test
    public void test_Info() {
        var logger = new SystemLogger();
        final String info = "This is an information message";

        logger.info(info);
        assertEquals("[INFO] : " + info + System.lineSeparator(), OUT_CONTENT.toString());
    }

    @Test
    public void test_Err() {
        var logger = new SystemLogger();
        final String err = "This is an error message";

        logger.err(err);
        assertEquals("[ERR ] : " + err + System.lineSeparator(), ERR_CONTENT.toString());
    }
}
