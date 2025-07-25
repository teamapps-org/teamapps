

package testapp.util;

import org.teamapps.projector.component.core.toolbar.Toolbar;
import org.teamapps.projector.component.core.toolbar.ToolbarButton;
import org.teamapps.projector.component.core.toolbar.ToolbarButtonGroup;
import org.teamapps.projector.component.treecomponents.itemview.ItemGroup;
import org.teamapps.projector.component.treecomponents.itemview.ItemView;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.ConfigurationFieldGenerator;
import testapp.common.IconComboBoxEntry;

public class DemoComponentsGenerator {

	public static ItemView<BaseTemplateRecord, IconComboBoxEntry> createDummyItemView() {
		ItemView<BaseTemplateRecord, IconComboBoxEntry> itemView = new ItemView<>();
		for (int i = 0; i < 5; i++) {
			ItemGroup<BaseTemplateRecord, IconComboBoxEntry> group = itemView.addGroup(new BaseTemplateRecord(MaterialIcon.HELP, "Group " + i, "Some description"),
					BaseTemplates.ITEM_VIEW_ITEM);
			for (int j = 0; j < ConfigurationFieldGenerator.TOOL_ICON_ENTRIES.size(); j++) {
				group.addItem(ConfigurationFieldGenerator.TOOL_ICON_ENTRIES.get(j));
			}
		}
		return itemView;
	}

	public static Toolbar createDummyToolbar() {
		Toolbar toolbar = new Toolbar();
		ToolbarButtonGroup buttonGroup = new ToolbarButtonGroup();
		ConfigurationFieldGenerator.TOOL_ICON_ENTRIES.forEach(iconComboBoxEntry -> buttonGroup.addButton(new ToolbarButton(iconComboBoxEntry)));
		toolbar.addButtonGroup(buttonGroup);
		return toolbar;
	}

}
