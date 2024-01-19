/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
(function webpackUniversalModuleDefinition(root, factory) {
    if (typeof exports === 'object' && typeof module === 'object')
        module.exports = factory(require("socket.io-client"), require("axios"));
    else if (typeof define === 'function' && define.amd)
        define(["socket.io-client", "axios"], factory);
    else if (typeof exports === 'object')
        exports["avcore"] = factory(require("socket.io-client"), require("axios"));
    else
        root["avcore"] = factory(root["io"], root["axios"]);
})(this, (__WEBPACK_EXTERNAL_MODULE__173__, __WEBPACK_EXTERNAL_MODULE__781__) => {
    return /******/ (() => { // webpackBootstrap
        /******/
        "use strict";
        /******/
        var __webpack_modules__ = ({

            /***/ 781:
            /***/ ((module) => {

                module.exports = __WEBPACK_EXTERNAL_MODULE__781__;

                /***/
            }),

            /***/ 173:
            /***/ ((module) => {

                module.exports = __WEBPACK_EXTERNAL_MODULE__173__;

                /***/
            })

            /******/
        });
        /************************************************************************/
        /******/ 	// The module cache
        /******/
        var __webpack_module_cache__ = {};
        /******/
        /******/ 	// The require function
        /******/
        function __webpack_require__(moduleId) {
            /******/ 		// Check if module is in cache
            /******/
            var cachedModule = __webpack_module_cache__[moduleId];
            /******/
            if (cachedModule !== undefined) {
                /******/
                return cachedModule.exports;
                /******/
            }
            /******/ 		// Create a new module (and put it into the cache)
            /******/
            var module = __webpack_module_cache__[moduleId] = {
                /******/ 			// no module.id needed
                /******/ 			// no module.loaded needed
                /******/            exports: {}
                /******/
            };
            /******/
            /******/ 		// Execute the module function
            /******/
            __webpack_modules__[moduleId](module, module.exports, __webpack_require__);
            /******/
            /******/ 		// Return the exports of the module
            /******/
            return module.exports;
            /******/
        }

        /******/
        /************************************************************************/
        /******/ 	/* webpack/runtime/compat get default export */
        /******/
        (() => {
            /******/ 		// getDefaultExport function for compatibility with non-harmony modules
            /******/
            __webpack_require__.n = (module) => {
                /******/
                var getter = module && module.__esModule ?
                    /******/                () => (module['default']) :
                    /******/                () => (module);
                /******/
                __webpack_require__.d(getter, {a: getter});
                /******/
                return getter;
                /******/
            };
            /******/
        })();
        /******/
        /******/ 	/* webpack/runtime/define property getters */
        /******/
        (() => {
            /******/ 		// define getter functions for harmony exports
            /******/
            __webpack_require__.d = (exports, definition) => {
                /******/
                for (var key in definition) {
                    /******/
                    if (__webpack_require__.o(definition, key) && !__webpack_require__.o(exports, key)) {
                        /******/
                        Object.defineProperty(exports, key, {enumerable: true, get: definition[key]});
                        /******/
                    }
                    /******/
                }
                /******/
            };
            /******/
        })();
        /******/
        /******/ 	/* webpack/runtime/hasOwnProperty shorthand */
        /******/
        (() => {
            /******/
            __webpack_require__.o = (obj, prop) => (Object.prototype.hasOwnProperty.call(obj, prop))
            /******/
        })();
        /******/
        /******/ 	/* webpack/runtime/make namespace object */
        /******/
        (() => {
            /******/ 		// define __esModule on exports
            /******/
            __webpack_require__.r = (exports) => {
                /******/
                if (typeof Symbol !== 'undefined' && Symbol.toStringTag) {
                    /******/
                    Object.defineProperty(exports, Symbol.toStringTag, {value: 'Module'});
                    /******/
                }
                /******/
                Object.defineProperty(exports, '__esModule', {value: true});
                /******/
            };
            /******/
        })();
        /******/
        /************************************************************************/
        var __webpack_exports__ = {};
// This entry need to be wrapped in an IIFE because it need to be isolated against other modules in the chunk.
        (() => {
// ESM COMPAT FLAG
            __webpack_require__.r(__webpack_exports__);

// EXPORTS
            __webpack_require__.d(__webpack_exports__, {
                "ACTION": () => (/* reexport */ ACTION),
                "API_OPERATION": () => (/* reexport */ API_OPERATION),
                "ERROR": () => (/* reexport */ ERROR),
                "EVENT": () => (/* reexport */ EVENT),
                "HLS": () => (/* reexport */ HLS),
                "MIXER_PIPE_TYPE": () => (/* reexport */ MIXER_PIPE_TYPE),
                "MIXER_RENDER_TYPE": () => (/* reexport */ MIXER_RENDER_TYPE),
                "MediasoupSocketApi": () => (/* reexport */ MediasoupSocketApi),
                "NEXMO": () => (/* reexport */ NEXMO),
                "PATH": () => (/* reexport */ PATH),
                "REST_ACTIONS": () => (/* reexport */ REST_ACTIONS),
                "SOCKET_ONLY_ACTIONS": () => (/* reexport */ SOCKET_ONLY_ACTIONS),
                "STAT": () => (/* reexport */ STAT)
            });

            ;// CONCATENATED MODULE: ./src/constants.ts
            var ACTION;
            (function (ACTION) {
                ACTION["GET_SERVER_CONFIGS"] = "getServerConfigs";
                ACTION["CREATE_TRANSPORT"] = "createTransport";
                ACTION["CONNECT_TRANSPORT"] = "connectTransport";
                ACTION["CLOSE_TRANSPORT"] = "closeTransport";
                ACTION["PRODUCE"] = "produce";
                ACTION["CONSUME"] = "consume";
                ACTION["RESUME_CONSUMER"] = "resumeConsumer";
                ACTION["PAUSE_CONSUMER"] = "pauseConsumer";
                ACTION["CLOSE_CONSUMER"] = "closeConsumer";
                ACTION["RESUME_PRODUCER"] = "resumeProducer";
                ACTION["PAUSE_PRODUCER"] = "pauseProducer";
                ACTION["CLOSE_PRODUCER"] = "closeProducer";
                ACTION["FILE_STREAMING"] = "fileStreaming";
                ACTION["LIVE_STREAMING"] = "liveStreaming";
                ACTION["LIVE_TO_HLS"] = "liveToHls";
                ACTION["STOP_FILE_STREAMING"] = "stopFileStreaming";
                ACTION["START_RECORDING"] = "startRecording";
                ACTION["STOP_RECORDING"] = "stopRecording";
                ACTION["CREATE_PIPE_TRANSPORT"] = "createPipeTransport";
                ACTION["CONNECT_PIPE_TRANSPORT"] = "connectPipeTransport";
                ACTION["SET_PREFERRED_LAYERS"] = "setPreferredLayers";
                ACTION["SET_MAX_INCOMING_BITRATE"] = "setMaxIncomingBitrate";
                ACTION["PRODUCERS_STATS"] = "producersStats";
                ACTION["CONSUMERS_STATS"] = "consumersStats";
                ACTION["TRANSPORT_STATS"] = "transportStats";
                ACTION["PIPE_TO_REMOTE_PRODUCER"] = "pipeToRemoteProducer";
                ACTION["PIPE_FROM_REMOTE_PRODUCER"] = "pipeFromRemoteProducer";
                ACTION["WORKER_LOAD"] = "workerLoad";
                ACTION["NUM_WORKERS"] = "numWorkers";
                ACTION["RECORDED_STREAMS"] = "recordedStreams";
                ACTION["STREAM_RECORDINGS"] = "streamRecordings";
                ACTION["DELETE_STREAM_RECORDINGS"] = "deleteStreamRecordings";
                ACTION["DELETE_RECORDING"] = "deleteRecording";
                ACTION["PUSH_TO_SERVER_INPUTS"] = "pushToServerInputs";
                ACTION["PULL_FROM_SERVER_INPUTS"] = "pullFromServerInputs";
                ACTION["PUSH_TO_SERVER_OPTIONS"] = "pushToServerOptions";
                ACTION["PUSH_TO_SERVER"] = "pushToServer";
                ACTION["KINDS_BY_FILE"] = "kindsByFile";
                ACTION["REQUEST_KEYFRAME"] = "requestKeyframe";
                ACTION["LISTEN_STREAM_STARTED"] = "listenStreamStarted";
                ACTION["LISTEN_STREAM_STOPPED"] = "listenStreamStopped";
                ACTION["MIXER_START"] = "mixerStart";
                ACTION["MIXER_CLOSE"] = "mixerClose";
                ACTION["MIXER_ADD"] = "mixerAdd";
                ACTION["MIXER_REMOVE"] = "mixerRemove";
                ACTION["MIXER_UPDATE"] = "mixerUpdate";
                ACTION["MIXER_PIPE_START"] = "mixerPipeStart";
                ACTION["MIXER_PIPE_STOP"] = "mixerPipeStop";
                ACTION["LISTEN_MIXER_STOPPED"] = "listenMixerStopped";
            })(ACTION || (ACTION = {}));
            const SOCKET_ONLY_ACTIONS = [
                ACTION.GET_SERVER_CONFIGS,
                ACTION.CREATE_TRANSPORT,
                ACTION.CONNECT_TRANSPORT,
                ACTION.CLOSE_TRANSPORT,
                ACTION.PRODUCE,
                ACTION.CONSUME,
                ACTION.LISTEN_STREAM_STARTED,
                ACTION.LISTEN_STREAM_STOPPED,
                ACTION.LISTEN_MIXER_STOPPED,
                ACTION.CREATE_PIPE_TRANSPORT,
                ACTION.CONNECT_PIPE_TRANSPORT,
                ACTION.PIPE_TO_REMOTE_PRODUCER,
                ACTION.PIPE_FROM_REMOTE_PRODUCER
            ];
            const REST_ACTIONS = Object.values(ACTION).filter(a => !SOCKET_ONLY_ACTIONS.includes(a));
            var EVENT;
            (function (EVENT) {
                EVENT["STREAM_STARTED"] = "streamStarted";
                EVENT["STREAM_STOPPED"] = "streamStopped";
                EVENT["MIXER_STOPPED"] = "mixerStopped";
            })(EVENT || (EVENT = {}));
            var STAT;
            (function (STAT) {
                STAT["STATS"] = "stats";
                STAT["TRAFFIC"] = "traffic";
                STAT["CPU"] = "cpu";
            })(STAT || (STAT = {}));
            const HLS = {
                ROOT: 'hls',
                PLAYLIST: 'master.m3u8'
            };
            var PATH;
            (function (PATH) {
                PATH["API"] = "api";
            })(PATH || (PATH = {}));
            const NEXMO = {
                PATH: "nexmo",
                AUDIO_SAMPLE_RATE: 16000,
                AUDIO_CHANNELS: 1
            };
            var ERROR;
            (function (ERROR) {
                ERROR[ERROR["UNKNOWN"] = 500] = "UNKNOWN";
                ERROR[ERROR["UNAUTHORIZED"] = 401] = "UNAUTHORIZED";
                ERROR[ERROR["INVALID_TRANSPORT"] = 530] = "INVALID_TRANSPORT";
                ERROR[ERROR["INVALID_PRODUCER"] = 531] = "INVALID_PRODUCER";
                ERROR[ERROR["INVALID_CONSUMER"] = 532] = "INVALID_CONSUMER";
                ERROR[ERROR["INVALID_STREAM"] = 533] = "INVALID_STREAM";
                ERROR[ERROR["INVALID_OPERATION"] = 534] = "INVALID_OPERATION";
                ERROR[ERROR["INVALID_WORKER"] = 535] = "INVALID_WORKER";
                ERROR[ERROR["INVALID_INPUT"] = 536] = "INVALID_INPUT";
                ERROR[ERROR["INVALID_ACTION"] = 537] = "INVALID_ACTION";
            })(ERROR || (ERROR = {}));
            var API_OPERATION;
            (function (API_OPERATION) {
                API_OPERATION[API_OPERATION["SUBSCRIBE"] = 0] = "SUBSCRIBE";
                API_OPERATION[API_OPERATION["PUBLISH"] = 1] = "PUBLISH";
                API_OPERATION[API_OPERATION["RECORDING"] = 2] = "RECORDING";
                API_OPERATION[API_OPERATION["STREAMING"] = 3] = "STREAMING";
                API_OPERATION[API_OPERATION["MIXER"] = 4] = "MIXER";
            })(API_OPERATION || (API_OPERATION = {}));
            var MIXER_PIPE_TYPE;
            (function (MIXER_PIPE_TYPE) {
                MIXER_PIPE_TYPE[MIXER_PIPE_TYPE["LIVE"] = 0] = "LIVE";
                MIXER_PIPE_TYPE[MIXER_PIPE_TYPE["RECORDING"] = 1] = "RECORDING";
                MIXER_PIPE_TYPE[MIXER_PIPE_TYPE["RTMP"] = 2] = "RTMP";
                MIXER_PIPE_TYPE[MIXER_PIPE_TYPE["HLS"] = 3] = "HLS";
            })(MIXER_PIPE_TYPE || (MIXER_PIPE_TYPE = {}));
            var MIXER_RENDER_TYPE;
            (function (MIXER_RENDER_TYPE) {
                MIXER_RENDER_TYPE["SCALE"] = "scale";
                MIXER_RENDER_TYPE["CROP"] = "crop";
                MIXER_RENDER_TYPE["PAD"] = "pad";
            })(MIXER_RENDER_TYPE || (MIXER_RENDER_TYPE = {}));

// EXTERNAL MODULE: external {"root":"io","commonjs":"socket.io-client","commonjs2":"socket.io-client","amd":"socket.io-client"}
            var external_root_io_commonjs_socket_io_client_commonjs2_socket_io_client_amd_socket_io_client_ = __webpack_require__(173);
// EXTERNAL MODULE: external {"root":"axios","commonjs2":"axios","commonjs":"axios","amd":"axios"}
            var external_root_axios_commonjs2_axios_commonjs_axios_amd_axios_ = __webpack_require__(781);
            var external_root_axios_commonjs2_axios_commonjs_axios_amd_axios_default = /*#__PURE__*/__webpack_require__.n(external_root_axios_commonjs2_axios_commonjs_axios_amd_axios_);
            ;// CONCATENATED MODULE: ./src/mediasoup-socket-api.ts
            var __awaiter = (undefined && undefined.__awaiter) || function (thisArg, _arguments, P, generator) {
                function adopt(value) {
                    return value instanceof P ? value : new P(function (resolve) {
                        resolve(value);
                    });
                }

                return new (P || (P = Promise))(function (resolve, reject) {
                    function fulfilled(value) {
                        try {
                            step(generator.next(value));
                        } catch (e) {
                            reject(e);
                        }
                    }

                    function rejected(value) {
                        try {
                            step(generator["throw"](value));
                        } catch (e) {
                            reject(e);
                        }
                    }

                    function step(result) {
                        result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected);
                    }

                    step((generator = generator.apply(thisArg, _arguments || [])).next());
                });
            };


            class MediasoupSocketApi {
                constructor(url, worker, token, log) {
                    this.closed = false;
                    this.log = log || console.log;
                    this.url = url;
                    this.worker = worker;
                    this.token = token;
                }

                get client() {
                    if (!this._client) {
                        this._client = (0, external_root_io_commonjs_socket_io_client_commonjs2_socket_io_client_amd_socket_io_client_.io)(this.url, {
                            transports: ['websocket'],
                            query: {
                                mediasoup_worker: this.worker
                            },
                            auth: {
                                token: this.token
                            },
                            forceNew: true
                        });
                    }
                    return this._client;
                }

                connectSocket() {
                    return new Promise((resolve, reject) => {
                        if (this.client.connected) {
                            resolve();
                        } else {
                            this.client.on('error', (e) => {
                                if (e.message === 'Not enough or too many segments') {
                                    e.errorId = ERROR.UNAUTHORIZED;
                                } else {
                                    e.errorId = ERROR.UNKNOWN;
                                }
                                reject(e);
                            });
                            this.client.on('connect', resolve);
                        }
                    });
                }

                resumeConsumer(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.RESUME_CONSUMER, json);
                    });
                }

                pauseConsumer(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.PAUSE_CONSUMER, json);
                    });
                }

                setPreferredLayers(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.SET_PREFERRED_LAYERS, json);
                    });
                }

                closeConsumer(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.CLOSE_CONSUMER, json);
                    });
                }

                resumeProducer(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.RESUME_PRODUCER, json);
                    });
                }

                pauseProducer(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.PAUSE_PRODUCER, json);
                    });
                }

                closeProducer(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.CLOSE_PRODUCER, json);
                    });
                }

                produce(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return (yield this.request(ACTION.PRODUCE, json));
                    });
                }

                consume(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return (yield this.request(ACTION.CONSUME, json));
                    });
                }

                createPipeTransport() {
                    return __awaiter(this, void 0, void 0, function* () {
                        return (yield this.request(ACTION.CREATE_PIPE_TRANSPORT));
                    });
                }

                connectPipeTransport(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.CONNECT_PIPE_TRANSPORT, json);
                    });
                }

                closeTransport(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.CLOSE_TRANSPORT, json);
                    });
                }

                getServerConfigs() {
                    return __awaiter(this, void 0, void 0, function* () {
                        return (yield this.request(ACTION.GET_SERVER_CONFIGS));
                    });
                }

                createTransport() {
                    return __awaiter(this, void 0, void 0, function* () {
                        return (yield this.request(ACTION.CREATE_TRANSPORT));
                    });
                }

                connectTransport(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.CONNECT_TRANSPORT, json);
                    });
                }

                setMaxIncomingBitrate(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.SET_MAX_INCOMING_BITRATE, json);
                    });
                }

                producersStats(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return (yield this.request(ACTION.PRODUCERS_STATS, json));
                    });
                }

                consumersStats(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return (yield this.request(ACTION.CONSUMERS_STATS, json));
                    });
                }

                transportStats(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return (yield this.request(ACTION.TRANSPORT_STATS, json));
                    });
                }

                workerLoad() {
                    return __awaiter(this, void 0, void 0, function* () {
                        return (yield this.request(ACTION.WORKER_LOAD));
                    });
                }

                numWorkers() {
                    return __awaiter(this, void 0, void 0, function* () {
                        return (yield this.request(ACTION.NUM_WORKERS));
                    });
                }

                pipeToRemoteProducer(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.PIPE_TO_REMOTE_PRODUCER, json);
                    });
                }

                pipeFromRemoteProducer(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.PIPE_FROM_REMOTE_PRODUCER, json);
                    });
                }

                startRecording(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.START_RECORDING, json);
                    });
                }

                stopRecording(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.STOP_RECORDING, json);
                    });
                }

                fileStreaming(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.FILE_STREAMING, json);
                    });
                }

                stopFileStreaming(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.STOP_FILE_STREAMING, json);
                    });
                }

                recordedStreams() {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.RECORDED_STREAMS);
                    });
                }

                streamRecordings(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.STREAM_RECORDINGS, json);
                    });
                }

                deleteStreamRecordings(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.DELETE_STREAM_RECORDINGS, json);
                    });
                }

                deleteRecording(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.DELETE_RECORDING, json);
                    });
                }

                pushToServerInputs(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.PUSH_TO_SERVER_INPUTS, json);
                    });
                }

                pushToServerOptions(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.PUSH_TO_SERVER_OPTIONS, json);
                    });
                }

                pushToServer(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.PUSH_TO_SERVER, json);
                    });
                }

                pullFromServerInputs(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.PULL_FROM_SERVER_INPUTS, json);
                    });
                }

                kindsByFile(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.KINDS_BY_FILE, json);
                    });
                }

                requestKeyframe(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.REQUEST_KEYFRAME, json);
                    });
                }

                listenStreamStarted(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.LISTEN_STREAM_STARTED, json);
                    });
                }

                listenStreamStopped(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.LISTEN_STREAM_STOPPED, json);
                    });
                }

                liveStreaming(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.LIVE_STREAMING, json);
                    });
                }

                liveToHls(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.LIVE_TO_HLS, json);
                    });
                }

                mixerStart(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.MIXER_START, json);
                    });
                }

                mixerClose(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.MIXER_CLOSE, json);
                    });
                }

                mixerAdd(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.MIXER_ADD, json);
                    });
                }

                mixerUpdate(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.MIXER_UPDATE, json);
                    });
                }

                mixerRemove(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.MIXER_REMOVE, json);
                    });
                }

                mixerPipeStart(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.MIXER_PIPE_START, json);
                    });
                }

                mixerPipeStop(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.request(ACTION.MIXER_PIPE_STOP, json);
                    });
                }

                listenMixerStopped(json) {
                    return __awaiter(this, void 0, void 0, function* () {
                        return yield this.request(ACTION.LISTEN_MIXER_STOPPED, json);
                    });
                }

                clear() {
                    this.closed = true;
                    if (this._client) {
                        this._client.removeAllListeners();
                        if (this.client.connected) {
                            this._client.disconnect();
                        }
                    }
                }

                request(action, json = {}) {
                    return __awaiter(this, void 0, void 0, function* () {
                        if (!this.closed) {
                            if (!this._client && !SOCKET_ONLY_ACTIONS.includes(action)) {
                                return this.restRequest(action, json);
                            } else {
                                return this.socketRequest(action, json);
                            }
                        }
                    });
                }

                socketRequest(action, json = {}) {
                    return __awaiter(this, void 0, void 0, function* () {
                        yield this.connectSocket();
                        this.log('sent message', action, JSON.stringify(json));
                        return new Promise((resolve, reject) => {
                            this.client.emit(action, json, (data) => {
                                if (data && typeof data !== 'boolean' && data.hasOwnProperty('errorId')) {
                                    this.log('got error', action, JSON.stringify(data));
                                    reject(data);
                                } else {
                                    this.log('got message', action, JSON.stringify(data));
                                    resolve(data);
                                }
                            });
                        });
                    });
                }

                restRequest(action, json = {}) {
                    return __awaiter(this, void 0, void 0, function* () {
                        try {
                            const {data} = yield external_root_axios_commonjs2_axios_commonjs_axios_amd_axios_default().post(`${this.url}/${PATH.API}/${this.worker}/${action}`, json, {
                                headers: {'Content-Type': 'application/json', "Authorization": `Bearer ${this.token}`},
                            });
                            this.log('got message', action, JSON.stringify(data));
                            return data;
                        } catch (e) {
                            let errorId = ERROR.UNKNOWN;
                            this.log('got error', e);
                            if (e.response && e.response.status && ERROR[e.response.status]) {
                                errorId = e.response.status;
                            }
                            throw {errorId};
                        }
                    });
                }
            }

            ;// CONCATENATED MODULE: ./src/index.ts


        })();

        /******/
        return __webpack_exports__;
        /******/
    })()
        ;
});
