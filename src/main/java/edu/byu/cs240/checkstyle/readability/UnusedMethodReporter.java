package edu.byu.cs240.checkstyle.readability;

import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static edu.byu.cs240.checkstyle.readability.UnusedMethodWalker.Method;

/**
 * Reports unused methods. This class is useless unless UnusedMethodWalker is also used.
 * Two separate classes are necessary because children of TreeWalker are not notified when all files are finished,
 * but using the abstract syntax tree was necessary.
 *
 * @author Michael Davenport
 */
public class UnusedMethodReporter extends AbstractFileSetCheck {

    @Override
    public void beginProcessing(String charset) {
        UnusedMethodWalker.clearData();
    }


    @Override
    protected void processFiltered(File file, FileText fileText) {}


    /**
     * Determines and reports which methods are unused
     */
    @Override
    public void finishProcessing() {
        Set<String> calledMethods = UnusedMethodWalker.getCalledMethods();
        Map<String, Set<Method>> definedMethods = new HashMap<>(UnusedMethodWalker.getDefinedMethods());

        calledMethods.forEach(definedMethods::remove);

        definedMethods.forEach((key, value) -> {
            for (Method method : value) {
                log(method.ast().getLineNo(), "Unused method " + key);
                fireErrors(method.filePath());
            }
        });
    }

}
