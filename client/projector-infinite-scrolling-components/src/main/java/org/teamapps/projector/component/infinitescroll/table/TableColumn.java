/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.projector.component.infinitescroll.table;

import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.component.field.*;
import org.teamapps.projector.dataextraction.ValueExtractor;
import org.teamapps.projector.dataextraction.ValueInjector;
import org.teamapps.projector.format.TextAlignment;
import org.teamapps.projector.session.CurrentSessionContext;
import org.teamapps.projector.session.SessionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableColumn<RECORD, VALUE> {
	public static final int DEFAULT_WIDTH = 150;
	private Table<RECORD> table;

	private final String propertyName;
	private Icon icon;
	private String title;
	private Field<VALUE> field;
	private int minWidth;
	private int defaultWidth;
	private int maxWidth;
	private boolean visible = true;
	private boolean sortable = true;
	private boolean resizeable = true;
	private boolean hiddenIfOnlyEmptyCellsVisible = false;
	private TextAlignment headerAlignment = TextAlignment.LEFT;
	private ValueExtractor<RECORD, VALUE> valueExtractor;
	private ValueInjector<RECORD, VALUE> valueInjector;

	private List<FieldMessage> messages = new ArrayList<>();

	public TableColumn(String propertyName, Field<VALUE> field) {
		this(propertyName, null, null, field, 0, DEFAULT_WIDTH, 0);
	}

	public TableColumn(String propertyName, String title, Field<VALUE> field) {
		this(propertyName, null, title, field, 0, DEFAULT_WIDTH, 0);
	}

	public TableColumn(String propertyName, Icon icon, String title, Field<VALUE> field) {
		this(propertyName, icon, title, field, 0, DEFAULT_WIDTH, 0);
	}

	public TableColumn(String propertyName, Icon icon, String title, Field<VALUE> field, int defaultWidth) {
		this(propertyName, icon, title, field, 0, defaultWidth, 0);
	}

	public TableColumn(String propertyName, Icon icon, String title, Field<VALUE> field, int minWidth, int defaultWidth, int maxWidth) {
		this.propertyName = propertyName;
		this.icon = icon;
		this.title = title;
		this.field = field;
		this.minWidth = minWidth;
		this.defaultWidth = defaultWidth;
		this.maxWidth = maxWidth;
	}

	public DtoTableColumn createDtoTableColumn() {
		SessionContext context = CurrentSessionContext.get();
		DtoTableColumn uiTableColumn = new DtoTableColumn();
		uiTableColumn.setPropertyName(propertyName);
		uiTableColumn.setIcon(context.resolveIcon(icon));
		uiTableColumn.setTitle(title);
		uiTableColumn.setField(field);
		uiTableColumn.setDefaultWidth(defaultWidth);
		uiTableColumn.setMinWidth(minWidth);
		uiTableColumn.setDefaultWidth(defaultWidth);
		uiTableColumn.setMaxWidth(maxWidth);
		uiTableColumn.setSortable(sortable);
		uiTableColumn.setResizeable(resizeable);
		uiTableColumn.setVisible(visible);
		uiTableColumn.setHeaderAlignment(headerAlignment);
		uiTableColumn.setHiddenIfOnlyEmptyCellsVisible(hiddenIfOnlyEmptyCellsVisible);
		uiTableColumn.setMessages(messages.stream().map(fieldMessage -> fieldMessage.createDtoFieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_HOVER_OR_FOCUS)).collect(Collectors.toList()));
		return uiTableColumn;
	}

	public List<FieldMessage> getMessages() {
		return messages;
	}

	public void addMessage(FieldMessage message) {
		this.messages.add(message);
		if (table != null) {
			table.updateColumnMessages(this);
		}
	}

	public void removeMessage(FieldMessage message) {
		this.messages.remove(message);
		if (table != null) {
			table.updateColumnMessages(this);
		}
	}

	public void setMessages(List<FieldMessage> messages) {
		this.messages = messages;
		if (table != null) {
			table.updateColumnMessages(this);
		}
	}

	public Icon getIcon() {
		return icon;
	}

	public TableColumn<RECORD, VALUE> setIcon(Icon icon) {
		this.icon = icon;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public TableColumn<RECORD, VALUE> setTitle(String title) {
		this.title = title;
		return this;
	}

	public Field<VALUE> getField() {
		return field;
	}

	public TableColumn<RECORD, VALUE> setField(Field<VALUE> field) {
		this.field = field;
		return this;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public TableColumn<RECORD, VALUE> setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		return this;
	}

	public int getDefaultWidth() {
		return defaultWidth;
	}

	public TableColumn<RECORD, VALUE> setDefaultWidth(int defaultWidth) {
		this.defaultWidth = defaultWidth;
		return this;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public TableColumn<RECORD, VALUE> setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public TableColumn<RECORD, VALUE> setVisible(boolean visible) {
		this.visible = visible;
		if (table != null) {
			table.updateColumnVisibility(this);
		}
		return this;
	}

	public boolean isSortable() {
		return sortable;
	}

	public TableColumn<RECORD, VALUE> setSortable(boolean sortable) {
		this.sortable = sortable;
		return this;
	}

	public boolean isResizeable() {
		return resizeable;
	}

	public TableColumn<RECORD, VALUE> setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
		return this;
	}

	public boolean isHiddenIfOnlyEmptyCellsVisible() {
		return hiddenIfOnlyEmptyCellsVisible;
	}

	public TableColumn<RECORD, VALUE> setHiddenIfOnlyEmptyCellsVisible(boolean hiddenIfOnlyEmptyCellsVisible) {
		this.hiddenIfOnlyEmptyCellsVisible = hiddenIfOnlyEmptyCellsVisible;
		return this;
	}

	/*package-private*/ void setTable(Table<RECORD> table) {
		this.table = table;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public TextAlignment getHeaderAlignment() {
		return headerAlignment;
	}

	public TableColumn<RECORD, VALUE> setHeaderAlignment(TextAlignment headerAlignment) {
		this.headerAlignment = headerAlignment;
		return this;
	}

	public ValueExtractor<RECORD, VALUE> getValueExtractor() {
		return valueExtractor;
	}

	public TableColumn<RECORD, VALUE> setValueExtractor(ValueExtractor<RECORD, VALUE> valueExtractor) {
		this.valueExtractor = valueExtractor;
		if (table != null) {
			table.refreshData();
		}
		return this;
	}

	public ValueInjector<RECORD, VALUE> getValueInjector() {
		return valueInjector;
	}

	public TableColumn<RECORD, VALUE> setValueInjector(ValueInjector<RECORD, VALUE> valueInjector) {
		this.valueInjector = valueInjector;
		return this;
	}
}
