package de.arbeitsagentur.opdt.keycloak.filestore.group;

import de.arbeitsagentur.opdt.keycloak.filestore.SearchableModelField;
import org.keycloak.models.GroupModel;

public class SearchableFields {
  public static final SearchableModelField<GroupModel> ID =
      new SearchableModelField<>("id", String.class);
  public static final SearchableModelField<GroupModel> REALM_ID =
      new SearchableModelField<>("realmId", String.class);

  /** Parent group ID */
  public static final SearchableModelField<GroupModel> PARENT_ID =
      new SearchableModelField<>("parentGroupId", String.class);

  public static final SearchableModelField<GroupModel> NAME =
      new SearchableModelField<>("name", String.class);

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
