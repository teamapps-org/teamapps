package org.teamapps.ux.component.field;

import org.jetbrains.annotations.NotNull;
import org.teamapps.dto.UiComponentReference;
import org.teamapps.dto.UiFieldGroup;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.flexcontainer.FlexSizeUnit;
import org.teamapps.ux.component.flexcontainer.FlexSizingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldGroup extends AbstractComponent {

	private List<AbstractField> fields = new ArrayList<>();

	@Override
	public UiFieldGroup createUiComponent() {
		UiFieldGroup uiFieldGroup = new UiFieldGroup();
		mapAbstractUiComponentProperties(uiFieldGroup);
		uiFieldGroup.setFields(createUiFieldReferences());
		return uiFieldGroup;
	}

	@NotNull
	private List<UiComponentReference> createUiFieldReferences() {
		return fields.stream()
				.map(c -> c.createUiComponentReference())
				.collect(Collectors.toList());
	}

	private void addField(AbstractField field, int index) {
		this.fields.remove(field);
		if (index > this.fields.size()) {
			index = this.fields.size();
		}
		this.fields.add(index, field);
		queueCommandIfRendered(() -> new UiFieldGroup.SetFieldsCommand(getId(), createUiFieldReferences()));
	}

	public void addField(AbstractField field, FlexSizingPolicy sizingPolicy) {
		field.setCssStyle("flex", sizingPolicy.toCssValue());
		addField(field, this.fields.size());
	}

	public void addFieldFillRemaining(AbstractField field) {
		addField(field, new FlexSizingPolicy(1, FlexSizeUnit.PIXEL, 1, 1));
	}

	public void addFieldAutoSize(AbstractField field) {
		addField(field, new FlexSizingPolicy( 0, 0));
	}

	public void addField(AbstractField field, FlexSizingPolicy sizingPolicy, int index) {
		field.setCssStyle("flex", sizingPolicy.toCssValue());
		addField(field, index);
	}

	public void addFieldFillRemaining(AbstractField field, int index) {
		addField(field, new FlexSizingPolicy(1, FlexSizeUnit.PIXEL, 1, 1), index);
	}

	public void addFieldAutoSize(AbstractField field, int index) {
		addField(field, new FlexSizingPolicy( 0, 0), index);
	}

	public void removeField(AbstractField field) {
		this.fields.remove(field);
		queueCommandIfRendered(() -> new UiFieldGroup.SetFieldsCommand(getId(), createUiFieldReferences()));
	}

}
