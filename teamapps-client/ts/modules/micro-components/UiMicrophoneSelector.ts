import {parseHtml} from "../Common";
import {UiAudioActivityDisplay} from "../micro-components/UiAudioActivityDisplay";
import {TeamAppsEvent} from "../util/TeamAppsEvent";

export class UiMicrophoneSelector {

	public readonly onMicrophoneSelected: TeamAppsEvent<string> = new TeamAppsEvent(this);

	private $mainDomElement: HTMLElement;
	private audioMediaStreams: MediaStream[];
	private listEntries: ListEntry[] = [];

	constructor() {
		this.$mainDomElement = parseHtml(`<div class="UiMicrophoneSelector">
		</div>`)
		this.updateList()
	}

	public async updateList() {
		this.stopIndicating();

		this.$mainDomElement.innerHTML = '';

		this.listEntries = [];
		const deviceInfos = await navigator.mediaDevices.enumerateDevices();
		for (let i = 0; i !== deviceInfos.length; ++i) {
			const deviceInfo = deviceInfos[i];
			const option = document.createElement('div');
			if (deviceInfo.kind === 'audioinput') {
				let listEntry = new ListEntry(deviceInfo);
				this.listEntries.push(listEntry);
				this.$mainDomElement.appendChild(listEntry.getMainElement());
				listEntry.getMainElement().addEventListener("click", ev => {
					this.onMicrophoneSelected.fire(deviceInfo.deviceId);
				})
			}
		}
	}

	async startIndicating() {
		this.listEntries.forEach(le => le.startIndicating());
	}

	stopIndicating() {
		this.listEntries.forEach(le => le.stopIndicating());
	}

	public getMainElement() {
		return this.$mainDomElement;
	}
}

class ListEntry {
	private $element: HTMLElement;
	private audioActivityDisplay: UiAudioActivityDisplay;
	private audioStream: MediaStream;

	constructor(private deviceInfo: MediaDeviceInfo) {
		this.$element = parseHtml(`<div class="mic" data-device-id="${deviceInfo.deviceId}">
						<div class="mic-volume-indicator-wrapper"></div>
						<div class="mic-name">${deviceInfo.label || deviceInfo.deviceId}</div>
					</div>`);
		const $volumeIndicatorWrapper = this.$element.querySelector(":scope .mic-volume-indicator-wrapper");
		this.audioActivityDisplay = new UiAudioActivityDisplay();
		$volumeIndicatorWrapper.appendChild(this.audioActivityDisplay.getMainDomElement());
	}

	async startIndicating() {
		this.audioStream = await navigator.mediaDevices.getUserMedia({audio: {deviceId: this.deviceInfo.deviceId}});
		this.audioActivityDisplay.bindToStream(this.audioStream)
	}

	stopIndicating() {
		if (this.audioStream != null) {
			this.audioActivityDisplay.unbind();
			this.audioStream.getTracks().forEach((track) => track.stop());
		}
	}

	getMainElement() {
		return this.$element;
	}
}

