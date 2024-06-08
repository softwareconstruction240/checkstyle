package edu.byu.cs240.checkstyle.readability;

import com.puppycrawl.tools.checkstyle.FileStatefulCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        if(isExcluded(ast)) return;
        ++depth;
        if (depth > max) {
            String message = "Code is too deeply nested. Current depth: %s (max is %s)".formatted(depth, max);
            log(ast, message, depth, max);
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        if(!isExcluded(ast)) --depth;
    }

    private boolean isExcluded(DetailAST ast) {
        return (ast.getType() == TokenTypes.LITERAL_IF && ast.getParent().getType() == TokenTypes.LITERAL_ELSE) || //else if
                (ast.getType() == TokenTypes.SLIST && SLIST_EXCLUDED_PARENTS.contains(ast.getParent().getType())) ||
                (ast.getType() == TokenTypes.SLIST && ast.getParent().getType() == TokenTypes.LITERAL_ELSE &&
                        getChildren(ast.getParent().getParent()).stream().noneMatch(ast1 -> ast1.getType() == TokenTypes.SLIST)) ||
                (ast.getType() != TokenTypes.SLIST && getChildren(ast).stream().anyMatch(ast1 -> ast1.getType() == TokenTypes.SLIST)); //block with curly braces
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
