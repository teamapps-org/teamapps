/// <reference types="node" />
import { EventEmitter } from "events";
import { ConferenceInput } from '../../ms/interfaces';
interface IConferenceApi {
}
export declare class ConferenceApi extends EventEmitter implements IConferenceApi {
    private readonly api;
    private readonly configs;
    private readonly device;
    private operation;
    private transport;
    private mediaStream?;
    private remoteIds;
    private transportTimeout;
    constructor(configs: ConferenceInput);
    startRecording(): Promise<void>;
    stopRecording(): Promise<void>;
    private init;
    publish(mediaStream: MediaStream): Promise<MediaStream>;
    subscribe(): Promise<MediaStream>;
    private subscribeTrack;
    private publishTrack;
    private consume;
    private listenStats;
    close(hard?: boolean): Promise<void>;
    private getTransport;
}
export {};
