

package testapp.test;

import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.field.DisplayField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.mobilelayout.MobileLayout;
import org.teamapps.projector.component.mobilelayout.NavigationBar;
import org.teamapps.projector.component.mobilelayout.NavigationBarButton;
import org.teamapps.projector.component.treecomponents.itemview.ItemView;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.notification.Notification;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.common.IconComboBoxEntry;
import testapp.util.DemoComponentsGenerator;

import java.time.Duration;

public class NavigationBarTest extends AbstractComponentTest<NavigationBar> {

	public NavigationBarTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Background color", fieldGenerator.createColorPicker("backgroundColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Border color", fieldGenerator.createColorPicker("borderColor"));

		}

	@Override
	public NavigationBar createComponent() {
		NavigationBar navigationBar = new NavigationBar();
		navigationBar.setBackgroundColor(RgbaColor.MATERIAL_GREY_100);
		navigationBar.setBorderColor(RgbaColor.MATERIAL_GREY_300);

		var button1 = NavigationBarButton.create(MaterialIcon.HELP, "Orange");
		ItemView<BaseTemplateRecord, IconComboBoxEntry> dummyItemView = DemoComponentsGenerator.createDummyItemView();
		button1.onClick.addListener(() -> navigationBar.showOrHideFanoutComponent(dummyItemView));
		navigationBar.addButton(button1);
		var button2 = NavigationBarButton.create(MaterialIcon.HELP, "Banana");
		DummyComponent dummyComponent = new DummyComponent("Fanout dummy component...");
		button2.onClick.addListener(() -> navigationBar.showOrHideFanoutComponent(dummyComponent));
		navigationBar.addButton(button2);
		var button3 = NavigationBarButton.create(MaterialIcon.HELP);
		button3.onClick.addListener(() -> Notification.createWithIconCaptionDescription(MaterialIcon.HELP, "Button \"" + button3.getRecord().getCaption() + "\" pressed.", "Great!").show(Duration.ofSeconds(2)));
		navigationBar.addButton(button3);
		var button4 = NavigationBarButton.create(MaterialIcon.HELP, "Lemon");
		button4.onClick.addListener(() -> Notification.createWithIconCaptionDescription(MaterialIcon.HELP, "Button \"" + button4.getRecord().getCaption() + "\" pressed.", "Great!").show(Duration.ofSeconds(2)));
		navigationBar.addButton(button4);

		return navigationBar;
	}

	@Override
	public Component wrapComponent(NavigationBar component) {
		MobileLayout mobileLayout = new MobileLayout();
		DisplayField displayField = new DisplayField();
		displayField.setShowHtml(true);
		displayField.setValue("<div style=\"height: 100%; display: flex; align-items: center; justify-content: center\">↓ See below ↓</div>");
		mobileLayout.setContent(displayField);
		mobileLayout.setNavigationBar(component);
		return mobileLayout;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/NavigationBar.html";
	}

}
