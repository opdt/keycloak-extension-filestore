package org.keycloak.testsetup;

import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

import java.util.Set;
import java.util.stream.Stream;

/**
 * @author hmlnarik
 */
public class KeycloakModelParameters {

    private final Set<Class<? extends Spi>> allowedSpis;
    private final Set<Class<? extends ProviderFactory>> allowedFactories;

    protected static final String STORAGE_CONFIG = "storage.provider";

    public KeycloakModelParameters(
            Set<Class<? extends Spi>> allowedSpis,
            Set<Class<? extends ProviderFactory>> allowedFactories) {
        this.allowedSpis = allowedSpis;
        this.allowedFactories = allowedFactories;
    }

    public boolean isSpiAllowed(Spi s) {
        return allowedSpis.contains(s.getClass());
    }

    public boolean isFactoryAllowed(ProviderFactory factory) {
        return allowedFactories.stream().anyMatch((c) -> c.isAssignableFrom(factory.getClass()));
    }

    /**
     * Returns stream of parameters of the given type, or an empty stream if no parameters of the
     * given type are supplied by this clazz.
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public <T> Stream<T> getParameters(Class<T> clazz) {
        return Stream.empty();
    }

    public void updateConfig(Config cf) {
    }

    public void beforeSuite(Config cf) {
    }

    public void afterSuite() {
    }
}
