import {TeamAppsEvent} from "../../util/TeamAppsEvent";

export class AudioTrackMixPlayer {

	public readonly onResumeSuccessful: TeamAppsEvent<void> = new TeamAppsEvent(this);
	private audioContext: AudioContext;

	constructor() {
		this.audioContext = new ((window as any).AudioContext || (window as any).webkitAudioContext || (window as any).mozAudioContext)();
	}

	public async addAudioTrack(track: MediaStreamTrack) {
		let audioTrackSource = this.audioContext.createMediaStreamSource(new MediaStream([track]));
		audioTrackSource.connect(this.audioContext.destination);
		
		track.addEventListener("ended", () => {
			// This is probably not necessary, since the audio nodes should die with their mediaStream. See: https://www.w3.org/TR/webaudio/#lifetime-AudioNode
			// Note that this event is somehow not triggered by Firefox although the track switches to state "ended" as stated in the spec.
			audioTrackSource.disconnect();
		});
	}

	public async tryResume() {
		if (this.audioContext.state !== "running") {
			await this.audioContext.resume();
			if ((this.audioContext.state as string) === "running") {
				this.onResumeSuccessful.fireIfChanged(null);
			}
		} else {
			this.onResumeSuccessful.fireIfChanged(null);
		}
	}

	public getAudioContextState() {
		return this.audioContext.state;
	}
}