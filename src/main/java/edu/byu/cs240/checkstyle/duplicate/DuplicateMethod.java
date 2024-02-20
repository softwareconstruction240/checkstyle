package edu.byu.cs240.checkstyle.duplicate;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import edu.byu.cs240.checkstyle.util.TreeUtils;

/**
 * Checks for duplicated methods, excluding variable names.
 *
 * @author Michael Davenport
 */
public class DuplicateMethod extends AbstractDuplicateCheck {

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
        return new int[]{TokenTypes.METHOD_DEF};
    }


    /**
     * Determines if the textual equality of an ast nodes can be ignored.
     * Ignores variable names.
     *
     * @param ast ast node to check
     * @return true if the ast is an exception to the texts being equal
     */
    @Override
    protected boolean isTextException(DetailAST ast) {
        return TreeUtils.isVariableName(ast);
    }


    /**
     * Logs violations of duplicate method.
     *
     * @param offender offending ast token
     * @param original original ast token that offender is a copy of
     */
    @Override
    protected void logViolation(DetailAST offender, DetailAST original) {
        String originalRoot = checkedAst.get(original);
        log(offender, "Method duplicated from " + originalRoot + ":" + original.getLineNo());
    }

}
