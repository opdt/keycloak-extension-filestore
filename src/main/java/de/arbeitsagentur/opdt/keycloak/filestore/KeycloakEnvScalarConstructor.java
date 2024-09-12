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

import java.util.Collection;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.env.EnvScalarConstructor;

/** Adjustment for Keycloak: Return original expression if no value is found in the environment */
public class KeycloakEnvScalarConstructor extends EnvScalarConstructor {
  public KeycloakEnvScalarConstructor(
      TypeDescription theRoot, Collection<TypeDescription> moreTDs, LoaderOptions loadingConfig) {
    super(theRoot, moreTDs, loadingConfig);
  }

  public String apply(String name, String separator, String value, String environment) {
    String result = super.apply(name, separator, value, environment);
    if (result == null || result.isEmpty()) {
      // ADJUSTMENT: Change from default behavior: Return variable expression for Keycloak to
      // substitute
      return "${" + name + "}";
    }
    return result;
  }
}
