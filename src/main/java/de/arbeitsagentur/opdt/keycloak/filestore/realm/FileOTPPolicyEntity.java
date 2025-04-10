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

import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;
import org.keycloak.models.OTPPolicy;

public class FileOTPPolicyEntity implements UpdatableEntity {

    private boolean isUpdated = false;
    private Integer otpPolicyInitialCounter;
    private Integer otpPolicyDigits;
    private Integer otpPolicyLookAheadWindow;
    private Integer otpPolicyPeriod;
    private String otpPolicyType;
    private String otpPolicyAlgorithm;
    private Boolean otpPolicyCodeReusable = false;

    static FileOTPPolicyEntity fromModel(OTPPolicy model) {
        if (model == null) return null;
        FileOTPPolicyEntity entity = new FileOTPPolicyEntity();
        entity.setOtpPolicyAlgorithm(model.getAlgorithm());
        entity.setOtpPolicyDigits(model.getDigits());
        entity.setOtpPolicyInitialCounter(model.getInitialCounter());
        entity.setOtpPolicyLookAheadWindow(model.getLookAheadWindow());
        entity.setOtpPolicyType(model.getType());
        entity.setOtpPolicyPeriod(model.getPeriod());
        entity.setOtpPolicyCodeReusable(model.isCodeReusable());
        return entity;
    }

    static OTPPolicy toModel(FileOTPPolicyEntity entity) {
        if (entity == null) return null;
        OTPPolicy model = new OTPPolicy();
        Integer otpPolicyDigits = entity.getOtpPolicyDigits();
        model.setDigits(otpPolicyDigits == null ? 0 : otpPolicyDigits);
        model.setAlgorithm(entity.getOtpPolicyAlgorithm());
        Integer otpPolicyInitialCounter = entity.getOtpPolicyInitialCounter();
        model.setInitialCounter(otpPolicyInitialCounter == null ? 0 : otpPolicyInitialCounter);
        Integer otpPolicyLookAheadWindow = entity.getOtpPolicyLookAheadWindow();
        model.setLookAheadWindow(otpPolicyLookAheadWindow == null ? 0 : otpPolicyLookAheadWindow);
        model.setType(entity.getOtpPolicyType());
        Integer otpPolicyPeriod = entity.getOtpPolicyPeriod();
        model.setPeriod(otpPolicyPeriod == null ? 0 : otpPolicyPeriod);
        Boolean isOtpPolicyReusable = entity.isOtpPolicyCodeReusable();
        model.setCodeReusable(isOtpPolicyReusable == null ? OTPPolicy.DEFAULT_IS_REUSABLE : isOtpPolicyReusable);
        return model;
    }

    public boolean isUpdated() {
        return this.isUpdated;
    }

    public Integer getOtpPolicyInitialCounter() {
        return this.otpPolicyInitialCounter;
    }

    public void setOtpPolicyInitialCounter(Integer otpPolicyInitialCounter) {
        this.otpPolicyInitialCounter = otpPolicyInitialCounter;
    }

    public Integer getOtpPolicyDigits() {
        return this.otpPolicyDigits;
    }

    public void setOtpPolicyDigits(Integer otpPolicyDigits) {
        this.otpPolicyDigits = otpPolicyDigits;
    }

    public Integer getOtpPolicyLookAheadWindow() {
        return this.otpPolicyLookAheadWindow;
    }

    public void setOtpPolicyLookAheadWindow(Integer otpPolicyLookAheadWindow) {
        this.otpPolicyLookAheadWindow = otpPolicyLookAheadWindow;
    }

    public Integer getOtpPolicyPeriod() {
        return this.otpPolicyPeriod;
    }

    public void setOtpPolicyPeriod(Integer otpPolicyPeriod) {
        this.otpPolicyPeriod = otpPolicyPeriod;
    }

    public String getOtpPolicyType() {
        return this.otpPolicyType;
    }

    public void setOtpPolicyType(String otpPolicyType) {
        this.otpPolicyType = otpPolicyType;
    }

    public String getOtpPolicyAlgorithm() {
        return this.otpPolicyAlgorithm;
    }

    public void setOtpPolicyAlgorithm(String otpPolicyAlgorithm) {
        this.otpPolicyAlgorithm = otpPolicyAlgorithm;
    }

    public Boolean isOtpPolicyCodeReusable() {
        return this.otpPolicyCodeReusable;
    }

    public void setOtpPolicyCodeReusable(Boolean otpPolicyCodeReusable) {
        this.otpPolicyCodeReusable = otpPolicyCodeReusable;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }
}
