package edu.byu.cs240.checkstyle.util;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * Provides utility methods for searching through trees
 *
 * @author Michael Davenport
 */
public abstract class TreeUtils {

    private static final List<Integer> ROOT_TYPES =
            List.of(TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.ENUM_DEF, TokenTypes.ANNOTATION_DEF,
                    TokenTypes.RECORD_DEF);


    /**
     * Finds the name of the class/interface/etc. at the root
     *
     * @param root root of the tree
     * @return the name of the class/interface/etc. at the root
     */
    public static String getRootName(DetailAST root) {
        for (int type : ROOT_TYPES) {
            DetailAST rootToken = root.findFirstToken(type);
            if (rootToken != null) {
                return rootToken.findFirstToken(TokenTypes.IDENT).getText();
            }
        }
        return null;
    }


    /**
     * Determines if the ast node is a variable name
     *
     * @param ast ast node to check
     * @return true if the ast represents a variable name, false otherwise
     */
    public static boolean isVariableName(DetailAST ast) {
        return ast.getType() == TokenTypes.IDENT &&
                !(ast.getParent().getType() == TokenTypes.TYPE || ast.getParent().getType() == TokenTypes.METHOD_CALL ||
                        (ast.getParent().getType() == TokenTypes.DOT && ast.getParent().getFirstChild() != ast));
    }

}
