package org.teamapps.ux.component.script;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiScript;
import org.teamapps.ux.component.AbstractComponent;

import java.util.List;

public class Script extends AbstractComponent {

	private final String script;

	public Script(String script) {
		this.script = script;
	}

	@Override
	public UiComponent createUiComponent() {
		UiScript uiScript = new UiScript(script);
		mapAbstractUiComponentProperties(uiScript);
		return uiScript;
	}

	public void callFunction(String name, Object... parameters) {
		queueCommandIfRendered(() -> new UiScript.CallFunctionCommand(getId(), name, List.of(parameters)));
	}
}
