package edu.byu.cs240.checkstyle.readability;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import edu.byu.cs240.checkstyle.util.TreeUtils;

import java.lang.invoke.MethodHandle;
import java.util.*;

/**
 * Locates unused methods. This class is useless unless UnusedMethodReporter is also used.
 * Two separate classes are necessary because children of TreeWalker are not notified when all files are finished,
 * but using the abstract syntax tree was necessary.
 *
 * @author Michael Davenport
 */
public class UnusedMethodWalker extends AbstractCheck {

    private static Set<String> calledMethods = new HashSet<>();

    private static Map<String, Set<Method>> definedMethods = new HashMap<>();

    private Set<String> allowedAnnotations = new HashSet<>(Set.of("Override"));

    private Set<String> excludedMethods = new HashSet<>(Set.of("main", "equals", "hashCode", "toString"));

    private Set<String> methodParameterAnnotations = new HashSet<>();

    private boolean allowGetters = true;

    private boolean allowSetters = true;

    private int maxGetterComplexity = 25;

    private int maxSetterComplexity = 25;


    static Set<String> getCalledMethods() {
        return Collections.unmodifiableSet(calledMethods);
    }

    static Map<String, Set<Method>> getDefinedMethods() {
        return Collections.unmodifiableMap(definedMethods);
    }

    public static void clearData() {
        calledMethods = new HashSet<>();
        definedMethods = new HashMap<>();
    }


    /**
     * Sets a list of annotations for methods that can be excluded
     *
     * @param annotations annotations to exclude marking as unused
     */
    public void setAllowedAnnotations(String[] annotations) {
        allowedAnnotations = new HashSet<>(Arrays.asList(annotations));
    }


    /**
     * Sets a list of method names that can be excluded
     *
     * @param methodNames a list of method names to exclude marking as unused
     */
    public void setExcludedMethods(String[] methodNames) {
        excludedMethods = new HashSet<>(Arrays.asList(methodNames));
    }

    /**
     * Sets a list of annotation names that have method names as parameters to not mark as unused
     *
     * @param annotationNames a list of annotation names that have method names as parameters to not mark as unused
     */
    public void setMethodParameterAnnotations(String[] annotationNames) {
        methodParameterAnnotations = new HashSet<>(Arrays.asList(annotationNames));
    }

    /**
     * Sets if getter methods can be excluded
     *
     * @param allowGetters true if getters are allowed, false otherwise
     */
    public void setAllowGetters(boolean allowGetters) {
        this.allowGetters = allowGetters;
    }

    /**
     * Sets if setter methods can be excluded
     *
     * @param allowSetters true if setters are allowed, false otherwise
     */
    public void setAllowSetters(boolean allowSetters) {
        this.allowSetters = allowSetters;
    }

    /**
     * Sets the maximum complexity of getter methods
     *
     * @param maxGetterComplexity maximum number of tokens to be counted as a getter
     */
    public void setMaxGetterComplexity(int maxGetterComplexity) {
        this.maxGetterComplexity = maxGetterComplexity;
    }

    /**
     * Sets the maximum complexity of setter methods
     *
     * @param maxSetterComplexity maximum number of tokens to be counted as a setter
     */
    public void setMaxSetterComplexity(int maxSetterComplexity) {
        this.maxSetterComplexity = maxSetterComplexity;
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
        return new int[]{TokenTypes.METHOD_DEF, TokenTypes.METHOD_CALL, TokenTypes.METHOD_REF, TokenTypes.ANNOTATION};
    }


    /**
     * Visits tokens and saves method definitions, calls, and references to appropriate collections
     *
     * @param ast ast node to visit
     */
    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_DEF -> visitMethodDefinition(ast);
            case TokenTypes.METHOD_CALL -> visitMethodCall(ast);
            case TokenTypes.METHOD_REF -> visitMethodReference(ast);
            case TokenTypes.ANNOTATION -> visitAnnotation(ast);
        }
    }

    private void visitMethodDefinition(DetailAST ast) {
        DetailAST modifiers = ast.getFirstChild();
        DetailAST annotation = modifiers.findFirstToken(TokenTypes.ANNOTATION);
        if (annotation != null) {
            while (annotation != null && annotation.getType() == TokenTypes.ANNOTATION) {
                String annotationName = annotation.findFirstToken(TokenTypes.IDENT).getText();
                if (allowedAnnotations.contains(annotationName)) {
                    return;
                }
                annotation = annotation.getNextSibling();
            }
        }
        DetailAST astChild = ast.findFirstToken(TokenTypes.IDENT);
        if (astChild != null) {
            String methodName = astChild.getText();
            if (isMethodExcluded(methodName, ast)) {
                return;
            }
            definedMethods.putIfAbsent(methodName, new HashSet<>());
            definedMethods.get(methodName).add(new Method(ast, getFilePath()));
        }
    }

    private boolean isMethodExcluded(String methodName, DetailAST methodAST) {
        if (excludedMethods.contains(methodName)) {
            return true;
        }
        int methodComplexity = TreeUtils.astComplexity(methodAST);
        if (allowGetters &&
                (methodName.toLowerCase().startsWith("get") || methodName.toLowerCase().startsWith("is")) &&
                methodComplexity <= maxGetterComplexity) {
            return true;
        }
        if (allowSetters && methodName.toLowerCase().startsWith("set") &&
                methodComplexity <= maxSetterComplexity) {
            return true;
        }
        return false;
    }

    private static void visitMethodCall(DetailAST ast) {
        DetailAST methodCall = ast.getFirstChild();
        while (methodCall != null && methodCall.getType() != TokenTypes.IDENT) {
            methodCall = methodCall.getLastChild();
        }
        if (methodCall != null) {
            calledMethods.add(methodCall.getText());
        }
    }

    private static void visitMethodReference(DetailAST ast) {
        calledMethods.add(ast.getLastChild().getText());
    }

    private void visitAnnotation(DetailAST ast) {
        String annotationName = ast.getFirstChild().getNextSibling().getText();
        if(!methodParameterAnnotations.contains(annotationName)) {
            return;
        }
        DetailAST parameter = ast.getFirstChild().getNextSibling().getNextSibling();
        while (parameter != null) {
            DetailAST parameterAst = null;
            if(parameter.getType() == TokenTypes.EXPR) {
                parameterAst = parameter;
            }
            else if(parameter.getLastChild() != null && parameter.getLastChild().getType() == TokenTypes.EXPR) {
                parameterAst = parameter.getLastChild();
            }
            if(parameterAst != null && parameterAst.getFirstChild().getType() == TokenTypes.STRING_LITERAL) {
                calledMethods.add(parameterAst.getFirstChild().getText().replace("\"", ""));
            }
            parameter = parameter.getNextSibling();
        }
    }

    record Method(DetailAST ast, String filePath) {}
}
