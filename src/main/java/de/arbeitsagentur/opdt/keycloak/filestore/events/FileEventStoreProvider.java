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

import static de.arbeitsagentur.opdt.keycloak.filestore.events.EventUtils.modelToEntity;
import static org.keycloak.common.util.StackUtil.getShortStackTrace;

import de.arbeitsagentur.opdt.keycloak.filestore.common.ExpirableEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.ExpirationUtils;
import java.util.stream.Stream;
import org.jboss.logging.Logger;
import org.keycloak.common.util.Time;
import org.keycloak.events.Event;
import org.keycloak.events.EventQuery;
import org.keycloak.events.EventStoreProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AdminEventQuery;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.RealmModel;

public class FileEventStoreProvider implements EventStoreProvider {

  private static final Logger LOG = Logger.getLogger(FileEventStoreProvider.class);
  private final KeycloakSession session;

  public FileEventStoreProvider(KeycloakSession session) {
    this.session = session;
  }

  /** LOGIN EVENTS */
  @Override
  public void onEvent(Event event) {
    LOG.tracef("onEvent(%s)%s", event, getShortStackTrace());
    String id = event.getId();
    String realmId = event.getRealmId();
    if (FileAdminEventInMemoryStore.exists(id)) {
      throw new ModelDuplicateException("Event already exists: " + id);
    }

    FileAuthEventEntity entity = modelToEntity(event);
    if (realmId != null) {
      RealmModel realm = session.realms().getRealm(realmId);
      if (realm != null && realm.getEventsExpiration() > 0) {
        entity.setExpiration(Time.currentTimeMillis() + (realm.getEventsExpiration() * 1000));
      }
    }
    FileAuthEventInMemoryStore.create(entity);
  }

  @Override
  public EventQuery createQuery() {
    LOG.tracef("createQuery()%s", getShortStackTrace());
    return new FileAuthEventQuery() {
      private boolean filterExpired(ExpirableEntity event) {
        // Check if entity is expired
        if (ExpirationUtils.isExpired(event, true)) {
          return false; // Do not include entity in the resulting stream
        }
        return true; // Entity is not expired
      }

      @Override
      protected Stream<Event> read() {
        return FileAuthEventInMemoryStore.readAll().stream()
            .filter(ev -> realmId.equals(ev.getRealmId()))
            .filter(this::filterExpired)
            .map(EventUtils::entityToModel);
      }
    };
  }

  @Override
  public void clear() {
    LOG.tracef("clear()%s", getShortStackTrace());
    FileAuthEventInMemoryStore.readAll().stream().forEach(FileAuthEventInMemoryStore::delete);
  }

  @Override
  public void clear(RealmModel realm) {
    LOG.tracef("clear(%s)%s", realm, getShortStackTrace());
    FileAuthEventInMemoryStore.readAll().stream()
        .filter(ev -> realm.getId().equals(ev.getRealmId()))
        .forEach(FileAuthEventInMemoryStore::delete);
  }

  @Override
  public void clear(RealmModel realm, long olderThan) {
    LOG.tracef("clear(%s, %d)%s", realm, olderThan, getShortStackTrace());
    FileAuthEventInMemoryStore.readAll().stream()
        .filter(ev -> realm.getId().equals(ev.getRealmId()))
        .filter(ev -> ev.getTimestamp() < olderThan)
        .forEach(FileAuthEventInMemoryStore::delete);
  }

  @Override
  public void clearExpiredEvents() {
    LOG.tracef("clearExpiredEvents()%s", getShortStackTrace());
    LOG.warnf(
        "Clearing expired entities should not be triggered manually. It is responsibility of the store to clear these.");
  }

  /** ADMIN EVENTS */
  @Override
  public void onEvent(AdminEvent event, boolean includeRepresentation) {
    LOG.tracef("onEvent(%s, %s)%s", event, includeRepresentation, getShortStackTrace());
    String id = event.getId();
    String realmId = event.getRealmId();
    if (FileAdminEventInMemoryStore.exists(id)) {
      throw new ModelDuplicateException("Event already exists: " + id);
    }
    FileAdminEventEntity entity = modelToEntity(event, includeRepresentation);
    if (realmId != null) {
      RealmModel realm = session.realms().getRealm(realmId);
      if (realm != null) {
        Long expiration = realm.getAttribute("adminEventsExpiration", 0L);
        if (expiration > 0) {
          entity.setExpiration(Time.currentTimeMillis() + (expiration * 1000));
        }
      }
    }
    FileAdminEventInMemoryStore.create(entity);
  }

  @Override
  public AdminEventQuery createAdminQuery() {
    LOG.tracef("createAdminQuery()%s", getShortStackTrace());
    return new FileAdminEventQuery() {
      private boolean filterExpired(ExpirableEntity event) {
        if (ExpirationUtils.isExpired(event, true)) {
          return false; // Do not include entity in the resulting stream
        }
        return true; // Entity is not expired
      }

      @Override
      protected Stream<AdminEvent> read() {
        return FileAdminEventInMemoryStore.readAll().stream()
            .filter(ev -> realmId.equals(ev.getRealmId()))
            .filter(this::filterExpired)
            .map(EventUtils::entityToModel);
      }
    };
  }

  @Override
  public void clearAdmin() {
    LOG.tracef("clearAdmin()%s", getShortStackTrace());
    FileAdminEventInMemoryStore.readAll().stream().forEach(FileAdminEventInMemoryStore::delete);
  }

  @Override
  public void clearAdmin(RealmModel realm) {
    LOG.tracef("clearAdmin(%s)%s", realm, getShortStackTrace());
    FileAdminEventInMemoryStore.readAll().stream()
        .filter(ev -> realm.getId().equals(ev.getRealmId()))
        .forEach(FileAdminEventInMemoryStore::delete);
  }

  @Override
  public void clearAdmin(RealmModel realm, long olderThan) {
    LOG.tracef("clearAdmin(%s, %d)%s", realm, olderThan, getShortStackTrace());
    FileAdminEventInMemoryStore.readAll().stream()
        .filter(ev -> realm.getId().equals(ev.getRealmId()))
        .filter(ev -> ev.getTimestamp() < olderThan)
        .forEach(FileAdminEventInMemoryStore::delete);
  }

  @Override
  public void close() {}
}
