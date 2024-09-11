package de.arbeitsagentur.opdt.keycloak.filestore;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import de.arbeitsagentur.opdt.keycloak.filestore.model.KeycloakModelTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SearchPatternsTest extends KeycloakModelTest {

    static Stream<Arguments> nullAndNotNullArguments() {
        return Stream.of(
                arguments(null, null),
                arguments(null, "not-null"),
                arguments("not-null", null)
        );
    }

    @ParameterizedTest
    @MethodSource("nullAndNotNullArguments")
    void whenLike_givenAnyNull_thenReturnFalse(String first, String second) {
        var actual = SearchPatterns.like(first, second);
        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abc-pattern",
            "abc-pattern-xyz",
            "pattern-xyz",
            "pattern"
    })
    void whenLike_givenLowerCaseMatches_thenReturnTrue(String value) {
        var actual = SearchPatterns.like(value, "%pattern%");
        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("nullAndNotNullArguments")
    void whenInsensitiveLike_givenNull_thenReturnFalse(String first, String second) {
        var actual = SearchPatterns.insensitiveLike(first, second);
        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abc-pattern",
            "abc-PATTERN",
            "abc-Pattern",
            "abc-pattern-xyz",
            "abc-Pattern-xyz",
            "abc-PATTERN-xyz",
            "pattern-xyz",
            "Pattern-xyz",
            "PATTERN-xyz",
            "pattern",
            "Pattern",
            "PATTERN"
    })
    void whenInsensitiveLike_givenMatch_thenReturnTrue(String value) {
        var actual = SearchPatterns.insensitiveLike(value, "%pattern%");
        assertThat(actual).isTrue();
    }
}
