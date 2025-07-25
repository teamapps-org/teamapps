

package testapp.test;

import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.SplitDirection;
import org.teamapps.projector.component.core.SplitSizePolicy;
import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.core.splitpane.SplitPane;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.icon.composite.CompositeIcon;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

public class SplitPaneTest extends AbstractComponentTest<SplitPane> {
	
	private final Component firstChild = new DummyComponent();
	private final Component lastChild = new DummyComponent();
	private NumberField referenceChildSizeNumberField;


	public SplitPaneTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Split direction", fieldGenerator.createComboBoxForEnum("splitDirection"));

		ComboBox<SplitSizePolicy> sizePolicyComboBox = (ComboBox<SplitSizePolicy>) fieldGenerator.createComboBoxForEnum("sizePolicy");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Size policy", sizePolicyComboBox);

		referenceChildSizeNumberField = fieldGenerator.createNumberField("referenceChildSize", 2, 0, 1, false);
		sizePolicyComboBox.onValueChanged.addListener(sizePolicy -> {
			if (sizePolicy == SplitSizePolicy.RELATIVE) {
				referenceChildSizeNumberField.setMaxValue(1);
				referenceChildSizeNumberField.setSliderStep(.01);
				referenceChildSizeNumberField.setValue(.5);
				getComponent().setReferenceChildSize(.5f);
			} else {
				referenceChildSizeNumberField.setMaxValue(1000);
				referenceChildSizeNumberField.setSliderStep(1);
				referenceChildSizeNumberField.setValue(200);
				getComponent().setReferenceChildSize(200);
			}
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Reference child size", referenceChildSizeNumberField); // TODO update

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "First child min size", fieldGenerator.createNumberField("firstChildMinSize", 0, 0, 1000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Last child min size", fieldGenerator.createNumberField("lastChildMinSize", 0, 0, 1000, false));
		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Resizable", fieldGenerator.createCheckBox("resizable"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Fill whole area if single child", fieldGenerator.createCheckBox("fillIfSingleChild"));

//		private boolean collapseEmptyChildren = true;

		CheckBox firstChildCheckBox = new CheckBox();
		firstChildCheckBox.onValueChanged.addListener(setFirstChild -> {
			getComponent().setFirstChild(setFirstChild ? firstChild : null);
		});
		firstChildCheckBox.setValue(getComponent().getFirstChild() != null);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Set first child", firstChildCheckBox);

				CheckBox lastChildCheckBox = new CheckBox();
		lastChildCheckBox.onValueChanged.addListener(setlastChild -> {
			getComponent().setLastChild(setlastChild ? lastChild : null);
		});
		lastChildCheckBox.setValue(getComponent().getLastChild() != null);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Set last child", lastChildCheckBox);
	}



	@Override
	public SplitPane createComponent() {
		SplitPane splitPane = new SplitPane(SplitDirection.VERTICAL);
		splitPane.setFirstChild(new DummyComponent());
		splitPane.setLastChild(new DummyComponent());

		splitPane.onResized.addListener(referenceChildSize -> referenceChildSizeNumberField.setValue(referenceChildSize));

		return splitPane;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/DocumentViewer.html";
	}

}
