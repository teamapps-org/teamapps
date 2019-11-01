package org.teamapps.ux.component.field;

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.UiClientRecord;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiTemplateField;
import org.teamapps.event.Event;
import org.teamapps.ux.component.template.Template;

import java.util.Collections;

public class TemplateField<RECORD> extends AbstractField<RECORD> {

	public final Event<Void> onClicked = new Event<>();

	private Template template;
	private PropertyExtractor<RECORD> propertyExtractor = new BeanPropertyExtractor<>();

	public TemplateField(Template template) {
		this.template = template;
	}

	public TemplateField(Template template, RECORD value) {
		this.template = template;
		this.setValue(value);
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		switch (event.getUiEventType()) {
			case UI_TEMPLATE_FIELD_CLICKED:
				onClicked.fire();
		}
	}

	@Override
	public UiTemplateField createUiComponent() {
		UiTemplateField ui = new UiTemplateField();
		mapAbstractFieldAttributesToUiField(ui);
		ui.setTemplate(template.createUiTemplate());
		ui.setValue(createUiRecord(getValue()));
		return ui;
	}

	public RECORD convertUiValueToUxValue(Object value) {
		return getValue();
	}

	public Object convertUxValueToUiValue(RECORD record) {
		if (record == null) {
			return null;
		} else {
			return createUiRecord(record);
		}
	}

	private UiClientRecord createUiRecord(RECORD record) {
		if (record == null) {
			return null;
		}
		UiClientRecord uiClientRecord = new UiClientRecord();
		uiClientRecord.setValues(propertyExtractor.getValues(record, template.getDataKeys()));
		return uiClientRecord;
	}

	public Template getTemplate() {
		return template;
	}

	public TemplateField<RECORD> setTemplate(Template template) {
		this.template = template;
		queueCommandIfRendered(() -> new UiTemplateField.UpdateCommand(getId(), createUiComponent()));
		return this;
	}

	public PropertyExtractor<RECORD> getPropertyExtractor() {
		return propertyExtractor;
	}

	public TemplateField<RECORD> setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.propertyExtractor = propertyExtractor;
		queueCommandIfRendered(() -> new UiTemplateField.UpdateCommand(getId(), createUiComponent()));
		return this;
	}
}
