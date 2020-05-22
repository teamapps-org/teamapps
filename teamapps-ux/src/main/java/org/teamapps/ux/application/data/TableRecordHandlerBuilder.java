/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.ux.application.data;

import org.teamapps.ux.application.filter.FilterProvider;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.table.Table;

import java.util.function.Supplier;

public class TableRecordHandlerBuilder<RECORD> {
    private final Table<RECORD> table;
    private final UpdateMode updateMode;
    private View view;
    private boolean emptyRecordsOnTop;
    private boolean ensureEmptyRecordRow;
    private RecordEditableDecider<RECORD> recordEditableDecider;
    private RecordDeletableDecider<RECORD> recordDeletableDecider;
    private RecordValidator<RECORD> recordValidator;
    private RecordUpdateHandler<RECORD> recordUpdateHandler;
    private RecordDeletionHandler<RECORD> recordDeletionHandler;
    private RecordHandler<RECORD> recordHandler;
    private Supplier<RECORD> emptyRecordSupplier;
    private FilterProvider<RECORD> filterProvider;

    public TableRecordHandlerBuilder(Table<RECORD> table, UpdateMode updateMode) {
        this.table = table;
        this.updateMode = updateMode;
    }

    public TableRecordHandlerBuilder<RECORD> setView(View view) {
        this.view = view;
        return this;
    }

    public TableRecordHandlerBuilder<RECORD> setEmptyRecordsOnTop(boolean emptyRecordsOnTop) {
        this.emptyRecordsOnTop = emptyRecordsOnTop;
        return this;
    }

    public TableRecordHandlerBuilder<RECORD> setEnsureEmptyRecordRow(boolean ensureEmptyRecordRow) {
        this.ensureEmptyRecordRow = ensureEmptyRecordRow;
        return this;
    }

    public TableRecordHandlerBuilder<RECORD> setRecordEditableDecider(RecordEditableDecider<RECORD> recordEditableDecider) {
        this.recordEditableDecider = recordEditableDecider;
        return this;
    }

    public TableRecordHandlerBuilder<RECORD> setRecordDeletableDecider(RecordDeletableDecider<RECORD> recordDeletableDecider) {
        this.recordDeletableDecider = recordDeletableDecider;
        return this;
    }

    public TableRecordHandlerBuilder<RECORD> setRecordValidator(RecordValidator<RECORD> recordValidator) {
        this.recordValidator = recordValidator;
        return this;
    }

    public TableRecordHandlerBuilder<RECORD> setRecordUpdateHandler(RecordUpdateHandler<RECORD> recordUpdateHandler) {
        this.recordUpdateHandler = recordUpdateHandler;
        return this;
    }

    public TableRecordHandlerBuilder<RECORD> setRecordDeletionHandler(RecordDeletionHandler<RECORD> recordDeletionHandler) {
        this.recordDeletionHandler = recordDeletionHandler;
        return this;
    }

    public TableRecordHandlerBuilder<RECORD> setRecordHandler(RecordHandler<RECORD> recordHandler) {
        this.recordHandler = recordHandler;
        return this;
    }

    public TableRecordHandlerBuilder<RECORD> setEmptyRecordSupplier(Supplier<RECORD> emptyRecordSupplier) {
        this.emptyRecordSupplier = emptyRecordSupplier;
        return this;
    }

    public TableRecordHandlerBuilder<RECORD> setFilterProvider(FilterProvider<RECORD> filterProvider) {
        this.filterProvider = filterProvider;
        return this;
    }

    public TableRecordHandler<RECORD> createTableRecordHandler() {
        return new TableRecordHandler<>(table, updateMode, emptyRecordsOnTop, ensureEmptyRecordRow, recordEditableDecider, recordDeletableDecider, recordValidator, recordUpdateHandler, recordDeletionHandler, recordHandler, emptyRecordSupplier, filterProvider, view);
    }
}
