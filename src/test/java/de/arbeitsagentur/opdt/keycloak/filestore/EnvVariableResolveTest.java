package de.arbeitsagentur.opdt.keycloak.filestore;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import de.arbeitsagentur.opdt.keycloak.filestore.model.KeycloakModelTest;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class EnvVariableResolveTest extends KeycloakModelTest {

    private final Path MASTER_REALM_FILE = Paths.get(TEST_FILESTORE_DIR, "master.yaml");
    private byte[] MASTER_REALM_FILE_BACKUP;

    @BeforeEach
    void backupFile() throws IOException {
        MASTER_REALM_FILE_BACKUP = Files.readAllBytes(MASTER_REALM_FILE);
    }

    @AfterEach
    void restoreFile() throws IOException {
        Files.write(MASTER_REALM_FILE, MASTER_REALM_FILE_BACKUP);
    }

    // Single test since var subst is done during static initializer and would be hard to handle in multiple tests
    @Test
    void givenEnvVariable_thenVariableResolutionIsCorrect() throws Exception {
        Consumer<KeycloakSession> assertThatVariableIsResolved = session -> {
            RealmModel realm = session.realms().getRealm("master");
            assertThat(realm.getAccountTheme())
                    .isEqualTo("i am resolved!");
            assertThat(realm.getAdminTheme())
                    .isEqualTo("${SHOULD_NOT_BE_RESOLVED}");
            assertThat(realm.getEmailTheme())
                    .isEqualTo("defaultValue");
        };
        EnvironmentVariables env = new EnvironmentVariables("SHOULD_BE_RESOLVED", "i am resolved!");
        env.execute(() -> inCommittedTransaction(assertThatVariableIsResolved));
    }
}
