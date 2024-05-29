package edu.byu.cs240.checkstyle.decomposition;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import edu.byu.cs240.checkstyle.CheckTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;


class DuplicateLinesTest extends CheckTest {

    @Override
    protected Set<Class<?>> getCheckClasses() {
        return Set.of(DuplicateLines.class);
    }


    @Override
    protected boolean isTreeWalkerDependent(Class<?> clazz) {
        return false;
    }


    @Override
    protected Map<String, String> getCheckProperties(Class<?> clazz) {
        return Map.of("min", "4");
    }


    @Test
    @DisplayName("Should find no errors in code that has no duplicates")
    public void should_FindNoErrors_when_NoDuplicates() throws CheckstyleException {
        String fileName = "testInputs/duplicateLines/should_FindNoErrors_when_NoDuplicates.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find no errors in code that has near duplicates")
    public void should_FindNoErrors_when_NearDuplicates() throws CheckstyleException {
        String fileName = "testInputs/duplicateLines/should_FindNoErrors_when_NearDuplicates.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find no errors when duplicates are below the minimum complexity")
    public void should_FindNoErrors_when_BelowThreshold() throws CheckstyleException {
        String fileName = "testInputs/duplicateLines/should_FindNoErrors_when_BelowThreshold.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find errors in code that has duplicate lines")
    public void should_FindErrors_when_DuplicateLines() throws CheckstyleException {
        String fileName = "testInputs/duplicateLines/should_FindErrors_when_DuplicateLines.java";
        testFiles(4, fileName);
    }
}