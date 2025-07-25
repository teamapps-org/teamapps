

package testapp.test.formfield;

import org.teamapps.projector.component.core.field.TemplateField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.*;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import org.teamapps.projector.template.mustache.MustacheTemplate;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.common.TemplateComboBoxEntry;

import java.util.Arrays;
import java.util.List;

import static org.teamapps.projector.format.JustifyContent.SPACE_AROUND;

public class TemplateFieldTest extends AbstractFieldTest<TemplateField<TemplateFieldTest.TemplateFieldTestRecord>> {

	private static final List<TemplateComboBoxEntry> TEMPLATE_COMBO_BOX_ENTRIES = Arrays.asList(
			new TemplateComboBoxEntry("Small icon single line", BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE),
			new TemplateComboBoxEntry("Medium icon single line", BaseTemplates.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE),
			new TemplateComboBoxEntry("Large icon single line", BaseTemplates.LIST_ITEM_LARGE_ICON_SINGLE_LINE),
			new TemplateComboBoxEntry("Very large icon two lines", BaseTemplates.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES),
			new TemplateComboBoxEntry("Large icon two lines", BaseTemplates.LIST_ITEM_LARGE_ICON_TWO_LINES),
			new TemplateComboBoxEntry("Medium icon two lines", BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES),
			new TemplateComboBoxEntry("HTML template", new MustacheTemplate("<div class=\"html-template\" style=\"color: red; display: flex; align-items: center; padding: 2px;\">{{caption}}</div>")),
			new TemplateComboBoxEntry("Custom Grid Template", new GridTemplate()
					.addColumn(SizingPolicy.AUTO, 2, 2)
					.addRow(SizeType.FIXED, 32, 32, 2, 2)
					.addRow(SizeType.AUTO, 0, 0, 0, 0)
					.addRow(SizeType.AUTO, 0, 0, 0, 0)
					.addElement(new IconElement("icon", 0, 0, 32)
							.setVerticalAlignment(VerticalElementAlignment.TOP)
							.setHorizontalAlignment(HorizontalElementAlignment.CENTER))
					.addElement(new TextElement("caption", 1, 0)
							.setWrapLines(true)
							.setVerticalAlignment(VerticalElementAlignment.TOP)
							.setHorizontalAlignment(HorizontalElementAlignment.CENTER))
					.addElement(new FloatingElement(2, 0)
							.addElement(new IconElement("miniIcon1", 32))
							.addElement(new TextElement("miniText1"))
							.addElement(new IconElement("miniIcon2", 32))
							.addElement(new TextElement("miniText2"))
							.setJustifyContent(SPACE_AROUND)
					))
	);

	public TemplateFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		var fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.FORMAT_PAINT, "template", fieldGenerator.createComboBoxForList("template", TEMPLATE_COMBO_BOX_ENTRIES,
				field -> TEMPLATE_COMBO_BOX_ENTRIES.stream().filter(e -> e.getTemplate() == field.getTemplate()).findFirst().orElseThrow(),
				(field, entry) -> field.setTemplate(entry.getTemplate())));
	}

	@Override
	protected TemplateField<TemplateFieldTestRecord> createField() {
		TemplateField<TemplateFieldTestRecord> field = new TemplateField<>(TEMPLATE_COMBO_BOX_ENTRIES.get(TEMPLATE_COMBO_BOX_ENTRIES.size() - 1).getTemplate());
		field.setValue(new TemplateFieldTestRecord(
				MaterialIcon.SMS, "SMS received", "You received an SMS 2 minutes ago.",
				MaterialIcon.MAIL, "2",
				MaterialIcon.ALARM, "10"
		));
		return field;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/TemplateField.html";
	}

	public record TemplateFieldTestRecord(
			Icon icon, String caption, String description,
			Icon miniIcon1, String miniText1,
			Icon miniIcon2, String miniText2
	) {
	}

}
