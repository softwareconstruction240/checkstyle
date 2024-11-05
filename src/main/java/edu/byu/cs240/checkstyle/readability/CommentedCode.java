package edu.byu.cs240.checkstyle.readability;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Set;

/**
 * Reports successive lines of commented out code
 *
 * @author Michael Davenport
 */
public class CommentedCode extends AbstractCheck {

    private static final Set<Character> CODE_LINE_END_CHARS = Set.of(';', ',', '{', '}', '(', ')', '/');

    private int min = 5;

    private int firstGroupCommentedLine;

    private int numSuccessiveLines;

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
    public void beginTree(DetailAST rootAST) {
        resetCounters();
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        checkLines();
    }

    @Override
    public void visitToken(DetailAST ast) {
        String[] split = ast.getText().split("\n");
        int lineNum = ast.getLineNo();
        for(int i = 0; i < split.length; i++) {
            String line = split[i].trim();

            if(line.isBlank()) {
                continue;
            }

            if(CODE_LINE_END_CHARS.contains(line.charAt(line.length() - 1))) {
                handleCommentedLine(lineNum + i);
            }
            else {
                checkLines();
                resetCounters();
            }
        }
    }

    private void handleCommentedLine(int lineNum) {
        if(firstGroupCommentedLine + numSuccessiveLines < lineNum) {
            checkLines();
            resetCounters();
        }

        if(numSuccessiveLines == 0) {
            firstGroupCommentedLine = lineNum;
            numSuccessiveLines = 1;
        }
        else {
            numSuccessiveLines++;
        }
    }

    private void resetCounters() {
        firstGroupCommentedLine = Integer.MIN_VALUE;
        numSuccessiveLines = 0;
    }

    private void checkLines() {
        if(numSuccessiveLines >= min) {
            log(firstGroupCommentedLine, String.format("%d lines of commented out code", numSuccessiveLines));
        }
    }
}
