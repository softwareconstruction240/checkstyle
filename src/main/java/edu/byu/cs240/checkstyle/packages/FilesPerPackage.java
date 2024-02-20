package edu.byu.cs240.checkstyle.packages;

import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;
import edu.byu.cs240.checkstyle.util.PropertyUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Finds packages that have too many files in them.
 *
 * @author Michael Davenport
 */
public class FilesPerPackage extends AbstractFileSetCheck {

    private final Map<String, Integer> packageSizes = new HashMap<>();

    private final Set<String> excludedFiles = new HashSet<>(Set.of("package-info.java", "module-info.java"));

    private int max = 10;


    /**
     * Sets the maximum number of files in a package
     *
     * @param max maximum number of files
     */
    public void setMax(int max) {
        this.max = max;
    }


    /**
     * Sets a list of file names that can be excluded
     *
     * @param parse a comma-delimited list of file names
     */
    public void setExcludedFiles(String parse) {
        PropertyUtils.parseString(parse, excludedFiles, ",");
    }


    /**
     * Finds the package path the file and updates packageSizes accordingly
     *
     * @param file     file object representing file we're looking at
     * @param fileText contents of the file
     */
    @Override
    protected void processFiltered(File file, FileText fileText) {
        if (excludedFiles.contains(file.getName())) return;
        String path = file.getPath();
        int fileStart = path.lastIndexOf('/');
        if (fileStart == -1) fileStart = path.lastIndexOf('\\');
        if (fileStart != -1) {
            String packagePath = path.substring(0, fileStart);
            if (packageSizes.containsKey(packagePath)) {
                packageSizes.put(packagePath, packageSizes.get(packagePath) + 1);
            } else {
                packageSizes.put(packagePath, 1);
            }
        }
    }


    /**
     * Reports packages that contain too many files
     */
    @Override
    public void finishProcessing() {
        packageSizes.forEach((key, value) -> {
            if (value > max) {
                log(0, String.format("Package has too many files: %d (max is %d)", value, max));
                fireErrors(key);
            }
        });
    }

}
