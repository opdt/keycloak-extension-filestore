package de.arbeitsagentur.opdt.keycloak.filestore.identityProvider;

import static org.keycloak.userprofile.DeclarativeUserProfileProviderFactory.PROVIDER_PRIORITY;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.IdentityProviderStorageProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(IdentityProviderStorageProviderFactory.class)
public class FileIdentityProviderStorageProviderFactory
    implements IdentityProviderStorageProviderFactory<FileIdentityProviderStorageProvider> {
  @Override
  public FileIdentityProviderStorageProvider create(KeycloakSession session) {
    return new FileIdentityProviderStorageProvider(session);
  }

  @Override
  public void init(Config.Scope config) {}

  @Override
  public void postInit(KeycloakSessionFactory factory) {}

  @Override
  public void close() {}

  @Override
  public String getId() {
    return "file";
  }

  @Override
  public int order() {
    return PROVIDER_PRIORITY + 1;
  }
}
