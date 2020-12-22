package org.teamapps.ux.component.div;

import org.teamapps.dto.UiDiv;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;

public class Div extends AbstractComponent {

	private Component content;

	public Div(Component content) {
		this.content = content;
	}

	public Div() {
	}

	@Override
	public UiDiv createUiComponent() {
		UiDiv ui = new UiDiv();
		mapAbstractUiComponentProperties(ui);
		ui.setContent(content.createUiReference());
		return ui;
	}

	public Component getContent() {
		return content;
	}

	public void setContent(Component content) {
		this.content = content;
		queueCommandIfRendered(() -> new UiDiv.SetContentCommand(getId(), content != null ? content.createUiReference() : null));
	}
}
