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
    private inputMediaStreams;
    private outputMediaStream?;
    private canvas;
    private context;
    private frameInterval;
    private isStopDrawingFrames;
    private audioContext?;
    private audioDestination?;
    constructor(inputMediaStreams: MediaStreamWithMixiSizingInfo[], frameRate?: number);
    getMixedStream(): Promise<MediaStream | undefined>;
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
