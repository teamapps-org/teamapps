import {
	Component,
	exitFullScreen,
	KeyEventType,
	releaseWakeLock,
	requestWakeLock,
	ServerObjectChannel,
	Showable, StaticDtoGlobalsServerObjectChannel,
} from "projector-client-object-api";
import {DefaultUiContext} from "./DefaultUiContext";

let serverObjectChannel: StaticDtoGlobalsServerObjectChannel;

let keyboardEventSettings = {
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

		serverObjectChannel?.sendEvent("globalKeyEventOccurred", {
			eventType: e.type == "keydown" ? KeyEventType.KEY_DOWN : KeyEventType.KEY_UP,
			sourceComponentId: componentId,
			code: e.code,
			composing: e.isComposing,
			key: e.key,
			locale: (e as any).locale,
			location: e.location,
			repeat: e.repeat,
			altKey: e.altKey,
			ctrlKey: e.ctrlKey,
			shiftKey: e.shiftKey,
			metaKey: e.metaKey
		});
	}
}

window.addEventListener('popstate', (event) => {
	serverObjectChannel?.sendEvent("navigationStateChange", {
		location: location.href,
		triggeredByUser: true
	});
});

export var CoreLibrary = {

	init(serverChan: ServerObjectChannel): void {
		serverObjectChannel = serverChan;
		this.configureGlobalKeyboardEvents(true, true, true, true, true, true, true);
	},

	configureGlobalKeyboardEvents(unmodified: boolean, modifiedWithAltKey: boolean, modifiedWithCtrlKey: boolean, modifiedWithMetaKey: boolean, includeRepeats: boolean, keyDown: boolean, keyUp: boolean) {
		document.removeEventListener("keydown", keyboardEventListener, {capture: true});
		document.removeEventListener("keyup", keyboardEventListener, {capture: true});
		keyboardEventSettings = {unmodified, modifiedWithAltKey, modifiedWithCtrlKey, modifiedWithMetaKey, includeRepeats};
		if (keyDown) {
			document.addEventListener("keydown", keyboardEventListener, {capture: true, passive: true})
		}
		if (keyUp) {
			document.addEventListener("keyup", keyboardEventListener, {capture: true, passive: true})
		}
	},

	setSessionMessageWindows(expiredMessageWindow: Showable, errorMessageWindow: Showable, terminatedMessageWindow: Showable, context: DefaultUiContext) {
		context.setSessionMessageWindows(expiredMessageWindow, errorMessageWindow, terminatedMessageWindow);
	},
	setPageTitle(pageTitle: string) {
		document.title = pageTitle;
	},
	setBackground(backgroundImageUrl: string, backgroundColor: string, animationDuration: number) {
		console.log("TODO setBackground(...)");
		// TODO
	},
	addRootComponent(containerSelector: string, component: Component): void {
		const $container = containerSelector ? document.querySelector(containerSelector) : document.body;
		$container.appendChild(component.getMainElement());
	},
	setThemeClassName(theme: string) {
		// remove other theme classes
		document.body.className = document.body.className.replace(/theme-\w+/, '');
		// add theme class
		if (theme) {
			document.body.classList.add(theme);
		}
	},
	downloadFile(fileUrl: string, fileName: string) {
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
	},
	addClientToken(token: string) {
		let tokens = this.loadClientTokens();
		tokens[token] = true;
		this.saveClientTokens(tokens);
	},
	removeClientToken(token: string) {
		let tokens = this.loadClientTokens();
		delete tokens[token];
		this.saveClientTokens(tokens);
	},
	clearClientTokens() {
		this.saveClientTokens({});
	},

	loadClientTokens(): { [tokenName: string]: true } {
		let tokens: { [tokenName: string]: true } = {};
		let tokenJson = localStorage.getItem("clientTokens");
		if (tokenJson) {
			tokens = JSON.parse(tokenJson);
		}
		return tokens;
	},
	getClientTokens(): string[] {
		return Object.keys(this.loadClientTokens());
	},
	saveClientTokens(tokens: { [token: string]: true }) {
		localStorage.setItem("clientTokens", JSON.stringify(tokens));
	},
	exitFullScreen() {
		exitFullScreen();
	},
	async requestWakeLock(uuid: string): Promise<boolean> {
		return await requestWakeLock(uuid);
	},
	async releaseWakeLock(uuid: string) {
		return releaseWakeLock(uuid);
	},
	async goToUrl(url: string, blankPage: boolean) {
		console.info(`goToUrl(${url}, ${blankPage})`);
		if (blankPage) {
			window.open(url, '_blank');
		} else {
			location.href = url;
		}
	},
	async pushHistoryState(relativeUrl: string) {
		window.history.pushState({}, "", relativeUrl);
		// Globals.onNavigationStateChange.fire({location: createUiLocation(), triggeredByUser: false});
	},
	async navigateForward(steps: number) {
		window.history.go(steps);
	},
	setFavicon(url: string) {
		let link: HTMLLinkElement = document.querySelector("link[rel~='icon']");
		if (!link) {
			link = document.createElement('link');
			link.rel = 'icon';
			document.getElementsByTagName('head')[0].appendChild(link);
		}
		link.href = url;
	},
	setTitle(title: string) {
		document.title = title;
	}
};


