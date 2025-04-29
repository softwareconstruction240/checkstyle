package edu.byu.cs240.checkstyle.readability;

import com.puppycrawl.tools.checkstyle.FileStatefulCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Reports uses of nested code structures that in most styles would indent the code (if/else, loops, try/catch, switch)
 * farther than a configurable number of levels
 *
 * @author Michael Davenport
 */
@FileStatefulCheck
public class NestingDepth extends AbstractCheck {
    private static final Set<Integer> SLIST_EXCLUDED_PARENTS = Set.of(TokenTypes.METHOD_DEF, TokenTypes.LITERAL_CATCH, TokenTypes.CASE_GROUP);

    /** Specify maximum allowed nesting depth. */
    private int max = 4;
    /** Current nesting depth. */
    private int depth;

    /**
     * Setter to specify maximum allowed nesting depth.
     *
     * @param max maximum allowed nesting depth.
     * @since 5.3
     */
    public void setMax(int max) {
        this.max = max;
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
        return new int[] {TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_WHILE, TokenTypes.LITERAL_DO,
                TokenTypes.LITERAL_SWITCH, TokenTypes.LITERAL_IF,
                TokenTypes.LITERAL_TRY, TokenTypes.SLIST};
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (isExcluded(ast)) {
            return;
        }
        ++depth;
        if (depth > max) {
            String message = "Code is too deeply nested. Current depth: %s (max is %s)".formatted(depth, max);
            log(ast, message, depth, max);
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        if(!isExcluded(ast)) {
            --depth;
        }
    }

    /**
     * Determines if the provided token should be excluded as a nesting level. Generally this is because it is
     * directly tied to another token that was included. For example, "else if" would double count (the else block
     * and the if statement are separate) if it wasn't excluded here.
     * @param ast AST token to check
     * @return true if ast should be excluded, false otherwise
     */
    private boolean isExcluded(DetailAST ast) {
        return isElseIf(ast) || isSListExcluded(ast) || isElseWithBracesOfIfWithoutBraces(ast) || isBlockWithBraces(ast);
    }

    private boolean isElseIf(DetailAST ast) {
        return ast.getType() == TokenTypes.LITERAL_IF && ast.getParent().getType() == TokenTypes.LITERAL_ELSE;
    }

    private boolean isSListExcluded(DetailAST ast) {
        return ast.getType() == TokenTypes.SLIST && SLIST_EXCLUDED_PARENTS.contains(ast.getParent().getType());
    }

    private boolean isElseWithBracesOfIfWithoutBraces(DetailAST ast) {
        return ast.getType() == TokenTypes.SLIST && ast.getParent().getType() == TokenTypes.LITERAL_ELSE &&
                !hasSListChild(ast.getParent().getParent());
    }

    private boolean isBlockWithBraces(DetailAST ast) {
        return ast.getType() != TokenTypes.SLIST && hasSListChild(ast);
    }

    private boolean hasSListChild(DetailAST ast) {
        return getChildren(ast).stream().anyMatch(ast1 -> ast1.getType() == TokenTypes.SLIST);
    }

    private List<DetailAST> getChildren(DetailAST ast) {
        DetailAST child = ast.getFirstChild();
        List<DetailAST> children = new ArrayList<>();
        while(child != null) {
            children.add(child);
            child = child.getNextSibling();
        }
        return children;
    }
}
