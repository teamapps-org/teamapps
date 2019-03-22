/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.ux.application.validation;

import org.teamapps.data.extract.PropertyProvider;

import java.util.*;

public class RecordChangeSet<RECORD> {

    private RECORD unmodifiedRecord;
    private Map<String, RecordValueChange> valueChangeByPropertyKey = new HashMap<>();

    public RecordChangeSet(RECORD unmodifiedRecord) {
        this.unmodifiedRecord = unmodifiedRecord;
    }

    public RecordChangeSet(RECORD unmodifiedRecord, Map<String, Object> changedValues) {
        this.unmodifiedRecord = unmodifiedRecord;
        changedValues.entrySet().forEach(entry -> {
            addValueChange(new RecordValueChange(entry.getKey(), entry.getValue()));
        });
    }

    public RecordChangeSet(RECORD unmodifiedRecord, List<RecordValueChange> valueChanges) {
        this.unmodifiedRecord = unmodifiedRecord;
        for (RecordValueChange valueChange : valueChanges) {
            addValueChange(valueChange);
        }
    }

    public boolean isChanged() {
        if (valueChangeByPropertyKey.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public RecordChangeSet addValueChange(RecordValueChange valueChange) {
        valueChangeByPropertyKey.put(valueChange.getPropertyName(), valueChange);
        return this;
    }

    public RECORD getUnmodifiedRecord() {
        return unmodifiedRecord;
    }

    public RecordValueChange getValueChange(String propertyName) {
        return valueChangeByPropertyKey.get(propertyName);
    }

    public List<RecordValueChange> getValueChanges() {
        return new ArrayList<>(valueChangeByPropertyKey.values());
    }


    public List<RecordValue> getRecordValues(PropertyProvider<RECORD> propertyProvider, String ... requiredPropertyNames) {
        List<RecordValue> values = new ArrayList<>();
        Set<String> requiredPropertyNameSet = new HashSet<>(Arrays.asList(requiredPropertyNames));
        valueChangeByPropertyKey.entrySet().forEach(entry -> {
            boolean required = requiredPropertyNameSet.contains(entry.getKey());
            RecordValueChange valueChange = entry.getValue();
            values.add(new RecordValue(entry.getKey(), required, valueChange.getValue(), true));
        });
        requiredPropertyNameSet.forEach(propertyName -> {
            if (!valueChangeByPropertyKey.containsKey(propertyName)) {
                values.add(new RecordValue(propertyName, true, propertyProvider.getValue(unmodifiedRecord, propertyName), false));
            }
        });
        return values;
    }



}
