/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates
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

package de.arbeitsagentur.opdt.keycloak.filestore.common;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;

public class SessionAttributesUtils {
  private static final Logger log = Logger.getLogger(SessionAttributesUtils.class);
  private static final AtomicInteger COUNTER_TX = new AtomicInteger();

  /**
   * Returns a new unique counter across whole Keycloak instance
   *
   * @return unique number
   */
  public static int grabNewFactoryIdentifier() {
    return COUNTER_TX.getAndIncrement();
  }

  /**
   * Used for creating a provider instance only once within one KeycloakSession.
   *
   * <p>Checks whether there already exists a provider withing session attributes for given {@code
   * providerClass} and {@code factoryIdentifier}. If exists returns existing provider, otherwise
   * creates a new instance using {@code createNew} function.
   *
   * @param session current Keycloak session
   * @param factoryIdentifier unique factory identifier. {@link
   *     SessionAttributesUtils#grabNewFactoryIdentifier()} can be used for obtaining new
   *     identifiers.
   * @param providerClass class of the requested provider
   * @param createNew function that creates a new instance of the provider
   * @param <T> type of the provider
   * @return an instance of the provider either from session attributes or freshly created.
   */
  public static <T extends Provider> T createProviderIfAbsent(
      KeycloakSession session,
      int factoryIdentifier,
      Class<T> providerClass,
      Function<KeycloakSession, T> createNew) {
    String uniqueKey = providerClass.getName() + factoryIdentifier;
    T provider = session.getAttribute(uniqueKey, providerClass);
    if (provider != null) {
      return provider;
    }
    log.debugf("Using %s ...", providerClass.getName());
    provider = createNew.apply(session);
    session.setAttribute(uniqueKey, provider);
    return provider;
  }
}
