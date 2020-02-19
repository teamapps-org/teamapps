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

import {Device} from "mediasoup-client";

const socketClient = require('socket.io-client');
const socketPromise = require('./socket.io-promise').promise;
const mediasoup = require('mediasoup-client');

export class Receiver {
  private socket: SocketIOClient.Socket | any;
  private device: Device;
  private subscribeTransport: any;
  private subscribeStreamVideo: any;
  private subscribeStreamAudio: any;
  private consumerAudio: any;
  private consumerVideo: any;
  private clientUUID: string;
  private streamUUID: string;
  private sourceConstraints: any;
  private serverDomain: string;
  private serverPort: number;

  public constructor(serverDomain: string, serverPort: number) {
    this.clientUUID = this.createUUID();
    this.sourceConstraints = {audio: true, video: true};
    this.serverDomain = serverDomain;
    this.serverPort = serverPort;
  }

  public async connect(callback: (status : string) => void) {
    const opts = {
      path: '/server',
      transports: ['websocket'],
    };
    const serverUrl = `https://${this.serverDomain}:${this.serverPort}`;
    this.socket = socketClient(serverUrl, opts);
    this.socket.request = socketPromise(this.socket);
  
    this.socket.on('connect', async () => {
      const data = await this.socket.request('getRouterRtpCapabilities');
      await this.loadDevice(data);
      callback('connect');
    });

    this.socket.on('disconnect', () => {
      callback('disconnect');
    });
  
    this.socket.on('connect_error', (error: any) => {
      console.error('could not connect to %s%s (%s)', serverUrl, opts.path, error.message);
      callback('connect_error');
    });
  
    this.socket.on('newVideoProducer', (data: any) => {
      if (data.streamUUID === this.streamUUID)   
        callback('newVideoProducer');
    });
    
    this.socket.on('videoProducerGone', (data: any) => {
      if (data.streamUUID === this.streamUUID) {
        this.consumerVideo = null;
        callback('videoProducerGone');
      }
    });
  }

  async disconnect(callback: () => void) {
    this.socket.disconnect();
    this.socket = null;
    callback();
  }

  private async loadDevice(routerRtpCapabilities: any) {
    try {
      this.device = new mediasoup.Device();
    } catch (error) {
      if (error.name === 'UnsupportedError') {
        console.error('browser not supported');
      }
    }
    await this.device.load({ routerRtpCapabilities });

    // Workaround for audio autoplay policy
    {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const audioTrack = stream.getAudioTracks()[0];

      audioTrack.enabled = false;

      setTimeout(() => audioTrack.stop(), 120000);
    }
  }

  public async checkResources(streamUUID: string): Promise<boolean> {
    let resourcesValid: boolean = await this.socket.request('checkSubscribeResources',
      { streamUUID: streamUUID, clientUUID: this.clientUUID });
    if (!resourcesValid)
      console.log("stream UUID is incorrect");
    return resourcesValid;
  }

  public async subscribe(streamUUID: string, isAudioOnly: boolean, isButtonClicked: boolean, videoProducerAvailable: boolean, 
    subscribeCallback: (status: string, streamAudio: any, streamVideo: any) => void) {
      this.streamUUID = streamUUID;
      if ((this.subscribeTransport == undefined || this.subscribeTransport == null) && isButtonClicked) {
      const data = await this.socket.request('createConsumerTransport', {
        forceTcp: false,
        streamUUID: streamUUID,
        clientUUID: this.clientUUID 
      });
      if (data.error) {
        console.error(data.error);
        return;
      }
  
      this.subscribeTransport = this.device.createRecvTransport(data);
      this.subscribeTransport.on('connect', ({ dtlsParameters }: any, callback: any, errback: any) => {
        this.socket.request('connectConsumerTransport', {
          transportId: this.subscribeTransport.id,
          dtlsParameters: dtlsParameters,
          streamUUID: streamUUID,
          clientUUID: this.clientUUID
        })
        .then(callback)
        .catch(errback);
      });
  
      this.subscribeTransport.on('connectionstatechange', (state: any) => {
        switch (state) {
        case 'connecting':
          subscribeCallback('connecting', null, null);
          break;
  
        case 'connected':
          subscribeCallback('connected', null, null);
          break;
  
        case 'failed':
          this.subscribeTransport.close();
          subscribeCallback('failed', null, null);
          break;
  
        default: break;
        }
      });
      this.subscribeStreamAudio = await this.consume(this.subscribeTransport, 'audio');
    }
    if (this.subscribeTransport != undefined && this.subscribeTransport != null) {
      if (isAudioOnly) {
        try {
          if (videoProducerAvailable && this.consumerVideo != null) {
            await this.socket.request('pause', { streamUUID: streamUUID, clientUUID: this.clientUUID });
            this.consumerVideo.pause();
          }
          this.subscribeStreamVideo = null;
        } catch (error) {
          console.log(error);
        }
      } else {
        if (videoProducerAvailable) {
          this.subscribeStreamVideo = await this.consume(this.subscribeTransport, 'video');
          if (this.subscribeStreamVideo != null) {
            this.socket.request('resume', { streamUUID: streamUUID, clientUUID: this.clientUUID });
            this.consumerVideo.resume();
          }
        }
      } 
      subscribeCallback('updateStream', this.subscribeStreamAudio, this.subscribeStreamVideo);
    }
  }

  public async unsubscribe() {
    const checkData = await this.socket.request('removeConsumer',
      { streamUUID: this.streamUUID, clientUUID: this.clientUUID });
    await this.removeData(checkData);
    this.subscribeTransport.close();
    this.subscribeTransport = null;
    this.subscribeStreamVideo = null;
    this.subscribeStreamAudio = null;
    this.consumerVideo = null;
    this.consumerAudio = null;
    this.streamUUID = null;
  }

  private async consume(transport: any, kindValue: string) {
    const { rtpCapabilities } = this.device;
    const data = await this.socket.request('consume', 
      { rtpCapabilities: rtpCapabilities, streamUUID: this.streamUUID, clientUUID: this.clientUUID, kind: kindValue });
    if (data == null)
      return null;
    const {
      producerId,
      id,
      kind,
      rtpParameters
    } = data;
  
    let codecOptions = {};
    const consumer = await transport.consume({
      id,
      producerId,
      kind,
      rtpParameters,
      codecOptions
    });
    const stream = new MediaStream();
    stream.addTrack(consumer.track);
    if (kind ===  'audio')
      this.consumerAudio = consumer;
    else if (kind ===  'video')
      this.consumerVideo = consumer;
    return stream;
  }

  private removeData(isDataRemoved: any) {
    console.log("Delete consumer");
  }

  private createUUID(): string { 
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }
}
