/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.issues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.hamcrest.core.IsInstanceOf;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sonarscratch.checker.http.Client;
import sonarscratch.checker.http.ClientException;
import sonarscratch.checker.http.ClientTestUtil;
import sonarscratch.checker.log.SystemLogger;

public class FinderTest {
    private final static String URL = "http://localhost:9580";
    private final ByteArrayOutputStream OUT_CONTENT = new ByteArrayOutputStream();
    private final ByteArrayOutputStream ERR_CONTENT = new ByteArrayOutputStream();
    private final PrintStream ORIGINAL_OUT = System.out;
    private final PrintStream ORIGINAL_ERR = System.err;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

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
    public void test_Count_Exception() throws FinderException, IOException, ClientException {
        expectedException.expect(FinderException.class);
        expectedException.expectMessage("Exception in class sonarscratch.checker.issues.Finder");
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(ClientException.class));

        var logger = new SystemLogger();

        new Finder(ClientTestUtil.MockClientByFinder(null, new Client(1, 0, logger)), logger).count(URL);
    }

    @Test
    public void test_Count_Log() throws FinderException, IOException, ClientException {
        final int total = 120;
        var logger = new SystemLogger();

        new Finder(ClientTestUtil.MockClientByFinder(("{\"total\" : " + total + "}").getBytes(StandardCharsets.UTF_8),
                new Client(1, 0, logger)), logger).count(URL);

        assertEquals(
                "[INFO] : Finding issues count" + System.lineSeparator() + "[INFO] : Waiting correct response from '"
                        + URL + "/api/issues/search?pageSize=1&resolved=false'" + System.lineSeparator()
                        + "[INFO] : Attempt 1/1" + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)"
                        + System.lineSeparator() + "[INFO] : -----------------------------------------"
                        + System.lineSeparator() + "[INFO] : Issues count is " + total + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                OUT_CONTENT.toString());
    }

    @Test
    public void test_Count_() throws FinderException, IOException, ClientException {
        final int total = 120;
        var logger = new SystemLogger();

        assertEquals(total,
                new Finder(ClientTestUtil.MockClientByFinder(
                        ("{\"total\" : " + total + "}").getBytes(StandardCharsets.UTF_8), new Client(1, 0, logger)),
                        logger).count(URL));
    }

    @Test
    public void test_Find_Exception() throws FinderException, IOException, ClientException {
        expectedException.expect(FinderException.class);
        expectedException.expectMessage("Exception in class sonarscratch.checker.issues.Finder");
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(ClientException.class));

        var logger = new SystemLogger();

        new Finder(ClientTestUtil.MockClientByFinder(null, new Client(1, 0, logger)), logger).find(URL);
    }

    @Test
    public void test_Find__OutOfLimit() throws FinderException, IOException, ClientException {
        expectedException.expect(FinderException.class);
        expectedException.expectMessage("'limit' argument cannot be greater than " + Finder.ISSUES_MAX_LIMIT);

        var logger = new SystemLogger();

        new Finder(ClientTestUtil.MockClientByFinder(null, new Client(1, 0, logger)), logger).find(URL,
                Finder.ISSUES_MAX_LIMIT + 1);
    }

    @Test
    public void test_Find__Log() throws FinderException, IOException, ClientException {
        var logger = new SystemLogger();

        new Finder(ClientTestUtil.MockClientByFinder(
                ("{\"total\" : 120, \"issues\" : [{}, {}], \"components\":[]}").getBytes(StandardCharsets.UTF_8),
                new Client(1, 0, logger)), logger).find(URL);

        assertEquals(
                "[INFO] : Finding issues" + System.lineSeparator() + "[INFO] : Waiting correct response from '" + URL
                        + "/api/issues/search?pageSize=-1&resolved=false'" + System.lineSeparator()
                        + "[INFO] : Attempt 1/1" + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)"
                        + System.lineSeparator() + "[INFO] : -----------------------------------------"
                        + System.lineSeparator() + "[INFO] : Found 2 issues" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                OUT_CONTENT.toString());
    }

    @Test
    public void test_Find__BadResponse() throws FinderException, IOException, ClientException {
        expectedException.expect(FinderException.class);
        expectedException.expectMessage("Exception in class sonarscratch.checker.issues.Finder");
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(JSONException.class));

        var logger = new SystemLogger();

        new Finder(
                ClientTestUtil.MockClientByFinder(("{\"total\" : 120, \"issues\" : }").getBytes(StandardCharsets.UTF_8),
                        new Client(1, 0, logger)),
                logger).find(URL);
    }

    @Test
    public void test_Find__Issues() throws FinderException, IOException, ClientException {
        final int total = 120;
        final String issue1Key = "issue1Key";
        final String issue1Value = "issue1Value";

        var logger = new SystemLogger();

        var finderResult = new Finder(
                ClientTestUtil.MockClientByFinder(
                        ("{\"total\" : " + total + ", \"issues\" : [{\"" + issue1Key + "\" : \"" + issue1Value
                                + "\"}, {}], \"components\":[]}").getBytes(StandardCharsets.UTF_8),
                        new Client(1, 0, logger)),
                logger).find(URL);

        var issues = finderResult.getIssuesCollection();

        assertEquals(total, issues.total());
        assertEquals(2, issues.count());

        var i = 0;

        for (JSONObject item : issues) {
            if (i == 0) {
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
            i++;
        }
    }

    @Test
    public void test_Find__Components() throws FinderException, IOException, ClientException {
        final String comp1Key = "comp1Key";
        final String comp1Value = "comp1Value";

        var logger = new SystemLogger();

        var finderResult = new Finder(ClientTestUtil.MockClientByFinder(
                ("{\"total\" : 120, \"issues\" : [], \"components\":[{\"" + comp1Key + "\" : \"" + comp1Value + "\"}]}")
                        .getBytes(StandardCharsets.UTF_8),
                new Client(1, 0, logger)), logger).find(URL);

        var components = finderResult.getComponentsCollection();

        assertEquals(1, components.count());

        for (JSONObject item : components) {
            assertTrue(item.has(comp1Key));
            assertEquals(item.optString(comp1Key), comp1Value);
        }
    }

    @Test
    public void test_Wait_Exception_Failing() throws FinderException, IOException, ClientException {
        final int failingCount = 2;

        expectedException.expect(FinderException.class);
        expectedException.expectMessage(
                "Finding " + failingCount + " failed activities, resolve failures and restart SonarQube");

        var logger = new SystemLogger();
        new Finder(ClientTestUtil.MockClientByFinder(
                ("{\"failing\" : " + failingCount + "}").getBytes(StandardCharsets.UTF_8), new Client(1, 0, logger)),
                logger).wait(URL);
    }

    @Test
    public void test_Wait_Exception_Pending() throws FinderException, IOException, ClientException {
        expectedException.expect(FinderException.class);
        expectedException.expectMessage("Waiting analysis failed after 1 attempt(s)");

        var logger = new SystemLogger();
        new Finder(ClientTestUtil.MockClientByFinder(
                "{\"failing\" : 0, \"pending\" : 1, \"inProgress\" : 0}".getBytes(StandardCharsets.UTF_8),
                new Client(1, 0, logger)), logger).wait(URL);
        assertEquals(
                "[INFO] : Waiting analysis" + System.lineSeparator() + "[INFO] : Waiting analysis : attempt 1/1"
                        + System.lineSeparator() + "[INFO] : Waiting correct response from '" + URL
                        + "/api/ce/activity_status'" + System.lineSeparator() + "[INFO] : Attempt 1/1"
                        + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator()
                        + "[INFO] : Waiting done after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                OUT_CONTENT.toString());
    }

    @Test
    public void test_Wait_Exception_() throws FinderException, IOException, ClientException {
        expectedException.expect(FinderException.class);
        expectedException.expectMessage("Exception in class sonarscratch.checker.issues.Finder");
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(ClientException.class));

        var logger = new SystemLogger();
        new Finder(ClientTestUtil.MockClientByFinder(null, new Client(1, 0, logger)), logger).wait(URL);
    }

    @Test
    public void test_Wait_Log_Failed_Retry() throws IOException, ClientException {
        var logger = new SystemLogger();

        try {
            new Finder(ClientTestUtil.MockClientByFinder(
                    "{\"failing\" : 0, \"pending\" : 1, \"inProgress\" : 0}".getBytes(StandardCharsets.UTF_8),
                    new Client(2, 1, logger)), logger).wait(URL);
        } catch (FinderException exception) {
            assertEquals(
                    "[INFO] : Waiting analysis" + System.lineSeparator() + "[INFO] : Waiting analysis : attempt 1/2"
                            + System.lineSeparator() + "[INFO] : Waiting correct response from '" + URL
                            + "/api/ce/activity_status'" + System.lineSeparator() + "[INFO] : Attempt 1/2"
                            + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                            + "[INFO] : -----------------------------------------" + System.lineSeparator()
                            + "[INFO] : Pending activities : 1, in progress activities : 0" + System.lineSeparator()
                            + "[INFO] : -----------------------------------------" + System.lineSeparator()
                            + "[INFO] : Waiting analysis : attempt 2/2" + System.lineSeparator()
                            + "[INFO] : Waiting correct response from '" + URL + "/api/ce/activity_status'"
                            + System.lineSeparator() + "[INFO] : Attempt 1/2" + System.lineSeparator()
                            + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                            + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                    OUT_CONTENT.toString());
        }
    }

    @Test
    public void test_Wait_Log_Failed_() throws IOException, ClientException {
        var logger = new SystemLogger();

        try {
            new Finder(ClientTestUtil.MockClientByFinder(
                    "{\"failing\" : 0, \"pending\" : 1, \"inProgress\" : 0}".getBytes(StandardCharsets.UTF_8),
                    new Client(1, 0, logger)), logger).wait(URL);
        } catch (FinderException exception) {
            assertEquals(
                    "[INFO] : Waiting analysis" + System.lineSeparator() + "[INFO] : Waiting analysis : attempt 1/1"
                            + System.lineSeparator() + "[INFO] : Waiting correct response from '" + URL
                            + "/api/ce/activity_status'" + System.lineSeparator() + "[INFO] : Attempt 1/1"
                            + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                            + "[INFO] : -----------------------------------------" + System.lineSeparator()
                            + "[INFO] : Pending activities : 1, in progress activities : 0" + System.lineSeparator()
                            + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                    OUT_CONTENT.toString());
            assertEquals(
                    "[ERR ] : Waiting analysis : failed after 1 attempt(s)" + System.lineSeparator()
                            + "[ERR ] : -----------------------------------------" + System.lineSeparator(),
                    ERR_CONTENT.toString());
        }
    }

    @Test
    public void test_Wait_Log_Succeed() throws FinderException, IOException, ClientException {
        var logger = new SystemLogger();
        new Finder(ClientTestUtil.MockClientByFinder(
                "{\"failing\" : 0, \"pending\" : 0, \"inProgress\" : 0}".getBytes(StandardCharsets.UTF_8),
                new Client(1, 0, logger)), logger).wait(URL);
        assertEquals(
                "[INFO] : Waiting analysis" + System.lineSeparator() + "[INFO] : Waiting analysis : attempt 1/1"
                        + System.lineSeparator() + "[INFO] : Waiting correct response from '" + URL
                        + "/api/ce/activity_status'" + System.lineSeparator() + "[INFO] : Attempt 1/1"
                        + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator()
                        + "[INFO] : Waiting done after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                OUT_CONTENT.toString());
    }
}
