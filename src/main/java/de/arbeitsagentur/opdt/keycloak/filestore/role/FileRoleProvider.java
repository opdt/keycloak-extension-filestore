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

import static de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractFileProviderFactory.MapProviderObjectType.ROLE_AFTER_REMOVE;
import static de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractFileProviderFactory.MapProviderObjectType.ROLE_BEFORE_REMOVE;
import static org.keycloak.common.util.StackUtil.getShortStackTrace;
import static org.keycloak.utils.StreamsUtil.paginatedStream;

import de.arbeitsagentur.opdt.keycloak.filestore.SearchPatterns;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jboss.logging.Logger;
import org.keycloak.models.*;

public class FileRoleProvider implements RoleProvider {

  private static final Logger LOG = Logger.getLogger(FileRoleProvider.class);
  private final KeycloakSession session;

  public FileRoleProvider(KeycloakSession session) {
    this.session = session;
  }

  private Function<FileRoleEntity, RoleModel> entityToAdapterFunc(RealmModel realm) {
    // Clone entity before returning back, to avoid giving away a reference to the live object to
    // the caller
    return origEntity -> new FileRoleAdapter(session, realm, origEntity);
  }

  @Override
  public RoleModel addRealmRole(RealmModel realm, String id, String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Role name cannot be null or blank");
    }

    String roleId = id == null ? name : id;
    if (getRealmRole(realm, name) != null) {
      throw new ModelDuplicateException(
          "Role with the same name exists: " + name + " for realm " + realm.getName());
    }

    if (FileRoleStore.exists(roleId, realm.getId())) {
      throw new ModelDuplicateException("Role exists: " + id);
    }

    LOG.tracef("addRealmRole(%s, %s, %s)%s", realm, id, name, getShortStackTrace());
    FileRoleEntity entity = new FileRoleEntity();
    entity.setId(roleId);
    entity.setRealmId(realm.getId());
    entity.setName(name);
    FileRoleStore.update(entity);
    return entityToAdapterFunc(realm).apply(entity);
  }

  @Override
  public Stream<RoleModel> getRealmRolesStream(RealmModel realm) {
    return getRealmRolesStream(realm, null, null);
  }

  @Override
  public Stream<RoleModel> getRealmRolesStream(RealmModel realm, Integer first, Integer max) {
    Stream<RoleModel> rolesStream =
        FileRoleStore.readAll().stream()
            .filter(e -> realm.getId().equals(e.getRealmId()))
            .filter(e -> e.getClientId() == null)
            .map(entityToAdapterFunc(realm))
            .sorted(Comparator.comparing(RoleModel::getName));
    return paginatedStream(rolesStream, first, max);
  }

  @Override
  public Stream<RoleModel> getRolesStream(
      RealmModel realm, Stream<String> ids, String search, Integer first, Integer max) {
    LOG.tracef(
        "getRolesStream(%s, %s, %s, %d, %d)%s",
        realm, ids, search, first, max, getShortStackTrace());

    Stream<RoleModel> roleStream =
        ids.filter(id -> FileRoleStore.exists(id, realm.getId()))
            .map(id -> FileRoleStore.read(id, realm.getId()))
            .map(entityToAdapterFunc(realm));
    if (search != null) {
      return roleStream.filter(
          entity -> SearchPatterns.insensitiveLike(entity.getName(), "%" + search + "%"));
    }
    return roleStream;
  }

  @Override
  public RoleModel addClientRole(ClientModel client, String id, String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Role name cannot be null or blank");
    }

    if (getClientRole(client, name) != null) {
      throw new ModelDuplicateException(
          "Role with the same name exists: " + name + " for client " + client.getClientId());
    }

    final RealmModel realm = client.getRealm();

    String entityId = (id == null) ? name : id;
    if (client.getClientId() != null) {
      entityId = client.getClientId() + ":" + entityId;
    }

    if (realm.getId() != null && FileRoleStore.exists(entityId, realm.getId())) {
      throw new ModelDuplicateException("Role exists: " + entityId);
    }

    LOG.tracef("addClientRole(%s, %s, %s)%s", client, entityId, name, getShortStackTrace());

    FileRoleEntity entity = new FileRoleEntity();
    entity.setId(entityId);
    entity.setRealmId(realm.getId());
    entity.setName(name);
    entity.setClientId(client.getId());

    FileRoleStore.update(entity);
    return entityToAdapterFunc(realm).apply(entity);
  }

  @Override
  public Stream<RoleModel> getClientRolesStream(ClientModel client, Integer first, Integer max) {
    final RealmModel realm = client.getRealm();

    Stream<RoleModel> rolesStream =
        FileRoleStore.readAll().stream()
            .filter(e -> realm.getId().equals(e.getRealmId()))
            .filter(entity -> client.getId().equals(entity.getClientId()))
            .map(entityToAdapterFunc(realm))
            .sorted(Comparator.comparing(RoleModel::getName));
    return paginatedStream(rolesStream, first, max);
  }

  @Override
  public Stream<RoleModel> getClientRolesStream(ClientModel client) {
    return getClientRolesStream(client, null, null);
  }

  @Override
  public boolean removeRole(RoleModel role) {
    LOG.tracef("removeRole(%s)%s", role, getShortStackTrace());

    RealmModel realm =
        role.isClientRole()
            ? ((ClientModel) role.getContainer()).getRealm()
            : (RealmModel) role.getContainer();

    session.invalidate(ROLE_BEFORE_REMOVE, realm, role);
    FileRoleStore.deleteById(role.getId(), realm.getId());
    session.invalidate(ROLE_AFTER_REMOVE, realm, role);
    return true;
  }

  @Override
  public void removeRoles(RealmModel realm) {
    getRealmRolesStream(realm).forEach(this::removeRole);
  }

  @Override
  public void removeRoles(ClientModel client) {
    getClientRolesStream(client).forEach(this::removeRole);
  }

  @Override
  public RoleModel getRealmRole(RealmModel realm, String name) {
    if (name == null || name.isBlank()) {
      return null;
    }

    LOG.tracef("getRealmRole(%s, %s)%s", realm, name, getShortStackTrace());
    return FileRoleStore.readAll().stream()
        .filter(e -> realm.getId().equals(e.getRealmId()))
        .filter(role -> role.getName().equals(name))
        .filter(role -> role.getClientId() == null)
        .map(entityToAdapterFunc(realm))
        .findFirst()
        .orElse(null);
  }

  @Override
  public RoleModel getClientRole(ClientModel client, String name) {
    if (name == null || name.isBlank()) {
      return null;
    }

    LOG.tracef("getClientRole(%s, %s)%s", client, name, getShortStackTrace());
    final RealmModel realm = client.getRealm();
    return FileRoleStore.readAll().stream()
        .filter(e -> realm.getId().equals(e.getRealmId()))
        .filter(role -> client.getId().equals(role.getClientId()))
        .filter(role -> name.equals(role.getName()))
        .map(entityToAdapterFunc(realm))
        .findFirst()
        .orElse(null);
  }

  @Override
  public RoleModel getRoleById(RealmModel realm, String id) {
    if (realm == null || realm.getId() == null || id == null || id.isBlank()) {
      return null;
    }

    LOG.tracef("getRoleById(%s, %s)%s", realm, id, getShortStackTrace());
    FileRoleEntity entity = FileRoleStore.read(id, realm.getId());
    String realmId = realm.getId();
    // when a store doesn't store information about all realms, it doesn't have the information
    // about
    return (entity == null
            || (entity.getRealmId() != null && !Objects.equals(realmId, entity.getRealmId())))
        ? null
        : entityToAdapterFunc(realm).apply(entity);
  }

  @Override
  public Stream<RoleModel> searchForRolesStream(
      RealmModel realm, String search, Integer first, Integer max) {
    if (search == null) {
      return Stream.empty();
    }

    Stream<RoleModel> roleStream =
        FileRoleStore.readAll().stream()
            .filter(e -> realm.getId().equals(e.getRealmId()))
            .filter(role -> role.getClientId() == null)
            .map(entityToAdapterFunc(realm))
            .sorted(Comparator.comparing(RoleModel::getName));

    String searchPattern = "%" + search + "%";

    if (!search.isBlank()) {
      roleStream =
          roleStream.filter(
              entity ->
                  SearchPatterns.insensitiveLike(entity.getName(), searchPattern)
                      || SearchPatterns.insensitiveLike(entity.getDescription(), searchPattern));
    }
    return paginatedStream(roleStream, first, max);
  }

  @Override
  public Stream<RoleModel> searchForClientRolesStream(
      ClientModel client, String search, Integer first, Integer max) {
    if (search == null) {
      return Stream.empty();
    }

    final RealmModel realm = client.getRealm();
    Stream<RoleModel> roleStream =
        FileRoleStore.readAll().stream()
            .filter(e -> realm.getId().equals(e.getRealmId()))
            .filter(role -> client.getId().equals(role.getClientId()))
            .map(entityToAdapterFunc(realm))
            .sorted(Comparator.comparing(RoleModel::getName));

    String searchPattern = "%" + search + "%";

    if (!search.isBlank()) {
      roleStream =
          roleStream.filter(
              entity ->
                  SearchPatterns.insensitiveLike(entity.getName(), searchPattern)
                      || SearchPatterns.insensitiveLike(entity.getDescription(), searchPattern));
    }
    return paginatedStream(roleStream, first, max);
  }

  @Override
  public Stream<RoleModel> searchForClientRolesStream(
      RealmModel realm, Stream<String> ids, String search, Integer first, Integer max) {
    if (search == null) {
      return Stream.empty();
    }

    Stream<RoleModel> roleStream =
        ids.filter(id -> FileRoleStore.exists(id, realm.getId()))
            .map(id -> FileRoleStore.read(id, realm.getId()))
            .map(entityToAdapterFunc(realm))
            .sorted(Comparator.comparing(RoleModel::getName));

    String searchPattern = "%" + search + "%";

    if (!search.isBlank()) {
      roleStream =
          roleStream.filter(
              entity ->
                  SearchPatterns.insensitiveLike(entity.getName(), searchPattern)
                      || SearchPatterns.insensitiveLike(entity.getDescription(), searchPattern));
    }
    return paginatedStream(roleStream, first, max);
  }

  @Override
  public Stream<RoleModel> searchForClientRolesStream(
      RealmModel realm, String search, Stream<String> excludedIds, Integer first, Integer max) {
    if (search == null) {
      return Stream.empty();
    }

    List<String> excludedIdsList = excludedIds.toList();
    Stream<RoleModel> roleStream =
        FileRoleStore.readAll().stream()
            .filter(e -> realm.getId().equals(e.getRealmId()))
            .filter(role -> !excludedIdsList.contains(role.getId()))
            .map(entityToAdapterFunc(realm))
            .sorted(Comparator.comparing(RoleModel::getName));

    String searchPattern = "%" + search + "%";

    if (!search.isBlank()) {
      roleStream =
          roleStream.filter(
              entity ->
                  SearchPatterns.insensitiveLike(entity.getName(), searchPattern)
                      || SearchPatterns.insensitiveLike(entity.getDescription(), searchPattern));
    }
    return paginatedStream(roleStream, first, max);
  }

  public void preRemove(RealmModel realm) {
    LOG.tracef("preRemove(%s)%s", realm, getShortStackTrace());
    FileRoleStore.readAll().stream()
        .filter(e -> realm.getId().equals(e.getRealmId()))
        .forEach(entity -> FileRoleStore.deleteById(entity.getId(), realm.getId()));
  }

  public void preRemove(RealmModel realm, RoleModel role) {
    FileRoleStore.readAll().stream()
        .filter(e -> realm.getId().equals(e.getRealmId()))
        .filter(e -> e.getCompositeRoles().contains(role.getId()))
        .forEach(e -> e.removeCompositeRole(role.getId()));
  }

  @Override
  public void close() {
    // nothing to close
  }
}
