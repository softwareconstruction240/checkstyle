package edu.byu.cs240.checkstyle.duplicate;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import edu.byu.cs240.checkstyle.util.TreeUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for checking duplicates across syntax trees
 *
 * @author Michael Davenport
 */
public abstract class AbstractDuplicateCheck extends AbstractCheck {


    protected final Map<DetailAST, String> checkedAst = new HashMap<>();

    protected int minComplexity = 50;


    /**
     * Sets the minimum complexity of the check
     *
     * @param minComplexity minimum number of tokens to be counted
     */
    public void setMinComplexity(int minComplexity) {
        this.minComplexity = minComplexity;
    }

    /**
     * Visits an AST node and checks to see if it has the required complexity
     * and if it's a duplicate of any before seen.
     * If it has been seen before, logs the violation
     * If not, adds it to the set of before seen tokens.
     *
     * @param ast specific AST to check
     */
    @Override
    public void visitToken(DetailAST ast) {
        if (Arrays.stream(getAcceptableTokens()).noneMatch(i -> i == ast.getType())) return;

        int complexity = TreeUtils.astComplexity(ast);
        if (complexity < minComplexity) return;

        for (DetailAST original : checkedAst.keySet()) {
            if (astEquals(original, ast)) {
                logViolation(ast, original);
                return;
            }
        }

        checkedAst.put(ast, getFilePath());
    }


    /**
     * Logs violations of duplicates.
     *
     * @param offender offending ast token
     * @param original original ast token that offender is a copy of
     */
    protected abstract void logViolation(DetailAST offender, DetailAST original);


    /**
     * Checks to see if two ast nodes are equal to each other.
     *
     * @param original original ast node
     * @param compare  ast node to compare to original
     * @return true if original and compare are effectively equal, false otherwise
     */
    protected boolean astEquals(DetailAST original, DetailAST compare) {
        if (original.getType() != compare.getType()) return false;
        if (original.getChildCount() != compare.getChildCount()) return false;

        if (!original.getText().equals(compare.getText()) && !isTextException(original)) return false;

        DetailAST originalChild = original.getFirstChild();
        DetailAST compareChild = compare.getFirstChild();

        while (originalChild != null && compareChild != null) {
            if (!astEquals(originalChild, compareChild)) return false;
            originalChild = originalChild.getNextSibling();
            compareChild = compareChild.getNextSibling();
        }

        return originalChild == compareChild;
    }


    /**
     * Determines if the textual equality of an ast nodes can be ignored.
     *
     * @param ast ast node to check
     * @return true if the ast is an exception to the texts being equal
     */
    protected abstract boolean isTextException(DetailAST ast);

}
