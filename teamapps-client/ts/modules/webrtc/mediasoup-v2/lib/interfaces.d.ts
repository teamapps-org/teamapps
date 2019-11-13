export interface ConferenceData {
    uid: string;
    token: string;
    params?: Params;
}
export interface JSONData {
    server: {
        url: string;
    };
    iceServers: [];
}
export interface Capturing {
    stream: MediaStream;
    audio: undefined | boolean | MediaTrackConstraints;
    video: undefined | boolean | MediaTrackConstraints;
}
export interface Ps {
    ws: any;
    room: any;
    peers?: any;
}
export interface Params {
    audio?: boolean;
    video?: boolean;
    mixFrameRate?: number;
    minBitrate?: number | null;
    maxBitrate?: number | null;
    qualityChangerSelector?: string;
    localVideo: string | HTMLMediaElement;
    constraints: MediaStreamConstraintsExtended;
    additionalConstraints?: MediaStreamConstraints;
    errorAutoPlayCallback: Function;
    onProfileChange: Function;
    serverUrl?: string;
    simulcast: boolean;
    mediaStreamCapturedCallback?: (mediaStream: MediaStream) => void;
}
export interface SocketResponse {
    errorId?: string | number;
    error?: string | number;
}
export interface MediaDevicesExtended extends MediaDevices {
    getDisplayMedia: (constraints: MediaStreamConstraints) => Promise<MediaStream>;
}
export interface MediaStreamConstraintsExtended extends MediaStreamConstraints {
    isDisplay?: boolean;
}
