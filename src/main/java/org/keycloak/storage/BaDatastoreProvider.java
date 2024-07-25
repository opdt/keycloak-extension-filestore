package org.keycloak.storage;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraDatastoreProvider;
import org.keycloak.models.*;

public class BaDatastoreProvider extends CassandraDatastoreProvider {
    private static final boolean baDatastoreEnabled;

    static {
        baDatastoreEnabled = Boolean.parseBoolean(System.getenv("KC_COMMUNITY_DATASTORE_BA_ENABLED"));
    }

    private KeycloakSession session;

    public BaDatastoreProvider(KeycloakSession session) {
        super(session);
        this.session = session;
    }

    @Override
    public ClientProvider clients() {
        if (!baDatastoreEnabled) {
            return super.clients();
        }
        return session.getProvider(ClientProvider.class, "file");
    }

    @Override
    public ClientScopeProvider clientScopes() {
        if (!baDatastoreEnabled) {
            return super.clientScopes();
        }
        return session.getProvider(ClientScopeProvider.class, "file");
    }

    @Override
    public GroupProvider groups() {
        if (!baDatastoreEnabled) {
            return super.groups();
        }
        return session.getProvider(GroupProvider.class, "file");
    }

    @Override
    public RealmProvider realms() {
        if (!baDatastoreEnabled) {
            return super.realms();
        }
        return session.getProvider(RealmProvider.class, "file");
    }

    @Override
    public RoleProvider roles() {
        if (!baDatastoreEnabled) {
            return super.roles();
        }
        return session.getProvider(RoleProvider.class, "file");
    }
}
