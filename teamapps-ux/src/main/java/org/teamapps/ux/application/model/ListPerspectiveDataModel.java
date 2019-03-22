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
package org.teamapps.ux.application.model;

import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.data.value.SortDirection;
import org.teamapps.data.value.Sorting;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListPerspectiveDataModel<RECORD> extends AbstractPerspectiveDataModel<RECORD> {


    private List<RECORD> records = new ArrayList<>();

    public ListPerspectiveDataModel(PropertyProvider<RECORD> propertyProvider) {
        super(propertyProvider);
    }

    public void addRecord(RECORD record) {
        records.add(record);
    }

    @Override
    public int getRecordCount() {
        return records.size();
    }

    @Override
    public List<RECORD> getEntities(int startIndex, int length, Instant start, Instant end, Sorting sorting) {
        List<RECORD> list;
        if (startIndex == 0 && length >= records.size()) {
            list = Collections.unmodifiableList(records);
        } else {
            int len = Math.min(length, records.size());
            list = records.subList(startIndex, Math.min(startIndex + len, records.size() - startIndex));
        }
        PropertyProvider<RECORD> propertyProvider = getPropertyProvider();
        if (start != null && end != null && start.getEpochSecond() < end.getEpochSecond()) {
            //propertyProvider.getInstantValue()
        }
        if (sorting != null && sorting.getFieldName() != null) {
            Comparator<RECORD> comparator = Comparator.comparing(record -> (Comparable) propertyProvider.getValue(record, sorting.getFieldName()));
            if (SortDirection.DESC == sorting.getSorting()) {
                comparator = comparator.reversed();
            }
            Collections.sort(list, comparator);
        }
        return list;
    }
}
