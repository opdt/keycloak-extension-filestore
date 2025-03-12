/*
 * Copyright 2024. IT-Systemhaus der Bundesagentur fuer Arbeit
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package de.arbeitsagentur.opdt.keycloak.filestore;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class SearchPatterns {

    private SearchPatterns() {
        // static utility functions
    }

    private static final Pattern LIKE_PATTERN_DELIMITER = Pattern.compile("%+");

    static String quoteRegex(String pattern) {
        return LIKE_PATTERN_DELIMITER.splitAsStream(pattern).map(Pattern::quote).collect(Collectors.joining(".*"))
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
