/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates
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

package org.keycloak.models.map.events;

import org.keycloak.events.Event;
import org.keycloak.events.EventQuery;
import org.keycloak.events.EventType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.keycloak.utils.StreamsUtil.paginatedStream;

public abstract class FileAuthEventQuery implements EventQuery {

    private Integer firstResult;
    private Integer maxResults;
    private String order = "DESC";
    protected String realmId;
    private String clientId;
    private List<EventType> eventTypes;
    private String userId;
    private Long fromTimestamp;
    private Long toTimestamp;
    private String ipAddress;


    @Override
    public EventQuery type(EventType... types) {
        this.eventTypes = Arrays.asList(types);
        return this;
    }

    @Override
    public EventQuery realm(String realmId) {
        this.realmId = realmId;
        return this;
    }

    @Override
    public EventQuery client(String clientId) {
        this.clientId = clientId;
        return this;
    }

    @Override
    public EventQuery user(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public EventQuery fromDate(Date fromDate) {
        this.fromTimestamp = fromDate.getTime();
        return this;
    }

    @Override
    public EventQuery toDate(Date toDate) {
        this.toTimestamp = toDate.getTime();
        return this;
    }

    @Override
    public EventQuery ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    @Override
    public EventQuery firstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    @Override
    public EventQuery maxResults(int max) {
        this.maxResults = max;
        return this;
    }

    @Override
    public EventQuery orderByDescTime() {
        order = "DESC";
        return this;
    }

    @Override
    public EventQuery orderByAscTime() {
        order = "ASC";
        return this;
    }

    @Override
    public Stream<Event> getResultStream() {
        Comparator<Event> ENTITY_COMPARATOR = Comparator
                .comparing(Event::getTime, "DESC".equals(this.order) ? Comparator.reverseOrder() : Comparator.naturalOrder());
        Stream<Event> adminEvents = read()
                .filter(ev -> this.realmId == null || this.realmId.equals(ev.getRealmId()))
                .filter(ev -> this.clientId == null || this.clientId.equals(ev.getClientId()))
                .filter(ev -> this.userId == null || this.userId.equals(ev.getUserId()))
                .filter(ev -> this.ipAddress == null || this.ipAddress.equals(ev.getIpAddress()))
                .filter(ev -> this.fromTimestamp == null || ev.getTime() >= this.fromTimestamp)
                .filter(ev -> this.toTimestamp == null || ev.getTime() <= this.toTimestamp)
                .sorted(ENTITY_COMPARATOR);
        return paginatedStream(adminEvents, firstResult, maxResults);
    }

    protected abstract Stream<Event> read();
}
