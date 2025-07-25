

package testapp.test;

import org.teamapps.projector.animation.RepeatableAnimation;
import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.flexcontainer.VerticalLayout;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.notificationbar.NotificationBar;
import org.teamapps.projector.component.notificationbar.NotificationBarItem;
import org.teamapps.projector.format.Spacing;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.ArrayList;
import java.util.List;


public class NotificationBarTest extends AbstractComponentTest<NotificationBar> {

	private Icon icon = MaterialIcon.BATTERY_ALERT;
	private String text = "Battery is low!";
	private String actionLinkText = "Do it...";
	private RepeatableAnimation iconAnimation = RepeatableAnimation.HEART_BEAT;
	private boolean dismissible = true;
	private int displayTimeInMillis = 5_000;
	private boolean progressBarVisible = true;
	private Color backgroundColor;
	private Color borderColor;
	private Color textColor;
	private Color actionLinkColor;
	private Integer padding;

	private final List<NotificationBarItem> items = new ArrayList<>();

	public NotificationBarTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.SETTINGS, "Configuration").setGridGap(5);
		var itemFieldGenerator = new ConfigurationFieldGenerator<>(this, getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "icon", itemFieldGenerator.createComboBoxForIcon("icon"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "text", itemFieldGenerator.createTextField("text"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "actionLinkText", itemFieldGenerator.createTextField("actionLinkText"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "iconAnimation", itemFieldGenerator.createComboBoxForEnum("iconAnimation"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "dismissible", itemFieldGenerator.createCheckBox("dismissible"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "displayTimeInMillis", itemFieldGenerator.createNumberField("displayTimeInMillis", 0, 0, 60_000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "progressBarVisible", itemFieldGenerator.createCheckBox("progressBarVisible"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "backgroundColor", itemFieldGenerator.createColorPicker("backgroundColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "borderColor", itemFieldGenerator.createColorPicker("borderColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "textColor", itemFieldGenerator.createColorPicker("textColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "actionLinkColor", itemFieldGenerator.createColorPicker("actionLinkColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "padding", itemFieldGenerator.createNumberField("padding", 0, 0, 30, true));
//		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "entranceAnimation", itemFieldGenerator.createComboBoxForEnum("entranceAnimation"));
//		responsiveFormLayout.addLabelAndField(MaterialIcon.SETTINGS, "exitAnimation", itemFieldGenerator.createComboBoxForEnum("exitAnimation"));

		Button addItemButton = Button.create(MaterialIcon.ADD, "Add item");
		responsiveFormLayout.addLabelAndField(MaterialIcon.ADD, "Add item", addItemButton);
		addItemButton.onClick.addListener(() -> {
			final NotificationBarItem item = new NotificationBarItem()
					.setIcon(icon)
					.setText(text)
					.setDismissible(dismissible)
					.setDisplayTimeInMillis(displayTimeInMillis)
					.setProgressBarVisible(progressBarVisible)
					.setBackgroundColor(backgroundColor)
					.setBorderColor(borderColor)
					.setTextColor(textColor)
					.setPadding(padding != null ? Spacing.px(padding) : null)
					.setIconAnimation(iconAnimation)
					.setActionLinkText(actionLinkText)
					.setActionLinkColor(actionLinkColor);
			getComponent().addItem(item);
			items.add(item);
		});

		Button removeItemButton = Button.create(MaterialIcon.REMOVE, "Remove item");
		responsiveFormLayout.addLabelAndField(MaterialIcon.REMOVE, "Remove item", removeItemButton);
		removeItemButton.onClick.addListener(() -> {
			final NotificationBarItem item = items.remove(0);
			getComponent().removeItem(item);
		});

		Button updateItemButton = Button.create(MaterialIcon.REMOVE, "Update item");
		responsiveFormLayout.addLabelAndField(MaterialIcon.REMOVE, "Update item", updateItemButton);
		updateItemButton.onClick.addListener(() -> {
			final NotificationBarItem item = items.get(0);
			item
					.setIcon(icon)
					.setText(text)
					.setDismissible(dismissible)
					.setDisplayTimeInMillis(displayTimeInMillis)
					.setProgressBarVisible(progressBarVisible)
					.setBackgroundColor(backgroundColor)
					.setBorderColor(borderColor)
					.setTextColor(textColor)
					.setPadding(padding != null ? Spacing.px(padding) : null)
					.setIconAnimation(iconAnimation)
					.setActionLinkText(actionLinkText)
					.setActionLinkColor(actionLinkColor);
		});
	}

	@Override
	public Component wrapComponent(NotificationBar component) {
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponent(component);
		verticalLayout.addComponentFillRemaining(new DummyComponent());
		return verticalLayout;
	}

	@Override
	public NotificationBar createComponent() {
		return new NotificationBar();
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/NotificationBar.html";
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public RepeatableAnimation getIconAnimation() {
		return iconAnimation;
	}

	public void setIconAnimation(RepeatableAnimation iconAnimation) {
		this.iconAnimation = iconAnimation;
	}

	public boolean isDismissible() {
		return dismissible;
	}

	public void setDismissible(boolean dismissible) {
		this.dismissible = dismissible;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public void setDisplayTimeInMillis(int displayTimeInMillis) {
		this.displayTimeInMillis = displayTimeInMillis;
	}

	public boolean isProgressBarVisible() {
		return progressBarVisible;
	}

	public void setProgressBarVisible(boolean progressBarVisible) {
		this.progressBarVisible = progressBarVisible;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public Integer getPadding() {
		return padding;
	}

	public void setPadding(Integer padding) {
		this.padding = padding;
	}

	public String getActionLinkText() {
		return actionLinkText;
	}

	public void setActionLinkText(String actionLinkText) {
		this.actionLinkText = actionLinkText;
	}

	public Color getActionLinkColor() {
		return actionLinkColor;
	}

	public void setActionLinkColor(Color actionLinkColor) {
		this.actionLinkColor = actionLinkColor;
	}
}
