/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
import * as moment from "moment-timezone";

import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiWindow} from "./UiWindow";
import {UiConfigurationConfig} from "../generated/UiConfigurationConfig";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext, TeamAppsUiContextInternalApi} from "./TeamAppsUiContext";
import {createUiLocation, exitFullScreen, getLastPointerCoordinates, pageTransition, parseHtml} from "./Common";
import {
	UiRootPanel_GlobalKeyEventOccurredEvent, UiRootPanel_NavigationStateChangeEvent,
	UiRootPanelCommandHandler,
	UiRootPanelConfig,
	UiRootPanelEventSource
} from "../generated/UiRootPanelConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";
import * as log from "loglevel";
import {ElementUiComponentAdapter} from "./micro-components/ElementUiComponentAdapter";
import {UiGenericErrorMessageOption} from "../generated/UiGenericErrorMessageOption";
import {UiComponent} from "./UiComponent";
import {UiPageTransition} from "../generated/UiPageTransition";
import {UiPopup} from "./UiPopup";
import {showNotification, UiNotification} from "./UiNotification";
import {UiNotificationPosition} from "../generated/UiNotificationPosition";
import {UiEntranceAnimation} from "../generated/UiEntranceAnimation";
import {UiExitAnimation} from "../generated/UiExitAnimation";
import {releaseWakeLock, requestWakeLock} from "./util/WakeLock";
import {EventSubscription, TeamAppsEvent, TeamAppsEventListener} from "./util/TeamAppsEvent";
import {KeyEventType} from "../generated/KeyEventType";

export class UiRootPanel extends AbstractUiComponent<UiRootPanelConfig> implements UiRootPanelCommandHandler, UiRootPanelEventSource {

	public static readonly onGlobalKeyEventOccurred: TeamAppsEvent<UiRootPanel_GlobalKeyEventOccurredEvent> = new TeamAppsEvent();
	public static readonly onNavigationStateChange: TeamAppsEvent<UiRootPanel_NavigationStateChangeEvent> = new TeamAppsEvent();

	private static LOGGER: log.Logger = log.getLogger("UiRootPanel");
	private static ALL_ROOT_PANELS_BY_ID: { [id: string]: UiRootPanel } = {};
	private static BACKGROUND_IMAGES_BY_ID: {
		[index: string]: {
			image: string;
			blurredImage: string;
		}
	} = {};
	private static WINDOWS_BY_ID: { [windowId: string]: UiWindow } = {};

	private $root: HTMLElement;
	private content: UiComponent;
	private $contentWrapper: HTMLElement;
	private $backgroundTransitionStyle: HTMLElement;
	private $backgroundStyle: HTMLElement;
	private $imagePreloadDiv: HTMLElement;

	private backgroundImage: string;
	private blurredBackgroundImage: string;
	private backgroundColor: string;

	constructor(config: UiRootPanelConfig, context: TeamAppsUiContext) {
		super(config, context);
		UiRootPanel.ALL_ROOT_PANELS_BY_ID[config.id] = this;

		this.$root = parseHtml(`<div data-background-container-id="${config.id}" class="UiRootPanel teamapps-backgroundImage">
              <div class="image-preload-div"></div>
              <style data-style-type="backgroundTransitionStyle"></style>
              <style data-style-type="backgroundStyle"></style>
              <style></style>
		</div>`);
		this.$imagePreloadDiv = this.$root.querySelector<HTMLElement>(":scope .image-preload-div");
		this.$backgroundTransitionStyle = this.$root.querySelector<HTMLElement>(":scope [data-style-type='backgroundTransitionStyle']");
		this.$backgroundStyle = this.$root.querySelector<HTMLElement>(":scope [data-style-type='backgroundStyle']");

		this.setContent(config.content as UiComponent);

		this.setOptimizedForTouch(context.config.optimizedForTouch);
	}

	public static setGlobalKeyEventsEnabled(unmodified: boolean, modifiedWithAltKey: boolean, modifiedWithCtrlKey: boolean, modifiedWithMetaKey: boolean, includeRepeats: boolean, keyDown: boolean, keyUp: boolean) {
		setGlobalKeyEventsEnabled.call(this, unmodified, modifiedWithAltKey, modifiedWithCtrlKey, modifiedWithMetaKey, includeRepeats, keyDown, keyUp);
	}

	public doGetMainElement(): HTMLElement {
		return this.$root;
	}

	public setContent(content: UiComponent, transition: UiPageTransition | null = null, animationDuration: number = 0): void {
		if (content == this.content) {
			return;
		}

		let oldContent = this.content;
		let $oldContentWrapper = this.$contentWrapper;

		this.content = content;

		this.$contentWrapper = parseHtml(`<div class="child-component-wrapper">`);
		if (content != null) {
			this.$contentWrapper.appendChild(content.getMainElement());
		}
		this.$root.appendChild(this.$contentWrapper);

		if (transition != null && animationDuration > 0) {
			pageTransition($oldContentWrapper, this.$contentWrapper, transition, animationDuration, () => {
				$oldContentWrapper && $oldContentWrapper.remove();
			});
		} else {
			$oldContentWrapper && $oldContentWrapper.remove();
		}
	}

	public static createComponent(config: UiComponentConfig, context: TeamAppsUiContextInternalApi) {
		let o = context.createClientObject(config);
		context.registerClientObject(o, config.id, config._type);
	}

	public static destroyComponent(componentId: string, context: TeamAppsUiContextInternalApi) {
		context.destroyClientObject(componentId);
	}

	public static refreshComponent(config: UiComponentConfig, context: TeamAppsUiContextInternalApi) {
		context.refreshComponent(config);
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

	public static setSessionMessageWindows(expiredMessageWindow: UiWindow, errorMessageWindow: UiWindow, terminatedMessageWindow: UiWindow, context: TeamAppsUiContext) {
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
			this.LOGGER.error(`Cannot register background image. Missing id!`);
			return;
		}
		if (!image) {
			this.LOGGER.warn(`Missing background image for image registration! (Still registering...)`);
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
				this.LOGGER.warn(`Background image with id ${id} does not exist!`);
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

	private updateBackground(animationDuration: number) {
		this.$backgroundTransitionStyle.textContent = `
                /*[data-background-container-id='${this.getId()}']*/ .teamapps-backgroundImage,
                /*[data-background-container-id='${this.getId()}']*/ .teamapps-blurredBackgroundImage {
                    transition: background-image ${animationDuration}ms ease-in-out, background-color ${animationDuration}ms ease-in-out;
                }
            `;
		this.$root.clientWidth; // ensure the css is applied!
		this.$backgroundStyle.textContent = `
				/*[data-background-container-id='${this.getId()}']*/.teamapps-backgroundImage,
                /*[data-background-container-id='${this.getId()}']*/ .teamapps-backgroundImage {
                    background-color: ${this.backgroundColor || ''};
                    background-image: ${this.backgroundImage ? `url('${this.backgroundImage}')` : 'none'};
                }
                /*[data-background-container-id='${this.getId()}']*/.teamapps-blurredBackgroundImage,
                /*[data-background-container-id='${this.getId()}']*/ .teamapps-blurredBackgroundImage {
                    background-image: ${this.blurredBackgroundImage ? `url('${this.blurredBackgroundImage}')` : 'none'};
                }
            `;
	}

	public destroy(): void {
		super.destroy();
		delete UiRootPanel.ALL_ROOT_PANELS_BY_ID[this.getId()];
		if (Object.keys(UiRootPanel.ALL_ROOT_PANELS_BY_ID).length === 0) {
			Object.keys(UiRootPanel.WINDOWS_BY_ID).forEach(windowId => UiRootPanel.WINDOWS_BY_ID[windowId].close(0));
		}
	}

	public static buildRootPanel(containerElementId: string, uiRootPanel: UiRootPanel, context?: TeamAppsUiContext): void {
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
		let tokens = UiRootPanel.loadClientTokens();
		tokens[token] = true;
		UiRootPanel.saveClientTokens(tokens);
	}

	public static removeClientToken(token: string) {
		let tokens = UiRootPanel.loadClientTokens();
		delete tokens[token];
		UiRootPanel.saveClientTokens(tokens);
	}

	public static clearClientTokens() {
		UiRootPanel.saveClientTokens({});
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
		return Object.keys(UiRootPanel.loadClientTokens());
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
	<div class="icon img img-48" style="background-image: url('/resources/window-close-grey.png'); display: ${showErrorIcon ? 'block' : 'none'}"></div>
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

	setOptimizedForTouch(optimizedForTouch: boolean) {
		this.$root.classList.toggle("optimized-for-touch", optimizedForTouch);
		document.body.classList.toggle("optimized-for-touch", optimizedForTouch); // needed for popups and maximized panels... TODO either only use this or change implementation
	}

	public static showPopupAtCurrentMousePosition(popup: UiPopup) {
		popup.setPosition(...getLastPointerCoordinates());
		document.body.appendChild(popup.getMainElement());
	}

	public static showPopup(popup: UiPopup) {
		document.body.appendChild(popup.getMainElement());
	}

	public static async requestWakeLock(uuid: string): Promise<boolean> {
		return await requestWakeLock(uuid);
	}

	public static async releaseWakeLock(uuid: string) {
		return releaseWakeLock(uuid);
	}

	public static async goToUrl(url: string, blankPage: boolean) {
		this.LOGGER.info(`goToUrl(${url}, ${blankPage})`);
		if (blankPage) {
			window.open(url, '_blank');
		} else {
			location.href = url;
		}
	}

	public static async changeNavigationHistoryState(relativeUrl: string, fireEvent: boolean, push: boolean) {
		if (window.location.pathname + window.location.search === relativeUrl) {
			return; // nothing to do here...
		}
		if (push) {
			window.history.pushState({}, "", relativeUrl);
		} else {
			window.history.replaceState({}, "", relativeUrl);
		}
		if (fireEvent) {
			UiRootPanel.onNavigationStateChange.fire({location: createUiLocation(), triggeredBrowserNavigation: false});
		}
	}

	public static async navigateForward(steps: number) {
		window.history.go(steps);
	}

	public static setFavicon(url: string) {
		let link:HTMLLinkElement = document.querySelector("link[rel~='icon']");
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
TeamAppsUiComponentRegistry.registerComponentClass("UiRootPanel", UiRootPanel);

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

		UiRootPanel.onGlobalKeyEventOccurred.fire({
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
	UiRootPanel.onNavigationStateChange.fire({
		location: createUiLocation(),
		triggeredBrowserNavigation: true
	});
});
