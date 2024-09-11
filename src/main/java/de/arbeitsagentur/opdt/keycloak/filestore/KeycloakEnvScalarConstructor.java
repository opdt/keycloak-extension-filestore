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
