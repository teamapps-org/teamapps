

package testapp.test;

import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.documentviewer.DocumentViewer;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.format.Border;
import org.teamapps.projector.format.BoxShadow;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

public class DocumentViewerTest extends AbstractComponentTest<DocumentViewer> {


	public DocumentViewerTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Page display mode", fieldGenerator.createComboBoxForEnum("displayMode"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Zoom factor", fieldGenerator.createNumberField("zoomFactor", 2, .01d, 10, false));

		CheckBox pageBorderCheckBox = new CheckBox("Page border");
		pageBorderCheckBox.onValueChanged.addListener(showBorder -> {
			updatePageBorder(showBorder);
		});
		pageBorderCheckBox.setValue(true);
		updatePageBorder(true);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Page border", pageBorderCheckBox);

		CheckBox pageShadowCheckBox = new CheckBox("Page shadow");
		pageShadowCheckBox.onValueChanged.addListener(showShadow -> {
			updatePageShadow(showShadow);
		});
		pageShadowCheckBox.setValue(true);
		updatePageShadow(true);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Page shadow", pageShadowCheckBox);

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Padding", fieldGenerator.createNumberField("padding", 0, 0, 100, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Page spacing", fieldGenerator.createNumberField("pageSpacing", 0, 0, 100, false));

		}

	private void updatePageBorder(boolean showBorder) {
		if (showBorder) {
			getComponent().setPageBorder(new Border(RgbaColor.MATERIAL_GREY_700, 1, 3));
		} else {
			getComponent().setPageBorder(null);
		}
	}

	private void updatePageShadow(boolean showShadow) {
		if (showShadow) {
			getComponent().setPageShadow(new BoxShadow(1, 1, 10, 0, RgbaColor.MATERIAL_GREY_700));
		} else {
			getComponent().setPageShadow(null);
		}
	}

	@Override
	public DocumentViewer createComponent() {
		DocumentViewer documentViewer = new DocumentViewer();
		documentViewer.setPageUrls(
				"/static-resources/sample-document-1.png", "/static-resources/sample-document-2.png",
				"/static-resources/sample-document-1.png", "/static-resources/sample-document-2.png",
				"/static-resources/sample-document-1.png", "/static-resources/sample-document-2.png",
				"/static-resources/sample-document-1.png", "/static-resources/sample-document-2.png",
				"/static-resources/sample-document-1.png", "/static-resources/sample-document-2.png"
		);
		return documentViewer;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/DocumentViewer.html";
	}

}
