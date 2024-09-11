package de.arbeitsagentur.opdt.keycloak.filestore.clientscope;

import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;
import de.arbeitsagentur.opdt.keycloak.filestore.model.KeycloakModelTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class FileClientScopeAdapterTest extends KeycloakModelTest {

    private static final String REALM_ID = "zoo";

    @Override
    protected void createEnvironment(KeycloakSession s) {
        s.realms().createRealm(REALM_ID);
    }

    @Override
    protected void cleanEnvironment(KeycloakSession s) {
        s.realms().removeRealm(REALM_ID);
    }

    @Test
    void whenSetName_thenGet() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clientScopes, (clients, realm) -> {
            var sut = clients.addClientScope(realm, "gorilla");
            sut.setName("godzilla minus one");
            assertThat(sut.getName()).isEqualTo("godzilla_minus_one");
        });
    }

    @Test
    void whenSetDescription_thenGet() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clientScopes, (clients, realm) -> {
            var sut = clients.addClientScope(realm, "gorilla");
            sut.setDescription("Large monkey");
            assertThat(sut.getDescription()).isEqualTo("Large monkey");
        });
    }

    @Test
    void whenSetAttributes_givenUnknownKey_thenNull() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clientScopes, (clients, realm) -> {
            var sut = clients.addClientScope(realm, "gorilla");
            assertThat(sut.getAttribute("unknown")).isNull();
        });
    }

    @Test
    void whenSetAttributes_thenGet() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clientScopes, (clients, realm) -> {
            var sut = clients.addClientScope(realm, "gorilla");
            sut.setAttribute("color", "silver");
            assertThat(sut.getAttribute("color")).isEqualTo("silver");
        });
    }

    @Test
    void whenRemoveAttributes_givenNoAttr_thenNothingHappens() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clientScopes, (clients, realm) -> {
            var sut = clients.addClientScope(realm, "gorilla");
            // Act & Assert
            assertThatNoException()
                    .isThrownBy(() -> sut.removeAttribute("color"));
        });
    }

    @Test
    void whenRemoveAttributes_thenNull() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clientScopes, (clients, realm) -> {
            var sut = clients.addClientScope(realm, "gorilla");
            sut.setAttribute("color", "silver");
            // Act
            sut.removeAttribute("color");
            // Assert
            assertThat(sut.getAttribute("color")).isNull();
        });
    }

    @Test
    void whenGetAttributes_givenNoAttributes_thenReturnEmptyStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clientScopes, (clients, realm) -> {
            var sut = clients.addClientScope(realm, "giraffe");
            // Act
            var actual = sut.getAttributes();
            // Assert
            assertThat(actual).isEmpty();
        });
    }

    @Test
    void whenGetAttributes_givenAttributes_thenReturnStream() {
        withRealmAndProvider(REALM_ID, KeycloakSession::clientScopes, (clients, realm) -> {
            var sut = clients.addClientScope(realm, "giraffe");
            sut.setAttribute("height", "tall");
            sut.setAttribute("color", "yellow-brown");
            // Act
            var actual = sut.getAttributes();
            // Assert
            assertThat(actual)
                    .hasSize(2)
                    .contains(
                            Map.entry("height", "tall"),
                            Map.entry("color", "yellow-brown")
                    );
        });
    }
}
