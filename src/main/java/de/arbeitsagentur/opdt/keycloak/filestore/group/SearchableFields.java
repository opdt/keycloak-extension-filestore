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

package de.arbeitsagentur.opdt.keycloak.filestore.group;

import de.arbeitsagentur.opdt.keycloak.filestore.SearchableModelField;
import org.keycloak.models.GroupModel;

public class SearchableFields {
    public static final SearchableModelField<GroupModel> ID = new SearchableModelField<>("id", String.class);
    public static final SearchableModelField<GroupModel> REALM_ID = new SearchableModelField<>("realmId", String.class);

    /** Parent group ID */
    public static final SearchableModelField<GroupModel> PARENT_ID =
            new SearchableModelField<>("parentGroupId", String.class);

    public static final SearchableModelField<GroupModel> NAME = new SearchableModelField<>("name", String.class);

    /**
     * Field for comparison with roles granted to this group. A role can be checked for belonging only
     * via EQ operator. Role is referred by their ID
     */
    public static final SearchableModelField<GroupModel> ASSIGNED_ROLE =
            new SearchableModelField<>("assignedRole", String.class);

    /**
     * Search for attribute value. The parameters is a pair {@code (attribute_name, values...)} where
     * {@code attribute_name} is always checked for equality, and the value is checked per the
     * operator.
     */
    public static final SearchableModelField<GroupModel> ATTRIBUTE =
            new SearchableModelField<>("attribute", String[].class);
}
