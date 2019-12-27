/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import sonarscratch.checker.config.Project;
import sonarscratch.checker.config.Reader;
import sonarscratch.checker.http.Client;
import sonarscratch.checker.http.ClientException;
import sonarscratch.checker.issues.ComponentsCollection;
import sonarscratch.checker.issues.Finder;
import sonarscratch.checker.issues.IssuesCollection;
import sonarscratch.checker.log.SystemLogger;
import sonarscratch.checker.report.Html;

class AppExecuteDependency {
    Reader reader(String[] args) {
        return new Reader(args);
    }

    SystemLogger systemLogger() {
        return new SystemLogger();
    }

    Client client(int attemptsCount, int attemptSleepMilliseconds, SystemLogger logger) throws ClientException {
        return new Client(attemptsCount, attemptSleepMilliseconds, logger);
    }

    Finder finder(Client client, SystemLogger systemLogger) {
        return new Finder(client, systemLogger);
    }

    BufferedWriter bufferedWriter(String reportPath) throws IOException {
        return new BufferedWriter(new FileWriter(reportPath/*, StandardCharsets.UTF_8*/));
    }

    Html html(IssuesCollection issuesCollection, ComponentsCollection componentsCollection, Iterable<Project> projects,
            Charset encoding, BufferedWriter bufferedWriter, SystemLogger logger) {
        return new Html(issuesCollection, componentsCollection, projects, encoding, bufferedWriter, logger);
    }
}
