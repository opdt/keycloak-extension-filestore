/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.arbeitsagentur.opdt.keycloak.filestore.role;

import static org.keycloak.common.util.StackUtil.getShortStackTrace;

import java.util.*;
import java.util.stream.Stream;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.utils.KeycloakModelUtils;

public class FileRoleAdapter extends AbstractRoleModel<FileRoleEntity> implements RoleModel {

  private static final Logger LOG = Logger.getLogger(FileRoleAdapter.class);

  public FileRoleAdapter(KeycloakSession session, RealmModel realm, FileRoleEntity entity) {
    super(session, realm, entity);
  }

  @Override
  public String getId() {
    return entity.getId();
  }

  @Override
  public String getName() {
    return entity.getName();
  }

  @Override
  public String getDescription() {
    return entity.getDescription();
  }

  @Override
  public void setDescription(String description) {
    entity.setDescription(description);
  }

  @Override
  public void setName(String name) {
    entity.setName(name);
  }

  @Override
  public boolean isComposite() {
    return !(entity.getCompositeRoles() == null || entity.getCompositeRoles().isEmpty());
  }

  @Override
  public Stream<RoleModel> getCompositesStream() {
    List<String> compositeRoles =
        entity.getCompositeRoles() == null ? Collections.emptyList() : entity.getCompositeRoles();
    LOG.tracef(
        "%% %s(%s).getCompositesStream():%d - %s",
        entity.getName(), entity.getId(), compositeRoles.size(), getShortStackTrace());
    return compositeRoles.stream()
        .map(uuid -> session.roles().getRoleById(realm, uuid))
        .filter(Objects::nonNull);
  }

  @Override
  public Stream<RoleModel> getCompositesStream(String search, Integer first, Integer max) {
    List<String> compositeRoles =
        entity.getCompositeRoles() == null ? Collections.emptyList() : entity.getCompositeRoles();
    LOG.tracef(
        "%% (%s).getCompositesStream(%s, %d, %d):%d - %s",
        this, search, first, max, compositeRoles.size(), getShortStackTrace());
    return session.roles().getRolesStream(realm, compositeRoles.stream(), search, first, max);
  }

  @Override
  public void addCompositeRole(RoleModel role) {
    LOG.tracef(
        "(%s).addCompositeRole(%s(%s))%s",
        this, role.getName(), role.getId(), getShortStackTrace());
    entity.addCompositeRole(role.getId());
  }

  @Override
  public void removeCompositeRole(RoleModel role) {
    LOG.tracef(
        "(%s).removeCompositeRole(%s(%s))%s",
        this, role.getName(), role.getId(), getShortStackTrace());
    entity.removeCompositeRole(role.getId());
  }

  @Override
  public boolean isClientRole() {
    return entity.getClientId() != null;
  }

  @Override
  public String getContainerId() {
    return isClientRole() ? entity.getClientId() : entity.getRealmId();
  }

  @Override
  public RoleContainerModel getContainer() {
    return isClientRole() ? session.clients().getClientById(realm, entity.getClientId()) : realm;
  }

  @Override
  public void setAttribute(String name, List<String> values) {
    entity.setAttribute(name, values);
  }

  @Override
  public void setSingleAttribute(String name, String value) {
    setAttribute(name, Collections.singletonList(value));
  }

  @Override
  public void removeAttribute(String name) {
    entity.removeAttribute(name);
  }

  @Override
  public Map<String, List<String>> getAttributes() {
    Map<String, List<String>> attributes = entity.getMultiValuedAttributes();
    return attributes == null ? Collections.emptyMap() : attributes;
  }

  @Override
  public boolean hasRole(RoleModel role) {
    return this.equals(role) || KeycloakModelUtils.searchFor(role, this, new HashSet<>());
  }

  @Override
  public Stream<String> getAttributeStream(String name) {
    return getAttributes().getOrDefault(name, Collections.EMPTY_LIST).stream();
  }

  @Override
  public String toString() {
    return String.format("%s@%08x", getName(), System.identityHashCode(this));
  }
}
