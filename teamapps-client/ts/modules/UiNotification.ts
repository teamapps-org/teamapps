import {UiNotification_ClosedEvent, UiNotificationCommandHandler, UiNotificationConfig, UiNotificationEventSource} from "../generated/UiNotificationConfig";
import {UiEntranceAnimation} from "../generated/UiEntranceAnimation";
import {UiNotificationPosition} from "../generated/UiNotificationPosition";
import {UiExitAnimation} from "../generated/UiExitAnimation";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {animateCSS, Constants, parseHtml} from "./Common";
import {createUiColorCssString, createUiSpacingValueCssString} from "./util/CssFormatUtil";
import {ProgressBar} from "./micro-components/ProgressBar";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiComponent} from "./UiComponent";

const containersByPosition: {
	[UiNotificationPosition.TOP_LEFT]: HTMLElement,
	[UiNotificationPosition.TOP_CENTER]: HTMLElement,
	[UiNotificationPosition.TOP_RIGHT]: HTMLElement,
	[UiNotificationPosition.BOTTOM_LEFT]: HTMLElement,
	[UiNotificationPosition.BOTTOM_CENTER]: HTMLElement,
	[UiNotificationPosition.BOTTOM_RIGHT]: HTMLElement
} = {
	[UiNotificationPosition.TOP_LEFT]: parseHtml(`<div class="UiNotification-container top-left"></div>`),
	[UiNotificationPosition.TOP_CENTER]: parseHtml(`<div class="UiNotification-container top-center"></div>`),
	[UiNotificationPosition.TOP_RIGHT]: parseHtml(`<div class="UiNotification-container top-right"></div>`),
	[UiNotificationPosition.BOTTOM_LEFT]: parseHtml(`<div class="UiNotification-container bottom-left"></div>`),
	[UiNotificationPosition.BOTTOM_CENTER]: parseHtml(`<div class="UiNotification-container bottom-center"></div>`),
	[UiNotificationPosition.BOTTOM_RIGHT]: parseHtml(`<div class="UiNotification-container bottom-right"></div>`)
};

const notificationWrappersByPosition: {
	[UiNotificationPosition.TOP_LEFT]: HTMLElement[],
	[UiNotificationPosition.TOP_CENTER]: HTMLElement[],
	[UiNotificationPosition.TOP_RIGHT]: HTMLElement[],
	[UiNotificationPosition.BOTTOM_LEFT]: HTMLElement[],
	[UiNotificationPosition.BOTTOM_CENTER]: HTMLElement[],
	[UiNotificationPosition.BOTTOM_RIGHT]: HTMLElement[]
} = {
	[UiNotificationPosition.TOP_LEFT]: [],
	[UiNotificationPosition.TOP_CENTER]: [],
	[UiNotificationPosition.TOP_RIGHT]: [],
	[UiNotificationPosition.BOTTOM_LEFT]: [],
	[UiNotificationPosition.BOTTOM_CENTER]: [],
	[UiNotificationPosition.BOTTOM_RIGHT]: []
};

export function showNotification(notification: UiNotification, position: UiNotificationPosition, entranceAnimation: UiEntranceAnimation, exitAnimation: UiExitAnimation) {
	if (notificationWrappersByPosition[position].length == 0) {
		document.body.appendChild(containersByPosition[position]);
	}

	let $notificationWrapper = parseHtml(`<div class="notification-wrapper"></div>`);
	$notificationWrapper.appendChild(notification.getMainDomElement());
	animateCSS(notification.getMainDomElement(), Constants.ENTRANCE_ANIMATION_CSS_CLASSES[entranceAnimation] as any, 700);
	notificationWrappersByPosition[position].push($notificationWrapper);
	containersByPosition[position].appendChild($notificationWrapper);

	let closeListener = () => {
		notification.onClosedAnyWay.removeListener(closeListener);
		$notificationWrapper.style.height = `${$notificationWrapper.offsetHeight}px`;
		$notificationWrapper.offsetHeight; // make sure the style above is applied so we get a transition!
		$notificationWrapper.style.height = "0px";
		$notificationWrapper.style.marginBottom = "0px";
		$notificationWrapper.style.zIndex = "0";

		animateCSS(notification.getMainDomElement(), Constants.EXIT_ANIMATION_CSS_CLASSES[exitAnimation] as any, 700, () => {
			$notificationWrapper.remove();
			notificationWrappersByPosition[position] = notificationWrappersByPosition[position].filter(w => w != $notificationWrapper);

			if (notificationWrappersByPosition[position].length == 0) {
				containersByPosition[position].remove();
			}
		});
	};
	notification.onClosedAnyWay.addListener(closeListener);

	setTimeout(() => notification.startTimeout());
}

export class UiNotification extends AbstractUiComponent<UiNotificationConfig> implements UiNotificationCommandHandler, UiNotificationEventSource {

	public readonly onClosed: TeamAppsEvent<UiNotification_ClosedEvent> = new TeamAppsEvent(this);
	public readonly onClosedAnyWay: TeamAppsEvent<void> = new TeamAppsEvent(this);

	private $main: HTMLElement;
	private $contentContainer: HTMLElement;
	private $progressBarContainer: HTMLElement;
	private progressBar: ProgressBar;

	constructor(config: UiNotificationConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiNotification">
	<div class="close-button"></div>
	<div class="content-container"></div>
	<div class="progress-container"></div>
</div>`);
		this.$contentContainer = this.$main.querySelector(":scope > .content-container");
		this.$progressBarContainer = this.$main.querySelector(":scope > .progress-container");
		this.$main.querySelector(":scope > .close-button").addEventListener("mousedown", () => this.close());
		this.update(config);
	}

	public update(config: UiNotificationConfig) {
		this.$main.style.backgroundColor = createUiColorCssString(config.backgroundColor, "transparent");
		// this.$main.style.borderColor = createUiColorCssString(config.borderColor, "#00000022");
		this.$contentContainer.style.padding = createUiSpacingValueCssString(config.padding);
		this.$main.classList.toggle("dismissible", config.dismissible);
		this.$main.classList.toggle("show-progress", config.showProgressBar && config.displayTimeInMillis > 0);

		if (config.showProgressBar && this.progressBar == null) {
			this.progressBar = new ProgressBar(0, {height: 5, transitionTime: config.displayTimeInMillis});
			this.$progressBarContainer.appendChild(this.progressBar.getMainDomElement());
		} else if (!config.showProgressBar && this.progressBar != null) {
			this.progressBar.getMainDomElement().remove();
			this.progressBar = null;
		}

		if (this.$contentContainer.firstChild !== (config.content && (config.content as UiComponent).getMainDomElement())) {
			this.$contentContainer.innerHTML = '';
			if (config.content != null) {
				this.$contentContainer.appendChild((config.content as UiComponent).getMainDomElement());
			}
		}
	}

	public startTimeout() {
		if (this.progressBar != null) {
			this.progressBar.setProgress(1);
		}
		if (this._config.displayTimeInMillis > 0) {
			setTimeout(() => {
				this.close();
				this.onClosed.fire({byUser: false});
				this.onClosedAnyWay.fire();
			}, this._config.displayTimeInMillis);
		}
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

	close(): void {
		this.onClosedAnyWay.fire();
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiNotification", UiNotification);