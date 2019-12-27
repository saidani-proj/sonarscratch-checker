# How SONARSCRATCH checker works

Before using this tool, it is recommended to understand, how run *SonarQube* analysis.
Every language (or development platform) has its way of analysis integration. See this [link](https://docs.sonarqube.org/latest/analysis/overview) for more details.

Using *SONARSCRATCH* docker and after fixing all issues found by *SonarQube* , you want set up a
continuous integration for you project. So you decide in the part of your integration flow to
check if there are an issues reported by your custom configuration *SonarQube*. To do this
you must install this tool and execute this command :

```bash
sn-scratch-ch
```

The console output shows this :

![Run without arguments](How-works/run-without-args.png)

As you see, *SONARSCRATCH* checker attempts to check the current activity of *SonarQube*. If your
instance is not yet running, the program with exit with error (code `1`). By default the tool check
the number of issues, if you want a report you must add `-r` option like this :

```bash
sn-scratch-ch -r
```

The generated report uses your source code to make previews, you must add the root path using
the option `--project` like this :

```bash
sn-scratch-ch -r --project:key "path/to/root/source"
```

where the **key** is the project key used by *SonarQube*. It is possible to see issues details using your browser (by default using the url **<http://localhost:9000>**). You can get the key from running *SonarQube* instance following theses steps :

1. Browse your *SonarQube* instance.
2. Click on your project

   ![Project root access](How-works/project-root.png)

3. Go to the part **About this project**

   ![About project](How-works/about-project.png)

4. At the bottom, you can find the project key

   ![Project key](How-works/project-key.png)

This is an example of a report :

![Report example](How-works/report-example.png)

Because starting new *SonarQube* instance can take time, *SONARSCRATCH* checker can wait using
the option `-c`, you can set the time (in milliseconds) between two attemps (by default 20 attemps as maximum),
like this (10 seconds) :

```bash
sn-scratch-ch -c --sleep 10000
```

So if we summarize, here are the steps of any integration using *SONARSCRATCH* :

1. Run *SonarQube* container using *SONARSCRATCH* docker.
1. Wait *SonarQube* instance to be ready using the option `-c` of *SONARSCRATCH* checker.
1. Start your analyse.
1. Check the result using *SONARSCRATCH* checker.
