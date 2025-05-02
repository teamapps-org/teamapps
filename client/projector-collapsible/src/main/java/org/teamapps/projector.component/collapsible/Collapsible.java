package org.teamapps.projector.component.collapsible;


import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.DtoComponentConfig;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.icon.Icon;

@ClientObjectLibrary(CollapsibleLibrary.class)
public class Collapsible extends AbstractComponent implements DtoCollapsibleEventHandler {

	private final DtoCollapsibleClientObjectChannel clientObjectChannel = new DtoCollapsibleClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Boolean> onCollapseStateChanged = new ProjectorEvent<>(clientObjectChannel::toggleCollapseStateChangedEvent);

	private Icon icon;
	private String caption;
	private Component content;
	private boolean collapsed;

	public Collapsible() {
		this(null, null, null);
	}

	public Collapsible(Icon icon, String caption) {
		this(icon, caption, null);
	}

	public Collapsible(Icon icon, String caption, Component content) {
		this.icon = icon;
		this.caption = caption;
		this.content = content;
	}

	@Override
	public DtoComponentConfig createDto() {
		DtoCollapsible ui = new DtoCollapsible();
		mapAbstractConfigProperties(ui);
		ui.setIcon(getSessionContext().resolveIcon(icon));
		ui.setCaption(caption);
		ui.setContent(content);
		ui.setCollapsed(collapsed);
		return ui;
	}


	@Override
	public void handleCollapseStateChanged(boolean collapsed) {
		onCollapseStateChanged.fire(collapsed);
	}

	public Component getContent() {
		return content;
	}

	public void setContent(Component content) {
		this.content = content;
		clientObjectChannel.setContent(content);
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		clientObjectChannel.setIcon(getSessionContext().resolveIcon(icon));
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		clientObjectChannel.setCaption(caption);
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
		clientObjectChannel.setCollapsed(collapsed);
	}
}
