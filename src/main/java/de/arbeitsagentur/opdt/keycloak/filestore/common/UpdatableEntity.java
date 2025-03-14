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
package de.arbeitsagentur.opdt.keycloak.filestore.common;

public interface UpdatableEntity {

    public static class Impl implements UpdatableEntity {
        protected boolean updated;

        @Override
        public boolean isUpdated() {
            return this.updated;
        }

        @Override
        public void clearUpdatedFlag() {
            this.updated = false;
        }

        @Override
        public void markUpdatedFlag() {
            this.updated = true;
        }
    }

    /**
     * Flag signalizing that any of the setters has been meaningfully used.
     *
     * @return
     */
    boolean isUpdated();

    /**
     * An optional operation clearing the updated flag. Right after using this method, the {@link
     * #isUpdated()} would return {@code false}.
     */
    default void clearUpdatedFlag() {}

    /**
     * An optional operation setting the updated flag. Right after using this method, the {@link
     * #isUpdated()} would return {@code true}.
     */
    default void markUpdatedFlag() {}
}
