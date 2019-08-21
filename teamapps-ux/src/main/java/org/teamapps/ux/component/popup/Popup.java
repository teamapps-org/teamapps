package org.teamapps.ux.component.popup;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiPopup;
import org.teamapps.util.UiUtil;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;

public class Popup extends AbstractComponent {

	private Component contentComponent;
	private int x;
	private int y;
	private int width; // 0 = full width, -1 = auto
	private int height; // 0 = full height, -1 = auto
	private Color backgroundColor;
	private boolean modal = false;
	private Color dimmingColor = new Color(0, 0, 0, .2f);
	private boolean closeOnEscape; // close if the user presses escape
	private boolean closeOnClickOutside; // close if the user clicks onto the area outside the window

	public Popup(Component contentComponent) {
		this.contentComponent = contentComponent;
	}

	@Override
	public UiComponent createUiComponent() {
		UiPopup ui = new UiPopup();
		mapAbstractUiComponentProperties(ui);
		ui.setContentComponent(contentComponent.createUiComponentReference());
		ui.setX(x);
		ui.setY(y);
		ui.setWidth(width);
		ui.setHeight(height);
		ui.setBackgroundColor(UiUtil.createUiColor(backgroundColor));
		ui.setModal(modal);
		ui.setDimmingColor(UiUtil.createUiColor(dimmingColor));
		ui.setCloseOnEscape(closeOnEscape);
		ui.setCloseOnClickOutside(closeOnClickOutside);
		return ui;
	}

	public Component getContentComponent() {
		return contentComponent;
	}

	public void setContentComponent(Component contentComponent) {
		this.contentComponent = contentComponent;
		// queueCommandIfRendered(() -> new UiPopup.SetContentComponentCommand(getId(), contentComponent));
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		queueCommandIfRendered(() -> new UiPopup.SetPositionCommand(getId(), x, y));
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		queueCommandIfRendered(() -> new UiPopup.SetPositionCommand(getId(), x, y));
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		queueCommandIfRendered(() -> new UiPopup.SetPositionCommand(getId(), x, y));
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		queueCommandIfRendered(() -> new UiPopup.SetDimensionsCommand(getId(), width, height));
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		queueCommandIfRendered(() -> new UiPopup.SetDimensionsCommand(getId(), width, height));
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		queueCommandIfRendered(() -> new UiPopup.SetBackgroundColorCommand(getId(), UiUtil.createUiColor(backgroundColor)));
	}

	public boolean isModal() {
		return modal;
	}

	public void setModal(boolean modal) {
		this.modal = modal;
		// queueCommandIfRendered(() -> new UiPopup.SetModalCommand(getId(), modal));
	}

	public Color getDimmingColor() {
		return dimmingColor;
	}

	public void setDimmingColor(Color dimmingColor) {
		this.dimmingColor = dimmingColor;
		queueCommandIfRendered(() -> new UiPopup.SetDimmingColorCommand(getId(), UiUtil.createUiColor(dimmingColor)));
	}

	public boolean isCloseOnEscape() {
		return closeOnEscape;
	}

	public void setCloseOnEscape(boolean closeOnEscape) {
		this.closeOnEscape = closeOnEscape;
		// queueCommandIfRendered(() -> new UiPopup.SetCloseOnEscapeCommand(getId(), closeOnEscape));
	}

	public boolean isCloseOnClickOutside() {
		return closeOnClickOutside;
	}

	public void setCloseOnClickOutside(boolean closeOnClickOutside) {
		this.closeOnClickOutside = closeOnClickOutside;
		// queueCommandIfRendered(() -> new UiPopup.SetCloseOnClickOutsideCommand(getId(), closeOnClickOutside));
	}

	public void close() {
		queueCommandIfRendered(() -> new UiPopup.CloseCommand(getId()));
	}
}
