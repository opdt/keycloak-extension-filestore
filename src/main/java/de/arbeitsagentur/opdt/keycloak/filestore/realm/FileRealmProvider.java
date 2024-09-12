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

package de.arbeitsagentur.opdt.keycloak.filestore.realm;

import static org.keycloak.common.util.StackUtil.getShortStackTrace;

import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractFileProviderFactory;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;

public class FileRealmProvider implements RealmProvider {

  private static final Logger LOG = Logger.getLogger(FileRealmProvider.class);
  private final KeycloakSession session;

  public FileRealmProvider(KeycloakSession session) {
    this.session = session;
  }

  private RealmModel entityToAdapter(FileRealmEntity entity) {
    return new FileRealmAdapter(session, entity);
  }

  @Override
  public RealmModel createRealm(String name) {
    return createRealm(name, name);
  }

  @Override
  public RealmModel createRealm(String id, String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("name cannot be null or empty");
    }

    if (id != null && FileRealmStore.exists(id)) {
      throw new ModelDuplicateException("Realm exists: " + id);
    }

    if (getRealmByName(name) != null) {
      throw new ModelDuplicateException("Realm with given name exists: " + name);
    }

    FileRealmEntity entity = new FileRealmEntity();
    entity.setId(id == null ? name : id);
    entity.setName(name);
    return entityToAdapter(entity);
  }

  @Override
  public RealmModel getRealm(String id) {
    if (id == null || id.isBlank()) return null;

    LOG.tracef("getRealm(%s)%s", id, getShortStackTrace());
    FileRealmEntity entity = FileRealmStore.read(id);
    return entity == null ? null : entityToAdapter(entity);
  }

  @Override
  public RealmModel getRealmByName(String name) {
    if (name == null || name.isBlank()) return null;

    LOG.tracef("getRealmByName(%s)%s", name, getShortStackTrace());

    Optional<FileRealmEntity> entity =
        FileRealmStore.readAll().stream().filter(e -> name.equals(e.getName())).findFirst();

    String realmId =
        entity.filter(e -> name.equals(e.getName())).map(FileRealmEntity::getId).orElse(null);
    return realmId == null ? null : session.realms().getRealm(realmId);
  }

  @Override
  public Stream<RealmModel> getRealmsStream() {
    return FileRealmStore.readAll().stream()
        .map(this::entityToAdapter)
        .sorted(Comparator.comparing(RealmModel::getName));
  }

  @Override
  public Stream<RealmModel> getRealmsWithProviderTypeStream(Class<?> type) {
    return FileRealmStore.readAll().stream()
        .map(this::entityToAdapter)
        .filter(
            realm ->
                realm
                    .getComponentsStream()
                    .anyMatch(component -> type.getName().equals(component.getProviderType())))
        .sorted(Comparator.comparing(RealmModel::getName));
  }

  @Override
  public boolean removeRealm(String id) {
    LOG.tracef("removeRealm(%s)%s", id, getShortStackTrace());
    RealmModel realm = getRealm(id);
    if (realm == null) return false;
    session.invalidate(
        AbstractFileProviderFactory.MapProviderObjectType.REALM_BEFORE_REMOVE, realm);
    FileRealmStore.deleteById(realm.getId());
    session.invalidate(AbstractFileProviderFactory.MapProviderObjectType.REALM_AFTER_REMOVE, realm);
    return true;
  }

  @Override
  public void removeExpiredClientInitialAccess() {
    FileRealmStore.readAll().stream()
        .filter(e -> !e.getClientInitialAccesses().isEmpty())
        .forEach(FileRealmEntity::removeExpiredClientInitialAccesses);
  }

  @Override
  public void saveLocalizationText(RealmModel realm, String locale, String key, String text) {
    if (locale == null || key == null || text == null) return;
    Map<String, String> texts = new HashMap<>();
    texts.put(key, text);
    realm.createOrUpdateRealmLocalizationTexts(locale, texts);
  }

  @Override
  public void saveLocalizationTexts(
      RealmModel realm, String locale, Map<String, String> localizationTexts) {
    if (locale == null || localizationTexts == null) return;
    realm.createOrUpdateRealmLocalizationTexts(locale, localizationTexts);
  }

  @Override
  public boolean updateLocalizationText(RealmModel realm, String locale, String key, String text) {
    if (locale == null
        || key == null
        || text == null
        || !realm.getRealmLocalizationTextsByLocale(locale).containsKey(key)) {
      return false;
    }

    saveLocalizationText(realm, locale, key, text);
    return true;
  }

  @Override
  public boolean deleteLocalizationTextsByLocale(RealmModel realm, String locale) {
    return realm.removeRealmLocalizationTexts(locale);
  }

  @Override
  public boolean deleteLocalizationText(RealmModel realm, String locale, String key) {
    if (locale == null
        || key == null
        || (!realm.getRealmLocalizationTextsByLocale(locale).containsKey(key))) {
      return false;
    }

    Map<String, String> texts = new HashMap<>(realm.getRealmLocalizationTextsByLocale(locale));
    texts.remove(key);
    realm.removeRealmLocalizationTexts(locale);
    realm.createOrUpdateRealmLocalizationTexts(locale, texts);
    return true;
  }

  @Override
  public String getLocalizationTextsById(RealmModel realm, String locale, String key) {
    if (locale == null
        || key == null
        || (!realm.getRealmLocalizationTextsByLocale(locale).containsKey(key))) {
      return null;
    }

    return realm.getRealmLocalizationTextsByLocale(locale).get(key);
  }

  @Override
  public void close() {
    // nothing to close
  }
}
