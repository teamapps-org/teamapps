/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
!function(e,t){"object"==typeof exports&&"object"==typeof module?module.exports=t(require("socket.io-client"),require("axios")):"function"==typeof define&&define.amd?define(["socket.io-client","axios"],t):"object"==typeof exports?exports.avcore=t(require("socket.io-client"),require("axios")):e.avcore=t(e.io,e.axios)}(this,(function(e,t){return function(e){var t={};function r(i){if(t[i])return t[i].exports;var n=t[i]={i:i,l:!1,exports:{}};return e[i].call(n.exports,n,n.exports,r),n.l=!0,n.exports}return r.m=e,r.c=t,r.d=function(e,t,i){r.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:i})},r.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},r.t=function(e,t){if(1&t&&(e=r(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var i=Object.create(null);if(r.r(i),Object.defineProperty(i,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var n in e)r.d(i,n,function(t){return e[t]}.bind(null,n));return i},r.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return r.d(t,"a",t),t},r.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},r.p="",r(r.s=6)}([function(e,t,r){"use strict";var i;r.d(t,"a",(function(){return i})),r.d(t,"k",(function(){return n})),r.d(t,"j",(function(){return o})),r.d(t,"d",(function(){return s})),r.d(t,"l",(function(){return u})),r.d(t,"e",(function(){return E})),r.d(t,"i",(function(){return R})),r.d(t,"h",(function(){return d})),r.d(t,"c",(function(){return c})),r.d(t,"b",(function(){return S})),r.d(t,"f",(function(){return T})),r.d(t,"g",(function(){return a})),function(e){e.GET_SERVER_CONFIGS="getServerConfigs",e.CREATE_TRANSPORT="createTransport",e.CONNECT_TRANSPORT="connectTransport",e.CLOSE_TRANSPORT="closeTransport",e.PRODUCE="produce",e.CONSUME="consume",e.RESUME_CONSUMER="resumeConsumer",e.PAUSE_CONSUMER="pauseConsumer",e.CLOSE_CONSUMER="closeConsumer",e.RESUME_PRODUCER="resumeProducer",e.PAUSE_PRODUCER="pauseProducer",e.CLOSE_PRODUCER="closeProducer",e.FILE_STREAMING="fileStreaming",e.LIVE_STREAMING="liveStreaming",e.LIVE_TO_HLS="liveToHls",e.STOP_FILE_STREAMING="stopFileStreaming",e.START_RECORDING="startRecording",e.STOP_RECORDING="stopRecording",e.CREATE_PIPE_TRANSPORT="createPipeTransport",e.CONNECT_PIPE_TRANSPORT="connectPipeTransport",e.SET_PREFERRED_LAYERS="setPreferredLayers",e.SET_MAX_INCOMING_BITRATE="setMaxIncomingBitrate",e.PRODUCERS_STATS="producersStats",e.CONSUMERS_STATS="consumersStats",e.TRANSPORT_STATS="transportStats",e.PIPE_TO_REMOTE_PRODUCER="pipeToRemoteProducer",e.PIPE_FROM_REMOTE_PRODUCER="pipeFromRemoteProducer",e.WORKER_LOAD="workerLoad",e.NUM_WORKERS="numWorkers",e.RECORDED_STREAMS="recordedStreams",e.STREAM_RECORDINGS="streamRecordings",e.DELETE_STREAM_RECORDINGS="deleteStreamRecordings",e.DELETE_RECORDING="deleteRecording",e.PUSH_TO_SERVER_INPUTS="pushToServerInputs",e.PULL_FROM_SERVER_INPUTS="pullFromServerInputs",e.PUSH_TO_SERVER_OPTIONS="pushToServerOptions",e.PUSH_TO_SERVER="pushToServer",e.KINDS_BY_FILE="kindsByFile",e.REQUEST_KEYFRAME="requestKeyframe",e.LISTEN_STREAM_STARTED="listenStreamStarted",e.LISTEN_STREAM_STOPPED="listenStreamStopped",e.MIXER_START="mixerStart",e.MIXER_CLOSE="mixerClose",e.MIXER_ADD="mixerAdd",e.MIXER_REMOVE="mixerRemove",e.MIXER_UPDATE="mixerUpdate",e.MIXER_PIPE_START="mixerPipeStart",e.MIXER_PIPE_STOP="mixerPipeStop",e.LISTEN_MIXER_STOPPED="listenMixerStopped"}(i||(i={}));const n=[i.GET_SERVER_CONFIGS,i.CREATE_TRANSPORT,i.CONNECT_TRANSPORT,i.CLOSE_TRANSPORT,i.PRODUCE,i.CONSUME,i.LISTEN_STREAM_STARTED,i.LISTEN_STREAM_STOPPED,i.LISTEN_MIXER_STOPPED,i.CREATE_PIPE_TRANSPORT,i.CONNECT_PIPE_TRANSPORT,i.PIPE_TO_REMOTE_PRODUCER,i.PIPE_FROM_REMOTE_PRODUCER],o=Object.values(i).filter(e=>!n.includes(e));var s,u;!function(e){e.STREAM_STARTED="streamStarted",e.STREAM_STOPPED="streamStopped",e.MIXER_STOPPED="mixerStopped"}(s||(s={})),function(e){e.STATS="stats",e.TRAFFIC="traffic",e.CPU="cpu"}(u||(u={}));const E={ROOT:"hls",PLAYLIST:"master.m3u8"};var R;!function(e){e.API="api"}(R||(R={}));const d={PATH:"nexmo",AUDIO_SAMPLE_RATE:16e3,AUDIO_CHANNELS:1};var c,S,T,a;!function(e){e[e.UNKNOWN=500]="UNKNOWN",e[e.UNAUTHORIZED=401]="UNAUTHORIZED",e[e.INVALID_TRANSPORT=530]="INVALID_TRANSPORT",e[e.INVALID_PRODUCER=531]="INVALID_PRODUCER",e[e.INVALID_CONSUMER=532]="INVALID_CONSUMER",e[e.INVALID_STREAM=533]="INVALID_STREAM",e[e.INVALID_OPERATION=534]="INVALID_OPERATION",e[e.INVALID_WORKER=535]="INVALID_WORKER",e[e.INVALID_INPUT=536]="INVALID_INPUT",e[e.INVALID_ACTION=537]="INVALID_ACTION"}(c||(c={})),function(e){e[e.SUBSCRIBE=0]="SUBSCRIBE",e[e.PUBLISH=1]="PUBLISH",e[e.RECORDING=2]="RECORDING",e[e.STREAMING=3]="STREAMING",e[e.MIXER=4]="MIXER"}(S||(S={})),function(e){e[e.LIVE=0]="LIVE",e[e.RECORDING=1]="RECORDING",e[e.RTMP=2]="RTMP",e[e.HLS=3]="HLS"}(T||(T={})),function(e){e.SCALE="scale",e.CROP="crop",e.PAD="pad"}(a||(a={}))},function(e,t,r){"use strict";r.d(t,"a",(function(){return R}));var i=r(4),n=r.n(i),o=r(5),s=r.n(o),u=r(0),E=function(e,t,r,i){return new(r||(r=Promise))((function(n,o){function s(e){try{E(i.next(e))}catch(e){o(e)}}function u(e){try{E(i.throw(e))}catch(e){o(e)}}function E(e){var t;e.done?n(e.value):(t=e.value,t instanceof r?t:new r((function(e){e(t)}))).then(s,u)}E((i=i.apply(e,t||[])).next())}))};class R{constructor(e,t,r,i){this.closed=!1,this.log=i||console.log,this.url=e,this.worker=t,this.token=r}get client(){return this._client||(this._client=n()(this.url,{path:"",transports:["websocket"],query:`auth_token=${this.token}&mediasoup_worker=${this.worker}`,forceNew:!0})),this._client}connectSocket(){return new Promise((e,t)=>{this.client.connected?e():(this.client.on("error",e=>{"Not enough or too many segments"===e.message?e.errorId=u.c.UNAUTHORIZED:e.errorId=u.c.UNKNOWN,t(e)}),this.client.on("connect",e))})}resumeConsumer(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.RESUME_CONSUMER,e)}))}pauseConsumer(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.PAUSE_CONSUMER,e)}))}setPreferredLayers(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.SET_PREFERRED_LAYERS,e)}))}closeConsumer(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.CLOSE_CONSUMER,e)}))}resumeProducer(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.RESUME_PRODUCER,e)}))}pauseProducer(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.PAUSE_PRODUCER,e)}))}closeProducer(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.CLOSE_PRODUCER,e)}))}produce(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.PRODUCE,e)}))}consume(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.CONSUME,e)}))}createPipeTransport(){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.CREATE_PIPE_TRANSPORT)}))}connectPipeTransport(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.CONNECT_PIPE_TRANSPORT,e)}))}closeTransport(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.CLOSE_TRANSPORT,e)}))}getServerConfigs(){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.GET_SERVER_CONFIGS)}))}createTransport(){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.CREATE_TRANSPORT)}))}connectTransport(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.CONNECT_TRANSPORT,e)}))}setMaxIncomingBitrate(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.SET_MAX_INCOMING_BITRATE,e)}))}producersStats(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.PRODUCERS_STATS,e)}))}consumersStats(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.CONSUMERS_STATS,e)}))}transportStats(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.TRANSPORT_STATS,e)}))}workerLoad(){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.WORKER_LOAD)}))}numWorkers(){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.NUM_WORKERS)}))}pipeToRemoteProducer(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.PIPE_TO_REMOTE_PRODUCER,e)}))}pipeFromRemoteProducer(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.PIPE_FROM_REMOTE_PRODUCER,e)}))}startRecording(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.START_RECORDING,e)}))}stopRecording(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.STOP_RECORDING,e)}))}fileStreaming(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.FILE_STREAMING,e)}))}stopFileStreaming(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.STOP_FILE_STREAMING,e)}))}recordedStreams(){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.RECORDED_STREAMS)}))}streamRecordings(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.STREAM_RECORDINGS,e)}))}deleteStreamRecordings(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.DELETE_STREAM_RECORDINGS,e)}))}deleteRecording(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.DELETE_RECORDING,e)}))}pushToServerInputs(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.PUSH_TO_SERVER_INPUTS,e)}))}pushToServerOptions(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.PUSH_TO_SERVER_OPTIONS,e)}))}pushToServer(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.PUSH_TO_SERVER,e)}))}pullFromServerInputs(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.PULL_FROM_SERVER_INPUTS,e)}))}kindsByFile(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.KINDS_BY_FILE,e)}))}requestKeyframe(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.REQUEST_KEYFRAME,e)}))}listenStreamStarted(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.LISTEN_STREAM_STARTED,e)}))}listenStreamStopped(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.LISTEN_STREAM_STOPPED,e)}))}liveStreaming(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.LIVE_STREAMING,e)}))}liveToHls(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.LIVE_TO_HLS,e)}))}mixerStart(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.MIXER_START,e)}))}mixerClose(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.MIXER_CLOSE,e)}))}mixerAdd(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.MIXER_ADD,e)}))}mixerUpdate(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.MIXER_UPDATE,e)}))}mixerRemove(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.MIXER_REMOVE,e)}))}mixerPipeStart(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.MIXER_PIPE_START,e)}))}mixerPipeStop(e){return E(this,void 0,void 0,(function*(){yield this.request(u.a.MIXER_PIPE_STOP,e)}))}listenMixerStopped(e){return E(this,void 0,void 0,(function*(){return yield this.request(u.a.LISTEN_MIXER_STOPPED,e)}))}clear(){this.closed=!0,this._client&&(this._client.removeAllListeners(),this.client.connected&&this._client.disconnect())}request(e,t={}){return E(this,void 0,void 0,(function*(){if(!this.closed)return this._client||u.k.includes(e)?this.socketRequest(e,t):this.restRequest(e,t)}))}socketRequest(e,t={}){return E(this,void 0,void 0,(function*(){return yield this.connectSocket(),this.log("sent message",e,JSON.stringify(t)),new Promise((r,i)=>{this.client.emit(e,t,t=>{t&&"boolean"!=typeof t&&t.hasOwnProperty("errorId")?(this.log("got error",e,JSON.stringify(t)),i(t)):(this.log("got message",e,JSON.stringify(t)),r(t))})})}))}restRequest(e,t={}){return E(this,void 0,void 0,(function*(){try{const{data:r}=yield s.a.post(`${this.url}/${u.i.API}/${this.worker}/${e}`,t,{headers:{"Content-Type":"application/json",Authorization:"Bearer "+this.token}});return this.log("got message",e,JSON.stringify(r)),r}catch(e){let t=u.c.UNKNOWN;throw this.log("got error",e),e.response&&e.response.status&&u.c[e.response.status]&&(t=e.response.status),{errorId:t}}}))}}},function(e,t){},function(e,t,r){"use strict";r.d(t,"a",(function(){return n}));var i=r(0);class n{static pinCodeChoice(e,t,r="Please, enter active pin code."){return[{action:"talk",text:r,bargeIn:!0},{action:"input",maxDigits:e,timeOut:10,eventUrl:[t]}]}static pinCodeChoiceRepeat(e,t,r="Sorry, this pin code is invalid.",i){return[{action:"talk",text:r},...n.pinCodeChoice(e,t,i)]}static mixerConnect(e,t,r="Connecting to meeting. Please, wait."){return[{action:"talk",text:r},{action:"connect",endpoint:[{type:"websocket",uri:`${e.replace("http","ws")}/${i.h.PATH}`,"content-type":"audio/l16;rate="+i.h.AUDIO_SAMPLE_RATE,headers:t}]}]}}},function(t,r){t.exports=e},function(e,r){e.exports=t},function(e,t,r){"use strict";r.r(t);var i=r(0);r.d(t,"ERROR",(function(){return i.c})),r.d(t,"ACTION",(function(){return i.a})),r.d(t,"API_OPERATION",(function(){return i.b})),r.d(t,"EVENT",(function(){return i.d})),r.d(t,"STAT",(function(){return i.l})),r.d(t,"MIXER_PIPE_TYPE",(function(){return i.f})),r.d(t,"NEXMO",(function(){return i.h})),r.d(t,"HLS",(function(){return i.e})),r.d(t,"PATH",(function(){return i.i})),r.d(t,"SOCKET_ONLY_ACTIONS",(function(){return i.k})),r.d(t,"REST_ACTIONS",(function(){return i.j})),r.d(t,"MIXER_RENDER_TYPE",(function(){return i.g}));var n=r(1);r.d(t,"MediasoupSocketApi",(function(){return n.a}));var o=r(2);for(var s in o)["ERROR","ACTION","API_OPERATION","EVENT","STAT","MIXER_PIPE_TYPE","NEXMO","HLS","PATH","SOCKET_ONLY_ACTIONS","REST_ACTIONS","MIXER_RENDER_TYPE","MediasoupSocketApi","NexmoUtils","default"].indexOf(s)<0&&function(e){r.d(t,e,(function(){return o[e]}))}(s);var u=r(3);r.d(t,"NexmoUtils",(function(){return u.a}))}])}));
