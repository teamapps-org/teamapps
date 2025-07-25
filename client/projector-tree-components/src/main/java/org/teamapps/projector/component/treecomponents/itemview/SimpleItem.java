package org.teamapps.projector.component.treecomponents.itemview;

import org.teamapps.commons.event.Event;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;

public class SimpleItem<PAYLOAD> extends BaseTemplateRecord<PAYLOAD> {

	public Event<Void> onClick = new Event<>();

	public SimpleItem() {
	}

	public SimpleItem(String caption) {
		this(null, null, caption, null, (String) null);
	}

	public SimpleItem(Icon icon) {
		this(icon, null);
	}

	public SimpleItem(String caption, PAYLOAD payload) {
		this(null, null, caption, null, null, payload);
	}

	public SimpleItem(Icon icon, String caption) {
		this(icon, null, caption, null, null);
	}

	public SimpleItem(Icon icon, String caption, PAYLOAD payload) {
		this(icon, null, caption, null, null, payload);
	}

	public SimpleItem(Icon icon, String caption, String description) {
		this(icon, null, caption, description, null);
	}

	public SimpleItem(Icon icon, String caption, String description, PAYLOAD payload) {
		this(icon, null, caption, description, null, payload);
	}

	public SimpleItem(Icon icon, String caption, String description, String badge) {
		this(icon, null, caption, description, badge);
	}

	public SimpleItem(String image, String caption) {
		this(null, image, caption, null, (String) null);
	}

	public SimpleItem(String image, String caption, PAYLOAD payload) {
		this(null, image, caption, null, payload);
	}

	public SimpleItem(String image, String caption, String description) {
		this(null, image, caption, description, (String) null);
	}

	public SimpleItem(String image, String caption, String description, PAYLOAD payload) {
		this(null, image, caption, description, payload);
	}

	public SimpleItem(String image, String caption, String description, String badge) {
		this(null, image, caption, description, badge);
	}

	public SimpleItem(String image, String caption, String description, String badge, PAYLOAD payload) {
		this(null, image, caption, description, null, payload);
	}

	public SimpleItem(Icon icon, String image, String caption, String description, String badge) {
		this(icon, image, caption, description, badge, null);
	}

	public SimpleItem(Icon icon, String image, String caption, String description, String badge, PAYLOAD payload) {
		super(icon, image, caption, description, badge, payload);
	}


}
