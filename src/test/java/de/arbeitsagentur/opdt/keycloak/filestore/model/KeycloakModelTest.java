package de.arbeitsagentur.opdt.keycloak.filestore.model; /*
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

import com.google.common.collect.ImmutableSet;
import de.arbeitsagentur.opdt.keycloak.filestore.testsetup.Config;
import de.arbeitsagentur.opdt.keycloak.filestore.testsetup.KeycloakModelParameters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.Config.Scope;
import org.keycloak.authorization.AuthorizationSpi;
import org.keycloak.authorization.DefaultAuthorizationProviderFactory;
import org.keycloak.authorization.policy.provider.PolicyProviderFactory;
import org.keycloak.authorization.policy.provider.PolicySpi;
import org.keycloak.authorization.store.StoreFactorySpi;
import org.keycloak.common.Profile;
import org.keycloak.common.profile.PropertiesProfileConfigResolver;
import org.keycloak.common.util.Time;
import org.keycloak.component.ComponentFactoryProviderFactory;
import org.keycloak.component.ComponentFactorySpi;
import org.keycloak.events.EventStoreSpi;
import org.keycloak.executors.DefaultExecutorsProviderFactory;
import org.keycloak.executors.ExecutorsSpi;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.PostMigrationEvent;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.ProviderManager;
import org.keycloak.provider.Spi;
import org.keycloak.quarkus.runtime.integration.resteasy.QuarkusKeycloakContext;
import org.keycloak.services.DefaultComponentFactoryProviderFactory;
import org.keycloak.services.DefaultKeycloakContext;
import org.keycloak.services.DefaultKeycloakSession;
import org.keycloak.services.DefaultKeycloakSessionFactory;
import org.keycloak.storage.DatastoreProviderFactory;
import org.keycloak.storage.DatastoreSpi;
import org.keycloak.timer.TimerSpi;

/**
 * Base of testcases that operate on session level. The tests derived from this class will have
 * access to a shared {@link KeycloakSessionFactory} in the {@link #LOCAL_FACTORY} field that can be
 * used to obtain a session and e.g. start / stop transaction.
 *
 * <p>This class expects {@code keycloak.model.parameters} system property to contain
 * comma-separated class names that implement {@link KeycloakModelParameters} interface to provide
 * list of factories and SPIs that are visible to the {@link KeycloakSessionFactory} that is offered
 * to the tests.
 *
 * <p>If no parameters are set via this property, the tests derived from this class are skipped.
 *
 * @author hmlnarik
 */
public abstract class KeycloakModelTest {

  public static final String TEST_FILESTORE_DIR = "src/test/filestore";
  private static final Logger LOG = Logger.getLogger(KeycloakModelParameters.class);
  private static final AtomicInteger FACTORY_COUNT = new AtomicInteger();
  private static final List<String> MAIN_THREAD_NAMES = Arrays.asList("main", "Time-limited test");

  private static final Set<Class<? extends Spi>> ALLOWED_SPIS =
      ImmutableSet.<Class<? extends Spi>>builder()
          .add(AuthorizationSpi.class)
          .add(PolicySpi.class)
          .add(ClientScopeSpi.class)
          .add(ClientSpi.class)
          .add(ComponentFactorySpi.class)
          .add(EventStoreSpi.class)
          .add(ExecutorsSpi.class)
          .add(GroupSpi.class)
          .add(RealmSpi.class)
          .add(RoleSpi.class)
          .add(DeploymentStateSpi.class)
          .add(StoreFactorySpi.class)
          .add(TimerSpi.class)
          .add(UserLoginFailureSpi.class)
          .add(UserSessionSpi.class)
          .add(UserSpi.class)
          .add(DatastoreSpi.class)
          .build();

  private static final Set<Class<? extends ProviderFactory>> ALLOWED_FACTORIES =
      ImmutableSet.<Class<? extends ProviderFactory>>builder()
          .add(ComponentFactoryProviderFactory.class)
          .add(DefaultAuthorizationProviderFactory.class)
          .add(PolicyProviderFactory.class)
          .add(DefaultExecutorsProviderFactory.class)
          .add(DeploymentStateProviderFactory.class)
          .add(DatastoreProviderFactory.class)
          .build();

  protected static final List<KeycloakModelParameters> MODEL_PARAMETERS;
  protected static final Config CONFIG = new Config(KeycloakModelTest::useDefaultFactory);
  private static volatile KeycloakSessionFactory DEFAULT_FACTORY;
  private static final ThreadLocal<KeycloakSessionFactory> LOCAL_FACTORY = new ThreadLocal<>();
  protected static boolean USE_DEFAULT_FACTORY = false;

  static {
    org.keycloak.Config.init(CONFIG);
    KeycloakModelParameters basicParameters =
        new KeycloakModelParameters(ALLOWED_SPIS, ALLOWED_FACTORIES);
    MODEL_PARAMETERS =
        Stream.concat(
                Stream.of(basicParameters),
                Stream.of(System.getProperty("keycloak.model.parameters", "").split("\\s*,\\s*"))
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .map(
                        cn -> {
                          try {
                            return Class.forName(
                                cn.indexOf('.') >= 0 ? cn : ("org.keycloak.testsetup." + cn));
                          } catch (Exception e) {
                            LOG.error("Cannot find " + cn);
                            return null;
                          }
                        })
                    .filter(Objects::nonNull)
                    .map(
                        c -> {
                          try {
                            return c.getDeclaredConstructor().newInstance();
                          } catch (Exception e) {
                            LOG.error("Cannot instantiate " + c);
                            return null;
                          }
                        })
                    .filter(KeycloakModelParameters.class::isInstance)
                    .map(KeycloakModelParameters.class::cast))
            .toList();
    for (KeycloakModelParameters kmp : KeycloakModelTest.MODEL_PARAMETERS) {
      kmp.beforeSuite(CONFIG);
    }
    reinitializeKeycloakSessionFactory();
    DEFAULT_FACTORY = getFactory();
  }

  /**
   * Creates a fresh initialized {@link KeycloakSessionFactory}. The returned factory uses
   * configuration local to the thread that calls this method, allowing for per-thread
   * customization. This in turn allows testing of several parallel session factories which can be
   * used to simulate several servers running in parallel.
   */
  public static KeycloakSessionFactory createKeycloakSessionFactory() {
    int factoryIndex = FACTORY_COUNT.incrementAndGet();
    String threadName = Thread.currentThread().getName();
    CONFIG.reset();
    CONFIG
        .spi(ComponentFactorySpi.NAME)
        .provider(DefaultComponentFactoryProviderFactory.PROVIDER_ID)
        .config("cachingForced", "true");
    CONFIG.spi("mapStorage").provider("file").config("dir", TEST_FILESTORE_DIR);
    MODEL_PARAMETERS.forEach(m -> m.updateConfig(CONFIG));
    LOG.debugf(
        "Creating factory %d in %s using the following configuration:\n    %s",
        factoryIndex, threadName, CONFIG);
    DefaultKeycloakSessionFactory res =
        new DefaultKeycloakSessionFactory() {
          @Override
          public KeycloakSession create() {
            return new DefaultKeycloakSession(this) {
              @Override
              protected DefaultKeycloakContext createKeycloakContext(
                  KeycloakSession keycloakSession) {
                return new QuarkusKeycloakContext(this);
              }
            };
          }

          @Override
          public void init() {
            Profile.configure(new PropertiesProfileConfigResolver(System.getProperties()));
            super.init();
          }

          @Override
          protected boolean isEnabled(ProviderFactory factory, Scope scope) {
            return super.isEnabled(factory, scope) && isFactoryAllowed(factory);
          }

          @Override
          protected Map<Class<? extends Provider>, Map<String, ProviderFactory>> loadFactories(
              ProviderManager pm) {
            spis.removeIf(s -> !isSpiAllowed(s));
            return super.loadFactories(pm);
          }

          private boolean isSpiAllowed(Spi s) {
            return MODEL_PARAMETERS.stream().anyMatch(p -> p.isSpiAllowed(s));
          }

          private boolean isFactoryAllowed(ProviderFactory factory) {
            return MODEL_PARAMETERS.stream().anyMatch(p -> p.isFactoryAllowed(factory));
          }

          @Override
          public String toString() {
            return "KeycloakSessionFactory " + factoryIndex + " (from " + threadName + " thread)";
          }
        };
    res.init();
    res.publish(new PostMigrationEvent(res));
    return res;
  }

  /**
   * Closes and initializes new {@link #LOCAL_FACTORY}. This has the same effect as server restart
   * in full-blown server scenario.
   */
  public static synchronized void reinitializeKeycloakSessionFactory() {
    closeKeycloakSessionFactory();
    setFactory(createKeycloakSessionFactory());
  }

  public static synchronized void closeKeycloakSessionFactory() {
    KeycloakSessionFactory f = getFactory();
    setFactory(null);
    if (f != null) {
      LOG.debugf("Closing %s", f);
      f.close();
    }
  }

  protected static boolean useDefaultFactory() {
    return USE_DEFAULT_FACTORY || MAIN_THREAD_NAMES.contains(Thread.currentThread().getName());
  }

  protected static KeycloakSessionFactory getFactory() {
    return useDefaultFactory() ? DEFAULT_FACTORY : LOCAL_FACTORY.get();
  }

  private static void setFactory(KeycloakSessionFactory factory) {
    if (useDefaultFactory()) {
      DEFAULT_FACTORY = factory;
    } else {
      LOCAL_FACTORY.set(factory);
    }
  }

  @BeforeAll
  public static void checkValidParameters() {
    Assumptions.assumeTrue(
        MODEL_PARAMETERS.size() > 0, // Additional parameters have to be set
        "keycloak.model.parameters property must be set");
  }

  protected void createEnvironment(KeycloakSession s) {}

  protected void cleanEnvironment(KeycloakSession s) {}

  @BeforeEach
  public final void createEnvironment() {
    Time.setOffset(0);
    KeycloakModelUtils.runJobInTransaction(getFactory(), this::createEnvironment);
  }

  @AfterEach
  public final void cleanEnvironment() {
    if (getFactory() == null) {
      reinitializeKeycloakSessionFactory();
    }
    Time.setOffset(0);
    KeycloakModelUtils.runJobInTransaction(getFactory(), this::cleanEnvironment);
  }

  protected void inCommittedTransaction(Consumer<KeycloakSession> what) {
    inCommittedTransaction(
        a -> {
          what.accept(a);
          return null;
        });
  }

  protected <R> R inCommittedTransaction(Function<KeycloakSession, R> what) {
    return inCommittedTransaction(1, (a, b) -> what.apply(a), null);
  }

  protected <T, R> R inCommittedTransaction(
      T parameter,
      BiFunction<KeycloakSession, T, R> what,
      BiConsumer<KeycloakSession, T> onCommit) {
    AtomicReference<R> res = new AtomicReference<>();
    KeycloakModelUtils.runJobInTransaction(
        getFactory(),
        session -> {
          session
              .getTransactionManager()
              .enlistAfterCompletion(
                  new AbstractKeycloakTransaction() {
                    @Override
                    protected void commitImpl() {
                      if (onCommit != null) {
                        onCommit.accept(session, parameter);
                      }
                    }

                    @Override
                    protected void rollbackImpl() {
                      // Unsupported in Cassandra
                    }
                  });
          res.set(what.apply(session, parameter));
        });
    return res.get();
  }

  /**
   * Convenience method for {@link #inCommittedTransaction(java.util.function.Consumer)} that
   * obtains realm model from the session and puts it into session context before running the {@code
   * fn} task.
   */
  protected void withRealm(String realmId, BiConsumer<KeycloakSession, RealmModel> fn) {
    inCommittedTransaction(
        session -> {
          final RealmModel realm = session.realms().getRealm(realmId);
          session.getContext().setRealm(realm);
          fn.accept(session, realm);
        });
  }

  /**
   * Convenience method for {@link #inCommittedTransaction(java.util.function.Consumer)} that
   * obtains realm model from the session and puts it into session context before running the {@code
   * fn} task.
   */
  protected <T extends Provider> void withRealmAndProvider(
      String realmId, Function<KeycloakSession, T> providerFn, BiConsumer<T, RealmModel> fn) {
    inCommittedTransaction(
        session -> {
          final RealmModel realm = session.realms().getRealm(realmId);
          session.getContext().setRealm(realm);
          T provider = providerFn.apply(session);
          fn.accept(provider, realm);
        });
  }
}
