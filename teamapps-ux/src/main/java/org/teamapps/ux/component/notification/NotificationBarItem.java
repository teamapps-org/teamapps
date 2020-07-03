package org.teamapps.ux.component.notification;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiNotificationBarItem;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.util.UiUtil;
import org.teamapps.ux.component.animation.RepeatableAnimation;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.session.SessionContext;

import java.util.UUID;

public class NotificationBarItem {

	public final Event<Void> onClicked = new Event<>();
	public final Event<NotificationBarItemClosedEvent.ClosingReason> onClosed = new Event<>();

	private final String uiId = UUID.randomUUID().toString();

	private final Icon icon;
	private final String text;

	private final RepeatableAnimation iconAnimation;

	private final boolean dismissible;
	private final int displayTimeInMillis; // <= 0: display until user closes it actively
	private final boolean progressBarVisible;

	private final Color backgroundColor;
	private final Color borderColor;
	private final Color textColor;
	private final Spacing padding;

	public NotificationBarItem(Icon icon, String text) {
		this(icon, text, true);
	}

	public NotificationBarItem(Icon icon, String text, boolean dismissible) {
		this(icon, text, dismissible, 0, false);
	}

	public NotificationBarItem(Icon icon, String text, boolean dismissible, int displayTimeInMillis, boolean progressBarVisible) {
		this(icon, text, dismissible, displayTimeInMillis, progressBarVisible, null, null, null, null, null);
	}

	public NotificationBarItem(Icon icon, String text, boolean dismissible, int displayTimeInMillis, boolean progressBarVisible, Color backgroundColor, Color borderColor, Color textColor,
	                           Spacing padding, RepeatableAnimation iconAnimation) {
		this.icon = icon;
		this.text = text;
		this.dismissible = dismissible;
		this.displayTimeInMillis = displayTimeInMillis;
		this.progressBarVisible = progressBarVisible;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.textColor = textColor;
		this.padding = padding;
		this.iconAnimation = iconAnimation;
	}

	public UiNotificationBarItem toUiNotificationBarItem() {
		UiNotificationBarItem ui = new UiNotificationBarItem();
		ui.setId(uiId);
		ui.setIcon(SessionContext.current().resolveIcon(icon));
		ui.setIconAnimation(iconAnimation.toUiRepeatableAnimation());
		ui.setText(text);
		ui.setDismissible(dismissible);
		ui.setDisplayTimeInMillis(displayTimeInMillis);
		ui.setProgressBarVisible(progressBarVisible);
		ui.setBackgroundColor(UiUtil.createUiColor(backgroundColor));
		ui.setBorderColor(UiUtil.createUiColor(borderColor));
		ui.setTextColor(UiUtil.createUiColor(textColor));
		ui.setPadding(padding != null ? padding.createUiSpacing() : null);
		return ui;
	}

	public String getUiId() {
		return uiId;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getText() {
		return text;
	}

	public boolean isDismissible() {
		return dismissible;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public boolean isProgressBarVisible() {
		return progressBarVisible;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public Spacing getPadding() {
		return padding;
	}

	public RepeatableAnimation getIconAnimation() {
		return iconAnimation;
	}

}
