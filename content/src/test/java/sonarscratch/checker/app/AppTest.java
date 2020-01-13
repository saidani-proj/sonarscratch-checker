/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import sonarscratch.checker.config.Config;
import sonarscratch.checker.http.Client;
import sonarscratch.checker.http.ClientException;
import sonarscratch.checker.http.ClientTestUtil;
import sonarscratch.checker.issues.ComponentsCollection;
import sonarscratch.checker.issues.Finder;
import sonarscratch.checker.issues.FinderException;
import sonarscratch.checker.issues.FinderResult;
import sonarscratch.checker.issues.IssuesCollection;
import sonarscratch.checker.log.SystemLogger;
import sonarscratch.checker.test.ExceptionAssert;
import sonarscratch.checker.test.StandardTest;
import sonarscratch.checker.test.TestException;
import sonarscratch.checker.util.ExceptionUtil;

public class AppTest extends StandardTest {
    private static final int STANDARD_ISSUES_COUNT = 2;
    private static final int STANDARD_ATTEMPTS = 2;
    private static final String USAGE_SN_SCRATCH_CH_OPTIONS = "Usage : sn-scratch-ch [OPTIONS]";

    @Test
    public void test_Execute_Help_Long() throws AppException {
        appExecute(new String[] { "--help" });
        assertEquals(USAGE_SN_SCRATCH_CH_OPTIONS,
                getOutContent().toString(StandardCharsets.UTF_8).substring(System.lineSeparator().length(),
                        System.lineSeparator().length() + USAGE_SN_SCRATCH_CH_OPTIONS.length()));
    }

    @Test
    public void test_Execute_Help_Short() throws AppException {
        appExecute(new String[] { "-h" });
        assertEquals(USAGE_SN_SCRATCH_CH_OPTIONS,
                getOutContent().toString(StandardCharsets.UTF_8).substring(System.lineSeparator().length(),
                        System.lineSeparator().length() + USAGE_SN_SCRATCH_CH_OPTIONS.length()));
    }

    @Test
    public void test_Execute_Version_Long() throws AppException {
        appExecute(new String[] { "--version" });
        assertEquals("1.0.0" + System.lineSeparator(), getOutContent().toString(StandardCharsets.UTF_8));
    }

    @Test
    public void test_Execute_Version_Short() throws AppException {
        appExecute(new String[] { "-v" });
        assertEquals("1.0.0" + System.lineSeparator(), getOutContent().toString(StandardCharsets.UTF_8));
    }

    @Test
    public void test_Execute_Check() throws TestException {
        try {
            var mockedApp = Mockito.spy(new App());
            var mockedAppExecuteDependency = Mockito.spy(new AppExecuteDependency());

            doReturn(mockedAppExecuteDependency).when(mockedApp).getExecuteDependency();

            var logger = new SystemLogger();
            var mockedClient = ClientTestUtil.mockClientForApp(new Client(STANDARD_ATTEMPTS, 0, logger));

            doReturn(logger).when(mockedAppExecuteDependency).systemLogger();
            doReturn(mockedClient).when(mockedAppExecuteDependency).client(Mockito.anyInt(), Mockito.anyInt(),
                    Mockito.any());

            assertEquals(0, mockedApp.execute(new String[] { "-c" }, mockedAppExecuteDependency,
                    mockedAppExecuteDependency.systemLogger()));
            assertEquals("[INFO] : Waiting analysis" + System.lineSeparator()
                    + "[INFO] : Waiting analysis : attempt 1/2" + System.lineSeparator()
                    + "[INFO] : Waiting correct response from '" + Config.DEFAULT_SONAR_URL + "/api/ce/activity_status'"
                    + System.lineSeparator() + "[INFO] : Attempt 1/2" + System.lineSeparator()
                    + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                    + "[INFO] : -----------------------------------------" + System.lineSeparator()
                    + "[INFO] : Waiting done after 1 attempt(s)" + System.lineSeparator()
                    + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                    getOutContent().toString(StandardCharsets.UTF_8));
        } catch (AppException | ClientException | IOException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(AppTest.class), exception);
        }
    }

    @Test
    public void test_Execute_GetCount_0() throws TestException {
        try {
            var mockedApp = mockApp();

            doReturn(0).when(mockedApp.finder).count(Mockito.anyString());
            assertEquals(0, mockedApp.app.execute(new String[0], mockedApp.appExecuteDependency,
                    mockedApp.appExecuteDependency.systemLogger()));
        } catch (AppException | FinderException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(AppTest.class), exception);
        }
    }

    @Test
    public void test_Execute_GetCount_() throws TestException {
        try {
            var mockedApp = mockApp();

            doReturn(STANDARD_ISSUES_COUNT).when(mockedApp.finder).count(Mockito.anyString());
            assertEquals(1, mockedApp.app.execute(new String[0], mockedApp.appExecuteDependency,
                    mockedApp.appExecuteDependency.systemLogger()));

        } catch (AppException | FinderException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(AppTest.class), exception);
        }
    }

    @Test
    public void test_Execute_Report_NoIssues() throws TestException {
        try {
            var mockedApp = mockApp();

            doReturn(0).when(mockedApp.finder).count(Mockito.anyString());

            var stringWriter = new StringWriter();
            var bufferedWriter = new BufferedWriter(stringWriter);

            doReturn(bufferedWriter).when(mockedApp.appExecuteDependency).bufferedWriter(Mockito.anyString());

            assertEquals(0, mockedApp.app.execute(new String[] { "-r" }, mockedApp.appExecuteDependency,
                    mockedApp.appExecuteDependency.systemLogger()));
            assertEquals(0, stringWriter.toString().length());
        } catch (AppException | FinderException | IOException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(AppTest.class), exception);
        }
    }

    @Test
    public void test_Execute_Report_() throws TestException {
        try {
            var mockedApp = mockApp();

            doReturn(STANDARD_ISSUES_COUNT).when(mockedApp.finder).count(Mockito.anyString());

            var stringWriter = new StringWriter();
            var bufferedWriter = new BufferedWriter(stringWriter);

            doReturn(bufferedWriter).when(mockedApp.appExecuteDependency).bufferedWriter(Mockito.anyString());

            final int TOTAL = 5;
            var issues = new IssuesCollection(TOTAL);
            var components = new ComponentsCollection();

            issues.addIssue(new JSONObject("{message : \"This is an test issue (1)\", component:\"test-component\"}"));
            issues.addIssue(new JSONObject("{message : \"This is an test issue (2)\", component:\"test-component\"}"));
            components.addComponent(new JSONObject("{key:\"test-component\"}"));

            var mockedFinderResult = Mockito.mock(FinderResult.class);

            doReturn(issues).when(mockedFinderResult).getIssuesCollection();
            doReturn(components).when(mockedFinderResult).getComponentsCollection();

            doReturn(mockedFinderResult).when(mockedApp.finder).find(Mockito.anyString());

            assertEquals(1, mockedApp.app.execute(new String[] { "-r" }, mockedApp.appExecuteDependency,
                    mockedApp.appExecuteDependency.systemLogger()));
            assertEquals(getResourceContent("execute-report.html"), stringWriter.toString());
        } catch (AppException | FinderException | JSONException | IOException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(AppTest.class), exception);
        }
    }

    @Test
    public void test_Execute_Exception() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectMessage("Exception in class sonarscratch.checker.app.AppTest");
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(AppException.class));

        try {
            var mockedApp = Mockito.spy(new App());
            var mockedAppExecuteDependency = Mockito.spy(new AppExecuteDependency());

            doReturn(mockedAppExecuteDependency).when(mockedApp).getExecuteDependency();
            var mockedFinder = Mockito.mock(Finder.class);

            doReturn(mockedFinder).when(mockedAppExecuteDependency).finder(Mockito.any(), Mockito.any());
            doReturn(STANDARD_ISSUES_COUNT).when(mockedFinder).count(Mockito.anyString());
            doThrow(new FinderException("AppException test")).when(mockedFinder).find(Mockito.anyString());

            mockedApp.execute(new String[] { "-r" }, mockedAppExecuteDependency,
                    mockedAppExecuteDependency.systemLogger());
        } catch (AppException | FinderException exception) {
            ExceptionAssert.checkMessage("Exception in class sonarscratch.checker.app.App", exception);
            ExceptionAssert.checkType(FinderException.class, exception.getCause());
            throw new TestException(ExceptionUtil.getDefaultMessage(AppTest.class), exception);
        }
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

        Mockito.doNothing().when(mockedException).printStackTrace(Mockito.any(PrintStream.class));
        doThrow(mockedException).when(mockedApp).execute(Mockito.any(), Mockito.any(), Mockito.any());
        assertEquals(1, mockedApp.execute(new String[0]));

        var err = getErrContent().toString(StandardCharsets.UTF_8);

        assertThat(err,
                CoreMatchers.startsWith("[ERR ] : Failed because exception : sonarscratch.checker.app.AppException"));
        assertThat(err, CoreMatchers.endsWith(": Failed" + System.lineSeparator()
                + "[ERR ] : Failed : exiting with code 1" + System.lineSeparator()));
    }

    @Test
    public void test_Execute_ExitCode_Failure_() throws AppException {
        var mockedApp = Mockito.spy(new App());

        final int EXIT_CODE = 2;

        doReturn(EXIT_CODE).when(mockedApp).execute(Mockito.any(), Mockito.any(), Mockito.any());
        assertEquals(EXIT_CODE, mockedApp.execute(new String[0]));

        assertEquals("[ERR ] : Failed : exiting with code " + EXIT_CODE + System.lineSeparator(),
                getErrContent().toString(StandardCharsets.UTF_8));
    }

    private static String getResourceContent(String resourceName) throws IOException {
        return Files.readString(Paths.get(Thread.currentThread().getContextClassLoader()
                .getResource("sonarscratch/checker/app/app/" + resourceName).getFile()));
    }

    private static void appExecute(String[] args) throws AppException {
        var app = new App();
        var executeDependency = app.getExecuteDependency();

        app.execute(args, executeDependency, executeDependency.systemLogger());
    }

    private static MockedApp mockApp() {
        var mockedApp = Mockito.spy(new App());
        var mockedAppExecuteDependency = Mockito.spy(new AppExecuteDependency());

        doReturn(mockedAppExecuteDependency).when(mockedApp).getExecuteDependency();

        var mockedFinder = Mockito.mock(Finder.class);

        doReturn(mockedFinder).when(mockedAppExecuteDependency).finder(Mockito.any(), Mockito.any());

        var returnedValue = new MockedApp();

        returnedValue.app = mockedApp;
        returnedValue.appExecuteDependency = mockedAppExecuteDependency;
        returnedValue.finder = mockedFinder;

        return returnedValue;
    }

    static final class MockedApp {
        private App app;
        private AppExecuteDependency appExecuteDependency;
        private Finder finder;

        private MockedApp() {
        }
    }
}
