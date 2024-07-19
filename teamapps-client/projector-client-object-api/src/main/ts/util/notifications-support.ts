import {parseHtml} from "./parseHtml";
import {TeamAppsEvent} from "./TeamAppsEvent";
import {EntranceAnimation, ExitAnimation, NotificationPosition} from "../generated";
import {animateCSS} from "./animations";

const containersByPosition: {
	[pos: string]: HTMLElement,
} = Object.keys(NotificationPosition).reduce((m, pos) => {
	m[pos] = parseHtml(`<div class="Notification-container ${pos}"></div>`);
	return m;
}, {});

let notificationContainerWrapper = document.body.querySelector(".Notification-container-wrapper");
if (notificationContainerWrapper == null) {
	let notificationContainerWrapper = parseHtml(`<div class="Notification-container bottom-left"></div>`);
	for (let p in containersByPosition) {
		notificationContainerWrapper.append(containersByPosition[p]);
	}
	document.body.appendChild(notificationContainerWrapper);
}

export interface ComponentLike {
	getMainElement(): HTMLElement;
}
export interface NotificationHandle {
	onTimeout: TeamAppsEvent<void>;
	get isOpen(): boolean;
	open(position: NotificationPosition, timeout: number): void;
	close(): void;
}

export function prepareNotificationLike(content: ComponentLike, position: NotificationPosition, entranceAnimation: EntranceAnimation, exitAnimation: ExitAnimation, timeout: number): NotificationHandle {
	return new NotificationWrapper(content, entranceAnimation, exitAnimation);
}

export function showNotificationLike(content: ComponentLike, position: NotificationPosition, entranceAnimation: EntranceAnimation, exitAnimation: ExitAnimation, timeout: number): NotificationHandle {
	let notificationWrapper = new NotificationWrapper(content, entranceAnimation, exitAnimation);
	notificationWrapper.open(position, timeout);
	return notificationWrapper;
}

class NotificationWrapper implements NotificationHandle {
	public readonly onTimeout = new TeamAppsEvent<void>();
	public readonly $wrapper: HTMLElement;
	private timeoutId: number;
	private position: NotificationPosition;
	#open: boolean;

	constructor(public content: ComponentLike, private entranceAnimation: EntranceAnimation, private exitAnimation: ExitAnimation) {
		this.$wrapper = parseHtml(`<div class="notification-wrapper"></div>`);
		this.$wrapper.appendChild(content.getMainElement());
	}

	public open(position: NotificationPosition, timeout: number) {
		clearTimeout(this.timeoutId);
		this.#open = true;

		let container = containersByPosition[position];

		if (!container.contains(this.$wrapper)) {
			this.position = position;
			this.$wrapper.style.height = null;
			this.$wrapper.style.marginBottom = null;
			this.$wrapper.style.zIndex = null;
			container.appendChild(this.$wrapper);

			animateCSS(this.content.getMainElement(), this.entranceAnimation, 700);
		}

		if (timeout > 0) {
			this.timeoutId = setTimeout(() => {
				this.close();
				this.onTimeout.fire();
			}, timeout);
		}
	}

	get isOpen(): boolean {
		return this.#open;
	}

	public close() {
		clearTimeout(this.timeoutId);
		this.#open = false;

		this.$wrapper.style.height = `${this.$wrapper.offsetHeight}px`;
		this.$wrapper.offsetHeight; // make sure the style above is applied so we get a transition!
		this.$wrapper.style.height = "0px";
		this.$wrapper.style.marginBottom = "0px";
		this.$wrapper.style.zIndex = "0";

		animateCSS(this.content.getMainElement(), this.exitAnimation, 700, () => {
			this.$wrapper.remove();
		});
	}
}