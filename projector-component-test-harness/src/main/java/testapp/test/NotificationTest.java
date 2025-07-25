

package testapp.test;

import org.teamapps.projector.animation.EntranceAnimation;
import org.teamapps.projector.animation.ExitAnimation;
import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.DisplayField;
import org.teamapps.projector.component.core.field.TemplateField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.format.Spacing;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.notification.Notification;
import org.teamapps.projector.notification.NotificationPosition;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.time.Duration;

public class NotificationTest extends AbstractComponentTest<Component> {

	private NotificationPosition position = NotificationPosition.TOP_RIGHT;
	private EntranceAnimation entranceAnimation = EntranceAnimation.FADE_IN_UP;
	private ExitAnimation exitAnimation = ExitAnimation.FADE_OUT_UP;
	private Color backgroundColor = null;
	private int padding = 10;
	int displayTimeInMillis = 3000; // 0 = display until user closes it actively
	boolean dismissible = true;
	boolean showProgressBar = true;

	public NotificationTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator<NotificationTest> fieldGenerator = new ConfigurationFieldGenerator<>(this, getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Position", fieldGenerator.createComboBoxForEnum("position"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Entrance animation", fieldGenerator.createComboBoxForEnum("entranceAnimation"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Exit animation", fieldGenerator.createComboBoxForEnum("exitAnimation"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Background color", fieldGenerator.createColorPicker("backgroundColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Padding", fieldGenerator.createNumberField("padding", 0, 0, 100, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Display time (ms)", fieldGenerator.createNumberField("displayTimeInMillis", 0, 0, 60000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Dismissible", fieldGenerator.createCheckBox("dismissible"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show progress bar", fieldGenerator.createCheckBox("showProgressBar"));

		Button showNotification = Button.create("Show notification");
		showNotification.onClick.addListener(() -> {
			Notification notification = new Notification();
			notification.setBackgroundColor(backgroundColor);
			notification.setPadding(new Spacing(padding));
			notification.setDismissible(dismissible);
			notification.setProgressBarEnabled(showProgressBar);
			notification.setContent(new TemplateField<>(BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES, new BaseTemplateRecord<Void>(MaterialIcon.ALARM_ON, "Some header", "Some description text...")));
			notification.show(Duration.ofMillis(displayTimeInMillis), position, entranceAnimation, exitAnimation);
		});
		responsiveFormLayout.addLabelAndComponent(showNotification);

	}

	@Override
	public Component createComponent() {
		return new DisplayField();
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/NavigationBar.html";
	}

	public NotificationPosition getPosition() {
		return position;
	}

	public void setPosition(NotificationPosition position) {
		this.position = position;
	}

	public EntranceAnimation getEntranceAnimation() {
		return entranceAnimation;
	}

	public void setEntranceAnimation(EntranceAnimation entranceAnimation) {
		this.entranceAnimation = entranceAnimation;
	}

	public ExitAnimation getExitAnimation() {
		return exitAnimation;
	}

	public void setExitAnimation(ExitAnimation exitAnimation) {
		this.exitAnimation = exitAnimation;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public void setDisplayTimeInMillis(int displayTimeInMillis) {
		this.displayTimeInMillis = displayTimeInMillis;
	}

	public boolean isDismissible() {
		return dismissible;
	}

	public void setDismissible(boolean dismissible) {
		this.dismissible = dismissible;
	}

	public boolean isShowProgressBar() {
		return showProgressBar;
	}

	public void setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
	}
}
