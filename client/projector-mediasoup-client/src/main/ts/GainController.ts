export class GainController {

    private readonly context: AudioContext;
    private _inputTrack: MediaStreamTrack;
    private inputStream: MediaStream;
    private micStreamSource: MediaStreamAudioSourceNode;
    private readonly gainFilter: GainNode;
    private readonly destination: MediaStreamAudioDestinationNode;
    private outputStream: MediaStream;
    public readonly outputTrack: MediaStreamTrack;

    constructor(initialGain: number) {
        this.context = new ((window as any).AudioContext || (window as any).webkitAudioContext)()
        this.inputStream = new MediaStream();
        this.gainFilter = this.context.createGain();
        this.gainFilter.gain.value = initialGain;
        this.destination = this.context.createMediaStreamDestination();
        this.outputStream = this.destination.stream;
        this.outputTrack = this.outputStream.getAudioTracks()[0];

        this.gainFilter.connect(this.destination);
    }

    set inputTrack(inputTrack: MediaStreamTrack) {
        this.micStreamSource?.disconnect();
        if (this._inputTrack != null) {
            this.inputStream?.removeTrack(this._inputTrack);
        }

        this._inputTrack = inputTrack;

        if (inputTrack != null) {
            this.inputStream = new MediaStream([inputTrack]);
            this.micStreamSource = this.context.createMediaStreamSource(this.inputStream);
            this.micStreamSource.connect(this.gainFilter);
        }
    }

    get inputTrack() {
        return this._inputTrack;
    }

    get gain(): number {
        return this.gainFilter.gain.value;
    }

    set gain(value: number) {
        this.gainFilter.gain.value = value;
    }

    // close() {
    // 	// just make 100% sure everything is closed and released!
    // 	this.micStreamSource.disconnect();
    // 	this.inputStream.removeTrack(this.inputTrack);
    // 	this.context.close();
    // 	this.gainFilter.disconnect();
    // 	this.destination.disconnect();
    // 	this.outputStream.removeTrack(this.outputTrack);
    // 	this.outputTrack.dispatchEvent(new Event("ended"));
    // }
}

