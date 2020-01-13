/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.log;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import sonarscratch.checker.test.StandardTest;

public class SystemLoggerTest extends StandardTest {
    @Test
    public void test_Info() {
        var logger = new SystemLogger();
        final String INFO = "This is an information message";

        logger.info(INFO);
        assertEquals("[INFO] : " + INFO + System.lineSeparator(), getOutContent().toString(StandardCharsets.UTF_8));
    }

    @Test
    public void test_Err() {
        var logger = new SystemLogger();
        final String ERR = "This is an error message";

        logger.err(ERR);
        assertEquals("[ERR ] : " + ERR + System.lineSeparator(), getErrContent().toString(StandardCharsets.UTF_8));
    }
}
