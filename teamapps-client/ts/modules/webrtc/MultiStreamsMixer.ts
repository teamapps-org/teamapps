/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
type MediaStreamAudioDestinationNode = AudioNode & { stream: MediaStream };
export type MixSizingInfo = {
    fullcanvas?: boolean,
    width?: number,    // <1: percentage
    height?: number,   // <1: percentage
    left?: number,     // <1: percentage
    top?: number,      // <1: percentage
    right?: number,    // <1: percentage
    bottom?: number,   // <1: percentage
    onRender?: (context: CanvasRenderingContext2D, video: HTMLVideoElement, mixSizingInfo: MixSizingInfo, x: number, y: number, width: number, height: number) => void
};
export type MediaStreamWithMixiSizingInfo = {
    mediaStream: MediaStream,
    mixSizingInfo: MixSizingInfo
};
export type MediaStreamWithMixiSizingInfoAndVideo = MediaStreamWithMixiSizingInfo & { video?: HTMLVideoElement };

export class MultiStreamsMixer {
    private closed = false;
    private inputMediaStreams: MediaStreamWithMixiSizingInfoAndVideo[];
    private outputMediaStream?: MediaStream;

    private canvas: HTMLCanvasElement;
    private context: CanvasRenderingContext2D | null;

    private frameInterval: number;
    private isStopDrawingFrames = false;

    private audioContext?: AudioContext & { createMediaStreamDestination(): MediaStreamAudioDestinationNode };
    private audioDestination?: MediaStreamAudioDestinationNode;

    constructor(inputMediaStreams: MediaStreamWithMixiSizingInfo[], frameRate: number = 10) {
        if (inputMediaStreams.length === 0) {
            throw "inputMediaSreams may not be empty!";
        }
        this.closed=false;
        this.frameInterval = 1000 / frameRate;
        this.inputMediaStreams = inputMediaStreams;
        this.canvas = document.createElement('canvas');
        this.context = this.canvas.getContext('2d');
        this.canvas.style.position = 'absolute';
        this.canvas.style.left = '-100000px';
        this.canvas.style.top = '-100000px';
        (document.body || document.documentElement).appendChild(this.canvas);
    }

    public async getMixedStream(): Promise<MediaStream> {
        if (this.outputMediaStream == null) {
            this.closed=false;
            const mixedVideoStream = await this.getMixedVideoStream();
            const mixedAudioStream = this.getMixedAudioStream();

            if (mixedAudioStream) {
                mixedAudioStream.getTracks().filter((t) => {
                    return t.kind === 'audio';
                }).forEach((track) => {
                    mixedVideoStream.addTrack(track);
                });
            }
            this.drawVideosToCanvas();

            this.outputMediaStream = mixedVideoStream;

            let endListenerAlreadyCalled = false;
            this.outputMediaStream.getTracks().forEach(track => {
                track.addEventListener("ended", () => {
                    let streamAlive = this.outputMediaStream.active && this.outputMediaStream.getTracks().filter(t => t.readyState !== 'ended').length > 0;
                    if (!endListenerAlreadyCalled && !streamAlive) {
                        endListenerAlreadyCalled = true;
                        console.log("Closing MultiStreamsMixer since outputMediaStream has ended.");
                        this.close();
                    }
                });
            })
        }
        return this.outputMediaStream;
    }

    public getInputMediaStreams() {
        return this.inputMediaStreams;
    }

    public close() {
        if(this && !this.closed){
            this.closed=true;
            [this.outputMediaStream].concat(this.inputMediaStreams.map(ms => ms.mediaStream))
                .forEach(ms => ms && ms.getTracks().forEach(t => {
                    t.stop();
                    t.dispatchEvent(new Event("ended"));
                }));
            this.canvas.remove();
        }
    };

    private drawVideosToCanvas() {
        if (this.isStopDrawingFrames) {
            return;
        }

        let fullcanvasVideo = this.inputMediaStreams.filter(ims => ims.mixSizingInfo.fullcanvas && ims.mediaStream.getVideoTracks().length > 0)[0];
        let remainingVideos = this.inputMediaStreams.filter(ims => ims !== fullcanvasVideo && ims.mediaStream.getVideoTracks().length > 0);

        if (fullcanvasVideo && this.canvas) {
            this.canvas.width = fullcanvasVideo.mixSizingInfo.width || (fullcanvasVideo.video != null ? fullcanvasVideo.video.videoWidth : 0);
            this.canvas.height = fullcanvasVideo.mixSizingInfo.height || (fullcanvasVideo.video != null ? fullcanvasVideo.video.videoHeight : 0);
        } else {
            this.canvas.width = 360;
            this.canvas.height = 240;
        }

        if (fullcanvasVideo && fullcanvasVideo.video instanceof HTMLVideoElement) {
            this.drawImage(fullcanvasVideo.video, fullcanvasVideo.mixSizingInfo);
        }

        remainingVideos.forEach((video) => {
            if(video.video){
                this.drawImage(video.video, video.mixSizingInfo);
            }
        });

        setTimeout(() => this.drawVideosToCanvas(), this.frameInterval);
    }

    private drawImage(video: HTMLVideoElement, mixSizingInfo: MixSizingInfo) {
        if (this.isStopDrawingFrames) {
            return;
        }

        let x = 0;
        let y = 0;
        let width;
        let height;

        if (mixSizingInfo.width != null) {
            if (mixSizingInfo.width >= 1) {
                width = mixSizingInfo.width;
            } else {
                width = this.canvas.width * mixSizingInfo.width;
            }
        } else {
            width = this.canvas.width;
        }
        if (mixSizingInfo.height != null) {
            if (mixSizingInfo.height >= 1) {
                height = mixSizingInfo.height;
            } else {
                height = this.canvas.height * mixSizingInfo.height;
            }
        } else {
            height = this.canvas.height;
        }

        if (mixSizingInfo.left != null) {
            if (mixSizingInfo.left >= 1) {
                x = mixSizingInfo.left;
            } else {
                x = this.canvas.width * mixSizingInfo.left;
            }
        }
        if (mixSizingInfo.top != null) {
            if (mixSizingInfo.top >= 1) {
                y = mixSizingInfo.top;
            } else {
                y = this.canvas.height * mixSizingInfo.top;
            }
        }
        if (mixSizingInfo.right != null) {
            if (mixSizingInfo.right >= 1) {
                x = this.canvas.width - width - mixSizingInfo.right;
            } else {
                x = this.canvas.width - width - (this.canvas.width * mixSizingInfo.right);
            }
        }
        if (mixSizingInfo.bottom != null) {
            if (mixSizingInfo.bottom >= 1) {
                y = this.canvas.height - height - mixSizingInfo.bottom;
            } else {
                y = this.canvas.height - height - (this.canvas.height * mixSizingInfo.bottom);
            }
        }


        if(this.context){
            this.context.drawImage(video, x, y, width, height);

            if (typeof mixSizingInfo.onRender === 'function') {
                mixSizingInfo.onRender(this.context, video, mixSizingInfo, x, y, width, height);
            }
        }

    }

    private async getMixedVideoStream() {
        await this.resetVideoStreams();

        const videoStream = new MediaStream();

        const capturedStream = ((this.canvas as any).captureStream && (this.canvas as any).captureStream())
            || ((this.canvas as any).mozCaptureStream && (this.canvas as any).mozCaptureStream());
        capturedStream.getTracks().filter((t: MediaStreamTrack) => {
            return t.kind === 'video';
        }).forEach((track: MediaStreamTrack) => {
            videoStream.addTrack(track);
        });

        return videoStream;
    }

    private getMixedAudioStream(): MediaStream | undefined {
        this.audioContext = new ((window as any).AudioContext || (window as any).webkitAudioContext || (window as any).mozAudioContext)();
        if (!this.audioContext) {
            return;
        }

        const audioSources: MediaStreamAudioSourceNode[] = [];
        let audioTracksLength = 0;
        this.inputMediaStreams.forEach((stream) => {
            if (!stream.mediaStream.getTracks().filter((t) => {
                return t.kind === 'audio';
            }).length) {
                return;
            }
            audioTracksLength++;
            if (!this.audioContext) {
                return;
            }
            const audioSource: MediaStreamAudioSourceNode = this.audioContext.createMediaStreamSource(stream.mediaStream);

            audioSources.push(audioSource);

        });

        if (!audioTracksLength) {
            return;
        }

        this.audioDestination = this.audioContext.createMediaStreamDestination();

        audioSources.forEach((audioSource) => {
            if (!this.audioDestination) {
                return;
            }
            audioSource.connect(this.audioDestination);
        });
        return this.audioDestination.stream;
    }

    private static async createVideo(stream: MediaStreamWithMixiSizingInfoAndVideo): Promise<HTMLVideoElement> {
        const video = document.createElement('video');
        video.srcObject = stream.mediaStream;
        video.muted = true;
        video.volume = 0;
        await video.play();

        if (stream.mixSizingInfo.width != null && stream.mixSizingInfo.height != null) {
            video.width = stream.mixSizingInfo.width;
            video.height = stream.mixSizingInfo.height;
        } else {
            let size = await determineVideoSize(stream.mediaStream);
            video.width = size.width;
            video.height = size.height;
        }

        return video;
    }

    private async resetVideoStreams() {
        for (let stream of this.inputMediaStreams) {
            delete stream.video;
            if (stream.mediaStream.getVideoTracks().length == 0) {
                continue;
            }

            stream.video = await MultiStreamsMixer.createVideo(stream);
        }
    }

}

export async function determineVideoSize(mediaStream: MediaStream, timeout = 1000): Promise<{ width: number, height: number }> {
    if (mediaStream.getVideoTracks().length == 0) {
        return {width: 0, height: 0};
    }
    return new Promise((resolve, reject) => {
        let video:HTMLVideoElement = document.createElement('video');
        video.style.position = 'absolute';
        video.style.left = '-100000px';
        video.style.top = '-100000px';
        document.body.appendChild(video);
        video.srcObject = mediaStream;
        video.muted = true;
        video.onloadedmetadata = (e) => {
            resolve({width: video.videoWidth, height: video.videoHeight});
            video.srcObject = null;
            video.remove();
        };
        setTimeout(() => {
            reject();
            video.srcObject = null;
            video.remove();
        }, timeout);
    });
}
