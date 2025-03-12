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

package de.arbeitsagentur.opdt.keycloak.filestore.events;

import de.arbeitsagentur.opdt.keycloak.filestore.SearchableModelField;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;

public class SearchableFields {
    public static final SearchableModelField<Event> ID = new SearchableModelField<>("id", String.class);
    public static final SearchableModelField<Event> REALM_ID = new SearchableModelField<>("realmId", String.class);
    public static final SearchableModelField<Event> CLIENT_ID = new SearchableModelField<>("clientId", String.class);
    public static final SearchableModelField<Event> USER_ID = new SearchableModelField<>("userId", String.class);
    public static final SearchableModelField<Event> TIMESTAMP = new SearchableModelField<>("timestamp", Long.class);
    public static final SearchableModelField<Event> IP_ADDRESS = new SearchableModelField<>("ipAddress", String.class);
    public static final SearchableModelField<Event> EVENT_TYPE =
            new SearchableModelField<>("eventType", EventType.class);
}
