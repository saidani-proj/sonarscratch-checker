/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.issues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.hamcrest.core.IsInstanceOf;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import sonarscratch.checker.http.Client;
import sonarscratch.checker.http.ClientException;
import sonarscratch.checker.http.ClientTestUtil;
import sonarscratch.checker.log.SystemLogger;
import sonarscratch.checker.test.ExceptionAssert;
import sonarscratch.checker.test.StandardTest;
import sonarscratch.checker.test.TestException;
import sonarscratch.checker.util.ExceptionUtil;

public class FinderTest extends StandardTest {
    private static final String INFO_WAITING_ANALYSIS_ATTEMPT_1_1 = "[INFO] : Waiting analysis : attempt 1/1";
    private static final String API_CE_ACTIVITY_STATUS = "/api/ce/activity_status'";
    private static final String INFO_WAITING_DONE_AFTER_1_ATTEMPT_S = "[INFO] : Waiting done after 1 attempt(s)";
    private static final String INFO_WAITING_ANALYSIS = "[INFO] : Waiting analysis";
    private static final String FAILING_0_PENDING_1_IN_PROGRESS_0 = "{\"failing\" : 0, \"pending\" : 1, "
            + "\"inProgress\" : 0}";
    private static final String TOTAL_KEY = "{\"total\" : ";
    private static final String EXCEPTION_IN_CLASS_FINDER = "Exception in class sonarscratch.checker.issues.Finder";
    private static final String EXCEPTION_IN_CLASS_FINDER_TEST = "Exception in "
            + "class sonarscratch.checker.issues.FinderTest";
    private static final String URL = "http://localhost:9580";

    @Test
    public void test_Count_Exception() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectMessage(EXCEPTION_IN_CLASS_FINDER_TEST);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(FinderException.class));

        try {
            var logger = new SystemLogger();

            new Finder(ClientTestUtil.mockClientForFinder(null, new Client(1, 0, logger)), logger).count(URL);
        } catch (FinderException | IOException | ClientException exception) {
            ExceptionAssert.checkType(FinderException.class, exception);
            ExceptionAssert.checkMessage(EXCEPTION_IN_CLASS_FINDER, exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Count_Log() throws TestException {
        try {
            final int TOTAL = 120;
            var logger = new SystemLogger();

            new Finder(ClientTestUtil.mockClientForFinder((TOTAL_KEY + TOTAL + "}").getBytes(StandardCharsets.UTF_8),
                    new Client(1, 0, logger)), logger).count(URL);

            assertEquals(
                    "[INFO] : Finding issues count" + System.lineSeparator() + INFO_WAITING_CORRECT_RESPONSE_FROM + URL
                            + "/api/issues/search?pageSize=1&resolved=false'" + System.lineSeparator()
                            + INFO_ATTEMPT_1_1 + System.lineSeparator() + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S
                            + System.lineSeparator() + INFO + System.lineSeparator() + "[INFO] : Issues count is "
                            + TOTAL + System.lineSeparator() + INFO + System.lineSeparator(),
                    getOutContent().toString(StandardCharsets.UTF_8));
        } catch (FinderException | IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Count_() throws TestException {
        try {
            final int TOTAL = 120;
            var logger = new SystemLogger();

            assertEquals(TOTAL,
                    new Finder(ClientTestUtil.mockClientForFinder(
                            (TOTAL_KEY + TOTAL + "}").getBytes(StandardCharsets.UTF_8), new Client(1, 0, logger)),
                            logger).count(URL));
        } catch (FinderException | IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Find_Exception() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectMessage(EXCEPTION_IN_CLASS_FINDER_TEST);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(FinderException.class));

        try {
            var logger = new SystemLogger();

            new Finder(ClientTestUtil.mockClientForFinder(null, new Client(1, 0, logger)), logger).find(URL);
        } catch (FinderException | IOException | ClientException exception) {
            ExceptionAssert.checkMessage(EXCEPTION_IN_CLASS_FINDER, exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Find__OutOfLimit() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(FinderException.class));

        try {
            var logger = new SystemLogger();

            new Finder(ClientTestUtil.mockClientForFinder(null, new Client(1, 0, logger)), logger).find(URL,
                    Finder.ISSUES_MAX_LIMIT + 1);
        } catch (FinderException | IOException | ClientException exception) {
            ExceptionAssert.checkMessage("'limit' argument cannot be greater than " + Finder.ISSUES_MAX_LIMIT,
                    exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Find__Log() throws TestException {
        try {
            var logger = new SystemLogger();

            new Finder(ClientTestUtil.mockClientForFinder(
                    ("{\"total\" : 120, \"issues\" : [{}, {}], \"components\":[]}").getBytes(StandardCharsets.UTF_8),
                    new Client(1, 0, logger)), logger).find(URL);

            assertEquals(
                    "[INFO] : Finding issues" + System.lineSeparator() + INFO_WAITING_CORRECT_RESPONSE_FROM + URL
                            + "/api/issues/search?pageSize=-1&resolved=false'" + System.lineSeparator()
                            + INFO_ATTEMPT_1_1 + System.lineSeparator() + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S
                            + System.lineSeparator() + INFO + System.lineSeparator() + "[INFO] : Found 2 issues"
                            + System.lineSeparator() + INFO + System.lineSeparator(),
                    getOutContent().toString(StandardCharsets.UTF_8));
        } catch (FinderException | IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Find__BadResponse() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectMessage(EXCEPTION_IN_CLASS_FINDER_TEST);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(FinderException.class));

        try {
            var logger = new SystemLogger();

            new Finder(ClientTestUtil.mockClientForFinder(
                    ("{\"total\" : 120, \"issues\" : }").getBytes(StandardCharsets.UTF_8), new Client(1, 0, logger)),
                    logger).find(URL);
        } catch (FinderException | IOException | ClientException exception) {
            ExceptionAssert.checkMessage(EXCEPTION_IN_CLASS_FINDER, exception);
            ExceptionAssert.checkType(JSONException.class, exception.getCause());
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Find__Issues() throws TestException {
        try {
            final int TOTAL = 120;
            final String ISSUE1_KEY = "issue1Key";
            final String ISSUE1_VALUE = "issue1Value";

            var logger = new SystemLogger();

            var finderResult = new Finder(ClientTestUtil.mockClientForFinder(
                    (TOTAL_KEY + TOTAL + ", \"issues\" : [{\"" + ISSUE1_KEY + "\" : \"" + ISSUE1_VALUE
                            + "\"}, {}], \"components\":[]}").getBytes(StandardCharsets.UTF_8),
                    new Client(1, 0, logger)), logger).find(URL);

            var issues = finderResult.getIssuesCollection();

            assertEquals(TOTAL, issues.total());

            final int ISSUES_COUNT = 2;

            assertEquals(ISSUES_COUNT, issues.count());

            var i = 0;

            for (JSONObject item : issues) {
                assertIssue(item, i, ISSUE1_KEY, ISSUE1_VALUE);
                i++;
            }
        } catch (FinderException | IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Find__Components() throws TestException {
        try {
            final String COMP1_KEY = "comp1Key";
            final String COMP1_VALUE = "comp1Value";

            var logger = new SystemLogger();

            var finderResult = new Finder(
                    ClientTestUtil.mockClientForFinder(
                            ("{\"total\" : 120, \"issues\" : [], \"components\":[{\"" + COMP1_KEY + "\" : \""
                                    + COMP1_VALUE + "\"}]}").getBytes(StandardCharsets.UTF_8),
                            new Client(1, 0, logger)),
                    logger).find(URL);

            var components = finderResult.getComponentsCollection();

            assertEquals(1, components.count());

            for (JSONObject item : components) {
                assertTrue(item.has(COMP1_KEY));
                assertEquals(item.optString(COMP1_KEY), COMP1_VALUE);
            }
        } catch (FinderException | IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Wait_Exception_Failing() throws TestException {
        final int FAILING_COUNT = 2;

        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(FinderException.class));

        try {

            var logger = new SystemLogger();
            new Finder(ClientTestUtil.mockClientForFinder(
                    ("{\"failing\" : " + FAILING_COUNT + "}").getBytes(StandardCharsets.UTF_8),
                    new Client(1, 0, logger)), logger).wait(URL);
        } catch (FinderException | IOException | ClientException exception) {
            ExceptionAssert.checkMessage(
                    "Finding " + FAILING_COUNT + " failed activities, resolve failures and restart SonarQube",
                    exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Wait_Exception_Pending() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(FinderException.class));

        try {
            var logger = new SystemLogger();
            new Finder(ClientTestUtil.mockClientForFinder(
                    FAILING_0_PENDING_1_IN_PROGRESS_0.getBytes(StandardCharsets.UTF_8), new Client(1, 0, logger)),
                    logger).wait(URL);
            assertEquals(INFO_WAITING_ANALYSIS + System.lineSeparator() + INFO_WAITING_ANALYSIS_ATTEMPT_1_1
                    + System.lineSeparator() + INFO_WAITING_CORRECT_RESPONSE_FROM + URL + API_CE_ACTIVITY_STATUS
                    + System.lineSeparator() + INFO_ATTEMPT_1_1 + System.lineSeparator()
                    + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO + System.lineSeparator()
                    + INFO_WAITING_DONE_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO + System.lineSeparator(),
                    getOutContent().toString(StandardCharsets.UTF_8));
        } catch (FinderException | IOException | ClientException exception) {
            ExceptionAssert.checkMessage("Waiting analysis failed after 1 attempt(s)", exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Wait_Exception_() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectMessage(EXCEPTION_IN_CLASS_FINDER_TEST);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(FinderException.class));

        try {
            var logger = new SystemLogger();
            new Finder(ClientTestUtil.mockClientForFinder(null, new Client(1, 0, logger)), logger).wait(URL);
        } catch (FinderException | IOException | ClientException exception) {
            ExceptionAssert.checkMessage(EXCEPTION_IN_CLASS_FINDER, exception);
            ExceptionAssert.checkType(ClientException.class, exception.getCause());
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Wait_Log_Failed_Retry() throws TestException {
        expectedExceptionRule.expect(TestException.class);

        try {
            var logger = new SystemLogger();

            final int ATTEMPTS_COUNT = 2;
            new Finder(ClientTestUtil.mockClientForFinder(
                    FAILING_0_PENDING_1_IN_PROGRESS_0.getBytes(StandardCharsets.UTF_8),
                    new Client(ATTEMPTS_COUNT, 1, logger)), logger).wait(URL);
        } catch (FinderException exception) {
            assertEquals(
                    INFO_WAITING_ANALYSIS + System.lineSeparator() + "[INFO] : Waiting analysis : attempt 1/2"
                            + System.lineSeparator() + INFO_WAITING_CORRECT_RESPONSE_FROM + URL + API_CE_ACTIVITY_STATUS
                            + System.lineSeparator() + "[INFO] : Attempt 1/2" + System.lineSeparator()
                            + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO + System.lineSeparator()
                            + "[INFO] : Pending activities : 1, in progress activities : 0" + System.lineSeparator()
                            + INFO + System.lineSeparator() + "[INFO] : Waiting analysis : attempt 2/2"
                            + System.lineSeparator() + INFO_WAITING_CORRECT_RESPONSE_FROM + URL + API_CE_ACTIVITY_STATUS
                            + System.lineSeparator() + "[INFO] : Attempt 1/2" + System.lineSeparator()
                            + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO + System.lineSeparator(),
                    getOutContent().toString(StandardCharsets.UTF_8));

            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        } catch (IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Wait_Log_Failed_() throws TestException {
        expectedExceptionRule.expect(TestException.class);

        try {
            var logger = new SystemLogger();

            new Finder(ClientTestUtil.mockClientForFinder(
                    FAILING_0_PENDING_1_IN_PROGRESS_0.getBytes(StandardCharsets.UTF_8), new Client(1, 0, logger)),
                    logger).wait(URL);
        } catch (FinderException exception) {
            assertEquals(INFO_WAITING_ANALYSIS + System.lineSeparator() + INFO_WAITING_ANALYSIS_ATTEMPT_1_1
                    + System.lineSeparator() + INFO_WAITING_CORRECT_RESPONSE_FROM + URL + API_CE_ACTIVITY_STATUS
                    + System.lineSeparator() + INFO_ATTEMPT_1_1 + System.lineSeparator()
                    + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO + System.lineSeparator()
                    + "[INFO] : Pending activities : 1, in progress activities : 0" + System.lineSeparator() + INFO
                    + System.lineSeparator(), getOutContent().toString(StandardCharsets.UTF_8));
            assertEquals(
                    "[ERR ] : Waiting analysis : failed after 1 attempt(s)" + System.lineSeparator()
                            + "[ERR ] : -----------------------------------------" + System.lineSeparator(),
                    getErrContent().toString(StandardCharsets.UTF_8));

            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        } catch (IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    @Test
    public void test_Wait_Log_Succeed() throws TestException {
        try {
            var logger = new SystemLogger();
            new Finder(ClientTestUtil.mockClientForFinder(
                    "{\"failing\" : 0, \"pending\" : 0, \"inProgress\" : 0}".getBytes(StandardCharsets.UTF_8),
                    new Client(1, 0, logger)), logger).wait(URL);
            assertEquals(INFO_WAITING_ANALYSIS + System.lineSeparator() + INFO_WAITING_ANALYSIS_ATTEMPT_1_1
                    + System.lineSeparator() + INFO_WAITING_CORRECT_RESPONSE_FROM + URL + API_CE_ACTIVITY_STATUS
                    + System.lineSeparator() + INFO_ATTEMPT_1_1 + System.lineSeparator()
                    + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO + System.lineSeparator()
                    + INFO_WAITING_DONE_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO + System.lineSeparator(),
                    getOutContent().toString(StandardCharsets.UTF_8));
        } catch (FinderException | IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(FinderTest.class), exception);
        }
    }

    private static void assertIssue(JSONObject item, int index, String issue1Key, String issue1Value) {
        if (index == 0) {
            assertTrue(item.has(issue1Key));
            assertEquals(issue1Value, item.optString(issue1Key));
        } else {
            var count = 0;
            var iterator = item.keys();

            while (iterator.hasNext()) {
                iterator.next();
                count++;
            }

            assertEquals(0, count);
        }
    }
}
