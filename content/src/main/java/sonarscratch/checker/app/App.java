/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.app;

import java.io.IOException;
import java.nio.charset.Charset;

import sonarscratch.checker.config.Config;
import sonarscratch.checker.config.ReaderException;
import sonarscratch.checker.http.ClientException;
import sonarscratch.checker.issues.FinderException;
import sonarscratch.checker.log.SystemLogger;
import sonarscratch.checker.report.HtmlException;
import sonarscratch.checker.util.ExceptionUtil;

public class App {
    public static void main(String[] args) {
        var exitCode = new App().execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    int execute(String[] args, AppExecuteDependency executeDependency, SystemLogger systemLogger) throws AppException {

        try {
            var config = executeDependency.reader(args).read();

            if (showHelpOrVersion(config)) {
                return 0;
            }

            return executeCore(executeDependency, config, systemLogger);
        } catch (FinderException | ClientException | IOException | HtmlException | ReaderException exception) {
            throw new AppException(ExceptionUtil.getDefaultMessage(App.class), exception);
        }
    }

    AppExecuteDependency getExecuteDependency() {
        return new AppExecuteDependency();
    }

    private static void showHelp() {
        showHelpSeparator();
        showHelpItem("Usage : sn-scratch-ch [OPTIONS]");
        showHelpSeparator();
        showHelpItem("Sonar Scratch Checker, check issues in your project");
        showHelpSeparator();
        showHelpItem("Options :");
        showHelpItem("   --help      Print this help and quit");
        showHelpItem("   -h          Print this help and quit");
        showHelpItem("   --version   Print current version and quit");
        showHelpItem("   -v          Print current version and quit");
        showHelpItem("   --url       SonarQube URL, default value : http://localhost:9000");
        showHelpItem("   --check     Check if SonarQube instance responds");
        showHelpItem("   -c          Check if SonarQube instance responds");
        showHelpItem("   --count     Attemps count before showing failure, default value : 20");
        showHelpItem("   --sleep     Time in milliseconds between two attemps, default value : 5000");
        showHelpItem("   --report    Create HTML report");
        showHelpItem("   -r          Create HTML report");
        showHelpItem("   --path      HTML report path and name, default value : sonarqube-issues.html");
        showHelpItem("   --project:  Pair of key/value project, used in code preview. Can be specified multiple times");
        showHelpItem("   --encoding  HTML report previews encoding, default value : UTF-8");
        showHelpItem("   -e          HTML report previews encoding, default value : UTF-8");
    }

    private static void showHelpItem(String helpItem) {
        System.out.println(helpItem);
    }

    private static void showHelpSeparator() {
        System.out.println();
    }

    private static boolean showHelpOrVersion(Config config) {
        if (config.showHelp()) {
            showHelp();
            return true;
        }

        if (config.showVersion()) {
            System.out.println("1.0.0");
            return true;
        }

        return false;
    }

    private static int executeCore(AppExecuteDependency executeDependency, Config config, SystemLogger systemLogger)
            throws ClientException, FinderException, IOException, HtmlException {
        var client = executeDependency.client(config.getSonarAttemptsCount(), config.getSonarAttemptSleepMilliseconds(),
                systemLogger);

        var finder = executeDependency.finder(client, systemLogger);

        if (config.check()) {
            finder.wait(config.getSonarUrl());
            return 0;
        }

        finder.wait(config.getSonarUrl());
        boolean hasIssues = finder.count(config.getSonarUrl()) > 0;

        if (hasIssues && config.writeReport()) {
            var finderResult = finder.find(config.getSonarUrl());
            var bufferWriter = executeDependency.bufferedWriter(config.getReportPath());

            try {
                executeDependency
                        .html(finderResult.getIssuesCollection(), finderResult.getComponentsCollection(),
                                config.getProjects(), Charset.forName(config.getEncoding()), bufferWriter, systemLogger)
                        .write();
            } finally {
                bufferWriter.close();
            }
        }

        if (hasIssues) {
            return 1;
        }

        return 0;
    }

    int execute(String[] args) {
        SystemLogger systemLogger = null;

        try {
            var executeDependency = this.getExecuteDependency();
            systemLogger = executeDependency.systemLogger();
            var exitCode = this.execute(args, executeDependency, systemLogger);

            if (exitCode != 0) {
                systemLogger.err("Failed : exiting with code " + exitCode);
                return exitCode;
            }
        } catch (AppException exception) {
            systemLogger.err("Failed because exception : " + exception);
            exception.printStackTrace(systemLogger.errStream());
            systemLogger.err("Failed : exiting with code 1");
            return 1;
        }

        return 0;
    }
}
