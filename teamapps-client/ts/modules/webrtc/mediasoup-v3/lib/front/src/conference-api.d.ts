/// <reference types="node" />
import { EventEmitter } from "events";
import { ConferenceInput } from '../../ms/interfaces';
import { ConsumerLayers } from 'mediasoup/lib/Consumer';
interface IConferenceApi {
    on(event: 'bitRate', listener: ({ bitRate: number, kind: MediaKind }: {
        bitRate: any;
        kind: any;
    }) => void): this;
}
export declare class ConferenceApi extends EventEmitter implements IConferenceApi {
    private readonly api;
    private readonly configs;
    private readonly device;
    private readonly remoteIds;
    private readonly layers;
    private operation;
    private transport;
    private mediaStream?;
    private transportTimeout;
    private iceServers;
    private simulcast;
    private readonly timeouts;
    constructor(configs: ConferenceInput);
    startRecording(): Promise<void>;
    stopRecording(): Promise<void>;
    setPreferredLayers(layers: ConsumerLayers): Promise<void>;
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
