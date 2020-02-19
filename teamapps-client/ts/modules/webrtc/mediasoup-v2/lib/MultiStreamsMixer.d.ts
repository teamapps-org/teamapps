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
export declare type MixSizingInfo = {
    fullcanvas?: boolean;
    width?: number;
    height?: number;
    left?: number;
    top?: number;
    right?: number;
    bottom?: number;
    onRender?: (context: CanvasRenderingContext2D, video: HTMLVideoElement, mixSizingInfo: MixSizingInfo, x: number, y: number, width: number, height: number) => void;
};
export declare type MediaStreamWithMixiSizingInfo = {
    mediaStream: MediaStream;
    mixSizingInfo: MixSizingInfo;
};
export declare type MediaStreamWithMixiSizingInfoAndVideo = MediaStreamWithMixiSizingInfo & {
    video?: HTMLVideoElement;
};
export declare class MultiStreamsMixer {
    private closed;
    private inputMediaStreams;
    private outputMediaStream?;
    private canvas;
    private context;
    private frameInterval;
    private isStopDrawingFrames;
    private audioContext?;
    private audioDestination?;
    constructor(inputMediaStreams: MediaStreamWithMixiSizingInfo[], frameRate?: number);
    getMixedStream(): Promise<MediaStream>;
    getInputMediaStreams(): MediaStreamWithMixiSizingInfoAndVideo[];
    close(): void;
    private drawVideosToCanvas;
    private drawImage;
    private getMixedVideoStream;
    private getMixedAudioStream;
    private static createVideo;
    private resetVideoStreams;
}
export declare function determineVideoSize(mediaStream: MediaStream, timeout?: number): Promise<{
    width: number;
    height: number;
}>;
