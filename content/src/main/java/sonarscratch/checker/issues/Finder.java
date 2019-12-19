/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.issues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;

import sonarscratch.checker.http.ActionException;
import sonarscratch.checker.http.Client;
import sonarscratch.checker.http.ClientException;
import sonarscratch.checker.log.SystemLogger;
import sonarscratch.checker.util.ExceptionUtil;

public class Finder {
    private static final String ATTEMPTS = "attempt(s)";
    public static final int ISSUES_MAX_LIMIT = 500;
    private Client client;
    private SystemLogger logger;

    public Finder(Client client, SystemLogger logger) {
        this.client = client;
        this.logger = logger;
    }

    public void wait(String baseUrl) throws FinderException {
        logger.info("Waiting analysis");
        var done = false;

        try {
            for (var i = 0; i < client.getAttemptsCount() && !done; i++) {
                logger.info("Waiting analysis : attempt " + (i + 1) + "/" + client.getAttemptsCount());

                final var activityJsonText = new StringBuilder();

                client.on(baseUrl + "/api/ce/activity_status", true, (InputStream stream) -> {
                    try {
                        fillTextResponse(activityJsonText, stream);
                    } catch (IOException exception) {
                        throw new ActionException("Waiting analysis failed", exception);
                    }
                });

                var activityObject = new JSONObject(activityJsonText.toString());
                var failing = activityObject.getInt("failing");

                if (failing != 0) {
                    throw new FinderException(
                            "Finding " + failing + " failed activities, resolve failures and restart SonarQube");
                }

                var pending = activityObject.getInt("pending");
                var inProgress = activityObject.getInt("inProgress");

                if (pending + inProgress == 0) {
                    logger.info("Waiting done after " + (i + 1) + " " + ATTEMPTS);
                    done = true;
                } else {
                    logger.info("Pending activities : " + pending + ", in progress activities : " + inProgress);
                }

                logger.info(SystemLogger.BLOCK_END);
                retry(done);
            }
        } catch (ClientException | JSONException exception) {
            throw new FinderException(ExceptionUtil.getDefaultMessage(Finder.class), exception);
        }

        if (!done) {
            logger.err("Waiting analysis : failed after " + client.getAttemptsCount() + " " + ATTEMPTS);
            logger.err(SystemLogger.BLOCK_END);
            throw new FinderException("Waiting analysis failed after " + client.getAttemptsCount() + " " + ATTEMPTS);
        }
    }

    public int count(String baseUrl) throws FinderException {
        try {
            logger.info("Finding issues count");
            final var issuesJsonText = new StringBuilder();

            client.on(baseUrl + "/api/issues/search?pageSize=1&resolved=false", false, (InputStream stream) -> {
                try {
                    fillTextResponse(issuesJsonText, stream);
                } catch (IOException exception) {
                    throw new ActionException("Finding issues count failed", exception);
                }
            });

            var count = new JSONObject(issuesJsonText.toString()).getInt("total");
            logger.info("Issues count is " + count);
            logger.info(SystemLogger.BLOCK_END);

            return count;
        } catch (ClientException | JSONException exception) {
            throw new FinderException(ExceptionUtil.getDefaultMessage(Finder.class), exception);
        }
    }

    public FinderResult find(String baseUrl, int limit) throws FinderException {
        try {
            if (limit < 0) {
                limit = -1;
            }

            if (limit > ISSUES_MAX_LIMIT) {
                throw new FinderException("'limit' argument cannot be greater than " + ISSUES_MAX_LIMIT);
            }

            logger.info("Finding issues");
            final var issuesJsonText = new StringBuilder();

            client.on(baseUrl + "/api/issues/search?pageSize=" + limit + "&resolved=false", false,
                    (InputStream stream) -> {
                        try {
                            fillTextResponse(issuesJsonText, stream);
                        } catch (IOException exception) {
                            throw new ActionException("Finding issues failed", exception);
                        }
                    });

            var jsonObject = new JSONObject(issuesJsonText.toString());
            var issuesCollection = getIssuesCollection(jsonObject);
            logger.info("Found " + issuesCollection.count() + " issues");
            logger.info(SystemLogger.BLOCK_END);

            return new FinderResult(issuesCollection, getComponentsCollection(jsonObject));
        } catch (ClientException | JSONException exception) {
            throw new FinderException(ExceptionUtil.getDefaultMessage(Finder.class), exception);
        }
    }

    public FinderResult find(String baseUrl) throws FinderException {
        return find(baseUrl, -1);
    }

    private static IssuesCollection getIssuesCollection(JSONObject jsonObject) throws JSONException {
        var issuesCollection = new IssuesCollection(jsonObject.getInt("total"));
        var jsonObjectIssues = jsonObject.getJSONArray("issues");
        var count = jsonObjectIssues.length();

        for (var i = 0; i < count; i++) {
            issuesCollection.addIssue(jsonObjectIssues.getJSONObject(i));
        }

        return issuesCollection;
    }

    private static ComponentsCollection getComponentsCollection(JSONObject jsonObject) throws JSONException {
        var componentsCollection = new ComponentsCollection();
        var jsonObjectComponents = jsonObject.getJSONArray("components");
        var count = jsonObjectComponents.length();

        for (var i = 0; i < count; i++) {
            componentsCollection.addComponent(jsonObjectComponents.getJSONObject(i));
        }

        return componentsCollection;
    }

    private static void fillTextResponse(StringBuilder stringBuilder, InputStream stream) throws IOException {
        var bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        try {
            int c;
            while ((c = bufferedReader.read()) != -1) {
                stringBuilder.append((char) c);
            }

        } finally {
            bufferedReader.close();
        }
    }

    private void retry(boolean done) {
        try {
            if (!done && client.getAttemptSleepMilliseconds() > 0) {
                Thread.sleep(client.getAttemptSleepMilliseconds());
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
