

package testapp.test.formfield;

import org.teamapps.projector.component.core.field.ComponentField;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.core.panel.Panel;
import org.teamapps.projector.component.core.toolbar.Toolbar;
import org.teamapps.projector.component.core.toolbar.ToolbarButton;
import org.teamapps.projector.component.core.toolbar.ToolbarButtonGroup;
import org.teamapps.projector.component.core.toolbar.ToolbarButtonGroupPosition;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.infinitescroll.table.ListTableModel;
import org.teamapps.projector.component.infinitescroll.table.Table;
import org.teamapps.projector.component.infinitescroll.table.TableColumn;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.List;

public class ComponentFieldTest extends AbstractFieldTest<ComponentField> {

	public ComponentFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		var fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Height", fieldGenerator.createNumberField("height", 0, 0, 1000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Bordered", fieldGenerator.createCheckBox("bordered"));
	}

	@Override
	protected ComponentField createField() {
		Table<String> table = Table.<String>builder().build();

		table.addColumn(new TableColumn<String, String>("string", new TextField()).setValueExtractor(s -> s));
		ListTableModel<String> tableModel = new ListTableModel<>(List.of());
//		table.setModel(tableModel);
		Panel panel = new Panel();
		panel.setTitleBarHidden(true);
		panel.setContent(table);
		Toolbar toolbar = new Toolbar();
		ToolbarButton button = ToolbarButton.create(MaterialIcon.HELP, "asdf", "asdf");
		button.onClick.addListener(() -> table.setModel(new ListTableModel<>(List.of("aaa", "bbb"))));
//		button.onClick.addListener(() -> tableModel.addRecord("new"+System.currentTimeMillis()));
		toolbar.addButtonGroup(new ToolbarButtonGroup(List.of(button), ToolbarButtonGroupPosition.FIRST));

		panel.setToolbar(toolbar);
		return new ComponentField(panel, 200);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/TextField.html";
	}
}
