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

package de.arbeitsagentur.opdt.keycloak.filestore.clientscope;

import de.arbeitsagentur.opdt.keycloak.filestore.SearchableModelField;
import org.keycloak.models.ClientScopeModel;

public class SearchableFields {
  public static final SearchableModelField<ClientScopeModel> ID =
      new SearchableModelField<>("id", String.class);
  public static final SearchableModelField<ClientScopeModel> REALM_ID =
      new SearchableModelField<>("realmId", String.class);
  public static final SearchableModelField<ClientScopeModel> NAME =
      new SearchableModelField<>("name", String.class);
}
