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

import static org.keycloak.utils.StreamsUtil.paginatedStream;

import de.arbeitsagentur.opdt.keycloak.filestore.SearchPatterns;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AdminEventQuery;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

public abstract class FileAdminEventQuery implements AdminEventQuery {

    private Integer firstResult;
    private Integer maxResults;
    private String order = "DESC";
    protected String realmId;
    private String authRealmId;
    private String authClientId;
    private String authUserId;
    private String authIpAddress;
    private List<OperationType> operationTypes;
    private List<ResourceType> resourceTypes;
    private String resourcePath;
    private Long fromTimestamp;
    private Long toTimestamp;

    @Override
    public AdminEventQuery realm(String realmId) {
        this.realmId = realmId;
        return this;
    }

    @Override
    public AdminEventQuery authRealm(String realmId) {
        this.authRealmId = realmId;
        return this;
    }

    @Override
    public AdminEventQuery authClient(String clientId) {
        this.authClientId = clientId;
        return this;
    }

    @Override
    public AdminEventQuery authUser(String userId) {
        this.authUserId = userId;
        return this;
    }

    @Override
    public AdminEventQuery authIpAddress(String ipAddress) {
        this.authIpAddress = ipAddress;
        return this;
    }

    @Override
    public AdminEventQuery operation(OperationType... operations) {
        // mcb = mcb.compare(AdminSearchableFields.OPERATION_TYPE, IN, Arrays.stream(operations));
        this.operationTypes = Arrays.asList(operations);
        return this;
    }

    @Override
    public AdminEventQuery resourceType(ResourceType... resourceTypes) {
        this.resourceTypes = Arrays.asList(resourceTypes);
        return this;
    }

    @Override
    public AdminEventQuery resourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
        return this;
    }

    @Override
    public AdminEventQuery fromTime(Date fromTime) {
        this.fromTimestamp = fromTime.getTime();
        return this;
    }

    @Override
    public AdminEventQuery toTime(Date toTime) {
        this.toTimestamp = toTime.getTime();
        return this;
    }

    @Override
    public AdminEventQuery fromTime(long l) {
        this.fromTimestamp = l;
        return this;
    }

    @Override
    public AdminEventQuery toTime(long l) {
        this.toTimestamp = l;
        return this;
    }

    @Override
    public AdminEventQuery firstResult(int first) {
        firstResult = first;
        return this;
    }

    @Override
    public AdminEventQuery maxResults(int max) {
        maxResults = max;
        return this;
    }

    @Override
    public AdminEventQuery orderByDescTime() {
        order = "DESC";
        return this;
    }

    @Override
    public AdminEventQuery orderByAscTime() {
        order = "ASC";
        return this;
    }

    @Override
    public Stream<AdminEvent> getResultStream() {
        Comparator<AdminEvent> ENTITY_COMPARATOR = Comparator.comparing(
                AdminEvent::getTime, "DESC".equals(this.order) ? Comparator.reverseOrder() : Comparator.naturalOrder());
        Stream<AdminEvent> adminEvents = read().filter(
                        ev -> this.realmId == null || this.realmId.equals(ev.getRealmId()))
                .filter(ev -> this.authRealmId == null
                        || (ev.getAuthDetails() != null
                                && this.authRealmId.equals(ev.getAuthDetails().getRealmId())))
                .filter(ev -> this.authClientId == null
                        || (ev.getAuthDetails() != null
                                && this.authClientId.equals(ev.getAuthDetails().getClientId())))
                .filter(ev -> this.authUserId == null
                        || (ev.getAuthDetails() != null
                                && this.authUserId.equals(ev.getAuthDetails().getUserId())))
                .filter(ev -> this.authIpAddress == null
                        || (ev.getAuthDetails() != null
                                && this.authIpAddress.equals(ev.getAuthDetails().getIpAddress())))
                .filter(ev -> this.operationTypes == null
                        || ev.getOperationType() != null && this.operationTypes.contains(ev.getOperationType()))
                .filter(ev -> this.resourceTypes == null
                        || ev.getResourceType() != null && this.resourceTypes.contains(ev.getResourceType()))
                .filter(ev -> this.resourcePath == null
                        || (ev.getResourceType() != null
                                && SearchPatterns.like(ev.getResourcePath(), this.resourcePath.replace('*', '%'))))
                .filter(ev -> this.fromTimestamp == null || ev.getTime() >= this.fromTimestamp)
                .filter(ev -> this.toTimestamp == null || ev.getTime() <= this.toTimestamp)
                .sorted(ENTITY_COMPARATOR);
        return paginatedStream(adminEvents, firstResult, maxResults);
    }

    protected abstract Stream<AdminEvent> read();
}
