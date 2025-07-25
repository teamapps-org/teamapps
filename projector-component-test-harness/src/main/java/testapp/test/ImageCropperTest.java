

package testapp.test;

import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.filefield.imagecropper.ImageCropper;
import org.teamapps.projector.component.filefield.imagecropper.ImageCropperSelection;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.Arrays;

public class ImageCropperTest extends AbstractComponentTest<ImageCropper> {

	private int selectionLeft;
	private int selectionTop;
	private int selectionWidth;
	private int selectionHeight;
	private NumberField selectionLeftNumberField;
	private NumberField selectionTopNumberField;
	private NumberField selectionWidthNumberField;
	private NumberField selectionHeightNumberField;

	public ImageCropperTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(this.getComponent(), this.getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Image URL", fieldGenerator.createComboBoxForList("imageUrl", Arrays.asList(
				"https://images.unsplash.com/photo-1463453091185-61582044d556",
				"https://images.unsplash.com/photo-1494790108377-be9c29b29330"
		)));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Selection mode", fieldGenerator.createComboBoxForEnum("selectionMode"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Aspect ratio", fieldGenerator.createNumberField("aspectRatio", 2, 0, 4, false));

		responsiveFormLayout.addSection(MaterialIcon.HELP, "Selection").setGridGap(5);
		ConfigurationFieldGenerator selectionFieldGenerator = new ConfigurationFieldGenerator(this, this.getTestContext());
		selectionLeftNumberField = selectionFieldGenerator.createNumberField("selectionLeft", 0, 0, 5000, false);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Selection left", selectionLeftNumberField);
		selectionTopNumberField = selectionFieldGenerator.createNumberField("selectionTop", 0, 0, 5000, false);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Selection top", selectionTopNumberField);
		selectionWidthNumberField = selectionFieldGenerator.createNumberField("selectionWidth", 0, 0, 5000, false);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Selection width", selectionWidthNumberField);
		selectionHeightNumberField = selectionFieldGenerator.createNumberField("selectionHeight", 0, 0, 5000, false);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Selection height", selectionHeightNumberField);
	}

	private void updateSelection() {
		getComponent().setSelection(new ImageCropperSelection(this.selectionLeft, this.selectionTop, this.selectionWidth, this.selectionHeight));
	}

	@Override
	protected ImageCropper createComponent() {
		ImageCropper imageCropper = new ImageCropper();
		imageCropper.setImageUrl("https://images.unsplash.com/photo-1463453091185-61582044d556");

		imageCropper.onSelectionChanged.addListener(selection -> {
			this.selectionLeft = selection.getLeft();
			this.selectionTop = selection.getTop();
			this.selectionWidth = selection.getWidth();
			this.selectionHeight = selection.getHeight();
			this.updateSelectionFieldValues();
		});
		return imageCropper;
	}

	private void updateSelectionFieldValues() {
		this.selectionLeftNumberField.setValue(this.selectionLeft);
		this.selectionTopNumberField.setValue(this.selectionTop);
		this.selectionWidthNumberField.setValue(this.selectionWidth);
		this.selectionHeightNumberField.setValue(this.selectionHeight);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return null;
	}

	public int getSelectionLeft() {
		return selectionLeft;
	}

	public int getSelectionTop() {
		return selectionTop;
	}

	public int getSelectionWidth() {
		return selectionWidth;
	}

	public int getSelectionHeight() {
		return selectionHeight;
	}

	public void setSelectionLeft(int selectionLeft) {
		this.selectionLeft = selectionLeft;
		updateSelection();
	}

	public void setSelectionTop(int selectionTop) {
		this.selectionTop = selectionTop;
		updateSelection();
	}

	public void setSelectionWidth(int selectionWidth) {
		this.selectionWidth = selectionWidth;
		updateSelection();
	}

	public void setSelectionHeight(int selectionHeight) {
		this.selectionHeight = selectionHeight;
		updateSelection();
	}
}
