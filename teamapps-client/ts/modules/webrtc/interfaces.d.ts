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
}
export interface Ps {
    ws: any;
    room: any;
    peers?: any;
}
export interface Params {
    audio?: boolean;
    video?: boolean;
    minBitrate?: number | null;
    maxBitrate?: number | null;
    qualityChangerSelector?: string;
    localVideo: string | HTMLMediaElement;
    getUserMedia: () => Promise<MediaStream>;
    errorAutoPlayCallback: Function;
    onProfileChange: Function;
    serverUrl?: string;
}
export interface SocketResponse {
    errorId?: string | number;
    error?: string | number;
}
