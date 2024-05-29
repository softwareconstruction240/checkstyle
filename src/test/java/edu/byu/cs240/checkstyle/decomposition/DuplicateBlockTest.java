package edu.byu.cs240.checkstyle.decomposition;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import edu.byu.cs240.checkstyle.CheckTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;


class DuplicateBlockTest extends CheckTest {

    @Override
    protected Set<Class<?>> getCheckClasses() {
        return Set.of(DuplicateBlock.class);
    }


    @Override
    protected boolean isTreeWalkerDependent(Class<?> clazz) {
        return true;
    }


    @Override
    protected Map<String, String> getCheckProperties(Class<?> clazz) {
        return Map.of("minComplexity", "10");
    }


    @Test
    @DisplayName("Should find no errors in code that has no duplicates")
    public void should_FindNoErrors_when_NoDuplicates() throws CheckstyleException {
        String fileName = "testInputs/duplicateBlock/should_FindNoErrors_when_NoDuplicates.java";
        testFiles(0, fileName);
    }


    @Test
    @DisplayName("Should find no errors in code that has near duplicates")
    public void should_FindNoErrors_when_NearDuplicates() throws CheckstyleException {
        String fileName = "testInputs/duplicateBlock/should_FindNoErrors_when_NearDuplicates.java";
        testFiles(0, fileName);
    }


    @Test
    @DisplayName("Should find no errors when duplicates are below the minimum complexity")
    public void should_FindNoErrors_when_BelowThreshold() throws CheckstyleException {
        String fileName = "testInputs/duplicateBlock/should_FindNoErrors_when_BelowThreshold.java";
        testFiles(0, fileName);
    }


    @Test
    @DisplayName("Should find errors in code that has duplicate blocks")
    public void should_FindErrors_when_DuplicateBlocks() throws CheckstyleException {
        String fileName = "testInputs/duplicateBlock/should_FindErrors_when_DuplicateBlocks.java";
        testFiles(3, fileName);
    }

}