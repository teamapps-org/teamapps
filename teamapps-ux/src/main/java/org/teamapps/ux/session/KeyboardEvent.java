package org.teamapps.ux.session;

import org.teamapps.dto.KeyEventType;
import org.teamapps.ux.component.Component;

public class KeyboardEvent {
	private final KeyEventType eventType;
	private final Component sourceComponent;
	private final String code;
	private final boolean isComposing;
	private final String key;
	private final int charCode;
	private final int keyCode;
	private final String locale;
	private final int location;
	private final boolean repeat;
	private final boolean altKey;
	private final boolean ctrlKey;
	private final boolean shiftKey;
	private final boolean metaKey;

	public KeyboardEvent(KeyEventType eventType, Component sourceComponent, String code, boolean isComposing, String key, int charCode, int keyCode, String locale, int location, boolean repeat, boolean altKey, boolean ctrlKey, boolean shiftKey, boolean metaKey) {
		this.eventType = eventType;
		this.sourceComponent = sourceComponent;
		this.code = code;
		this.isComposing = isComposing;
		this.key = key;
		this.charCode = charCode;
		this.keyCode = keyCode;
		this.locale = locale;
		this.location = location;
		this.repeat = repeat;
		this.altKey = altKey;
		this.ctrlKey = ctrlKey;
		this.shiftKey = shiftKey;
		this.metaKey = metaKey;
	}

	public KeyEventType getEventType() {
		return eventType;
	}

	public Component getSourceComponent() {
		return sourceComponent;
	}

	public String getCode() {
		return code;
	}

	public boolean isComposing() {
		return isComposing;
	}

	public String getKey() {
		return key;
	}

	public int getCharCode() {
		return charCode;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public String getLocale() {
		return locale;
	}

	public int getLocation() {
		return location;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public boolean isAltKey() {
		return altKey;
	}

	public boolean isCtrlKey() {
		return ctrlKey;
	}

	public boolean isShiftKey() {
		return shiftKey;
	}

	public boolean isMetaKey() {
		return metaKey;
	}

	@Override
	public String toString() {
		return "KeyboardEvent{" +
				"eventType=" + eventType +
				", sourceComponent=" + sourceComponent +
				", code='" + code + '\'' +
				", isComposing=" + isComposing +
				", key='" + key + '\'' +
				", charCode=" + charCode +
				", keyCode=" + keyCode +
				", locale='" + locale + '\'' +
				", location=" + location +
				", repeat=" + repeat +
				", altKey=" + altKey +
				", ctrlKey=" + ctrlKey +
				", shiftKey=" + shiftKey +
				", metaKey=" + metaKey +
				'}';
	}
}
