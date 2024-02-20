package edu.byu.cs240.checkstyle.deadcode;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import edu.byu.cs240.checkstyle.util.PropertyUtils;
import edu.byu.cs240.checkstyle.util.TreeUtils;

import java.util.*;

/**
 * Locates unused methods. This class is useless unless UnusedMethodReporter is also used.
 * Two separate classes are necessary because children of TreeWalker are not notified when all files are finished,
 * but using the abstract syntax tree was necessary.
 *
 * @author Michael Davenport
 */
public class UnusedMethodWalker extends AbstractCheck {

    private static final Set<String> calledMethods = new HashSet<>();

    private static final Map<String, DetailAST> definedMethods = new HashMap<>();

    private static final Map<String, String> methodClasses = new HashMap<>();

    private String rootName;

    private final Set<String> allowedAnnotations = new HashSet<>(Set.of("Override"));

    private final Set<String> excludedMethods = new HashSet<>(Set.of("main", "equals", "hashCode", "toString"));


    public static Set<String> getCalledMethods() {
        return Collections.unmodifiableSet(calledMethods);
    }


    public static Map<String, DetailAST> getDefinedMethods() {
        return Collections.unmodifiableMap(definedMethods);
    }


    public static Map<String, String> getMethodClasses() {
        return Collections.unmodifiableMap(methodClasses);
    }


    public static void clearData() {
        calledMethods.clear();
        definedMethods.clear();
        methodClasses.clear();
    }


    /**
     * Sets a list of annotations for methods that can be excluded
     *
     * @param parse a comma-delimited list of annotations
     */
    public void setAllowedAnnotations(String parse) {
        PropertyUtils.parseString(parse, allowedAnnotations, ",");
    }


    /**
     * Sets a list of method names that can be excluded
     *
     * @param parse a comma-delimited list of method names
     */
    public void setExcludedMethods(String parse) {
        PropertyUtils.parseString(parse, excludedMethods, ",");
    }



    @Override
    public int[] getDefaultTokens() {
        return getRequiredTokens();
    }


    @Override
    public int[] getAcceptableTokens() {
        return getRequiredTokens();
    }


    @Override
    public int[] getRequiredTokens() {
        return new int[]{TokenTypes.METHOD_DEF, TokenTypes.METHOD_CALL, TokenTypes.METHOD_REF};
    }


    /**
     * Visits tokens and saves method definitions, calls, and references to appropriate collections
     *
     * @param ast ast node to visit
     */
    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_DEF -> {
                DetailAST modifiers = ast.getFirstChild();
                DetailAST firstModifier = modifiers.getFirstChild();
                if (firstModifier != null && firstModifier.getType() == TokenTypes.ANNOTATION) {
                    DetailAST annotation = firstModifier;
                    while (annotation != null && annotation.getType() == TokenTypes.ANNOTATION) {
                        if (allowedAnnotations.contains(annotation.findFirstToken(TokenTypes.IDENT).getText())) return;
                        annotation = annotation.getNextSibling();
                    }
                }
                DetailAST astChild = modifiers;
                while (astChild != null && astChild.getType() != TokenTypes.IDENT) {
                    astChild = astChild.getNextSibling();
                }
                if (astChild != null) {
                    String methodName = astChild.getText();
                    if (excludedMethods.contains(methodName)) return;
                    definedMethods.put(methodName, ast);
                    methodClasses.put(methodName, rootName);
                }
            }
            case TokenTypes.METHOD_CALL -> {
                DetailAST methodCall = ast.getFirstChild();
                while (methodCall != null && methodCall.getType() != TokenTypes.IDENT) {
                    methodCall = methodCall.getLastChild();
                }
                if (methodCall != null) calledMethods.add(methodCall.getText());
            }
            case TokenTypes.METHOD_REF -> calledMethods.add(ast.getLastChild().getText());
        }
    }


    /**
     * Finds the name of the class/interface/etc. at the root and saves this value to rootName
     *
     * @param rootAST root of the tree
     */
    @Override
    public void beginTree(DetailAST rootAST) {
        rootName = TreeUtils.getRootName(rootAST);
    }

}
