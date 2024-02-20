package edu.byu.cs240.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.net.URL;
import java.util.*;

public abstract class CheckTest {

    private Checker checker;


    @BeforeEach
    public void setUp() throws CheckstyleException {
        checker = new Checker();
        checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
        DefaultConfiguration checks = new DefaultConfiguration("Checks");
        for(Class<?> className : getCheckClasses()) {
            DefaultConfiguration checkUnderTest = new DefaultConfiguration(className.getCanonicalName());
            getCheckProperties(className).forEach(checkUnderTest::addProperty);
            if (isTreeWalkerDependent(className)) {
                DefaultConfiguration treeWalker = new DefaultConfiguration("TreeWalker");
                treeWalker.addChild(checkUnderTest);
                checks.addChild(treeWalker);
            } else {
                checks.addChild(checkUnderTest);
            }
        }
        checker.configure(checks);
    }


    protected void testFiles(int expectedNumberOfErrors, String... fileNames) throws CheckstyleException {
        List<File> files = new ArrayList<>();
        for(String fileName : fileNames) {
            URL testFileUrl = getClass().getResource(fileName);
            File testFile = new File(Objects.requireNonNull(testFileUrl).getFile());
            files.add(testFile);
        }
        int numberOfErrors = checker.process(files);
        Assertions.assertEquals(expectedNumberOfErrors, numberOfErrors);
    }


    protected abstract Set<Class<?>> getCheckClasses();


    protected abstract boolean isTreeWalkerDependent(Class<?> clazz);


    protected abstract Map<String, String> getCheckProperties(Class<?> clazz);

}
