package org.keycloak.storage;

import com.google.auto.service.AutoService;
import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraDatastoreProviderFactory;
import de.arbeitsagentur.opdt.keycloak.common.ProviderHelpers;
import org.keycloak.models.KeycloakSession;

@AutoService(DatastoreProviderFactory.class)
public class BaDatastoreProviderFactory extends CassandraDatastoreProviderFactory {
    @Override
    public DatastoreProvider create(KeycloakSession session) {
        return ProviderHelpers.createProviderCached(session, BaDatastoreProvider.class, () -> new BaDatastoreProvider(session));
    }

    @Override
    public int order() {
        return super.order() + 1;
    }
}
