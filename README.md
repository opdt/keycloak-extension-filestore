# keycloak-extension-filestore

Implements client, clientscope, group, realm, role file-based storage.
Initially forked from Keycloak's own experimental version.

Intended to be used in read-only filesystems, for example mounted K8s-configmaps.
To use this, you most likely have to implement your own `DatastoreProvider` and mix it with a different implementation to store users, sessions etc.

