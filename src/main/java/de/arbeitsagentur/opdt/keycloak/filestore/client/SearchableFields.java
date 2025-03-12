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

package de.arbeitsagentur.opdt.keycloak.filestore.client;

import de.arbeitsagentur.opdt.keycloak.filestore.SearchableModelField;
import org.keycloak.models.ClientModel;

public class SearchableFields {
    public static final SearchableModelField<ClientModel> ID = new SearchableModelField<>("id", String.class);
    public static final SearchableModelField<ClientModel> REALM_ID =
            new SearchableModelField<>("realmId", String.class);
    public static final SearchableModelField<ClientModel> CLIENT_ID =
            new SearchableModelField<>("clientId", String.class);
    public static final SearchableModelField<ClientModel> ENABLED =
            new SearchableModelField<>("enabled", Boolean.class);
    public static final SearchableModelField<ClientModel> SCOPE_MAPPING_ROLE =
            new SearchableModelField<>("scopeMappingRole", String.class);
    public static final SearchableModelField<ClientModel> ALWAYS_DISPLAY_IN_CONSOLE =
            new SearchableModelField<>("alwaysDisplayInConsole", Boolean.class);

    /**
     * Search for attribute value. The parameters is a pair {@code (attribute_name, value)} where
     * {@code attribute_name} is always checked for equality, and the value is checked per the
     * operator.
     */
    public static final SearchableModelField<ClientModel> ATTRIBUTE =
            new SearchableModelField<>("attribute", String[].class);
}
