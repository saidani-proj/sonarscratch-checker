/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.http;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import sonarscratch.checker.log.SystemLogger;

public class ClientTest {
    private final ByteArrayOutputStream OUT_CONTENT = new ByteArrayOutputStream();
    private final ByteArrayOutputStream ERR_CONTENT = new ByteArrayOutputStream();
    private final PrintStream ORIGINAL_OUT = System.out;
    private final PrintStream ORIGINAL_ERR = System.err;
    private final static String URL = "http://localhost:9580";

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
    public void test_Constructor_IncorrectCount() throws ClientException {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("'attemptsCount' argument cannot be zero nor negative (-2)");

        new Client(-2, 0, null);
    }

    @Test
    public void test_Constructor_IncorrectSleep() throws ClientException {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("'attemptSleepMilliSeconds' argument cannot be negative (-500)");

        new Client(1, -500, null);
    }

    @Test
    public void test_On_Url_Incorrect() throws ClientException {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("Exception in class sonarscratch.checker.http.Client");
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(MalformedURLException.class));

        new Client(1, 1000, new SystemLogger()).on("Incorrect url", null);
    }

    @Test
    public void test_On_Url_Correct_NotResponding() throws ClientException {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("Enable to get correct response after 1 attempt(s)");
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(UnknownHostException.class));

        new Client(1, 1000, new SystemLogger()).on("http://unfound_url", null);
    }

    @Test
    public void test_On_Url_Correct_Responding_IncorrectCode() throws IOException, ClientException {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("Enable to get correct response after 2 attempt(s)");

        mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE + 1, new Client(2, 0, new SystemLogger())).on(URL, null);
    }

    @Test
    public void test_On_Url_Correct_Responding_CorrectCode_Default_WithoutCallback()
            throws ClientException, IOException {
        mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL, null);
        assertEquals(
                "[INFO] : Waiting correct response from '" + URL + "'" + System.lineSeparator() + "[INFO] : Attempt 1/1"
                        + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                OUT_CONTENT.toString());
    }

    @Test
    public void test_On_Url_Correct_Responding_CorrectCode_Default_WithCallback_Exception_InnerException()
            throws ClientException, IOException {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("Enable to get correct response after 1 attempt(s)");
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(ActionException.class));

        mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL, new Action() {
            @Override
            public void run(InputStream stream) throws ActionException {
                throw new ActionException("Run with exception", new Exception("Action inner exception"));
            }
        });
    }

    @Test
    public void test_On_Url_Correct_Responding_CorrectCode_Default_WithCallback_Exception_()
            throws ClientException, IOException {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("Enable to get correct response after 1 attempt(s)");
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(ActionException.class));

        mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL, new Action() {
            @Override
            public void run(InputStream stream) throws ActionException {
                throw new ActionException("Run with exception");
            }
        });
    }

    @Test
    public void test_On_Url_Correct_Responding_CorrectCode_Default_WithCallback_() throws ClientException, IOException {
        final String runExecutedMessage = "Run executed";

        mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL, new Action() {
            @Override
            public void run(InputStream stream) throws ActionException {
                System.out.println(runExecutedMessage);
            }
        });

        assertEquals(
                "[INFO] : Waiting correct response from '" + URL + "'" + System.lineSeparator() + "[INFO] : Attempt 1/1"
                        + System.lineSeparator() + runExecutedMessage + System.lineSeparator()
                        + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                OUT_CONTENT.toString());
    }

    @Test
    public void test_On_Url_Correct_Responding_CorrectCode_() throws ClientException, IOException {
        mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE + 1, new Client(1, 0, new SystemLogger())).on(URL,
                Client.DEFAULT_CORRECT_RESPONSE_CODE + 1, false, null);
        assertEquals(
                "[INFO] : Waiting correct response from '" + URL + "'" + System.lineSeparator() + "[INFO] : Attempt 1/1"
                        + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                OUT_CONTENT.toString());
    }

    @Test
    public void test_On_CountGreaterThan1() throws ClientException, IOException {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("Enable to get correct response after 2 attempt(s)");

        mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE + 1, new Client(2, 0, new SystemLogger())).on(URL, null);
    }

    @Test
    public void test_On_AdminAuthorization() throws ClientException, IOException {
        mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL, true, null);
        assertEquals(
                "[INFO] : Waiting correct response from '" + URL + "'" + System.lineSeparator() + "[INFO] : Attempt 1/1"
                        + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                OUT_CONTENT.toString());
    }

    @Test
    public void test_On_Log_Error() throws ClientException, IOException {
        try {
            mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE + 1, new Client(1, 0, new SystemLogger())).on(URL, null);
        } catch (ClientException exception) {
        }

        assertEquals(
                "[ERR ] : Failed after 1 attempt(s)" + System.lineSeparator()
                        + "[ERR ] : -----------------------------------------" + System.lineSeparator(),
                ERR_CONTENT.toString());
    }

    @Test
    public void test_On_Log_Info() throws ClientException, IOException {
        mockClient(Client.DEFAULT_CORRECT_RESPONSE_CODE, new Client(1, 0, new SystemLogger())).on(URL, null);

        assertEquals(
                "[INFO] : Waiting correct response from '" + URL + "'" + System.lineSeparator() + "[INFO] : Attempt 1/1"
                        + System.lineSeparator() + "[INFO] : Succeeded after 1 attempt(s)" + System.lineSeparator()
                        + "[INFO] : -----------------------------------------" + System.lineSeparator(),
                OUT_CONTENT.toString());
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
