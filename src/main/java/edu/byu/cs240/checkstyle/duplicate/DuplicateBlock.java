package edu.byu.cs240.checkstyle.duplicate;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks for exactly duplicated blocks ({...}).
 *
 * @author Michael Davenport
 */
public class DuplicateBlock extends AbstractDuplicateCheck {


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
        return new int[]{TokenTypes.SLIST};
    }


    /**
     * Determines if the textual equality of an ast nodes can be ignored.
     * This class compares strictly, so there are no exceptions
     *
     * @param ast ast node to check
     * @return true if the ast is an exception to the texts being equal
     */
    @Override
    protected boolean isTextException(DetailAST ast) {
        return false;
    }


    /**
     * Logs violations of duplicate blocks.
     *
     * @param offender offending ast token
     * @param original original ast token that offender is a copy of
     */
    @Override
    protected void logViolation(DetailAST offender, DetailAST original) {
        String originalRoot = checkedAst.get(original);
        log(offender, "Duplicated code from " + originalRoot + ":" + original.getLineNo());
    }

}
