/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg community. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import sonarscratch.checker.log.SystemLogger;
import sonarscratch.checker.util.ExceptionUtil;

public class Client {
    private static final String ATTEMPTS = "attempt(s)";
    public static final int DEFAULT_CORRECT_RESPONSE_CODE = 200;
    private int attemptsCount;
    private int attemptSleepMilliseconds;
    private SystemLogger logger;

    public Client(int attemptsCount, int attemptSleepMilliseconds, SystemLogger logger) throws ClientException {
        if (attemptsCount <= 0) {
            throw new ClientException("'attemptsCount' argument cannot be zero nor negative (" + attemptsCount + ")");
        }

        this.attemptsCount = attemptsCount;

        if (attemptSleepMilliseconds < 0) {
            throw new ClientException(
                    "'attemptSleepMilliSeconds' argument cannot be negative (" + attemptSleepMilliseconds + ")");
        }

        this.attemptSleepMilliseconds = attemptSleepMilliseconds;
        this.logger = logger;
    }

    public int getAttemptsCount() {
        return attemptsCount;
    }

    public int getAttemptSleepMilliseconds() {
        return attemptSleepMilliseconds;
    }

    public void on(String url, int correctResponseCode, boolean asAdmin, Action action) throws ClientException {
        var dependency = getClientOnDependency();

        logger.info("Waiting correct response from '" + url + "'");

        var hasCorrectResponse = false;
        Exception lastException = null;
        int responseCode = -1;

        for (var i = 0; i < attemptsCount && !hasCorrectResponse; i++) {
            logger.info("Attempt " + (i + 1) + "/" + attemptsCount);

            try {
                dependency.connection(url, asAdmin);
            } catch (IOException exception) {
                throw new ClientException(ExceptionUtil.getDefaultMessage(ClientException.class), exception);
            }

            try {
                responseCode = dependency.connectionResponseCode();

                if (responseCode == correctResponseCode) {
                    runAction(action, dependency.connectionInputStream());
                    hasCorrectResponse = true;
                    logger.info("Succeeded after " + (i + 1) + " " + ATTEMPTS);
                    logger.info(SystemLogger.BLOCK_END);
                }
            } catch (IOException | ActionException exception) {
                lastException = exception;
            } finally {
                dependency.connectionDisconnect();
            }

            retry(hasCorrectResponse);
        }

        checkFailure(hasCorrectResponse, responseCode, lastException);
    }

    private static void runAction(Action action, InputStream stream) throws ActionException {
        if (action != null) {
            action.run(stream);
        }
    }

    public void on(String url, boolean asAdmin, Action action) throws ClientException {
        on(url, DEFAULT_CORRECT_RESPONSE_CODE, asAdmin, action);
    }

    public void on(String url, Action action) throws ClientException {
        on(url, DEFAULT_CORRECT_RESPONSE_CODE, false, action);
    }

    ClientOnDependency getClientOnDependency() {
        return new ClientOnDependency();
    }

    static HttpURLConnection createConnection(String url, boolean asAdmin) throws IOException {
        var connection = (HttpURLConnection) new URL(url).openConnection();

        if (asAdmin) {
            connection.setRequestProperty("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes(StandardCharsets.UTF_8)));
        }

        return connection;
    }

    private void retry(boolean hasCorrectResponse) {
        try {
            if (!hasCorrectResponse && attemptSleepMilliseconds > 0) {
                Thread.sleep(attemptSleepMilliseconds);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private void checkFailure(boolean hasCorrectResponse, int responseCode, Exception lastException)
            throws ClientException {
        if (!hasCorrectResponse) {
            logger.err("Failed after " + attemptsCount + " " + ATTEMPTS);
            logger.err(SystemLogger.BLOCK_END);
            throw new ClientException(
                    "Enable to get correct response after " + attemptsCount + " " + ATTEMPTS
                            + (responseCode != -1 ? (" (Last response code was " + responseCode + ")") : ""),
                    lastException);
        }
    }
}
