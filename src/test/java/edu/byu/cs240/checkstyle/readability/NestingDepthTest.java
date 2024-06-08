package edu.byu.cs240.checkstyle.readability;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import edu.byu.cs240.checkstyle.CheckTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

public class NestingDepthTest extends CheckTest {
    @Override
    protected Set<Class<?>> getCheckClasses() {
        return Set.of(NestingDepth.class);
    }

    @Override
    protected boolean isTreeWalkerDependent(Class<?> clazz) {
        return true;
    }

    @Override
    protected Map<String, String> getCheckProperties(Class<?> clazz) {
        return Map.of("max", "2");
    }

    @Test
    @DisplayName("Should find no errors in code below the maximum nesting depth")
    public void should_FindNoErrors_when_BelowNestingThreshold() throws CheckstyleException {
        String fileName = "testInputs/nestingDepth/should_FindNoErrors_when_BelowNestingThreshold.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find no errors in code with a long if/else if chain")
    public void should_FindNoErrors_when_ElseIfChain() throws CheckstyleException {
        String fileName = "testInputs/nestingDepth/should_FindNoErrors_when_ElseIfChain.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find errors in code nested without curly braces")
    public void should_FindErrors_when_NoCurlyNest() throws CheckstyleException {
        String fileName = "testInputs/nestingDepth/should_FindErrors_when_NoCurlyNest.java";
        testFiles(2, fileName);
    }

    @Test
    @DisplayName("Should find errors in code at or above the maximum nesting depth")
    public void should_FindErrors_when_AtOrAboveNestingThreshold() throws CheckstyleException {
        String fileName = "testInputs/nestingDepth/should_FindErrors_when_AtOrAboveNestingThreshold.java";
        testFiles(9, fileName);
    }
}
