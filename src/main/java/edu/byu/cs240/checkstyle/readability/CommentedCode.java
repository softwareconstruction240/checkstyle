package edu.byu.cs240.checkstyle.readability;

import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Reports successive lines of commented out code
 *
 * @author Michael Davenport
 */
public class CommentedCode extends AbstractFileSetCheck {

    private static final Set<Character> CODE_LINE_END_CHARS = Set.of(';', ',', '{', '}', '(', ')', '/');

    // STRINGS_REGEX matches strings that begin with an even number of non-escaped " chars
    private static final String STRINGS_REGEX = "^[^\"]*((?<!/)\"[^\"]*(?<!/)\"[^\"]*)*";
    private static final String BLOCK_COMMENT_START_REGEX = STRINGS_REGEX + "/\\*";
    private static final String BLOCK_COMMENT_END_REGEX = STRINGS_REGEX + "\\*/";

    private static final Pattern BLOCK_COMMENT_START_PATTERN = Pattern.compile(BLOCK_COMMENT_START_REGEX);
    private static final Pattern BLOCK_COMMENT_END_PATTERN = Pattern.compile(BLOCK_COMMENT_END_REGEX);

    private int min = 5;


    /**
     * Sets the minimum number of successive lines of commented code before reporting
     *
     * @param min minimum number
     */
    public void setMin(int min) {
        this.min = min;
    }


    /**
     * Finds and reports commented out code
     *
     * @param file     file to check
     * @param fileText contents of file
     */
    @Override
    protected void processFiltered(File file, FileText fileText) {
        List<String> lines = Arrays.asList(fileText.toLinesArray());
        lines.replaceAll(String::trim);

        boolean inBlockComment = false;
        boolean inTextBlock = false;
        int numLines = 0;
        int startLine = -1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isBlank()) {
                continue;
            }
            if (!line.startsWith("//") && !beginsBlockComment(line) && !inBlockComment && line.contains("\"\"\"")) {
                inTextBlock = !inTextBlock;
            }
            if (inTextBlock) {
                continue;
            }
            if (!line.startsWith("//") && beginsBlockComment(line)) {
                inBlockComment = true;
            }
            if (endsBlockComment(line)) {
                inBlockComment = false;
            }
            if ((inBlockComment || line.startsWith("//")) &&
                    CODE_LINE_END_CHARS.contains(line.charAt(line.length() - 1))) {
                if (numLines == 0) {
                    startLine = i;
                }
                numLines++;
            } else {
                if (numLines >= min) {
                    logViolation(file.getAbsolutePath(), numLines, startLine);
                }
                startLine = -1;
                numLines = 0;
            }
        }
        if (numLines >= min) {
            logViolation(file.getAbsolutePath(), numLines, startLine);
        }
    }

    private boolean beginsBlockComment(String str) {
        return BLOCK_COMMENT_START_PATTERN.matcher(str).find();
    }

    private boolean endsBlockComment(String str) {
        return BLOCK_COMMENT_END_PATTERN.matcher(str).find();
    }

    /**
     * Reports an instance of commented out code
     *
     * @param path      file path where the commented code is
     * @param numLines  number of lines of code commented out
     * @param startLine line number of start of
     */
    private void logViolation(String path, int numLines, int startLine) {
        log(startLine, String.format("%d lines of commented out code", numLines));
        fireErrors(path);
    }

}
