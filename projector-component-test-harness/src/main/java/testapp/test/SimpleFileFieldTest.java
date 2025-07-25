package testapp.test;

import org.teamapps.projector.component.filefield.simple.SimpleFileField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.test.formfield.AbstractFieldTest;

/**
 * @author Yann Massard (yamass@gmail.com)
 */
public class SimpleFileFieldTest extends AbstractFieldTest<SimpleFileField> {

	private SimpleFileField field;

	public SimpleFileFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.FORMAT_LIST_BULLETED, "Display type", fieldGenerator.createComboBoxForEnum("displayType"));

		responsiveFormLayout.addLabelAndField(MaterialIcon.NETWORK_CHECK, "Max file size (Bytes)", fieldGenerator.createNumberField("maxBytesPerFile", 0, 100, 10_000_000_000L, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.NETWORK_CHECK, "Max number of files", fieldGenerator.createNumberField("maxFiles", 0, 1, 1000L, false));

//		TextField fileTooLargeMessageTextField = new TextField("fileTooLargeMessage");
//		fileTooLargeMessageTextField.onValueChanged.addListener(fileTooLargeMessage -> {
//			this.fileTooLargeMessage = fileTooLargeMessage;
//			field.setFileTooLargeMessage(Caption.staticCaption(fileTooLargeMessage));
//		});
//		fileTooLargeMessageTextField.setValue(fileTooLargeMessage);
//		responsiveFormLayout.addLabelAndField(Icons.MESSAGE, new StaticCaption("\"File too large\" message"), fileTooLargeMessageTextField);
//
//		TextField uploadErrorMessageTextField = new TextField("uploadErrorMessage");
//		uploadErrorMessageTextField.onValueChanged.addListener(uploadErrorMessage -> {
//			this.uploadErrorMessage = uploadErrorMessage;
//			field.setUploadErrorMessage(Caption.staticCaption(uploadErrorMessage));
//		});
//		uploadErrorMessageTextField.setValue(uploadErrorMessage);
//		responsiveFormLayout.addLabelAndField(Icons.MESSAGE, new StaticCaption("\"Upload error\" message"), uploadErrorMessageTextField);
	}

	@Override
	protected SimpleFileField createField() {
		field = new SimpleFileField();
		field.setUploadUrl("http://localhost:8080/upload");
		return field;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/SimpleFileField.html";
	}

}
