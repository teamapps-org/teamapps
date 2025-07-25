package org.teamapps.projector.component.treecomponents.itemview;

import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

public class SimpleItemGroup<PAYLOAD> extends ItemGroup<BaseTemplateRecord<Void>, SimpleItem<PAYLOAD>> {

	public static <PAYLOAD> SimpleItemGroup<PAYLOAD> singleColumnGroup(Icon icon, String title) {
		SimpleItemGroup<PAYLOAD> group = new SimpleItemGroup<>(icon, title);
		group.setButtonWidth(0);
		return group;
	}

	public static <PAYLOAD> SimpleItemGroup<PAYLOAD> twoColumnGroup(Icon icon, String title) {
		SimpleItemGroup<PAYLOAD> group = new SimpleItemGroup<>(icon, title);
		group.setButtonWidth(0.5f);
		return group;
	}

	public SimpleItemGroup(Icon icon, String title) {
		this(icon, title, BaseTemplates.MENU_ITEM);
	}

	public SimpleItemGroup(Icon icon, String title, Template template) {
		super(new BaseTemplateRecord<>(icon, title), template);
	}

	public SimpleItem<PAYLOAD> addItem(Icon icon, String title, String caption) {
		SimpleItem<PAYLOAD> item = new SimpleItem<>(icon, title, caption);
		addItem(item);
		return item;
	}

	public SimpleItem<PAYLOAD> addItem(Icon icon, String title, String caption, PAYLOAD payload) {
		SimpleItem<PAYLOAD> item = new SimpleItem<>(icon, title, caption, payload);
		addItem(item);
		return item;
	}

}
