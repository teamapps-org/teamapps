import {TeamAppsUiContext, TeamAppsUiContextInternalApi} from "./TeamAppsUiContext";
import {
	DtoComponent as DtoComponentConfig,
	DtoConfiguration,
	DtoGenericErrorMessageOption,
	DtoGlobals,
	DtoGlobals_GlobalKeyEventOccurredEvent,
	DtoGlobals_NavigationStateChangeEvent,
	DtoKeyEventType,
} from "./generated";
import {releaseWakeLock, requestWakeLock} from "./util/wakeLock";
import {exitFullScreen} from "./util/fullscreen";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {Component} from "./component/Component";
import {Showable} from "./util/Showable";
import {parseHtml} from "./util/parseHtml";
import {createUiLocation} from "./util/locationUtil";
import {ClientObject} from "./ClientObject";
import {loadJavaScript} from "./util/resourceLoading";

export class Globals implements ClientObject<DtoGlobals> {

	public static readonly onGlobalKeyEventOccurred: TeamAppsEvent<DtoGlobals_GlobalKeyEventOccurredEvent> = new TeamAppsEvent();
	public static readonly onNavigationStateChange: TeamAppsEvent<DtoGlobals_NavigationStateChangeEvent> = new TeamAppsEvent();

	// dummy constructor
	constructor(config: DtoGlobals){
		throw new Error("Globals should never be instantiated!");
	}

	destroy(): void {
        // will not get instantiated anyway...
    }

	public static setGlobalKeyEventsEnabled(unmodified: boolean, modifiedWithAltKey: boolean, modifiedWithCtrlKey: boolean, modifiedWithMetaKey: boolean, includeRepeats: boolean, keyDown: boolean, keyUp: boolean) {
		setGlobalKeyEventsEnabled.call(this, unmodified, modifiedWithAltKey, modifiedWithCtrlKey, modifiedWithMetaKey, includeRepeats, keyDown, keyUp);
	}

	public static async registerComponentLibrary(uuid: string, mainJsUrl: string, mainCssUrl: string, context: TeamAppsUiContextInternalApi) {
		context.registerComponentLibrary(uuid, mainJsUrl, mainCssUrl);
	}

	public static async registerClientObjectType(libraryUuid: string, clientObjectType: string, eventNames: string[], queryNames: string[], context: TeamAppsUiContextInternalApi) {
		console.log("TODO registerClientObjectType - need to do something here??:", libraryUuid, clientObjectType, eventNames, queryNames);
	}

	public static async toggleEventListening(libraryUuid: string, clientObjectId: string, eventName: string, enabled: boolean, context: TeamAppsUiContextInternalApi) {
		context.toggleEventListener(libraryUuid, clientObjectId, eventName, enabled)
	}

	public static async render(libraryUuid: string, config: DtoComponentConfig, context: TeamAppsUiContextInternalApi) {
		await context.renderClientObject(libraryUuid, config);
	}

	public static unrender(componentId: string, context: TeamAppsUiContextInternalApi) {
		context.destroyClientObject(componentId);
	}

	public static refreshComponent(libraryUuid: string, config: DtoComponentConfig, context: TeamAppsUiContextInternalApi) {
		context.refreshComponent(libraryUuid, config);
	}

	public static setConfig(config: DtoConfiguration, context: TeamAppsUiContext) {
		this.setThemeClassName(config.themeClassName);

		document.body.classList.toggle("optimized-for-touch", config.optimizedForTouch);

		// console.warn("TODO Setting configuration on context. This should be implemented using an event instead!");
		(context as any).config = config; // TODO change this to firing an event to the context!!!!
	}

	public static setSessionMessageWindows(expiredMessageWindow: Showable, errorMessageWindow: Showable, terminatedMessageWindow: Showable, context: TeamAppsUiContext) {
		(context as any).setSessionMessageWindows(expiredMessageWindow, errorMessageWindow, terminatedMessageWindow);
	}

	public static setPageTitle(pageTitle: string) {
		document.title = pageTitle;
	}

	public static setBackground(backgroundImageUrl: string, backgroundColor: string, animationDuration: number) {
		console.log("TODO setBackground(...)");
		// TODO
	}

	public static addRootComponent(containerElementId: string, component: Component, context?: TeamAppsUiContext): void {
		const $container = containerElementId ? document.querySelector(containerElementId) : document.body;
		$container.appendChild(component.getMainElement());
	}

	public static setThemeClassName(theme: string) {
		// remove other theme classes
		document.body.className = document.body.className.replace(/theme-\w+/, '');
		// add theme class
		if (theme) {
			document.body.classList.add(theme);
		}
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

	public static addClientToken(token: string) {
		let tokens = Globals.loadClientTokens();
		tokens[token] = true;
		Globals.saveClientTokens(tokens);
	}

	public static removeClientToken(token: string) {
		let tokens = Globals.loadClientTokens();
		delete tokens[token];
		Globals.saveClientTokens(tokens);
	}

	public static clearClientTokens() {
		Globals.saveClientTokens({});
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
		return Object.keys(Globals.loadClientTokens());
	}

	private static saveClientTokens(tokens: { [token: string]: true }) {
		localStorage.setItem("clientTokens", JSON.stringify(tokens));
	}

	public static exitFullScreen() {
		exitFullScreen();
	}

	public static createGenericErrorMessageShowable(title: string, message: string, showErrorIcon: boolean, options: DtoGenericErrorMessageOption[]): Showable {
		let $div = parseHtml(`
<div data-teamapps-id="ad352268-6d6b-473a-8c8d-316df8375361"
     style="position: absolute; inset: 0 0 0 0; z-index: 1000000; display: flex; justify-content: center; align-items: center; background-color: rgba(0, 0, 0, 0.5);">
    <div style="width: 370px; height: 200px; max-width: 100%; max-height: 100%; box-shadow: 3px 10px 70px rgb(0 0 0 / 85%);">
        <div data-teamapps-id="ad352268-6d6b-473a-8c8d-316df8375361" style="width: 100%; height: 100%; background-color: white; margin: 0;">
            <div style="min-height: 32px; border-bottom: 1px solid rgba(0, 0, 0, 0.09); padding: 0 1px 0 8px; display: flex; align-items: center;">Session Expired</div>
            <div style="padding: 15px">
                    <div style="text-align: justify; margin-bottom: 15px">${message}</div>
                    <div style="display: flex; justify-content: space-around;">
                        ${options.map(o => `<div class="${DtoGenericErrorMessageOption[o].toLowerCase()}" style="display: inline-block; text-align: center; cursor: pointer; padding: 5px; border-radius: 3px; user-select: none; border: 1px solid rgba(0, 0, 0, 0.09);">${DtoGenericErrorMessageOption[o]}</div>`).join("")}
                    </div>
            </div>
        </div>
    </div>
</div>
`);
		$div.querySelector<HTMLElement>(':scope .ok').addEventListener('click', () => $div.remove());
		$div.querySelector<HTMLElement>(':scope .reload').addEventListener('click', () => window.location.reload());
		return {
			show: () => document.body.append($div)
		};
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
		Globals.onNavigationStateChange.fire({location: createUiLocation(), triggeredByUser: false});
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

		Globals.onGlobalKeyEventOccurred.fire({
			eventType: e.type == "keydown" ? DtoKeyEventType.KEY_DOWN : DtoKeyEventType.KEY_UP,
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
	Globals.onNavigationStateChange.fire({
		location: createUiLocation(),
		triggeredByUser: true
	});
});