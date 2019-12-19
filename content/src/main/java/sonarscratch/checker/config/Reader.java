/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.config;

import java.util.ArrayList;

public class Reader {
    private static final int EXTRA_STEP_ARGUMENTS_COUNT = 2;
    private String[] args;

    public Reader(String[] args) {
        this.args = args.clone();
    }

    private String getNextArgument(int i) throws ReaderException {
        if (i + 1 < args.length) {
            return args[i + 1];
        }

        throw new ReaderException("Out of arguments");
    }

    public Config read() throws ReaderException {
        var config = new Config();
        var projects = new ArrayList<Project>();
        int stepCount;

        for (int i = 0; i < args.length; i += stepCount) {
            var arg = args[i];

            stepCount = fetchSingleArgument(arg, config);

            if (stepCount == 0) {
                stepCount = fetchExtraArgument(i, config, projects);
            }

            if (stepCount == 0) {
                throw new ReaderException("Unknown argument '" + arg + "'");
            }
        }

        config.setProjects(projects);
        return config;
    }

    private static int fetchSingleArgument(String arg, Config config) {
        int stepCount = 0;

        if (arg.isEmpty()) {
            stepCount = 1;
        }

        if ("--help".equals(arg) || "-h".equals(arg)) {
            config.setShowHelp(true);
            stepCount = 1;
        }

        if ("--version".equals(arg) || "-v".equals(arg)) {
            config.setShowVersion(true);
            stepCount = 1;
        }

        if ("--report".equals(arg) || "-r".equals(arg)) {
            config.setWriteReport(true);
            stepCount = 1;
        }

        if ("--check".equals(arg) || "-c".equals(arg)) {
            config.setCheck(true);
            stepCount = 1;
        }

        return stepCount;
    }

    private int fetchExtraArgument(int argIndex, Config config, ArrayList<Project> projects) throws ReaderException {
        var arg = args[argIndex];
        int stepCount = 0;

        if ("--url".equals(arg)) {
            config.setSonarUrl(getNextArgument(argIndex));
            stepCount = EXTRA_STEP_ARGUMENTS_COUNT;
        }

        if ("--count".equals(arg)) {
            config.setSonarAttemptsCount(Integer.parseInt(getNextArgument(argIndex)));
            stepCount = EXTRA_STEP_ARGUMENTS_COUNT;
        }

        if ("--sleep".equals(arg)) {
            config.setSonarAttemptSleepMilliseconds(Integer.parseInt(getNextArgument(argIndex)));
            stepCount = EXTRA_STEP_ARGUMENTS_COUNT;
        }

        if ("--path".equals(arg)) {
            config.setReportPath(getNextArgument(argIndex));
            stepCount = EXTRA_STEP_ARGUMENTS_COUNT;
        }

        if (arg.startsWith("--project:")) {
            projects.add(new Project(arg.substring("--project:".length()), getNextArgument(argIndex)));
            stepCount = EXTRA_STEP_ARGUMENTS_COUNT;
        }

        if ("--encoding".equals(arg) || "-e".equals(arg)) {
            config.setEncoding(getNextArgument(argIndex));
            stepCount = EXTRA_STEP_ARGUMENTS_COUNT;
        }

        return stepCount;
    }
}
