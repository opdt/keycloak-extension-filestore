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

package de.arbeitsagentur.opdt.keycloak.filestore.role;

import de.arbeitsagentur.opdt.keycloak.filestore.SearchableModelField;
import org.keycloak.models.RoleModel;

public class SearchableFields {
  public static final SearchableModelField<RoleModel> ID =
      new SearchableModelField<>("id", String.class);
  public static final SearchableModelField<RoleModel> REALM_ID =
      new SearchableModelField<>("realmId", String.class);

  /** If client role, ID of the client (not the clientId) */
  public static final SearchableModelField<RoleModel> CLIENT_ID =
      new SearchableModelField<>("clientId", String.class);

  public static final SearchableModelField<RoleModel> NAME =
      new SearchableModelField<>("name", String.class);
  public static final SearchableModelField<RoleModel> DESCRIPTION =
      new SearchableModelField<>("description", String.class);
  public static final SearchableModelField<RoleModel> COMPOSITE_ROLE =
      new SearchableModelField<>("compositeRoles", Boolean.class);
}
