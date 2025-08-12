package edu.byu.cs240.checkstyle.readability;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Reports successive lines of commented out code
 *
 * @author Michael Davenport
 */
public class CommentedCode extends AbstractCheck {

    private static final Map<Pattern, Double> ENTIRE_COMMENT_CODE_PATTERNS = new HashMap<>();
    private static final Map<Pattern, Double> SINGLE_LINE_CODE_PATTERNS = new HashMap<>();
    private static final Map<Pattern, Double> ENTIRE_COMMENT_NONCODE_PATTERNS = new HashMap<>();
    private static final Map<Pattern, Double> SINGLE_LINE_NONCODE_PATTERNS = new HashMap<>();

    private static final Set<String> JAVA_RESERVED_WORDS =
            Set.of("abstract", "continue", "for", "new", "switch", "assert", "default", "package", "synchronized",
                    "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw",
                    "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient",
                    "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class",
                    "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while");

    static {
        ENTIRE_COMMENT_CODE_PATTERNS.put(Pattern.compile("[\\s\\S]*(?:if|for|while)\\s*\\([\\s\\S]+\\)\\s*\\{[\\s\\S]*}[\\s\\S]*"), 0.9); //simple if/for/while
        ENTIRE_COMMENT_CODE_PATTERNS.put(Pattern.compile("[\\s\\S]*if\\s*\\([\\s\\S]+\\)\\s*\\{[\\s\\S]*}\\s*else(?:\\sif\\s*\\([\\s\\S]+\\))?\\s*\\{[\\s\\S]*}[\\s\\S]*"), 0.95); //if/else or if/elseif
        ENTIRE_COMMENT_CODE_PATTERNS.put(Pattern.compile("^(?:[\\s\\S]*\\n\\s*)?(?:private|public|protected)?\\s+\\w+\\s+\\w+\\s*\\([\\s\\S]*\\)\\s*\\{[\\s\\S]*}[\\s\\S]*$"), 0.9); //method declaration

        SINGLE_LINE_CODE_PATTERNS.put(Pattern.compile("^(?:if|while)\\s*\\([\\s\\S]+\\)\\s*\\{?\\s$"), 0.85); //simple if/while
        SINGLE_LINE_CODE_PATTERNS.put(Pattern.compile("^for\\s*\\([\\s\\S]*;[\\s\\S]*;[\\s\\S]*\\)\\s*\\{?\\s$"), 0.9); //start of for loop
        SINGLE_LINE_CODE_PATTERNS.put(Pattern.compile("^\\w+\\s+\\w+\\s*=[\\s\\S]*$"), 0.9); //variable declaration
        SINGLE_LINE_CODE_PATTERNS.put(Pattern.compile("^\\w+\\s*(?:\\+|-|\\*|/|%|&|\\^|\\||<<|>>|>>>)?=[\\s\\S]*$"), 0.85); //variable assignment
        SINGLE_LINE_CODE_PATTERNS.put(Pattern.compile("^(?:\\w+(?:\\(.*\\))?\\.)*\\w+\\(.*\\);$"), 0.85); //method call
        SINGLE_LINE_CODE_PATTERNS.put(Pattern.compile("^return\\s+.*;$"), 0.9); // return statements
        SINGLE_LINE_CODE_PATTERNS.put(Pattern.compile("^\\w+(?:--|\\+\\+);?$"), 0.9); //increment/decrement
        SINGLE_LINE_CODE_PATTERNS.put(Pattern.compile("^.*;$"), 0.8);   //end semicolon
        SINGLE_LINE_CODE_PATTERNS.put(Pattern.compile("^}$"), 0.6);   //only end curly


        ENTIRE_COMMENT_NONCODE_PATTERNS.put(Pattern.compile("^(?:\\*.*\n)*\\*.*?$"), 0.85); //Javadoc comment
        ENTIRE_COMMENT_NONCODE_PATTERNS.put(Pattern.compile("^\\{(?:\\s*\"\\w+\":\\s*(?:\"?\\w+\"?|\"\"|\\[[\\s\\S]*]|\\{[\\s\\S]*}),?\\s*)+}$"), 0.75); //JSON object

        SINGLE_LINE_NONCODE_PATTERNS.put(Pattern.compile("^\\*.*$"), 0.65); //start of javadoc comment line
        SINGLE_LINE_NONCODE_PATTERNS.put(Pattern.compile("^\\*\\s+@(?:param|return|throws)\\s+.*$"), 0.8); //start of javadoc comment line
        SINGLE_LINE_NONCODE_PATTERNS.put(Pattern.compile("^\"\\w+\":\\s*(?:\"?[\\w: ]+\"?|\"\"),?$"), 0.65); //individual line of json field
        SINGLE_LINE_NONCODE_PATTERNS.put(Pattern.compile("^(?i)(?:TODO|FIXME):.*$"), 0.8);
        SINGLE_LINE_NONCODE_PATTERNS.put(Pattern.compile("^NOTE:.*$"), 0.7);
    }

    private int min = 5;

    private double minConfidence = 0.5;

    private int entireCommentWeight = 15;
    private int singleLineWeight = 7;
    private int reservedWordWeight = 2;

    private final List<CommentNode> commentNodes = new ArrayList<>();

    /**
     * Sets the minimum number of successive lines of commented code before reporting
     *
     * @param min minimum number
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * Sets the minimum confidence a selection of comments should be before reporting
     *
     * @param minConfidence minimum proportion
     */
    public void setMinConfidence(double minConfidence) {
        this.minConfidence = minConfidence;
    }

    public void setEntireCommentWeight(int entireCommentWeight) {
        this.entireCommentWeight = entireCommentWeight;
    }

    public void setReservedWordWeight(int reservedWordWeight) {
        this.reservedWordWeight = reservedWordWeight;
    }

    public void setSingleLineWeight(int singleLineWeight) {
        this.singleLineWeight = singleLineWeight;
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

        String entireComment = String.join("\n", commentLines);
        double entireCommentCodeScore = maxMatcher(entireComment, ENTIRE_COMMENT_CODE_PATTERNS);
        double entireCommentNonCodeScore = maxMatcher(entireComment, ENTIRE_COMMENT_NONCODE_PATTERNS);
        double entireCommentScore = (entireCommentCodeScore - entireCommentNonCodeScore) * entireCommentWeight;

        double singleLineCodeScore = maxMatcher(commentLines, SINGLE_LINE_CODE_PATTERNS);
        double singleLineNonCodeScore = maxMatcher(commentLines, SINGLE_LINE_NONCODE_PATTERNS);
        double singleLineScore = (singleLineCodeScore - singleLineNonCodeScore) * singleLineWeight;

        String[] allWords = entireComment.toLowerCase().split("\\s+");
        long reservedWordCount = Arrays.stream(allWords).filter(JAVA_RESERVED_WORDS::contains).count();
        double reservedWordScore = Math.min(3.0 * reservedWordCount / allWords.length, 1) * reservedWordWeight;

        int totalWeight = reservedWordWeight;
        double totalScore = reservedWordScore;
        if(entireCommentScore != 0) {
            totalWeight += entireCommentWeight;
            totalScore += entireCommentScore;
        }
        if(singleLineScore != 0) {
            totalWeight += singleLineWeight;
            totalScore += singleLineScore;
        }

        double totalConfidence = totalScore / totalWeight;

        if (totalConfidence >= minConfidence) {
            log(commentNodes.getFirst().ast(), String.format("%d comment lines likely commented code (%d%% confidence)",
                    commentNodes.getLast().getLastLineNo() + 1 - commentNodes.getFirst().ast().getLineNo(),
                    Math.round(totalConfidence * 100)));
        }

        commentNodes.clear();
    }

    private double maxMatcher(String match, Map<Pattern, Double> patternScores) {
        double max = 0;
        for(var entry : patternScores.entrySet()) {
            if(entry.getValue() > max && entry.getKey().matcher(match).matches()) {
                max = entry.getValue();
            }
        }
        return max;
    }

    private double maxMatcher(List<String> matches, Map<Pattern, Double> patternScores) {
        if(matches.isEmpty()) {
            return 0;
        }

        double totalScore = 0;
        int totalNonBlankLines = 0;

        for(String match : matches) {
            if(!match.isBlank()) {
                totalScore += maxMatcher(match, patternScores);
                totalNonBlankLines++;
            }
        }
        return totalNonBlankLines == 0 ? 0 : totalScore / totalNonBlankLines;
    }

    private record CommentNode(DetailAST ast, List<String> lines) {
        int getLastLineNo() {
            return ast.getLineNo() + lines.size() - 1;
        }
    }
}
