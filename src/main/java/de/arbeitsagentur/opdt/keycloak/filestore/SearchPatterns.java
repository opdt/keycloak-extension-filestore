package de.arbeitsagentur.opdt.keycloak.filestore;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class SearchPatterns {

    private SearchPatterns() {
        // static utility functions
    }

    private static final Pattern LIKE_PATTERN_DELIMITER = Pattern.compile("%+");

    static String quoteRegex(String pattern) {
        return LIKE_PATTERN_DELIMITER
                .splitAsStream(pattern)
                .map(Pattern::quote)
                .collect(Collectors.joining(".*"))
                + (pattern.endsWith("%") ? ".*" : "");
    }

    public static boolean like(String toCompareWith, String sValue) {
        if (toCompareWith == null || sValue == null) {
            return false;
        }
        // TODO simplify ?
        // return toCompareWith.contains(sValue);

        if (Pattern.matches("^%+$", sValue)) {
            return true;
        }
        Pattern pValue = Pattern.compile(quoteRegex(sValue), Pattern.DOTALL);
        return pValue.matcher(toCompareWith).matches();
    }

    public static boolean insensitiveLike(String toCompareWith, String sValue) {
        if (toCompareWith == null || sValue == null) {
            return false;
        }

        // TODO simplify ?
        // return toCompareWith.toLowerCase().contains(sValue.toLowerCase());

        if (Pattern.matches("^%+$", sValue)) {
            return true;
        }
        int flags = Pattern.CASE_INSENSITIVE + Pattern.DOTALL;
        Pattern pValue = Pattern.compile(quoteRegex(sValue), flags);
        return pValue.matcher(toCompareWith).matches();
    }
}
