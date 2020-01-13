/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class StandardTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private static final PrintStream ORIGINAL_OUT = System.out;
    private static final PrintStream ORIGINAL_ERR = System.err;
    public static final String INFO_WAITING_CORRECT_RESPONSE_FROM = "[INFO] : Waiting correct response from '";
    public static final String INFO_SUCCEEDED_AFTER_1_ATTEMPT_S = "[INFO] : Succeeded after 1 attempt(s)";
    public static final String INFO_ATTEMPT_1_1 = "[INFO] : Attempt 1/1";
    public static final String INFO = "[INFO] : -----------------------------------------";

    @Rule
    public final ExpectedException expectedExceptionRule = ExpectedException.none();

    public ByteArrayOutputStream getOutContent() {
        return outContent;
    }

    public ByteArrayOutputStream getErrContent() {
        return errContent;
    }

    @Before
    public void before() {
        System.setOut(new PrintStream(outContent, false, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(errContent, false, StandardCharsets.UTF_8));
    }

    @After
    public void after() {
        System.setOut(ORIGINAL_OUT);
        System.setErr(ORIGINAL_ERR);
    }
}
