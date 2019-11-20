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
    errorAutoPlayCallback: Function;
    onProfileChange: Function;
    onStatusChange?: (live: {
        audio: boolean;
        video: boolean;
    }) => void;
    onConnectionChange?: (connected: boolean) => void;
    onBitrate?: (mediaType: "audio" | "video", bitrate: number) => void;
    serverUrl?: string;
    simulcast: boolean;
}
export interface SocketResponse {
    errorId?: string | number;
    error?: string | number;
}
export interface MediaDevicesExtended extends MediaDevices {
    getDisplayMedia: (constraints: MediaStreamConstraints) => Promise<MediaStream>;
}
