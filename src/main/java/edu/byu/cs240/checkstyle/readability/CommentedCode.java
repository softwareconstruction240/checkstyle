package edu.byu.cs240.checkstyle.readability;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Reports successive lines of commented out code
 *
 * @author Michael Davenport
 */
public class CommentedCode extends AbstractCheck {

    private static final Set<Character> CODE_LINE_END_CHARS = Set.of(';', ',', '{', '}', '(', ')', '/');

    private int min = 5;

    private final List<CommentNode> commentNodes = new ArrayList<>();

    /**
     * Sets the minimum number of successive lines of commented code before reporting
     *
     * @param min minimum number
     */
    public void setMin(int min) {
        this.min = min;
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
        return new int[]{TokenTypes.COMMENT_CONTENT};
    }

    @Override
    public boolean isCommentNodesRequired() {
        return true;
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        checkCommentList();
    }

    @Override
    public void visitToken(DetailAST ast) {
        CommentNode commentNode = new CommentNode(ast, Arrays.stream(ast.getText().trim().split("\r?\n")).map(String::trim).toList());

        if(!commentNodes.isEmpty() && ast.getLineNo() > commentNodes.getLast().getLastLineNo() + 1) {
            checkCommentList();
        }

        commentNodes.add(commentNode);
    }

    private void checkCommentList() {
        List<String> commentLines = new ArrayList<>();
        commentNodes.forEach(node -> commentLines.addAll(node.lines()));

        if (commentLines.size() < min) {
            commentNodes.clear();
            return;
        }

        int numSuccessiveLines = 0;
        for(String comment : commentLines) {
            if(comment.isBlank() || CODE_LINE_END_CHARS.contains(comment.charAt(comment.length() - 1))) {
                numSuccessiveLines++;
            }
            else {
//            int numSuccessiveLines = commentNodes.getLast().getLastLineNo() + 1 - commentNodes.getFirst().ast().getLineNo();
                if(numSuccessiveLines >= min) {
                    log(commentNodes.getFirst().ast(), String.format("%d lines of commented out code", numSuccessiveLines));
                }
                numSuccessiveLines = 0;
            }
        }
        if(numSuccessiveLines >= min) {
            log(commentNodes.getFirst().ast(), String.format("%d lines of commented out code", numSuccessiveLines));
        }

        commentNodes.clear();
    }

    private record CommentNode(DetailAST ast, List<String> lines) {
        int getLastLineNo() {
            return ast.getLineNo() + lines.size() - 1;
        }
    }
}
