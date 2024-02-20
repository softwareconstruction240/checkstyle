package edu.byu.cs240.checkstyle.duplicate;


import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import edu.byu.cs240.checkstyle.CheckTest;
import edu.byu.cs240.checkstyle.duplicate.DuplicateMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

class DuplicateMethodTest extends CheckTest {

    @Override
    protected Set<Class<?>> getCheckClasses() {
        return Set.of(DuplicateMethod.class);
    }


    @Override
    protected boolean isTreeWalkerDependent(Class<?> clazz) {
        return true;
    }


    @Override
    protected Map<String, String> getCheckProperties(Class<?> clazz) {
        return Map.of("minComplexity", "20");
    }

    @Test
    @DisplayName("Should find no errors in code that has no duplicates")
    public void should_FindNoErrors_when_NoDuplicates() throws CheckstyleException {
        String fileName = "testInputs/duplicateMethod/should_FindNoErrors_when_NoDuplicates.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find no errors in code that has almost duplicates but a variable of different type")
    public void should_FindNoErrors_when_NearDuplicatesVariableType() throws CheckstyleException {
        String fileName = "testInputs/duplicateMethod/should_FindNoErrors_when_NearDuplicatesVariableType.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find no errors in code that has duplicates below complexity threshold")
    public void should_FindNoErrors_when_DuplicatesBelowThreshold() throws CheckstyleException {
        String fileName = "testInputs/duplicateMethod/should_FindNoErrors_when_DuplicatesBelowThreshold.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find errors in code that has almost duplicates but variables are named differently")
    public void should_FindErrors_when_NearDuplicatesVariableName() throws CheckstyleException {
        String fileName = "testInputs/duplicateMethod/should_FindErrors_when_NearDuplicatesVariableName.java";
        testFiles(1, fileName);
    }

    @Test
    @DisplayName("Should find errors in code that has exactly duplicated methods")
    public void should_FindErrors_when_ExactDuplicates() throws CheckstyleException {
        String fileName = "testInputs/duplicateMethod/should_FindErrors_when_ExactDuplicates.java";
        testFiles(3, fileName);
    }

}