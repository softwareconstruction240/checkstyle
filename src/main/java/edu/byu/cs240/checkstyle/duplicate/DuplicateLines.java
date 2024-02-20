package edu.byu.cs240.checkstyle.duplicate;

import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;

import java.io.File;
import java.util.*;

/**
 * Checks for exactly duplicated lines
 *
 * @author Michael Davenport
 */
public class DuplicateLines extends AbstractFileSetCheck {

    private static final long IGNORE = Long.MIN_VALUE;

    private int min = 10;


    public void setMin(int min) {
        this.min = min;
    }


    private final Set<FileStorage> files = new HashSet<>();


    /**
     * Stores the contents of a file and checks it for duplicates against stored files
     *
     * @param file     file object representing file we're looking at
     * @param fileText contents of the file
     */
    @Override
    protected void processFiltered(File file, FileText fileText) {
        List<String> lines = Arrays.asList(fileText.toLinesArray());
        lines.replaceAll(String::trim);
        List<Long> hashedLines = new ArrayList<>();
        for (String line : lines) {
            if (isIgnored(line)) hashedLines.add(IGNORE);
            else hashedLines.add((long) line.hashCode());
        }
        List<Long> hashedBlocks = hashBlocks(hashedLines);
        FileStorage added = new FileStorage(file, fileText, lines, hashedLines, hashedBlocks);
        files.add(added);

        for (FileStorage f : files) {
            searchDuplicates(f, added);
        }
    }


    /**
     * Determines if a line of code should be ignored for duplicates.
     * Ignores import statements and javadoc comments
     *
     * @param line line of text to check
     * @return true if the line should be ignored, false otherwise
     */
    private boolean isIgnored(String line) {
        return line.startsWith("import") || line.startsWith("/**") || line.startsWith("*");
    }


    /**
     * Compiles a list of hashes of blocks of code by line
     *
     * @param hashedLines hashes of each line of code
     * @return a list of hashes of blocks
     */
    private List<Long> hashBlocks(List<Long> hashedLines) {
        List<Long> hashes = new ArrayList<>();
        if (hashedLines.size() < min) return hashes;

        for (int i = 0; i <= hashedLines.size() - min; i++) {
            long sum = 0;
            for (int j = i; j < i + min; j++) {
                if (hashedLines.get(j) == IGNORE) {
                    sum = IGNORE;
                    break;
                }
                sum += hashedLines.get(j);
            }
            hashes.add(sum);
        }
        return hashes;
    }


    /**
     * Searches for duplicate blocks of code; verifies and reports them if found.
     *
     * @param original original file to compare against
     * @param compare  file being compared to original
     */
    private void searchDuplicates(FileStorage original, FileStorage compare) {
        List<Long> originalHashedBlocks = original.hashedBlocks();
        List<Long> compareHashedBlocks = compare.hashedBlocks();
        for (int i = 0; i < originalHashedBlocks.size(); i++) {
            for (int j = 0; j < compareHashedBlocks.size(); j++) {
                if (original == compare && Math.abs(i - j) < min) continue;
                if (originalHashedBlocks.get(i) == IGNORE) continue;
                if (Objects.equals(originalHashedBlocks.get(i), compareHashedBlocks.get(j))) {
                    int searched = verifyDuplicates(original, compare, i, j);
                    i += searched;
                    j += searched;
                    if (i >= originalHashedBlocks.size()) break;
                }
            }
        }
    }


    /**
     * Verifies line duplication and reports found duplicates
     *
     * @param original  base file to compare against
     * @param compare   file being compared to original
     * @param origStart starting line number of duplication in original
     * @param compStart starting line number of duplication in compare
     * @return the number of duplicated lines
     */
    private int verifyDuplicates(FileStorage original, FileStorage compare, int origStart, int compStart) {
        List<String> originalLines = original.lines();
        List<String> compareLines = compare.lines();
        int i = 0;
        int duplicatedLines = 0;
        while (origStart + i < originalLines.size() && compStart + i < compareLines.size() &&
                originalLines.get(origStart + i).equals(compareLines.get(compStart + i))) {
            if (isIgnored(originalLines.get(origStart + i))) break;
            i++;
            if (!originalLines.get(origStart + i).isBlank()) duplicatedLines++;
        }
        if (duplicatedLines >= min) {
            log(compStart + 1,
                    String.format("Duplicated %d lines from %s:%d", i, original.file().toPath(), origStart + 1));
            fireErrors(compare.file().toPath().toString());
            for (int j = 0; j <= i - min; j++) {
                compare.hashedBlocks().set(compStart + j, IGNORE);
            }
        }
        return i;
    }


    /**
     * Record to keep all information about a file central
     *
     * @param file         file object
     * @param fileText     text of the file
     * @param lines        trimmed text of the file in a list
     * @param hashedLines  hashed value of each line
     * @param hashedBlocks hashed value of each block
     */
    private record FileStorage(File file, FileText fileText, List<String> lines, List<Long> hashedLines,
                               List<Long> hashedBlocks) {}

}
