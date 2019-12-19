/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import sonarscratch.checker.config.Config;
import sonarscratch.checker.config.ReaderException;
import sonarscratch.checker.http.Client;
import sonarscratch.checker.http.ClientException;
import sonarscratch.checker.http.ClientTestUtil;
import sonarscratch.checker.issues.ComponentsCollection;
import sonarscratch.checker.issues.Finder;
import sonarscratch.checker.issues.FinderException;
import sonarscratch.checker.issues.FinderResult;
import sonarscratch.checker.issues.IssuesCollection;
import sonarscratch.checker.log.SystemLogger;

public class AppTest {
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
    public void test_Execute_Help_Long() throws AppException, ReaderException {
        appExecute(new String[] { "--help" });
        assertEquals("Usage : sn-scratch-ch [OPTIONS]", OUT_CONTENT.toString()
                .substring(System.lineSeparator().length(), System.lineSeparator().length() + 31));
    }

    @Test
    public void test_Execute_Help_Short() throws AppException, ReaderException {
        appExecute(new String[] { "-h" });
        assertEquals("Usage : sn-scratch-ch [OPTIONS]", OUT_CONTENT.toString()
                .substring(System.lineSeparator().length(), System.lineSeparator().length() + 31));
    }

    @Test
    public void test_Execute_Version_Long() throws AppException, ReaderException {
        appExecute(new String[] { "--version" });
        assertEquals("1.0" + System.lineSeparator(), OUT_CONTENT.toString());
    }

    @Test
    public void test_Execute_Version_Short() throws AppException, ReaderException {
        appExecute(new String[] { "-v" });
        assertEquals("1.0" + System.lineSeparator(), OUT_CONTENT.toString());
    }

    @Test
    public void test_Execute_Check()
            throws AppException, ReaderException, FinderException, ClientException, IOException {
        var mockedApp = Mockito.spy(new App());
        var mockedAppExecuteDependency = Mockito.spy(new AppExecuteDependency());

        doReturn(mockedAppExecuteDependency).when(mockedApp).getExecuteDependency();

        var logger = new SystemLogger();
        var mockedClient = ClientTestUtil.MockClientByApp(new Client(2, 0, logger));

        doReturn(logger).when(mockedAppExecuteDependency).systemLogger();
        doReturn(mockedClient).when(mockedAppExecuteDependency).client(Mockito.anyInt(), Mockito.anyInt(),
                Mockito.any());

        assertEquals(0, mockedApp.execute(new String[] { "-c" }, mockedAppExecuteDependency,
                mockedAppExecuteDependency.systemLogger()));
        assertEquals(
                "[INFO] : Waiting analysis" + System.lineSeparator() + "[INFO] : Waiting analysis : attempt 1/2"
                        + System.lineSeparator() + "[INFO] : Waiting correct response from '" + Config.DEFAULT_SONAR_URL
                        + "/api/ce/activity_status'" + System.lineSeparator() + "[INFO] : Attempt 1/2"
                        + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator()
                        + "[INFO] : Waiting done after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                OUT_CONTENT.toString());
    }

    @Test
    public void test_Execute_GetCount_0() throws AppException, ReaderException, FinderException {
        var mockedApp = Mockito.spy(new App());
        var mockedAppExecuteDependency = Mockito.spy(new AppExecuteDependency());

        doReturn(mockedAppExecuteDependency).when(mockedApp).getExecuteDependency();

        var mockedFinder = Mockito.mock(Finder.class);

        doReturn(mockedFinder).when(mockedAppExecuteDependency).finder(Mockito.any(), Mockito.any());
        doReturn(0).when(mockedFinder).count(Mockito.anyString());

        assertEquals(0, mockedApp.execute(new String[0], mockedAppExecuteDependency,
                mockedAppExecuteDependency.systemLogger()));
    }

    @Test
    public void test_Execute_GetCount_() throws AppException, ReaderException, FinderException {
        var mockedApp = Mockito.spy(new App());
        var mockedAppExecuteDependency = Mockito.spy(new AppExecuteDependency());

        doReturn(mockedAppExecuteDependency).when(mockedApp).getExecuteDependency();

        var mockedFinder = Mockito.mock(Finder.class);

        doReturn(mockedFinder).when(mockedAppExecuteDependency).finder(Mockito.any(), Mockito.any());
        doReturn(2).when(mockedFinder).count(Mockito.anyString());

        assertEquals(1, mockedApp.execute(new String[0], mockedAppExecuteDependency,
                mockedAppExecuteDependency.systemLogger()));
    }

    @Test
    public void test_Execute_Report()
            throws AppException, ReaderException, FinderException, JSONException, IOException {
        var mockedApp = Mockito.spy(new App());
        var mockedAppExecuteDependency = Mockito.spy(new AppExecuteDependency());

        doReturn(mockedAppExecuteDependency).when(mockedApp).getExecuteDependency();

        var mockedFinder = Mockito.mock(Finder.class);

        doReturn(mockedFinder).when(mockedAppExecuteDependency).finder(Mockito.any(), Mockito.any());
        doReturn(2).when(mockedFinder).count(Mockito.anyString());

        var stringWriter = new StringWriter();
        var bufferedWriter = new BufferedWriter(stringWriter);

        doReturn(bufferedWriter).when(mockedAppExecuteDependency).bufferedWriter(Mockito.anyString());

        var issues = new IssuesCollection(5);
        var components = new ComponentsCollection();

        issues.addIssue(new JSONObject("{message : \"This is an test issue (1)\", component:\"test-component\"}"));
        issues.addIssue(new JSONObject("{message : \"This is an test issue (2)\", component:\"test-component\"}"));
        components.addComponent(new JSONObject("{key:\"test-component\"}"));

        var mockedFinderResult = Mockito.mock(FinderResult.class);

        doReturn(issues).when(mockedFinderResult).getIssuesCollection();
        doReturn(components).when(mockedFinderResult).getComponentsCollection();

        doReturn(mockedFinderResult).when(mockedFinder).find(Mockito.anyString());

        assertEquals(1, mockedApp.execute(new String[] { "-r" }, mockedAppExecuteDependency,
                mockedAppExecuteDependency.systemLogger()));
        assertEquals(getResourceContent("execute-report.html"), stringWriter.toString());
    }

    @Test
    public void test_Execute_Exception() throws AppException, ReaderException, FinderException {
        expectedException.expect(AppException.class);
        expectedException.expectMessage("Exception in class sonarscratch.checker.app.App");
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(FinderException.class));

        var mockedApp = Mockito.spy(new App());
        var mockedAppExecuteDependency = Mockito.spy(new AppExecuteDependency());

        doReturn(mockedAppExecuteDependency).when(mockedApp).getExecuteDependency();
        var mockedFinder = Mockito.mock(Finder.class);

        doReturn(mockedFinder).when(mockedAppExecuteDependency).finder(Mockito.any(), Mockito.any());
        doReturn(2).when(mockedFinder).count(Mockito.anyString());
        doThrow(new FinderException("AppException test")).when(mockedFinder).find(Mockito.anyString());

        mockedApp.execute(new String[] { "-r" }, mockedAppExecuteDependency, mockedAppExecuteDependency.systemLogger());
    }

    @Test
    public void test_Execute_ExitCode_Success() throws AppException {
        var mockedApp = Mockito.spy(new App());

        doReturn(0).when(mockedApp).execute(Mockito.any(), Mockito.any(), Mockito.any());
        assertEquals(0, mockedApp.execute(new String[0]));
    }

    @Test
    public void test_Execute_ExitCode_Failure_OnException() throws AppException {
        var mockedApp = Mockito.spy(new App());
        var mockedException = Mockito.spy(new AppException("Failed", null));

        doNothing().when(mockedException).printStackTrace(Mockito.any(PrintStream.class));
        doThrow(mockedException).when(mockedApp).execute(Mockito.any(), Mockito.any(), Mockito.any());
        assertEquals(1, mockedApp.execute(new String[0]));

        var err = ERR_CONTENT.toString();

        assertThat(err,
                CoreMatchers.startsWith("[ERR ] : Failed because exception : sonarscratch.checker.app.AppException"));
        assertThat(err, CoreMatchers.endsWith(": Failed" + System.lineSeparator()
                + "[ERR ] : Failed : exiting with code 1" + System.lineSeparator()));
    }

    @Test
    public void test_Execute_ExitCode_Failure_() throws AppException {
        var mockedApp = Mockito.spy(new App());

        final int exitCode = 2;

        doReturn(exitCode).when(mockedApp).execute(Mockito.any(), Mockito.any(), Mockito.any());
        assertEquals(exitCode, mockedApp.execute(new String[0]));

        assertEquals("[ERR ] : Failed : exiting with code " + exitCode + System.lineSeparator(),
                ERR_CONTENT.toString());
    }

    private String getResourceContent(String resourceName) throws IOException {
        return Files.readString(Paths.get(
                getClass().getClassLoader().getResource("sonarscratch/checker/app/app/" + resourceName).getFile()));
    }

    private static void appExecute(String[] args) throws AppException {
        var app = new App();
        var executeDependency = app.getExecuteDependency();

        app.execute(args, executeDependency, executeDependency.systemLogger());
    }
}
