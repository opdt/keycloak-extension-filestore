[![CI](https://github.com/opdt/keycloak-extension-filestore/workflows/CI/badge.svg)](https://github.com/opdt/keycloak-extension-filestore/actions?query=workflow%3ACI)
[![Maven Central](https://img.shields.io/maven-central/v/de.arbeitsagentur.opdt/keycloak-extension-filestore.svg)](https://search.maven.org/artifact/de.arbeitsagentur.opdt/keycloak-extension-filestore)

# keycloak-extension-filestore

Implements client, clientscope, group, realm, role file-based storage.
Initially forked from Keycloak's own experimental version.

Intended to be used in read-only filesystems, for example mounted K8s-configmaps (but can also be used to interactively create the configuration by using the admin console)
To use this, you most likely have to implement your own `DatastoreProvider` and mix it with a different implementation to store users, sessions etc.

