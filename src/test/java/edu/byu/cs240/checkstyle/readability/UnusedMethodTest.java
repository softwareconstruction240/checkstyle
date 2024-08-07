package edu.byu.cs240.checkstyle.readability;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import edu.byu.cs240.checkstyle.CheckTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

public class UnusedMethodTest extends CheckTest {


    @Test
    @DisplayName("Should find no errors in code that has no unused methods")
    public void should_FindNoErrors_when_NoUnused() throws CheckstyleException {
        String fileName = "testInputs/unusedMethod/should_FindNoErrors_when_NoUnused.java";
        testFiles(0, fileName);
    }


    @Test
    @DisplayName("Should find no errors when unused methods are annotated")
    public void should_FindNoErrors_when_UnusedAnnotated() throws CheckstyleException {
        String fileName = "testInputs/unusedMethod/should_FindNoErrors_when_UnusedAnnotated.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find no errors in code that has otherwise unused methods in allowed annotations")
    public void should_FindNoErrors_when_UnusedInAllowedAnnotation() throws CheckstyleException {
        String fileName = "testInputs/unusedMethod/should_FindNoErrors_when_UnusedInAllowedAnnotation.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find errors in code that has unused methods")
    public void should_FindErrors_when_UnusedMethods() throws CheckstyleException {
        String fileName = "testInputs/unusedMethod/should_FindErrors_when_UnusedMethods.java";
        testFiles(2, fileName);
    }

    @Test
    @DisplayName("Should find errors in code that has duplicate unused methods")
    public void should_FindErrors_when_DuplicateUnusedMethods() throws CheckstyleException {
        String fileName = "testInputs/unusedMethod/should_FindErrors_when_DuplicateUnusedMethods.java";
        testFiles(4, fileName);
    }


    @Override
    protected Set<Class<?>> getCheckClasses() {
        return Set.of(UnusedMethodWalker.class, UnusedMethodReporter.class);
    }


    @Override
    protected boolean isTreeWalkerDependent(Class<?> clazz) {
        return clazz == UnusedMethodWalker.class;
    }


    @Override
    protected Map<String, String> getCheckProperties(Class<?> clazz) {
        if (clazz == UnusedMethodWalker.class) {
            return Map.of("allowedAnnotations", "Override",
                    "methodParameterAnnotations", "FooBar");
        }
        else return Map.of();
    }

}
