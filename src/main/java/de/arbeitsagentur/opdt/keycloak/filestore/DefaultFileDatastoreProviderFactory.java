package de.arbeitsagentur.opdt.keycloak.filestore;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.storage.DatastoreProvider;
import org.keycloak.storage.DatastoreProviderFactory;

@AutoService(DatastoreProviderFactory.class)
public class DefaultFileDatastoreProviderFactory implements DatastoreProviderFactory {
    @Override
    public DatastoreProvider create(KeycloakSession session) {
        return new DefaultFileDatastoreProvider(session);
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "file";
    }
}
