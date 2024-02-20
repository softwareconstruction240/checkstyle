package edu.byu.cs240.checkstyle.packages;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import edu.byu.cs240.checkstyle.CheckTest;
import edu.byu.cs240.checkstyle.packages.FilesPerPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

class FilesPerPackageTest extends CheckTest {

    @Override
    protected Set<Class<?>> getCheckClasses() {
        return Set.of(FilesPerPackage.class);
    }


    @Override
    protected boolean isTreeWalkerDependent(Class<?> clazz) {
        return false;
    }


    @Override
    protected Map<String, String> getCheckProperties(Class<?> clazz) {
        return Map.of("max", "1");
    }

    @Test
    @DisplayName("Should find no errors in packages below limit")
    public void should_FindNoErrors_when_BelowLimit() throws CheckstyleException {
        String fileName = "testInputs/filesPerPackage/oneFile/Empty.java";
        testFiles(0, fileName);
    }

    @Test
    @DisplayName("Should find errors in packages above limit")
    public void should_FindErrors_when_AboveLimit() throws CheckstyleException {
        String emptyFileName = "testInputs/filesPerPackage/twoFiles/Empty.java";
        String lessEmptyFileName = "testInputs/filesPerPackage/twoFiles/LessEmpty.java";
        testFiles(1, emptyFileName, lessEmptyFileName);
    }

}