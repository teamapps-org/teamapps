import {parseHtml} from "./parseHtml";
import {ProjectorEvent} from "./ProjectorEvent";
import {EntranceAnimation, ExitAnimation, NotificationPosition, NotificationPositions} from "../generated";
import {animateCSS} from "./animations";

let notificationPositions: NotificationPosition[] = Object.values(NotificationPositions);
const containersByPosition = new Map<NotificationPosition, HTMLElement>();

let notificationContainersWrapper = document.body.querySelector(".notification-containers");
if (notificationContainersWrapper == null) {
	// create the elements
	let notificationContainersWrapper = document.createElement("div");
	notificationContainersWrapper.classList.add("notification-containers");
	notificationPositions.forEach(pos => {
		let notificationContainer = document.createElement("div");
		notificationContainer.classList.add("notification-container", pos);
		notificationContainersWrapper.append(notificationContainer);
	});
	document.body.append(notificationContainersWrapper);
}
// register the elements
notificationPositions.forEach(pos => {
	containersByPosition.set(pos, document.body.querySelector(`.notification-container.${pos}`));
})


export interface ComponentLike {
	getMainElement(): HTMLElement;
}
export interface NotificationHandle {
	onTimeout: ProjectorEvent<void>;
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
	public readonly onTimeout = new ProjectorEvent<void>();
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

		let container = containersByPosition.get(position);

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
				console.log("notification timeout over!");
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

		console.log("starting close animation");
		animateCSS(this.$wrapper, this.exitAnimation, 700, () => {
			console.log("close animation timeout over");
			this.$wrapper.remove();
		});
	}
}