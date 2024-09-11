package de.arbeitsagentur.opdt.keycloak.filestore.events;

import org.keycloak.events.Event;
import org.keycloak.events.EventType;
import de.arbeitsagentur.opdt.keycloak.filestore.SearchableModelField;

public class SearchableFields {
    public static final SearchableModelField<Event> ID = new SearchableModelField<>("id", String.class);
    public static final SearchableModelField<Event> REALM_ID = new SearchableModelField<>("realmId", String.class);
    public static final SearchableModelField<Event> CLIENT_ID = new SearchableModelField<>("clientId", String.class);
    public static final SearchableModelField<Event> USER_ID = new SearchableModelField<>("userId", String.class);
    public static final SearchableModelField<Event> TIMESTAMP = new SearchableModelField<>("timestamp", Long.class);
    public static final SearchableModelField<Event> IP_ADDRESS = new SearchableModelField<>("ipAddress", String.class);
    public static final SearchableModelField<Event> EVENT_TYPE = new SearchableModelField<>("eventType", EventType.class);
}
