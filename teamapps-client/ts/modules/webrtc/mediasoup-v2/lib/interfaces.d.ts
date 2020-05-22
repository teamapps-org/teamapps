/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
    onRemoteStream?: (stream: MediaStream) => void;
    onBitrate?: (mediaType: "audio" | "video", bitrate: number) => void;
    serverUrl?: string;
    simulcast: boolean;
    msUrl?: string;
}
export interface SocketResponse {
    errorId?: string | number;
    error?: string | number;
}
export interface MediaDevicesExtended extends MediaDevices {
    getDisplayMedia: (constraints: MediaStreamConstraints) => Promise<MediaStream>;
}
