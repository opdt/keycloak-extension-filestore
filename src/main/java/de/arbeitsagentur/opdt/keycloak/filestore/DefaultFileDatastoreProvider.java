package de.arbeitsagentur.opdt.keycloak.filestore;

import org.keycloak.models.*;
import org.keycloak.storage.datastore.DefaultDatastoreProvider;

public class DefaultFileDatastoreProvider extends DefaultDatastoreProvider {
  private KeycloakSession session;

  public DefaultFileDatastoreProvider(KeycloakSession session) {
    super(null, session);
    this.session = session;
  }

  @Override
  public ClientProvider clients() {
    return session.getProvider(ClientProvider.class, "file");
  }

  @Override
  public ClientProvider clientStorageManager() {
    return clients();
  }

  @Override
  public ClientScopeProvider clientScopes() {
    return session.getProvider(ClientScopeProvider.class, "file");
  }

  @Override
  public ClientScopeProvider clientScopeStorageManager() {
    return clientScopes();
  }

  @Override
  public GroupProvider groups() {
    return session.getProvider(GroupProvider.class, "file");
  }

  @Override
  public GroupProvider groupStorageManager() {
    return groups();
  }

  @Override
  public RealmProvider realms() {
    return session.getProvider(RealmProvider.class, "file");
  }

  @Override
  public RoleProvider roles() {
    return session.getProvider(RoleProvider.class, "file");
  }

  @Override
  public RoleProvider roleStorageManager() {
    return roles();
  }
}
