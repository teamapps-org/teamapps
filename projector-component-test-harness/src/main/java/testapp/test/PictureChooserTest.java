package testapp.test;

import org.teamapps.projector.component.filefield.PictureChooser;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.test.formfield.AbstractFieldTest;

/**
 * @author Yann Massard (yamass@gmail.com)
 */
public class PictureChooserTest extends AbstractFieldTest<PictureChooser> {

	private PictureChooser field;

	public PictureChooserTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.NETWORK_CHECK, "Max file size (Bytes)", fieldGenerator.createNumberField("maxFileSize", 0, 100, 10_000_000_000L, false));

		PictureChooser valueEditor = new PictureChooser();
		valueEditor.setTargetImageSize(getComponent().getTargetImageWidth(), getComponent().getTargetImageHeight());
		valueEditor.setImageDisplaySize(getComponent().getTargetImageWidth() / 2, getComponent().getTargetImageHeight() / 2);
		valueEditor.onValueChanged.addListener(resource -> getComponent().setValue(resource));
		getComponent().onValueChanged.addListener(resource -> valueEditor.setValue(resource));
		responsiveFormLayout.addLabelAndField(MaterialIcon.EDIT, "Value", valueEditor);
	}

	@Override
	protected PictureChooser createField() {
		field = new PictureChooser();
		field.setTargetImageSize(200, 240);
//		field.setUploadUrl("http://localhost:8080/upload");
		return field;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/PictureChooser.html";
	}

}
