/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.http;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;
import org.mockito.Mockito;

import sonarscratch.checker.log.SystemLogger;
import sonarscratch.checker.test.ExceptionAssert;
import sonarscratch.checker.test.StandardTest;
import sonarscratch.checker.test.TestException;
import sonarscratch.checker.util.ExceptionUtil;

public class ClientTest extends StandardTest {
    private static final String URL = "http://localhost:9580";

    @Test
    public void test_Constructor_IncorrectCount() throws ClientException {
        expectedExceptionRule.expect(ClientException.class);
        expectedExceptionRule.expectMessage("'attemptsCount' argument cannot be zero nor negative (-2)");

        final int ATTEMPTS_COUNT = -2;
        new Client(ATTEMPTS_COUNT, 0, null);
    }

    @Test
    public void test_Constructor_IncorrectSleep() throws ClientException {
        expectedExceptionRule.expect(ClientException.class);
        expectedExceptionRule.expectMessage("'attemptSleepMilliSeconds' argument cannot be negative (-500)");

        final int ATTEMPT_SLEEP_MILLISECONDS = -500;
        new Client(1, ATTEMPT_SLEEP_MILLISECONDS, null);
    }

    @Test
    public void test_On_Url_Incorrect() throws ClientException {
        expectedExceptionRule.expect(ClientException.class);
        expectedExceptionRule.expectMessage("Exception in class sonarscratch.checker.http.Client");
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(MalformedURLException.class));

        final int ATTEMPT_SLEEP_MILLISECONDS = 1000;
        new Client(1, ATTEMPT_SLEEP_MILLISECONDS, new SystemLogger()).on("Incorrect url", null);
    }

    @Test
    public void test_On_Url_Correct_NotResponding() throws ClientException {
        expectedExceptionRule.expect(ClientException.class);
        expectedExceptionRule.expectMessage("Enable to get correct response after 1 attempt(s)");
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(UnknownHostException.class));

        final int ATTEMPT_SLEEP_MILLISECONDS = 1000;
        new Client(1, ATTEMPT_SLEEP_MILLISECONDS, new SystemLogger()).on("http://unfound_url", null);
    }

    @Test
    public void test_On_Url_Correct_Responding_IncorrectCode() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(ClientException.class));

        try {
            final int ATTEMPTS_COUNT = 2;
            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE + 1, new Client(ATTEMPTS_COUNT, 0, new SystemLogger()))
                    .on(URL, null);
        } catch (IOException | ClientException exception) {
            ExceptionAssert.checkMessage(
                    "Enable to get correct response after 2 attempt(s) (Last response code was 201)", exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(ClientTest.class), exception);
        }
    }

    @Test
    public void test_On_Url_Correct_Responding_CorrectCode_Default_WithoutCallback() throws TestException {
        try {
            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL, null);
            assertEquals(INFO_WAITING_CORRECT_RESPONSE_FROM + URL + "'" + System.lineSeparator() + INFO_ATTEMPT_1_1
                    + System.lineSeparator() + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO
                    + System.lineSeparator(), getOutContent().toString(StandardCharsets.UTF_8));
        } catch (IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(ClientTest.class), exception);
        }
    }

    @Test
    public void test_On_Url_Correct_Responding_CorrectCode_Default_WithCallback_Exception_InnerException()
            throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(ClientException.class));

        try {
            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL,
                    new Action() {
                        @Override
                        public void run(InputStream stream) throws ActionException {
                            throw new ActionException("Run with exception", new Exception("Action inner exception"));
                        }
                    });
        } catch (IOException | ClientException exception) {
            ExceptionAssert.checkMessage(
                    "Enable to get correct response after 1 attempt(s) (Last response code was 200)", exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(ClientTest.class), exception);
        }
    }

    @Test
    public void test_On_Url_Correct_Responding_CorrectCode_Default_WithCallback_Exception_() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(ClientException.class));

        try {
            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL,
                    new Action() {
                        @Override
                        public void run(InputStream stream) throws ActionException {
                            throw new ActionException("Run with exception");
                        }
                    });
        } catch (IOException | ClientException exception) {
            ExceptionAssert.checkType(ClientException.class, exception);
            ExceptionAssert.checkMessage(
                    "Enable to get correct response after 1 attempt(s) (Last response code was 200)", exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(ClientTest.class), exception);
        }
    }

    @Test
    public void test_On_Url_Correct_Responding_CorrectCode_Default_WithCallback_() throws TestException {
        try {
            final String RUN_EXECUTED_MESSAGE = "Run executed";

            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL,
                    new Action() {
                        @Override
                        public void run(InputStream stream) throws ActionException {
                            System.out.println(RUN_EXECUTED_MESSAGE);
                        }
                    });

            assertEquals(
                    INFO_WAITING_CORRECT_RESPONSE_FROM + URL + "'" + System.lineSeparator() + INFO_ATTEMPT_1_1
                            + System.lineSeparator() + RUN_EXECUTED_MESSAGE + System.lineSeparator()
                            + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO + System.lineSeparator(),
                    getOutContent().toString(StandardCharsets.UTF_8));
        } catch (IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(ClientTest.class), exception);
        }
    }

    @Test
    public void test_On_Url_Correct_Responding_CorrectCode_() throws TestException {
        try {
            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE + 1, new Client(1, 0, new SystemLogger())).on(URL,
                    Client.DEFAULT_CORRECT_RESPONSE_CODE + 1, false, null);
            assertEquals(INFO_WAITING_CORRECT_RESPONSE_FROM + URL + "'" + System.lineSeparator() + INFO_ATTEMPT_1_1
                    + System.lineSeparator() + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO
                    + System.lineSeparator(), getOutContent().toString(StandardCharsets.UTF_8));
        } catch (IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(ClientTest.class), exception);
        }
    }

    @Test
    public void test_On_CountGreaterThan1() throws TestException {
        expectedExceptionRule.expect(TestException.class);
        expectedExceptionRule.expectCause(IsInstanceOf.instanceOf(ClientException.class));

        try {
            final int ATTEMPTS_COUNT = 3;
            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE + 1, new Client(ATTEMPTS_COUNT, 0, new SystemLogger()))
                    .on(URL, null);
        } catch (IOException | ClientException exception) {
            ExceptionAssert.checkMessage(
                    "Enable to get correct response after 3 attempt(s) (Last response code was 201)", exception);
            throw new TestException(ExceptionUtil.getDefaultMessage(ClientTest.class), exception);
        }
    }

    @Test
    public void test_On_AdminAuthorization() throws TestException {
        try {
            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL, true, null);
            assertEquals(INFO_WAITING_CORRECT_RESPONSE_FROM + URL + "'" + System.lineSeparator() + INFO_ATTEMPT_1_1
                    + System.lineSeparator() + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO
                    + System.lineSeparator(), getOutContent().toString(StandardCharsets.UTF_8));
        } catch (IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(ClientTest.class), exception);
        }
    }

    @Test
    public void test_On_Log_Error() throws TestException {
        expectedExceptionRule.expect(TestException.class);

        try {
            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE + 1, new Client(1, 0, new SystemLogger())).on(URL, null);
        } catch (IOException | ClientException exception) {
            assertEquals(
                    "[ERR ] : Failed after 1 attempt(s)" + System.lineSeparator()
                            + "[ERR ] : -----------------------------------------" + System.lineSeparator(),
                    getErrContent().toString(StandardCharsets.UTF_8));
            throw new TestException(ExceptionUtil.getDefaultMessage(ClientTest.class), exception);
        }
    }

    @Test
    public void test_On_Log_Info() throws TestException {
        try {
            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL, null);

            assertEquals(INFO_WAITING_CORRECT_RESPONSE_FROM + URL + "'" + System.lineSeparator() + INFO_ATTEMPT_1_1
                    + System.lineSeparator() + INFO_SUCCEEDED_AFTER_1_ATTEMPT_S + System.lineSeparator() + INFO
                    + System.lineSeparator(), getOutContent().toString(StandardCharsets.UTF_8));
        } catch (IOException | ClientException exception) {
            throw new TestException(ExceptionUtil.getDefaultMessage(ClientTest.class), exception);
        }
    }

    private static Client mockClient(int responseCode, Client client) throws IOException {
        var mockedClientOnDependency = Mockito.mock(ClientOnDependency.class);

        Mockito.doNothing().when(mockedClientOnDependency).connection(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(responseCode).when(mockedClientOnDependency).connectionResponseCode();

        var mockedClient = Mockito.spy(client);

        Mockito.doReturn(mockedClientOnDependency).when(mockedClient).getClientOnDependency();
        return mockedClient;
    }
}
