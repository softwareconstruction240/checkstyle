package edu.byu.cs240.checkstyle.util;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

/**
 * Provides utility methods for searching through trees
 *
 * @author Michael Davenport
 */
public final class TreeUtils {
    /**
     * Determines the number of nodes that are a descendent of a node.
     *
     * @param ast the parent node
     * @return the number of nodes that are a descendent of ast
     */
    public static int astComplexity(DetailAST ast) {
        int out = 1;
        DetailAST child = ast.getFirstChild();
        while (child != null) {
            out += astComplexity(child);
            child = child.getNextSibling();
        }
        return out;
    }
}
