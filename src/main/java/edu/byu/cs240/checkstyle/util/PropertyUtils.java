package edu.byu.cs240.checkstyle.util;

import java.util.Collection;

/**
 * Utility methods for dealing with check properties
 *
 * @author Michael Davenport
 */
public class PropertyUtils {

    /**
     * Replaces the contents of a collection with the contents of a delimited list of strings
     *
     * @param parse string to parse
     * @param coll  collection to replace
     * @param delim delimiting string
     */
    public static void parseString(String parse, Collection<String> coll, String delim) {
        coll.clear();
        String[] tokens = parse.split(delim);
        for (String token : tokens) {
            String toAdd = token.trim();
            if (!toAdd.isBlank()) coll.add(toAdd);
        }
    }

}
