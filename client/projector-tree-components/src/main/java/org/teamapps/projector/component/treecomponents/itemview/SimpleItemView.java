package org.teamapps.projector.component.treecomponents.itemview;

import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;

public class SimpleItemView<PAYLOAD> extends ItemView<BaseTemplateRecord<Void>, SimpleItem<PAYLOAD>> {

	public SimpleItemView() {
		onItemClicked.addListener(simpleItemItemClickedEventData -> {
			SimpleItem<PAYLOAD> item = simpleItemItemClickedEventData.item();
			item.onClick.fire(null);
		});
		setVerticalPadding(0);
		setHorizontalPadding(2);
		setGroupSpacing(2);
	}

	public SimpleItemGroup<PAYLOAD> addSingleColumnGroup(Icon icon, String title) {
		SimpleItemGroup<PAYLOAD> group = SimpleItemGroup.singleColumnGroup(icon, title);
		addGroup(group);
		return group;
	}

	public SimpleItemGroup<PAYLOAD> addTwoColumnGroup(Icon icon, String title) {
		SimpleItemGroup<PAYLOAD> group = SimpleItemGroup.twoColumnGroup(icon, title);
		addGroup(group);
		return group;
	}

}
