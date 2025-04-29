# BYU C S 240 Code Style/Quality Checker
This repository holds the source code for the "Code Quality Checker" for the BYU C S 240 class that is not part of the base checkstyle repository

The code is based on [checkstyle](https://github.com/checkstyle/checkstyle) and includes the [checkstyle built-in checks](https://checkstyle.org/checks.html). Checkstyle also allows for [writing your own custom checks](https://checkstyle.org/writingchecks.html); most of the code in this repository is either a custom check or part of a test for a custom check.

Checkstyle is based on the [Visitor Pattern](https://en.wikipedia.org/wiki/Visitor_pattern), often representing Java code in an [Abstract Syntax Tree (AST)](https://en.wikipedia.org/wiki/Abstract_syntax_tree). Because of this, any code must compile in order to be used as input for the checker. (Note to future TA's: if C S 236 still had you building a Datalog parser, an AST is somewhat similar to the list of tokens except it's a tree instead of a list of tokens)


## Building
This repository uses Maven for dependencies and building so you can build it with `mvn package` after which a jar will be in the `target` directory as `checkstyle-<version>.jar`, like `checkstyle-1.0.8.jar`.


## Running
`java -jar path/to/jar/checkstyle-<version>.jar -c <check file name>.xml <path to files/folders to check (can be multiple)`

C S 240 Chess project running example (cs240_checks.xml is in the jar and usable from the jar, but you can substitute it if needed):

`java -jar ./checkstyle-1.0.8.jar -c cs240_checks.xml ./client/ ./server/ ./shared/`


## Code Tour
```txt
└─ src
   ├─ main
   │  ├─ java/edu.byu.cs240.checkstyle                      Root folder for custom checkstyle checks
   │  │  ├─ util                                            Contains utility functionality used in multiple places          
   │  │  └─ <package name>                                  Contains custom checks for a particular quality rubric category
   │  │      └─ <check name>.java                           Contains code necessary for checking <check name>
   │  └─ resources
   │     ├─ checkstyle_packages.xml                         Informs Checkstyle where to find custom check classes
   │     └─ cs240_checks.xml                                Informs Checkstyle which checks to run. Used as an argument when running checkstyle. Named similarly 
   │                                                            to google_checks.xml and sun_checks.xml, which also appear in the jar.
   └─ test
      │  ├─ java/edu.byu.cs240.checkstyle                   Root folder for tests for the custom checkstyle checks
      │  ├─ CheckTest.java                                  Base abstract class for writing tests for custom checks          
      │  └─ <package name>                                  Contains tests for custom checks for a particular quality rubric category. Most extend CheckTest
      │      └─ <check name>Test.java                       Tests for <check name>.java inside main. Uses input files from test/resources
      └─ resources
         └─ edu.byu.cs240.checkstyle                        Root folder for code input files used to test the checks. Generally nonsense code
             │                                                  with specific problems we expect the check to find. Many contain no errors (for
             │                                                  the specified check) to make sure it doesn't flag in unexpected cases it shouldn't
             └─ <package name>.testInputs.<check name>      Test input files for a specific check
```