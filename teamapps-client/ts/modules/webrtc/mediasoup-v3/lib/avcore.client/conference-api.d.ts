/// <reference types="node" />
import { EventEmitter } from "events";
import { MediaKind } from 'mediasoup-client/lib/RtpParameters';
import { ConferenceInput, ConsumerLayers } from 'avcore';
export declare interface ConferenceApi {
    on(event: 'bitRate', listener: ({ bitRate: number, kind: MediaKind }: {
        bitRate: any;
        kind: any;
    }) => void): this;
    on(event: 'connectionstatechange', listener: ({ state: string }: {
        state: any;
    }) => void): this;
    on(event: 'newTransportId', listener: ({ id: string }: {
        id: any;
    }) => void): this;
    on(event: 'newProducerId', listener: ({ id: string, kind: MediaKind }: {
        id: any;
        kind: any;
    }) => void): this;
    on(event: 'newConsumerId', listener: ({ id: string, kind: MediaKind }: {
        id: any;
        kind: any;
    }) => void): this;
    on(event: 'addtrack', listener: (event: MediaStreamTrackEvent) => void): this;
    on(event: 'removetrack', listener: (event: MediaStreamTrackEvent) => void): this;
}
export declare class ConferenceApi extends EventEmitter {
    private api;
    private readonly configs;
    private readonly device;
    private readonly connectors;
    private readonly layers;
    private readonly log;
    private operation;
    private transport;
    private mediaStream?;
    private transportTimeout;
    private iceServers;
    private simulcast;
    private readonly availableKinds;
    constructor(configs: ConferenceInput);
    setPreferredLayers(layers: ConsumerLayers): Promise<void>;
    addTrack(track: MediaStreamTrack): Promise<void>;
    removeTrack(track: MediaStreamTrack): Promise<void>;
    setMaxPublisherBitrate(bitrate: number): Promise<void>;
    updateKinds(kinds: MediaKind[]): Promise<void>;
    private destroyClient;
    private createClient;
    private init;
    publish(mediaStream: MediaStream): Promise<MediaStream>;
    subscribe(): Promise<MediaStream>;
    private unsubscribeTrack;
    private subscribeTrack;
    private publishTrack;
    private listenStats;
    close(hard?: boolean): Promise<void>;
    private closeConnectors;
    private restartAll;
    private getTransport;
}
