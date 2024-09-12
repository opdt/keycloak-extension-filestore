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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

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

  // Single test since var subst is done during static initializer and would be hard to handle in
  // multiple tests
  @Test
  void givenEnvVariable_thenVariableResolutionIsCorrect() throws Exception {
    Consumer<KeycloakSession> assertThatVariableIsResolved =
        session -> {
          RealmModel realm = session.realms().getRealm("master");
          assertThat(realm.getAccountTheme()).isEqualTo("i am resolved!");
          assertThat(realm.getAdminTheme()).isEqualTo("${SHOULD_NOT_BE_RESOLVED}");
          assertThat(realm.getEmailTheme()).isEqualTo("defaultValue");
        };
    EnvironmentVariables env = new EnvironmentVariables("SHOULD_BE_RESOLVED", "i am resolved!");
    env.execute(() -> inCommittedTransaction(assertThatVariableIsResolved));
  }
}
