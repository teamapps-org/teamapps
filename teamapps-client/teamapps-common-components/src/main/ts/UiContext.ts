import * as moment from "moment-timezone";
import {UiContext_GlobalKeyEventOccurredEvent, UiContext_NavigationStateChangeEvent} from "./generated/UiContextConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TeamAppsUiContext, TeamAppsUiContextInternalApi} from "./TeamAppsUiContext";
import {UiComponentConfig} from "./generated/UiComponentConfig";
import {UiConfigurationConfig} from "./generated/UiConfigurationConfig";
import {UiGenericErrorMessageOption} from "./generated/UiGenericErrorMessageOption";
import {createUiLocation, exitFullScreen, parseHtml} from "./Common";
import {KeyEventType} from "./generated/KeyEventType";
import { ElementUiComponentAdapter } from "./components/ElementUiComponentAdapter";
import {showNotification, UiNotification} from "./components/UiNotification";
import {UiNotificationPosition} from "./generated/UiNotificationPosition";
import {UiEntranceAnimation} from "./generated/UiEntranceAnimation";
import {UiExitAnimation} from "./generated/UiExitAnimation";
import {UiTemplateConfig} from "./generated/UiTemplateConfig";
import {Component} from "preact";
import {UiComponent} from "./UiComponent";
import {releaseWakeLock, requestWakeLock } from "./util/WakeLock";
import {Showable} from "./components/Showable";

export class UiContext {

	public static readonly onGlobalKeyEventOccurred: TeamAppsEvent<UiContext_GlobalKeyEventOccurredEvent> = new TeamAppsEvent();
	public static readonly onNavigationStateChange: TeamAppsEvent<UiContext_NavigationStateChangeEvent> = new TeamAppsEvent();

	public static setGlobalKeyEventsEnabled(unmodified: boolean, modifiedWithAltKey: boolean, modifiedWithCtrlKey: boolean, modifiedWithMetaKey: boolean, includeRepeats: boolean, keyDown: boolean, keyUp: boolean) {
		setGlobalKeyEventsEnabled.call(this, unmodified, modifiedWithAltKey, modifiedWithCtrlKey, modifiedWithMetaKey, includeRepeats, keyDown, keyUp);
	}

	public static async registerComponentLibrary(uuid: string, mainJsUrl: string, context: TeamAppsUiContextInternalApi) {
		context.registerComponentLibrary(uuid, mainJsUrl);
	}

	public static async registerClientObjectType(libraryUuid: string, clientObjectType: string, eventNames: string[], queryNames: string[], context: TeamAppsUiContextInternalApi) {
		console.log("TODO registerClientObjectType:", libraryUuid, clientObjectType);
	}

	public static async toggleEventListening(libraryUuid: string, clientObjectId: string, eventName: string, enabled: boolean, context: TeamAppsUiContextInternalApi) {
		context.toggleEventListener(libraryUuid, clientObjectId, eventName, enabled)
	}

	public static async render(libraryUuid: string, config: UiComponentConfig, context: TeamAppsUiContextInternalApi) {
		await context.renderClientObject(libraryUuid, config);
	}

	public static unrender(componentId: string, context: TeamAppsUiContextInternalApi) {
		context.destroyClientObject(componentId);
	}

	public static refreshComponent(libraryUuid: string, config: UiComponentConfig, context: TeamAppsUiContextInternalApi) {
		context.refreshComponent(libraryUuid, config);
	}

	public static setConfig(config: UiConfigurationConfig, context: TeamAppsUiContext) {
		let oldConfig = context.config;
		if ((!oldConfig || oldConfig.locale !== config.locale) && config.locale !== 'en') {
			$.getScript("runtime-resources/moment-locales/" + config.locale + ".js");
			$.getScript("runtime-resources/fullcalendar-locales/" + config.locale + ".js");
		}
		moment.locale(config.locale);
		this.setThemeClassName(config.themeClassName);

		this.ALL_ROOT_PANELS.forEach(uiRootPanel => {
			uiRootPanel.setOptimizedForTouch(config.optimizedForTouch);
		});

		// this.LOGGER.warn("TODO Setting configuration on context. This should be implemented using an event instead!");
		(context as any).config = config; // TODO change this to firing an event to the context!!!!
	}

	public static setSessionMessageWindows(expiredMessageWindow: Showable, errorMessageWindow: Showable, terminatedMessageWindow: Showable, context: TeamAppsUiContext) {
		(context as any).setSessionMessageWindows(expiredMessageWindow, errorMessageWindow, terminatedMessageWindow);
	}

	public static setPageTitle(pageTitle: string) {
		document.title = pageTitle;
	}

	private static get ALL_ROOT_PANELS() {
		return Object.keys(this.ALL_ROOT_PANELS_BY_ID).map(id => this.ALL_ROOT_PANELS_BY_ID[id]);
	}

	public static registerBackgroundImage(id: string, image: string, blurredImage: string) {
		if (!id) {
			console.error(`Cannot register background image. Missing id!`);
			return;
		}
		if (!image) {
			console.warn(`Missing background image for image registration! (Still registering...)`);
		}
		if (!blurredImage) {
			blurredImage = image;
		}

		(new Image()).src = image; // preload
		(new Image()).src = blurredImage; // preload

		this.BACKGROUND_IMAGES_BY_ID[id] = {image, blurredImage};
	}

	public static setBackgroundImage(id: string, animationDuration: number) {
		let backgroundImage: string = null;
		let blurredBackgroundImage: string = null;
		if (id != null) {
			let registeredImage = this.BACKGROUND_IMAGES_BY_ID[id];
			if (!registeredImage) {
				console.warn(`Background image with id ${id} does not exist!`);
				return;
			}
			backgroundImage = registeredImage.image;
			blurredBackgroundImage = registeredImage.blurredImage;
		}
		this.ALL_ROOT_PANELS.forEach(uiRootPanel => {
			uiRootPanel.backgroundImage = backgroundImage;
			uiRootPanel.blurredBackgroundImage = blurredBackgroundImage;
			uiRootPanel.updateBackground(animationDuration);
		});
	}

	public static setBackgroundColor(backgroundColor: string, animationDuration: number) {
		this.ALL_ROOT_PANELS.forEach(uiRootPanel => {
			uiRootPanel.backgroundColor = backgroundColor;
			uiRootPanel.updateBackground(animationDuration);
		})
	}

	public static buildRootPanel(containerElementId: string, uiRootPanel: UiComponent, context?: TeamAppsUiContext): void {
		const $container = containerElementId ? document.querySelector(containerElementId) : document.body;
		$container.appendChild(uiRootPanel.getMainElement());
	}

	public static setThemeClassName(theme: string) {
		// remove other theme classes
		document.body.className = document.body.className.replace(/theme-\w+/, '');
		// add theme class
		if (theme) {
			document.body.classList.add(theme);
		}
	}

	public static showNotification(notification: UiNotification, position: UiNotificationPosition, entranceAnimation: UiEntranceAnimation, exitAnimation: UiExitAnimation, context: TeamAppsUiContext) {
		showNotification(notification, position, entranceAnimation, exitAnimation);
	}

	public static downloadFile(fileUrl: string, fileName: string) {
		const link = document.createElement('a');
		link.href = fileUrl;
		if (fileName != null) {
			link.href += (fileUrl.indexOf('?') === -1 ? '?' : '&') + 'teamapps-download-filename=' + fileName;
			link.setAttribute("download", fileName);
		}
		if (document.createEvent) {
			const e = document.createEvent('MouseEvents');
			e.initEvent('click', true, true);
			link.dispatchEvent(e);
			return true;
		}
	}

	public static registerTemplate(name: string, template: UiTemplateConfig, context: TeamAppsUiContext) {
		context.templateRegistry.registerTemplate(name, template);
	}

	public static registerTemplates(templates: { [name: string]: UiTemplateConfig }, context: TeamAppsUiContext) {
		Object.keys(templates).forEach(templateName => {
			this.registerTemplate(templateName, templates[templateName], context);
		});
	}

	public static addClientToken(token: string) {
		let tokens = UiContext.loadClientTokens();
		tokens[token] = true;
		UiContext.saveClientTokens(tokens);
	}

	public static removeClientToken(token: string) {
		let tokens = UiContext.loadClientTokens();
		delete tokens[token];
		UiContext.saveClientTokens(tokens);
	}

	public static clearClientTokens() {
		UiContext.saveClientTokens({});
	}

	private static loadClientTokens(): { [tokenName: string]: true } {
		let tokens: { [tokenName: string]: true } = {};
		let tokenJson = localStorage.getItem("clientTokens");
		if (tokenJson) {
			tokens = JSON.parse(tokenJson);
		}
		return tokens;
	}

	public static getClientTokens(): string[] {
		return Object.keys(UiContext.loadClientTokens());
	}

	private static saveClientTokens(tokens: { [token: string]: true }) {
		localStorage.setItem("clientTokens", JSON.stringify(tokens));
	}

	public static exitFullScreen() {
		exitFullScreen();
	}

	public static createGenericErrorMessageWindow(title: string, message: string, showErrorIcon: boolean, options: UiGenericErrorMessageOption[], context: TeamAppsUiContext): UiWindow {
		let uiWindow = new UiWindow({
			id: null,
			title: title,
			width: 370,
			height: 200,
			modalBackgroundDimmingColor: "rgba(0, 0, 0, .5)",
			modal: true,
			content: null
		}, context);
		let $contentElement = parseHtml(`<div class="UiGenericErrorMessage">
	<div class="icon img img-48 ta-icon-window-close-grey" style="display: ${showErrorIcon ? 'block' : 'none'}"></div>
	<div class="message" style="text-align: justify;">${message}</div>
	<div class="option-buttons">
		${options.map(o => `<div class="btn btn-default ${UiGenericErrorMessageOption[o].toLowerCase()}">${UiGenericErrorMessageOption[o]}</div>`).join("")}
	</div>
</div>`);
		uiWindow.setContent(new ElementUiComponentAdapter($contentElement));
		$contentElement.querySelector<HTMLElement>(':scope .ok').addEventListener('click', () => {
			uiWindow.close(500);
		});
		$contentElement.querySelector<HTMLElement>(':scope .reload').addEventListener('click', () => {
			window.location.reload();
		});
		return uiWindow;
	}

	public static async requestWakeLock(uuid: string): Promise<boolean> {
		return await requestWakeLock(uuid);
	}

	public static async releaseWakeLock(uuid: string) {
		return releaseWakeLock(uuid);
	}

	public static async goToUrl(url: string, blankPage: boolean) {
		console.info(`goToUrl(${url}, ${blankPage})`);
		if (blankPage) {
			window.open(url, '_blank');
		} else {
			location.href = url;
		}
	}

	public static async pushHistoryState(relativeUrl: string) {
		window.history.pushState({}, "", relativeUrl);
		UiContext.onNavigationStateChange.fire({location: createUiLocation(), triggeredByUser: false});
	}

	public static async navigateForward(steps: number) {
		window.history.go(steps);
	}

	public static setFavicon(url: string) {
		let link: HTMLLinkElement = document.querySelector("link[rel~='icon']");
		if (!link) {
			link = document.createElement('link');
			link.rel = 'icon';
			document.getElementsByTagName('head')[0].appendChild(link);
		}
		link.href = url;
	}

	public static setTitle(title: string) {
		document.title = title;
	}

}

// GLOBAL:

let keyboardEventSettings: { unmodified: boolean, modifiedWithAltKey: boolean, modifiedWithCtrlKey: boolean, modifiedWithMetaKey: boolean, includeRepeats: boolean } = {
	unmodified: false,
	modifiedWithAltKey: false,
	modifiedWithCtrlKey: false,
	modifiedWithMetaKey: false,
	includeRepeats: false,
};

let keyboardEventListener = (e: KeyboardEvent) => {
	let settings = keyboardEventSettings;
	if (e.repeat && !settings.includeRepeats) {
		return;
	}
	let modified = e.ctrlKey || e.altKey || e.metaKey;
	if (!modified && settings.unmodified
		|| e.ctrlKey && settings.modifiedWithCtrlKey
		|| e.altKey && settings.modifiedWithAltKey
		|| e.metaKey && settings.modifiedWithMetaKey) {

		let el = e.target as Element;
		let componentId: string | null;
		while (el != null) {
			el = el.parentElement;
			componentId = el?.getAttribute("data-teamapps-id");
			if (componentId != null) {
				break;
			}
		}

		UiContext.onGlobalKeyEventOccurred.fire({
			componentId: null,
			eventType: e.type == "keydown" ? KeyEventType.KEY_DOWN : KeyEventType.KEY_UP,
			sourceComponentId: componentId,
			code: e.code,
			isComposing: e.isComposing,
			key: e.key,
			charCode: e.charCode,
			keyCode: e.keyCode,
			locale: (e as any).locale,
			location: e.location,
			repeat: e.repeat,
			altKey: e.altKey,
			ctrlKey: e.ctrlKey,
			shiftKey: e.shiftKey,
			metaKey: e.metaKey
		})
	}
}

function setGlobalKeyEventsEnabled(unmodified: boolean, modifiedWithAltKey: boolean, modifiedWithCtrlKey: boolean, modifiedWithMetaKey: boolean, includeRepeats: boolean, keyDown: boolean, keyUp: boolean) {
	document.removeEventListener("keydown", keyboardEventListener, {capture: true});
	document.removeEventListener("keyup", keyboardEventListener, {capture: true});
	keyboardEventSettings = {unmodified, modifiedWithAltKey, modifiedWithCtrlKey, modifiedWithMetaKey, includeRepeats};
	if (keyDown) {
		document.addEventListener("keydown", keyboardEventListener, {capture: true, passive: true})
	}
	if (keyUp) {
		document.addEventListener("keyup", keyboardEventListener, {capture: true, passive: true})
	}
}

window.addEventListener('popstate', (event) => {
	UiContext.onNavigationStateChange.fire({
		componentId: null,
		location: createUiLocation(),
		triggeredByUser: true
	});
});
