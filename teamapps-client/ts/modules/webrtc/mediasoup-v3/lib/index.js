(function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p=n[i]={exports:{}};e[i][0].call(p.exports,function(r){var n=e[i][1][r];return o(n||r)},p,p.exports,r,e,n,t)}return n[i].exports}for(var u="function"==typeof require&&require,i=0;i<t.length;i++)o(t[i]);return o}return r})()({1:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
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
    ACTION["STREAM_FILE"] = "streamFile";
    ACTION["START_RECORDING"] = "startRecording";
    ACTION["STOP_RECORDING"] = "stopRecording";
    ACTION["CREATE_PIPE_TRANSPORT"] = "createPipeTransport";
    ACTION["CONNECT_PIPE_TRANSPORT"] = "connectPipeTransport";
    ACTION["SET_PREFERRED_LAYERS"] = "setPreferredLayers";
    ACTION["SET_MAX_INCOMING_BITRATE"] = "setMaxIncomingBitrate";
    ACTION["PRODUCERS_STATS"] = "producersStats";
    ACTION["CONSUMERS_STATS"] = "consumersStats";
    ACTION["TRANSPORT_STATS"] = "transportStats";
})(ACTION = exports.ACTION || (exports.ACTION = {}));
var PATH;
(function (PATH) {
    PATH["RECORDINGS"] = "recordings";
    PATH["MEDIASOUP"] = "mediasoup";
    PATH["FRONT"] = "front";
    PATH["API_DOCS"] = "api-docs";
})(PATH = exports.PATH || (exports.PATH = {}));
var ERROR;
(function (ERROR) {
    ERROR[ERROR["UNKNOWN"] = 500] = "UNKNOWN";
    ERROR[ERROR["UNAUTHORIZED"] = 401] = "UNAUTHORIZED";
    ERROR[ERROR["INVALID_TRANSPORT"] = 530] = "INVALID_TRANSPORT";
    ERROR[ERROR["INVALID_PRODUCER"] = 531] = "INVALID_PRODUCER";
    ERROR[ERROR["INVALID_CONSUMER"] = 532] = "INVALID_CONSUMER";
    ERROR[ERROR["INVALID_STREAM"] = 533] = "INVALID_STREAM";
    ERROR[ERROR["INVALID_OPERATION"] = 534] = "INVALID_OPERATION";
})(ERROR = exports.ERROR || (exports.ERROR = {}));
var API_OPERATION;
(function (API_OPERATION) {
    API_OPERATION[API_OPERATION["SUBSCRIBE"] = 0] = "SUBSCRIBE";
    API_OPERATION[API_OPERATION["PUBLISH"] = 1] = "PUBLISH";
})(API_OPERATION = exports.API_OPERATION || (exports.API_OPERATION = {}));

},{}],2:[function(require,module,exports){
"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
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
var constants_1 = require("../../config/constants");
var events_1 = require("events");
var mediasoup_rest_api_1 = require("./mediasoup-rest-api");
var mediasoup_client_1 = require("mediasoup-client");
var ConferenceApi = /** @class */ (function (_super) {
    __extends(ConferenceApi, _super);
    function ConferenceApi(configs) {
        var _this = _super.call(this) || this;
        _this.connectors = new Map();
        _this.layers = new Map();
        _this.timeouts = [];
        _this.configs = __assign({ url: location.protocol + "//" + location.host, kinds: ['video', 'audio'], maxIncomingBitrate: 0, timeout: {
                "stats": 1000,
                "stream": 30000
            } }, configs);
        _this.api = new mediasoup_rest_api_1.MediasoupRestApi(_this.configs.url, _this.configs.token);
        _this.device = new mediasoup_client_1.Device();
        return _this;
    }
    ConferenceApi.prototype.startRecording = function () {
        return __awaiter(this, void 0, void 0, function () {
            var _a, stream, kinds;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        _a = this.configs, stream = _a.stream, kinds = _a.kinds;
                        return [4 /*yield*/, this.api.startRecording({ wait: true, stream: stream, kinds: kinds })];
                    case 1:
                        _b.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.stopRecording = function () {
        return __awaiter(this, void 0, void 0, function () {
            var stream;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        stream = this.configs.stream;
                        return [4 /*yield*/, this.api.stopRecording({ wait: true, stream: stream })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.setPreferredLayers = function (layers) {
        return __awaiter(this, void 0, void 0, function () {
            var kind, consumer;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!(this.operation === constants_1.API_OPERATION.SUBSCRIBE)) return [3 /*break*/, 2];
                        kind = 'video';
                        this.layers.set(kind, layers);
                        consumer = this.connectors.get(kind);
                        if (!consumer) return [3 /*break*/, 2];
                        return [4 /*yield*/, this.api.setPreferredLayers({ consumerId: consumer.id, layers: layers })];
                    case 1:
                        _a.sent();
                        _a.label = 2;
                    case 2: return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.addTrack = function (track) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!(this.operation === constants_1.API_OPERATION.PUBLISH && this.mediaStream)) return [3 /*break*/, 2];
                        this.mediaStream.addTrack(track);
                        return [4 /*yield*/, this.publishTrack(track)];
                    case 1:
                        _a.sent();
                        _a.label = 2;
                    case 2: return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.removeTrack = function (track) {
        return __awaiter(this, void 0, void 0, function () {
            var consumer;
            return __generator(this, function (_a) {
                if (this.operation === constants_1.API_OPERATION.PUBLISH && this.mediaStream) {
                    this.mediaStream.removeTrack(track);
                    consumer = this.connectors.get(track.kind);
                    if (consumer) {
                        consumer.close();
                        consumer.emit('close');
                    }
                }
                return [2 /*return*/];
            });
        });
    };
    ConferenceApi.prototype.setMaxPublisherBitrate = function (bitrate) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        this.configs.maxIncomingBitrate = bitrate;
                        if (!this.transport) return [3 /*break*/, 2];
                        return [4 /*yield*/, this.api.setMaxIncomingBitrate({ transportId: this.transport.id, bitrate: bitrate })];
                    case 1:
                        _a.sent();
                        _a.label = 2;
                    case 2: return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.updateKinds = function (kinds) {
        return __awaiter(this, void 0, void 0, function () {
            var oldKinds, _i, oldKinds_1, kind, connector, _a, kinds_1, kind;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        if (!(this.operation === constants_1.API_OPERATION.SUBSCRIBE)) return [3 /*break*/, 4];
                        oldKinds = this.configs.kinds;
                        this.configs.kinds = kinds;
                        for (_i = 0, oldKinds_1 = oldKinds; _i < oldKinds_1.length; _i++) {
                            kind = oldKinds_1[_i];
                            if (!kinds.includes(kind)) {
                                connector = this.connectors.get(kind);
                                if (connector) {
                                    connector.close();
                                    connector.emit('close');
                                }
                            }
                        }
                        _a = 0, kinds_1 = kinds;
                        _b.label = 1;
                    case 1:
                        if (!(_a < kinds_1.length)) return [3 /*break*/, 4];
                        kind = kinds_1[_a];
                        if (!!this.connectors.get(kind)) return [3 /*break*/, 3];
                        return [4 /*yield*/, this.subscribeTrack(kind)];
                    case 2:
                        _b.sent();
                        _b.label = 3;
                    case 3:
                        _a++;
                        return [3 /*break*/, 1];
                    case 4: return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.init = function (operation) {
        return __awaiter(this, void 0, void 0, function () {
            var _a, routerRtpCapabilities, iceServers, simulcast;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        if (this.operation) {
                            throw new Error("Already processing");
                        }
                        this.operation = operation;
                        if (!!this.device.loaded) return [3 /*break*/, 3];
                        return [4 /*yield*/, this.api.getServerConfigs()];
                    case 1:
                        _a = _b.sent(), routerRtpCapabilities = _a.routerRtpCapabilities, iceServers = _a.iceServers, simulcast = _a.simulcast;
                        if (routerRtpCapabilities.headerExtensions) {
                            routerRtpCapabilities.headerExtensions = routerRtpCapabilities.headerExtensions.
                                filter(function (ext) { return ext.uri !== 'urn:3gpp:video-orientation'; });
                        }
                        return [4 /*yield*/, this.device.load({ routerRtpCapabilities: routerRtpCapabilities })];
                    case 2:
                        _b.sent();
                        this.iceServers = iceServers;
                        this.simulcast = simulcast;
                        _b.label = 3;
                    case 3: return [4 /*yield*/, this.getTransport()];
                    case 4:
                        _b.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.publish = function (mediaStream) {
        return __awaiter(this, void 0, void 0, function () {
            var _this = this;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.init(constants_1.API_OPERATION.PUBLISH)];
                    case 1:
                        _a.sent();
                        this.mediaStream = mediaStream;
                        return [4 /*yield*/, Promise.all(mediaStream.getTracks().map(function (track) { return _this.publishTrack(track); }))];
                    case 2:
                        _a.sent();
                        return [2 /*return*/, mediaStream];
                }
            });
        });
    };
    ConferenceApi.prototype.subscribe = function () {
        return __awaiter(this, void 0, void 0, function () {
            var mediaStream;
            var _this = this;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.init(constants_1.API_OPERATION.SUBSCRIBE)];
                    case 1:
                        _a.sent();
                        mediaStream = this.mediaStream || new MediaStream();
                        this.mediaStream = mediaStream;
                        this.configs.kinds.map(function (kind) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0: return [4 /*yield*/, this.subscribeTrack(kind)];
                                    case 1:
                                        _a.sent();
                                        return [2 /*return*/];
                                }
                            });
                        }); });
                        return [2 /*return*/, mediaStream];
                }
            });
        });
    };
    ConferenceApi.prototype.subscribeTrack = function (kind) {
        return __awaiter(this, void 0, void 0, function () {
            var api, consumer, onClose;
            var _this = this;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        api = this;
                        return [4 /*yield*/, this.consume(this.transport, this.configs.stream, kind)];
                    case 1:
                        consumer = _a.sent();
                        this.connectors.set(kind, consumer);
                        this.emit('newConsumerId', { id: consumer.id, kind: kind });
                        onClose = function () { return __awaiter(_this, void 0, void 0, function () {
                            var _consumer, e_1;
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (this.mediaStream) {
                                            consumer.track.stop();
                                            this.mediaStream.removeTrack(consumer.track);
                                            this.mediaStream.dispatchEvent(new MediaStreamTrackEvent("removetrack", { track: consumer.track }));
                                        }
                                        if (!(this.transport && !this.transport.closed)) return [3 /*break*/, 6];
                                        _consumer = this.connectors.get(kind);
                                        _a.label = 1;
                                    case 1:
                                        _a.trys.push([1, 3, , 4]);
                                        return [4 /*yield*/, this.api.closeConsumer({ consumerId: consumer.id })];
                                    case 2:
                                        _a.sent();
                                        return [3 /*break*/, 4];
                                    case 3:
                                        e_1 = _a.sent();
                                        return [3 /*break*/, 4];
                                    case 4:
                                        if (!(_consumer && consumer.id === _consumer.id)) return [3 /*break*/, 6];
                                        this.connectors.delete(consumer.track.kind);
                                        if (!this.mediaStream) return [3 /*break*/, 6];
                                        if (!(this.transport && this.configs.kinds.includes(kind))) return [3 /*break*/, 6];
                                        return [4 /*yield*/, this.subscribeTrack(kind)];
                                    case 5:
                                        _a.sent();
                                        _a.label = 6;
                                    case 6: return [2 /*return*/];
                                }
                            });
                        }); };
                        consumer.on('close', onClose);
                        this.listenStats(consumer, 'inbound-rtp');
                        return [4 /*yield*/, api.api.resumeConsumer({ consumerId: consumer.id })];
                    case 2:
                        _a.sent();
                        if (this.mediaStream) {
                            this.mediaStream.addTrack(consumer.track);
                            this.mediaStream.dispatchEvent(new MediaStreamTrackEvent("addtrack", { track: consumer.track }));
                        }
                        return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.publishTrack = function (track) {
        return __awaiter(this, void 0, void 0, function () {
            var kind, params, producer;
            var _this = this;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        kind = track.kind;
                        if (!this.configs.kinds.includes(kind)) return [3 /*break*/, 2];
                        track.addEventListener('ended', function () { return __awaiter(_this, void 0, void 0, function () {
                            var producer, e_2;
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        producer = this.connectors.get(kind);
                                        if (!producer) return [3 /*break*/, 4];
                                        this.connectors.delete(kind);
                                        _a.label = 1;
                                    case 1:
                                        _a.trys.push([1, 3, , 4]);
                                        return [4 /*yield*/, this.api.closeProducer({ producerId: producer.id })];
                                    case 2:
                                        _a.sent();
                                        return [3 /*break*/, 4];
                                    case 3:
                                        e_2 = _a.sent();
                                        return [3 /*break*/, 4];
                                    case 4: return [2 /*return*/];
                                }
                            });
                        }); });
                        params = { track: track, stopTracks: false };
                        if (this.configs.simulcast && kind === 'video' && this.simulcast) {
                            if (this.simulcast.encodings) {
                                params.encodings = this.simulcast.encodings;
                            }
                            if (this.simulcast.codecOptions) {
                                params.codecOptions = this.simulcast.codecOptions;
                            }
                        }
                        return [4 /*yield*/, this.transport.produce(params)];
                    case 1:
                        producer = _a.sent();
                        this.listenStats(producer, 'outbound-rtp');
                        this.connectors.set(kind, producer);
                        this.emit('newProducerId', { id: producer.id, kind: kind });
                        _a.label = 2;
                    case 2: return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.consume = function (transport, stream, _kind) {
        return __awaiter(this, void 0, void 0, function () {
            var rtpCapabilities, data, layers, e_3;
            var _this = this;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        rtpCapabilities = this.device.rtpCapabilities;
                        _a.label = 1;
                    case 1:
                        _a.trys.push([1, 5, , 9]);
                        return [4 /*yield*/, this.api.consume({ rtpCapabilities: rtpCapabilities, stream: stream, kind: _kind, transportId: transport.id })];
                    case 2:
                        data = _a.sent();
                        layers = this.layers.get(_kind);
                        if (!layers) return [3 /*break*/, 4];
                        return [4 /*yield*/, this.api.setPreferredLayers({ consumerId: data.id, layers: layers })];
                    case 3:
                        _a.sent();
                        _a.label = 4;
                    case 4: return [2 /*return*/, transport.consume(data)];
                    case 5:
                        e_3 = _a.sent();
                        if (!(e_3.response && e_3.response.status && e_3.response.status === constants_1.ERROR.INVALID_STREAM)) return [3 /*break*/, 7];
                        return [4 /*yield*/, new Promise(function (resolve) { return _this.timeouts.push(setTimeout(resolve, 1000)); })];
                    case 6:
                        _a.sent();
                        return [2 /*return*/, this.consume(transport, stream, _kind)];
                    case 7: throw e_3;
                    case 8: return [3 /*break*/, 9];
                    case 9: return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.listenStats = function (target, type) {
        var _this = this;
        var lastBytes = 0;
        var lastBytesTime = Date.now();
        var bytesField = type === 'inbound-rtp' ? 'bytesReceived' : 'bytesSent';
        var deadTime = 0;
        target.on('close', function () {
            _this.emit('bitRate', { bitRate: 0, kind: target.kind });
        });
        var getStats = function () {
            if (target && !target.closed) {
                target.getStats().then(function (stats) { return __awaiter(_this, void 0, void 0, function () {
                    var alive_1, i_1, checkTarget_1;
                    var _this = this;
                    return __generator(this, function (_a) {
                        if (target && !target.closed) {
                            alive_1 = false;
                            i_1 = 0;
                            checkTarget_1 = function () {
                                if (i_1 === stats.size) {
                                    if (alive_1) {
                                        deadTime = 0;
                                    }
                                    else {
                                        _this.emit('bitRate', { bitRate: 0, kind: target.kind });
                                        if (type === 'inbound-rtp') {
                                            deadTime++;
                                            if (deadTime > 5) {
                                                try {
                                                    if (lastBytes) {
                                                        target.close();
                                                        target.emit('close');
                                                    }
                                                    else {
                                                        _this.restartAll().then(function () { }).catch(function () { });
                                                    }
                                                }
                                                catch (e) {
                                                }
                                                return;
                                            }
                                        }
                                    }
                                    setTimeout(getStats, _this.configs.timeout.stats);
                                }
                            };
                            if (stats.size) {
                                stats.forEach(function (s) {
                                    if (s && s.type === type) {
                                        if (s[bytesField] && s[bytesField] > lastBytes) {
                                            var bitRate = Math.round((s[bytesField] - lastBytes) / (Date.now() - lastBytesTime) * 1000 * 8);
                                            _this.emit('bitRate', { bitRate: bitRate, kind: target.kind });
                                            lastBytes = s[bytesField];
                                            lastBytesTime = Date.now();
                                            alive_1 = true;
                                        }
                                    }
                                    i_1++;
                                    checkTarget_1();
                                });
                            }
                            else {
                                checkTarget_1();
                            }
                        }
                        return [2 /*return*/];
                    });
                }); });
            }
        };
        getStats();
    };
    ConferenceApi.prototype.close = function (hard) {
        if (hard === void 0) { hard = true; }
        return __awaiter(this, void 0, void 0, function () {
            var e_4, t;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!this.transport) return [3 /*break*/, 5];
                        if (!this.transport.closed && hard) {
                            this.transport.close();
                        }
                        _a.label = 1;
                    case 1:
                        _a.trys.push([1, 3, , 4]);
                        return [4 /*yield*/, this.api.closeTransport({ transportId: this.transport.id })];
                    case 2:
                        _a.sent();
                        return [3 /*break*/, 4];
                    case 3:
                        e_4 = _a.sent();
                        return [3 /*break*/, 4];
                    case 4:
                        delete this.transport;
                        this.emit('connectionstatechange', { state: 'disconnected' });
                        _a.label = 5;
                    case 5:
                        if (hard && this.mediaStream) {
                            this.mediaStream.getTracks().forEach(function (track) {
                                track.stop();
                            });
                        }
                        return [4 /*yield*/, this.closeConnectors()];
                    case 6:
                        _a.sent();
                        delete this.operation;
                        while (this.timeouts.length) {
                            t = this.timeouts.shift();
                            if (t) {
                                clearTimeout(t);
                            }
                        }
                        this.api.clear();
                        return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.closeConnectors = function () {
        return __awaiter(this, void 0, void 0, function () {
            var _this = this;
            return __generator(this, function (_a) {
                if (this.connectors.size) {
                    return [2 /*return*/, new Promise(function (resolve) {
                            _this.connectors.forEach(function (connector, kind) {
                                _this.connectors.delete(kind);
                                try {
                                    connector.close();
                                    connector.emit('close');
                                }
                                catch (e) { }
                                if (!_this.connectors.size) {
                                    resolve();
                                }
                            });
                        })];
                }
                return [2 /*return*/];
            });
        });
    };
    ConferenceApi.prototype.restartAll = function () {
        return __awaiter(this, void 0, void 0, function () {
            var operation;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        operation = this.operation;
                        return [4 /*yield*/, this.close(operation === constants_1.API_OPERATION.SUBSCRIBE)];
                    case 1:
                        _a.sent();
                        if (!(operation === constants_1.API_OPERATION.SUBSCRIBE)) return [3 /*break*/, 3];
                        return [4 /*yield*/, this.subscribe()];
                    case 2:
                        _a.sent();
                        return [3 /*break*/, 5];
                    case 3:
                        if (!(operation === constants_1.API_OPERATION.PUBLISH && this.mediaStream)) return [3 /*break*/, 5];
                        return [4 /*yield*/, this.publish(this.mediaStream)];
                    case 4:
                        _a.sent();
                        _a.label = 5;
                    case 5: return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.getTransport = function () {
        return __awaiter(this, void 0, void 0, function () {
            var api_1, data;
            var _this = this;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!!this.transport) return [3 /*break*/, 4];
                        api_1 = this;
                        return [4 /*yield*/, this.api.createTransport()];
                    case 1:
                        data = _a.sent();
                        if (this.iceServers) {
                            data.iceServers = this.iceServers;
                        }
                        if (this.operation === constants_1.API_OPERATION.SUBSCRIBE) {
                            this.transport = this.device.createRecvTransport(data);
                        }
                        else if (this.operation === constants_1.API_OPERATION.PUBLISH) {
                            this.transport = this.device.createSendTransport(data);
                        }
                        this.emit('newTransportId', { id: this.transport.id });
                        if (!this.configs.maxIncomingBitrate) return [3 /*break*/, 3];
                        return [4 /*yield*/, this.api.setMaxIncomingBitrate({ transportId: this.transport.id, bitrate: this.configs.maxIncomingBitrate })];
                    case 2:
                        _a.sent();
                        _a.label = 3;
                    case 3:
                        this.transport.on('connect', function (_a, callback, errback) {
                            var dtlsParameters = _a.dtlsParameters;
                            api_1.api.connectTransport({
                                transportId: _this.transport.id,
                                dtlsParameters: dtlsParameters
                            }).then(callback).catch(errback);
                        });
                        if (this.operation === constants_1.API_OPERATION.PUBLISH) {
                            this.transport.on('produce', function (_a, callback, errback) {
                                var kind = _a.kind, rtpParameters = _a.rtpParameters;
                                return __awaiter(_this, void 0, void 0, function () {
                                    var data_1, err_1;
                                    return __generator(this, function (_b) {
                                        switch (_b.label) {
                                            case 0:
                                                _b.trys.push([0, 2, , 3]);
                                                return [4 /*yield*/, api_1.api.produce({
                                                        transportId: this.transport.id,
                                                        stream: api_1.configs.stream,
                                                        kind: kind,
                                                        rtpParameters: rtpParameters
                                                    })];
                                            case 1:
                                                data_1 = _b.sent();
                                                callback(data_1);
                                                return [3 /*break*/, 3];
                                            case 2:
                                                err_1 = _b.sent();
                                                errback(err_1);
                                                return [3 /*break*/, 3];
                                            case 3: return [2 /*return*/];
                                        }
                                    });
                                });
                            });
                        }
                        this.transport.on('connectionstatechange', function (state) { return __awaiter(_this, void 0, void 0, function () {
                            var _this = this;
                            return __generator(this, function (_a) {
                                this.emit('connectionstatechange', { state: state });
                                switch (state) {
                                    case 'connected':
                                        if (this.transportTimeout) {
                                            clearTimeout(this.transportTimeout);
                                        }
                                        break;
                                    case 'failed':
                                    case 'disconnected':
                                        if (this.transportTimeout) {
                                            clearTimeout(this.transportTimeout);
                                        }
                                        this.transportTimeout = setTimeout(function () { return __awaiter(_this, void 0, void 0, function () {
                                            return __generator(this, function (_a) {
                                                switch (_a.label) {
                                                    case 0: return [4 /*yield*/, this.restartAll()];
                                                    case 1:
                                                        _a.sent();
                                                        return [2 /*return*/];
                                                }
                                            });
                                        }); }, this.configs.timeout.stream);
                                        break;
                                }
                                return [2 /*return*/];
                            });
                        }); });
                        _a.label = 4;
                    case 4: return [2 /*return*/, this.transport];
                }
            });
        });
    };
    return ConferenceApi;
}(events_1.EventEmitter));
exports.ConferenceApi = ConferenceApi;

},{"../../config/constants":1,"./mediasoup-rest-api":4,"events":37,"mediasoup-client":64}],3:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
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
var utils_1 = require("./utils");
var conference_api_1 = require("./conference-api");
var constants_1 = require("../../config/constants");
window.Utils = utils_1.Utils;
window.ConferenceApi = conference_api_1.ConferenceApi;
window.ERROR = constants_1.ERROR;

},{"../../config/constants":1,"./conference-api":2,"./utils":5}],4:[function(require,module,exports){
"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
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
var constants_1 = require("../../config/constants");
var axios_1 = __importDefault(require("axios"));
var MediasoupRestApi = /** @class */ (function () {
    function MediasoupRestApi(url, token) {
        this.timeouts = [];
        this.url = url;
        this.token = token;
    }
    MediasoupRestApi.prototype.resumeConsumer = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.RESUME_CONSUMER, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.pauseConsumer = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.PAUSE_CONSUMER, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.setPreferredLayers = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.SET_PREFERRED_LAYERS, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.closeConsumer = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.CLOSE_CONSUMER, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.resumeProducer = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.RESUME_PRODUCER, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.pauseProducer = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.PAUSE_PRODUCER, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.closeProducer = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.CLOSE_PRODUCER, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.produce = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.PRODUCE, json)];
                    case 1: return [2 /*return*/, (_a.sent())];
                }
            });
        });
    };
    MediasoupRestApi.prototype.consume = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.CONSUME, json)];
                    case 1: return [2 /*return*/, (_a.sent())];
                }
            });
        });
    };
    MediasoupRestApi.prototype.createPipeTransport = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.CREATE_PIPE_TRANSPORT)];
                    case 1: return [2 /*return*/, (_a.sent())];
                }
            });
        });
    };
    MediasoupRestApi.prototype.connectPipeTransport = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.CONNECT_PIPE_TRANSPORT, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.closeTransport = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.CLOSE_TRANSPORT, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.getServerConfigs = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.GET_SERVER_CONFIGS)];
                    case 1: return [2 /*return*/, (_a.sent())];
                }
            });
        });
    };
    MediasoupRestApi.prototype.createTransport = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.CREATE_TRANSPORT)];
                    case 1: return [2 /*return*/, (_a.sent())];
                }
            });
        });
    };
    MediasoupRestApi.prototype.connectTransport = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.CONNECT_TRANSPORT, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.setMaxIncomingBitrate = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.SET_MAX_INCOMING_BITRATE, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.producersStats = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.PRODUCERS_STATS, json)];
                    case 1: return [2 /*return*/, (_a.sent())];
                }
            });
        });
    };
    MediasoupRestApi.prototype.consumersStats = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.CONSUMERS_STATS, json)];
                    case 1: return [2 /*return*/, (_a.sent())];
                }
            });
        });
    };
    MediasoupRestApi.prototype.transportStats = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.TRANSPORT_STATS, json)];
                    case 1: return [2 /*return*/, (_a.sent())];
                }
            });
        });
    };
    MediasoupRestApi.prototype.startRecording = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.START_RECORDING, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.stopRecording = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.STOP_RECORDING, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.streamFile = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.STREAM_FILE, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.clear = function () {
        while (this.timeouts.length) {
            var t = this.timeouts.shift();
            if (t) {
                clearTimeout(t);
            }
        }
    };
    MediasoupRestApi.prototype.request = function (action, json) {
        if (json === void 0) { json = {}; }
        return __awaiter(this, void 0, void 0, function () {
            var data, e_1, timeout_1;
            var _this = this;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        console.log('sent message', action, JSON.stringify(json));
                        _a.label = 1;
                    case 1:
                        _a.trys.push([1, 3, , 8]);
                        return [4 /*yield*/, axios_1.default.post(this.url + "/" + constants_1.PATH.MEDIASOUP + "/" + action, json, {
                                headers: { 'Content-Type': 'application/json', "Authorization": "Bearer " + this.token },
                            })];
                    case 2:
                        data = (_a.sent()).data;
                        console.log('got message', action, JSON.stringify(data));
                        return [2 /*return*/, data];
                    case 3:
                        e_1 = _a.sent();
                        if (!(!e_1.response.status && !constants_1.ERROR[e_1.response.status])) return [3 /*break*/, 6];
                        return [4 /*yield*/, new Promise(function (resolve) {
                                timeout_1 = setTimeout(resolve, 1000);
                                _this.timeouts.push(timeout_1);
                            })];
                    case 4:
                        _a.sent();
                        if (!this.timeouts.includes(timeout_1)) {
                            throw e_1;
                        }
                        return [4 /*yield*/, this.request(action, json)];
                    case 5: return [2 /*return*/, _a.sent()];
                    case 6: throw e_1;
                    case 7: return [3 /*break*/, 8];
                    case 8: return [2 /*return*/];
                }
            });
        });
    };
    return MediasoupRestApi;
}());
exports.MediasoupRestApi = MediasoupRestApi;

},{"../../config/constants":1,"axios":7}],5:[function(require,module,exports){
"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
var Utils = /** @class */ (function () {
    function Utils() {
    }
    Utils.getUserMedia = function (constraints, isDisplay) {
        if (isDisplay === void 0) { isDisplay = false; }
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!isDisplay) return [3 /*break*/, 2];
                        return [4 /*yield*/, navigator.mediaDevices.getDisplayMedia(constraints)];
                    case 1: return [2 /*return*/, _a.sent()];
                    case 2: return [4 /*yield*/, navigator.mediaDevices.getUserMedia(constraints)];
                    case 3: return [2 /*return*/, _a.sent()];
                }
            });
        });
    };
    Utils.isFirefox = typeof window.InstallTrigger !== 'undefined';
    Utils.isOpera = !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0;
    Utils.isChrome = !!window.chrome && !Utils.isOpera;
    Utils.isSafari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent);
    return Utils;
}());
exports.Utils = Utils;

},{}],6:[function(require,module,exports){
"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
class AwaitQueue {
    constructor({ ClosedErrorClass, StoppedErrorClass } = {
        ClosedErrorClass: Error,
        StoppedErrorClass: Error
    }) {
        // Closed flag.
        this._closed = false;
        // Queue of pending tasks.
        this._pendingTasks = [];
        // Error class used when rejecting a task due to AwaitQueue being closed.
        this._ClosedErrorClass = Error;
        // Error class used when rejecting a task due to AwaitQueue being stopped.
        this._StoppedErrorClass = Error;
        this._ClosedErrorClass = ClosedErrorClass;
        this._StoppedErrorClass = StoppedErrorClass;
    }
    /**
     * Closes the AwaitQueue. Pending tasks will be rejected with ClosedErrorClass
     * error.
     */
    close() {
        this._closed = true;
    }
    /**
     * Accepts a task as argument and enqueues it after pending tasks. Once
     * processed, the push() method resolves (or rejects) with the result
     * returned by the given task.
     *
     * The given task must return a Promise or directly a value.
     */
    push(task) {
        return __awaiter(this, void 0, void 0, function* () {
            if (typeof task !== 'function')
                throw new TypeError('given task is not a function');
            return new Promise((resolve, reject) => {
                const pendingTask = {
                    execute: task,
                    resolve,
                    reject,
                    stopped: false
                };
                // Append task to the queue.
                this._pendingTasks.push(pendingTask);
                // And run it if this is the only task in the queue.
                if (this._pendingTasks.length === 1)
                    this._next();
            });
        });
    }
    stop() {
        for (const pendingTask of this._pendingTasks) {
            pendingTask.stopped = true;
            pendingTask.reject(new this._StoppedErrorClass('AwaitQueue stopped'));
        }
        // Enpty the pending tasks array.
        this._pendingTasks.length = 0;
    }
    _next() {
        return __awaiter(this, void 0, void 0, function* () {
            // Take the first pending task.
            const pendingTask = this._pendingTasks[0];
            if (!pendingTask)
                return;
            // Execute it.
            yield this._executeTask(pendingTask);
            // Remove the first pending task (the completed one) from the queue.
            this._pendingTasks.shift();
            // And continue.
            this._next();
        });
    }
    _executeTask(pendingTask) {
        return __awaiter(this, void 0, void 0, function* () {
            if (this._closed) {
                pendingTask.reject(new this._ClosedErrorClass('AwaitQueue closed'));
                return;
            }
            // If stop() was called for this task, ignore it.
            if (pendingTask.stopped)
                return;
            try {
                const result = yield pendingTask.execute();
                if (this._closed) {
                    pendingTask.reject(new this._ClosedErrorClass('AwaitQueue closed'));
                    return;
                }
                // If stop() was called for this task, ignore it.
                if (pendingTask.stopped)
                    return;
                // Resolve the task with the returned result (if any).
                pendingTask.resolve(result);
            }
            catch (error) {
                if (this._closed) {
                    pendingTask.reject(new this._ClosedErrorClass('AwaitQueue closed'));
                    return;
                }
                // If stop() was called for this task, ignore it.
                if (pendingTask.stopped)
                    return;
                // Reject the task with its own error.
                pendingTask.reject(error);
            }
        });
    }
}
exports.AwaitQueue = AwaitQueue;

},{}],7:[function(require,module,exports){
module.exports = require('./lib/axios');
},{"./lib/axios":9}],8:[function(require,module,exports){
'use strict';

var utils = require('./../utils');
var settle = require('./../core/settle');
var buildURL = require('./../helpers/buildURL');
var buildFullPath = require('../core/buildFullPath');
var parseHeaders = require('./../helpers/parseHeaders');
var isURLSameOrigin = require('./../helpers/isURLSameOrigin');
var createError = require('../core/createError');

module.exports = function xhrAdapter(config) {
  return new Promise(function dispatchXhrRequest(resolve, reject) {
    var requestData = config.data;
    var requestHeaders = config.headers;

    if (utils.isFormData(requestData)) {
      delete requestHeaders['Content-Type']; // Let the browser set it
    }

    var request = new XMLHttpRequest();

    // HTTP basic authentication
    if (config.auth) {
      var username = config.auth.username || '';
      var password = config.auth.password || '';
      requestHeaders.Authorization = 'Basic ' + btoa(username + ':' + password);
    }

    var fullPath = buildFullPath(config.baseURL, config.url);
    request.open(config.method.toUpperCase(), buildURL(fullPath, config.params, config.paramsSerializer), true);

    // Set the request timeout in MS
    request.timeout = config.timeout;

    // Listen for ready state
    request.onreadystatechange = function handleLoad() {
      if (!request || request.readyState !== 4) {
        return;
      }

      // The request errored out and we didn't get a response, this will be
      // handled by onerror instead
      // With one exception: request that using file: protocol, most browsers
      // will return status as 0 even though it's a successful request
      if (request.status === 0 && !(request.responseURL && request.responseURL.indexOf('file:') === 0)) {
        return;
      }

      // Prepare the response
      var responseHeaders = 'getAllResponseHeaders' in request ? parseHeaders(request.getAllResponseHeaders()) : null;
      var responseData = !config.responseType || config.responseType === 'text' ? request.responseText : request.response;
      var response = {
        data: responseData,
        status: request.status,
        statusText: request.statusText,
        headers: responseHeaders,
        config: config,
        request: request
      };

      settle(resolve, reject, response);

      // Clean up request
      request = null;
    };

    // Handle browser request cancellation (as opposed to a manual cancellation)
    request.onabort = function handleAbort() {
      if (!request) {
        return;
      }

      reject(createError('Request aborted', config, 'ECONNABORTED', request));

      // Clean up request
      request = null;
    };

    // Handle low level network errors
    request.onerror = function handleError() {
      // Real errors are hidden from us by the browser
      // onerror should only fire if it's a network error
      reject(createError('Network Error', config, null, request));

      // Clean up request
      request = null;
    };

    // Handle timeout
    request.ontimeout = function handleTimeout() {
      var timeoutErrorMessage = 'timeout of ' + config.timeout + 'ms exceeded';
      if (config.timeoutErrorMessage) {
        timeoutErrorMessage = config.timeoutErrorMessage;
      }
      reject(createError(timeoutErrorMessage, config, 'ECONNABORTED',
        request));

      // Clean up request
      request = null;
    };

    // Add xsrf header
    // This is only done if running in a standard browser environment.
    // Specifically not if we're in a web worker, or react-native.
    if (utils.isStandardBrowserEnv()) {
      var cookies = require('./../helpers/cookies');

      // Add xsrf header
      var xsrfValue = (config.withCredentials || isURLSameOrigin(fullPath)) && config.xsrfCookieName ?
        cookies.read(config.xsrfCookieName) :
        undefined;

      if (xsrfValue) {
        requestHeaders[config.xsrfHeaderName] = xsrfValue;
      }
    }

    // Add headers to the request
    if ('setRequestHeader' in request) {
      utils.forEach(requestHeaders, function setRequestHeader(val, key) {
        if (typeof requestData === 'undefined' && key.toLowerCase() === 'content-type') {
          // Remove Content-Type if data is undefined
          delete requestHeaders[key];
        } else {
          // Otherwise add header to the request
          request.setRequestHeader(key, val);
        }
      });
    }

    // Add withCredentials to request if needed
    if (!utils.isUndefined(config.withCredentials)) {
      request.withCredentials = !!config.withCredentials;
    }

    // Add responseType to request if needed
    if (config.responseType) {
      try {
        request.responseType = config.responseType;
      } catch (e) {
        // Expected DOMException thrown by browsers not compatible XMLHttpRequest Level 2.
        // But, this can be suppressed for 'json' type as it can be parsed by default 'transformResponse' function.
        if (config.responseType !== 'json') {
          throw e;
        }
      }
    }

    // Handle progress if needed
    if (typeof config.onDownloadProgress === 'function') {
      request.addEventListener('progress', config.onDownloadProgress);
    }

    // Not all browsers support upload events
    if (typeof config.onUploadProgress === 'function' && request.upload) {
      request.upload.addEventListener('progress', config.onUploadProgress);
    }

    if (config.cancelToken) {
      // Handle cancellation
      config.cancelToken.promise.then(function onCanceled(cancel) {
        if (!request) {
          return;
        }

        request.abort();
        reject(cancel);
        // Clean up request
        request = null;
      });
    }

    if (requestData === undefined) {
      requestData = null;
    }

    // Send the request
    request.send(requestData);
  });
};

},{"../core/buildFullPath":15,"../core/createError":16,"./../core/settle":20,"./../helpers/buildURL":24,"./../helpers/cookies":26,"./../helpers/isURLSameOrigin":28,"./../helpers/parseHeaders":30,"./../utils":32}],9:[function(require,module,exports){
'use strict';

var utils = require('./utils');
var bind = require('./helpers/bind');
var Axios = require('./core/Axios');
var mergeConfig = require('./core/mergeConfig');
var defaults = require('./defaults');

/**
 * Create an instance of Axios
 *
 * @param {Object} defaultConfig The default config for the instance
 * @return {Axios} A new instance of Axios
 */
function createInstance(defaultConfig) {
  var context = new Axios(defaultConfig);
  var instance = bind(Axios.prototype.request, context);

  // Copy axios.prototype to instance
  utils.extend(instance, Axios.prototype, context);

  // Copy context to instance
  utils.extend(instance, context);

  return instance;
}

// Create the default instance to be exported
var axios = createInstance(defaults);

// Expose Axios class to allow class inheritance
axios.Axios = Axios;

// Factory for creating new instances
axios.create = function create(instanceConfig) {
  return createInstance(mergeConfig(axios.defaults, instanceConfig));
};

// Expose Cancel & CancelToken
axios.Cancel = require('./cancel/Cancel');
axios.CancelToken = require('./cancel/CancelToken');
axios.isCancel = require('./cancel/isCancel');

// Expose all/spread
axios.all = function all(promises) {
  return Promise.all(promises);
};
axios.spread = require('./helpers/spread');

module.exports = axios;

// Allow use of default import syntax in TypeScript
module.exports.default = axios;

},{"./cancel/Cancel":10,"./cancel/CancelToken":11,"./cancel/isCancel":12,"./core/Axios":13,"./core/mergeConfig":19,"./defaults":22,"./helpers/bind":23,"./helpers/spread":31,"./utils":32}],10:[function(require,module,exports){
'use strict';

/**
 * A `Cancel` is an object that is thrown when an operation is canceled.
 *
 * @class
 * @param {string=} message The message.
 */
function Cancel(message) {
  this.message = message;
}

Cancel.prototype.toString = function toString() {
  return 'Cancel' + (this.message ? ': ' + this.message : '');
};

Cancel.prototype.__CANCEL__ = true;

module.exports = Cancel;

},{}],11:[function(require,module,exports){
'use strict';

var Cancel = require('./Cancel');

/**
 * A `CancelToken` is an object that can be used to request cancellation of an operation.
 *
 * @class
 * @param {Function} executor The executor function.
 */
function CancelToken(executor) {
  if (typeof executor !== 'function') {
    throw new TypeError('executor must be a function.');
  }

  var resolvePromise;
  this.promise = new Promise(function promiseExecutor(resolve) {
    resolvePromise = resolve;
  });

  var token = this;
  executor(function cancel(message) {
    if (token.reason) {
      // Cancellation has already been requested
      return;
    }

    token.reason = new Cancel(message);
    resolvePromise(token.reason);
  });
}

/**
 * Throws a `Cancel` if cancellation has been requested.
 */
CancelToken.prototype.throwIfRequested = function throwIfRequested() {
  if (this.reason) {
    throw this.reason;
  }
};

/**
 * Returns an object that contains a new `CancelToken` and a function that, when called,
 * cancels the `CancelToken`.
 */
CancelToken.source = function source() {
  var cancel;
  var token = new CancelToken(function executor(c) {
    cancel = c;
  });
  return {
    token: token,
    cancel: cancel
  };
};

module.exports = CancelToken;

},{"./Cancel":10}],12:[function(require,module,exports){
'use strict';

module.exports = function isCancel(value) {
  return !!(value && value.__CANCEL__);
};

},{}],13:[function(require,module,exports){
'use strict';

var utils = require('./../utils');
var buildURL = require('../helpers/buildURL');
var InterceptorManager = require('./InterceptorManager');
var dispatchRequest = require('./dispatchRequest');
var mergeConfig = require('./mergeConfig');

/**
 * Create a new instance of Axios
 *
 * @param {Object} instanceConfig The default config for the instance
 */
function Axios(instanceConfig) {
  this.defaults = instanceConfig;
  this.interceptors = {
    request: new InterceptorManager(),
    response: new InterceptorManager()
  };
}

/**
 * Dispatch a request
 *
 * @param {Object} config The config specific for this request (merged with this.defaults)
 */
Axios.prototype.request = function request(config) {
  /*eslint no-param-reassign:0*/
  // Allow for axios('example/url'[, config]) a la fetch API
  if (typeof config === 'string') {
    config = arguments[1] || {};
    config.url = arguments[0];
  } else {
    config = config || {};
  }

  config = mergeConfig(this.defaults, config);

  // Set config.method
  if (config.method) {
    config.method = config.method.toLowerCase();
  } else if (this.defaults.method) {
    config.method = this.defaults.method.toLowerCase();
  } else {
    config.method = 'get';
  }

  // Hook up interceptors middleware
  var chain = [dispatchRequest, undefined];
  var promise = Promise.resolve(config);

  this.interceptors.request.forEach(function unshiftRequestInterceptors(interceptor) {
    chain.unshift(interceptor.fulfilled, interceptor.rejected);
  });

  this.interceptors.response.forEach(function pushResponseInterceptors(interceptor) {
    chain.push(interceptor.fulfilled, interceptor.rejected);
  });

  while (chain.length) {
    promise = promise.then(chain.shift(), chain.shift());
  }

  return promise;
};

Axios.prototype.getUri = function getUri(config) {
  config = mergeConfig(this.defaults, config);
  return buildURL(config.url, config.params, config.paramsSerializer).replace(/^\?/, '');
};

// Provide aliases for supported request methods
utils.forEach(['delete', 'get', 'head', 'options'], function forEachMethodNoData(method) {
  /*eslint func-names:0*/
  Axios.prototype[method] = function(url, config) {
    return this.request(utils.merge(config || {}, {
      method: method,
      url: url
    }));
  };
});

utils.forEach(['post', 'put', 'patch'], function forEachMethodWithData(method) {
  /*eslint func-names:0*/
  Axios.prototype[method] = function(url, data, config) {
    return this.request(utils.merge(config || {}, {
      method: method,
      url: url,
      data: data
    }));
  };
});

module.exports = Axios;

},{"../helpers/buildURL":24,"./../utils":32,"./InterceptorManager":14,"./dispatchRequest":17,"./mergeConfig":19}],14:[function(require,module,exports){
'use strict';

var utils = require('./../utils');

function InterceptorManager() {
  this.handlers = [];
}

/**
 * Add a new interceptor to the stack
 *
 * @param {Function} fulfilled The function to handle `then` for a `Promise`
 * @param {Function} rejected The function to handle `reject` for a `Promise`
 *
 * @return {Number} An ID used to remove interceptor later
 */
InterceptorManager.prototype.use = function use(fulfilled, rejected) {
  this.handlers.push({
    fulfilled: fulfilled,
    rejected: rejected
  });
  return this.handlers.length - 1;
};

/**
 * Remove an interceptor from the stack
 *
 * @param {Number} id The ID that was returned by `use`
 */
InterceptorManager.prototype.eject = function eject(id) {
  if (this.handlers[id]) {
    this.handlers[id] = null;
  }
};

/**
 * Iterate over all the registered interceptors
 *
 * This method is particularly useful for skipping over any
 * interceptors that may have become `null` calling `eject`.
 *
 * @param {Function} fn The function to call for each interceptor
 */
InterceptorManager.prototype.forEach = function forEach(fn) {
  utils.forEach(this.handlers, function forEachHandler(h) {
    if (h !== null) {
      fn(h);
    }
  });
};

module.exports = InterceptorManager;

},{"./../utils":32}],15:[function(require,module,exports){
'use strict';

var isAbsoluteURL = require('../helpers/isAbsoluteURL');
var combineURLs = require('../helpers/combineURLs');

/**
 * Creates a new URL by combining the baseURL with the requestedURL,
 * only when the requestedURL is not already an absolute URL.
 * If the requestURL is absolute, this function returns the requestedURL untouched.
 *
 * @param {string} baseURL The base URL
 * @param {string} requestedURL Absolute or relative URL to combine
 * @returns {string} The combined full path
 */
module.exports = function buildFullPath(baseURL, requestedURL) {
  if (baseURL && !isAbsoluteURL(requestedURL)) {
    return combineURLs(baseURL, requestedURL);
  }
  return requestedURL;
};

},{"../helpers/combineURLs":25,"../helpers/isAbsoluteURL":27}],16:[function(require,module,exports){
'use strict';

var enhanceError = require('./enhanceError');

/**
 * Create an Error with the specified message, config, error code, request and response.
 *
 * @param {string} message The error message.
 * @param {Object} config The config.
 * @param {string} [code] The error code (for example, 'ECONNABORTED').
 * @param {Object} [request] The request.
 * @param {Object} [response] The response.
 * @returns {Error} The created error.
 */
module.exports = function createError(message, config, code, request, response) {
  var error = new Error(message);
  return enhanceError(error, config, code, request, response);
};

},{"./enhanceError":18}],17:[function(require,module,exports){
'use strict';

var utils = require('./../utils');
var transformData = require('./transformData');
var isCancel = require('../cancel/isCancel');
var defaults = require('../defaults');

/**
 * Throws a `Cancel` if cancellation has been requested.
 */
function throwIfCancellationRequested(config) {
  if (config.cancelToken) {
    config.cancelToken.throwIfRequested();
  }
}

/**
 * Dispatch a request to the server using the configured adapter.
 *
 * @param {object} config The config that is to be used for the request
 * @returns {Promise} The Promise to be fulfilled
 */
module.exports = function dispatchRequest(config) {
  throwIfCancellationRequested(config);

  // Ensure headers exist
  config.headers = config.headers || {};

  // Transform request data
  config.data = transformData(
    config.data,
    config.headers,
    config.transformRequest
  );

  // Flatten headers
  config.headers = utils.merge(
    config.headers.common || {},
    config.headers[config.method] || {},
    config.headers
  );

  utils.forEach(
    ['delete', 'get', 'head', 'post', 'put', 'patch', 'common'],
    function cleanHeaderConfig(method) {
      delete config.headers[method];
    }
  );

  var adapter = config.adapter || defaults.adapter;

  return adapter(config).then(function onAdapterResolution(response) {
    throwIfCancellationRequested(config);

    // Transform response data
    response.data = transformData(
      response.data,
      response.headers,
      config.transformResponse
    );

    return response;
  }, function onAdapterRejection(reason) {
    if (!isCancel(reason)) {
      throwIfCancellationRequested(config);

      // Transform response data
      if (reason && reason.response) {
        reason.response.data = transformData(
          reason.response.data,
          reason.response.headers,
          config.transformResponse
        );
      }
    }

    return Promise.reject(reason);
  });
};

},{"../cancel/isCancel":12,"../defaults":22,"./../utils":32,"./transformData":21}],18:[function(require,module,exports){
'use strict';

/**
 * Update an Error with the specified config, error code, and response.
 *
 * @param {Error} error The error to update.
 * @param {Object} config The config.
 * @param {string} [code] The error code (for example, 'ECONNABORTED').
 * @param {Object} [request] The request.
 * @param {Object} [response] The response.
 * @returns {Error} The error.
 */
module.exports = function enhanceError(error, config, code, request, response) {
  error.config = config;
  if (code) {
    error.code = code;
  }

  error.request = request;
  error.response = response;
  error.isAxiosError = true;

  error.toJSON = function() {
    return {
      // Standard
      message: this.message,
      name: this.name,
      // Microsoft
      description: this.description,
      number: this.number,
      // Mozilla
      fileName: this.fileName,
      lineNumber: this.lineNumber,
      columnNumber: this.columnNumber,
      stack: this.stack,
      // Axios
      config: this.config,
      code: this.code
    };
  };
  return error;
};

},{}],19:[function(require,module,exports){
'use strict';

var utils = require('../utils');

/**
 * Config-specific merge-function which creates a new config-object
 * by merging two configuration objects together.
 *
 * @param {Object} config1
 * @param {Object} config2
 * @returns {Object} New object resulting from merging config2 to config1
 */
module.exports = function mergeConfig(config1, config2) {
  // eslint-disable-next-line no-param-reassign
  config2 = config2 || {};
  var config = {};

  var valueFromConfig2Keys = ['url', 'method', 'params', 'data'];
  var mergeDeepPropertiesKeys = ['headers', 'auth', 'proxy'];
  var defaultToConfig2Keys = [
    'baseURL', 'url', 'transformRequest', 'transformResponse', 'paramsSerializer',
    'timeout', 'withCredentials', 'adapter', 'responseType', 'xsrfCookieName',
    'xsrfHeaderName', 'onUploadProgress', 'onDownloadProgress',
    'maxContentLength', 'validateStatus', 'maxRedirects', 'httpAgent',
    'httpsAgent', 'cancelToken', 'socketPath'
  ];

  utils.forEach(valueFromConfig2Keys, function valueFromConfig2(prop) {
    if (typeof config2[prop] !== 'undefined') {
      config[prop] = config2[prop];
    }
  });

  utils.forEach(mergeDeepPropertiesKeys, function mergeDeepProperties(prop) {
    if (utils.isObject(config2[prop])) {
      config[prop] = utils.deepMerge(config1[prop], config2[prop]);
    } else if (typeof config2[prop] !== 'undefined') {
      config[prop] = config2[prop];
    } else if (utils.isObject(config1[prop])) {
      config[prop] = utils.deepMerge(config1[prop]);
    } else if (typeof config1[prop] !== 'undefined') {
      config[prop] = config1[prop];
    }
  });

  utils.forEach(defaultToConfig2Keys, function defaultToConfig2(prop) {
    if (typeof config2[prop] !== 'undefined') {
      config[prop] = config2[prop];
    } else if (typeof config1[prop] !== 'undefined') {
      config[prop] = config1[prop];
    }
  });

  var axiosKeys = valueFromConfig2Keys
    .concat(mergeDeepPropertiesKeys)
    .concat(defaultToConfig2Keys);

  var otherKeys = Object
    .keys(config2)
    .filter(function filterAxiosKeys(key) {
      return axiosKeys.indexOf(key) === -1;
    });

  utils.forEach(otherKeys, function otherKeysDefaultToConfig2(prop) {
    if (typeof config2[prop] !== 'undefined') {
      config[prop] = config2[prop];
    } else if (typeof config1[prop] !== 'undefined') {
      config[prop] = config1[prop];
    }
  });

  return config;
};

},{"../utils":32}],20:[function(require,module,exports){
'use strict';

var createError = require('./createError');

/**
 * Resolve or reject a Promise based on response status.
 *
 * @param {Function} resolve A function that resolves the promise.
 * @param {Function} reject A function that rejects the promise.
 * @param {object} response The response.
 */
module.exports = function settle(resolve, reject, response) {
  var validateStatus = response.config.validateStatus;
  if (!validateStatus || validateStatus(response.status)) {
    resolve(response);
  } else {
    reject(createError(
      'Request failed with status code ' + response.status,
      response.config,
      null,
      response.request,
      response
    ));
  }
};

},{"./createError":16}],21:[function(require,module,exports){
'use strict';

var utils = require('./../utils');

/**
 * Transform the data for a request or a response
 *
 * @param {Object|String} data The data to be transformed
 * @param {Array} headers The headers for the request or response
 * @param {Array|Function} fns A single function or Array of functions
 * @returns {*} The resulting transformed data
 */
module.exports = function transformData(data, headers, fns) {
  /*eslint no-param-reassign:0*/
  utils.forEach(fns, function transform(fn) {
    data = fn(data, headers);
  });

  return data;
};

},{"./../utils":32}],22:[function(require,module,exports){
(function (process){
'use strict';

var utils = require('./utils');
var normalizeHeaderName = require('./helpers/normalizeHeaderName');

var DEFAULT_CONTENT_TYPE = {
  'Content-Type': 'application/x-www-form-urlencoded'
};

function setContentTypeIfUnset(headers, value) {
  if (!utils.isUndefined(headers) && utils.isUndefined(headers['Content-Type'])) {
    headers['Content-Type'] = value;
  }
}

function getDefaultAdapter() {
  var adapter;
  if (typeof XMLHttpRequest !== 'undefined') {
    // For browsers use XHR adapter
    adapter = require('./adapters/xhr');
  } else if (typeof process !== 'undefined' && Object.prototype.toString.call(process) === '[object process]') {
    // For node use HTTP adapter
    adapter = require('./adapters/http');
  }
  return adapter;
}

var defaults = {
  adapter: getDefaultAdapter(),

  transformRequest: [function transformRequest(data, headers) {
    normalizeHeaderName(headers, 'Accept');
    normalizeHeaderName(headers, 'Content-Type');
    if (utils.isFormData(data) ||
      utils.isArrayBuffer(data) ||
      utils.isBuffer(data) ||
      utils.isStream(data) ||
      utils.isFile(data) ||
      utils.isBlob(data)
    ) {
      return data;
    }
    if (utils.isArrayBufferView(data)) {
      return data.buffer;
    }
    if (utils.isURLSearchParams(data)) {
      setContentTypeIfUnset(headers, 'application/x-www-form-urlencoded;charset=utf-8');
      return data.toString();
    }
    if (utils.isObject(data)) {
      setContentTypeIfUnset(headers, 'application/json;charset=utf-8');
      return JSON.stringify(data);
    }
    return data;
  }],

  transformResponse: [function transformResponse(data) {
    /*eslint no-param-reassign:0*/
    if (typeof data === 'string') {
      try {
        data = JSON.parse(data);
      } catch (e) { /* Ignore */ }
    }
    return data;
  }],

  /**
   * A timeout in milliseconds to abort a request. If set to 0 (default) a
   * timeout is not created.
   */
  timeout: 0,

  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',

  maxContentLength: -1,

  validateStatus: function validateStatus(status) {
    return status >= 200 && status < 300;
  }
};

defaults.headers = {
  common: {
    'Accept': 'application/json, text/plain, */*'
  }
};

utils.forEach(['delete', 'get', 'head'], function forEachMethodNoData(method) {
  defaults.headers[method] = {};
});

utils.forEach(['post', 'put', 'patch'], function forEachMethodWithData(method) {
  defaults.headers[method] = utils.merge(DEFAULT_CONTENT_TYPE);
});

module.exports = defaults;

}).call(this,require('_process'))
},{"./adapters/http":8,"./adapters/xhr":8,"./helpers/normalizeHeaderName":29,"./utils":32,"_process":69}],23:[function(require,module,exports){
'use strict';

module.exports = function bind(fn, thisArg) {
  return function wrap() {
    var args = new Array(arguments.length);
    for (var i = 0; i < args.length; i++) {
      args[i] = arguments[i];
    }
    return fn.apply(thisArg, args);
  };
};

},{}],24:[function(require,module,exports){
'use strict';

var utils = require('./../utils');

function encode(val) {
  return encodeURIComponent(val).
    replace(/%40/gi, '@').
    replace(/%3A/gi, ':').
    replace(/%24/g, '$').
    replace(/%2C/gi, ',').
    replace(/%20/g, '+').
    replace(/%5B/gi, '[').
    replace(/%5D/gi, ']');
}

/**
 * Build a URL by appending params to the end
 *
 * @param {string} url The base of the url (e.g., http://www.google.com)
 * @param {object} [params] The params to be appended
 * @returns {string} The formatted url
 */
module.exports = function buildURL(url, params, paramsSerializer) {
  /*eslint no-param-reassign:0*/
  if (!params) {
    return url;
  }

  var serializedParams;
  if (paramsSerializer) {
    serializedParams = paramsSerializer(params);
  } else if (utils.isURLSearchParams(params)) {
    serializedParams = params.toString();
  } else {
    var parts = [];

    utils.forEach(params, function serialize(val, key) {
      if (val === null || typeof val === 'undefined') {
        return;
      }

      if (utils.isArray(val)) {
        key = key + '[]';
      } else {
        val = [val];
      }

      utils.forEach(val, function parseValue(v) {
        if (utils.isDate(v)) {
          v = v.toISOString();
        } else if (utils.isObject(v)) {
          v = JSON.stringify(v);
        }
        parts.push(encode(key) + '=' + encode(v));
      });
    });

    serializedParams = parts.join('&');
  }

  if (serializedParams) {
    var hashmarkIndex = url.indexOf('#');
    if (hashmarkIndex !== -1) {
      url = url.slice(0, hashmarkIndex);
    }

    url += (url.indexOf('?') === -1 ? '?' : '&') + serializedParams;
  }

  return url;
};

},{"./../utils":32}],25:[function(require,module,exports){
'use strict';

/**
 * Creates a new URL by combining the specified URLs
 *
 * @param {string} baseURL The base URL
 * @param {string} relativeURL The relative URL
 * @returns {string} The combined URL
 */
module.exports = function combineURLs(baseURL, relativeURL) {
  return relativeURL
    ? baseURL.replace(/\/+$/, '') + '/' + relativeURL.replace(/^\/+/, '')
    : baseURL;
};

},{}],26:[function(require,module,exports){
'use strict';

var utils = require('./../utils');

module.exports = (
  utils.isStandardBrowserEnv() ?

  // Standard browser envs support document.cookie
    (function standardBrowserEnv() {
      return {
        write: function write(name, value, expires, path, domain, secure) {
          var cookie = [];
          cookie.push(name + '=' + encodeURIComponent(value));

          if (utils.isNumber(expires)) {
            cookie.push('expires=' + new Date(expires).toGMTString());
          }

          if (utils.isString(path)) {
            cookie.push('path=' + path);
          }

          if (utils.isString(domain)) {
            cookie.push('domain=' + domain);
          }

          if (secure === true) {
            cookie.push('secure');
          }

          document.cookie = cookie.join('; ');
        },

        read: function read(name) {
          var match = document.cookie.match(new RegExp('(^|;\\s*)(' + name + ')=([^;]*)'));
          return (match ? decodeURIComponent(match[3]) : null);
        },

        remove: function remove(name) {
          this.write(name, '', Date.now() - 86400000);
        }
      };
    })() :

  // Non standard browser env (web workers, react-native) lack needed support.
    (function nonStandardBrowserEnv() {
      return {
        write: function write() {},
        read: function read() { return null; },
        remove: function remove() {}
      };
    })()
);

},{"./../utils":32}],27:[function(require,module,exports){
'use strict';

/**
 * Determines whether the specified URL is absolute
 *
 * @param {string} url The URL to test
 * @returns {boolean} True if the specified URL is absolute, otherwise false
 */
module.exports = function isAbsoluteURL(url) {
  // A URL is considered absolute if it begins with "<scheme>://" or "//" (protocol-relative URL).
  // RFC 3986 defines scheme name as a sequence of characters beginning with a letter and followed
  // by any combination of letters, digits, plus, period, or hyphen.
  return /^([a-z][a-z\d\+\-\.]*:)?\/\//i.test(url);
};

},{}],28:[function(require,module,exports){
'use strict';

var utils = require('./../utils');

module.exports = (
  utils.isStandardBrowserEnv() ?

  // Standard browser envs have full support of the APIs needed to test
  // whether the request URL is of the same origin as current location.
    (function standardBrowserEnv() {
      var msie = /(msie|trident)/i.test(navigator.userAgent);
      var urlParsingNode = document.createElement('a');
      var originURL;

      /**
    * Parse a URL to discover it's components
    *
    * @param {String} url The URL to be parsed
    * @returns {Object}
    */
      function resolveURL(url) {
        var href = url;

        if (msie) {
        // IE needs attribute set twice to normalize properties
          urlParsingNode.setAttribute('href', href);
          href = urlParsingNode.href;
        }

        urlParsingNode.setAttribute('href', href);

        // urlParsingNode provides the UrlUtils interface - http://url.spec.whatwg.org/#urlutils
        return {
          href: urlParsingNode.href,
          protocol: urlParsingNode.protocol ? urlParsingNode.protocol.replace(/:$/, '') : '',
          host: urlParsingNode.host,
          search: urlParsingNode.search ? urlParsingNode.search.replace(/^\?/, '') : '',
          hash: urlParsingNode.hash ? urlParsingNode.hash.replace(/^#/, '') : '',
          hostname: urlParsingNode.hostname,
          port: urlParsingNode.port,
          pathname: (urlParsingNode.pathname.charAt(0) === '/') ?
            urlParsingNode.pathname :
            '/' + urlParsingNode.pathname
        };
      }

      originURL = resolveURL(window.location.href);

      /**
    * Determine if a URL shares the same origin as the current location
    *
    * @param {String} requestURL The URL to test
    * @returns {boolean} True if URL shares the same origin, otherwise false
    */
      return function isURLSameOrigin(requestURL) {
        var parsed = (utils.isString(requestURL)) ? resolveURL(requestURL) : requestURL;
        return (parsed.protocol === originURL.protocol &&
            parsed.host === originURL.host);
      };
    })() :

  // Non standard browser envs (web workers, react-native) lack needed support.
    (function nonStandardBrowserEnv() {
      return function isURLSameOrigin() {
        return true;
      };
    })()
);

},{"./../utils":32}],29:[function(require,module,exports){
'use strict';

var utils = require('../utils');

module.exports = function normalizeHeaderName(headers, normalizedName) {
  utils.forEach(headers, function processHeader(value, name) {
    if (name !== normalizedName && name.toUpperCase() === normalizedName.toUpperCase()) {
      headers[normalizedName] = value;
      delete headers[name];
    }
  });
};

},{"../utils":32}],30:[function(require,module,exports){
'use strict';

var utils = require('./../utils');

// Headers whose duplicates are ignored by node
// c.f. https://nodejs.org/api/http.html#http_message_headers
var ignoreDuplicateOf = [
  'age', 'authorization', 'content-length', 'content-type', 'etag',
  'expires', 'from', 'host', 'if-modified-since', 'if-unmodified-since',
  'last-modified', 'location', 'max-forwards', 'proxy-authorization',
  'referer', 'retry-after', 'user-agent'
];

/**
 * Parse headers into an object
 *
 * ```
 * Date: Wed, 27 Aug 2014 08:58:49 GMT
 * Content-Type: application/json
 * Connection: keep-alive
 * Transfer-Encoding: chunked
 * ```
 *
 * @param {String} headers Headers needing to be parsed
 * @returns {Object} Headers parsed into an object
 */
module.exports = function parseHeaders(headers) {
  var parsed = {};
  var key;
  var val;
  var i;

  if (!headers) { return parsed; }

  utils.forEach(headers.split('\n'), function parser(line) {
    i = line.indexOf(':');
    key = utils.trim(line.substr(0, i)).toLowerCase();
    val = utils.trim(line.substr(i + 1));

    if (key) {
      if (parsed[key] && ignoreDuplicateOf.indexOf(key) >= 0) {
        return;
      }
      if (key === 'set-cookie') {
        parsed[key] = (parsed[key] ? parsed[key] : []).concat([val]);
      } else {
        parsed[key] = parsed[key] ? parsed[key] + ', ' + val : val;
      }
    }
  });

  return parsed;
};

},{"./../utils":32}],31:[function(require,module,exports){
'use strict';

/**
 * Syntactic sugar for invoking a function and expanding an array for arguments.
 *
 * Common use case would be to use `Function.prototype.apply`.
 *
 *  ```js
 *  function f(x, y, z) {}
 *  var args = [1, 2, 3];
 *  f.apply(null, args);
 *  ```
 *
 * With `spread` this example can be re-written.
 *
 *  ```js
 *  spread(function(x, y, z) {})([1, 2, 3]);
 *  ```
 *
 * @param {Function} callback
 * @returns {Function}
 */
module.exports = function spread(callback) {
  return function wrap(arr) {
    return callback.apply(null, arr);
  };
};

},{}],32:[function(require,module,exports){
'use strict';

var bind = require('./helpers/bind');

/*global toString:true*/

// utils is a library of generic helper functions non-specific to axios

var toString = Object.prototype.toString;

/**
 * Determine if a value is an Array
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an Array, otherwise false
 */
function isArray(val) {
  return toString.call(val) === '[object Array]';
}

/**
 * Determine if a value is undefined
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if the value is undefined, otherwise false
 */
function isUndefined(val) {
  return typeof val === 'undefined';
}

/**
 * Determine if a value is a Buffer
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Buffer, otherwise false
 */
function isBuffer(val) {
  return val !== null && !isUndefined(val) && val.constructor !== null && !isUndefined(val.constructor)
    && typeof val.constructor.isBuffer === 'function' && val.constructor.isBuffer(val);
}

/**
 * Determine if a value is an ArrayBuffer
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an ArrayBuffer, otherwise false
 */
function isArrayBuffer(val) {
  return toString.call(val) === '[object ArrayBuffer]';
}

/**
 * Determine if a value is a FormData
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an FormData, otherwise false
 */
function isFormData(val) {
  return (typeof FormData !== 'undefined') && (val instanceof FormData);
}

/**
 * Determine if a value is a view on an ArrayBuffer
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a view on an ArrayBuffer, otherwise false
 */
function isArrayBufferView(val) {
  var result;
  if ((typeof ArrayBuffer !== 'undefined') && (ArrayBuffer.isView)) {
    result = ArrayBuffer.isView(val);
  } else {
    result = (val) && (val.buffer) && (val.buffer instanceof ArrayBuffer);
  }
  return result;
}

/**
 * Determine if a value is a String
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a String, otherwise false
 */
function isString(val) {
  return typeof val === 'string';
}

/**
 * Determine if a value is a Number
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Number, otherwise false
 */
function isNumber(val) {
  return typeof val === 'number';
}

/**
 * Determine if a value is an Object
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is an Object, otherwise false
 */
function isObject(val) {
  return val !== null && typeof val === 'object';
}

/**
 * Determine if a value is a Date
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Date, otherwise false
 */
function isDate(val) {
  return toString.call(val) === '[object Date]';
}

/**
 * Determine if a value is a File
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a File, otherwise false
 */
function isFile(val) {
  return toString.call(val) === '[object File]';
}

/**
 * Determine if a value is a Blob
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Blob, otherwise false
 */
function isBlob(val) {
  return toString.call(val) === '[object Blob]';
}

/**
 * Determine if a value is a Function
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Function, otherwise false
 */
function isFunction(val) {
  return toString.call(val) === '[object Function]';
}

/**
 * Determine if a value is a Stream
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a Stream, otherwise false
 */
function isStream(val) {
  return isObject(val) && isFunction(val.pipe);
}

/**
 * Determine if a value is a URLSearchParams object
 *
 * @param {Object} val The value to test
 * @returns {boolean} True if value is a URLSearchParams object, otherwise false
 */
function isURLSearchParams(val) {
  return typeof URLSearchParams !== 'undefined' && val instanceof URLSearchParams;
}

/**
 * Trim excess whitespace off the beginning and end of a string
 *
 * @param {String} str The String to trim
 * @returns {String} The String freed of excess whitespace
 */
function trim(str) {
  return str.replace(/^\s*/, '').replace(/\s*$/, '');
}

/**
 * Determine if we're running in a standard browser environment
 *
 * This allows axios to run in a web worker, and react-native.
 * Both environments support XMLHttpRequest, but not fully standard globals.
 *
 * web workers:
 *  typeof window -> undefined
 *  typeof document -> undefined
 *
 * react-native:
 *  navigator.product -> 'ReactNative'
 * nativescript
 *  navigator.product -> 'NativeScript' or 'NS'
 */
function isStandardBrowserEnv() {
  if (typeof navigator !== 'undefined' && (navigator.product === 'ReactNative' ||
                                           navigator.product === 'NativeScript' ||
                                           navigator.product === 'NS')) {
    return false;
  }
  return (
    typeof window !== 'undefined' &&
    typeof document !== 'undefined'
  );
}

/**
 * Iterate over an Array or an Object invoking a function for each item.
 *
 * If `obj` is an Array callback will be called passing
 * the value, index, and complete array for each item.
 *
 * If 'obj' is an Object callback will be called passing
 * the value, key, and complete object for each property.
 *
 * @param {Object|Array} obj The object to iterate
 * @param {Function} fn The callback to invoke for each item
 */
function forEach(obj, fn) {
  // Don't bother if no value provided
  if (obj === null || typeof obj === 'undefined') {
    return;
  }

  // Force an array if not already something iterable
  if (typeof obj !== 'object') {
    /*eslint no-param-reassign:0*/
    obj = [obj];
  }

  if (isArray(obj)) {
    // Iterate over array values
    for (var i = 0, l = obj.length; i < l; i++) {
      fn.call(null, obj[i], i, obj);
    }
  } else {
    // Iterate over object keys
    for (var key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        fn.call(null, obj[key], key, obj);
      }
    }
  }
}

/**
 * Accepts varargs expecting each argument to be an object, then
 * immutably merges the properties of each object and returns result.
 *
 * When multiple objects contain the same key the later object in
 * the arguments list will take precedence.
 *
 * Example:
 *
 * ```js
 * var result = merge({foo: 123}, {foo: 456});
 * console.log(result.foo); // outputs 456
 * ```
 *
 * @param {Object} obj1 Object to merge
 * @returns {Object} Result of all merge properties
 */
function merge(/* obj1, obj2, obj3, ... */) {
  var result = {};
  function assignValue(val, key) {
    if (typeof result[key] === 'object' && typeof val === 'object') {
      result[key] = merge(result[key], val);
    } else {
      result[key] = val;
    }
  }

  for (var i = 0, l = arguments.length; i < l; i++) {
    forEach(arguments[i], assignValue);
  }
  return result;
}

/**
 * Function equal to merge with the difference being that no reference
 * to original objects is kept.
 *
 * @see merge
 * @param {Object} obj1 Object to merge
 * @returns {Object} Result of all merge properties
 */
function deepMerge(/* obj1, obj2, obj3, ... */) {
  var result = {};
  function assignValue(val, key) {
    if (typeof result[key] === 'object' && typeof val === 'object') {
      result[key] = deepMerge(result[key], val);
    } else if (typeof val === 'object') {
      result[key] = deepMerge({}, val);
    } else {
      result[key] = val;
    }
  }

  for (var i = 0, l = arguments.length; i < l; i++) {
    forEach(arguments[i], assignValue);
  }
  return result;
}

/**
 * Extends object a by mutably adding to it the properties of object b.
 *
 * @param {Object} a The object to be extended
 * @param {Object} b The object to copy properties from
 * @param {Object} thisArg The object to bind function to
 * @return {Object} The resulting value of object a
 */
function extend(a, b, thisArg) {
  forEach(b, function assignValue(val, key) {
    if (thisArg && typeof val === 'function') {
      a[key] = bind(val, thisArg);
    } else {
      a[key] = val;
    }
  });
  return a;
}

module.exports = {
  isArray: isArray,
  isArrayBuffer: isArrayBuffer,
  isBuffer: isBuffer,
  isFormData: isFormData,
  isArrayBufferView: isArrayBufferView,
  isString: isString,
  isNumber: isNumber,
  isObject: isObject,
  isUndefined: isUndefined,
  isDate: isDate,
  isFile: isFile,
  isBlob: isBlob,
  isFunction: isFunction,
  isStream: isStream,
  isURLSearchParams: isURLSearchParams,
  isStandardBrowserEnv: isStandardBrowserEnv,
  forEach: forEach,
  merge: merge,
  deepMerge: deepMerge,
  extend: extend,
  trim: trim
};

},{"./helpers/bind":23}],33:[function(require,module,exports){
!function(e,t){"object"==typeof exports&&"object"==typeof module?module.exports=t():"function"==typeof define&&define.amd?define([],t):"object"==typeof exports?exports.bowser=t():e.bowser=t()}(this,(function(){return function(e){var t={};function r(n){if(t[n])return t[n].exports;var i=t[n]={i:n,l:!1,exports:{}};return e[n].call(i.exports,i,i.exports,r),i.l=!0,i.exports}return r.m=e,r.c=t,r.d=function(e,t,n){r.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:n})},r.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},r.t=function(e,t){if(1&t&&(e=r(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var n=Object.create(null);if(r.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var i in e)r.d(n,i,function(t){return e[t]}.bind(null,i));return n},r.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return r.d(t,"a",t),t},r.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},r.p="",r(r.s=90)}({17:function(e,t,r){"use strict";t.__esModule=!0,t.default=void 0;var n=r(18),i=function(){function e(){}return e.getFirstMatch=function(e,t){var r=t.match(e);return r&&r.length>0&&r[1]||""},e.getSecondMatch=function(e,t){var r=t.match(e);return r&&r.length>1&&r[2]||""},e.matchAndReturnConst=function(e,t,r){if(e.test(t))return r},e.getWindowsVersionName=function(e){switch(e){case"NT":return"NT";case"XP":return"XP";case"NT 5.0":return"2000";case"NT 5.1":return"XP";case"NT 5.2":return"2003";case"NT 6.0":return"Vista";case"NT 6.1":return"7";case"NT 6.2":return"8";case"NT 6.3":return"8.1";case"NT 10.0":return"10";default:return}},e.getMacOSVersionName=function(e){var t=e.split(".").splice(0,2).map((function(e){return parseInt(e,10)||0}));if(t.push(0),10===t[0])switch(t[1]){case 5:return"Leopard";case 6:return"Snow Leopard";case 7:return"Lion";case 8:return"Mountain Lion";case 9:return"Mavericks";case 10:return"Yosemite";case 11:return"El Capitan";case 12:return"Sierra";case 13:return"High Sierra";case 14:return"Mojave";case 15:return"Catalina";default:return}},e.getAndroidVersionName=function(e){var t=e.split(".").splice(0,2).map((function(e){return parseInt(e,10)||0}));if(t.push(0),!(1===t[0]&&t[1]<5))return 1===t[0]&&t[1]<6?"Cupcake":1===t[0]&&t[1]>=6?"Donut":2===t[0]&&t[1]<2?"Eclair":2===t[0]&&2===t[1]?"Froyo":2===t[0]&&t[1]>2?"Gingerbread":3===t[0]?"Honeycomb":4===t[0]&&t[1]<1?"Ice Cream Sandwich":4===t[0]&&t[1]<4?"Jelly Bean":4===t[0]&&t[1]>=4?"KitKat":5===t[0]?"Lollipop":6===t[0]?"Marshmallow":7===t[0]?"Nougat":8===t[0]?"Oreo":9===t[0]?"Pie":void 0},e.getVersionPrecision=function(e){return e.split(".").length},e.compareVersions=function(t,r,n){void 0===n&&(n=!1);var i=e.getVersionPrecision(t),s=e.getVersionPrecision(r),o=Math.max(i,s),a=0,u=e.map([t,r],(function(t){var r=o-e.getVersionPrecision(t),n=t+new Array(r+1).join(".0");return e.map(n.split("."),(function(e){return new Array(20-e.length).join("0")+e})).reverse()}));for(n&&(a=o-Math.min(i,s)),o-=1;o>=a;){if(u[0][o]>u[1][o])return 1;if(u[0][o]===u[1][o]){if(o===a)return 0;o-=1}else if(u[0][o]<u[1][o])return-1}},e.map=function(e,t){var r,n=[];if(Array.prototype.map)return Array.prototype.map.call(e,t);for(r=0;r<e.length;r+=1)n.push(t(e[r]));return n},e.find=function(e,t){var r,n;if(Array.prototype.find)return Array.prototype.find.call(e,t);for(r=0,n=e.length;r<n;r+=1){var i=e[r];if(t(i,r))return i}},e.assign=function(e){for(var t,r,n=e,i=arguments.length,s=new Array(i>1?i-1:0),o=1;o<i;o++)s[o-1]=arguments[o];if(Object.assign)return Object.assign.apply(Object,[e].concat(s));var a=function(){var e=s[t];"object"==typeof e&&null!==e&&Object.keys(e).forEach((function(t){n[t]=e[t]}))};for(t=0,r=s.length;t<r;t+=1)a();return e},e.getBrowserAlias=function(e){return n.BROWSER_ALIASES_MAP[e]},e.getBrowserTypeByAlias=function(e){return n.BROWSER_MAP[e]||""},e}();t.default=i,e.exports=t.default},18:function(e,t,r){"use strict";t.__esModule=!0,t.ENGINE_MAP=t.OS_MAP=t.PLATFORMS_MAP=t.BROWSER_MAP=t.BROWSER_ALIASES_MAP=void 0;t.BROWSER_ALIASES_MAP={"Amazon Silk":"amazon_silk","Android Browser":"android",Bada:"bada",BlackBerry:"blackberry",Chrome:"chrome",Chromium:"chromium",Electron:"electron",Epiphany:"epiphany",Firefox:"firefox",Focus:"focus",Generic:"generic","Google Search":"google_search",Googlebot:"googlebot","Internet Explorer":"ie","K-Meleon":"k_meleon",Maxthon:"maxthon","Microsoft Edge":"edge","MZ Browser":"mz","NAVER Whale Browser":"naver",Opera:"opera","Opera Coast":"opera_coast",PhantomJS:"phantomjs",Puffin:"puffin",QupZilla:"qupzilla",QQ:"qq",QQLite:"qqlite",Safari:"safari",Sailfish:"sailfish","Samsung Internet for Android":"samsung_internet",SeaMonkey:"seamonkey",Sleipnir:"sleipnir",Swing:"swing",Tizen:"tizen","UC Browser":"uc",Vivaldi:"vivaldi","WebOS Browser":"webos",WeChat:"wechat","Yandex Browser":"yandex",Roku:"roku"};t.BROWSER_MAP={amazon_silk:"Amazon Silk",android:"Android Browser",bada:"Bada",blackberry:"BlackBerry",chrome:"Chrome",chromium:"Chromium",electron:"Electron",epiphany:"Epiphany",firefox:"Firefox",focus:"Focus",generic:"Generic",googlebot:"Googlebot",google_search:"Google Search",ie:"Internet Explorer",k_meleon:"K-Meleon",maxthon:"Maxthon",edge:"Microsoft Edge",mz:"MZ Browser",naver:"NAVER Whale Browser",opera:"Opera",opera_coast:"Opera Coast",phantomjs:"PhantomJS",puffin:"Puffin",qupzilla:"QupZilla",qq:"QQ Browser",qqlite:"QQ Browser Lite",safari:"Safari",sailfish:"Sailfish",samsung_internet:"Samsung Internet for Android",seamonkey:"SeaMonkey",sleipnir:"Sleipnir",swing:"Swing",tizen:"Tizen",uc:"UC Browser",vivaldi:"Vivaldi",webos:"WebOS Browser",wechat:"WeChat",yandex:"Yandex Browser"};t.PLATFORMS_MAP={tablet:"tablet",mobile:"mobile",desktop:"desktop",tv:"tv"};t.OS_MAP={WindowsPhone:"Windows Phone",Windows:"Windows",MacOS:"macOS",iOS:"iOS",Android:"Android",WebOS:"WebOS",BlackBerry:"BlackBerry",Bada:"Bada",Tizen:"Tizen",Linux:"Linux",ChromeOS:"Chrome OS",PlayStation4:"PlayStation 4",Roku:"Roku"};t.ENGINE_MAP={EdgeHTML:"EdgeHTML",Blink:"Blink",Trident:"Trident",Presto:"Presto",Gecko:"Gecko",WebKit:"WebKit"}},90:function(e,t,r){"use strict";t.__esModule=!0,t.default=void 0;var n,i=(n=r(91))&&n.__esModule?n:{default:n},s=r(18);function o(e,t){for(var r=0;r<t.length;r++){var n=t[r];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(e,n.key,n)}}var a=function(){function e(){}var t,r,n;return e.getParser=function(e,t){if(void 0===t&&(t=!1),"string"!=typeof e)throw new Error("UserAgent should be a string");return new i.default(e,t)},e.parse=function(e){return new i.default(e).getResult()},t=e,n=[{key:"BROWSER_MAP",get:function(){return s.BROWSER_MAP}},{key:"ENGINE_MAP",get:function(){return s.ENGINE_MAP}},{key:"OS_MAP",get:function(){return s.OS_MAP}},{key:"PLATFORMS_MAP",get:function(){return s.PLATFORMS_MAP}}],(r=null)&&o(t.prototype,r),n&&o(t,n),e}();t.default=a,e.exports=t.default},91:function(e,t,r){"use strict";t.__esModule=!0,t.default=void 0;var n=u(r(92)),i=u(r(93)),s=u(r(94)),o=u(r(95)),a=u(r(17));function u(e){return e&&e.__esModule?e:{default:e}}var d=function(){function e(e,t){if(void 0===t&&(t=!1),null==e||""===e)throw new Error("UserAgent parameter can't be empty");this._ua=e,this.parsedResult={},!0!==t&&this.parse()}var t=e.prototype;return t.getUA=function(){return this._ua},t.test=function(e){return e.test(this._ua)},t.parseBrowser=function(){var e=this;this.parsedResult.browser={};var t=a.default.find(n.default,(function(t){if("function"==typeof t.test)return t.test(e);if(t.test instanceof Array)return t.test.some((function(t){return e.test(t)}));throw new Error("Browser's test function is not valid")}));return t&&(this.parsedResult.browser=t.describe(this.getUA())),this.parsedResult.browser},t.getBrowser=function(){return this.parsedResult.browser?this.parsedResult.browser:this.parseBrowser()},t.getBrowserName=function(e){return e?String(this.getBrowser().name).toLowerCase()||"":this.getBrowser().name||""},t.getBrowserVersion=function(){return this.getBrowser().version},t.getOS=function(){return this.parsedResult.os?this.parsedResult.os:this.parseOS()},t.parseOS=function(){var e=this;this.parsedResult.os={};var t=a.default.find(i.default,(function(t){if("function"==typeof t.test)return t.test(e);if(t.test instanceof Array)return t.test.some((function(t){return e.test(t)}));throw new Error("Browser's test function is not valid")}));return t&&(this.parsedResult.os=t.describe(this.getUA())),this.parsedResult.os},t.getOSName=function(e){var t=this.getOS().name;return e?String(t).toLowerCase()||"":t||""},t.getOSVersion=function(){return this.getOS().version},t.getPlatform=function(){return this.parsedResult.platform?this.parsedResult.platform:this.parsePlatform()},t.getPlatformType=function(e){void 0===e&&(e=!1);var t=this.getPlatform().type;return e?String(t).toLowerCase()||"":t||""},t.parsePlatform=function(){var e=this;this.parsedResult.platform={};var t=a.default.find(s.default,(function(t){if("function"==typeof t.test)return t.test(e);if(t.test instanceof Array)return t.test.some((function(t){return e.test(t)}));throw new Error("Browser's test function is not valid")}));return t&&(this.parsedResult.platform=t.describe(this.getUA())),this.parsedResult.platform},t.getEngine=function(){return this.parsedResult.engine?this.parsedResult.engine:this.parseEngine()},t.getEngineName=function(e){return e?String(this.getEngine().name).toLowerCase()||"":this.getEngine().name||""},t.parseEngine=function(){var e=this;this.parsedResult.engine={};var t=a.default.find(o.default,(function(t){if("function"==typeof t.test)return t.test(e);if(t.test instanceof Array)return t.test.some((function(t){return e.test(t)}));throw new Error("Browser's test function is not valid")}));return t&&(this.parsedResult.engine=t.describe(this.getUA())),this.parsedResult.engine},t.parse=function(){return this.parseBrowser(),this.parseOS(),this.parsePlatform(),this.parseEngine(),this},t.getResult=function(){return a.default.assign({},this.parsedResult)},t.satisfies=function(e){var t=this,r={},n=0,i={},s=0;if(Object.keys(e).forEach((function(t){var o=e[t];"string"==typeof o?(i[t]=o,s+=1):"object"==typeof o&&(r[t]=o,n+=1)})),n>0){var o=Object.keys(r),u=a.default.find(o,(function(e){return t.isOS(e)}));if(u){var d=this.satisfies(r[u]);if(void 0!==d)return d}var c=a.default.find(o,(function(e){return t.isPlatform(e)}));if(c){var f=this.satisfies(r[c]);if(void 0!==f)return f}}if(s>0){var l=Object.keys(i),h=a.default.find(l,(function(e){return t.isBrowser(e,!0)}));if(void 0!==h)return this.compareVersion(i[h])}},t.isBrowser=function(e,t){void 0===t&&(t=!1);var r=this.getBrowserName().toLowerCase(),n=e.toLowerCase(),i=a.default.getBrowserTypeByAlias(n);return t&&i&&(n=i.toLowerCase()),n===r},t.compareVersion=function(e){var t=[0],r=e,n=!1,i=this.getBrowserVersion();if("string"==typeof i)return">"===e[0]||"<"===e[0]?(r=e.substr(1),"="===e[1]?(n=!0,r=e.substr(2)):t=[],">"===e[0]?t.push(1):t.push(-1)):"="===e[0]?r=e.substr(1):"~"===e[0]&&(n=!0,r=e.substr(1)),t.indexOf(a.default.compareVersions(i,r,n))>-1},t.isOS=function(e){return this.getOSName(!0)===String(e).toLowerCase()},t.isPlatform=function(e){return this.getPlatformType(!0)===String(e).toLowerCase()},t.isEngine=function(e){return this.getEngineName(!0)===String(e).toLowerCase()},t.is=function(e){return this.isBrowser(e)||this.isOS(e)||this.isPlatform(e)},t.some=function(e){var t=this;return void 0===e&&(e=[]),e.some((function(e){return t.is(e)}))},e}();t.default=d,e.exports=t.default},92:function(e,t,r){"use strict";t.__esModule=!0,t.default=void 0;var n,i=(n=r(17))&&n.__esModule?n:{default:n};var s=/version\/(\d+(\.?_?\d+)+)/i,o=[{test:[/googlebot/i],describe:function(e){var t={name:"Googlebot"},r=i.default.getFirstMatch(/googlebot\/(\d+(\.\d+))/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/opera/i],describe:function(e){var t={name:"Opera"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/(?:opera)[\s/](\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/opr\/|opios/i],describe:function(e){var t={name:"Opera"},r=i.default.getFirstMatch(/(?:opr|opios)[\s/](\S+)/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/SamsungBrowser/i],describe:function(e){var t={name:"Samsung Internet for Android"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/(?:SamsungBrowser)[\s/](\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/Whale/i],describe:function(e){var t={name:"NAVER Whale Browser"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/(?:whale)[\s/](\d+(?:\.\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/MZBrowser/i],describe:function(e){var t={name:"MZ Browser"},r=i.default.getFirstMatch(/(?:MZBrowser)[\s/](\d+(?:\.\d+)+)/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/focus/i],describe:function(e){var t={name:"Focus"},r=i.default.getFirstMatch(/(?:focus)[\s/](\d+(?:\.\d+)+)/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/swing/i],describe:function(e){var t={name:"Swing"},r=i.default.getFirstMatch(/(?:swing)[\s/](\d+(?:\.\d+)+)/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/coast/i],describe:function(e){var t={name:"Opera Coast"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/(?:coast)[\s/](\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/yabrowser/i],describe:function(e){var t={name:"Yandex Browser"},r=i.default.getFirstMatch(/(?:yabrowser)[\s/](\d+(\.?_?\d+)+)/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/ucbrowser/i],describe:function(e){var t={name:"UC Browser"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/(?:ucbrowser)[\s/](\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/Maxthon|mxios/i],describe:function(e){var t={name:"Maxthon"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/(?:Maxthon|mxios)[\s/](\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/epiphany/i],describe:function(e){var t={name:"Epiphany"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/(?:epiphany)[\s/](\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/puffin/i],describe:function(e){var t={name:"Puffin"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/(?:puffin)[\s/](\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/sleipnir/i],describe:function(e){var t={name:"Sleipnir"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/(?:sleipnir)[\s/](\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/k-meleon/i],describe:function(e){var t={name:"K-Meleon"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/(?:k-meleon)[\s/](\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/micromessenger/i],describe:function(e){var t={name:"WeChat"},r=i.default.getFirstMatch(/(?:micromessenger)[\s/](\d+(\.?_?\d+)+)/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/qqbrowser/i],describe:function(e){var t={name:/qqbrowserlite/i.test(e)?"QQ Browser Lite":"QQ Browser"},r=i.default.getFirstMatch(/(?:qqbrowserlite|qqbrowser)[/](\d+(\.?_?\d+)+)/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/msie|trident/i],describe:function(e){var t={name:"Internet Explorer"},r=i.default.getFirstMatch(/(?:msie |rv:)(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/\sedg\//i],describe:function(e){var t={name:"Microsoft Edge"},r=i.default.getFirstMatch(/\sedg\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/edg([ea]|ios)/i],describe:function(e){var t={name:"Microsoft Edge"},r=i.default.getSecondMatch(/edg([ea]|ios)\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/vivaldi/i],describe:function(e){var t={name:"Vivaldi"},r=i.default.getFirstMatch(/vivaldi\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/seamonkey/i],describe:function(e){var t={name:"SeaMonkey"},r=i.default.getFirstMatch(/seamonkey\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/sailfish/i],describe:function(e){var t={name:"Sailfish"},r=i.default.getFirstMatch(/sailfish\s?browser\/(\d+(\.\d+)?)/i,e);return r&&(t.version=r),t}},{test:[/silk/i],describe:function(e){var t={name:"Amazon Silk"},r=i.default.getFirstMatch(/silk\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/phantom/i],describe:function(e){var t={name:"PhantomJS"},r=i.default.getFirstMatch(/phantomjs\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/slimerjs/i],describe:function(e){var t={name:"SlimerJS"},r=i.default.getFirstMatch(/slimerjs\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/blackberry|\bbb\d+/i,/rim\stablet/i],describe:function(e){var t={name:"BlackBerry"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/blackberry[\d]+\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/(web|hpw)[o0]s/i],describe:function(e){var t={name:"WebOS Browser"},r=i.default.getFirstMatch(s,e)||i.default.getFirstMatch(/w(?:eb)?[o0]sbrowser\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/bada/i],describe:function(e){var t={name:"Bada"},r=i.default.getFirstMatch(/dolfin\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/tizen/i],describe:function(e){var t={name:"Tizen"},r=i.default.getFirstMatch(/(?:tizen\s?)?browser\/(\d+(\.?_?\d+)+)/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/qupzilla/i],describe:function(e){var t={name:"QupZilla"},r=i.default.getFirstMatch(/(?:qupzilla)[\s/](\d+(\.?_?\d+)+)/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/firefox|iceweasel|fxios/i],describe:function(e){var t={name:"Firefox"},r=i.default.getFirstMatch(/(?:firefox|iceweasel|fxios)[\s/](\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/electron/i],describe:function(e){var t={name:"Electron"},r=i.default.getFirstMatch(/(?:electron)\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/chromium/i],describe:function(e){var t={name:"Chromium"},r=i.default.getFirstMatch(/(?:chromium)[\s/](\d+(\.?_?\d+)+)/i,e)||i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/chrome|crios|crmo/i],describe:function(e){var t={name:"Chrome"},r=i.default.getFirstMatch(/(?:chrome|crios|crmo)\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/GSA/i],describe:function(e){var t={name:"Google Search"},r=i.default.getFirstMatch(/(?:GSA)\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:function(e){var t=!e.test(/like android/i),r=e.test(/android/i);return t&&r},describe:function(e){var t={name:"Android Browser"},r=i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/playstation 4/i],describe:function(e){var t={name:"PlayStation 4"},r=i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/safari|applewebkit/i],describe:function(e){var t={name:"Safari"},r=i.default.getFirstMatch(s,e);return r&&(t.version=r),t}},{test:[/.*/i],describe:function(e){var t=-1!==e.search("\\(")?/^(.*)\/(.*)[ \t]\((.*)/:/^(.*)\/(.*) /;return{name:i.default.getFirstMatch(t,e),version:i.default.getSecondMatch(t,e)}}}];t.default=o,e.exports=t.default},93:function(e,t,r){"use strict";t.__esModule=!0,t.default=void 0;var n,i=(n=r(17))&&n.__esModule?n:{default:n},s=r(18);var o=[{test:[/Roku\/DVP/],describe:function(e){var t=i.default.getFirstMatch(/Roku\/DVP-(\d+\.\d+)/i,e);return{name:s.OS_MAP.Roku,version:t}}},{test:[/windows phone/i],describe:function(e){var t=i.default.getFirstMatch(/windows phone (?:os)?\s?(\d+(\.\d+)*)/i,e);return{name:s.OS_MAP.WindowsPhone,version:t}}},{test:[/windows /i],describe:function(e){var t=i.default.getFirstMatch(/Windows ((NT|XP)( \d\d?.\d)?)/i,e),r=i.default.getWindowsVersionName(t);return{name:s.OS_MAP.Windows,version:t,versionName:r}}},{test:[/Macintosh(.*?) FxiOS(.*?) Version\//],describe:function(e){var t=i.default.getSecondMatch(/(Version\/)(\d[\d.]+)/,e);return{name:s.OS_MAP.iOS,version:t}}},{test:[/macintosh/i],describe:function(e){var t=i.default.getFirstMatch(/mac os x (\d+(\.?_?\d+)+)/i,e).replace(/[_\s]/g,"."),r=i.default.getMacOSVersionName(t),n={name:s.OS_MAP.MacOS,version:t};return r&&(n.versionName=r),n}},{test:[/(ipod|iphone|ipad)/i],describe:function(e){var t=i.default.getFirstMatch(/os (\d+([_\s]\d+)*) like mac os x/i,e).replace(/[_\s]/g,".");return{name:s.OS_MAP.iOS,version:t}}},{test:function(e){var t=!e.test(/like android/i),r=e.test(/android/i);return t&&r},describe:function(e){var t=i.default.getFirstMatch(/android[\s/-](\d+(\.\d+)*)/i,e),r=i.default.getAndroidVersionName(t),n={name:s.OS_MAP.Android,version:t};return r&&(n.versionName=r),n}},{test:[/(web|hpw)[o0]s/i],describe:function(e){var t=i.default.getFirstMatch(/(?:web|hpw)[o0]s\/(\d+(\.\d+)*)/i,e),r={name:s.OS_MAP.WebOS};return t&&t.length&&(r.version=t),r}},{test:[/blackberry|\bbb\d+/i,/rim\stablet/i],describe:function(e){var t=i.default.getFirstMatch(/rim\stablet\sos\s(\d+(\.\d+)*)/i,e)||i.default.getFirstMatch(/blackberry\d+\/(\d+([_\s]\d+)*)/i,e)||i.default.getFirstMatch(/\bbb(\d+)/i,e);return{name:s.OS_MAP.BlackBerry,version:t}}},{test:[/bada/i],describe:function(e){var t=i.default.getFirstMatch(/bada\/(\d+(\.\d+)*)/i,e);return{name:s.OS_MAP.Bada,version:t}}},{test:[/tizen/i],describe:function(e){var t=i.default.getFirstMatch(/tizen[/\s](\d+(\.\d+)*)/i,e);return{name:s.OS_MAP.Tizen,version:t}}},{test:[/linux/i],describe:function(){return{name:s.OS_MAP.Linux}}},{test:[/CrOS/],describe:function(){return{name:s.OS_MAP.ChromeOS}}},{test:[/PlayStation 4/],describe:function(e){var t=i.default.getFirstMatch(/PlayStation 4[/\s](\d+(\.\d+)*)/i,e);return{name:s.OS_MAP.PlayStation4,version:t}}}];t.default=o,e.exports=t.default},94:function(e,t,r){"use strict";t.__esModule=!0,t.default=void 0;var n,i=(n=r(17))&&n.__esModule?n:{default:n},s=r(18);var o=[{test:[/googlebot/i],describe:function(){return{type:"bot",vendor:"Google"}}},{test:[/huawei/i],describe:function(e){var t=i.default.getFirstMatch(/(can-l01)/i,e)&&"Nova",r={type:s.PLATFORMS_MAP.mobile,vendor:"Huawei"};return t&&(r.model=t),r}},{test:[/nexus\s*(?:7|8|9|10).*/i],describe:function(){return{type:s.PLATFORMS_MAP.tablet,vendor:"Nexus"}}},{test:[/ipad/i],describe:function(){return{type:s.PLATFORMS_MAP.tablet,vendor:"Apple",model:"iPad"}}},{test:[/Macintosh(.*?) FxiOS(.*?) Version\//],describe:function(){return{type:s.PLATFORMS_MAP.tablet,vendor:"Apple",model:"iPad"}}},{test:[/kftt build/i],describe:function(){return{type:s.PLATFORMS_MAP.tablet,vendor:"Amazon",model:"Kindle Fire HD 7"}}},{test:[/silk/i],describe:function(){return{type:s.PLATFORMS_MAP.tablet,vendor:"Amazon"}}},{test:[/tablet(?! pc)/i],describe:function(){return{type:s.PLATFORMS_MAP.tablet}}},{test:function(e){var t=e.test(/ipod|iphone/i),r=e.test(/like (ipod|iphone)/i);return t&&!r},describe:function(e){var t=i.default.getFirstMatch(/(ipod|iphone)/i,e);return{type:s.PLATFORMS_MAP.mobile,vendor:"Apple",model:t}}},{test:[/nexus\s*[0-6].*/i,/galaxy nexus/i],describe:function(){return{type:s.PLATFORMS_MAP.mobile,vendor:"Nexus"}}},{test:[/[^-]mobi/i],describe:function(){return{type:s.PLATFORMS_MAP.mobile}}},{test:function(e){return"blackberry"===e.getBrowserName(!0)},describe:function(){return{type:s.PLATFORMS_MAP.mobile,vendor:"BlackBerry"}}},{test:function(e){return"bada"===e.getBrowserName(!0)},describe:function(){return{type:s.PLATFORMS_MAP.mobile}}},{test:function(e){return"windows phone"===e.getBrowserName()},describe:function(){return{type:s.PLATFORMS_MAP.mobile,vendor:"Microsoft"}}},{test:function(e){var t=Number(String(e.getOSVersion()).split(".")[0]);return"android"===e.getOSName(!0)&&t>=3},describe:function(){return{type:s.PLATFORMS_MAP.tablet}}},{test:function(e){return"android"===e.getOSName(!0)},describe:function(){return{type:s.PLATFORMS_MAP.mobile}}},{test:function(e){return"macos"===e.getOSName(!0)},describe:function(){return{type:s.PLATFORMS_MAP.desktop,vendor:"Apple"}}},{test:function(e){return"windows"===e.getOSName(!0)},describe:function(){return{type:s.PLATFORMS_MAP.desktop}}},{test:function(e){return"linux"===e.getOSName(!0)},describe:function(){return{type:s.PLATFORMS_MAP.desktop}}},{test:function(e){return"playstation 4"===e.getOSName(!0)},describe:function(){return{type:s.PLATFORMS_MAP.tv}}},{test:function(e){return"roku"===e.getOSName(!0)},describe:function(){return{type:s.PLATFORMS_MAP.tv}}}];t.default=o,e.exports=t.default},95:function(e,t,r){"use strict";t.__esModule=!0,t.default=void 0;var n,i=(n=r(17))&&n.__esModule?n:{default:n},s=r(18);var o=[{test:function(e){return"microsoft edge"===e.getBrowserName(!0)},describe:function(e){if(/\sedg\//i.test(e))return{name:s.ENGINE_MAP.Blink};var t=i.default.getFirstMatch(/edge\/(\d+(\.?_?\d+)+)/i,e);return{name:s.ENGINE_MAP.EdgeHTML,version:t}}},{test:[/trident/i],describe:function(e){var t={name:s.ENGINE_MAP.Trident},r=i.default.getFirstMatch(/trident\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:function(e){return e.test(/presto/i)},describe:function(e){var t={name:s.ENGINE_MAP.Presto},r=i.default.getFirstMatch(/presto\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:function(e){var t=e.test(/gecko/i),r=e.test(/like gecko/i);return t&&!r},describe:function(e){var t={name:s.ENGINE_MAP.Gecko},r=i.default.getFirstMatch(/gecko\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}},{test:[/(apple)?webkit\/537\.36/i],describe:function(){return{name:s.ENGINE_MAP.Blink}}},{test:[/(apple)?webkit/i],describe:function(e){var t={name:s.ENGINE_MAP.WebKit},r=i.default.getFirstMatch(/webkit\/(\d+(\.?_?\d+)+)/i,e);return r&&(t.version=r),t}}];t.default=o,e.exports=t.default}})}));
},{}],34:[function(require,module,exports){
/**
 * Helpers.
 */

var s = 1000;
var m = s * 60;
var h = m * 60;
var d = h * 24;
var w = d * 7;
var y = d * 365.25;

/**
 * Parse or format the given `val`.
 *
 * Options:
 *
 *  - `long` verbose formatting [false]
 *
 * @param {String|Number} val
 * @param {Object} [options]
 * @throws {Error} throw an error if val is not a non-empty string or a number
 * @return {String|Number}
 * @api public
 */

module.exports = function(val, options) {
  options = options || {};
  var type = typeof val;
  if (type === 'string' && val.length > 0) {
    return parse(val);
  } else if (type === 'number' && isFinite(val)) {
    return options.long ? fmtLong(val) : fmtShort(val);
  }
  throw new Error(
    'val is not a non-empty string or a valid number. val=' +
      JSON.stringify(val)
  );
};

/**
 * Parse the given `str` and return milliseconds.
 *
 * @param {String} str
 * @return {Number}
 * @api private
 */

function parse(str) {
  str = String(str);
  if (str.length > 100) {
    return;
  }
  var match = /^(-?(?:\d+)?\.?\d+) *(milliseconds?|msecs?|ms|seconds?|secs?|s|minutes?|mins?|m|hours?|hrs?|h|days?|d|weeks?|w|years?|yrs?|y)?$/i.exec(
    str
  );
  if (!match) {
    return;
  }
  var n = parseFloat(match[1]);
  var type = (match[2] || 'ms').toLowerCase();
  switch (type) {
    case 'years':
    case 'year':
    case 'yrs':
    case 'yr':
    case 'y':
      return n * y;
    case 'weeks':
    case 'week':
    case 'w':
      return n * w;
    case 'days':
    case 'day':
    case 'd':
      return n * d;
    case 'hours':
    case 'hour':
    case 'hrs':
    case 'hr':
    case 'h':
      return n * h;
    case 'minutes':
    case 'minute':
    case 'mins':
    case 'min':
    case 'm':
      return n * m;
    case 'seconds':
    case 'second':
    case 'secs':
    case 'sec':
    case 's':
      return n * s;
    case 'milliseconds':
    case 'millisecond':
    case 'msecs':
    case 'msec':
    case 'ms':
      return n;
    default:
      return undefined;
  }
}

/**
 * Short format for `ms`.
 *
 * @param {Number} ms
 * @return {String}
 * @api private
 */

function fmtShort(ms) {
  var msAbs = Math.abs(ms);
  if (msAbs >= d) {
    return Math.round(ms / d) + 'd';
  }
  if (msAbs >= h) {
    return Math.round(ms / h) + 'h';
  }
  if (msAbs >= m) {
    return Math.round(ms / m) + 'm';
  }
  if (msAbs >= s) {
    return Math.round(ms / s) + 's';
  }
  return ms + 'ms';
}

/**
 * Long format for `ms`.
 *
 * @param {Number} ms
 * @return {String}
 * @api private
 */

function fmtLong(ms) {
  var msAbs = Math.abs(ms);
  if (msAbs >= d) {
    return plural(ms, msAbs, d, 'day');
  }
  if (msAbs >= h) {
    return plural(ms, msAbs, h, 'hour');
  }
  if (msAbs >= m) {
    return plural(ms, msAbs, m, 'minute');
  }
  if (msAbs >= s) {
    return plural(ms, msAbs, s, 'second');
  }
  return ms + ' ms';
}

/**
 * Pluralization helper.
 */

function plural(ms, msAbs, n, name) {
  var isPlural = msAbs >= n * 1.5;
  return Math.round(ms / n) + ' ' + name + (isPlural ? 's' : '');
}

},{}],35:[function(require,module,exports){
(function (process){
/* eslint-env browser */

/**
 * This is the web browser implementation of `debug()`.
 */

exports.log = log;
exports.formatArgs = formatArgs;
exports.save = save;
exports.load = load;
exports.useColors = useColors;
exports.storage = localstorage();

/**
 * Colors.
 */

exports.colors = [
	'#0000CC',
	'#0000FF',
	'#0033CC',
	'#0033FF',
	'#0066CC',
	'#0066FF',
	'#0099CC',
	'#0099FF',
	'#00CC00',
	'#00CC33',
	'#00CC66',
	'#00CC99',
	'#00CCCC',
	'#00CCFF',
	'#3300CC',
	'#3300FF',
	'#3333CC',
	'#3333FF',
	'#3366CC',
	'#3366FF',
	'#3399CC',
	'#3399FF',
	'#33CC00',
	'#33CC33',
	'#33CC66',
	'#33CC99',
	'#33CCCC',
	'#33CCFF',
	'#6600CC',
	'#6600FF',
	'#6633CC',
	'#6633FF',
	'#66CC00',
	'#66CC33',
	'#9900CC',
	'#9900FF',
	'#9933CC',
	'#9933FF',
	'#99CC00',
	'#99CC33',
	'#CC0000',
	'#CC0033',
	'#CC0066',
	'#CC0099',
	'#CC00CC',
	'#CC00FF',
	'#CC3300',
	'#CC3333',
	'#CC3366',
	'#CC3399',
	'#CC33CC',
	'#CC33FF',
	'#CC6600',
	'#CC6633',
	'#CC9900',
	'#CC9933',
	'#CCCC00',
	'#CCCC33',
	'#FF0000',
	'#FF0033',
	'#FF0066',
	'#FF0099',
	'#FF00CC',
	'#FF00FF',
	'#FF3300',
	'#FF3333',
	'#FF3366',
	'#FF3399',
	'#FF33CC',
	'#FF33FF',
	'#FF6600',
	'#FF6633',
	'#FF9900',
	'#FF9933',
	'#FFCC00',
	'#FFCC33'
];

/**
 * Currently only WebKit-based Web Inspectors, Firefox >= v31,
 * and the Firebug extension (any Firefox version) are known
 * to support "%c" CSS customizations.
 *
 * TODO: add a `localStorage` variable to explicitly enable/disable colors
 */

// eslint-disable-next-line complexity
function useColors() {
	// NB: In an Electron preload script, document will be defined but not fully
	// initialized. Since we know we're in Chrome, we'll just detect this case
	// explicitly
	if (typeof window !== 'undefined' && window.process && (window.process.type === 'renderer' || window.process.__nwjs)) {
		return true;
	}

	// Internet Explorer and Edge do not support colors.
	if (typeof navigator !== 'undefined' && navigator.userAgent && navigator.userAgent.toLowerCase().match(/(edge|trident)\/(\d+)/)) {
		return false;
	}

	// Is webkit? http://stackoverflow.com/a/16459606/376773
	// document is undefined in react-native: https://github.com/facebook/react-native/pull/1632
	return (typeof document !== 'undefined' && document.documentElement && document.documentElement.style && document.documentElement.style.WebkitAppearance) ||
		// Is firebug? http://stackoverflow.com/a/398120/376773
		(typeof window !== 'undefined' && window.console && (window.console.firebug || (window.console.exception && window.console.table))) ||
		// Is firefox >= v31?
		// https://developer.mozilla.org/en-US/docs/Tools/Web_Console#Styling_messages
		(typeof navigator !== 'undefined' && navigator.userAgent && navigator.userAgent.toLowerCase().match(/firefox\/(\d+)/) && parseInt(RegExp.$1, 10) >= 31) ||
		// Double check webkit in userAgent just in case we are in a worker
		(typeof navigator !== 'undefined' && navigator.userAgent && navigator.userAgent.toLowerCase().match(/applewebkit\/(\d+)/));
}

/**
 * Colorize log arguments if enabled.
 *
 * @api public
 */

function formatArgs(args) {
	args[0] = (this.useColors ? '%c' : '') +
		this.namespace +
		(this.useColors ? ' %c' : ' ') +
		args[0] +
		(this.useColors ? '%c ' : ' ') +
		'+' + module.exports.humanize(this.diff);

	if (!this.useColors) {
		return;
	}

	const c = 'color: ' + this.color;
	args.splice(1, 0, c, 'color: inherit');

	// The final "%c" is somewhat tricky, because there could be other
	// arguments passed either before or after the %c, so we need to
	// figure out the correct index to insert the CSS into
	let index = 0;
	let lastC = 0;
	args[0].replace(/%[a-zA-Z%]/g, match => {
		if (match === '%%') {
			return;
		}
		index++;
		if (match === '%c') {
			// We only are interested in the *last* %c
			// (the user may have provided their own)
			lastC = index;
		}
	});

	args.splice(lastC, 0, c);
}

/**
 * Invokes `console.log()` when available.
 * No-op when `console.log` is not a "function".
 *
 * @api public
 */
function log(...args) {
	// This hackery is required for IE8/9, where
	// the `console.log` function doesn't have 'apply'
	return typeof console === 'object' &&
		console.log &&
		console.log(...args);
}

/**
 * Save `namespaces`.
 *
 * @param {String} namespaces
 * @api private
 */
function save(namespaces) {
	try {
		if (namespaces) {
			exports.storage.setItem('debug', namespaces);
		} else {
			exports.storage.removeItem('debug');
		}
	} catch (error) {
		// Swallow
		// XXX (@Qix-) should we be logging these?
	}
}

/**
 * Load `namespaces`.
 *
 * @return {String} returns the previously persisted debug modes
 * @api private
 */
function load() {
	let r;
	try {
		r = exports.storage.getItem('debug');
	} catch (error) {
		// Swallow
		// XXX (@Qix-) should we be logging these?
	}

	// If debug isn't set in LS, and we're in Electron, try to load $DEBUG
	if (!r && typeof process !== 'undefined' && 'env' in process) {
		r = process.env.DEBUG;
	}

	return r;
}

/**
 * Localstorage attempts to return the localstorage.
 *
 * This is necessary because safari throws
 * when a user disables cookies/localstorage
 * and you attempt to access it.
 *
 * @return {LocalStorage}
 * @api private
 */

function localstorage() {
	try {
		// TVMLKit (Apple TV JS Runtime) does not have a window object, just localStorage in the global context
		// The Browser also has localStorage in the global context.
		return localStorage;
	} catch (error) {
		// Swallow
		// XXX (@Qix-) should we be logging these?
	}
}

module.exports = require('./common')(exports);

const {formatters} = module.exports;

/**
 * Map %j to `JSON.stringify()`, since no Web Inspectors do that by default.
 */

formatters.j = function (v) {
	try {
		return JSON.stringify(v);
	} catch (error) {
		return '[UnexpectedJSONParseError]: ' + error.message;
	}
};

}).call(this,require('_process'))
},{"./common":36,"_process":69}],36:[function(require,module,exports){

/**
 * This is the common logic for both the Node.js and web browser
 * implementations of `debug()`.
 */

function setup(env) {
	createDebug.debug = createDebug;
	createDebug.default = createDebug;
	createDebug.coerce = coerce;
	createDebug.disable = disable;
	createDebug.enable = enable;
	createDebug.enabled = enabled;
	createDebug.humanize = require('ms');

	Object.keys(env).forEach(key => {
		createDebug[key] = env[key];
	});

	/**
	* Active `debug` instances.
	*/
	createDebug.instances = [];

	/**
	* The currently active debug mode names, and names to skip.
	*/

	createDebug.names = [];
	createDebug.skips = [];

	/**
	* Map of special "%n" handling functions, for the debug "format" argument.
	*
	* Valid key names are a single, lower or upper-case letter, i.e. "n" and "N".
	*/
	createDebug.formatters = {};

	/**
	* Selects a color for a debug namespace
	* @param {String} namespace The namespace string for the for the debug instance to be colored
	* @return {Number|String} An ANSI color code for the given namespace
	* @api private
	*/
	function selectColor(namespace) {
		let hash = 0;

		for (let i = 0; i < namespace.length; i++) {
			hash = ((hash << 5) - hash) + namespace.charCodeAt(i);
			hash |= 0; // Convert to 32bit integer
		}

		return createDebug.colors[Math.abs(hash) % createDebug.colors.length];
	}
	createDebug.selectColor = selectColor;

	/**
	* Create a debugger with the given `namespace`.
	*
	* @param {String} namespace
	* @return {Function}
	* @api public
	*/
	function createDebug(namespace) {
		let prevTime;

		function debug(...args) {
			// Disabled?
			if (!debug.enabled) {
				return;
			}

			const self = debug;

			// Set `diff` timestamp
			const curr = Number(new Date());
			const ms = curr - (prevTime || curr);
			self.diff = ms;
			self.prev = prevTime;
			self.curr = curr;
			prevTime = curr;

			args[0] = createDebug.coerce(args[0]);

			if (typeof args[0] !== 'string') {
				// Anything else let's inspect with %O
				args.unshift('%O');
			}

			// Apply any `formatters` transformations
			let index = 0;
			args[0] = args[0].replace(/%([a-zA-Z%])/g, (match, format) => {
				// If we encounter an escaped % then don't increase the array index
				if (match === '%%') {
					return match;
				}
				index++;
				const formatter = createDebug.formatters[format];
				if (typeof formatter === 'function') {
					const val = args[index];
					match = formatter.call(self, val);

					// Now we need to remove `args[index]` since it's inlined in the `format`
					args.splice(index, 1);
					index--;
				}
				return match;
			});

			// Apply env-specific formatting (colors, etc.)
			createDebug.formatArgs.call(self, args);

			const logFn = self.log || createDebug.log;
			logFn.apply(self, args);
		}

		debug.namespace = namespace;
		debug.enabled = createDebug.enabled(namespace);
		debug.useColors = createDebug.useColors();
		debug.color = selectColor(namespace);
		debug.destroy = destroy;
		debug.extend = extend;
		// Debug.formatArgs = formatArgs;
		// debug.rawLog = rawLog;

		// env-specific initialization logic for debug instances
		if (typeof createDebug.init === 'function') {
			createDebug.init(debug);
		}

		createDebug.instances.push(debug);

		return debug;
	}

	function destroy() {
		const index = createDebug.instances.indexOf(this);
		if (index !== -1) {
			createDebug.instances.splice(index, 1);
			return true;
		}
		return false;
	}

	function extend(namespace, delimiter) {
		const newDebug = createDebug(this.namespace + (typeof delimiter === 'undefined' ? ':' : delimiter) + namespace);
		newDebug.log = this.log;
		return newDebug;
	}

	/**
	* Enables a debug mode by namespaces. This can include modes
	* separated by a colon and wildcards.
	*
	* @param {String} namespaces
	* @api public
	*/
	function enable(namespaces) {
		createDebug.save(namespaces);

		createDebug.names = [];
		createDebug.skips = [];

		let i;
		const split = (typeof namespaces === 'string' ? namespaces : '').split(/[\s,]+/);
		const len = split.length;

		for (i = 0; i < len; i++) {
			if (!split[i]) {
				// ignore empty strings
				continue;
			}

			namespaces = split[i].replace(/\*/g, '.*?');

			if (namespaces[0] === '-') {
				createDebug.skips.push(new RegExp('^' + namespaces.substr(1) + '$'));
			} else {
				createDebug.names.push(new RegExp('^' + namespaces + '$'));
			}
		}

		for (i = 0; i < createDebug.instances.length; i++) {
			const instance = createDebug.instances[i];
			instance.enabled = createDebug.enabled(instance.namespace);
		}
	}

	/**
	* Disable debug output.
	*
	* @return {String} namespaces
	* @api public
	*/
	function disable() {
		const namespaces = [
			...createDebug.names.map(toNamespace),
			...createDebug.skips.map(toNamespace).map(namespace => '-' + namespace)
		].join(',');
		createDebug.enable('');
		return namespaces;
	}

	/**
	* Returns true if the given mode name is enabled, false otherwise.
	*
	* @param {String} name
	* @return {Boolean}
	* @api public
	*/
	function enabled(name) {
		if (name[name.length - 1] === '*') {
			return true;
		}

		let i;
		let len;

		for (i = 0, len = createDebug.skips.length; i < len; i++) {
			if (createDebug.skips[i].test(name)) {
				return false;
			}
		}

		for (i = 0, len = createDebug.names.length; i < len; i++) {
			if (createDebug.names[i].test(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	* Convert regexp to namespace
	*
	* @param {RegExp} regxep
	* @return {String} namespace
	* @api private
	*/
	function toNamespace(regexp) {
		return regexp.toString()
			.substring(2, regexp.toString().length - 2)
			.replace(/\.\*\?$/, '*');
	}

	/**
	* Coerce `val`.
	*
	* @param {Mixed} val
	* @return {Mixed}
	* @api private
	*/
	function coerce(val) {
		if (val instanceof Error) {
			return val.stack || val.message;
		}
		return val;
	}

	createDebug.enable(createDebug.load());

	return createDebug;
}

module.exports = setup;

},{"ms":34}],37:[function(require,module,exports){
// Copyright Joyent, Inc. and other Node contributors.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.

var objectCreate = Object.create || objectCreatePolyfill;
var objectKeys = Object.keys || objectKeysPolyfill;
var bind = Function.prototype.bind || functionBindPolyfill;

function EventEmitter() {
  if (!this._events || !Object.prototype.hasOwnProperty.call(this, '_events')) {
    this._events = objectCreate(null);
    this._eventsCount = 0;
  }

  this._maxListeners = this._maxListeners || undefined;
}
module.exports = EventEmitter;

// Backwards-compat with node 0.10.x
EventEmitter.EventEmitter = EventEmitter;

EventEmitter.prototype._events = undefined;
EventEmitter.prototype._maxListeners = undefined;

// By default EventEmitters will print a warning if more than 10 listeners are
// added to it. This is a useful default which helps finding memory leaks.
var defaultMaxListeners = 10;

var hasDefineProperty;
try {
  var o = {};
  if (Object.defineProperty) Object.defineProperty(o, 'x', { value: 0 });
  hasDefineProperty = o.x === 0;
} catch (err) { hasDefineProperty = false }
if (hasDefineProperty) {
  Object.defineProperty(EventEmitter, 'defaultMaxListeners', {
    enumerable: true,
    get: function() {
      return defaultMaxListeners;
    },
    set: function(arg) {
      // check whether the input is a positive number (whose value is zero or
      // greater and not a NaN).
      if (typeof arg !== 'number' || arg < 0 || arg !== arg)
        throw new TypeError('"defaultMaxListeners" must be a positive number');
      defaultMaxListeners = arg;
    }
  });
} else {
  EventEmitter.defaultMaxListeners = defaultMaxListeners;
}

// Obviously not all Emitters should be limited to 10. This function allows
// that to be increased. Set to zero for unlimited.
EventEmitter.prototype.setMaxListeners = function setMaxListeners(n) {
  if (typeof n !== 'number' || n < 0 || isNaN(n))
    throw new TypeError('"n" argument must be a positive number');
  this._maxListeners = n;
  return this;
};

function $getMaxListeners(that) {
  if (that._maxListeners === undefined)
    return EventEmitter.defaultMaxListeners;
  return that._maxListeners;
}

EventEmitter.prototype.getMaxListeners = function getMaxListeners() {
  return $getMaxListeners(this);
};

// These standalone emit* functions are used to optimize calling of event
// handlers for fast cases because emit() itself often has a variable number of
// arguments and can be deoptimized because of that. These functions always have
// the same number of arguments and thus do not get deoptimized, so the code
// inside them can execute faster.
function emitNone(handler, isFn, self) {
  if (isFn)
    handler.call(self);
  else {
    var len = handler.length;
    var listeners = arrayClone(handler, len);
    for (var i = 0; i < len; ++i)
      listeners[i].call(self);
  }
}
function emitOne(handler, isFn, self, arg1) {
  if (isFn)
    handler.call(self, arg1);
  else {
    var len = handler.length;
    var listeners = arrayClone(handler, len);
    for (var i = 0; i < len; ++i)
      listeners[i].call(self, arg1);
  }
}
function emitTwo(handler, isFn, self, arg1, arg2) {
  if (isFn)
    handler.call(self, arg1, arg2);
  else {
    var len = handler.length;
    var listeners = arrayClone(handler, len);
    for (var i = 0; i < len; ++i)
      listeners[i].call(self, arg1, arg2);
  }
}
function emitThree(handler, isFn, self, arg1, arg2, arg3) {
  if (isFn)
    handler.call(self, arg1, arg2, arg3);
  else {
    var len = handler.length;
    var listeners = arrayClone(handler, len);
    for (var i = 0; i < len; ++i)
      listeners[i].call(self, arg1, arg2, arg3);
  }
}

function emitMany(handler, isFn, self, args) {
  if (isFn)
    handler.apply(self, args);
  else {
    var len = handler.length;
    var listeners = arrayClone(handler, len);
    for (var i = 0; i < len; ++i)
      listeners[i].apply(self, args);
  }
}

EventEmitter.prototype.emit = function emit(type) {
  var er, handler, len, args, i, events;
  var doError = (type === 'error');

  events = this._events;
  if (events)
    doError = (doError && events.error == null);
  else if (!doError)
    return false;

  // If there is no 'error' event listener then throw.
  if (doError) {
    if (arguments.length > 1)
      er = arguments[1];
    if (er instanceof Error) {
      throw er; // Unhandled 'error' event
    } else {
      // At least give some kind of context to the user
      var err = new Error('Unhandled "error" event. (' + er + ')');
      err.context = er;
      throw err;
    }
    return false;
  }

  handler = events[type];

  if (!handler)
    return false;

  var isFn = typeof handler === 'function';
  len = arguments.length;
  switch (len) {
      // fast cases
    case 1:
      emitNone(handler, isFn, this);
      break;
    case 2:
      emitOne(handler, isFn, this, arguments[1]);
      break;
    case 3:
      emitTwo(handler, isFn, this, arguments[1], arguments[2]);
      break;
    case 4:
      emitThree(handler, isFn, this, arguments[1], arguments[2], arguments[3]);
      break;
      // slower
    default:
      args = new Array(len - 1);
      for (i = 1; i < len; i++)
        args[i - 1] = arguments[i];
      emitMany(handler, isFn, this, args);
  }

  return true;
};

function _addListener(target, type, listener, prepend) {
  var m;
  var events;
  var existing;

  if (typeof listener !== 'function')
    throw new TypeError('"listener" argument must be a function');

  events = target._events;
  if (!events) {
    events = target._events = objectCreate(null);
    target._eventsCount = 0;
  } else {
    // To avoid recursion in the case that type === "newListener"! Before
    // adding it to the listeners, first emit "newListener".
    if (events.newListener) {
      target.emit('newListener', type,
          listener.listener ? listener.listener : listener);

      // Re-assign `events` because a newListener handler could have caused the
      // this._events to be assigned to a new object
      events = target._events;
    }
    existing = events[type];
  }

  if (!existing) {
    // Optimize the case of one listener. Don't need the extra array object.
    existing = events[type] = listener;
    ++target._eventsCount;
  } else {
    if (typeof existing === 'function') {
      // Adding the second element, need to change to array.
      existing = events[type] =
          prepend ? [listener, existing] : [existing, listener];
    } else {
      // If we've already got an array, just append.
      if (prepend) {
        existing.unshift(listener);
      } else {
        existing.push(listener);
      }
    }

    // Check for listener leak
    if (!existing.warned) {
      m = $getMaxListeners(target);
      if (m && m > 0 && existing.length > m) {
        existing.warned = true;
        var w = new Error('Possible EventEmitter memory leak detected. ' +
            existing.length + ' "' + String(type) + '" listeners ' +
            'added. Use emitter.setMaxListeners() to ' +
            'increase limit.');
        w.name = 'MaxListenersExceededWarning';
        w.emitter = target;
        w.type = type;
        w.count = existing.length;
        if (typeof console === 'object' && console.warn) {
          console.warn('%s: %s', w.name, w.message);
        }
      }
    }
  }

  return target;
}

EventEmitter.prototype.addListener = function addListener(type, listener) {
  return _addListener(this, type, listener, false);
};

EventEmitter.prototype.on = EventEmitter.prototype.addListener;

EventEmitter.prototype.prependListener =
    function prependListener(type, listener) {
      return _addListener(this, type, listener, true);
    };

function onceWrapper() {
  if (!this.fired) {
    this.target.removeListener(this.type, this.wrapFn);
    this.fired = true;
    switch (arguments.length) {
      case 0:
        return this.listener.call(this.target);
      case 1:
        return this.listener.call(this.target, arguments[0]);
      case 2:
        return this.listener.call(this.target, arguments[0], arguments[1]);
      case 3:
        return this.listener.call(this.target, arguments[0], arguments[1],
            arguments[2]);
      default:
        var args = new Array(arguments.length);
        for (var i = 0; i < args.length; ++i)
          args[i] = arguments[i];
        this.listener.apply(this.target, args);
    }
  }
}

function _onceWrap(target, type, listener) {
  var state = { fired: false, wrapFn: undefined, target: target, type: type, listener: listener };
  var wrapped = bind.call(onceWrapper, state);
  wrapped.listener = listener;
  state.wrapFn = wrapped;
  return wrapped;
}

EventEmitter.prototype.once = function once(type, listener) {
  if (typeof listener !== 'function')
    throw new TypeError('"listener" argument must be a function');
  this.on(type, _onceWrap(this, type, listener));
  return this;
};

EventEmitter.prototype.prependOnceListener =
    function prependOnceListener(type, listener) {
      if (typeof listener !== 'function')
        throw new TypeError('"listener" argument must be a function');
      this.prependListener(type, _onceWrap(this, type, listener));
      return this;
    };

// Emits a 'removeListener' event if and only if the listener was removed.
EventEmitter.prototype.removeListener =
    function removeListener(type, listener) {
      var list, events, position, i, originalListener;

      if (typeof listener !== 'function')
        throw new TypeError('"listener" argument must be a function');

      events = this._events;
      if (!events)
        return this;

      list = events[type];
      if (!list)
        return this;

      if (list === listener || list.listener === listener) {
        if (--this._eventsCount === 0)
          this._events = objectCreate(null);
        else {
          delete events[type];
          if (events.removeListener)
            this.emit('removeListener', type, list.listener || listener);
        }
      } else if (typeof list !== 'function') {
        position = -1;

        for (i = list.length - 1; i >= 0; i--) {
          if (list[i] === listener || list[i].listener === listener) {
            originalListener = list[i].listener;
            position = i;
            break;
          }
        }

        if (position < 0)
          return this;

        if (position === 0)
          list.shift();
        else
          spliceOne(list, position);

        if (list.length === 1)
          events[type] = list[0];

        if (events.removeListener)
          this.emit('removeListener', type, originalListener || listener);
      }

      return this;
    };

EventEmitter.prototype.removeAllListeners =
    function removeAllListeners(type) {
      var listeners, events, i;

      events = this._events;
      if (!events)
        return this;

      // not listening for removeListener, no need to emit
      if (!events.removeListener) {
        if (arguments.length === 0) {
          this._events = objectCreate(null);
          this._eventsCount = 0;
        } else if (events[type]) {
          if (--this._eventsCount === 0)
            this._events = objectCreate(null);
          else
            delete events[type];
        }
        return this;
      }

      // emit removeListener for all listeners on all events
      if (arguments.length === 0) {
        var keys = objectKeys(events);
        var key;
        for (i = 0; i < keys.length; ++i) {
          key = keys[i];
          if (key === 'removeListener') continue;
          this.removeAllListeners(key);
        }
        this.removeAllListeners('removeListener');
        this._events = objectCreate(null);
        this._eventsCount = 0;
        return this;
      }

      listeners = events[type];

      if (typeof listeners === 'function') {
        this.removeListener(type, listeners);
      } else if (listeners) {
        // LIFO order
        for (i = listeners.length - 1; i >= 0; i--) {
          this.removeListener(type, listeners[i]);
        }
      }

      return this;
    };

function _listeners(target, type, unwrap) {
  var events = target._events;

  if (!events)
    return [];

  var evlistener = events[type];
  if (!evlistener)
    return [];

  if (typeof evlistener === 'function')
    return unwrap ? [evlistener.listener || evlistener] : [evlistener];

  return unwrap ? unwrapListeners(evlistener) : arrayClone(evlistener, evlistener.length);
}

EventEmitter.prototype.listeners = function listeners(type) {
  return _listeners(this, type, true);
};

EventEmitter.prototype.rawListeners = function rawListeners(type) {
  return _listeners(this, type, false);
};

EventEmitter.listenerCount = function(emitter, type) {
  if (typeof emitter.listenerCount === 'function') {
    return emitter.listenerCount(type);
  } else {
    return listenerCount.call(emitter, type);
  }
};

EventEmitter.prototype.listenerCount = listenerCount;
function listenerCount(type) {
  var events = this._events;

  if (events) {
    var evlistener = events[type];

    if (typeof evlistener === 'function') {
      return 1;
    } else if (evlistener) {
      return evlistener.length;
    }
  }

  return 0;
}

EventEmitter.prototype.eventNames = function eventNames() {
  return this._eventsCount > 0 ? Reflect.ownKeys(this._events) : [];
};

// About 1.5x faster than the two-arg version of Array#splice().
function spliceOne(list, index) {
  for (var i = index, k = i + 1, n = list.length; k < n; i += 1, k += 1)
    list[i] = list[k];
  list.pop();
}

function arrayClone(arr, n) {
  var copy = new Array(n);
  for (var i = 0; i < n; ++i)
    copy[i] = arr[i];
  return copy;
}

function unwrapListeners(arr) {
  var ret = new Array(arr.length);
  for (var i = 0; i < ret.length; ++i) {
    ret[i] = arr[i].listener || arr[i];
  }
  return ret;
}

function objectCreatePolyfill(proto) {
  var F = function() {};
  F.prototype = proto;
  return new F;
}
function objectKeysPolyfill(obj) {
  var keys = [];
  for (var k in obj) if (Object.prototype.hasOwnProperty.call(obj, k)) {
    keys.push(k);
  }
  return k;
}
function functionBindPolyfill(context) {
  var fn = this;
  return function () {
    return fn.apply(context, arguments);
  };
}

},{}],38:[function(require,module,exports){
const debug = require('debug')('h264-profile-level-id');

/* eslint-disable no-console */
debug.log = console.info.bind(console);
/* eslint-enable no-console */

const ProfileConstrainedBaseline = 1;
const ProfileBaseline = 2;
const ProfileMain = 3;
const ProfileConstrainedHigh = 4;
const ProfileHigh = 5;

exports.ProfileConstrainedBaseline = ProfileConstrainedBaseline;
exports.ProfileBaseline = ProfileBaseline;
exports.ProfileMain = ProfileMain;
exports.ProfileConstrainedHigh = ProfileConstrainedHigh;
exports.ProfileHigh = ProfileHigh;

// All values are equal to ten times the level number, except level 1b which is
// special.
const Level1_b = 0;
const Level1 = 10;
const Level1_1 = 11;
const Level1_2 = 12;
const Level1_3 = 13;
const Level2 = 20;
const Level2_1 = 21;
const Level2_2 = 22;
const Level3 = 30;
const Level3_1 = 31;
const Level3_2 = 32;
const Level4 = 40;
const Level4_1 = 41;
const Level4_2 = 42;
const Level5 = 50;
const Level5_1 = 51;
const Level5_2 = 52;

exports.Level1_b = Level1_b;
exports.Level1 = Level1;
exports.Level1_1 = Level1_1;
exports.Level1_2 = Level1_2;
exports.Level1_3 = Level1_3;
exports.Level2 = Level2;
exports.Level2_1 = Level2_1;
exports.Level2_2 = Level2_2;
exports.Level3 = Level3;
exports.Level3_1 = Level3_1;
exports.Level3_2 = Level3_2;
exports.Level4 = Level4;
exports.Level4_1 = Level4_1;
exports.Level4_2 = Level4_2;
exports.Level5 = Level5;
exports.Level5_1 = Level5_1;
exports.Level5_2 = Level5_2;

class ProfileLevelId
{
	constructor(profile, level)
	{
		this.profile = profile;
		this.level = level;
	}
}

exports.ProfileLevelId = ProfileLevelId;

// Default ProfileLevelId.
//
// TODO: The default should really be profile Baseline and level 1 according to
// the spec: https://tools.ietf.org/html/rfc6184#section-8.1. In order to not
// break backwards compatibility with older versions of WebRTC where external
// codecs don't have any parameters, use profile ConstrainedBaseline level 3_1
// instead. This workaround will only be done in an interim period to allow
// external clients to update their code.
//
// http://crbug/webrtc/6337.
const DefaultProfileLevelId =
	new ProfileLevelId(ProfileConstrainedBaseline, Level3_1);

// For level_idc=11 and profile_idc=0x42, 0x4D, or 0x58, the constraint set3
// flag specifies if level 1b or level 1.1 is used.
const ConstraintSet3Flag = 0x10;

// Class for matching bit patterns such as "x1xx0000" where 'x' is allowed to be
// either 0 or 1.
class BitPattern
{
	constructor(str)
	{
		this._mask = ~byteMaskString('x', str);
		this._maskedValue = byteMaskString('1', str);
	}

	isMatch(value)
	{
		return this._maskedValue === (value & this._mask);
	}
}

// Class for converting between profile_idc/profile_iop to Profile.
class ProfilePattern
{
	constructor(profile_idc, profile_iop, profile)
	{
		this.profile_idc = profile_idc;
		this.profile_iop = profile_iop;
		this.profile = profile;
	}
}

// This is from https://tools.ietf.org/html/rfc6184#section-8.1.
const ProfilePatterns =
[
	new ProfilePattern(0x42, new BitPattern('x1xx0000'), ProfileConstrainedBaseline),
	new ProfilePattern(0x4D, new BitPattern('1xxx0000'), ProfileConstrainedBaseline),
	new ProfilePattern(0x58, new BitPattern('11xx0000'), ProfileConstrainedBaseline),
	new ProfilePattern(0x42, new BitPattern('x0xx0000'), ProfileBaseline),
	new ProfilePattern(0x58, new BitPattern('10xx0000'), ProfileBaseline),
	new ProfilePattern(0x4D, new BitPattern('0x0x0000'), ProfileMain),
	new ProfilePattern(0x64, new BitPattern('00000000'), ProfileHigh),
	new ProfilePattern(0x64, new BitPattern('00001100'), ProfileConstrainedHigh)
];

/**
 * Parse profile level id that is represented as a string of 3 hex bytes.
 * Nothing will be returned if the string is not a recognized H264 profile
 * level id.
 *
 * @param {String} str - profile-level-id value as a string of 3 hex bytes.
 *
 * @returns {ProfileLevelId}
 */
exports.parseProfileLevelId = function(str)
{
	// The string should consist of 3 bytes in hexadecimal format.
	if (typeof str !== 'string' || str.length !== 6)
		return null;

	const profile_level_id_numeric = parseInt(str, 16);

	if (profile_level_id_numeric === 0)
		return null;

	// Separate into three bytes.
	const level_idc = profile_level_id_numeric & 0xFF;
	const profile_iop = (profile_level_id_numeric >> 8) & 0xFF;
	const profile_idc = (profile_level_id_numeric >> 16) & 0xFF;

	// Parse level based on level_idc and constraint set 3 flag.
	let level;

	switch (level_idc)
	{
		case Level1_1:
		{
			level = (profile_iop & ConstraintSet3Flag) !== 0 ? Level1_b : Level1_1;
			break;
		}
		case Level1:
		case Level1_2:
		case Level1_3:
		case Level2:
		case Level2_1:
		case Level2_2:
		case Level3:
		case Level3_1:
		case Level3_2:
		case Level4:
		case Level4_1:
		case Level4_2:
		case Level5:
		case Level5_1:
		case Level5_2:
		{
			level = level_idc;
			break;
		}
		// Unrecognized level_idc.
		default:
		{
			debug('parseProfileLevelId() | unrecognized level_idc:%s', level_idc);

			return null;
		}
	}

	// Parse profile_idc/profile_iop into a Profile enum.
	for (const pattern of ProfilePatterns)
	{
		if (
			profile_idc === pattern.profile_idc &&
			pattern.profile_iop.isMatch(profile_iop)
		)
		{
			return new ProfileLevelId(pattern.profile, level);
		}
	}

	debug('parseProfileLevelId() | unrecognized profile_idc/profile_iop combination');

	return null;
};

/**
 * Returns canonical string representation as three hex bytes of the profile
 * level id, or returns nothing for invalid profile level ids.
 *
 * @param {ProfileLevelId} profile_level_id
 *
 * @returns {String}
 */
exports.profileLevelIdToString = function(profile_level_id)
{
	// Handle special case level == 1b.
	if (profile_level_id.level == Level1_b)
	{
		switch (profile_level_id.profile)
		{
			case ProfileConstrainedBaseline:
			{
				return '42f00b';
			}
			case ProfileBaseline:
			{
				return '42100b';
			}
			case ProfileMain:
			{
				return '4d100b';
			}
			// Level 1_b is not allowed for other profiles.
			default:
			{
				debug(
					'profileLevelIdToString() | Level 1_b not is allowed for profile:%s',
					profile_level_id.profile);

				return null;
			}
		}
	}

	let profile_idc_iop_string;

	switch (profile_level_id.profile)
	{
		case ProfileConstrainedBaseline:
		{
			profile_idc_iop_string = '42e0';
			break;
		}
		case ProfileBaseline:
		{
			profile_idc_iop_string = '4200';
			break;
		}
		case ProfileMain:
		{
			profile_idc_iop_string = '4d00';
			break;
		}
		case ProfileConstrainedHigh:
		{
			profile_idc_iop_string = '640c';
			break;
		}
		case ProfileHigh:
		{
			profile_idc_iop_string = '6400';
			break;
		}
		default:
		{
			debug(
				'profileLevelIdToString() | unrecognized profile:%s',
				profile_level_id.profile);

			return null;
		}
	}

	let levelStr = (profile_level_id.level).toString(16);

	if (levelStr.length === 1)
		levelStr = `0${levelStr}`;

	return `${profile_idc_iop_string}${levelStr}`;
};

/**
 * Parse profile level id that is represented as a string of 3 hex bytes
 * contained in an SDP key-value map. A default profile level id will be
 * returned if the profile-level-id key is missing. Nothing will be returned if
 * the key is present but the string is invalid.
 *
 * @param {Object} [params={}] - Codec parameters object.
 *
 * @returns {ProfileLevelId}
 */
exports.parseSdpProfileLevelId = function(params = {})
{
	const profile_level_id = params['profile-level-id'];

	return !profile_level_id
		? DefaultProfileLevelId
		: exports.parseProfileLevelId(profile_level_id);
};

/**
 * Returns true if the parameters have the same H264 profile, i.e. the same
 * H264 profile (Baseline, High, etc).
 *
 * @param {Object} [params1={}] - Codec parameters object.
 * @param {Object} [params2={}] - Codec parameters object.
 *
 * @returns {Boolean}
 */
exports.isSameProfile = function(params1 = {}, params2 = {})
{
	const profile_level_id_1 = exports.parseSdpProfileLevelId(params1);
	const profile_level_id_2 = exports.parseSdpProfileLevelId(params2);

	// Compare H264 profiles, but not levels.
	return Boolean(
		profile_level_id_1 &&
		profile_level_id_2 &&
		profile_level_id_1.profile === profile_level_id_2.profile
	);
};

/**
 * Generate codec parameters that will be used as answer in an SDP negotiation
 * based on local supported parameters and remote offered parameters. Both
 * local_supported_params and remote_offered_params represent sendrecv media
 * descriptions, i.e they are a mix of both encode and decode capabilities. In
 * theory, when the profile in local_supported_params represent a strict superset
 * of the profile in remote_offered_params, we could limit the profile in the
 * answer to the profile in remote_offered_params.
 *
 * However, to simplify the code, each supported H264 profile should be listed
 * explicitly in the list of local supported codecs, even if they are redundant.
 * Then each local codec in the list should be tested one at a time against the
 * remote codec, and only when the profiles are equal should this function be
 * called. Therefore, this function does not need to handle profile intersection,
 * and the profile of local_supported_params and remote_offered_params must be
 * equal before calling this function. The parameters that are used when
 * negotiating are the level part of profile-level-id and level-asymmetry-allowed.
 *
 * @param {Object} [local_supported_params={}]
 * @param {Object} [remote_offered_params={}]
 *
 * @returns {String} Canonical string representation as three hex bytes of the
 *   profile level id, or null if no one of the params have profile-level-id.
 *
 * @throws {TypeError} If Profile mismatch or invalid params.
 */
exports.generateProfileLevelIdForAnswer = function(
	local_supported_params = {},
	remote_offered_params = {}
)
{
	// If both local and remote params do not contain profile-level-id, they are
	// both using the default profile. In this case, don't return anything.
	if (
		!local_supported_params['profile-level-id'] &&
		!remote_offered_params['profile-level-id']
	)
	{
		debug(
			'generateProfileLevelIdForAnswer() | no profile-level-id in local and remote params');

		return null;
	}

	// Parse profile-level-ids.
	const local_profile_level_id =
		exports.parseSdpProfileLevelId(local_supported_params);
	const remote_profile_level_id =
		exports.parseSdpProfileLevelId(remote_offered_params);

	// The local and remote codec must have valid and equal H264 Profiles.
	if (!local_profile_level_id)
		throw new TypeError('invalid local_profile_level_id');

	if (!remote_profile_level_id)
		throw new TypeError('invalid remote_profile_level_id');

	if (local_profile_level_id.profile !== remote_profile_level_id.profile)
		throw new TypeError('H264 Profile mismatch');

	// Parse level information.
	const level_asymmetry_allowed = (
		isLevelAsymmetryAllowed(local_supported_params) &&
		isLevelAsymmetryAllowed(remote_offered_params)
	);

	const local_level = local_profile_level_id.level;
	const remote_level = remote_profile_level_id.level;
	const min_level = minLevel(local_level, remote_level);

	// Determine answer level. When level asymmetry is not allowed, level upgrade
	// is not allowed, i.e., the level in the answer must be equal to or lower
	// than the level in the offer.
	const answer_level = level_asymmetry_allowed ? local_level : min_level;

	debug(
		'generateProfileLevelIdForAnswer() | result: [profile:%s, level:%s]',
		local_profile_level_id.profile, answer_level);

	// Return the resulting profile-level-id for the answer parameters.
	return exports.profileLevelIdToString(
		new ProfileLevelId(local_profile_level_id.profile, answer_level));
};

// Convert a string of 8 characters into a byte where the positions containing
// character c will have their bit set. For example, c = 'x', str = "x1xx0000"
// will return 0b10110000.
function byteMaskString(c, str)
{
	return (
		((str[0] === c) << 7) | ((str[1] === c) << 6) | ((str[2] === c) << 5) |
		((str[3] === c) << 4)	| ((str[4] === c) << 3)	| ((str[5] === c) << 2)	|
		((str[6] === c) << 1)	| ((str[7] === c) << 0)
	);
}

// Compare H264 levels and handle the level 1b case.
function isLessLevel(a, b)
{
	if (a === Level1_b)
		return b !== Level1 && b !== Level1_b;

	if (b === Level1_b)
		return a !== Level1;

	return a < b;
}

function minLevel(a, b)
{
	return isLessLevel(a, b) ? a : b;
}

function isLevelAsymmetryAllowed(params = {})
{
	const level_asymmetry_allowed = params['level-asymmetry-allowed'];

	return (
		level_asymmetry_allowed === 1 ||
		level_asymmetry_allowed === '1'
	);
}

},{"debug":35}],39:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Logger_1 = require("./Logger");
const EnhancedEventEmitter_1 = require("./EnhancedEventEmitter");
const errors_1 = require("./errors");
const logger = new Logger_1.Logger('Consumer');
class Consumer extends EnhancedEventEmitter_1.EnhancedEventEmitter {
    /**
     * @emits transportclose
     * @emits trackended
     * @emits @getstats
     * @emits @close
     */
    constructor({ id, localId, producerId, rtpReceiver, track, rtpParameters, appData }) {
        super();
        // Closed flag.
        this._closed = false;
        logger.debug('constructor()');
        this._id = id;
        this._localId = localId;
        this._producerId = producerId;
        this._rtpReceiver = rtpReceiver;
        this._track = track;
        this._rtpParameters = rtpParameters;
        this._paused = !track.enabled;
        this._appData = appData;
        this._onTrackEnded = this._onTrackEnded.bind(this);
        this._handleTrack();
    }
    /**
     * Consumer id.
     */
    get id() {
        return this._id;
    }
    /**
     * Local id.
     */
    get localId() {
        return this._localId;
    }
    /**
     * Associated Producer id.
     */
    get producerId() {
        return this._producerId;
    }
    /**
     * Whether the Consumer is closed.
     */
    get closed() {
        return this._closed;
    }
    /**
     * Media kind.
     */
    get kind() {
        return this._track.kind;
    }
    /**
     * Associated RTCRtpReceiver.
     */
    get rtpReceiver() {
        return this._rtpReceiver;
    }
    /**
     * The associated track.
     */
    get track() {
        return this._track;
    }
    /**
     * RTP parameters.
     */
    get rtpParameters() {
        return this._rtpParameters;
    }
    /**
     * Whether the Consumer is paused.
     */
    get paused() {
        return this._paused;
    }
    /**
     * App custom data.
     */
    get appData() {
        return this._appData;
    }
    /**
     * Invalid setter.
     */
    set appData(appData) {
        throw new Error('cannot override appData object');
    }
    /**
     * Closes the Consumer.
     */
    close() {
        if (this._closed)
            return;
        logger.debug('close()');
        this._closed = true;
        this._destroyTrack();
        this.emit('@close');
    }
    /**
     * Transport was closed.
     */
    transportClosed() {
        if (this._closed)
            return;
        logger.debug('transportClosed()');
        this._closed = true;
        this._destroyTrack();
        this.safeEmit('transportclose');
    }
    /**
     * Get associated RTCRtpReceiver stats.
     */
    async getStats() {
        if (this._closed)
            throw new errors_1.InvalidStateError('closed');
        return this.safeEmitAsPromise('@getstats');
    }
    /**
     * Pauses receiving media.
     */
    pause() {
        logger.debug('pause()');
        if (this._closed) {
            logger.error('pause() | Consumer closed');
            return;
        }
        this._paused = true;
        this._track.enabled = false;
    }
    /**
     * Resumes receiving media.
     */
    resume() {
        logger.debug('resume()');
        if (this._closed) {
            logger.error('resume() | Consumer closed');
            return;
        }
        this._paused = false;
        this._track.enabled = true;
    }
    _onTrackEnded() {
        logger.debug('track "ended" event');
        this.safeEmit('trackended');
    }
    _handleTrack() {
        this._track.addEventListener('ended', this._onTrackEnded);
    }
    _destroyTrack() {
        try {
            this._track.removeEventListener('ended', this._onTrackEnded);
            this._track.stop();
        }
        catch (error) { }
    }
}
exports.Consumer = Consumer;

},{"./EnhancedEventEmitter":43,"./Logger":44,"./errors":47}],40:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Logger_1 = require("./Logger");
const EnhancedEventEmitter_1 = require("./EnhancedEventEmitter");
const logger = new Logger_1.Logger('DataConsumer');
class DataConsumer extends EnhancedEventEmitter_1.EnhancedEventEmitter {
    /**
     * @emits transportclose
     * @emits open
     * @emits error - (error: Error)
     * @emits close
     * @emits message - (message: any)
     * @emits @close
     */
    constructor({ id, dataProducerId, dataChannel, sctpStreamParameters, appData }) {
        super();
        // Closed flag.
        this._closed = false;
        logger.debug('constructor()');
        this._id = id;
        this._dataProducerId = dataProducerId;
        this._dataChannel = dataChannel;
        this._sctpStreamParameters = sctpStreamParameters;
        this._appData = appData;
        this._handleDataChannel();
    }
    /**
     * DataConsumer id.
     */
    get id() {
        return this._id;
    }
    /**
     * Associated DataProducer id.
     */
    get dataProducerId() {
        return this._dataProducerId;
    }
    /**
     * Whether the DataConsumer is closed.
     */
    get closed() {
        return this._closed;
    }
    /**
     * SCTP stream parameters.
     */
    get sctpStreamParameters() {
        return this._sctpStreamParameters;
    }
    /**
     * DataChannel readyState.
     */
    get readyState() {
        return this._dataChannel.readyState;
    }
    /**
     * DataChannel label.
     */
    get label() {
        return this._dataChannel.label;
    }
    /**
     * DataChannel protocol.
     */
    get protocol() {
        return this._dataChannel.protocol;
    }
    /**
     * DataChannel binaryType.
     */
    get binaryType() {
        return this._dataChannel.binaryType;
    }
    /**
     * Set DataChannel binaryType.
     */
    set binaryType(binaryType) {
        this._dataChannel.binaryType = binaryType;
    }
    /**
     * App custom data.
     */
    get appData() {
        return this._appData;
    }
    /**
     * Invalid setter.
     */
    set appData(appData) {
        throw new Error('cannot override appData object');
    }
    /**
     * Closes the DataConsumer.
     */
    close() {
        if (this._closed)
            return;
        logger.debug('close()');
        this._closed = true;
        this._dataChannel.close();
        this.emit('@close');
    }
    /**
     * Transport was closed.
     */
    transportClosed() {
        if (this._closed)
            return;
        logger.debug('transportClosed()');
        this._closed = true;
        this._dataChannel.close();
        this.safeEmit('transportclose');
    }
    _handleDataChannel() {
        this._dataChannel.addEventListener('open', () => {
            if (this._closed)
                return;
            logger.debug('DataChannel "open" event');
            this.safeEmit('open');
        });
        this._dataChannel.addEventListener('error', (event) => {
            if (this._closed)
                return;
            let { error } = event;
            if (!error)
                error = new Error('unknown DataChannel error');
            if (error.errorDetail === 'sctp-failure') {
                logger.error('DataChannel SCTP error [sctpCauseCode:%s]: %s', error.sctpCauseCode, error.message);
            }
            else {
                logger.error('DataChannel "error" event: %o', error);
            }
            this.safeEmit('error', error);
        });
        this._dataChannel.addEventListener('close', () => {
            if (this._closed)
                return;
            logger.warn('DataChannel "close" event');
            this._closed = true;
            this.emit('@close');
            this.safeEmit('close');
        });
        this._dataChannel.addEventListener('message', (event) => {
            if (this._closed)
                return;
            this.safeEmit('message', event.data);
        });
    }
}
exports.DataConsumer = DataConsumer;

},{"./EnhancedEventEmitter":43,"./Logger":44}],41:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Logger_1 = require("./Logger");
const EnhancedEventEmitter_1 = require("./EnhancedEventEmitter");
const errors_1 = require("./errors");
const logger = new Logger_1.Logger('DataProducer');
class DataProducer extends EnhancedEventEmitter_1.EnhancedEventEmitter {
    /**
     * @emits transportclose
     * @emits open
     * @emits error - (error: Error)
     * @emits close
     * @emits bufferedamountlow
     * @emits @close
     */
    constructor({ id, dataChannel, sctpStreamParameters, appData }) {
        super();
        // Closed flag.
        this._closed = false;
        logger.debug('constructor()');
        this._id = id;
        this._dataChannel = dataChannel;
        this._sctpStreamParameters = sctpStreamParameters;
        this._appData = appData;
        this._handleDataChannel();
    }
    /**
     * DataProducer id.
     */
    get id() {
        return this._id;
    }
    /**
     * Whether the DataProducer is closed.
     */
    get closed() {
        return this._closed;
    }
    /**
     * SCTP stream parameters.
     */
    get sctpStreamParameters() {
        return this._sctpStreamParameters;
    }
    /**
     * DataChannel readyState.
     */
    get readyState() {
        return this._dataChannel.readyState;
    }
    /**
     * DataChannel label.
     */
    get label() {
        return this._dataChannel.label;
    }
    /**
     * DataChannel protocol.
     */
    get protocol() {
        return this._dataChannel.protocol;
    }
    /**
     * DataChannel bufferedAmount.
     */
    get bufferedAmount() {
        return this._dataChannel.bufferedAmount;
    }
    /**
     * DataChannel bufferedAmountLowThreshold.
     */
    get bufferedAmountLowThreshold() {
        return this._dataChannel.bufferedAmountLowThreshold;
    }
    /**
     * Set DataChannel bufferedAmountLowThreshold.
     */
    set bufferedAmountLowThreshold(bufferedAmountLowThreshold) {
        this._dataChannel.bufferedAmountLowThreshold = bufferedAmountLowThreshold;
    }
    /**
     * App custom data.
     */
    get appData() {
        return this._appData;
    }
    /**
     * Invalid setter.
     */
    set appData(appData) {
        throw new Error('cannot override appData object');
    }
    /**
     * Closes the DataProducer.
     */
    close() {
        if (this._closed)
            return;
        logger.debug('close()');
        this._closed = true;
        this._dataChannel.close();
        this.emit('@close');
    }
    /**
     * Transport was closed.
     */
    transportClosed() {
        if (this._closed)
            return;
        logger.debug('transportClosed()');
        this._closed = true;
        this._dataChannel.close();
        this.safeEmit('transportclose');
    }
    /**
     * Send a message.
     *
     * @param {String|Blob|ArrayBuffer|ArrayBufferView} data.
     */
    send(data) {
        logger.debug('send()');
        if (this._closed)
            throw new errors_1.InvalidStateError('closed');
        this._dataChannel.send(data);
    }
    _handleDataChannel() {
        this._dataChannel.addEventListener('open', () => {
            if (this._closed)
                return;
            logger.debug('DataChannel "open" event');
            this.safeEmit('open');
        });
        this._dataChannel.addEventListener('error', (event) => {
            if (this._closed)
                return;
            let { error } = event;
            if (!error)
                error = new Error('unknown DataChannel error');
            if (error.errorDetail === 'sctp-failure') {
                logger.error('DataChannel SCTP error [sctpCauseCode:%s]: %s', error.sctpCauseCode, error.message);
            }
            else {
                logger.error('DataChannel "error" event: %o', error);
            }
            this.safeEmit('error', error);
        });
        this._dataChannel.addEventListener('close', () => {
            if (this._closed)
                return;
            logger.warn('DataChannel "close" event');
            this._closed = true;
            this.emit('@close');
            this.safeEmit('close');
        });
        this._dataChannel.addEventListener('message', () => {
            if (this._closed)
                return;
            logger.warn('DataChannel "message" event in a DataProducer, message discarded');
        });
        this._dataChannel.addEventListener('bufferedamountlow', () => {
            if (this._closed)
                return;
            this.safeEmit('bufferedamountlow');
        });
    }
}
exports.DataProducer = DataProducer;

},{"./EnhancedEventEmitter":43,"./Logger":44,"./errors":47}],42:[function(require,module,exports){
"use strict";
/* global RTCRtpTransceiver */
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const bowser = __importStar(require("bowser"));
const Logger_1 = require("./Logger");
const errors_1 = require("./errors");
const ortc = __importStar(require("./ortc"));
const Transport_1 = require("./Transport");
const Chrome74_1 = require("./handlers/Chrome74");
const Chrome70_1 = require("./handlers/Chrome70");
const Chrome67_1 = require("./handlers/Chrome67");
const Chrome55_1 = require("./handlers/Chrome55");
const Firefox60_1 = require("./handlers/Firefox60");
const Safari12_1 = require("./handlers/Safari12");
const Safari11_1 = require("./handlers/Safari11");
const Edge11_1 = require("./handlers/Edge11");
const ReactNative_1 = require("./handlers/ReactNative");
const logger = new Logger_1.Logger('Device');
function detectDevice() {
    // React-Native.
    // NOTE: react-native-webrtc >= 1.75.0 is required.
    if (typeof navigator === 'object' && navigator.product === 'ReactNative') {
        if (typeof RTCPeerConnection === 'undefined') {
            logger.warn('this._detectDevice() | unsupported ReactNative without RTCPeerConnection');
            return undefined;
        }
        logger.debug('this._detectDevice() | ReactNative handler chosen');
        return 'ReactNative';
    }
    // Browser.
    else if (typeof navigator === 'object' && typeof navigator.userAgent === 'string') {
        const ua = navigator.userAgent;
        const browser = bowser.getParser(ua);
        const engine = browser.getEngine();
        // Chrome and Chromium.
        if (browser.satisfies({ chrome: '>=74', chromium: '>=74' })) {
            return 'Chrome74';
        }
        else if (browser.satisfies({ chrome: '>=70', chromium: '>=70' })) {
            return 'Chrome70';
        }
        else if (browser.satisfies({ chrome: '>=67', chromium: '>=67' })) {
            return 'Chrome67';
        }
        else if (browser.satisfies({ chrome: '>=55', chromium: '>=55' })) {
            return 'Chrome55';
        }
        // Firefox.
        else if (browser.satisfies({ firefox: '>=60' })) {
            return 'Firefox60';
        }
        // Safari with Unified-Plan support enabled.
        else if (browser.satisfies({ safari: '>=12.0' }) &&
            typeof RTCRtpTransceiver !== 'undefined' &&
            RTCRtpTransceiver.prototype.hasOwnProperty('currentDirection')) {
            return 'Safari12';
        }
        // Safari with Plab-B support.
        else if (browser.satisfies({ safari: '>=11' })) {
            return 'Safari11';
        }
        // Old Edge with ORTC support.
        else if (browser.satisfies({ 'microsoft edge': '>=11' }) &&
            browser.satisfies({ 'microsoft edge': '<=18' })) {
            return 'Edge11';
        }
        // Best effort for Chromium based browsers.
        else if (engine.name && engine.name.toLowerCase() === 'blink') {
            const match = ua.match(/(?:(?:Chrome|Chromium))[ /](\w+)/i);
            if (match) {
                const version = Number(match[1]);
                if (version >= 74) {
                    return 'Chrome74';
                }
                else if (version >= 70) {
                    return 'Chrome70';
                }
                else if (version >= 67) {
                    return 'Chrome67';
                }
                else {
                    return 'Chrome55';
                }
            }
            else {
                return 'Chrome74';
            }
        }
        // Unsupported browser.
        else {
            logger.warn('this._detectDevice() | browser not supported [name:%s, version:%s]', browser.getBrowserName(), browser.getBrowserVersion());
            return undefined;
        }
    }
    // Unknown device.
    else {
        logger.warn('this._detectDevice() | unknown device');
        return undefined;
    }
}
exports.detectDevice = detectDevice;
class Device {
    /**
     * Create a new Device to connect to mediasoup server.
     *
     * @throws {UnsupportedError} if device is not supported.
     */
    constructor({ handlerName, handlerFactory, Handler } = {}) {
        // Loaded flag.
        this._loaded = false;
        logger.debug('constructor()');
        // Handle deprecated option.
        if (Handler) {
            logger.warn('constructor() | Handler option is DEPRECATED, use handlerName or handlerFactory instead');
            if (typeof Handler === 'string')
                handlerName = Handler;
            else
                throw new TypeError('non string Handler option no longer supported, use handlerFactory instead');
        }
        if (handlerName && handlerFactory) {
            throw new TypeError('just one of handlerName or handlerInterface can be given');
        }
        if (handlerFactory) {
            this._handlerFactory = handlerFactory;
        }
        else {
            if (handlerName) {
                logger.debug('constructor() | handler given: %s', handlerName);
            }
            else {
                handlerName = detectDevice();
                if (handlerName)
                    logger.debug('constructor() | detected handler: %s', handlerName);
                else
                    throw new errors_1.UnsupportedError('device not supported');
            }
            switch (handlerName) {
                case 'Chrome74':
                    this._handlerFactory = Chrome74_1.Chrome74.createFactory();
                    break;
                case 'Chrome70':
                    this._handlerFactory = Chrome70_1.Chrome70.createFactory();
                    break;
                case 'Chrome67':
                    this._handlerFactory = Chrome67_1.Chrome67.createFactory();
                    break;
                case 'Chrome55':
                    this._handlerFactory = Chrome55_1.Chrome55.createFactory();
                    break;
                case 'Firefox60':
                    this._handlerFactory = Firefox60_1.Firefox60.createFactory();
                    break;
                case 'Safari12':
                    this._handlerFactory = Safari12_1.Safari12.createFactory();
                    break;
                case 'Safari11':
                    this._handlerFactory = Safari11_1.Safari11.createFactory();
                    break;
                case 'Edge11':
                    this._handlerFactory = Edge11_1.Edge11.createFactory();
                    break;
                case 'ReactNative':
                    this._handlerFactory = ReactNative_1.ReactNative.createFactory();
                    break;
                default:
                    throw new TypeError(`unknown handlerName "${handlerName}"`);
            }
        }
        // Create a temporal handler to get its name.
        const handler = this._handlerFactory();
        this._handlerName = handler.name;
        handler.close();
        this._extendedRtpCapabilities = null;
        this._recvRtpCapabilities = undefined;
        this._canProduceByKind =
            {
                audio: false,
                video: false
            };
        this._sctpCapabilities = null;
    }
    /**
     * The RTC handler name.
     */
    get handlerName() {
        return this._handlerName;
    }
    /**
     * Whether the Device is loaded.
     */
    get loaded() {
        return this._loaded;
    }
    /**
     * RTP capabilities of the Device for receiving media.
     *
     * @throws {InvalidStateError} if not loaded.
     */
    get rtpCapabilities() {
        if (!this._loaded)
            throw new errors_1.InvalidStateError('not loaded');
        return this._recvRtpCapabilities;
    }
    /**
     * SCTP capabilities of the Device.
     *
     * @throws {InvalidStateError} if not loaded.
     */
    get sctpCapabilities() {
        if (!this._loaded)
            throw new errors_1.InvalidStateError('not loaded');
        return this._sctpCapabilities;
    }
    /**
     * Initialize the Device.
     */
    async load({ routerRtpCapabilities } = {}) {
        logger.debug('load() [routerRtpCapabilities:%o]', routerRtpCapabilities);
        // Temporal handler to get its capabilities.
        let handler;
        try {
            if (this._loaded)
                throw new errors_1.InvalidStateError('already loaded');
            // This may throw.
            ortc.validateRtpCapabilities(routerRtpCapabilities);
            handler = this._handlerFactory();
            const nativeRtpCapabilities = await handler.getNativeRtpCapabilities();
            logger.debug('load() | got native RTP capabilities:%o', nativeRtpCapabilities);
            // This may throw.
            ortc.validateRtpCapabilities(nativeRtpCapabilities);
            // Get extended RTP capabilities.
            this._extendedRtpCapabilities = ortc.getExtendedRtpCapabilities(nativeRtpCapabilities, routerRtpCapabilities);
            logger.debug('load() | got extended RTP capabilities:%o', this._extendedRtpCapabilities);
            // Check whether we can produce audio/video.
            this._canProduceByKind.audio =
                ortc.canSend('audio', this._extendedRtpCapabilities);
            this._canProduceByKind.video =
                ortc.canSend('video', this._extendedRtpCapabilities);
            // Generate our receiving RTP capabilities for receiving media.
            this._recvRtpCapabilities =
                ortc.getRecvRtpCapabilities(this._extendedRtpCapabilities);
            logger.debug('load() | got receiving RTP capabilities:%o', this._recvRtpCapabilities);
            // Generate our SCTP capabilities.
            this._sctpCapabilities = await handler.getNativeSctpCapabilities();
            logger.debug('load() | got native SCTP capabilities:%o', this._sctpCapabilities);
            logger.debug('load() succeeded');
            this._loaded = true;
            handler.close();
        }
        catch (error) {
            if (handler)
                handler.close();
            throw error;
        }
    }
    /**
     * Whether we can produce audio/video.
     *
     * @throws {InvalidStateError} if not loaded.
     * @throws {TypeError} if wrong arguments.
     */
    canProduce(kind) {
        if (!this._loaded)
            throw new errors_1.InvalidStateError('not loaded');
        else if (kind !== 'audio' && kind !== 'video')
            throw new TypeError(`invalid kind "${kind}"`);
        return this._canProduceByKind[kind];
    }
    /**
     * Creates a Transport for sending media.
     *
     * @throws {InvalidStateError} if not loaded.
     * @throws {TypeError} if wrong arguments.
     */
    createSendTransport({ id, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, appData = {} }) {
        logger.debug('createSendTransport()');
        return this._createTransport({
            direction: 'send',
            id: id,
            iceParameters: iceParameters,
            iceCandidates: iceCandidates,
            dtlsParameters: dtlsParameters,
            sctpParameters: sctpParameters,
            iceServers: iceServers,
            iceTransportPolicy: iceTransportPolicy,
            additionalSettings: additionalSettings,
            proprietaryConstraints: proprietaryConstraints,
            appData: appData
        });
    }
    /**
     * Creates a Transport for receiving media.
     *
     * @throws {InvalidStateError} if not loaded.
     * @throws {TypeError} if wrong arguments.
     */
    createRecvTransport({ id, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, appData = {} }) {
        logger.debug('createRecvTransport()');
        return this._createTransport({
            direction: 'recv',
            id: id,
            iceParameters: iceParameters,
            iceCandidates: iceCandidates,
            dtlsParameters: dtlsParameters,
            sctpParameters: sctpParameters,
            iceServers: iceServers,
            iceTransportPolicy: iceTransportPolicy,
            additionalSettings: additionalSettings,
            proprietaryConstraints: proprietaryConstraints,
            appData: appData
        });
    }
    _createTransport({ direction, id, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, appData = {} }) {
        if (!this._loaded)
            throw new errors_1.InvalidStateError('not loaded');
        else if (typeof id !== 'string')
            throw new TypeError('missing id');
        else if (typeof iceParameters !== 'object')
            throw new TypeError('missing iceParameters');
        else if (!Array.isArray(iceCandidates))
            throw new TypeError('missing iceCandidates');
        else if (typeof dtlsParameters !== 'object')
            throw new TypeError('missing dtlsParameters');
        else if (sctpParameters && typeof sctpParameters !== 'object')
            throw new TypeError('wrong sctpParameters');
        else if (appData && typeof appData !== 'object')
            throw new TypeError('if given, appData must be an object');
        // Create a new Transport.
        const transport = new Transport_1.Transport({
            direction,
            id,
            iceParameters,
            iceCandidates,
            dtlsParameters,
            sctpParameters,
            iceServers,
            iceTransportPolicy,
            additionalSettings,
            proprietaryConstraints,
            appData,
            handlerFactory: this._handlerFactory,
            extendedRtpCapabilities: this._extendedRtpCapabilities,
            canProduceByKind: this._canProduceByKind
        });
        return transport;
    }
}
exports.Device = Device;

},{"./Logger":44,"./Transport":46,"./errors":47,"./handlers/Chrome55":48,"./handlers/Chrome67":49,"./handlers/Chrome70":50,"./handlers/Chrome74":51,"./handlers/Edge11":52,"./handlers/Firefox60":53,"./handlers/ReactNative":55,"./handlers/Safari11":56,"./handlers/Safari12":57,"./ortc":65,"bowser":33}],43:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const events_1 = require("events");
const Logger_1 = require("./Logger");
const logger = new Logger_1.Logger('EnhancedEventEmitter');
class EnhancedEventEmitter extends events_1.EventEmitter {
    constructor() {
        super();
        this.setMaxListeners(Infinity);
    }
    safeEmit(event, ...args) {
        const numListeners = this.listenerCount(event);
        try {
            return this.emit(event, ...args);
        }
        catch (error) {
            logger.error('safeEmit() | event listener threw an error [event:%s]:%o', event, error);
            return Boolean(numListeners);
        }
    }
    async safeEmitAsPromise(event, ...args) {
        return new Promise((resolve, reject) => (this.safeEmit(event, ...args, resolve, reject)));
    }
}
exports.EnhancedEventEmitter = EnhancedEventEmitter;

},{"./Logger":44,"events":37}],44:[function(require,module,exports){
"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const debug_1 = __importDefault(require("debug"));
const APP_NAME = 'mediasoup-client';
class Logger {
    constructor(prefix) {
        if (prefix) {
            this._debug = debug_1.default(`${APP_NAME}:${prefix}`);
            this._warn = debug_1.default(`${APP_NAME}:WARN:${prefix}`);
            this._error = debug_1.default(`${APP_NAME}:ERROR:${prefix}`);
        }
        else {
            this._debug = debug_1.default(APP_NAME);
            this._warn = debug_1.default(`${APP_NAME}:WARN`);
            this._error = debug_1.default(`${APP_NAME}:ERROR`);
        }
        /* eslint-disable no-console */
        this._debug.log = console.info.bind(console);
        this._warn.log = console.warn.bind(console);
        this._error.log = console.error.bind(console);
        /* eslint-enable no-console */
    }
    get debug() {
        return this._debug;
    }
    get warn() {
        return this._warn;
    }
    get error() {
        return this._error;
    }
}
exports.Logger = Logger;

},{"debug":35}],45:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Logger_1 = require("./Logger");
const EnhancedEventEmitter_1 = require("./EnhancedEventEmitter");
const errors_1 = require("./errors");
const logger = new Logger_1.Logger('Producer');
class Producer extends EnhancedEventEmitter_1.EnhancedEventEmitter {
    /**
     * @emits transportclose
     * @emits trackended
     * @emits @replacetrack - (track: MediaStreamTrack)
     * @emits @setmaxspatiallayer - (spatialLayer: string)
     * @emits @setrtpencodingparameters - (params: any)
     * @emits @getstats
     * @emits @close
     */
    constructor({ id, localId, rtpSender, track, rtpParameters, stopTracks, appData }) {
        super();
        // Closed flag.
        this._closed = false;
        logger.debug('constructor()');
        this._id = id;
        this._localId = localId;
        this._rtpSender = rtpSender;
        this._track = track;
        this._rtpParameters = rtpParameters;
        this._paused = !track.enabled;
        this._maxSpatialLayer = undefined;
        this._stopTracks = stopTracks;
        this._appData = appData;
        this._onTrackEnded = this._onTrackEnded.bind(this);
        this._handleTrack();
    }
    /**
     * Producer id.
     */
    get id() {
        return this._id;
    }
    /**
     * Local id.
     */
    get localId() {
        return this._localId;
    }
    /**
     * Whether the Producer is closed.
     */
    get closed() {
        return this._closed;
    }
    /**
     * Media kind.
     */
    get kind() {
        return this._track.kind;
    }
    /**
     * Associated RTCRtpSender.
     */
    get rtpSender() {
        return this._rtpSender;
    }
    /**
     * The associated track.
     */
    get track() {
        return this._track;
    }
    /**
     * RTP parameters.
     */
    get rtpParameters() {
        return this._rtpParameters;
    }
    /**
     * Whether the Producer is paused.
     */
    get paused() {
        return this._paused;
    }
    /**
     * Max spatial layer.
     *
     * @type {Number | undefined}
     */
    get maxSpatialLayer() {
        return this._maxSpatialLayer;
    }
    /**
     * App custom data.
     */
    get appData() {
        return this._appData;
    }
    /**
     * Invalid setter.
     */
    set appData(appData) {
        throw new Error('cannot override appData object');
    }
    /**
     * Closes the Producer.
     */
    close() {
        if (this._closed)
            return;
        logger.debug('close()');
        this._closed = true;
        this._destroyTrack();
        this.emit('@close');
    }
    /**
     * Transport was closed.
     */
    transportClosed() {
        if (this._closed)
            return;
        logger.debug('transportClosed()');
        this._closed = true;
        this._destroyTrack();
        this.safeEmit('transportclose');
    }
    /**
     * Get associated RTCRtpSender stats.
     */
    async getStats() {
        if (this._closed)
            throw new errors_1.InvalidStateError('closed');
        return this.safeEmitAsPromise('@getstats');
    }
    /**
     * Pauses sending media.
     */
    pause() {
        logger.debug('pause()');
        if (this._closed) {
            logger.error('pause() | Producer closed');
            return;
        }
        this._paused = true;
        this._track.enabled = false;
    }
    /**
     * Resumes sending media.
     */
    resume() {
        logger.debug('resume()');
        if (this._closed) {
            logger.error('resume() | Producer closed');
            return;
        }
        this._paused = false;
        this._track.enabled = true;
    }
    /**
     * Replaces the current track with a new one.
     */
    async replaceTrack({ track }) {
        logger.debug('replaceTrack() [track:%o]', track);
        if (this._closed) {
            // This must be done here. Otherwise there is no chance to stop the given
            // track.
            if (this._stopTracks) {
                try {
                    track.stop();
                }
                catch (error) { }
            }
            throw new errors_1.InvalidStateError('closed');
        }
        else if (!track) {
            throw new TypeError('missing track');
        }
        else if (track.readyState === 'ended') {
            throw new errors_1.InvalidStateError('track ended');
        }
        // Do nothing if this is the same track as the current handled one.
        if (track === this._track) {
            logger.debug('replaceTrack() | same track, ignored');
            return;
        }
        await this.safeEmitAsPromise('@replacetrack', track);
        // Destroy the previous track.
        this._destroyTrack();
        // Set the new track.
        this._track = track;
        // If this Producer was paused/resumed and the state of the new
        // track does not match, fix it.
        if (!this._paused)
            this._track.enabled = true;
        else
            this._track.enabled = false;
        // Handle the effective track.
        this._handleTrack();
    }
    /**
     * Sets the video max spatial layer to be sent.
     */
    async setMaxSpatialLayer(spatialLayer) {
        if (this._closed)
            throw new errors_1.InvalidStateError('closed');
        else if (this._track.kind !== 'video')
            throw new errors_1.UnsupportedError('not a video Producer');
        else if (typeof spatialLayer !== 'number')
            throw new TypeError('invalid spatialLayer');
        if (spatialLayer === this._maxSpatialLayer)
            return;
        await this.safeEmitAsPromise('@setmaxspatiallayer', spatialLayer);
        this._maxSpatialLayer = spatialLayer;
    }
    /**
     * Sets the DSCP value.
     */
    async setRtpEncodingParameters(params) {
        if (this._closed)
            throw new errors_1.InvalidStateError('closed');
        else if (typeof params !== 'object')
            throw new TypeError('invalid params');
        await this.safeEmitAsPromise('@setrtpencodingparameters', params);
    }
    _onTrackEnded() {
        logger.debug('track "ended" event');
        this.safeEmit('trackended');
    }
    _handleTrack() {
        this._track.addEventListener('ended', this._onTrackEnded);
    }
    _destroyTrack() {
        try {
            this._track.removeEventListener('ended', this._onTrackEnded);
            // Just stop the track unless the app set stopTracks: false.
            if (this._stopTracks)
                this._track.stop();
        }
        catch (error) { }
    }
}
exports.Producer = Producer;

},{"./EnhancedEventEmitter":43,"./Logger":44,"./errors":47}],46:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const awaitqueue_1 = require("awaitqueue");
const Logger_1 = require("./Logger");
const EnhancedEventEmitter_1 = require("./EnhancedEventEmitter");
const errors_1 = require("./errors");
const utils = __importStar(require("./utils"));
const ortc = __importStar(require("./ortc"));
const Producer_1 = require("./Producer");
const Consumer_1 = require("./Consumer");
const DataProducer_1 = require("./DataProducer");
const DataConsumer_1 = require("./DataConsumer");
const logger = new Logger_1.Logger('Transport');
class Transport extends EnhancedEventEmitter_1.EnhancedEventEmitter {
    /**
     * @emits connect - (transportLocalParameters: any, callback: Function, errback: Function)
     * @emits connectionstatechange - (connectionState: ConnectionState)
     * @emits produce - (producerLocalParameters: any, callback: Function, errback: Function)
     * @emits producedata - (dataProducerLocalParameters: any, callback: Function, errback: Function)
     */
    constructor({ direction, id, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, appData, handlerFactory, extendedRtpCapabilities, canProduceByKind }) {
        super();
        // Closed flag.
        this._closed = false;
        // Transport connection state.
        this._connectionState = 'new';
        // Map of Producers indexed by id.
        this._producers = new Map();
        // Map of Consumers indexed by id.
        this._consumers = new Map();
        // Map of DataProducers indexed by id.
        this._dataProducers = new Map();
        // Map of DataConsumers indexed by id.
        this._dataConsumers = new Map();
        // Whether the Consumer for RTP probation has been created.
        this._probatorConsumerCreated = false;
        // AwaitQueue instance to make async tasks happen sequentially.
        this._awaitQueue = new awaitqueue_1.AwaitQueue({ ClosedErrorClass: errors_1.InvalidStateError });
        logger.debug('constructor() [id:%s, direction:%s]', id, direction);
        this._id = id;
        this._direction = direction;
        this._extendedRtpCapabilities = extendedRtpCapabilities;
        this._canProduceByKind = canProduceByKind;
        this._maxSctpMessageSize =
            sctpParameters ? sctpParameters.maxMessageSize : null;
        // Clone and sanitize additionalSettings.
        additionalSettings = utils.clone(additionalSettings);
        delete additionalSettings.iceServers;
        delete additionalSettings.iceTransportPolicy;
        delete additionalSettings.bundlePolicy;
        delete additionalSettings.rtcpMuxPolicy;
        delete additionalSettings.sdpSemantics;
        this._handler = handlerFactory();
        this._handler.run({
            direction,
            iceParameters,
            iceCandidates,
            dtlsParameters,
            sctpParameters,
            iceServers,
            iceTransportPolicy,
            additionalSettings,
            proprietaryConstraints,
            extendedRtpCapabilities
        });
        this._appData = appData;
        this._handleHandler();
    }
    /**
     * Transport id.
     */
    get id() {
        return this._id;
    }
    /**
     * Whether the Transport is closed.
     */
    get closed() {
        return this._closed;
    }
    /**
     * Transport direction.
     */
    get direction() {
        return this._direction;
    }
    /**
     * RTC handler instance.
     */
    get handler() {
        return this._handler;
    }
    /**
     * Connection state.
     */
    get connectionState() {
        return this._connectionState;
    }
    /**
     * App custom data.
     */
    get appData() {
        return this._appData;
    }
    /**
     * Invalid setter.
     */
    set appData(appData) {
        throw new Error('cannot override appData object');
    }
    /**
     * Close the Transport.
     */
    close() {
        if (this._closed)
            return;
        logger.debug('close()');
        this._closed = true;
        // Close the AwaitQueue.
        this._awaitQueue.close();
        // Close the handler.
        this._handler.close();
        // Close all Producers.
        for (const producer of this._producers.values()) {
            producer.transportClosed();
        }
        this._producers.clear();
        // Close all Consumers.
        for (const consumer of this._consumers.values()) {
            consumer.transportClosed();
        }
        this._consumers.clear();
        // Close all DataProducers.
        for (const dataProducer of this._dataProducers.values()) {
            dataProducer.transportClosed();
        }
        this._dataProducers.clear();
        // Close all DataConsumers.
        for (const dataConsumer of this._dataConsumers.values()) {
            dataConsumer.transportClosed();
        }
        this._dataConsumers.clear();
    }
    /**
     * Get associated Transport (RTCPeerConnection) stats.
     *
     * @returns {RTCStatsReport}
     */
    async getStats() {
        if (this._closed)
            throw new errors_1.InvalidStateError('closed');
        return this._handler.getTransportStats();
    }
    /**
     * Restart ICE connection.
     */
    async restartIce({ iceParameters }) {
        logger.debug('restartIce()');
        if (this._closed)
            throw new errors_1.InvalidStateError('closed');
        else if (!iceParameters)
            throw new TypeError('missing iceParameters');
        // Enqueue command.
        return this._awaitQueue.push(async () => this._handler.restartIce(iceParameters));
    }
    /**
     * Update ICE servers.
     */
    async updateIceServers({ iceServers } = {}) {
        logger.debug('updateIceServers()');
        if (this._closed)
            throw new errors_1.InvalidStateError('closed');
        else if (!Array.isArray(iceServers))
            throw new TypeError('missing iceServers');
        // Enqueue command.
        return this._awaitQueue.push(async () => this._handler.updateIceServers(iceServers));
    }
    /**
     * Create a Producer.
     */
    async produce({ track, encodings, codecOptions, stopTracks = true, appData = {} } = {}) {
        logger.debug('produce() [track:%o]', track);
        if (!track)
            throw new TypeError('missing track');
        else if (this._direction !== 'send')
            throw new errors_1.UnsupportedError('not a sending Transport');
        else if (!this._canProduceByKind[track.kind])
            throw new errors_1.UnsupportedError(`cannot produce ${track.kind}`);
        else if (track.readyState === 'ended')
            throw new errors_1.InvalidStateError('track ended');
        else if (this.listenerCount('connect') === 0 && this._connectionState === 'new')
            throw new TypeError('no "connect" listener set into this transport');
        else if (this.listenerCount('produce') === 0)
            throw new TypeError('no "produce" listener set into this transport');
        else if (appData && typeof appData !== 'object')
            throw new TypeError('if given, appData must be an object');
        // Enqueue command.
        return this._awaitQueue.push(async () => {
            let normalizedEncodings;
            if (encodings && !Array.isArray(encodings)) {
                throw TypeError('encodings must be an array');
            }
            else if (encodings && encodings.length === 0) {
                normalizedEncodings = undefined;
            }
            else if (encodings) {
                normalizedEncodings = encodings
                    .map((encoding) => {
                    const normalizedEncoding = { active: true };
                    if (encoding.active === false)
                        normalizedEncoding.active = false;
                    if (typeof encoding.maxBitrate === 'number')
                        normalizedEncoding.maxBitrate = encoding.maxBitrate;
                    if (typeof encoding.maxFramerate === 'number')
                        normalizedEncoding.maxFramerate = encoding.maxFramerate;
                    if (typeof encoding.scaleResolutionDownBy === 'number')
                        normalizedEncoding.scaleResolutionDownBy = encoding.scaleResolutionDownBy;
                    if (typeof encoding.dtx === 'boolean')
                        normalizedEncoding.dtx = encoding.dtx;
                    if (typeof encoding.scalabilityMode === 'string')
                        normalizedEncoding.scalabilityMode = encoding.scalabilityMode;
                    if (typeof encoding.priority === 'string')
                        normalizedEncoding.priority = encoding.priority;
                    if (typeof encoding.networkPriority === 'string')
                        normalizedEncoding.networkPriority = encoding.networkPriority;
                    return normalizedEncoding;
                });
            }
            const { localId, rtpParameters, rtpSender } = await this._handler.send({
                track,
                encodings: normalizedEncodings,
                codecOptions
            });
            try {
                // This will fill rtpParameters's missing fields with default values.
                ortc.validateRtpParameters(rtpParameters);
                const { id } = await this.safeEmitAsPromise('produce', {
                    kind: track.kind,
                    rtpParameters,
                    appData
                });
                const producer = new Producer_1.Producer({ id, localId, rtpSender, track, rtpParameters, stopTracks, appData });
                this._producers.set(producer.id, producer);
                this._handleProducer(producer);
                return producer;
            }
            catch (error) {
                this._handler.stopSending(localId)
                    .catch(() => { });
                throw error;
            }
        })
            // This catch is needed to stop the given track if the command above
            // failed due to closed Transport.
            .catch((error) => {
            if (stopTracks) {
                try {
                    track.stop();
                }
                catch (error2) { }
            }
            throw error;
        });
    }
    /**
     * Create a Consumer to consume a remote Producer.
     */
    async consume({ id, producerId, kind, rtpParameters, appData = {} } = {}) {
        logger.debug('consume()');
        if (this._closed)
            throw new errors_1.InvalidStateError('closed');
        else if (this._direction !== 'recv')
            throw new errors_1.UnsupportedError('not a receiving Transport');
        else if (typeof id !== 'string')
            throw new TypeError('missing id');
        else if (typeof producerId !== 'string')
            throw new TypeError('missing producerId');
        else if (kind !== 'audio' && kind !== 'video')
            throw new TypeError(`invalid kind '${kind}'`);
        else if (this.listenerCount('connect') === 0 && this._connectionState === 'new')
            throw new TypeError('no "connect" listener set into this transport');
        else if (appData && typeof appData !== 'object')
            throw new TypeError('if given, appData must be an object');
        // Enqueue command.
        return this._awaitQueue.push(async () => {
            // Ensure the device can consume it.
            const canConsume = ortc.canReceive(rtpParameters, this._extendedRtpCapabilities);
            if (!canConsume)
                throw new errors_1.UnsupportedError('cannot consume this Producer');
            const { localId, rtpReceiver, track } = await this._handler.receive({ trackId: id, kind, rtpParameters });
            const consumer = new Consumer_1.Consumer({ id, localId, producerId, rtpReceiver, track, rtpParameters, appData });
            this._consumers.set(consumer.id, consumer);
            this._handleConsumer(consumer);
            // If this is the first video Consumer and the Consumer for RTP probation
            // has not yet been created, create it now.
            if (!this._probatorConsumerCreated && kind === 'video') {
                try {
                    const probatorRtpParameters = ortc.generateProbatorRtpParameters(consumer.rtpParameters);
                    await this._handler.receive({
                        trackId: 'probator',
                        kind: 'video',
                        rtpParameters: probatorRtpParameters
                    });
                    logger.debug('consume() | Consumer for RTP probation created');
                    this._probatorConsumerCreated = true;
                }
                catch (error) {
                    logger.error('consume() | failed to create Consumer for RTP probation:%o', error);
                }
            }
            return consumer;
        });
    }
    /**
     * Create a DataProducer
     */
    async produceData({ ordered = true, maxPacketLifeTime, maxRetransmits, priority = 'low', label = '', protocol = '', appData = {} } = {}) {
        logger.debug('produceData()');
        if (this._direction !== 'send')
            throw new errors_1.UnsupportedError('not a sending Transport');
        else if (!this._maxSctpMessageSize)
            throw new errors_1.UnsupportedError('SCTP not enabled by remote Transport');
        else if (!['very-low', 'low', 'medium', 'high'].includes(priority))
            throw new TypeError('wrong priority');
        else if (this.listenerCount('connect') === 0 && this._connectionState === 'new')
            throw new TypeError('no "connect" listener set into this transport');
        else if (this.listenerCount('producedata') === 0)
            throw new TypeError('no "producedata" listener set into this transport');
        else if (appData && typeof appData !== 'object')
            throw new TypeError('if given, appData must be an object');
        if (maxPacketLifeTime || maxRetransmits)
            ordered = false;
        // Enqueue command.
        return this._awaitQueue.push(async () => {
            const { dataChannel, sctpStreamParameters } = await this._handler.sendDataChannel({
                ordered,
                maxPacketLifeTime,
                maxRetransmits,
                priority,
                label,
                protocol
            });
            // This will fill sctpStreamParameters's missing fields with default values.
            ortc.validateSctpStreamParameters(sctpStreamParameters);
            const { id } = await this.safeEmitAsPromise('producedata', {
                sctpStreamParameters,
                label,
                protocol,
                appData
            });
            const dataProducer = new DataProducer_1.DataProducer({ id, dataChannel, sctpStreamParameters, appData });
            this._dataProducers.set(dataProducer.id, dataProducer);
            this._handleDataProducer(dataProducer);
            return dataProducer;
        });
    }
    /**
     * Create a DataConsumer
     */
    async consumeData({ id, dataProducerId, sctpStreamParameters, label = '', protocol = '', appData = {} }) {
        logger.debug('consumeData()');
        if (this._closed)
            throw new errors_1.InvalidStateError('closed');
        else if (this._direction !== 'recv')
            throw new errors_1.UnsupportedError('not a receiving Transport');
        else if (!this._maxSctpMessageSize)
            throw new errors_1.UnsupportedError('SCTP not enabled by remote Transport');
        else if (typeof id !== 'string')
            throw new TypeError('missing id');
        else if (typeof dataProducerId !== 'string')
            throw new TypeError('missing dataProducerId');
        else if (this.listenerCount('connect') === 0 && this._connectionState === 'new')
            throw new TypeError('no "connect" listener set into this transport');
        else if (appData && typeof appData !== 'object')
            throw new TypeError('if given, appData must be an object');
        // This may throw.
        ortc.validateSctpStreamParameters(sctpStreamParameters);
        // Enqueue command.
        return this._awaitQueue.push(async () => {
            const { dataChannel } = await this._handler.receiveDataChannel({
                sctpStreamParameters,
                label,
                protocol
            });
            const dataConsumer = new DataConsumer_1.DataConsumer({
                id,
                dataProducerId,
                dataChannel,
                sctpStreamParameters,
                appData
            });
            this._dataConsumers.set(dataConsumer.id, dataConsumer);
            this._handleDataConsumer(dataConsumer);
            return dataConsumer;
        });
    }
    _handleHandler() {
        const handler = this._handler;
        handler.on('@connect', ({ dtlsParameters }, callback, errback) => {
            if (this._closed) {
                errback(new errors_1.InvalidStateError('closed'));
                return;
            }
            this.safeEmit('connect', { dtlsParameters }, callback, errback);
        });
        handler.on('@connectionstatechange', (connectionState) => {
            if (connectionState === this._connectionState)
                return;
            logger.debug('connection state changed to %s', connectionState);
            this._connectionState = connectionState;
            if (!this._closed)
                this.safeEmit('connectionstatechange', connectionState);
        });
    }
    _handleProducer(producer) {
        producer.on('@close', () => {
            this._producers.delete(producer.id);
            if (this._closed)
                return;
            this._awaitQueue.push(async () => this._handler.stopSending(producer.localId))
                .catch((error) => logger.warn('producer.close() failed:%o', error));
        });
        producer.on('@replacetrack', (track, callback, errback) => {
            this._awaitQueue.push(async () => this._handler.replaceTrack(producer.localId, track))
                .then(callback)
                .catch(errback);
        });
        producer.on('@setmaxspatiallayer', (spatialLayer, callback, errback) => {
            this._awaitQueue.push(async () => (this._handler.setMaxSpatialLayer(producer.localId, spatialLayer)))
                .then(callback)
                .catch(errback);
        });
        producer.on('@setrtpencodingparameters', (params, callback, errback) => {
            this._awaitQueue.push(async () => (this._handler.setRtpEncodingParameters(producer.localId, params)))
                .then(callback)
                .catch(errback);
        });
        producer.on('@getstats', (callback, errback) => {
            if (this._closed)
                return errback(new errors_1.InvalidStateError('closed'));
            this._handler.getSenderStats(producer.localId)
                .then(callback)
                .catch(errback);
        });
    }
    _handleConsumer(consumer) {
        consumer.on('@close', () => {
            this._consumers.delete(consumer.id);
            if (this._closed)
                return;
            this._awaitQueue.push(async () => this._handler.stopReceiving(consumer.localId))
                .catch(() => { });
        });
        consumer.on('@getstats', (callback, errback) => {
            if (this._closed)
                return errback(new errors_1.InvalidStateError('closed'));
            this._handler.getReceiverStats(consumer.localId)
                .then(callback)
                .catch(errback);
        });
    }
    _handleDataProducer(dataProducer) {
        dataProducer.on('@close', () => {
            this._dataProducers.delete(dataProducer.id);
        });
    }
    _handleDataConsumer(dataConsumer) {
        dataConsumer.on('@close', () => {
            this._dataConsumers.delete(dataConsumer.id);
        });
    }
}
exports.Transport = Transport;

},{"./Consumer":39,"./DataConsumer":40,"./DataProducer":41,"./EnhancedEventEmitter":43,"./Logger":44,"./Producer":45,"./errors":47,"./ortc":65,"./utils":68,"awaitqueue":6}],47:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * Error indicating not support for something.
 */
class UnsupportedError extends Error {
    constructor(message) {
        super(message);
        this.name = 'UnsupportedError';
        if (Error.hasOwnProperty('captureStackTrace')) // Just in V8.
         {
            // @ts-ignore
            Error.captureStackTrace(this, UnsupportedError);
        }
        else {
            this.stack = (new Error(message)).stack;
        }
    }
}
exports.UnsupportedError = UnsupportedError;
/**
 * Error produced when calling a method in an invalid state.
 */
class InvalidStateError extends Error {
    constructor(message) {
        super(message);
        this.name = 'InvalidStateError';
        if (Error.hasOwnProperty('captureStackTrace')) // Just in V8.
         {
            // @ts-ignore
            Error.captureStackTrace(this, InvalidStateError);
        }
        else {
            this.stack = (new Error(message)).stack;
        }
    }
}
exports.InvalidStateError = InvalidStateError;

},{}],48:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sdpTransform = __importStar(require("sdp-transform"));
const Logger_1 = require("../Logger");
const errors_1 = require("../errors");
const utils = __importStar(require("../utils"));
const ortc = __importStar(require("../ortc"));
const sdpCommonUtils = __importStar(require("./sdp/commonUtils"));
const sdpPlanBUtils = __importStar(require("./sdp/planBUtils"));
const HandlerInterface_1 = require("./HandlerInterface");
const RemoteSdp_1 = require("./sdp/RemoteSdp");
const logger = new Logger_1.Logger('Chrome55');
const SCTP_NUM_STREAMS = { OS: 1024, MIS: 1024 };
class Chrome55 extends HandlerInterface_1.HandlerInterface {
    constructor() {
        super();
        // Local stream for sending.
        this._sendStream = new MediaStream();
        // Map of sending MediaStreamTracks indexed by localId.
        this._mapSendLocalIdTrack = new Map();
        // Next sending localId.
        this._nextSendLocalId = 0;
        // Map of MID, RTP parameters and RTCRtpReceiver indexed by local id.
        // Value is an Object with mid, rtpParameters and rtpReceiver.
        this._mapRecvLocalIdInfo = new Map();
        // Whether a DataChannel m=application section has been created.
        this._hasDataChannelMediaSection = false;
        // Sending DataChannel id value counter. Incremented for each new DataChannel.
        this._nextSendSctpStreamId = 0;
        // Got transport local and remote parameters.
        this._transportReady = false;
    }
    /**
     * Creates a factory function.
     */
    static createFactory() {
        return () => new Chrome55();
    }
    get name() {
        return 'Chrome55';
    }
    close() {
        logger.debug('close()');
        // Close RTCPeerConnection.
        if (this._pc) {
            try {
                this._pc.close();
            }
            catch (error) { }
        }
    }
    async getNativeRtpCapabilities() {
        logger.debug('getNativeRtpCapabilities()');
        const pc = new RTCPeerConnection({
            iceServers: [],
            iceTransportPolicy: 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'plan-b'
        });
        try {
            const offer = await pc.createOffer({
                offerToReceiveAudio: true,
                offerToReceiveVideo: true
            });
            try {
                pc.close();
            }
            catch (error) { }
            const sdpObject = sdpTransform.parse(offer.sdp);
            const nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities({ sdpObject });
            return nativeRtpCapabilities;
        }
        catch (error) {
            try {
                pc.close();
            }
            catch (error2) { }
            throw error;
        }
    }
    async getNativeSctpCapabilities() {
        logger.debug('getNativeSctpCapabilities()');
        return {
            numStreams: SCTP_NUM_STREAMS
        };
    }
    run({ direction, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, extendedRtpCapabilities }) {
        logger.debug('run()');
        this._direction = direction;
        this._remoteSdp = new RemoteSdp_1.RemoteSdp({
            iceParameters,
            iceCandidates,
            dtlsParameters,
            sctpParameters,
            planB: true
        });
        this._sendingRtpParametersByKind =
            {
                audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
            };
        this._sendingRemoteRtpParametersByKind =
            {
                audio: ortc.getSendingRemoteRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRemoteRtpParameters('video', extendedRtpCapabilities)
            };
        this._pc = new RTCPeerConnection({
            iceServers: iceServers || [],
            iceTransportPolicy: iceTransportPolicy || 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'plan-b',
            ...additionalSettings
        }, proprietaryConstraints);
        // Handle RTCPeerConnection connection status.
        this._pc.addEventListener('iceconnectionstatechange', () => {
            switch (this._pc.iceConnectionState) {
                case 'checking':
                    this.emit('@connectionstatechange', 'connecting');
                    break;
                case 'connected':
                case 'completed':
                    this.emit('@connectionstatechange', 'connected');
                    break;
                case 'failed':
                    this.emit('@connectionstatechange', 'failed');
                    break;
                case 'disconnected':
                    this.emit('@connectionstatechange', 'disconnected');
                    break;
                case 'closed':
                    this.emit('@connectionstatechange', 'closed');
                    break;
            }
        });
    }
    async updateIceServers(iceServers) {
        logger.debug('updateIceServers()');
        const configuration = this._pc.getConfiguration();
        configuration.iceServers = iceServers;
        this._pc.setConfiguration(configuration);
    }
    async restartIce(iceParameters) {
        logger.debug('restartIce()');
        // Provide the remote SDP handler with new remote ICE parameters.
        this._remoteSdp.updateIceParameters(iceParameters);
        if (!this._transportReady)
            return;
        if (this._direction === 'send') {
            const offer = await this._pc.createOffer({ iceRestart: true });
            logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
        }
        else {
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
        }
    }
    async getTransportStats() {
        return this._pc.getStats();
    }
    async send({ track, encodings, codecOptions }) {
        this._assertSendDirection();
        logger.debug('send() [kind:%s, track.id:%s]', track.kind, track.id);
        this._sendStream.addTrack(track);
        this._pc.addStream(this._sendStream);
        let offer = await this._pc.createOffer();
        let localSdpObject = sdpTransform.parse(offer.sdp);
        let offerMediaObject;
        const sendingRtpParameters = utils.clone(this._sendingRtpParametersByKind[track.kind]);
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
        if (track.kind === 'video' && encodings && encodings.length > 1) {
            logger.debug('send() | enabling simulcast');
            localSdpObject = sdpTransform.parse(offer.sdp);
            offerMediaObject = localSdpObject.media.find((m) => m.type === 'video');
            sdpPlanBUtils.addLegacySimulcast({
                offerMediaObject,
                track,
                numStreams: encodings.length
            });
            offer = { type: 'offer', sdp: sdpTransform.write(localSdpObject) };
        }
        logger.debug('send() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        offerMediaObject = localSdpObject.media
            .find((m) => m.type === track.kind);
        // Set RTCP CNAME.
        sendingRtpParameters.rtcp.cname =
            sdpCommonUtils.getCname({ offerMediaObject });
        // Set RTP encodings.
        sendingRtpParameters.encodings =
            sdpPlanBUtils.getRtpEncodings({ offerMediaObject, track });
        // Complete encodings with given values.
        if (encodings) {
            for (let idx = 0; idx < sendingRtpParameters.encodings.length; ++idx) {
                if (encodings[idx])
                    Object.assign(sendingRtpParameters.encodings[idx], encodings[idx]);
            }
        }
        // If VP8 and there is effective simulcast, add scalabilityMode to each
        // encoding.
        if (sendingRtpParameters.encodings.length > 1 &&
            sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/vp8') {
            for (const encoding of sendingRtpParameters.encodings) {
                encoding.scalabilityMode = 'S1T3';
            }
        }
        this._remoteSdp.send({
            offerMediaObject,
            offerRtpParameters: sendingRtpParameters,
            answerRtpParameters: this._sendingRemoteRtpParametersByKind[track.kind],
            codecOptions
        });
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('send() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
        const localId = String(this._nextSendLocalId);
        this._nextSendLocalId++;
        // Insert into the map.
        this._mapSendLocalIdTrack.set(localId, track);
        return {
            localId: localId,
            rtpParameters: sendingRtpParameters
        };
    }
    async stopSending(localId) {
        this._assertSendDirection();
        logger.debug('stopSending() [localId:%s]', localId);
        const track = this._mapSendLocalIdTrack.get(localId);
        if (!track)
            throw new Error('track not found');
        this._mapSendLocalIdTrack.delete(localId);
        this._sendStream.removeTrack(track);
        this._pc.addStream(this._sendStream);
        const offer = await this._pc.createOffer();
        logger.debug('stopSending() | calling pc.setLocalDescription() [offer:%o]', offer);
        try {
            await this._pc.setLocalDescription(offer);
        }
        catch (error) {
            // NOTE: If there are no sending tracks, setLocalDescription() will fail with
            // "Failed to create channels". If so, ignore it.
            if (this._sendStream.getTracks().length === 0) {
                logger.warn('stopSending() | ignoring expected error due no sending tracks: %s', error.toString());
                return;
            }
            throw error;
        }
        if (this._pc.signalingState === 'stable')
            return;
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopSending() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async replaceTrack(localId, track) {
        throw new errors_1.UnsupportedError('not implemented');
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async setMaxSpatialLayer(localId, spatialLayer) {
        throw new errors_1.UnsupportedError(' not implemented');
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async setRtpEncodingParameters(localId, params) {
        throw new errors_1.UnsupportedError('not supported');
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async getSenderStats(localId) {
        throw new errors_1.UnsupportedError('not implemented');
    }
    async sendDataChannel({ ordered, maxPacketLifeTime, maxRetransmits, label, protocol, priority }) {
        this._assertSendDirection();
        const options = {
            negotiated: true,
            id: this._nextSendSctpStreamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmitTime: maxPacketLifeTime,
            maxRetransmits,
            protocol,
            priority
        };
        logger.debug('sendDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // Increase next id.
        this._nextSendSctpStreamId =
            ++this._nextSendSctpStreamId % SCTP_NUM_STREAMS.MIS;
        // If this is the first DataChannel we need to create the SDP answer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            const offer = await this._pc.createOffer();
            const localSdpObject = sdpTransform.parse(offer.sdp);
            const offerMediaObject = localSdpObject.media
                .find((m) => m.type === 'application');
            if (!this._transportReady)
                await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
            logger.debug('sendDataChannel() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            this._remoteSdp.sendSctpAssociation({ offerMediaObject });
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('sendDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        const sctpStreamParameters = {
            streamId: options.id,
            ordered: options.ordered,
            maxPacketLifeTime: options.maxPacketLifeTime,
            maxRetransmits: options.maxRetransmits
        };
        return { dataChannel, sctpStreamParameters };
    }
    async receive({ trackId, kind, rtpParameters }) {
        this._assertRecvDirection();
        logger.debug('receive() [trackId:%s, kind:%s]', trackId, kind);
        const localId = trackId;
        const mid = kind;
        const streamId = rtpParameters.rtcp.cname;
        this._remoteSdp.receive({
            mid,
            kind,
            offerRtpParameters: rtpParameters,
            streamId,
            trackId
        });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('receive() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        let answer = await this._pc.createAnswer();
        const localSdpObject = sdpTransform.parse(answer.sdp);
        const answerMediaObject = localSdpObject.media
            .find((m) => String(m.mid) === mid);
        // May need to modify codec parameters in the answer based on codec
        // parameters in the offer.
        sdpCommonUtils.applyCodecParameters({
            offerRtpParameters: rtpParameters,
            answerMediaObject
        });
        answer = { type: 'answer', sdp: sdpTransform.write(localSdpObject) };
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
        logger.debug('receive() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
        const stream = this._pc.getRemoteStreams()
            .find((s) => s.id === streamId);
        const track = stream.getTrackById(localId);
        if (!track)
            throw new Error('remote track not found');
        // Insert into the map.
        this._mapRecvLocalIdInfo.set(localId, { mid, rtpParameters });
        return { localId, track };
    }
    async stopReceiving(localId) {
        this._assertRecvDirection();
        logger.debug('stopReceiving() [localId:%s]', localId);
        const { mid, rtpParameters } = this._mapRecvLocalIdInfo.get(localId);
        // Remove from the map.
        this._mapRecvLocalIdInfo.delete(localId);
        this._remoteSdp.planBStopReceiving({ mid, offerRtpParameters: rtpParameters });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopReceiving() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        const answer = await this._pc.createAnswer();
        logger.debug('stopReceiving() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async getReceiverStats(localId) {
        throw new errors_1.UnsupportedError('not implemented');
    }
    async receiveDataChannel({ sctpStreamParameters, label, protocol }) {
        this._assertRecvDirection();
        const { streamId, ordered, maxPacketLifeTime, maxRetransmits } = sctpStreamParameters;
        const options = {
            negotiated: true,
            id: streamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmitTime: maxPacketLifeTime,
            maxRetransmits,
            protocol
        };
        logger.debug('receiveDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // If this is the first DataChannel we need to create the SDP offer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            this._remoteSdp.receiveSctpAssociation({ oldDataChannelSpec: true });
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            if (!this._transportReady) {
                const localSdpObject = sdpTransform.parse(answer.sdp);
                await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
            }
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        return { dataChannel };
    }
    async _setupTransport({ localDtlsRole, localSdpObject }) {
        if (!localSdpObject)
            localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        // Get our local DTLS parameters.
        const dtlsParameters = sdpCommonUtils.extractDtlsParameters({ sdpObject: localSdpObject });
        // Set our DTLS role.
        dtlsParameters.role = localDtlsRole;
        // Update the remote DTLS role in the SDP.
        this._remoteSdp.updateDtlsRole(localDtlsRole === 'client' ? 'server' : 'client');
        // Need to tell the remote transport about our parameters.
        await this.safeEmitAsPromise('@connect', { dtlsParameters });
        this._transportReady = true;
    }
    _assertSendDirection() {
        if (this._direction !== 'send') {
            throw new Error('method can just be called for handlers with "send" direction');
        }
    }
    _assertRecvDirection() {
        if (this._direction !== 'recv') {
            throw new Error('method can just be called for handlers with "recv" direction');
        }
    }
}
exports.Chrome55 = Chrome55;

},{"../Logger":44,"../errors":47,"../ortc":65,"../utils":68,"./HandlerInterface":54,"./sdp/RemoteSdp":60,"./sdp/commonUtils":61,"./sdp/planBUtils":62,"sdp-transform":71}],49:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sdpTransform = __importStar(require("sdp-transform"));
const Logger_1 = require("../Logger");
const utils = __importStar(require("../utils"));
const ortc = __importStar(require("../ortc"));
const sdpCommonUtils = __importStar(require("./sdp/commonUtils"));
const sdpPlanBUtils = __importStar(require("./sdp/planBUtils"));
const HandlerInterface_1 = require("./HandlerInterface");
const RemoteSdp_1 = require("./sdp/RemoteSdp");
const logger = new Logger_1.Logger('Chrome67');
const SCTP_NUM_STREAMS = { OS: 1024, MIS: 1024 };
class Chrome67 extends HandlerInterface_1.HandlerInterface {
    constructor() {
        super();
        // Local stream for sending.
        this._sendStream = new MediaStream();
        // Map of sending MediaStreamTracks indexed by localId.
        this._mapSendLocalIdTrack = new Map();
        // Next sending localId.
        this._nextSendLocalId = 0;
        // Map of MID, RTP parameters and RTCRtpReceiver indexed by local id.
        // Value is an Object with mid, rtpParameters and rtpReceiver.
        this._mapRecvLocalIdInfo = new Map();
        // Whether a DataChannel m=application section has been created.
        this._hasDataChannelMediaSection = false;
        // Sending DataChannel id value counter. Incremented for each new DataChannel.
        this._nextSendSctpStreamId = 0;
        // Got transport local and remote parameters.
        this._transportReady = false;
    }
    /**
     * Creates a factory function.
     */
    static createFactory() {
        return () => new Chrome67();
    }
    get name() {
        return 'Chrome67';
    }
    close() {
        logger.debug('close()');
        // Close RTCPeerConnection.
        if (this._pc) {
            try {
                this._pc.close();
            }
            catch (error) { }
        }
    }
    async getNativeRtpCapabilities() {
        logger.debug('getNativeRtpCapabilities()');
        const pc = new RTCPeerConnection({
            iceServers: [],
            iceTransportPolicy: 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'plan-b'
        });
        try {
            const offer = await pc.createOffer({
                offerToReceiveAudio: true,
                offerToReceiveVideo: true
            });
            try {
                pc.close();
            }
            catch (error) { }
            const sdpObject = sdpTransform.parse(offer.sdp);
            const nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities({ sdpObject });
            return nativeRtpCapabilities;
        }
        catch (error) {
            try {
                pc.close();
            }
            catch (error2) { }
            throw error;
        }
    }
    async getNativeSctpCapabilities() {
        logger.debug('getNativeSctpCapabilities()');
        return {
            numStreams: SCTP_NUM_STREAMS
        };
    }
    run({ direction, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, extendedRtpCapabilities }) {
        logger.debug('run()');
        this._direction = direction;
        this._remoteSdp = new RemoteSdp_1.RemoteSdp({
            iceParameters,
            iceCandidates,
            dtlsParameters,
            sctpParameters,
            planB: true
        });
        this._sendingRtpParametersByKind =
            {
                audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
            };
        this._sendingRemoteRtpParametersByKind =
            {
                audio: ortc.getSendingRemoteRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRemoteRtpParameters('video', extendedRtpCapabilities)
            };
        this._pc = new RTCPeerConnection({
            iceServers: iceServers || [],
            iceTransportPolicy: iceTransportPolicy || 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'plan-b',
            ...additionalSettings
        }, proprietaryConstraints);
        // Handle RTCPeerConnection connection status.
        this._pc.addEventListener('iceconnectionstatechange', () => {
            switch (this._pc.iceConnectionState) {
                case 'checking':
                    this.emit('@connectionstatechange', 'connecting');
                    break;
                case 'connected':
                case 'completed':
                    this.emit('@connectionstatechange', 'connected');
                    break;
                case 'failed':
                    this.emit('@connectionstatechange', 'failed');
                    break;
                case 'disconnected':
                    this.emit('@connectionstatechange', 'disconnected');
                    break;
                case 'closed':
                    this.emit('@connectionstatechange', 'closed');
                    break;
            }
        });
    }
    async updateIceServers(iceServers) {
        logger.debug('updateIceServers()');
        const configuration = this._pc.getConfiguration();
        configuration.iceServers = iceServers;
        this._pc.setConfiguration(configuration);
    }
    async restartIce(iceParameters) {
        logger.debug('restartIce()');
        // Provide the remote SDP handler with new remote ICE parameters.
        this._remoteSdp.updateIceParameters(iceParameters);
        if (!this._transportReady)
            return;
        if (this._direction === 'send') {
            const offer = await this._pc.createOffer({ iceRestart: true });
            logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
        }
        else {
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
        }
    }
    async getTransportStats() {
        return this._pc.getStats();
    }
    async send({ track, encodings, codecOptions }) {
        this._assertSendDirection();
        logger.debug('send() [kind:%s, track.id:%s]', track.kind, track.id);
        this._sendStream.addTrack(track);
        this._pc.addTrack(track, this._sendStream);
        let offer = await this._pc.createOffer();
        let localSdpObject = sdpTransform.parse(offer.sdp);
        let offerMediaObject;
        const sendingRtpParameters = utils.clone(this._sendingRtpParametersByKind[track.kind]);
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
        if (track.kind === 'video' && encodings && encodings.length > 1) {
            logger.debug('send() | enabling simulcast');
            localSdpObject = sdpTransform.parse(offer.sdp);
            offerMediaObject = localSdpObject.media
                .find((m) => m.type === 'video');
            sdpPlanBUtils.addLegacySimulcast({
                offerMediaObject,
                track,
                numStreams: encodings.length
            });
            offer = { type: 'offer', sdp: sdpTransform.write(localSdpObject) };
        }
        logger.debug('send() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        offerMediaObject = localSdpObject.media
            .find((m) => m.type === track.kind);
        // Set RTCP CNAME.
        sendingRtpParameters.rtcp.cname =
            sdpCommonUtils.getCname({ offerMediaObject });
        // Set RTP encodings.
        sendingRtpParameters.encodings =
            sdpPlanBUtils.getRtpEncodings({ offerMediaObject, track });
        // Complete encodings with given values.
        if (encodings) {
            for (let idx = 0; idx < sendingRtpParameters.encodings.length; ++idx) {
                if (encodings[idx])
                    Object.assign(sendingRtpParameters.encodings[idx], encodings[idx]);
            }
        }
        // If VP8 and there is effective simulcast, add scalabilityMode to each
        // encoding.
        if (sendingRtpParameters.encodings.length > 1 &&
            sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/vp8') {
            for (const encoding of sendingRtpParameters.encodings) {
                encoding.scalabilityMode = 'S1T3';
            }
        }
        this._remoteSdp.send({
            offerMediaObject,
            offerRtpParameters: sendingRtpParameters,
            answerRtpParameters: this._sendingRemoteRtpParametersByKind[track.kind],
            codecOptions
        });
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('send() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
        const localId = String(this._nextSendLocalId);
        this._nextSendLocalId++;
        // Insert into the map.
        this._mapSendLocalIdTrack.set(localId, track);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === track);
        return {
            localId: localId,
            rtpParameters: sendingRtpParameters,
            rtpSender
        };
    }
    async stopSending(localId) {
        this._assertSendDirection();
        logger.debug('stopSending() [localId:%s]', localId);
        const track = this._mapSendLocalIdTrack.get(localId);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === track);
        if (!rtpSender)
            throw new Error('associated RTCRtpSender not found');
        this._pc.removeTrack(rtpSender);
        this._sendStream.removeTrack(track);
        this._mapSendLocalIdTrack.delete(localId);
        const offer = await this._pc.createOffer();
        logger.debug('stopSending() | calling pc.setLocalDescription() [offer:%o]', offer);
        try {
            await this._pc.setLocalDescription(offer);
        }
        catch (error) {
            // NOTE: If there are no sending tracks, setLocalDescription() will fail with
            // "Failed to create channels". If so, ignore it.
            if (this._sendStream.getTracks().length === 0) {
                logger.warn('stopSending() | ignoring expected error due no sending tracks: %s', error.toString());
                return;
            }
            throw error;
        }
        if (this._pc.signalingState === 'stable')
            return;
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopSending() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
    }
    async replaceTrack(localId, track) {
        this._assertSendDirection();
        logger.debug('replaceTrack() [localId:%s, track.id:%s]', localId, track.id);
        const oldTrack = this._mapSendLocalIdTrack.get(localId);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === oldTrack);
        if (!rtpSender)
            throw new Error('associated RTCRtpSender not found');
        await rtpSender.replaceTrack(track);
        // Remove the old track from the local stream.
        this._sendStream.removeTrack(oldTrack);
        // Add the new track to the local stream.
        this._sendStream.addTrack(track);
        // Replace entry in the map.
        this._mapSendLocalIdTrack.set(localId, track);
    }
    async setMaxSpatialLayer(localId, spatialLayer) {
        this._assertSendDirection();
        logger.debug('setMaxSpatialLayer() [localId:%s, spatialLayer:%s]', localId, spatialLayer);
        const track = this._mapSendLocalIdTrack.get(localId);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === track);
        if (!rtpSender)
            throw new Error('associated RTCRtpSender not found');
        const parameters = rtpSender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            if (idx <= spatialLayer)
                encoding.active = true;
            else
                encoding.active = false;
        });
        await rtpSender.setParameters(parameters);
    }
    async setRtpEncodingParameters(localId, params) {
        this._assertSendDirection();
        logger.debug('setRtpEncodingParameters() [localId:%s, params:%o]', localId, params);
        const track = this._mapSendLocalIdTrack.get(localId);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === track);
        if (!rtpSender)
            throw new Error('associated RTCRtpSender not found');
        const parameters = rtpSender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            parameters.encodings[idx] = { ...encoding, ...params };
        });
        await rtpSender.setParameters(parameters);
    }
    async getSenderStats(localId) {
        this._assertSendDirection();
        const track = this._mapSendLocalIdTrack.get(localId);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === track);
        if (!rtpSender)
            throw new Error('associated RTCRtpSender not found');
        return rtpSender.getStats();
    }
    async sendDataChannel({ ordered, maxPacketLifeTime, maxRetransmits, label, protocol, priority }) {
        this._assertSendDirection();
        const options = {
            negotiated: true,
            id: this._nextSendSctpStreamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmitTime: maxPacketLifeTime,
            maxRetransmits,
            protocol,
            priority
        };
        logger.debug('sendDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // Increase next id.
        this._nextSendSctpStreamId =
            ++this._nextSendSctpStreamId % SCTP_NUM_STREAMS.MIS;
        // If this is the first DataChannel we need to create the SDP answer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            const offer = await this._pc.createOffer();
            const localSdpObject = sdpTransform.parse(offer.sdp);
            const offerMediaObject = localSdpObject.media
                .find((m) => m.type === 'application');
            if (!this._transportReady)
                await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
            logger.debug('sendDataChannel() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            this._remoteSdp.sendSctpAssociation({ offerMediaObject });
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('sendDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        const sctpStreamParameters = {
            streamId: options.id,
            ordered: options.ordered,
            maxPacketLifeTime: options.maxPacketLifeTime,
            maxRetransmits: options.maxRetransmits
        };
        return { dataChannel, sctpStreamParameters };
    }
    async receive({ trackId, kind, rtpParameters }) {
        this._assertRecvDirection();
        logger.debug('receive() [trackId:%s, kind:%s]', trackId, kind);
        const localId = trackId;
        const mid = kind;
        this._remoteSdp.receive({
            mid,
            kind,
            offerRtpParameters: rtpParameters,
            streamId: rtpParameters.rtcp.cname,
            trackId
        });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('receive() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        let answer = await this._pc.createAnswer();
        const localSdpObject = sdpTransform.parse(answer.sdp);
        const answerMediaObject = localSdpObject.media
            .find((m) => String(m.mid) === mid);
        // May need to modify codec parameters in the answer based on codec
        // parameters in the offer.
        sdpCommonUtils.applyCodecParameters({
            offerRtpParameters: rtpParameters,
            answerMediaObject
        });
        answer = { type: 'answer', sdp: sdpTransform.write(localSdpObject) };
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
        logger.debug('receive() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
        const rtpReceiver = this._pc.getReceivers()
            .find((r) => r.track && r.track.id === localId);
        if (!rtpReceiver)
            throw new Error('new RTCRtpReceiver not');
        // Insert into the map.
        this._mapRecvLocalIdInfo.set(localId, { mid, rtpParameters, rtpReceiver });
        return {
            localId,
            track: rtpReceiver.track,
            rtpReceiver
        };
    }
    async stopReceiving(localId) {
        this._assertRecvDirection();
        logger.debug('stopReceiving() [localId:%s]', localId);
        const { mid, rtpParameters } = this._mapRecvLocalIdInfo.get(localId);
        // Remove from the map.
        this._mapRecvLocalIdInfo.delete(localId);
        this._remoteSdp.planBStopReceiving({ mid, offerRtpParameters: rtpParameters });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopReceiving() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        const answer = await this._pc.createAnswer();
        logger.debug('stopReceiving() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
    }
    async getReceiverStats(localId) {
        this._assertRecvDirection();
        const { rtpReceiver } = this._mapRecvLocalIdInfo.get(localId);
        if (!rtpReceiver)
            throw new Error('associated RTCRtpReceiver not found');
        return rtpReceiver.getStats();
    }
    async receiveDataChannel({ sctpStreamParameters, label, protocol }) {
        this._assertRecvDirection();
        const { streamId, ordered, maxPacketLifeTime, maxRetransmits } = sctpStreamParameters;
        const options = {
            negotiated: true,
            id: streamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmitTime: maxPacketLifeTime,
            maxRetransmits,
            protocol
        };
        logger.debug('receiveDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // If this is the first DataChannel we need to create the SDP offer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            this._remoteSdp.receiveSctpAssociation({ oldDataChannelSpec: true });
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            if (!this._transportReady) {
                const localSdpObject = sdpTransform.parse(answer.sdp);
                await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
            }
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        return { dataChannel };
    }
    async _setupTransport({ localDtlsRole, localSdpObject }) {
        if (!localSdpObject)
            localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        // Get our local DTLS parameters.
        const dtlsParameters = sdpCommonUtils.extractDtlsParameters({ sdpObject: localSdpObject });
        // Set our DTLS role.
        dtlsParameters.role = localDtlsRole;
        // Update the remote DTLS role in the SDP.
        this._remoteSdp.updateDtlsRole(localDtlsRole === 'client' ? 'server' : 'client');
        // Need to tell the remote transport about our parameters.
        await this.safeEmitAsPromise('@connect', { dtlsParameters });
        this._transportReady = true;
    }
    _assertSendDirection() {
        if (this._direction !== 'send') {
            throw new Error('method can just be called for handlers with "send" direction');
        }
    }
    _assertRecvDirection() {
        if (this._direction !== 'recv') {
            throw new Error('method can just be called for handlers with "recv" direction');
        }
    }
}
exports.Chrome67 = Chrome67;

},{"../Logger":44,"../ortc":65,"../utils":68,"./HandlerInterface":54,"./sdp/RemoteSdp":60,"./sdp/commonUtils":61,"./sdp/planBUtils":62,"sdp-transform":71}],50:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sdpTransform = __importStar(require("sdp-transform"));
const Logger_1 = require("../Logger");
const utils = __importStar(require("../utils"));
const ortc = __importStar(require("../ortc"));
const sdpCommonUtils = __importStar(require("./sdp/commonUtils"));
const sdpUnifiedPlanUtils = __importStar(require("./sdp/unifiedPlanUtils"));
const HandlerInterface_1 = require("./HandlerInterface");
const RemoteSdp_1 = require("./sdp/RemoteSdp");
const scalabilityModes_1 = require("../scalabilityModes");
const logger = new Logger_1.Logger('Chrome70');
const SCTP_NUM_STREAMS = { OS: 1024, MIS: 1024 };
class Chrome70 extends HandlerInterface_1.HandlerInterface {
    constructor() {
        super();
        // Map of RTCTransceivers indexed by MID.
        this._mapMidTransceiver = new Map();
        // Local stream for sending.
        this._sendStream = new MediaStream();
        // Whether a DataChannel m=application section has been created.
        this._hasDataChannelMediaSection = false;
        // Sending DataChannel id value counter. Incremented for each new DataChannel.
        this._nextSendSctpStreamId = 0;
        // Got transport local and remote parameters.
        this._transportReady = false;
    }
    /**
     * Creates a factory function.
     */
    static createFactory() {
        return () => new Chrome70();
    }
    get name() {
        return 'Chrome70';
    }
    close() {
        logger.debug('close()');
        // Close RTCPeerConnection.
        if (this._pc) {
            try {
                this._pc.close();
            }
            catch (error) { }
        }
    }
    run({ direction, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, extendedRtpCapabilities }) {
        logger.debug('run()');
        this._direction = direction;
        this._remoteSdp = new RemoteSdp_1.RemoteSdp({
            iceParameters,
            iceCandidates,
            dtlsParameters,
            sctpParameters
        });
        this._sendingRtpParametersByKind =
            {
                audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
            };
        this._sendingRemoteRtpParametersByKind =
            {
                audio: ortc.getSendingRemoteRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRemoteRtpParameters('video', extendedRtpCapabilities)
            };
        this._pc = new RTCPeerConnection({
            iceServers: iceServers || [],
            iceTransportPolicy: iceTransportPolicy || 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'unified-plan',
            ...additionalSettings
        }, proprietaryConstraints);
        // Handle RTCPeerConnection connection status.
        this._pc.addEventListener('iceconnectionstatechange', () => {
            switch (this._pc.iceConnectionState) {
                case 'checking':
                    this.emit('@connectionstatechange', 'connecting');
                    break;
                case 'connected':
                case 'completed':
                    this.emit('@connectionstatechange', 'connected');
                    break;
                case 'failed':
                    this.emit('@connectionstatechange', 'failed');
                    break;
                case 'disconnected':
                    this.emit('@connectionstatechange', 'disconnected');
                    break;
                case 'closed':
                    this.emit('@connectionstatechange', 'closed');
                    break;
            }
        });
    }
    async getNativeRtpCapabilities() {
        logger.debug('getNativeRtpCapabilities()');
        const pc = new RTCPeerConnection({
            iceServers: [],
            iceTransportPolicy: 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'unified-plan'
        });
        try {
            pc.addTransceiver('audio');
            pc.addTransceiver('video');
            const offer = await pc.createOffer();
            try {
                pc.close();
            }
            catch (error) { }
            const sdpObject = sdpTransform.parse(offer.sdp);
            const nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities({ sdpObject });
            return nativeRtpCapabilities;
        }
        catch (error) {
            try {
                pc.close();
            }
            catch (error2) { }
            throw error;
        }
    }
    async getNativeSctpCapabilities() {
        logger.debug('getNativeSctpCapabilities()');
        return {
            numStreams: SCTP_NUM_STREAMS
        };
    }
    async updateIceServers(iceServers) {
        logger.debug('updateIceServers()');
        const configuration = this._pc.getConfiguration();
        configuration.iceServers = iceServers;
        this._pc.setConfiguration(configuration);
    }
    async restartIce(iceParameters) {
        logger.debug('restartIce()');
        // Provide the remote SDP handler with new remote ICE parameters.
        this._remoteSdp.updateIceParameters(iceParameters);
        if (!this._transportReady)
            return;
        if (this._direction === 'send') {
            const offer = await this._pc.createOffer({ iceRestart: true });
            logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
        }
        else {
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
        }
    }
    async getTransportStats() {
        return this._pc.getStats();
    }
    async send({ track, encodings, codecOptions }) {
        this._assertSendDirection();
        logger.debug('send() [kind:%s, track.id:%s]', track.kind, track.id);
        const mediaSectionIdx = this._remoteSdp.getNextMediaSectionIdx();
        const transceiver = this._pc.addTransceiver(track, { direction: 'sendonly', streams: [this._sendStream] });
        let offer = await this._pc.createOffer();
        let localSdpObject = sdpTransform.parse(offer.sdp);
        let offerMediaObject;
        const sendingRtpParameters = utils.clone(this._sendingRtpParametersByKind[track.kind]);
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
        if (encodings && encodings.length > 1) {
            logger.debug('send() | enabling legacy simulcast');
            localSdpObject = sdpTransform.parse(offer.sdp);
            offerMediaObject = localSdpObject.media[mediaSectionIdx.idx];
            sdpUnifiedPlanUtils.addLegacySimulcast({
                offerMediaObject,
                numStreams: encodings.length
            });
            offer = { type: 'offer', sdp: sdpTransform.write(localSdpObject) };
        }
        // Special case for VP9 with SVC.
        let hackVp9Svc = false;
        const layers = scalabilityModes_1.parse((encodings || [{}])[0].scalabilityMode);
        if (encodings &&
            encodings.length === 1 &&
            layers.spatialLayers > 1 &&
            sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/vp9') {
            logger.debug('send() | enabling legacy simulcast for VP9 SVC');
            hackVp9Svc = true;
            localSdpObject = sdpTransform.parse(offer.sdp);
            offerMediaObject = localSdpObject.media[mediaSectionIdx.idx];
            sdpUnifiedPlanUtils.addLegacySimulcast({
                offerMediaObject,
                numStreams: layers.spatialLayers
            });
            offer = { type: 'offer', sdp: sdpTransform.write(localSdpObject) };
        }
        logger.debug('send() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        // We can now get the transceiver.mid.
        const localId = transceiver.mid;
        // Set MID.
        sendingRtpParameters.mid = localId;
        localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        offerMediaObject = localSdpObject.media[mediaSectionIdx.idx];
        // Set RTCP CNAME.
        sendingRtpParameters.rtcp.cname =
            sdpCommonUtils.getCname({ offerMediaObject });
        // Set RTP encodings.
        sendingRtpParameters.encodings =
            sdpUnifiedPlanUtils.getRtpEncodings({ offerMediaObject });
        // Complete encodings with given values.
        if (encodings) {
            for (let idx = 0; idx < sendingRtpParameters.encodings.length; ++idx) {
                if (encodings[idx])
                    Object.assign(sendingRtpParameters.encodings[idx], encodings[idx]);
            }
        }
        // Hack for VP9 SVC.
        if (hackVp9Svc) {
            sendingRtpParameters.encodings = [sendingRtpParameters.encodings[0]];
        }
        // If VP8 or H264 and there is effective simulcast, add scalabilityMode to
        // each encoding.
        if (sendingRtpParameters.encodings.length > 1 &&
            (sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/vp8' ||
                sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/h264')) {
            for (const encoding of sendingRtpParameters.encodings) {
                encoding.scalabilityMode = 'S1T3';
            }
        }
        this._remoteSdp.send({
            offerMediaObject,
            reuseMid: mediaSectionIdx.reuseMid,
            offerRtpParameters: sendingRtpParameters,
            answerRtpParameters: this._sendingRemoteRtpParametersByKind[track.kind],
            codecOptions
        });
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('send() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
        // Store in the map.
        this._mapMidTransceiver.set(localId, transceiver);
        return {
            localId,
            rtpParameters: sendingRtpParameters,
            rtpSender: transceiver.sender
        };
    }
    async stopSending(localId) {
        this._assertSendDirection();
        logger.debug('stopSending() [localId:%s]', localId);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        transceiver.sender.replaceTrack(null);
        this._pc.removeTrack(transceiver.sender);
        this._remoteSdp.closeMediaSection(transceiver.mid);
        const offer = await this._pc.createOffer();
        logger.debug('stopSending() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopSending() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
    }
    async replaceTrack(localId, track) {
        this._assertSendDirection();
        logger.debug('replaceTrack() [localId:%s, track.id:%s]', localId, track.id);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        await transceiver.sender.replaceTrack(track);
    }
    async setMaxSpatialLayer(localId, spatialLayer) {
        this._assertSendDirection();
        logger.debug('setMaxSpatialLayer() [localId:%s, spatialLayer:%s]', localId, spatialLayer);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        const parameters = transceiver.sender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            if (idx <= spatialLayer)
                encoding.active = true;
            else
                encoding.active = false;
        });
        await transceiver.sender.setParameters(parameters);
    }
    async setRtpEncodingParameters(localId, params) {
        this._assertSendDirection();
        logger.debug('setRtpEncodingParameters() [localId:%s, params:%o]', localId, params);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        const parameters = transceiver.sender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            parameters.encodings[idx] = { ...encoding, ...params };
        });
        await transceiver.sender.setParameters(parameters);
    }
    async getSenderStats(localId) {
        this._assertSendDirection();
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        return transceiver.sender.getStats();
    }
    async sendDataChannel({ ordered, maxPacketLifeTime, maxRetransmits, label, protocol, priority }) {
        this._assertSendDirection();
        const options = {
            negotiated: true,
            id: this._nextSendSctpStreamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmitTime: maxPacketLifeTime,
            maxRetransmits,
            protocol,
            priority
        };
        logger.debug('sendDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // Increase next id.
        this._nextSendSctpStreamId =
            ++this._nextSendSctpStreamId % SCTP_NUM_STREAMS.MIS;
        // If this is the first DataChannel we need to create the SDP answer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            const offer = await this._pc.createOffer();
            const localSdpObject = sdpTransform.parse(offer.sdp);
            const offerMediaObject = localSdpObject.media
                .find((m) => m.type === 'application');
            if (!this._transportReady)
                await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
            logger.debug('sendDataChannel() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            this._remoteSdp.sendSctpAssociation({ offerMediaObject });
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('sendDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        const sctpStreamParameters = {
            streamId: options.id,
            ordered: options.ordered,
            maxPacketLifeTime: options.maxPacketLifeTime,
            maxRetransmits: options.maxRetransmits
        };
        return { dataChannel, sctpStreamParameters };
    }
    async receive({ trackId, kind, rtpParameters }) {
        this._assertRecvDirection();
        logger.debug('receive() [trackId:%s, kind:%s]', trackId, kind);
        const localId = String(this._mapMidTransceiver.size);
        this._remoteSdp.receive({
            mid: localId,
            kind,
            offerRtpParameters: rtpParameters,
            streamId: rtpParameters.rtcp.cname,
            trackId
        });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('receive() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        let answer = await this._pc.createAnswer();
        const localSdpObject = sdpTransform.parse(answer.sdp);
        const answerMediaObject = localSdpObject.media
            .find((m) => String(m.mid) === localId);
        // May need to modify codec parameters in the answer based on codec
        // parameters in the offer.
        sdpCommonUtils.applyCodecParameters({
            offerRtpParameters: rtpParameters,
            answerMediaObject
        });
        answer = { type: 'answer', sdp: sdpTransform.write(localSdpObject) };
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
        logger.debug('receive() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
        const transceiver = this._pc.getTransceivers()
            .find((t) => t.mid === localId);
        if (!transceiver)
            throw new Error('new RTCRtpTransceiver not found');
        // Store in the map.
        this._mapMidTransceiver.set(localId, transceiver);
        return {
            localId,
            track: transceiver.receiver.track,
            rtpReceiver: transceiver.receiver
        };
    }
    async stopReceiving(localId) {
        this._assertRecvDirection();
        logger.debug('stopReceiving() [localId:%s]', localId);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        this._remoteSdp.closeMediaSection(transceiver.mid);
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopReceiving() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        const answer = await this._pc.createAnswer();
        logger.debug('stopReceiving() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
    }
    async getReceiverStats(localId) {
        this._assertRecvDirection();
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        return transceiver.receiver.getStats();
    }
    async receiveDataChannel({ sctpStreamParameters, label, protocol }) {
        this._assertRecvDirection();
        const { streamId, ordered, maxPacketLifeTime, maxRetransmits } = sctpStreamParameters;
        const options = {
            negotiated: true,
            id: streamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmitTime: maxPacketLifeTime,
            maxRetransmits,
            protocol
        };
        logger.debug('receiveDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // If this is the first DataChannel we need to create the SDP offer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            this._remoteSdp.receiveSctpAssociation();
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            if (!this._transportReady) {
                const localSdpObject = sdpTransform.parse(answer.sdp);
                await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
            }
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        return { dataChannel };
    }
    async _setupTransport({ localDtlsRole, localSdpObject }) {
        if (!localSdpObject)
            localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        // Get our local DTLS parameters.
        const dtlsParameters = sdpCommonUtils.extractDtlsParameters({ sdpObject: localSdpObject });
        // Set our DTLS role.
        dtlsParameters.role = localDtlsRole;
        // Update the remote DTLS role in the SDP.
        this._remoteSdp.updateDtlsRole(localDtlsRole === 'client' ? 'server' : 'client');
        // Need to tell the remote transport about our parameters.
        await this.safeEmitAsPromise('@connect', { dtlsParameters });
        this._transportReady = true;
    }
    _assertSendDirection() {
        if (this._direction !== 'send') {
            throw new Error('method can just be called for handlers with "send" direction');
        }
    }
    _assertRecvDirection() {
        if (this._direction !== 'recv') {
            throw new Error('method can just be called for handlers with "recv" direction');
        }
    }
}
exports.Chrome70 = Chrome70;

},{"../Logger":44,"../ortc":65,"../scalabilityModes":66,"../utils":68,"./HandlerInterface":54,"./sdp/RemoteSdp":60,"./sdp/commonUtils":61,"./sdp/unifiedPlanUtils":63,"sdp-transform":71}],51:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sdpTransform = __importStar(require("sdp-transform"));
const Logger_1 = require("../Logger");
const utils = __importStar(require("../utils"));
const ortc = __importStar(require("../ortc"));
const sdpCommonUtils = __importStar(require("./sdp/commonUtils"));
const sdpUnifiedPlanUtils = __importStar(require("./sdp/unifiedPlanUtils"));
const HandlerInterface_1 = require("./HandlerInterface");
const RemoteSdp_1 = require("./sdp/RemoteSdp");
const scalabilityModes_1 = require("../scalabilityModes");
const logger = new Logger_1.Logger('Chrome74');
const SCTP_NUM_STREAMS = { OS: 1024, MIS: 1024 };
class Chrome74 extends HandlerInterface_1.HandlerInterface {
    constructor() {
        super();
        // Map of RTCTransceivers indexed by MID.
        this._mapMidTransceiver = new Map();
        // Local stream for sending.
        this._sendStream = new MediaStream();
        // Whether a DataChannel m=application section has been created.
        this._hasDataChannelMediaSection = false;
        // Sending DataChannel id value counter. Incremented for each new DataChannel.
        this._nextSendSctpStreamId = 0;
        // Got transport local and remote parameters.
        this._transportReady = false;
    }
    /**
     * Creates a factory function.
     */
    static createFactory() {
        return () => new Chrome74();
    }
    get name() {
        return 'Chrome74';
    }
    close() {
        logger.debug('close()');
        // Close RTCPeerConnection.
        if (this._pc) {
            try {
                this._pc.close();
            }
            catch (error) { }
        }
    }
    async getNativeRtpCapabilities() {
        logger.debug('getNativeRtpCapabilities()');
        const pc = new RTCPeerConnection({
            iceServers: [],
            iceTransportPolicy: 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'unified-plan'
        });
        try {
            pc.addTransceiver('audio');
            pc.addTransceiver('video');
            const offer = await pc.createOffer();
            try {
                pc.close();
            }
            catch (error) { }
            const sdpObject = sdpTransform.parse(offer.sdp);
            const nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities({ sdpObject });
            return nativeRtpCapabilities;
        }
        catch (error) {
            try {
                pc.close();
            }
            catch (error2) { }
            throw error;
        }
    }
    async getNativeSctpCapabilities() {
        logger.debug('getNativeSctpCapabilities()');
        return {
            numStreams: SCTP_NUM_STREAMS
        };
    }
    run({ direction, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, extendedRtpCapabilities }) {
        logger.debug('run()');
        this._direction = direction;
        this._remoteSdp = new RemoteSdp_1.RemoteSdp({
            iceParameters,
            iceCandidates,
            dtlsParameters,
            sctpParameters
        });
        this._sendingRtpParametersByKind =
            {
                audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
            };
        this._sendingRemoteRtpParametersByKind =
            {
                audio: ortc.getSendingRemoteRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRemoteRtpParameters('video', extendedRtpCapabilities)
            };
        this._pc = new RTCPeerConnection({
            iceServers: iceServers || [],
            iceTransportPolicy: iceTransportPolicy || 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'unified-plan',
            ...additionalSettings
        }, proprietaryConstraints);
        // Handle RTCPeerConnection connection status.
        this._pc.addEventListener('iceconnectionstatechange', () => {
            switch (this._pc.iceConnectionState) {
                case 'checking':
                    this.emit('@connectionstatechange', 'connecting');
                    break;
                case 'connected':
                case 'completed':
                    this.emit('@connectionstatechange', 'connected');
                    break;
                case 'failed':
                    this.emit('@connectionstatechange', 'failed');
                    break;
                case 'disconnected':
                    this.emit('@connectionstatechange', 'disconnected');
                    break;
                case 'closed':
                    this.emit('@connectionstatechange', 'closed');
                    break;
            }
        });
    }
    async updateIceServers(iceServers) {
        logger.debug('updateIceServers()');
        const configuration = this._pc.getConfiguration();
        configuration.iceServers = iceServers;
        this._pc.setConfiguration(configuration);
    }
    async restartIce(iceParameters) {
        logger.debug('restartIce()');
        // Provide the remote SDP handler with new remote ICE parameters.
        this._remoteSdp.updateIceParameters(iceParameters);
        if (!this._transportReady)
            return;
        if (this._direction === 'send') {
            const offer = await this._pc.createOffer({ iceRestart: true });
            logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
        }
        else {
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
        }
    }
    async getTransportStats() {
        return this._pc.getStats();
    }
    async send({ track, encodings, codecOptions }) {
        this._assertSendDirection();
        logger.debug('send() [kind:%s, track.id:%s]', track.kind, track.id);
        if (encodings && encodings.length > 1) {
            encodings.forEach((encoding, idx) => {
                encoding.rid = `r${idx}`;
            });
        }
        const mediaSectionIdx = this._remoteSdp.getNextMediaSectionIdx();
        const transceiver = this._pc.addTransceiver(track, {
            direction: 'sendonly',
            streams: [this._sendStream],
            sendEncodings: encodings
        });
        let offer = await this._pc.createOffer();
        let localSdpObject = sdpTransform.parse(offer.sdp);
        let offerMediaObject;
        const sendingRtpParameters = utils.clone(this._sendingRtpParametersByKind[track.kind]);
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
        // Special case for VP9 with SVC.
        let hackVp9Svc = false;
        const layers = scalabilityModes_1.parse((encodings || [{}])[0].scalabilityMode);
        if (encodings &&
            encodings.length === 1 &&
            layers.spatialLayers > 1 &&
            sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/vp9') {
            logger.debug('send() | enabling legacy simulcast for VP9 SVC');
            hackVp9Svc = true;
            localSdpObject = sdpTransform.parse(offer.sdp);
            offerMediaObject = localSdpObject.media[mediaSectionIdx.idx];
            sdpUnifiedPlanUtils.addLegacySimulcast({
                offerMediaObject,
                numStreams: layers.spatialLayers
            });
            offer = { type: 'offer', sdp: sdpTransform.write(localSdpObject) };
        }
        logger.debug('send() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        // We can now get the transceiver.mid.
        const localId = transceiver.mid;
        // Set MID.
        sendingRtpParameters.mid = localId;
        localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        offerMediaObject = localSdpObject.media[mediaSectionIdx.idx];
        // Set RTCP CNAME.
        sendingRtpParameters.rtcp.cname =
            sdpCommonUtils.getCname({ offerMediaObject });
        // Set RTP encodings by parsing the SDP offer if no encodings are given.
        if (!encodings) {
            sendingRtpParameters.encodings =
                sdpUnifiedPlanUtils.getRtpEncodings({ offerMediaObject });
        }
        // Set RTP encodings by parsing the SDP offer and complete them with given
        // one if just a single encoding has been given.
        else if (encodings.length === 1) {
            let newEncodings = sdpUnifiedPlanUtils.getRtpEncodings({ offerMediaObject });
            Object.assign(newEncodings[0], encodings[0]);
            // Hack for VP9 SVC.
            if (hackVp9Svc)
                newEncodings = [newEncodings[0]];
            sendingRtpParameters.encodings = newEncodings;
        }
        // Otherwise if more than 1 encoding are given use them verbatim.
        else {
            sendingRtpParameters.encodings = encodings;
        }
        // If VP8 or H264 and there is effective simulcast, add scalabilityMode to
        // each encoding.
        if (sendingRtpParameters.encodings.length > 1 &&
            (sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/vp8' ||
                sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/h264')) {
            for (const encoding of sendingRtpParameters.encodings) {
                encoding.scalabilityMode = 'S1T3';
            }
        }
        this._remoteSdp.send({
            offerMediaObject,
            reuseMid: mediaSectionIdx.reuseMid,
            offerRtpParameters: sendingRtpParameters,
            answerRtpParameters: this._sendingRemoteRtpParametersByKind[track.kind],
            codecOptions,
            extmapAllowMixed: true
        });
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('send() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
        // Store in the map.
        this._mapMidTransceiver.set(localId, transceiver);
        return {
            localId,
            rtpParameters: sendingRtpParameters,
            rtpSender: transceiver.sender
        };
    }
    async stopSending(localId) {
        this._assertSendDirection();
        logger.debug('stopSending() [localId:%s]', localId);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        transceiver.sender.replaceTrack(null);
        this._pc.removeTrack(transceiver.sender);
        this._remoteSdp.closeMediaSection(transceiver.mid);
        const offer = await this._pc.createOffer();
        logger.debug('stopSending() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopSending() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
    }
    async replaceTrack(localId, track) {
        this._assertSendDirection();
        logger.debug('replaceTrack() [localId:%s, track.id:%s]', localId, track.id);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        await transceiver.sender.replaceTrack(track);
    }
    async setMaxSpatialLayer(localId, spatialLayer) {
        this._assertSendDirection();
        logger.debug('setMaxSpatialLayer() [localId:%s, spatialLayer:%s]', localId, spatialLayer);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        const parameters = transceiver.sender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            if (idx <= spatialLayer)
                encoding.active = true;
            else
                encoding.active = false;
        });
        await transceiver.sender.setParameters(parameters);
    }
    async setRtpEncodingParameters(localId, params) {
        this._assertSendDirection();
        logger.debug('setRtpEncodingParameters() [localId:%s, params:%o]', localId, params);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        const parameters = transceiver.sender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            parameters.encodings[idx] = { ...encoding, ...params };
        });
        await transceiver.sender.setParameters(parameters);
    }
    async getSenderStats(localId) {
        this._assertSendDirection();
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        return transceiver.sender.getStats();
    }
    async sendDataChannel({ ordered, maxPacketLifeTime, maxRetransmits, label, protocol, priority }) {
        this._assertSendDirection();
        const options = {
            negotiated: true,
            id: this._nextSendSctpStreamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmits,
            protocol,
            priority
        };
        logger.debug('sendDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // Increase next id.
        this._nextSendSctpStreamId =
            ++this._nextSendSctpStreamId % SCTP_NUM_STREAMS.MIS;
        // If this is the first DataChannel we need to create the SDP answer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            const offer = await this._pc.createOffer();
            const localSdpObject = sdpTransform.parse(offer.sdp);
            const offerMediaObject = localSdpObject.media
                .find((m) => m.type === 'application');
            if (!this._transportReady)
                await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
            logger.debug('sendDataChannel() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            this._remoteSdp.sendSctpAssociation({ offerMediaObject });
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('sendDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        const sctpStreamParameters = {
            streamId: options.id,
            ordered: options.ordered,
            maxPacketLifeTime: options.maxPacketLifeTime,
            maxRetransmits: options.maxRetransmits
        };
        return { dataChannel, sctpStreamParameters };
    }
    async receive({ trackId, kind, rtpParameters }) {
        this._assertRecvDirection();
        logger.debug('receive() [trackId:%s, kind:%s]', trackId, kind);
        const localId = String(this._mapMidTransceiver.size);
        this._remoteSdp.receive({
            mid: localId,
            kind,
            offerRtpParameters: rtpParameters,
            streamId: rtpParameters.rtcp.cname,
            trackId
        });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('receive() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        let answer = await this._pc.createAnswer();
        const localSdpObject = sdpTransform.parse(answer.sdp);
        const answerMediaObject = localSdpObject.media
            .find((m) => String(m.mid) === localId);
        // May need to modify codec parameters in the answer based on codec
        // parameters in the offer.
        sdpCommonUtils.applyCodecParameters({
            offerRtpParameters: rtpParameters,
            answerMediaObject
        });
        answer = { type: 'answer', sdp: sdpTransform.write(localSdpObject) };
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
        logger.debug('receive() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
        const transceiver = this._pc.getTransceivers()
            .find((t) => t.mid === localId);
        if (!transceiver)
            throw new Error('new RTCRtpTransceiver not found');
        // Store in the map.
        this._mapMidTransceiver.set(localId, transceiver);
        return {
            localId,
            track: transceiver.receiver.track,
            rtpReceiver: transceiver.receiver
        };
    }
    async stopReceiving(localId) {
        this._assertRecvDirection();
        logger.debug('stopReceiving() [localId:%s]', localId);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        this._remoteSdp.closeMediaSection(transceiver.mid);
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopReceiving() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        const answer = await this._pc.createAnswer();
        logger.debug('stopReceiving() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
    }
    async getReceiverStats(localId) {
        this._assertRecvDirection();
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        return transceiver.receiver.getStats();
    }
    async receiveDataChannel({ sctpStreamParameters, label, protocol }) {
        this._assertRecvDirection();
        const { streamId, ordered, maxPacketLifeTime, maxRetransmits } = sctpStreamParameters;
        const options = {
            negotiated: true,
            id: streamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmits,
            protocol
        };
        logger.debug('receiveDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // If this is the first DataChannel we need to create the SDP offer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            this._remoteSdp.receiveSctpAssociation();
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            if (!this._transportReady) {
                const localSdpObject = sdpTransform.parse(answer.sdp);
                await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
            }
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        return { dataChannel };
    }
    async _setupTransport({ localDtlsRole, localSdpObject }) {
        if (!localSdpObject)
            localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        // Get our local DTLS parameters.
        const dtlsParameters = sdpCommonUtils.extractDtlsParameters({ sdpObject: localSdpObject });
        // Set our DTLS role.
        dtlsParameters.role = localDtlsRole;
        // Update the remote DTLS role in the SDP.
        this._remoteSdp.updateDtlsRole(localDtlsRole === 'client' ? 'server' : 'client');
        // Need to tell the remote transport about our parameters.
        await this.safeEmitAsPromise('@connect', { dtlsParameters });
        this._transportReady = true;
    }
    _assertSendDirection() {
        if (this._direction !== 'send') {
            throw new Error('method can just be called for handlers with "send" direction');
        }
    }
    _assertRecvDirection() {
        if (this._direction !== 'recv') {
            throw new Error('method can just be called for handlers with "recv" direction');
        }
    }
}
exports.Chrome74 = Chrome74;

},{"../Logger":44,"../ortc":65,"../scalabilityModes":66,"../utils":68,"./HandlerInterface":54,"./sdp/RemoteSdp":60,"./sdp/commonUtils":61,"./sdp/unifiedPlanUtils":63,"sdp-transform":71}],52:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const Logger_1 = require("../Logger");
const errors_1 = require("../errors");
const utils = __importStar(require("../utils"));
const ortc = __importStar(require("../ortc"));
const edgeUtils = __importStar(require("./ortc/edgeUtils"));
const HandlerInterface_1 = require("./HandlerInterface");
const logger = new Logger_1.Logger('Edge11');
class Edge11 extends HandlerInterface_1.HandlerInterface {
    constructor() {
        super();
        // Map of RTCRtpSenders indexed by id.
        this._rtpSenders = new Map();
        // Map of RTCRtpReceivers indexed by id.
        this._rtpReceivers = new Map();
        // Next localId for sending tracks.
        this._nextSendLocalId = 0;
        // Got transport local and remote parameters.
        this._transportReady = false;
    }
    /**
     * Creates a factory function.
     */
    static createFactory() {
        return () => new Edge11();
    }
    get name() {
        return 'Edge11';
    }
    close() {
        logger.debug('close()');
        // Close the ICE gatherer.
        // NOTE: Not yet implemented by Edge.
        try {
            this._iceGatherer.close();
        }
        catch (error) { }
        // Close the ICE transport.
        try {
            this._iceTransport.stop();
        }
        catch (error) { }
        // Close the DTLS transport.
        try {
            this._dtlsTransport.stop();
        }
        catch (error) { }
        // Close RTCRtpSenders.
        for (const rtpSender of this._rtpSenders.values()) {
            try {
                rtpSender.stop();
            }
            catch (error) { }
        }
        // Close RTCRtpReceivers.
        for (const rtpReceiver of this._rtpReceivers.values()) {
            try {
                rtpReceiver.stop();
            }
            catch (error) { }
        }
    }
    async getNativeRtpCapabilities() {
        logger.debug('getNativeRtpCapabilities()');
        return edgeUtils.getCapabilities();
    }
    async getNativeSctpCapabilities() {
        logger.debug('getNativeSctpCapabilities()');
        return {
            numStreams: { OS: 0, MIS: 0 }
        };
    }
    run({ direction, // eslint-disable-line @typescript-eslint/no-unused-vars
    iceParameters, iceCandidates, dtlsParameters, sctpParameters, // eslint-disable-line @typescript-eslint/no-unused-vars
    iceServers, iceTransportPolicy, additionalSettings, // eslint-disable-line @typescript-eslint/no-unused-vars
    proprietaryConstraints, // eslint-disable-line @typescript-eslint/no-unused-vars
    extendedRtpCapabilities }) {
        logger.debug('run()');
        this._sendingRtpParametersByKind =
            {
                audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
            };
        this._remoteIceParameters = iceParameters;
        this._remoteIceCandidates = iceCandidates;
        this._remoteDtlsParameters = dtlsParameters;
        this._cname = `CNAME-${utils.generateRandomNumber()}`;
        this._setIceGatherer({ iceServers, iceTransportPolicy });
        this._setIceTransport();
        this._setDtlsTransport();
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async updateIceServers(iceServers) {
        // NOTE: Edge 11 does not implement iceGatherer.gater().
        throw new errors_1.UnsupportedError('not supported');
    }
    async restartIce(iceParameters) {
        logger.debug('restartIce()');
        this._remoteIceParameters = iceParameters;
        if (!this._transportReady)
            return;
        logger.debug('restartIce() | calling iceTransport.start()');
        this._iceTransport.start(this._iceGatherer, iceParameters, 'controlling');
        for (const candidate of this._remoteIceCandidates) {
            this._iceTransport.addRemoteCandidate(candidate);
        }
        this._iceTransport.addRemoteCandidate({});
    }
    async getTransportStats() {
        return this._iceTransport.getStats();
    }
    async send(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    { track, encodings, codecOptions }) {
        logger.debug('send() [kind:%s, track.id:%s]', track.kind, track.id);
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'server' });
        logger.debug('send() | calling new RTCRtpSender()');
        const rtpSender = new RTCRtpSender(track, this._dtlsTransport);
        const rtpParameters = utils.clone(this._sendingRtpParametersByKind[track.kind]);
        const useRtx = rtpParameters.codecs
            .some((codec) => /.+\/rtx$/i.test(codec.mimeType));
        if (!encodings)
            encodings = [{}];
        for (const encoding of encodings) {
            encoding.ssrc = utils.generateRandomNumber();
            if (useRtx)
                encoding.rtx = { ssrc: utils.generateRandomNumber() };
        }
        rtpParameters.encodings = encodings;
        // Fill RTCRtpParameters.rtcp.
        rtpParameters.rtcp =
            {
                cname: this._cname,
                reducedSize: true,
                mux: true
            };
        // NOTE: Convert our standard RTCRtpParameters into those that Edge
        // expects.
        const edgeRtpParameters = edgeUtils.mangleRtpParameters(rtpParameters);
        logger.debug('send() | calling rtpSender.send() [params:%o]', edgeRtpParameters);
        await rtpSender.send(edgeRtpParameters);
        const localId = String(this._nextSendLocalId);
        this._nextSendLocalId++;
        // Store it.
        this._rtpSenders.set(localId, rtpSender);
        return { localId, rtpParameters, rtpSender };
    }
    async stopSending(localId) {
        logger.debug('stopSending() [localId:%s]', localId);
        const rtpSender = this._rtpSenders.get(localId);
        if (!rtpSender)
            throw new Error('RTCRtpSender not found');
        this._rtpSenders.delete(localId);
        try {
            logger.debug('stopSending() | calling rtpSender.stop()');
            rtpSender.stop();
        }
        catch (error) {
            logger.warn('stopSending() | rtpSender.stop() failed:%o', error);
            throw error;
        }
    }
    async replaceTrack(localId, track) {
        logger.debug('replaceTrack() [localId:%s, track.id:%s]', localId, track.id);
        const rtpSender = this._rtpSenders.get(localId);
        if (!rtpSender)
            throw new Error('RTCRtpSender not found');
        const oldTrack = rtpSender.track;
        rtpSender.setTrack(track);
        // Replace key.
        this._rtpSenders.delete(oldTrack.id);
        this._rtpSenders.set(track.id, rtpSender);
    }
    async setMaxSpatialLayer(localId, spatialLayer) {
        logger.debug('setMaxSpatialLayer() [localId:%s, spatialLayer:%s]', localId, spatialLayer);
        const rtpSender = this._rtpSenders.get(localId);
        if (!rtpSender)
            throw new Error('RTCRtpSender not found');
        const parameters = rtpSender.getParameters();
        parameters.encodings
            .forEach((encoding, idx) => {
            if (idx <= spatialLayer)
                encoding.active = true;
            else
                encoding.active = false;
        });
        await rtpSender.setParameters(parameters);
    }
    async setRtpEncodingParameters(localId, params) {
        logger.debug('setRtpEncodingParameters() [localId:%s, params:%o]', localId, params);
        const rtpSender = this._rtpSenders.get(localId);
        if (!rtpSender)
            throw new Error('RTCRtpSender not found');
        const parameters = rtpSender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            parameters.encodings[idx] = { ...encoding, ...params };
        });
        await rtpSender.setParameters(parameters);
    }
    async getSenderStats(localId) {
        const rtpSender = this._rtpSenders.get(localId);
        if (!rtpSender)
            throw new Error('RTCRtpSender not found');
        return rtpSender.getStats();
    }
    async sendDataChannel(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    options) {
        throw new errors_1.UnsupportedError('not implemented');
    }
    async receive({ trackId, kind, rtpParameters }) {
        logger.debug('receive() [trackId:%s, kind:%s]', trackId, kind);
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'server' });
        logger.debug('receive() | calling new RTCRtpReceiver()');
        const rtpReceiver = new RTCRtpReceiver(this._dtlsTransport, kind);
        rtpReceiver.addEventListener('error', (event) => {
            logger.error('rtpReceiver "error" event [event:%o]', event);
        });
        // NOTE: Convert our standard RTCRtpParameters into those that Edge
        // expects.
        const edgeRtpParameters = edgeUtils.mangleRtpParameters(rtpParameters);
        logger.debug('receive() | calling rtpReceiver.receive() [params:%o]', edgeRtpParameters);
        await rtpReceiver.receive(edgeRtpParameters);
        const localId = trackId;
        // Store it.
        this._rtpReceivers.set(localId, rtpReceiver);
        return {
            localId,
            track: rtpReceiver.track,
            rtpReceiver
        };
    }
    async stopReceiving(localId) {
        logger.debug('stopReceiving() [localId:%s]', localId);
        const rtpReceiver = this._rtpReceivers.get(localId);
        if (!rtpReceiver)
            throw new Error('RTCRtpReceiver not found');
        this._rtpReceivers.delete(localId);
        try {
            logger.debug('stopReceiving() | calling rtpReceiver.stop()');
            rtpReceiver.stop();
        }
        catch (error) {
            logger.warn('stopReceiving() | rtpReceiver.stop() failed:%o', error);
        }
    }
    async getReceiverStats(localId) {
        const rtpReceiver = this._rtpReceivers.get(localId);
        if (!rtpReceiver)
            throw new Error('RTCRtpReceiver not found');
        return rtpReceiver.getStats();
    }
    async receiveDataChannel(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    options) {
        throw new errors_1.UnsupportedError('not implemented');
    }
    _setIceGatherer({ iceServers, iceTransportPolicy }) {
        const iceGatherer = new RTCIceGatherer({
            iceServers: iceServers || [],
            gatherPolicy: iceTransportPolicy || 'all'
        });
        iceGatherer.addEventListener('error', (event) => {
            logger.error('iceGatherer "error" event [event:%o]', event);
        });
        // NOTE: Not yet implemented by Edge, which starts gathering automatically.
        try {
            iceGatherer.gather();
        }
        catch (error) {
            logger.debug('_setIceGatherer() | iceGatherer.gather() failed: %s', error.toString());
        }
        this._iceGatherer = iceGatherer;
    }
    _setIceTransport() {
        const iceTransport = new RTCIceTransport(this._iceGatherer);
        // NOTE: Not yet implemented by Edge.
        iceTransport.addEventListener('statechange', () => {
            switch (iceTransport.state) {
                case 'checking':
                    this.emit('@connectionstatechange', 'connecting');
                    break;
                case 'connected':
                case 'completed':
                    this.emit('@connectionstatechange', 'connected');
                    break;
                case 'failed':
                    this.emit('@connectionstatechange', 'failed');
                    break;
                case 'disconnected':
                    this.emit('@connectionstatechange', 'disconnected');
                    break;
                case 'closed':
                    this.emit('@connectionstatechange', 'closed');
                    break;
            }
        });
        // NOTE: Not standard, but implemented by Edge.
        iceTransport.addEventListener('icestatechange', () => {
            switch (iceTransport.state) {
                case 'checking':
                    this.emit('@connectionstatechange', 'connecting');
                    break;
                case 'connected':
                case 'completed':
                    this.emit('@connectionstatechange', 'connected');
                    break;
                case 'failed':
                    this.emit('@connectionstatechange', 'failed');
                    break;
                case 'disconnected':
                    this.emit('@connectionstatechange', 'disconnected');
                    break;
                case 'closed':
                    this.emit('@connectionstatechange', 'closed');
                    break;
            }
        });
        iceTransport.addEventListener('candidatepairchange', (event) => {
            logger.debug('iceTransport "candidatepairchange" event [pair:%o]', event.pair);
        });
        this._iceTransport = iceTransport;
    }
    _setDtlsTransport() {
        const dtlsTransport = new RTCDtlsTransport(this._iceTransport);
        // NOTE: Not yet implemented by Edge.
        dtlsTransport.addEventListener('statechange', () => {
            logger.debug('dtlsTransport "statechange" event [state:%s]', dtlsTransport.state);
        });
        // NOTE: Not standard, but implemented by Edge.
        dtlsTransport.addEventListener('dtlsstatechange', () => {
            logger.debug('dtlsTransport "dtlsstatechange" event [state:%s]', dtlsTransport.state);
            if (dtlsTransport.state === 'closed')
                this.emit('@connectionstatechange', 'closed');
        });
        dtlsTransport.addEventListener('error', (event) => {
            logger.error('dtlsTransport "error" event [event:%o]', event);
        });
        this._dtlsTransport = dtlsTransport;
    }
    async _setupTransport({ localDtlsRole }) {
        logger.debug('_setupTransport()');
        // Get our local DTLS parameters.
        const dtlsParameters = this._dtlsTransport.getLocalParameters();
        dtlsParameters.role = localDtlsRole;
        // Need to tell the remote transport about our parameters.
        await this.safeEmitAsPromise('@connect', { dtlsParameters });
        // Start the RTCIceTransport.
        this._iceTransport.start(this._iceGatherer, this._remoteIceParameters, 'controlling');
        // Add remote ICE candidates.
        for (const candidate of this._remoteIceCandidates) {
            this._iceTransport.addRemoteCandidate(candidate);
        }
        // Also signal a 'complete' candidate as per spec.
        // NOTE: It should be {complete: true} but Edge prefers {}.
        // NOTE: If we don't signal end of candidates, the Edge RTCIceTransport
        // won't enter the 'completed' state.
        this._iceTransport.addRemoteCandidate({});
        // NOTE: Edge does not like SHA less than 256.
        this._remoteDtlsParameters.fingerprints = this._remoteDtlsParameters.fingerprints
            .filter((fingerprint) => {
            return (fingerprint.algorithm === 'sha-256' ||
                fingerprint.algorithm === 'sha-384' ||
                fingerprint.algorithm === 'sha-512');
        });
        // Start the RTCDtlsTransport.
        this._dtlsTransport.start(this._remoteDtlsParameters);
        this._transportReady = true;
    }
}
exports.Edge11 = Edge11;

},{"../Logger":44,"../errors":47,"../ortc":65,"../utils":68,"./HandlerInterface":54,"./ortc/edgeUtils":58}],53:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sdpTransform = __importStar(require("sdp-transform"));
const Logger_1 = require("../Logger");
const errors_1 = require("../errors");
const utils = __importStar(require("../utils"));
const ortc = __importStar(require("../ortc"));
const sdpCommonUtils = __importStar(require("./sdp/commonUtils"));
const sdpUnifiedPlanUtils = __importStar(require("./sdp/unifiedPlanUtils"));
const HandlerInterface_1 = require("./HandlerInterface");
const RemoteSdp_1 = require("./sdp/RemoteSdp");
const logger = new Logger_1.Logger('Firefox60');
const SCTP_NUM_STREAMS = { OS: 16, MIS: 2048 };
class Firefox60 extends HandlerInterface_1.HandlerInterface {
    constructor() {
        super();
        // Map of RTCTransceivers indexed by MID.
        this._mapMidTransceiver = new Map();
        // Local stream for sending.
        this._sendStream = new MediaStream();
        // Whether a DataChannel m=application section has been created.
        this._hasDataChannelMediaSection = false;
        // Sending DataChannel id value counter. Incremented for each new DataChannel.
        this._nextSendSctpStreamId = 0;
        // Got transport local and remote parameters.
        this._transportReady = false;
    }
    /**
     * Creates a factory function.
     */
    static createFactory() {
        return () => new Firefox60();
    }
    get name() {
        return 'Firefox60';
    }
    close() {
        logger.debug('close()');
        // Close RTCPeerConnection.
        if (this._pc) {
            try {
                this._pc.close();
            }
            catch (error) { }
        }
    }
    async getNativeRtpCapabilities() {
        logger.debug('getNativeRtpCapabilities()');
        const pc = new RTCPeerConnection({
            iceServers: [],
            iceTransportPolicy: 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require'
        });
        // NOTE: We need to add a real video track to get the RID extension mapping.
        const canvas = document.createElement('canvas');
        // NOTE: Otherwise Firefox fails in next line.
        canvas.getContext('2d');
        const fakeStream = canvas.captureStream();
        const fakeVideoTrack = fakeStream.getVideoTracks()[0];
        try {
            pc.addTransceiver('audio', { direction: 'sendrecv' });
            const videoTransceiver = pc.addTransceiver(fakeVideoTrack, { direction: 'sendrecv' });
            const parameters = videoTransceiver.sender.getParameters();
            const encodings = [
                { rid: 'r0', maxBitrate: 100000 },
                { rid: 'r1', maxBitrate: 500000 }
            ];
            parameters.encodings = encodings;
            await videoTransceiver.sender.setParameters(parameters);
            const offer = await pc.createOffer();
            try {
                canvas.remove();
            }
            catch (error) { }
            try {
                fakeVideoTrack.stop();
            }
            catch (error) { }
            try {
                pc.close();
            }
            catch (error) { }
            const sdpObject = sdpTransform.parse(offer.sdp);
            const nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities({ sdpObject });
            return nativeRtpCapabilities;
        }
        catch (error) {
            try {
                canvas.remove();
            }
            catch (error2) { }
            try {
                fakeVideoTrack.stop();
            }
            catch (error2) { }
            try {
                pc.close();
            }
            catch (error2) { }
            throw error;
        }
    }
    async getNativeSctpCapabilities() {
        logger.debug('getNativeSctpCapabilities()');
        return {
            numStreams: SCTP_NUM_STREAMS
        };
    }
    run({ direction, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, extendedRtpCapabilities }) {
        logger.debug('run()');
        this._direction = direction;
        this._remoteSdp = new RemoteSdp_1.RemoteSdp({
            iceParameters,
            iceCandidates,
            dtlsParameters,
            sctpParameters
        });
        this._sendingRtpParametersByKind =
            {
                audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
            };
        this._sendingRemoteRtpParametersByKind =
            {
                audio: ortc.getSendingRemoteRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRemoteRtpParameters('video', extendedRtpCapabilities)
            };
        this._pc = new RTCPeerConnection({
            iceServers: iceServers || [],
            iceTransportPolicy: iceTransportPolicy || 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            ...additionalSettings
        }, proprietaryConstraints);
        // Handle RTCPeerConnection connection status.
        this._pc.addEventListener('iceconnectionstatechange', () => {
            switch (this._pc.iceConnectionState) {
                case 'checking':
                    this.emit('@connectionstatechange', 'connecting');
                    break;
                case 'connected':
                case 'completed':
                    this.emit('@connectionstatechange', 'connected');
                    break;
                case 'failed':
                    this.emit('@connectionstatechange', 'failed');
                    break;
                case 'disconnected':
                    this.emit('@connectionstatechange', 'disconnected');
                    break;
                case 'closed':
                    this.emit('@connectionstatechange', 'closed');
                    break;
            }
        });
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async updateIceServers(iceServers) {
        // NOTE: Firefox does not implement pc.setConfiguration().
        throw new errors_1.UnsupportedError('not supported');
    }
    async restartIce(iceParameters) {
        logger.debug('restartIce()');
        // Provide the remote SDP handler with new remote ICE parameters.
        this._remoteSdp.updateIceParameters(iceParameters);
        if (!this._transportReady)
            return;
        if (this._direction === 'send') {
            const offer = await this._pc.createOffer({ iceRestart: true });
            logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
        }
        else {
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
        }
    }
    async getTransportStats() {
        return this._pc.getStats();
    }
    async send({ track, encodings, codecOptions }) {
        this._assertSendDirection();
        logger.debug('send() [kind:%s, track.id:%s]', track.kind, track.id);
        let reverseEncodings;
        if (encodings && encodings.length > 1) {
            encodings.forEach((encoding, idx) => {
                encoding.rid = `r${idx}`;
            });
            // Clone the encodings and reverse them because Firefox likes them
            // from high to low.
            reverseEncodings = utils.clone(encodings).reverse();
        }
        // NOTE: Firefox fails sometimes to properly anticipate the closed media
        // section that it should use, so don't reuse closed media sections.
        //   https://github.com/versatica/mediasoup-client/issues/104
        //
        // const mediaSectionIdx = this._remoteSdp.getNextMediaSectionIdx();
        const transceiver = this._pc.addTransceiver(track, { direction: 'sendonly', streams: [this._sendStream] });
        // NOTE: This is not spec compliants. Encodings should be given in addTransceiver
        // second argument, but Firefox does not support it.
        if (reverseEncodings) {
            const parameters = transceiver.sender.getParameters();
            parameters.encodings = reverseEncodings;
            await transceiver.sender.setParameters(parameters);
        }
        const offer = await this._pc.createOffer();
        let localSdpObject = sdpTransform.parse(offer.sdp);
        const sendingRtpParameters = utils.clone(this._sendingRtpParametersByKind[track.kind]);
        // In Firefox use DTLS role client even if we are the "offerer" since
        // Firefox does not respect ICE-Lite.
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
        logger.debug('send() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        // We can now get the transceiver.mid.
        const localId = transceiver.mid;
        // Set MID.
        sendingRtpParameters.mid = localId;
        localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        const offerMediaObject = localSdpObject.media[localSdpObject.media.length - 1];
        // Set RTCP CNAME.
        sendingRtpParameters.rtcp.cname =
            sdpCommonUtils.getCname({ offerMediaObject });
        // Set RTP encodings by parsing the SDP offer if no encodings are given.
        if (!encodings) {
            sendingRtpParameters.encodings =
                sdpUnifiedPlanUtils.getRtpEncodings({ offerMediaObject });
        }
        // Set RTP encodings by parsing the SDP offer and complete them with given
        // one if just a single encoding has been given.
        else if (encodings.length === 1) {
            const newEncodings = sdpUnifiedPlanUtils.getRtpEncodings({ offerMediaObject });
            Object.assign(newEncodings[0], encodings[0]);
            sendingRtpParameters.encodings = newEncodings;
        }
        // Otherwise if more than 1 encoding are given use them verbatim.
        else {
            sendingRtpParameters.encodings = encodings;
        }
        // If VP8 or H264 and there is effective simulcast, add scalabilityMode to
        // each encoding.
        if (sendingRtpParameters.encodings.length > 1 &&
            (sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/vp8' ||
                sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/h264')) {
            for (const encoding of sendingRtpParameters.encodings) {
                encoding.scalabilityMode = 'S1T3';
            }
        }
        this._remoteSdp.send({
            offerMediaObject,
            offerRtpParameters: sendingRtpParameters,
            answerRtpParameters: this._sendingRemoteRtpParametersByKind[track.kind],
            codecOptions,
            extmapAllowMixed: true
        });
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('send() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
        // Store in the map.
        this._mapMidTransceiver.set(localId, transceiver);
        return {
            localId,
            rtpParameters: sendingRtpParameters,
            rtpSender: transceiver.sender
        };
    }
    async stopSending(localId) {
        logger.debug('stopSending() [localId:%s]', localId);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated transceiver not found');
        transceiver.sender.replaceTrack(null);
        this._pc.removeTrack(transceiver.sender);
        // NOTE: Cannot use closeMediaSection() due to the the note above in send()
        // method.
        // this._remoteSdp.closeMediaSection(transceiver.mid);
        this._remoteSdp.disableMediaSection(transceiver.mid);
        const offer = await this._pc.createOffer();
        logger.debug('stopSending() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopSending() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
    }
    async replaceTrack(localId, track) {
        this._assertSendDirection();
        logger.debug('replaceTrack() [localId:%s, track.id:%s]', localId, track.id);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        await transceiver.sender.replaceTrack(track);
    }
    async setMaxSpatialLayer(localId, spatialLayer) {
        this._assertSendDirection();
        logger.debug('setMaxSpatialLayer() [localId:%s, spatialLayer:%s]', localId, spatialLayer);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated transceiver not found');
        const parameters = transceiver.sender.getParameters();
        // NOTE: We require encodings given from low to high, however Firefox
        // requires them in reverse order, so do magic here.
        spatialLayer = parameters.encodings.length - 1 - spatialLayer;
        parameters.encodings.forEach((encoding, idx) => {
            if (idx >= spatialLayer)
                encoding.active = true;
            else
                encoding.active = false;
        });
        await transceiver.sender.setParameters(parameters);
    }
    async setRtpEncodingParameters(localId, params) {
        this._assertSendDirection();
        logger.debug('setRtpEncodingParameters() [localId:%s, params:%o]', localId, params);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        const parameters = transceiver.sender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            parameters.encodings[idx] = { ...encoding, ...params };
        });
        await transceiver.sender.setParameters(parameters);
    }
    async getSenderStats(localId) {
        this._assertSendDirection();
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        return transceiver.sender.getStats();
    }
    async sendDataChannel({ ordered, maxPacketLifeTime, maxRetransmits, label, protocol, priority }) {
        this._assertSendDirection();
        const options = {
            negotiated: true,
            id: this._nextSendSctpStreamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmits,
            protocol,
            priority
        };
        logger.debug('sendDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // Increase next id.
        this._nextSendSctpStreamId =
            ++this._nextSendSctpStreamId % SCTP_NUM_STREAMS.MIS;
        // If this is the first DataChannel we need to create the SDP answer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            const offer = await this._pc.createOffer();
            const localSdpObject = sdpTransform.parse(offer.sdp);
            const offerMediaObject = localSdpObject.media
                .find((m) => m.type === 'application');
            if (!this._transportReady)
                await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
            logger.debug('sendDataChannel() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            this._remoteSdp.sendSctpAssociation({ offerMediaObject });
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('sendDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        const sctpStreamParameters = {
            streamId: options.id,
            ordered: options.ordered,
            maxPacketLifeTime: options.maxPacketLifeTime,
            maxRetransmits: options.maxRetransmits
        };
        return { dataChannel, sctpStreamParameters };
    }
    async receive({ trackId, kind, rtpParameters }) {
        this._assertRecvDirection();
        logger.debug('receive() [trackId:%s, kind:%s]', trackId, kind);
        const localId = String(this._mapMidTransceiver.size);
        this._remoteSdp.receive({
            mid: localId,
            kind,
            offerRtpParameters: rtpParameters,
            streamId: rtpParameters.rtcp.cname,
            trackId
        });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('receive() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        let answer = await this._pc.createAnswer();
        const localSdpObject = sdpTransform.parse(answer.sdp);
        const answerMediaObject = localSdpObject.media
            .find((m) => String(m.mid) === localId);
        // May need to modify codec parameters in the answer based on codec
        // parameters in the offer.
        sdpCommonUtils.applyCodecParameters({
            offerRtpParameters: rtpParameters,
            answerMediaObject
        });
        answer = { type: 'answer', sdp: sdpTransform.write(localSdpObject) };
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
        logger.debug('receive() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
        const transceiver = this._pc.getTransceivers()
            .find((t) => t.mid === localId);
        if (!transceiver)
            throw new Error('new RTCRtpTransceiver not found');
        // Store in the map.
        this._mapMidTransceiver.set(localId, transceiver);
        return {
            localId,
            track: transceiver.receiver.track,
            rtpReceiver: transceiver.receiver
        };
    }
    async stopReceiving(localId) {
        this._assertRecvDirection();
        logger.debug('stopReceiving() [localId:%s]', localId);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        this._remoteSdp.closeMediaSection(transceiver.mid);
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopReceiving() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        const answer = await this._pc.createAnswer();
        logger.debug('stopReceiving() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
    }
    async getReceiverStats(localId) {
        this._assertRecvDirection();
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        return transceiver.receiver.getStats();
    }
    async receiveDataChannel({ sctpStreamParameters, label, protocol }) {
        this._assertRecvDirection();
        const { streamId, ordered, maxPacketLifeTime, maxRetransmits } = sctpStreamParameters;
        const options = {
            negotiated: true,
            id: streamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmits,
            protocol
        };
        logger.debug('receiveDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // If this is the first DataChannel we need to create the SDP offer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            this._remoteSdp.receiveSctpAssociation();
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            if (!this._transportReady) {
                const localSdpObject = sdpTransform.parse(answer.sdp);
                await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
            }
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        return { dataChannel };
    }
    async _setupTransport({ localDtlsRole, localSdpObject }) {
        if (!localSdpObject)
            localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        // Get our local DTLS parameters.
        const dtlsParameters = sdpCommonUtils.extractDtlsParameters({ sdpObject: localSdpObject });
        // Set our DTLS role.
        dtlsParameters.role = localDtlsRole;
        // Update the remote DTLS role in the SDP.
        this._remoteSdp.updateDtlsRole(localDtlsRole === 'client' ? 'server' : 'client');
        // Need to tell the remote transport about our parameters.
        await this.safeEmitAsPromise('@connect', { dtlsParameters });
        this._transportReady = true;
    }
    _assertSendDirection() {
        if (this._direction !== 'send') {
            throw new Error('method can just be called for handlers with "send" direction');
        }
    }
    _assertRecvDirection() {
        if (this._direction !== 'recv') {
            throw new Error('method can just be called for handlers with "recv" direction');
        }
    }
}
exports.Firefox60 = Firefox60;

},{"../Logger":44,"../errors":47,"../ortc":65,"../utils":68,"./HandlerInterface":54,"./sdp/RemoteSdp":60,"./sdp/commonUtils":61,"./sdp/unifiedPlanUtils":63,"sdp-transform":71}],54:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const EnhancedEventEmitter_1 = require("../EnhancedEventEmitter");
class HandlerInterface extends EnhancedEventEmitter_1.EnhancedEventEmitter {
    /**
     * @emits @connect - (
     *     { dtlsParameters: DtlsParameters },
     *     callback: Function,
     *     errback: Function
     *   )
     * @emits @connectionstatechange - (connectionState: ConnectionState)
     */
    constructor() {
        super();
    }
}
exports.HandlerInterface = HandlerInterface;

},{"../EnhancedEventEmitter":43}],55:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sdpTransform = __importStar(require("sdp-transform"));
const Logger_1 = require("../Logger");
const errors_1 = require("../errors");
const utils = __importStar(require("../utils"));
const ortc = __importStar(require("../ortc"));
const sdpCommonUtils = __importStar(require("./sdp/commonUtils"));
const sdpPlanBUtils = __importStar(require("./sdp/planBUtils"));
const HandlerInterface_1 = require("./HandlerInterface");
const RemoteSdp_1 = require("./sdp/RemoteSdp");
const logger = new Logger_1.Logger('ReactNative');
const SCTP_NUM_STREAMS = { OS: 1024, MIS: 1024 };
class ReactNative extends HandlerInterface_1.HandlerInterface {
    constructor() {
        super();
        // Local stream for sending.
        this._sendStream = new MediaStream();
        // Map of sending MediaStreamTracks indexed by localId.
        this._mapSendLocalIdTrack = new Map();
        // Next sending localId.
        this._nextSendLocalId = 0;
        // Map of MID, RTP parameters and RTCRtpReceiver indexed by local id.
        // Value is an Object with mid, rtpParameters and rtpReceiver.
        this._mapRecvLocalIdInfo = new Map();
        // Whether a DataChannel m=application section has been created.
        this._hasDataChannelMediaSection = false;
        // Sending DataChannel id value counter. Incremented for each new DataChannel.
        this._nextSendSctpStreamId = 0;
        // Got transport local and remote parameters.
        this._transportReady = false;
    }
    /**
     * Creates a factory function.
     */
    static createFactory() {
        return () => new ReactNative();
    }
    get name() {
        return 'ReactNative';
    }
    close() {
        logger.debug('close()');
        // Close RTCPeerConnection.
        if (this._pc) {
            try {
                this._pc.close();
            }
            catch (error) { }
        }
    }
    async getNativeRtpCapabilities() {
        logger.debug('getNativeRtpCapabilities()');
        const pc = new RTCPeerConnection({
            iceServers: [],
            iceTransportPolicy: 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'plan-b'
        });
        try {
            const offer = await pc.createOffer({
                offerToReceiveAudio: true,
                offerToReceiveVideo: true
            });
            try {
                pc.close();
            }
            catch (error) { }
            const sdpObject = sdpTransform.parse(offer.sdp);
            const nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities({ sdpObject });
            return nativeRtpCapabilities;
        }
        catch (error) {
            try {
                pc.close();
            }
            catch (error2) { }
            throw error;
        }
    }
    async getNativeSctpCapabilities() {
        logger.debug('getNativeSctpCapabilities()');
        return {
            numStreams: SCTP_NUM_STREAMS
        };
    }
    run({ direction, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, extendedRtpCapabilities }) {
        logger.debug('run()');
        this._direction = direction;
        this._remoteSdp = new RemoteSdp_1.RemoteSdp({
            iceParameters,
            iceCandidates,
            dtlsParameters,
            sctpParameters,
            planB: true
        });
        this._sendingRtpParametersByKind =
            {
                audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
            };
        this._sendingRemoteRtpParametersByKind =
            {
                audio: ortc.getSendingRemoteRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRemoteRtpParameters('video', extendedRtpCapabilities)
            };
        this._pc = new RTCPeerConnection({
            iceServers: iceServers || [],
            iceTransportPolicy: iceTransportPolicy || 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'plan-b',
            ...additionalSettings
        }, proprietaryConstraints);
        // Handle RTCPeerConnection connection status.
        this._pc.addEventListener('iceconnectionstatechange', () => {
            switch (this._pc.iceConnectionState) {
                case 'checking':
                    this.emit('@connectionstatechange', 'connecting');
                    break;
                case 'connected':
                case 'completed':
                    this.emit('@connectionstatechange', 'connected');
                    break;
                case 'failed':
                    this.emit('@connectionstatechange', 'failed');
                    break;
                case 'disconnected':
                    this.emit('@connectionstatechange', 'disconnected');
                    break;
                case 'closed':
                    this.emit('@connectionstatechange', 'closed');
                    break;
            }
        });
    }
    async updateIceServers(iceServers) {
        logger.debug('updateIceServers()');
        const configuration = this._pc.getConfiguration();
        configuration.iceServers = iceServers;
        this._pc.setConfiguration(configuration);
    }
    async restartIce(iceParameters) {
        logger.debug('restartIce()');
        // Provide the remote SDP handler with new remote ICE parameters.
        this._remoteSdp.updateIceParameters(iceParameters);
        if (!this._transportReady)
            return;
        if (this._direction === 'send') {
            const offer = await this._pc.createOffer({ iceRestart: true });
            logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
        }
        else {
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
        }
    }
    async getTransportStats() {
        return this._pc.getStats();
    }
    async send({ track, encodings, codecOptions }) {
        this._assertSendDirection();
        logger.debug('send() [kind:%s, track.id:%s]', track.kind, track.id);
        this._sendStream.addTrack(track);
        this._pc.addStream(this._sendStream);
        let offer = await this._pc.createOffer();
        let localSdpObject = sdpTransform.parse(offer.sdp);
        let offerMediaObject;
        const sendingRtpParameters = utils.clone(this._sendingRtpParametersByKind[track.kind]);
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
        if (track.kind === 'video' && encodings && encodings.length > 1) {
            logger.debug('send() | enabling simulcast');
            localSdpObject = sdpTransform.parse(offer.sdp);
            offerMediaObject = localSdpObject.media
                .find((m) => m.type === 'video');
            sdpPlanBUtils.addLegacySimulcast({
                offerMediaObject,
                track,
                numStreams: encodings.length
            });
            offer = { type: 'offer', sdp: sdpTransform.write(localSdpObject) };
        }
        logger.debug('send() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        offerMediaObject = localSdpObject.media
            .find((m) => m.type === track.kind);
        // Set RTCP CNAME.
        sendingRtpParameters.rtcp.cname =
            sdpCommonUtils.getCname({ offerMediaObject });
        // Set RTP encodings.
        sendingRtpParameters.encodings =
            sdpPlanBUtils.getRtpEncodings({ offerMediaObject, track });
        // Complete encodings with given values.
        if (encodings) {
            for (let idx = 0; idx < sendingRtpParameters.encodings.length; ++idx) {
                if (encodings[idx])
                    Object.assign(sendingRtpParameters.encodings[idx], encodings[idx]);
            }
        }
        // If VP8 or H264 and there is effective simulcast, add scalabilityMode to
        // each encoding.
        if (sendingRtpParameters.encodings.length > 1 &&
            (sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/vp8' ||
                sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/h264')) {
            for (const encoding of sendingRtpParameters.encodings) {
                encoding.scalabilityMode = 'S1T3';
            }
        }
        this._remoteSdp.send({
            offerMediaObject,
            offerRtpParameters: sendingRtpParameters,
            answerRtpParameters: this._sendingRemoteRtpParametersByKind[track.kind],
            codecOptions
        });
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('send() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
        const localId = String(this._nextSendLocalId);
        this._nextSendLocalId++;
        // Insert into the map.
        this._mapSendLocalIdTrack.set(localId, track);
        return {
            localId: localId,
            rtpParameters: sendingRtpParameters
        };
    }
    async stopSending(localId) {
        this._assertSendDirection();
        logger.debug('stopSending() [localId:%s]', localId);
        const track = this._mapSendLocalIdTrack.get(localId);
        if (!track)
            throw new Error('track not found');
        this._mapSendLocalIdTrack.delete(localId);
        this._sendStream.removeTrack(track);
        this._pc.addStream(this._sendStream);
        const offer = await this._pc.createOffer();
        logger.debug('stopSending() | calling pc.setLocalDescription() [offer:%o]', offer);
        try {
            await this._pc.setLocalDescription(offer);
        }
        catch (error) {
            // NOTE: If there are no sending tracks, setLocalDescription() will fail with
            // "Failed to create channels". If so, ignore it.
            if (this._sendStream.getTracks().length === 0) {
                logger.warn('stopSending() | ignoring expected error due no sending tracks: %s', error.toString());
                return;
            }
            throw error;
        }
        if (this._pc.signalingState === 'stable')
            return;
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopSending() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async replaceTrack(localId, track) {
        throw new errors_1.UnsupportedError('not implemented');
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async setMaxSpatialLayer(localId, spatialLayer) {
        throw new errors_1.UnsupportedError('not implemented');
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async setRtpEncodingParameters(localId, params) {
        throw new errors_1.UnsupportedError('not implemented');
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async getSenderStats(localId) {
        throw new errors_1.UnsupportedError('not implemented');
    }
    async sendDataChannel({ ordered, maxPacketLifeTime, maxRetransmits, label, protocol, priority }) {
        this._assertSendDirection();
        const options = {
            negotiated: true,
            id: this._nextSendSctpStreamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmitTime: maxPacketLifeTime,
            maxRetransmits,
            protocol,
            priority
        };
        logger.debug('sendDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // Increase next id.
        this._nextSendSctpStreamId =
            ++this._nextSendSctpStreamId % SCTP_NUM_STREAMS.MIS;
        // If this is the first DataChannel we need to create the SDP answer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            const offer = await this._pc.createOffer();
            const localSdpObject = sdpTransform.parse(offer.sdp);
            const offerMediaObject = localSdpObject.media
                .find((m) => m.type === 'application');
            if (!this._transportReady)
                await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
            logger.debug('sendDataChannel() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            this._remoteSdp.sendSctpAssociation({ offerMediaObject });
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('sendDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        const sctpStreamParameters = {
            streamId: options.id,
            ordered: options.ordered,
            maxPacketLifeTime: options.maxPacketLifeTime,
            maxRetransmits: options.maxRetransmits
        };
        return { dataChannel, sctpStreamParameters };
    }
    async receive({ trackId, kind, rtpParameters }) {
        this._assertRecvDirection();
        logger.debug('receive() [trackId:%s, kind:%s]', trackId, kind);
        const localId = trackId;
        const mid = kind;
        let streamId = rtpParameters.rtcp.cname;
        // NOTE: In React-Native we cannot reuse the same remote MediaStream for new
        // remote tracks. This is because react-native-webrtc does not react on new
        // tracks generated within already existing streams, so force the streamId
        // to be different.
        logger.debug('receive() | forcing a random remote streamId to avoid well known bug in react-native-webrtc');
        streamId += `-hack-${utils.generateRandomNumber()}`;
        this._remoteSdp.receive({
            mid,
            kind,
            offerRtpParameters: rtpParameters,
            streamId,
            trackId
        });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('receive() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        let answer = await this._pc.createAnswer();
        const localSdpObject = sdpTransform.parse(answer.sdp);
        const answerMediaObject = localSdpObject.media
            .find((m) => String(m.mid) === mid);
        // May need to modify codec parameters in the answer based on codec
        // parameters in the offer.
        sdpCommonUtils.applyCodecParameters({
            offerRtpParameters: rtpParameters,
            answerMediaObject
        });
        answer = { type: 'answer', sdp: sdpTransform.write(localSdpObject) };
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
        logger.debug('receive() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
        const stream = this._pc.getRemoteStreams()
            .find((s) => s.id === streamId);
        const track = stream.getTrackById(localId);
        if (!track)
            throw new Error('remote track not found');
        // Insert into the map.
        this._mapRecvLocalIdInfo.set(localId, { mid, rtpParameters });
        return { localId, track };
    }
    async stopReceiving(localId) {
        this._assertRecvDirection();
        logger.debug('stopReceiving() [localId:%s]', localId);
        const { mid, rtpParameters } = this._mapRecvLocalIdInfo.get(localId);
        // Remove from the map.
        this._mapRecvLocalIdInfo.delete(localId);
        this._remoteSdp.planBStopReceiving({ mid, offerRtpParameters: rtpParameters });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopReceiving() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        const answer = await this._pc.createAnswer();
        logger.debug('stopReceiving() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    async getReceiverStats(localId) {
        throw new errors_1.UnsupportedError('not implemented');
    }
    async receiveDataChannel({ sctpStreamParameters, label, protocol }) {
        this._assertRecvDirection();
        const { streamId, ordered, maxPacketLifeTime, maxRetransmits } = sctpStreamParameters;
        const options = {
            negotiated: true,
            id: streamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmitTime: maxPacketLifeTime,
            maxRetransmits,
            protocol
        };
        logger.debug('receiveDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // If this is the first DataChannel we need to create the SDP offer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            this._remoteSdp.receiveSctpAssociation({ oldDataChannelSpec: true });
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            if (!this._transportReady) {
                const localSdpObject = sdpTransform.parse(answer.sdp);
                await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
            }
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        return { dataChannel };
    }
    async _setupTransport({ localDtlsRole, localSdpObject }) {
        if (!localSdpObject)
            localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        // Get our local DTLS parameters.
        const dtlsParameters = sdpCommonUtils.extractDtlsParameters({ sdpObject: localSdpObject });
        // Set our DTLS role.
        dtlsParameters.role = localDtlsRole;
        // Update the remote DTLS role in the SDP.
        this._remoteSdp.updateDtlsRole(localDtlsRole === 'client' ? 'server' : 'client');
        // Need to tell the remote transport about our parameters.
        await this.safeEmitAsPromise('@connect', { dtlsParameters });
        this._transportReady = true;
    }
    _assertSendDirection() {
        if (this._direction !== 'send') {
            throw new Error('method can just be called for handlers with "send" direction');
        }
    }
    _assertRecvDirection() {
        if (this._direction !== 'recv') {
            throw new Error('method can just be called for handlers with "recv" direction');
        }
    }
}
exports.ReactNative = ReactNative;

},{"../Logger":44,"../errors":47,"../ortc":65,"../utils":68,"./HandlerInterface":54,"./sdp/RemoteSdp":60,"./sdp/commonUtils":61,"./sdp/planBUtils":62,"sdp-transform":71}],56:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sdpTransform = __importStar(require("sdp-transform"));
const Logger_1 = require("../Logger");
const utils = __importStar(require("../utils"));
const ortc = __importStar(require("../ortc"));
const sdpCommonUtils = __importStar(require("./sdp/commonUtils"));
const sdpPlanBUtils = __importStar(require("./sdp/planBUtils"));
const HandlerInterface_1 = require("./HandlerInterface");
const RemoteSdp_1 = require("./sdp/RemoteSdp");
const logger = new Logger_1.Logger('Safari11');
const SCTP_NUM_STREAMS = { OS: 1024, MIS: 1024 };
class Safari11 extends HandlerInterface_1.HandlerInterface {
    constructor() {
        super();
        // Local stream for sending.
        this._sendStream = new MediaStream();
        // Map of sending MediaStreamTracks indexed by localId.
        this._mapSendLocalIdTrack = new Map();
        // Next sending localId.
        this._nextSendLocalId = 0;
        // Map of MID, RTP parameters and RTCRtpReceiver indexed by local id.
        // Value is an Object with mid, rtpParameters and rtpReceiver.
        this._mapRecvLocalIdInfo = new Map();
        // Whether a DataChannel m=application section has been created.
        this._hasDataChannelMediaSection = false;
        // Sending DataChannel id value counter. Incremented for each new DataChannel.
        this._nextSendSctpStreamId = 0;
        // Got transport local and remote parameters.
        this._transportReady = false;
    }
    /**
     * Creates a factory function.
     */
    static createFactory() {
        return () => new Safari11();
    }
    get name() {
        return 'Safari11';
    }
    close() {
        logger.debug('close()');
        // Close RTCPeerConnection.
        if (this._pc) {
            try {
                this._pc.close();
            }
            catch (error) { }
        }
    }
    async getNativeRtpCapabilities() {
        logger.debug('getNativeRtpCapabilities()');
        const pc = new RTCPeerConnection({
            iceServers: [],
            iceTransportPolicy: 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            sdpSemantics: 'plan-b'
        });
        try {
            const offer = await pc.createOffer({
                offerToReceiveAudio: true,
                offerToReceiveVideo: true
            });
            try {
                pc.close();
            }
            catch (error) { }
            const sdpObject = sdpTransform.parse(offer.sdp);
            const nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities({ sdpObject });
            return nativeRtpCapabilities;
        }
        catch (error) {
            try {
                pc.close();
            }
            catch (error2) { }
            throw error;
        }
    }
    async getNativeSctpCapabilities() {
        logger.debug('getNativeSctpCapabilities()');
        return {
            numStreams: SCTP_NUM_STREAMS
        };
    }
    run({ direction, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, extendedRtpCapabilities }) {
        logger.debug('run()');
        this._direction = direction;
        this._remoteSdp = new RemoteSdp_1.RemoteSdp({
            iceParameters,
            iceCandidates,
            dtlsParameters,
            sctpParameters,
            planB: true
        });
        this._sendingRtpParametersByKind =
            {
                audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
            };
        this._sendingRemoteRtpParametersByKind =
            {
                audio: ortc.getSendingRemoteRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRemoteRtpParameters('video', extendedRtpCapabilities)
            };
        this._pc = new RTCPeerConnection({
            iceServers: iceServers || [],
            iceTransportPolicy: iceTransportPolicy || 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            ...additionalSettings
        }, proprietaryConstraints);
        // Handle RTCPeerConnection connection status.
        this._pc.addEventListener('iceconnectionstatechange', () => {
            switch (this._pc.iceConnectionState) {
                case 'checking':
                    this.emit('@connectionstatechange', 'connecting');
                    break;
                case 'connected':
                case 'completed':
                    this.emit('@connectionstatechange', 'connected');
                    break;
                case 'failed':
                    this.emit('@connectionstatechange', 'failed');
                    break;
                case 'disconnected':
                    this.emit('@connectionstatechange', 'disconnected');
                    break;
                case 'closed':
                    this.emit('@connectionstatechange', 'closed');
                    break;
            }
        });
    }
    async updateIceServers(iceServers) {
        logger.debug('updateIceServers()');
        const configuration = this._pc.getConfiguration();
        configuration.iceServers = iceServers;
        this._pc.setConfiguration(configuration);
    }
    async restartIce(iceParameters) {
        logger.debug('restartIce()');
        // Provide the remote SDP handler with new remote ICE parameters.
        this._remoteSdp.updateIceParameters(iceParameters);
        if (!this._transportReady)
            return;
        if (this._direction === 'send') {
            const offer = await this._pc.createOffer({ iceRestart: true });
            logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
        }
        else {
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
        }
    }
    async getTransportStats() {
        return this._pc.getStats();
    }
    async send({ track, encodings, codecOptions }) {
        this._assertSendDirection();
        logger.debug('send() [kind:%s, track.id:%s]', track.kind, track.id);
        this._sendStream.addTrack(track);
        this._pc.addTrack(track, this._sendStream);
        let offer = await this._pc.createOffer();
        let localSdpObject = sdpTransform.parse(offer.sdp);
        let offerMediaObject;
        const sendingRtpParameters = utils.clone(this._sendingRtpParametersByKind[track.kind]);
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
        if (track.kind === 'video' && encodings && encodings.length > 1) {
            logger.debug('send() | enabling simulcast');
            localSdpObject = sdpTransform.parse(offer.sdp);
            offerMediaObject = localSdpObject.media.find((m) => m.type === 'video');
            sdpPlanBUtils.addLegacySimulcast({
                offerMediaObject,
                track,
                numStreams: encodings.length
            });
            offer = { type: 'offer', sdp: sdpTransform.write(localSdpObject) };
        }
        logger.debug('send() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        offerMediaObject = localSdpObject.media
            .find((m) => m.type === track.kind);
        // Set RTCP CNAME.
        sendingRtpParameters.rtcp.cname =
            sdpCommonUtils.getCname({ offerMediaObject });
        // Set RTP encodings.
        sendingRtpParameters.encodings =
            sdpPlanBUtils.getRtpEncodings({ offerMediaObject, track });
        // Complete encodings with given values.
        if (encodings) {
            for (let idx = 0; idx < sendingRtpParameters.encodings.length; ++idx) {
                if (encodings[idx])
                    Object.assign(sendingRtpParameters.encodings[idx], encodings[idx]);
            }
        }
        // If VP8 and there is effective simulcast, add scalabilityMode to each
        // encoding.
        if (sendingRtpParameters.encodings.length > 1 &&
            sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/vp8') {
            for (const encoding of sendingRtpParameters.encodings) {
                encoding.scalabilityMode = 'S1T3';
            }
        }
        this._remoteSdp.send({
            offerMediaObject,
            offerRtpParameters: sendingRtpParameters,
            answerRtpParameters: this._sendingRemoteRtpParametersByKind[track.kind],
            codecOptions
        });
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('send() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
        const localId = String(this._nextSendLocalId);
        this._nextSendLocalId++;
        // Insert into the map.
        this._mapSendLocalIdTrack.set(localId, track);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === track);
        return {
            localId: localId,
            rtpParameters: sendingRtpParameters,
            rtpSender
        };
    }
    async stopSending(localId) {
        this._assertSendDirection();
        const track = this._mapSendLocalIdTrack.get(localId);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === track);
        if (!rtpSender)
            throw new Error('associated RTCRtpSender not found');
        this._pc.removeTrack(rtpSender);
        this._sendStream.removeTrack(track);
        this._mapSendLocalIdTrack.delete(localId);
        const offer = await this._pc.createOffer();
        logger.debug('stopSending() | calling pc.setLocalDescription() [offer:%o]', offer);
        try {
            await this._pc.setLocalDescription(offer);
        }
        catch (error) {
            // NOTE: If there are no sending tracks, setLocalDescription() will fail with
            // "Failed to create channels". If so, ignore it.
            if (this._sendStream.getTracks().length === 0) {
                logger.warn('stopSending() | ignoring expected error due no sending tracks: %s', error.toString());
                return;
            }
            throw error;
        }
        if (this._pc.signalingState === 'stable')
            return;
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopSending() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
    }
    async replaceTrack(localId, track) {
        this._assertSendDirection();
        logger.debug('replaceTrack() [localId:%s, track.id:%s]', localId, track.id);
        const oldTrack = this._mapSendLocalIdTrack.get(localId);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === oldTrack);
        if (!rtpSender)
            throw new Error('associated RTCRtpSender not found');
        await rtpSender.replaceTrack(track);
        // Remove the old track from the local stream.
        this._sendStream.removeTrack(oldTrack);
        // Add the new track to the local stream.
        this._sendStream.addTrack(track);
        // Replace entry in the map.
        this._mapSendLocalIdTrack.set(localId, track);
    }
    async setMaxSpatialLayer(localId, spatialLayer) {
        this._assertSendDirection();
        logger.debug('setMaxSpatialLayer() [localId:%s, spatialLayer:%s]', localId, spatialLayer);
        const track = this._mapSendLocalIdTrack.get(localId);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === track);
        if (!rtpSender)
            throw new Error('associated RTCRtpSender not found');
        const parameters = rtpSender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            if (idx <= spatialLayer)
                encoding.active = true;
            else
                encoding.active = false;
        });
        await rtpSender.setParameters(parameters);
    }
    async setRtpEncodingParameters(localId, params) {
        this._assertSendDirection();
        logger.debug('setRtpEncodingParameters() [localId:%s, params:%o]', localId, params);
        const track = this._mapSendLocalIdTrack.get(localId);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === track);
        if (!rtpSender)
            throw new Error('associated RTCRtpSender not found');
        const parameters = rtpSender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            parameters.encodings[idx] = { ...encoding, ...params };
        });
        await rtpSender.setParameters(parameters);
    }
    async getSenderStats(localId) {
        this._assertSendDirection();
        const track = this._mapSendLocalIdTrack.get(localId);
        const rtpSender = this._pc.getSenders()
            .find((s) => s.track === track);
        if (!rtpSender)
            throw new Error('associated RTCRtpSender not found');
        return rtpSender.getStats();
    }
    async sendDataChannel({ ordered, maxPacketLifeTime, maxRetransmits, label, protocol, priority }) {
        this._assertSendDirection();
        const options = {
            negotiated: true,
            id: this._nextSendSctpStreamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmits,
            protocol,
            priority
        };
        logger.debug('sendDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // Increase next id.
        this._nextSendSctpStreamId =
            ++this._nextSendSctpStreamId % SCTP_NUM_STREAMS.MIS;
        // If this is the first DataChannel we need to create the SDP answer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            const offer = await this._pc.createOffer();
            const localSdpObject = sdpTransform.parse(offer.sdp);
            const offerMediaObject = localSdpObject.media
                .find((m) => m.type === 'application');
            if (!this._transportReady)
                await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
            logger.debug('sendDataChannel() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            this._remoteSdp.sendSctpAssociation({ offerMediaObject });
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('sendDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        const sctpStreamParameters = {
            streamId: options.id,
            ordered: options.ordered,
            maxPacketLifeTime: options.maxPacketLifeTime,
            maxRetransmits: options.maxRetransmits
        };
        return { dataChannel, sctpStreamParameters };
    }
    async receive({ trackId, kind, rtpParameters }) {
        this._assertRecvDirection();
        logger.debug('receive() [trackId:%s, kind:%s]', trackId, kind);
        const localId = trackId;
        const mid = kind;
        this._remoteSdp.receive({
            mid,
            kind,
            offerRtpParameters: rtpParameters,
            streamId: rtpParameters.rtcp.cname,
            trackId
        });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('receive() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        let answer = await this._pc.createAnswer();
        const localSdpObject = sdpTransform.parse(answer.sdp);
        const answerMediaObject = localSdpObject.media
            .find((m) => String(m.mid) === mid);
        // May need to modify codec parameters in the answer based on codec
        // parameters in the offer.
        sdpCommonUtils.applyCodecParameters({
            offerRtpParameters: rtpParameters,
            answerMediaObject
        });
        answer = { type: 'answer', sdp: sdpTransform.write(localSdpObject) };
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
        logger.debug('receive() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
        const rtpReceiver = this._pc.getReceivers()
            .find((r) => r.track && r.track.id === localId);
        if (!rtpReceiver)
            throw new Error('new RTCRtpReceiver not');
        // Insert into the map.
        this._mapRecvLocalIdInfo.set(localId, { mid, rtpParameters, rtpReceiver });
        return {
            localId,
            track: rtpReceiver.track,
            rtpReceiver
        };
    }
    async stopReceiving(localId) {
        this._assertRecvDirection();
        logger.debug('stopReceiving() [localId:%s]', localId);
        const { mid, rtpParameters } = this._mapRecvLocalIdInfo.get(localId);
        // Remove from the map.
        this._mapRecvLocalIdInfo.delete(localId);
        this._remoteSdp.planBStopReceiving({ mid, offerRtpParameters: rtpParameters });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopReceiving() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        const answer = await this._pc.createAnswer();
        logger.debug('stopReceiving() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
    }
    async getReceiverStats(localId) {
        this._assertRecvDirection();
        const { rtpReceiver } = this._mapRecvLocalIdInfo.get(localId);
        if (!rtpReceiver)
            throw new Error('associated RTCRtpReceiver not found');
        return rtpReceiver.getStats();
    }
    async receiveDataChannel({ sctpStreamParameters, label, protocol }) {
        this._assertRecvDirection();
        const { streamId, ordered, maxPacketLifeTime, maxRetransmits } = sctpStreamParameters;
        const options = {
            negotiated: true,
            id: streamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmits,
            protocol
        };
        logger.debug('receiveDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // If this is the first DataChannel we need to create the SDP offer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            this._remoteSdp.receiveSctpAssociation({ oldDataChannelSpec: true });
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            if (!this._transportReady) {
                const localSdpObject = sdpTransform.parse(answer.sdp);
                await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
            }
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        return { dataChannel };
    }
    async _setupTransport({ localDtlsRole, localSdpObject }) {
        if (!localSdpObject)
            localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        // Get our local DTLS parameters.
        const dtlsParameters = sdpCommonUtils.extractDtlsParameters({ sdpObject: localSdpObject });
        // Set our DTLS role.
        dtlsParameters.role = localDtlsRole;
        // Update the remote DTLS role in the SDP.
        this._remoteSdp.updateDtlsRole(localDtlsRole === 'client' ? 'server' : 'client');
        // Need to tell the remote transport about our parameters.
        await this.safeEmitAsPromise('@connect', { dtlsParameters });
        this._transportReady = true;
    }
    _assertSendDirection() {
        if (this._direction !== 'send') {
            throw new Error('method can just be called for handlers with "send" direction');
        }
    }
    _assertRecvDirection() {
        if (this._direction !== 'recv') {
            throw new Error('method can just be called for handlers with "recv" direction');
        }
    }
}
exports.Safari11 = Safari11;

},{"../Logger":44,"../ortc":65,"../utils":68,"./HandlerInterface":54,"./sdp/RemoteSdp":60,"./sdp/commonUtils":61,"./sdp/planBUtils":62,"sdp-transform":71}],57:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sdpTransform = __importStar(require("sdp-transform"));
const Logger_1 = require("../Logger");
const utils = __importStar(require("../utils"));
const ortc = __importStar(require("../ortc"));
const sdpCommonUtils = __importStar(require("./sdp/commonUtils"));
const sdpUnifiedPlanUtils = __importStar(require("./sdp/unifiedPlanUtils"));
const HandlerInterface_1 = require("./HandlerInterface");
const RemoteSdp_1 = require("./sdp/RemoteSdp");
const logger = new Logger_1.Logger('Safari12');
const SCTP_NUM_STREAMS = { OS: 1024, MIS: 1024 };
class Safari12 extends HandlerInterface_1.HandlerInterface {
    constructor() {
        super();
        // Map of RTCTransceivers indexed by MID.
        this._mapMidTransceiver = new Map();
        // Local stream for sending.
        this._sendStream = new MediaStream();
        // Whether a DataChannel m=application section has been created.
        this._hasDataChannelMediaSection = false;
        // Sending DataChannel id value counter. Incremented for each new DataChannel.
        this._nextSendSctpStreamId = 0;
        // Got transport local and remote parameters.
        this._transportReady = false;
    }
    /**
     * Creates a factory function.
     */
    static createFactory() {
        return () => new Safari12();
    }
    get name() {
        return 'Safari12';
    }
    close() {
        logger.debug('close()');
        // Close RTCPeerConnection.
        if (this._pc) {
            try {
                this._pc.close();
            }
            catch (error) { }
        }
    }
    async getNativeRtpCapabilities() {
        logger.debug('getNativeRtpCapabilities()');
        const pc = new RTCPeerConnection({
            iceServers: [],
            iceTransportPolicy: 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require'
        });
        try {
            pc.addTransceiver('audio');
            pc.addTransceiver('video');
            const offer = await pc.createOffer();
            try {
                pc.close();
            }
            catch (error) { }
            const sdpObject = sdpTransform.parse(offer.sdp);
            const nativeRtpCapabilities = sdpCommonUtils.extractRtpCapabilities({ sdpObject });
            return nativeRtpCapabilities;
        }
        catch (error) {
            try {
                pc.close();
            }
            catch (error2) { }
            throw error;
        }
    }
    async getNativeSctpCapabilities() {
        logger.debug('getNativeSctpCapabilities()');
        return {
            numStreams: SCTP_NUM_STREAMS
        };
    }
    run({ direction, iceParameters, iceCandidates, dtlsParameters, sctpParameters, iceServers, iceTransportPolicy, additionalSettings, proprietaryConstraints, extendedRtpCapabilities }) {
        logger.debug('run()');
        this._direction = direction;
        this._remoteSdp = new RemoteSdp_1.RemoteSdp({
            iceParameters,
            iceCandidates,
            dtlsParameters,
            sctpParameters
        });
        this._sendingRtpParametersByKind =
            {
                audio: ortc.getSendingRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRtpParameters('video', extendedRtpCapabilities)
            };
        this._sendingRemoteRtpParametersByKind =
            {
                audio: ortc.getSendingRemoteRtpParameters('audio', extendedRtpCapabilities),
                video: ortc.getSendingRemoteRtpParameters('video', extendedRtpCapabilities)
            };
        this._pc = new RTCPeerConnection({
            iceServers: iceServers || [],
            iceTransportPolicy: iceTransportPolicy || 'all',
            bundlePolicy: 'max-bundle',
            rtcpMuxPolicy: 'require',
            ...additionalSettings
        }, proprietaryConstraints);
        // Handle RTCPeerConnection connection status.
        this._pc.addEventListener('iceconnectionstatechange', () => {
            switch (this._pc.iceConnectionState) {
                case 'checking':
                    this.emit('@connectionstatechange', 'connecting');
                    break;
                case 'connected':
                case 'completed':
                    this.emit('@connectionstatechange', 'connected');
                    break;
                case 'failed':
                    this.emit('@connectionstatechange', 'failed');
                    break;
                case 'disconnected':
                    this.emit('@connectionstatechange', 'disconnected');
                    break;
                case 'closed':
                    this.emit('@connectionstatechange', 'closed');
                    break;
            }
        });
    }
    async updateIceServers(iceServers) {
        logger.debug('updateIceServers()');
        const configuration = this._pc.getConfiguration();
        configuration.iceServers = iceServers;
        this._pc.setConfiguration(configuration);
    }
    async restartIce(iceParameters) {
        logger.debug('restartIce()');
        // Provide the remote SDP handler with new remote ICE parameters.
        this._remoteSdp.updateIceParameters(iceParameters);
        if (!this._transportReady)
            return;
        if (this._direction === 'send') {
            const offer = await this._pc.createOffer({ iceRestart: true });
            logger.debug('restartIce() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
        }
        else {
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('restartIce() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            logger.debug('restartIce() | calling pc.setLocalDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
        }
    }
    async getTransportStats() {
        return this._pc.getStats();
    }
    async send({ track, encodings, codecOptions }) {
        this._assertSendDirection();
        logger.debug('send() [kind:%s, track.id:%s]', track.kind, track.id);
        const mediaSectionIdx = this._remoteSdp.getNextMediaSectionIdx();
        const transceiver = this._pc.addTransceiver(track, { direction: 'sendonly', streams: [this._sendStream] });
        let offer = await this._pc.createOffer();
        let localSdpObject = sdpTransform.parse(offer.sdp);
        let offerMediaObject;
        const sendingRtpParameters = utils.clone(this._sendingRtpParametersByKind[track.kind]);
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
        if (encodings && encodings.length > 1) {
            logger.debug('send() | enabling legacy simulcast');
            localSdpObject = sdpTransform.parse(offer.sdp);
            offerMediaObject = localSdpObject.media[mediaSectionIdx.idx];
            sdpUnifiedPlanUtils.addLegacySimulcast({
                offerMediaObject,
                numStreams: encodings.length
            });
            offer = { type: 'offer', sdp: sdpTransform.write(localSdpObject) };
        }
        logger.debug('send() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        // We can now get the transceiver.mid.
        const localId = transceiver.mid;
        // Set MID.
        sendingRtpParameters.mid = localId;
        localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        offerMediaObject = localSdpObject.media[mediaSectionIdx.idx];
        // Set RTCP CNAME.
        sendingRtpParameters.rtcp.cname =
            sdpCommonUtils.getCname({ offerMediaObject });
        // Set RTP encodings.
        sendingRtpParameters.encodings =
            sdpUnifiedPlanUtils.getRtpEncodings({ offerMediaObject });
        // Complete encodings with given values.
        if (encodings) {
            for (let idx = 0; idx < sendingRtpParameters.encodings.length; ++idx) {
                if (encodings[idx])
                    Object.assign(sendingRtpParameters.encodings[idx], encodings[idx]);
            }
        }
        // If VP8 or H264 and there is effective simulcast, add scalabilityMode to
        // each encoding.
        if (sendingRtpParameters.encodings.length > 1 &&
            (sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/vp8' ||
                sendingRtpParameters.codecs[0].mimeType.toLowerCase() === 'video/h264')) {
            for (const encoding of sendingRtpParameters.encodings) {
                encoding.scalabilityMode = 'S1T3';
            }
        }
        this._remoteSdp.send({
            offerMediaObject,
            reuseMid: mediaSectionIdx.reuseMid,
            offerRtpParameters: sendingRtpParameters,
            answerRtpParameters: this._sendingRemoteRtpParametersByKind[track.kind],
            codecOptions
        });
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('send() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
        // Store in the map.
        this._mapMidTransceiver.set(localId, transceiver);
        return {
            localId,
            rtpParameters: sendingRtpParameters,
            rtpSender: transceiver.sender
        };
    }
    async stopSending(localId) {
        this._assertSendDirection();
        logger.debug('stopSending() [localId:%s]', localId);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        transceiver.sender.replaceTrack(null);
        this._pc.removeTrack(transceiver.sender);
        this._remoteSdp.closeMediaSection(transceiver.mid);
        const offer = await this._pc.createOffer();
        logger.debug('stopSending() | calling pc.setLocalDescription() [offer:%o]', offer);
        await this._pc.setLocalDescription(offer);
        const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopSending() | calling pc.setRemoteDescription() [answer:%o]', answer);
        await this._pc.setRemoteDescription(answer);
    }
    async replaceTrack(localId, track) {
        this._assertSendDirection();
        logger.debug('replaceTrack() [localId:%s, track.id:%s]', localId, track.id);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        await transceiver.sender.replaceTrack(track);
    }
    async setMaxSpatialLayer(localId, spatialLayer) {
        this._assertSendDirection();
        logger.debug('setMaxSpatialLayer() [localId:%s, spatialLayer:%s]', localId, spatialLayer);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        const parameters = transceiver.sender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            if (idx <= spatialLayer)
                encoding.active = true;
            else
                encoding.active = false;
        });
        await transceiver.sender.setParameters(parameters);
    }
    async setRtpEncodingParameters(localId, params) {
        this._assertSendDirection();
        logger.debug('setRtpEncodingParameters() [localId:%s, params:%o]', localId, params);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        const parameters = transceiver.sender.getParameters();
        parameters.encodings.forEach((encoding, idx) => {
            parameters.encodings[idx] = { ...encoding, ...params };
        });
        await transceiver.sender.setParameters(parameters);
    }
    async getSenderStats(localId) {
        this._assertSendDirection();
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        return transceiver.sender.getStats();
    }
    async sendDataChannel({ ordered, maxPacketLifeTime, maxRetransmits, label, protocol, priority }) {
        this._assertSendDirection();
        const options = {
            negotiated: true,
            id: this._nextSendSctpStreamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmits,
            protocol,
            priority
        };
        logger.debug('sendDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // Increase next id.
        this._nextSendSctpStreamId =
            ++this._nextSendSctpStreamId % SCTP_NUM_STREAMS.MIS;
        // If this is the first DataChannel we need to create the SDP answer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            const offer = await this._pc.createOffer();
            const localSdpObject = sdpTransform.parse(offer.sdp);
            const offerMediaObject = localSdpObject.media
                .find((m) => m.type === 'application');
            if (!this._transportReady)
                await this._setupTransport({ localDtlsRole: 'server', localSdpObject });
            logger.debug('sendDataChannel() | calling pc.setLocalDescription() [offer:%o]', offer);
            await this._pc.setLocalDescription(offer);
            this._remoteSdp.sendSctpAssociation({ offerMediaObject });
            const answer = { type: 'answer', sdp: this._remoteSdp.getSdp() };
            logger.debug('sendDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setRemoteDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        const sctpStreamParameters = {
            streamId: options.id,
            ordered: options.ordered,
            maxPacketLifeTime: options.maxPacketLifeTime,
            maxRetransmits: options.maxRetransmits
        };
        return { dataChannel, sctpStreamParameters };
    }
    async receive({ trackId, kind, rtpParameters }) {
        this._assertRecvDirection();
        logger.debug('receive() [trackId:%s, kind:%s]', trackId, kind);
        const localId = String(this._mapMidTransceiver.size);
        this._remoteSdp.receive({
            mid: localId,
            kind,
            offerRtpParameters: rtpParameters,
            streamId: rtpParameters.rtcp.cname,
            trackId
        });
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('receive() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        let answer = await this._pc.createAnswer();
        const localSdpObject = sdpTransform.parse(answer.sdp);
        const answerMediaObject = localSdpObject.media
            .find((m) => String(m.mid) === localId);
        // May need to modify codec parameters in the answer based on codec
        // parameters in the offer.
        sdpCommonUtils.applyCodecParameters({
            offerRtpParameters: rtpParameters,
            answerMediaObject
        });
        answer = { type: 'answer', sdp: sdpTransform.write(localSdpObject) };
        if (!this._transportReady)
            await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
        logger.debug('receive() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
        const transceiver = this._pc.getTransceivers()
            .find((t) => t.mid === localId);
        if (!transceiver)
            throw new Error('new RTCRtpTransceiver not found');
        // Store in the map.
        this._mapMidTransceiver.set(localId, transceiver);
        return {
            localId,
            track: transceiver.receiver.track,
            rtpReceiver: transceiver.receiver
        };
    }
    async stopReceiving(localId) {
        this._assertRecvDirection();
        logger.debug('stopReceiving() [localId:%s]', localId);
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        this._remoteSdp.closeMediaSection(transceiver.mid);
        const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
        logger.debug('stopReceiving() | calling pc.setRemoteDescription() [offer:%o]', offer);
        await this._pc.setRemoteDescription(offer);
        const answer = await this._pc.createAnswer();
        logger.debug('stopReceiving() | calling pc.setLocalDescription() [answer:%o]', answer);
        await this._pc.setLocalDescription(answer);
    }
    async getReceiverStats(localId) {
        this._assertRecvDirection();
        const transceiver = this._mapMidTransceiver.get(localId);
        if (!transceiver)
            throw new Error('associated RTCRtpTransceiver not found');
        return transceiver.receiver.getStats();
    }
    async receiveDataChannel({ sctpStreamParameters, label, protocol }) {
        this._assertRecvDirection();
        const { streamId, ordered, maxPacketLifeTime, maxRetransmits } = sctpStreamParameters;
        const options = {
            negotiated: true,
            id: streamId,
            ordered,
            maxPacketLifeTime,
            maxRetransmits,
            protocol
        };
        logger.debug('receiveDataChannel() [options:%o]', options);
        const dataChannel = this._pc.createDataChannel(label, options);
        // If this is the first DataChannel we need to create the SDP offer with
        // m=application section.
        if (!this._hasDataChannelMediaSection) {
            this._remoteSdp.receiveSctpAssociation();
            const offer = { type: 'offer', sdp: this._remoteSdp.getSdp() };
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [offer:%o]', offer);
            await this._pc.setRemoteDescription(offer);
            const answer = await this._pc.createAnswer();
            if (!this._transportReady) {
                const localSdpObject = sdpTransform.parse(answer.sdp);
                await this._setupTransport({ localDtlsRole: 'client', localSdpObject });
            }
            logger.debug('receiveDataChannel() | calling pc.setRemoteDescription() [answer:%o]', answer);
            await this._pc.setLocalDescription(answer);
            this._hasDataChannelMediaSection = true;
        }
        return { dataChannel };
    }
    async _setupTransport({ localDtlsRole, localSdpObject }) {
        if (!localSdpObject)
            localSdpObject = sdpTransform.parse(this._pc.localDescription.sdp);
        // Get our local DTLS parameters.
        const dtlsParameters = sdpCommonUtils.extractDtlsParameters({ sdpObject: localSdpObject });
        // Set our DTLS role.
        dtlsParameters.role = localDtlsRole;
        // Update the remote DTLS role in the SDP.
        this._remoteSdp.updateDtlsRole(localDtlsRole === 'client' ? 'server' : 'client');
        // Need to tell the remote transport about our parameters.
        await this.safeEmitAsPromise('@connect', { dtlsParameters });
        this._transportReady = true;
    }
    _assertSendDirection() {
        if (this._direction !== 'send') {
            throw new Error('method can just be called for handlers with "send" direction');
        }
    }
    _assertRecvDirection() {
        if (this._direction !== 'recv') {
            throw new Error('method can just be called for handlers with "recv" direction');
        }
    }
}
exports.Safari12 = Safari12;

},{"../Logger":44,"../ortc":65,"../utils":68,"./HandlerInterface":54,"./sdp/RemoteSdp":60,"./sdp/commonUtils":61,"./sdp/unifiedPlanUtils":63,"sdp-transform":71}],58:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const utils = __importStar(require("../../utils"));
/**
 * Normalize ORTC based Edge's RTCRtpReceiver.getCapabilities() to produce a full
 * compliant ORTC RTCRtpCapabilities.
 */
function getCapabilities() {
    const nativeCaps = RTCRtpReceiver.getCapabilities();
    const caps = utils.clone(nativeCaps);
    for (const codec of caps.codecs) {
        // Rename numChannels to channels.
        codec.channels = codec.numChannels;
        delete codec.numChannels;
        // Add mimeType.
        codec.mimeType = codec.mimeType || `${codec.kind}/${codec.name}`;
        // NOTE: Edge sets some numeric parameters as string rather than number. Fix them.
        if (codec.parameters) {
            const parameters = codec.parameters;
            if (parameters.apt)
                parameters.apt = Number(parameters.apt);
            if (parameters['packetization-mode'])
                parameters['packetization-mode'] = Number(parameters['packetization-mode']);
        }
        // Delete emty parameter String in rtcpFeedback.
        for (const feedback of codec.rtcpFeedback || []) {
            if (!feedback.parameter)
                feedback.parameter = '';
        }
    }
    return caps;
}
exports.getCapabilities = getCapabilities;
/**
 * Generate RTCRtpParameters as ORTC based Edge likes.
 */
function mangleRtpParameters(rtpParameters) {
    const params = utils.clone(rtpParameters);
    // Rename mid to muxId.
    if (params.mid) {
        params.muxId = params.mid;
        delete params.mid;
    }
    for (const codec of params.codecs) {
        // Rename channels to numChannels.
        if (codec.channels) {
            codec.numChannels = codec.channels;
            delete codec.channels;
        }
        // Add codec.name (requried by Edge).
        if (codec.mimeType && !codec.name)
            codec.name = codec.mimeType.split('/')[1];
        // Remove mimeType.
        delete codec.mimeType;
    }
    return params;
}
exports.mangleRtpParameters = mangleRtpParameters;

},{"../../utils":68}],59:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const utils = __importStar(require("../../utils"));
class MediaSection {
    constructor({ iceParameters, iceCandidates, dtlsParameters, planB = false }) {
        this._mediaObject = {};
        this._planB = planB;
        if (iceParameters) {
            this.setIceParameters(iceParameters);
        }
        if (iceCandidates) {
            this._mediaObject.candidates = [];
            for (const candidate of iceCandidates) {
                const candidateObject = {};
                // mediasoup does mandates rtcp-mux so candidates component is always
                // RTP (1).
                candidateObject.component = 1;
                candidateObject.foundation = candidate.foundation;
                candidateObject.ip = candidate.ip;
                candidateObject.port = candidate.port;
                candidateObject.priority = candidate.priority;
                candidateObject.transport = candidate.protocol;
                candidateObject.type = candidate.type;
                if (candidate.tcpType)
                    candidateObject.tcptype = candidate.tcpType;
                this._mediaObject.candidates.push(candidateObject);
            }
            this._mediaObject.endOfCandidates = 'end-of-candidates';
            this._mediaObject.iceOptions = 'renomination';
        }
        if (dtlsParameters) {
            this.setDtlsRole(dtlsParameters.role);
        }
    }
    get mid() {
        return String(this._mediaObject.mid);
    }
    get closed() {
        return this._mediaObject.port === 0;
    }
    getObject() {
        return this._mediaObject;
    }
    setIceParameters(iceParameters) {
        this._mediaObject.iceUfrag = iceParameters.usernameFragment;
        this._mediaObject.icePwd = iceParameters.password;
    }
    disable() {
        this._mediaObject.direction = 'inactive';
        delete this._mediaObject.ext;
        delete this._mediaObject.ssrcs;
        delete this._mediaObject.ssrcGroups;
        delete this._mediaObject.simulcast;
        delete this._mediaObject.simulcast_03;
        delete this._mediaObject.rids;
    }
    close() {
        this._mediaObject.direction = 'inactive';
        this._mediaObject.port = 0;
        delete this._mediaObject.ext;
        delete this._mediaObject.ssrcs;
        delete this._mediaObject.ssrcGroups;
        delete this._mediaObject.simulcast;
        delete this._mediaObject.simulcast_03;
        delete this._mediaObject.rids;
        delete this._mediaObject.extmapAllowMixed;
    }
}
class AnswerMediaSection extends MediaSection {
    constructor({ iceParameters, iceCandidates, dtlsParameters, sctpParameters, plainRtpParameters, planB = false, offerMediaObject, offerRtpParameters, answerRtpParameters, codecOptions, extmapAllowMixed = false }) {
        super({ iceParameters, iceCandidates, dtlsParameters, planB });
        this._mediaObject.mid = String(offerMediaObject.mid);
        this._mediaObject.type = offerMediaObject.type;
        this._mediaObject.protocol = offerMediaObject.protocol;
        if (!plainRtpParameters) {
            this._mediaObject.connection = { ip: '127.0.0.1', version: 4 };
            this._mediaObject.port = 7;
        }
        else {
            this._mediaObject.connection =
                {
                    ip: plainRtpParameters.ip,
                    version: plainRtpParameters.ipVersion
                };
            this._mediaObject.port = plainRtpParameters.port;
        }
        switch (offerMediaObject.type) {
            case 'audio':
            case 'video':
                {
                    this._mediaObject.direction = 'recvonly';
                    this._mediaObject.rtp = [];
                    this._mediaObject.rtcpFb = [];
                    this._mediaObject.fmtp = [];
                    for (const codec of answerRtpParameters.codecs) {
                        const rtp = {
                            payload: codec.payloadType,
                            codec: getCodecName(codec),
                            rate: codec.clockRate
                        };
                        if (codec.channels > 1)
                            rtp.encoding = codec.channels;
                        this._mediaObject.rtp.push(rtp);
                        const codecParameters = utils.clone(codec.parameters || {});
                        if (codecOptions) {
                            const { opusStereo, opusFec, opusDtx, opusMaxPlaybackRate, opusPtime, videoGoogleStartBitrate, videoGoogleMaxBitrate, videoGoogleMinBitrate } = codecOptions;
                            const offerCodec = offerRtpParameters.codecs
                                .find((c) => c.payloadType === codec.payloadType);
                            switch (codec.mimeType.toLowerCase()) {
                                case 'audio/opus':
                                    {
                                        if (opusStereo !== undefined) {
                                            offerCodec.parameters['sprop-stereo'] = opusStereo ? 1 : 0;
                                            codecParameters.stereo = opusStereo ? 1 : 0;
                                        }
                                        if (opusFec !== undefined) {
                                            offerCodec.parameters.useinbandfec = opusFec ? 1 : 0;
                                            codecParameters.useinbandfec = opusFec ? 1 : 0;
                                        }
                                        if (opusDtx !== undefined) {
                                            offerCodec.parameters.usedtx = opusDtx ? 1 : 0;
                                            codecParameters.usedtx = opusDtx ? 1 : 0;
                                        }
                                        if (opusMaxPlaybackRate !== undefined) {
                                            codecParameters.maxplaybackrate = opusMaxPlaybackRate;
                                        }
                                        if (opusPtime !== undefined) {
                                            offerCodec.parameters.ptime = opusPtime;
                                            codecParameters.ptime = opusPtime;
                                        }
                                        break;
                                    }
                                case 'video/vp8':
                                case 'video/vp9':
                                case 'video/h264':
                                case 'video/h265':
                                    {
                                        if (videoGoogleStartBitrate !== undefined)
                                            codecParameters['x-google-start-bitrate'] = videoGoogleStartBitrate;
                                        if (videoGoogleMaxBitrate !== undefined)
                                            codecParameters['x-google-max-bitrate'] = videoGoogleMaxBitrate;
                                        if (videoGoogleMinBitrate !== undefined)
                                            codecParameters['x-google-min-bitrate'] = videoGoogleMinBitrate;
                                        break;
                                    }
                            }
                        }
                        const fmtp = {
                            payload: codec.payloadType,
                            config: ''
                        };
                        for (const key of Object.keys(codecParameters)) {
                            if (fmtp.config)
                                fmtp.config += ';';
                            fmtp.config += `${key}=${codecParameters[key]}`;
                        }
                        if (fmtp.config)
                            this._mediaObject.fmtp.push(fmtp);
                        for (const fb of codec.rtcpFeedback) {
                            this._mediaObject.rtcpFb.push({
                                payload: codec.payloadType,
                                type: fb.type,
                                subtype: fb.parameter
                            });
                        }
                    }
                    this._mediaObject.payloads = answerRtpParameters.codecs
                        .map((codec) => codec.payloadType)
                        .join(' ');
                    this._mediaObject.ext = [];
                    for (const ext of answerRtpParameters.headerExtensions) {
                        // Don't add a header extension if not present in the offer.
                        const found = (offerMediaObject.ext || [])
                            .some((localExt) => localExt.uri === ext.uri);
                        if (!found)
                            continue;
                        this._mediaObject.ext.push({
                            uri: ext.uri,
                            value: ext.id
                        });
                    }
                    // Allow both 1 byte and 2 bytes length header extensions.
                    if (extmapAllowMixed &&
                        offerMediaObject.extmapAllowMixed === 'extmap-allow-mixed') {
                        this._mediaObject.extmapAllowMixed = 'extmap-allow-mixed';
                    }
                    // Simulcast.
                    if (offerMediaObject.simulcast) {
                        this._mediaObject.simulcast =
                            {
                                dir1: 'recv',
                                list1: offerMediaObject.simulcast.list1
                            };
                        this._mediaObject.rids = [];
                        for (const rid of offerMediaObject.rids || []) {
                            if (rid.direction !== 'send')
                                continue;
                            this._mediaObject.rids.push({
                                id: rid.id,
                                direction: 'recv'
                            });
                        }
                    }
                    // Simulcast (draft version 03).
                    else if (offerMediaObject.simulcast_03) {
                        // eslint-disable-next-line camelcase, @typescript-eslint/camelcase
                        this._mediaObject.simulcast_03 =
                            {
                                value: offerMediaObject.simulcast_03.value.replace(/send/g, 'recv')
                            };
                        this._mediaObject.rids = [];
                        for (const rid of offerMediaObject.rids || []) {
                            if (rid.direction !== 'send')
                                continue;
                            this._mediaObject.rids.push({
                                id: rid.id,
                                direction: 'recv'
                            });
                        }
                    }
                    this._mediaObject.rtcpMux = 'rtcp-mux';
                    this._mediaObject.rtcpRsize = 'rtcp-rsize';
                    if (this._planB && this._mediaObject.type === 'video')
                        this._mediaObject.xGoogleFlag = 'conference';
                    break;
                }
            case 'application':
                {
                    // New spec.
                    if (typeof offerMediaObject.sctpPort === 'number') {
                        this._mediaObject.payloads = 'webrtc-datachannel';
                        this._mediaObject.sctpPort = sctpParameters.port;
                        this._mediaObject.maxMessageSize = sctpParameters.maxMessageSize;
                    }
                    // Old spec.
                    else if (offerMediaObject.sctpmap) {
                        this._mediaObject.payloads = sctpParameters.port;
                        this._mediaObject.sctpmap =
                            {
                                app: 'webrtc-datachannel',
                                sctpmapNumber: sctpParameters.port,
                                maxMessageSize: sctpParameters.maxMessageSize
                            };
                    }
                    break;
                }
        }
    }
    setDtlsRole(role) {
        switch (role) {
            case 'client':
                this._mediaObject.setup = 'active';
                break;
            case 'server':
                this._mediaObject.setup = 'passive';
                break;
            case 'auto':
                this._mediaObject.setup = 'actpass';
                break;
        }
    }
}
exports.AnswerMediaSection = AnswerMediaSection;
class OfferMediaSection extends MediaSection {
    constructor({ iceParameters, iceCandidates, dtlsParameters, sctpParameters, plainRtpParameters, planB = false, mid, kind, offerRtpParameters, streamId, trackId, oldDataChannelSpec = false }) {
        super({ iceParameters, iceCandidates, dtlsParameters, planB });
        this._mediaObject.mid = String(mid);
        this._mediaObject.type = kind;
        if (!plainRtpParameters) {
            this._mediaObject.connection = { ip: '127.0.0.1', version: 4 };
            if (!sctpParameters)
                this._mediaObject.protocol = 'UDP/TLS/RTP/SAVPF';
            else
                this._mediaObject.protocol = 'UDP/DTLS/SCTP';
            this._mediaObject.port = 7;
        }
        else {
            this._mediaObject.connection =
                {
                    ip: plainRtpParameters.ip,
                    version: plainRtpParameters.ipVersion
                };
            this._mediaObject.protocol = 'RTP/AVP';
            this._mediaObject.port = plainRtpParameters.port;
        }
        switch (kind) {
            case 'audio':
            case 'video':
                {
                    this._mediaObject.direction = 'sendonly';
                    this._mediaObject.rtp = [];
                    this._mediaObject.rtcpFb = [];
                    this._mediaObject.fmtp = [];
                    if (!this._planB)
                        this._mediaObject.msid = `${streamId || '-'} ${trackId}`;
                    for (const codec of offerRtpParameters.codecs) {
                        const rtp = {
                            payload: codec.payloadType,
                            codec: getCodecName(codec),
                            rate: codec.clockRate
                        };
                        if (codec.channels > 1)
                            rtp.encoding = codec.channels;
                        this._mediaObject.rtp.push(rtp);
                        const fmtp = {
                            payload: codec.payloadType,
                            config: ''
                        };
                        for (const key of Object.keys(codec.parameters)) {
                            if (fmtp.config)
                                fmtp.config += ';';
                            fmtp.config += `${key}=${codec.parameters[key]}`;
                        }
                        if (fmtp.config)
                            this._mediaObject.fmtp.push(fmtp);
                        for (const fb of codec.rtcpFeedback) {
                            this._mediaObject.rtcpFb.push({
                                payload: codec.payloadType,
                                type: fb.type,
                                subtype: fb.parameter
                            });
                        }
                    }
                    this._mediaObject.payloads = offerRtpParameters.codecs
                        .map((codec) => codec.payloadType)
                        .join(' ');
                    this._mediaObject.ext = [];
                    for (const ext of offerRtpParameters.headerExtensions) {
                        this._mediaObject.ext.push({
                            uri: ext.uri,
                            value: ext.id
                        });
                    }
                    this._mediaObject.rtcpMux = 'rtcp-mux';
                    this._mediaObject.rtcpRsize = 'rtcp-rsize';
                    const encoding = offerRtpParameters.encodings[0];
                    const ssrc = encoding.ssrc;
                    const rtxSsrc = (encoding.rtx && encoding.rtx.ssrc)
                        ? encoding.rtx.ssrc
                        : undefined;
                    this._mediaObject.ssrcs = [];
                    this._mediaObject.ssrcGroups = [];
                    if (offerRtpParameters.rtcp.cname) {
                        this._mediaObject.ssrcs.push({
                            id: ssrc,
                            attribute: 'cname',
                            value: offerRtpParameters.rtcp.cname
                        });
                    }
                    if (this._planB) {
                        this._mediaObject.ssrcs.push({
                            id: ssrc,
                            attribute: 'msid',
                            value: `${streamId || '-'} ${trackId}`
                        });
                    }
                    if (rtxSsrc) {
                        if (offerRtpParameters.rtcp.cname) {
                            this._mediaObject.ssrcs.push({
                                id: rtxSsrc,
                                attribute: 'cname',
                                value: offerRtpParameters.rtcp.cname
                            });
                        }
                        if (this._planB) {
                            this._mediaObject.ssrcs.push({
                                id: rtxSsrc,
                                attribute: 'msid',
                                value: `${streamId || '-'} ${trackId}`
                            });
                        }
                        // Associate original and retransmission SSRCs.
                        this._mediaObject.ssrcGroups.push({
                            semantics: 'FID',
                            ssrcs: `${ssrc} ${rtxSsrc}`
                        });
                    }
                    break;
                }
            case 'application':
                {
                    // New spec.
                    if (!oldDataChannelSpec) {
                        this._mediaObject.payloads = 'webrtc-datachannel';
                        this._mediaObject.sctpPort = sctpParameters.port;
                        this._mediaObject.maxMessageSize = sctpParameters.maxMessageSize;
                    }
                    // Old spec.
                    else {
                        this._mediaObject.payloads = sctpParameters.port;
                        this._mediaObject.sctpmap =
                            {
                                app: 'webrtc-datachannel',
                                sctpmapNumber: sctpParameters.port,
                                maxMessageSize: sctpParameters.maxMessageSize
                            };
                    }
                    break;
                }
        }
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    setDtlsRole(role) {
        // Always 'actpass'.
        this._mediaObject.setup = 'actpass';
    }
    planBReceive({ offerRtpParameters, streamId, trackId }) {
        const encoding = offerRtpParameters.encodings[0];
        const ssrc = encoding.ssrc;
        const rtxSsrc = (encoding.rtx && encoding.rtx.ssrc)
            ? encoding.rtx.ssrc
            : undefined;
        if (offerRtpParameters.rtcp.cname) {
            this._mediaObject.ssrcs.push({
                id: ssrc,
                attribute: 'cname',
                value: offerRtpParameters.rtcp.cname
            });
        }
        this._mediaObject.ssrcs.push({
            id: ssrc,
            attribute: 'msid',
            value: `${streamId || '-'} ${trackId}`
        });
        if (rtxSsrc) {
            if (offerRtpParameters.rtcp.cname) {
                this._mediaObject.ssrcs.push({
                    id: rtxSsrc,
                    attribute: 'cname',
                    value: offerRtpParameters.rtcp.cname
                });
            }
            this._mediaObject.ssrcs.push({
                id: rtxSsrc,
                attribute: 'msid',
                value: `${streamId || '-'} ${trackId}`
            });
            // Associate original and retransmission SSRCs.
            this._mediaObject.ssrcGroups.push({
                semantics: 'FID',
                ssrcs: `${ssrc} ${rtxSsrc}`
            });
        }
    }
    planBStopReceiving({ offerRtpParameters }) {
        const encoding = offerRtpParameters.encodings[0];
        const ssrc = encoding.ssrc;
        const rtxSsrc = (encoding.rtx && encoding.rtx.ssrc)
            ? encoding.rtx.ssrc
            : undefined;
        this._mediaObject.ssrcs = this._mediaObject.ssrcs
            .filter((s) => s.id !== ssrc && s.id !== rtxSsrc);
        if (rtxSsrc) {
            this._mediaObject.ssrcGroups = this._mediaObject.ssrcGroups
                .filter((group) => group.ssrcs !== `${ssrc} ${rtxSsrc}`);
        }
    }
}
exports.OfferMediaSection = OfferMediaSection;
function getCodecName(codec) {
    const MimeTypeRegex = new RegExp('^(audio|video)/(.+)', 'i');
    const mimeTypeMatch = MimeTypeRegex.exec(codec.mimeType);
    if (!mimeTypeMatch)
        throw new TypeError('invalid codec.mimeType');
    return mimeTypeMatch[2];
}

},{"../../utils":68}],60:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sdpTransform = __importStar(require("sdp-transform"));
const Logger_1 = require("../../Logger");
const MediaSection_1 = require("./MediaSection");
const logger = new Logger_1.Logger('RemoteSdp');
class RemoteSdp {
    constructor({ iceParameters, iceCandidates, dtlsParameters, sctpParameters, plainRtpParameters, planB = false }) {
        // MediaSection instances indexed by MID.
        this._mediaSections = new Map();
        this._iceParameters = iceParameters;
        this._iceCandidates = iceCandidates;
        this._dtlsParameters = dtlsParameters;
        this._sctpParameters = sctpParameters;
        this._plainRtpParameters = plainRtpParameters;
        this._planB = planB;
        this._sdpObject =
            {
                version: 0,
                origin: {
                    address: '0.0.0.0',
                    ipVer: 4,
                    netType: 'IN',
                    sessionId: 10000,
                    sessionVersion: 0,
                    username: 'mediasoup-client'
                },
                name: '-',
                timing: { start: 0, stop: 0 },
                media: []
            };
        // If ICE parameters are given, add ICE-Lite indicator.
        if (iceParameters && iceParameters.iceLite) {
            this._sdpObject.icelite = 'ice-lite';
        }
        // If DTLS parameters are given, assume WebRTC and BUNDLE.
        if (dtlsParameters) {
            this._sdpObject.msidSemantic = { semantic: 'WMS', token: '*' };
            // NOTE: We take the latest fingerprint.
            const numFingerprints = this._dtlsParameters.fingerprints.length;
            this._sdpObject.fingerprint =
                {
                    type: dtlsParameters.fingerprints[numFingerprints - 1].algorithm,
                    hash: dtlsParameters.fingerprints[numFingerprints - 1].value
                };
            this._sdpObject.groups = [{ type: 'BUNDLE', mids: '' }];
        }
        // If there are plain RPT parameters, override SDP origin.
        if (plainRtpParameters) {
            this._sdpObject.origin.address = plainRtpParameters.ip;
            this._sdpObject.origin.ipVer = plainRtpParameters.ipVersion;
        }
    }
    updateIceParameters(iceParameters) {
        logger.debug('updateIceParameters() [iceParameters:%o]', iceParameters);
        this._iceParameters = iceParameters;
        this._sdpObject.icelite = iceParameters.iceLite ? 'ice-lite' : undefined;
        for (const mediaSection of this._mediaSections.values()) {
            mediaSection.setIceParameters(iceParameters);
        }
    }
    updateDtlsRole(role) {
        logger.debug('updateDtlsRole() [role:%s]', role);
        this._dtlsParameters.role = role;
        for (const mediaSection of this._mediaSections.values()) {
            mediaSection.setDtlsRole(role);
        }
    }
    getNextMediaSectionIdx() {
        let idx = -1;
        // If a closed media section is found, return its index.
        for (const mediaSection of this._mediaSections.values()) {
            idx++;
            if (mediaSection.closed)
                return { idx, reuseMid: mediaSection.mid };
        }
        // If no closed media section is found, return next one.
        return { idx: this._mediaSections.size, reuseMid: false };
    }
    send({ offerMediaObject, reuseMid, offerRtpParameters, answerRtpParameters, codecOptions, extmapAllowMixed = false }) {
        const mediaSection = new MediaSection_1.AnswerMediaSection({
            iceParameters: this._iceParameters,
            iceCandidates: this._iceCandidates,
            dtlsParameters: this._dtlsParameters,
            plainRtpParameters: this._plainRtpParameters,
            planB: this._planB,
            offerMediaObject,
            offerRtpParameters,
            answerRtpParameters,
            codecOptions,
            extmapAllowMixed
        });
        // Unified-Plan with closed media section replacement.
        if (reuseMid) {
            this._replaceMediaSection(mediaSection, reuseMid);
        }
        // Unified-Plan or Plan-B with different media kind.
        else if (!this._mediaSections.has(mediaSection.mid)) {
            this._addMediaSection(mediaSection);
        }
        // Plan-B with same media kind.
        else {
            this._replaceMediaSection(mediaSection);
        }
    }
    receive({ mid, kind, offerRtpParameters, streamId, trackId }) {
        // Unified-Plan or different media kind.
        if (!this._mediaSections.has(mid)) {
            const mediaSection = new MediaSection_1.OfferMediaSection({
                iceParameters: this._iceParameters,
                iceCandidates: this._iceCandidates,
                dtlsParameters: this._dtlsParameters,
                plainRtpParameters: this._plainRtpParameters,
                planB: this._planB,
                mid,
                kind,
                offerRtpParameters,
                streamId,
                trackId
            });
            this._addMediaSection(mediaSection);
        }
        // Plan-B.
        else {
            const mediaSection = this._mediaSections.get(mid);
            mediaSection.planBReceive({ offerRtpParameters, streamId, trackId });
            this._replaceMediaSection(mediaSection);
        }
    }
    disableMediaSection(mid) {
        const mediaSection = this._mediaSections.get(mid);
        mediaSection.disable();
    }
    closeMediaSection(mid) {
        const mediaSection = this._mediaSections.get(mid);
        // NOTE: Closing the first m section is a pain since it invalidates the
        // bundled transport, so let's avoid it.
        if (String(mid) === this._firstMid) {
            logger.debug('closeMediaSection() | cannot close first media section, disabling it instead [mid:%s]', mid);
            this.disableMediaSection(mid);
            return;
        }
        mediaSection.close();
        // Regenerate BUNDLE mids.
        this._regenerateBundleMids();
    }
    planBStopReceiving({ mid, offerRtpParameters }) {
        const mediaSection = this._mediaSections.get(mid);
        mediaSection.planBStopReceiving({ offerRtpParameters });
        this._replaceMediaSection(mediaSection);
    }
    sendSctpAssociation({ offerMediaObject }) {
        const mediaSection = new MediaSection_1.AnswerMediaSection({
            iceParameters: this._iceParameters,
            iceCandidates: this._iceCandidates,
            dtlsParameters: this._dtlsParameters,
            sctpParameters: this._sctpParameters,
            plainRtpParameters: this._plainRtpParameters,
            offerMediaObject
        });
        this._addMediaSection(mediaSection);
    }
    receiveSctpAssociation({ oldDataChannelSpec = false } = {}) {
        const mediaSection = new MediaSection_1.OfferMediaSection({
            iceParameters: this._iceParameters,
            iceCandidates: this._iceCandidates,
            dtlsParameters: this._dtlsParameters,
            sctpParameters: this._sctpParameters,
            plainRtpParameters: this._plainRtpParameters,
            mid: 'datachannel',
            kind: 'application',
            oldDataChannelSpec
        });
        this._addMediaSection(mediaSection);
    }
    getSdp() {
        // Increase SDP version.
        this._sdpObject.origin.sessionVersion++;
        return sdpTransform.write(this._sdpObject);
    }
    _addMediaSection(newMediaSection) {
        if (!this._firstMid)
            this._firstMid = newMediaSection.mid;
        // Store it in the map.
        this._mediaSections.set(newMediaSection.mid, newMediaSection);
        // Update SDP object.
        this._sdpObject.media.push(newMediaSection.getObject());
        // Regenerate BUNDLE mids.
        this._regenerateBundleMids();
    }
    _replaceMediaSection(newMediaSection, reuseMid) {
        // Store it in the map.
        if (reuseMid) {
            const newMediaSections = new Map();
            for (const mediaSection of this._mediaSections.values()) {
                if (mediaSection.mid === reuseMid)
                    newMediaSections.set(newMediaSection.mid, newMediaSection);
                else
                    newMediaSections.set(mediaSection.mid, mediaSection);
            }
            // Regenerate media sections.
            this._mediaSections = newMediaSections;
            // Regenerate BUNDLE mids.
            this._regenerateBundleMids();
        }
        else {
            this._mediaSections.set(newMediaSection.mid, newMediaSection);
        }
        // Update SDP object.
        this._sdpObject.media = Array.from(this._mediaSections.values())
            .map((mediaSection) => mediaSection.getObject());
    }
    _regenerateBundleMids() {
        if (!this._dtlsParameters)
            return;
        this._sdpObject.groups[0].mids = Array.from(this._mediaSections.values())
            .filter((mediaSection) => !mediaSection.closed)
            .map((mediaSection) => mediaSection.mid)
            .join(' ');
    }
}
exports.RemoteSdp = RemoteSdp;

},{"../../Logger":44,"./MediaSection":59,"sdp-transform":71}],61:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const sdpTransform = __importStar(require("sdp-transform"));
function extractRtpCapabilities({ sdpObject }) {
    // Map of RtpCodecParameters indexed by payload type.
    const codecsMap = new Map();
    // Array of RtpHeaderExtensions.
    const headerExtensions = [];
    // Whether a m=audio/video section has been already found.
    let gotAudio = false;
    let gotVideo = false;
    for (const m of sdpObject.media) {
        const kind = m.type;
        switch (kind) {
            case 'audio':
                {
                    if (gotAudio)
                        continue;
                    gotAudio = true;
                    break;
                }
            case 'video':
                {
                    if (gotVideo)
                        continue;
                    gotVideo = true;
                    break;
                }
            default:
                {
                    continue;
                }
        }
        // Get codecs.
        for (const rtp of m.rtp) {
            const codec = {
                kind: kind,
                mimeType: `${kind}/${rtp.codec}`,
                preferredPayloadType: rtp.payload,
                clockRate: rtp.rate,
                channels: rtp.encoding,
                parameters: {},
                rtcpFeedback: []
            };
            codecsMap.set(codec.preferredPayloadType, codec);
        }
        // Get codec parameters.
        for (const fmtp of m.fmtp || []) {
            const parameters = sdpTransform.parseParams(fmtp.config);
            const codec = codecsMap.get(fmtp.payload);
            if (!codec)
                continue;
            // Specials case to convert parameter value to string.
            if (parameters && parameters['profile-level-id'])
                parameters['profile-level-id'] = String(parameters['profile-level-id']);
            codec.parameters = parameters;
        }
        // Get RTCP feedback for each codec.
        for (const fb of m.rtcpFb || []) {
            const codec = codecsMap.get(fb.payload);
            if (!codec)
                continue;
            const feedback = {
                type: fb.type,
                parameter: fb.subtype
            };
            if (!feedback.parameter)
                delete feedback.parameter;
            codec.rtcpFeedback.push(feedback);
        }
        // Get RTP header extensions.
        for (const ext of m.ext || []) {
            const headerExtension = {
                kind: kind,
                uri: ext.uri,
                preferredId: ext.value
            };
            headerExtensions.push(headerExtension);
        }
    }
    const rtpCapabilities = {
        codecs: Array.from(codecsMap.values()),
        headerExtensions: headerExtensions
    };
    return rtpCapabilities;
}
exports.extractRtpCapabilities = extractRtpCapabilities;
function extractDtlsParameters({ sdpObject }) {
    const mediaObject = (sdpObject.media || [])
        .find((m) => m.iceUfrag && m.port !== 0);
    if (!mediaObject)
        throw new Error('no active media section found');
    const fingerprint = mediaObject.fingerprint || sdpObject.fingerprint;
    let role;
    switch (mediaObject.setup) {
        case 'active':
            role = 'client';
            break;
        case 'passive':
            role = 'server';
            break;
        case 'actpass':
            role = 'auto';
            break;
    }
    const dtlsParameters = {
        role,
        fingerprints: [
            {
                algorithm: fingerprint.type,
                value: fingerprint.hash
            }
        ]
    };
    return dtlsParameters;
}
exports.extractDtlsParameters = extractDtlsParameters;
function getCname({ offerMediaObject }) {
    const ssrcCnameLine = (offerMediaObject.ssrcs || [])
        .find((line) => line.attribute === 'cname');
    if (!ssrcCnameLine)
        return '';
    return ssrcCnameLine.value;
}
exports.getCname = getCname;
/**
 * Apply codec parameters in the given SDP m= section answer based on the
 * given RTP parameters of an offer.
 */
function applyCodecParameters({ offerRtpParameters, answerMediaObject }) {
    for (const codec of offerRtpParameters.codecs) {
        const mimeType = codec.mimeType.toLowerCase();
        // Avoid parsing codec parameters for unhandled codecs.
        if (mimeType !== 'audio/opus')
            continue;
        const rtp = (answerMediaObject.rtp || [])
            .find((r) => r.payload === codec.payloadType);
        if (!rtp)
            continue;
        // Just in case.
        answerMediaObject.fmtp = answerMediaObject.fmtp || [];
        let fmtp = answerMediaObject.fmtp
            .find((f) => f.payload === codec.payloadType);
        if (!fmtp) {
            fmtp = { payload: codec.payloadType, config: '' };
            answerMediaObject.fmtp.push(fmtp);
        }
        const parameters = sdpTransform.parseParams(fmtp.config);
        switch (mimeType) {
            case 'audio/opus':
                {
                    const spropStereo = codec.parameters['sprop-stereo'];
                    if (spropStereo !== undefined)
                        parameters.stereo = spropStereo ? 1 : 0;
                    break;
                }
        }
        // Write the codec fmtp.config back.
        fmtp.config = '';
        for (const key of Object.keys(parameters)) {
            if (fmtp.config)
                fmtp.config += ';';
            fmtp.config += `${key}=${parameters[key]}`;
        }
    }
}
exports.applyCodecParameters = applyCodecParameters;

},{"sdp-transform":71}],62:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
function getRtpEncodings({ offerMediaObject, track }) {
    // First media SSRC (or the only one).
    let firstSsrc;
    const ssrcs = new Set();
    for (const line of offerMediaObject.ssrcs || []) {
        if (line.attribute !== 'msid')
            continue;
        const trackId = line.value.split(' ')[1];
        if (trackId === track.id) {
            const ssrc = line.id;
            ssrcs.add(ssrc);
            if (!firstSsrc)
                firstSsrc = ssrc;
        }
    }
    if (ssrcs.size === 0)
        throw new Error(`a=ssrc line with msid information not found [track.id:${track.id}]`);
    const ssrcToRtxSsrc = new Map();
    // First assume RTX is used.
    for (const line of offerMediaObject.ssrcGroups || []) {
        if (line.semantics !== 'FID')
            continue;
        let [ssrc, rtxSsrc] = line.ssrcs.split(/\s+/);
        ssrc = Number(ssrc);
        rtxSsrc = Number(rtxSsrc);
        if (ssrcs.has(ssrc)) {
            // Remove both the SSRC and RTX SSRC from the set so later we know that they
            // are already handled.
            ssrcs.delete(ssrc);
            ssrcs.delete(rtxSsrc);
            // Add to the map.
            ssrcToRtxSsrc.set(ssrc, rtxSsrc);
        }
    }
    // If the set of SSRCs is not empty it means that RTX is not being used, so take
    // media SSRCs from there.
    for (const ssrc of ssrcs) {
        // Add to the map.
        ssrcToRtxSsrc.set(ssrc, null);
    }
    const encodings = [];
    for (const [ssrc, rtxSsrc] of ssrcToRtxSsrc) {
        const encoding = { ssrc };
        if (rtxSsrc)
            encoding.rtx = { ssrc: rtxSsrc };
        encodings.push(encoding);
    }
    return encodings;
}
exports.getRtpEncodings = getRtpEncodings;
/**
 * Adds multi-ssrc based simulcast into the given SDP media section offer.
 */
function addLegacySimulcast({ offerMediaObject, track, numStreams }) {
    if (numStreams <= 1)
        throw new TypeError('numStreams must be greater than 1');
    let firstSsrc;
    let firstRtxSsrc;
    let streamId;
    // Get the SSRC.
    const ssrcMsidLine = (offerMediaObject.ssrcs || [])
        .find((line) => {
        if (line.attribute !== 'msid')
            return false;
        const trackId = line.value.split(' ')[1];
        if (trackId === track.id) {
            firstSsrc = line.id;
            streamId = line.value.split(' ')[0];
            return true;
        }
        else {
            return false;
        }
    });
    if (!ssrcMsidLine)
        throw new Error(`a=ssrc line with msid information not found [track.id:${track.id}]`);
    // Get the SSRC for RTX.
    (offerMediaObject.ssrcGroups || [])
        .some((line) => {
        if (line.semantics !== 'FID')
            return false;
        const ssrcs = line.ssrcs.split(/\s+/);
        if (Number(ssrcs[0]) === firstSsrc) {
            firstRtxSsrc = Number(ssrcs[1]);
            return true;
        }
        else {
            return false;
        }
    });
    const ssrcCnameLine = offerMediaObject.ssrcs
        .find((line) => (line.attribute === 'cname' && line.id === firstSsrc));
    if (!ssrcCnameLine)
        throw new Error(`a=ssrc line with cname information not found [track.id:${track.id}]`);
    const cname = ssrcCnameLine.value;
    const ssrcs = [];
    const rtxSsrcs = [];
    for (let i = 0; i < numStreams; ++i) {
        ssrcs.push(firstSsrc + i);
        if (firstRtxSsrc)
            rtxSsrcs.push(firstRtxSsrc + i);
    }
    offerMediaObject.ssrcGroups = offerMediaObject.ssrcGroups || [];
    offerMediaObject.ssrcs = offerMediaObject.ssrcs || [];
    offerMediaObject.ssrcGroups.push({
        semantics: 'SIM',
        ssrcs: ssrcs.join(' ')
    });
    for (let i = 0; i < ssrcs.length; ++i) {
        const ssrc = ssrcs[i];
        offerMediaObject.ssrcs.push({
            id: ssrc,
            attribute: 'cname',
            value: cname
        });
        offerMediaObject.ssrcs.push({
            id: ssrc,
            attribute: 'msid',
            value: `${streamId} ${track.id}`
        });
    }
    for (let i = 0; i < rtxSsrcs.length; ++i) {
        const ssrc = ssrcs[i];
        const rtxSsrc = rtxSsrcs[i];
        offerMediaObject.ssrcs.push({
            id: rtxSsrc,
            attribute: 'cname',
            value: cname
        });
        offerMediaObject.ssrcs.push({
            id: rtxSsrc,
            attribute: 'msid',
            value: `${streamId} ${track.id}`
        });
        offerMediaObject.ssrcGroups.push({
            semantics: 'FID',
            ssrcs: `${ssrc} ${rtxSsrc}`
        });
    }
}
exports.addLegacySimulcast = addLegacySimulcast;

},{}],63:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
function getRtpEncodings({ offerMediaObject }) {
    const ssrcs = new Set();
    for (const line of offerMediaObject.ssrcs || []) {
        const ssrc = line.id;
        ssrcs.add(ssrc);
    }
    if (ssrcs.size === 0)
        throw new Error('no a=ssrc lines found');
    const ssrcToRtxSsrc = new Map();
    // First assume RTX is used.
    for (const line of offerMediaObject.ssrcGroups || []) {
        if (line.semantics !== 'FID')
            continue;
        let [ssrc, rtxSsrc] = line.ssrcs.split(/\s+/);
        ssrc = Number(ssrc);
        rtxSsrc = Number(rtxSsrc);
        if (ssrcs.has(ssrc)) {
            // Remove both the SSRC and RTX SSRC from the set so later we know that they
            // are already handled.
            ssrcs.delete(ssrc);
            ssrcs.delete(rtxSsrc);
            // Add to the map.
            ssrcToRtxSsrc.set(ssrc, rtxSsrc);
        }
    }
    // If the set of SSRCs is not empty it means that RTX is not being used, so take
    // media SSRCs from there.
    for (const ssrc of ssrcs) {
        // Add to the map.
        ssrcToRtxSsrc.set(ssrc, null);
    }
    const encodings = [];
    for (const [ssrc, rtxSsrc] of ssrcToRtxSsrc) {
        const encoding = { ssrc };
        if (rtxSsrc)
            encoding.rtx = { ssrc: rtxSsrc };
        encodings.push(encoding);
    }
    return encodings;
}
exports.getRtpEncodings = getRtpEncodings;
/**
 * Adds multi-ssrc based simulcast into the given SDP media section offer.
 */
function addLegacySimulcast({ offerMediaObject, numStreams }) {
    if (numStreams <= 1)
        throw new TypeError('numStreams must be greater than 1');
    // Get the SSRC.
    const ssrcMsidLine = (offerMediaObject.ssrcs || [])
        .find((line) => line.attribute === 'msid');
    if (!ssrcMsidLine)
        throw new Error('a=ssrc line with msid information not found');
    const [streamId, trackId] = ssrcMsidLine.value.split(' ')[0];
    const firstSsrc = ssrcMsidLine.id;
    let firstRtxSsrc;
    // Get the SSRC for RTX.
    (offerMediaObject.ssrcGroups || [])
        .some((line) => {
        if (line.semantics !== 'FID')
            return false;
        const ssrcs = line.ssrcs.split(/\s+/);
        if (Number(ssrcs[0]) === firstSsrc) {
            firstRtxSsrc = Number(ssrcs[1]);
            return true;
        }
        else {
            return false;
        }
    });
    const ssrcCnameLine = offerMediaObject.ssrcs
        .find((line) => line.attribute === 'cname');
    if (!ssrcCnameLine)
        throw new Error('a=ssrc line with cname information not found');
    const cname = ssrcCnameLine.value;
    const ssrcs = [];
    const rtxSsrcs = [];
    for (let i = 0; i < numStreams; ++i) {
        ssrcs.push(firstSsrc + i);
        if (firstRtxSsrc)
            rtxSsrcs.push(firstRtxSsrc + i);
    }
    offerMediaObject.ssrcGroups = [];
    offerMediaObject.ssrcs = [];
    offerMediaObject.ssrcGroups.push({
        semantics: 'SIM',
        ssrcs: ssrcs.join(' ')
    });
    for (let i = 0; i < ssrcs.length; ++i) {
        const ssrc = ssrcs[i];
        offerMediaObject.ssrcs.push({
            id: ssrc,
            attribute: 'cname',
            value: cname
        });
        offerMediaObject.ssrcs.push({
            id: ssrc,
            attribute: 'msid',
            value: `${streamId} ${trackId}`
        });
    }
    for (let i = 0; i < rtxSsrcs.length; ++i) {
        const ssrc = ssrcs[i];
        const rtxSsrc = rtxSsrcs[i];
        offerMediaObject.ssrcs.push({
            id: rtxSsrc,
            attribute: 'cname',
            value: cname
        });
        offerMediaObject.ssrcs.push({
            id: rtxSsrc,
            attribute: 'msid',
            value: `${streamId} ${trackId}`
        });
        offerMediaObject.ssrcGroups.push({
            semantics: 'FID',
            ssrcs: `${ssrc} ${rtxSsrc}`
        });
    }
}
exports.addLegacySimulcast = addLegacySimulcast;

},{}],64:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const Device_1 = require("./Device");
exports.Device = Device_1.Device;
exports.detectDevice = Device_1.detectDevice;
const types = __importStar(require("./types"));
exports.types = types;
/**
 * Expose mediasoup-client version.
 */
exports.version = '3.3.17';
/**
 * Expose parseScalabilityMode() function.
 */
var scalabilityModes_1 = require("./scalabilityModes");
exports.parseScalabilityMode = scalabilityModes_1.parse;

},{"./Device":42,"./scalabilityModes":66,"./types":67}],65:[function(require,module,exports){
"use strict";
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result["default"] = mod;
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
const h264 = __importStar(require("h264-profile-level-id"));
const PROBATOR_SSRC = 1234;
/**
 * Validates RtpCapabilities. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateRtpCapabilities(caps) {
    if (typeof caps !== 'object')
        throw new TypeError('caps is not an object');
    // codecs is optional. If unset, fill with an empty array.
    if (caps.codecs && !Array.isArray(caps.codecs))
        throw new TypeError('caps.codecs is not an array');
    else if (!caps.codecs)
        caps.codecs = [];
    for (const codec of caps.codecs) {
        validateRtpCodecCapability(codec);
    }
    // headerExtensions is optional. If unset, fill with an empty array.
    if (caps.headerExtensions && !Array.isArray(caps.headerExtensions))
        throw new TypeError('caps.headerExtensions is not an array');
    else if (!caps.headerExtensions)
        caps.headerExtensions = [];
    for (const ext of caps.headerExtensions) {
        validateRtpHeaderExtension(ext);
    }
}
exports.validateRtpCapabilities = validateRtpCapabilities;
/**
 * Validates RtpCodecCapability. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateRtpCodecCapability(codec) {
    const MimeTypeRegex = new RegExp('^(audio|video)/(.+)', 'i');
    if (typeof codec !== 'object')
        throw new TypeError('codec is not an object');
    // mimeType is mandatory.
    if (!codec.mimeType || typeof codec.mimeType !== 'string')
        throw new TypeError('missing codec.mimeType');
    const mimeTypeMatch = MimeTypeRegex.exec(codec.mimeType);
    if (!mimeTypeMatch)
        throw new TypeError('invalid codec.mimeType');
    // Just override kind with media component of mimeType.
    codec.kind = mimeTypeMatch[1].toLowerCase();
    // preferredPayloadType is optional.
    if (codec.preferredPayloadType && typeof codec.preferredPayloadType !== 'number')
        throw new TypeError('invalid codec.preferredPayloadType');
    // clockRate is mandatory.
    if (typeof codec.clockRate !== 'number')
        throw new TypeError('missing codec.clockRate');
    // channels is optional. If unset, set it to 1.
    if (typeof codec.channels !== 'number')
        codec.channels = 1;
    // parameters is optional. If unset, set it to an empty object.
    if (!codec.parameters || typeof codec.parameters !== 'object')
        codec.parameters = {};
    for (const key of Object.keys(codec.parameters)) {
        let value = codec.parameters[key];
        if (value === undefined) {
            codec.parameters[key] = '';
            value = '';
        }
        if (typeof value !== 'string' && typeof value !== 'number') {
            throw new TypeError(`invalid codec parameter [key:${key}s, value:${value}]`);
        }
        // Specific parameters validation.
        if (key === 'apt') {
            if (typeof value !== 'number')
                throw new TypeError('invalid codec apt parameter');
        }
    }
    // rtcpFeedback is optional. If unset, set it to an empty array.
    if (!codec.rtcpFeedback || !Array.isArray(codec.rtcpFeedback))
        codec.rtcpFeedback = [];
    for (const fb of codec.rtcpFeedback) {
        validateRtcpFeedback(fb);
    }
}
exports.validateRtpCodecCapability = validateRtpCodecCapability;
/**
 * Validates RtcpFeedback. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateRtcpFeedback(fb) {
    if (typeof fb !== 'object')
        throw new TypeError('fb is not an object');
    // type is mandatory.
    if (!fb.type || typeof fb.type !== 'string')
        throw new TypeError('missing fb.type');
    // parameter is optional. If unset set it to an empty string.
    if (!fb.parameter || typeof fb.parameter !== 'string')
        fb.parameter = '';
}
exports.validateRtcpFeedback = validateRtcpFeedback;
/**
 * Validates RtpHeaderExtension. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateRtpHeaderExtension(ext) {
    if (typeof ext !== 'object')
        throw new TypeError('ext is not an object');
    // kind is optional. If unset set it to an empty string.
    if (!ext.kind || typeof ext.kind !== 'string')
        ext.kind = '';
    if (ext.kind !== '' && ext.kind !== 'audio' && ext.kind !== 'video')
        throw new TypeError('invalid ext.kind');
    // uri is mandatory.
    if (!ext.uri || typeof ext.uri !== 'string')
        throw new TypeError('missing ext.uri');
    // preferredId is mandatory.
    if (typeof ext.preferredId !== 'number')
        throw new TypeError('missing ext.preferredId');
    // preferredEncrypt is optional. If unset set it to false.
    if (ext.preferredEncrypt && typeof ext.preferredEncrypt !== 'boolean')
        throw new TypeError('invalid ext.preferredEncrypt');
    else if (!ext.preferredEncrypt)
        ext.preferredEncrypt = false;
    // direction is optional. If unset set it to sendrecv.
    if (ext.direction && typeof ext.direction !== 'string')
        throw new TypeError('invalid ext.direction');
    else if (!ext.direction)
        ext.direction = 'sendrecv';
}
exports.validateRtpHeaderExtension = validateRtpHeaderExtension;
/**
 * Validates RtpParameters. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateRtpParameters(params) {
    if (typeof params !== 'object')
        throw new TypeError('params is not an object');
    // mid is optional.
    if (params.mid && typeof params.mid !== 'string')
        throw new TypeError('params.mid is not a string');
    // codecs is mandatory.
    if (!Array.isArray(params.codecs))
        throw new TypeError('missing params.codecs');
    for (const codec of params.codecs) {
        validateRtpCodecParameters(codec);
    }
    // headerExtensions is optional. If unset, fill with an empty array.
    if (params.headerExtensions && !Array.isArray(params.headerExtensions))
        throw new TypeError('params.headerExtensions is not an array');
    else if (!params.headerExtensions)
        params.headerExtensions = [];
    for (const ext of params.headerExtensions) {
        validateRtpHeaderExtensionParameters(ext);
    }
    // encodings is optional. If unset, fill with an empty array.
    if (params.encodings && !Array.isArray(params.encodings))
        throw new TypeError('params.encodings is not an array');
    else if (!params.encodings)
        params.encodings = [];
    for (const encoding of params.encodings) {
        validateRtpEncodingParameters(encoding);
    }
    // rtcp is optional. If unset, fill with an empty object.
    if (params.rtcp && typeof params.rtcp !== 'object')
        throw new TypeError('params.rtcp is not an object');
    else if (!params.rtcp)
        params.rtcp = {};
    validateRtcpParameters(params.rtcp);
}
exports.validateRtpParameters = validateRtpParameters;
/**
 * Validates RtpCodecParameters. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateRtpCodecParameters(codec) {
    const MimeTypeRegex = new RegExp('^(audio|video)/(.+)', 'i');
    if (typeof codec !== 'object')
        throw new TypeError('codec is not an object');
    // mimeType is mandatory.
    if (!codec.mimeType || typeof codec.mimeType !== 'string')
        throw new TypeError('missing codec.mimeType');
    const mimeTypeMatch = MimeTypeRegex.exec(codec.mimeType);
    if (!mimeTypeMatch)
        throw new TypeError('invalid codec.mimeType');
    // payloadType is mandatory.
    if (typeof codec.payloadType !== 'number')
        throw new TypeError('missing codec.payloadType');
    // clockRate is mandatory.
    if (typeof codec.clockRate !== 'number')
        throw new TypeError('missing codec.clockRate');
    // channels is optional. If unset, set it to 1.
    if (typeof codec.channels !== 'number')
        codec.channels = 1;
    // parameters is optional. If unset, set it to an empty object.
    if (!codec.parameters || typeof codec.parameters !== 'object')
        codec.parameters = {};
    for (const key of Object.keys(codec.parameters)) {
        let value = codec.parameters[key];
        if (value === undefined) {
            codec.parameters[key] = '';
            value = '';
        }
        if (typeof value !== 'string' && typeof value !== 'number') {
            throw new TypeError(`invalid codec parameter [key:${key}s, value:${value}]`);
        }
        // Specific parameters validation.
        if (key === 'apt') {
            if (typeof value !== 'number')
                throw new TypeError('invalid codec apt parameter');
        }
    }
    // rtcpFeedback is optional. If unset, set it to an empty array.
    if (!codec.rtcpFeedback || !Array.isArray(codec.rtcpFeedback))
        codec.rtcpFeedback = [];
    for (const fb of codec.rtcpFeedback) {
        validateRtcpFeedback(fb);
    }
}
exports.validateRtpCodecParameters = validateRtpCodecParameters;
/**
 * Validates RtpHeaderExtensionParameteters. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateRtpHeaderExtensionParameters(ext) {
    if (typeof ext !== 'object')
        throw new TypeError('ext is not an object');
    // uri is mandatory.
    if (!ext.uri || typeof ext.uri !== 'string')
        throw new TypeError('missing ext.uri');
    // id is mandatory.
    if (typeof ext.id !== 'number')
        throw new TypeError('missing ext.id');
    // encrypt is optional. If unset set it to false.
    if (ext.encrypt && typeof ext.encrypt !== 'boolean')
        throw new TypeError('invalid ext.encrypt');
    else if (!ext.encrypt)
        ext.encrypt = false;
    // parameters is optional. If unset, set it to an empty object.
    if (!ext.parameters || typeof ext.parameters !== 'object')
        ext.parameters = {};
    for (const key of Object.keys(ext.parameters)) {
        let value = ext.parameters[key];
        if (value === undefined) {
            ext.parameters[key] = '';
            value = '';
        }
        if (typeof value !== 'string' && typeof value !== 'number')
            throw new TypeError('invalid header extension parameter');
    }
}
exports.validateRtpHeaderExtensionParameters = validateRtpHeaderExtensionParameters;
/**
 * Validates RtpEncodingParameters. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateRtpEncodingParameters(encoding) {
    if (typeof encoding !== 'object')
        throw new TypeError('encoding is not an object');
    // ssrc is optional.
    if (encoding.ssrc && typeof encoding.ssrc !== 'number')
        throw new TypeError('invalid encoding.ssrc');
    // rid is optional.
    if (encoding.rid && typeof encoding.rid !== 'string')
        throw new TypeError('invalid encoding.rid');
    // rtx is optional.
    if (encoding.rtx && typeof encoding.rtx !== 'object') {
        throw new TypeError('invalid encoding.rtx');
    }
    else if (encoding.rtx) {
        // RTX ssrc is mandatory if rtx is present.
        if (typeof encoding.rtx.ssrc !== 'number')
            throw new TypeError('missing encoding.rtx.ssrc');
    }
    // dtx is optional. If unset set it to false.
    if (!encoding.dtx || typeof encoding.dtx !== 'boolean')
        encoding.dtx = false;
    // scalabilityMode is optional.
    if (encoding.scalabilityMode && typeof encoding.scalabilityMode !== 'string')
        throw new TypeError('invalid encoding.scalabilityMode');
}
exports.validateRtpEncodingParameters = validateRtpEncodingParameters;
/**
 * Validates RtcpParameters. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateRtcpParameters(rtcp) {
    if (typeof rtcp !== 'object')
        throw new TypeError('rtcp is not an object');
    // cname is optional.
    if (rtcp.cname && typeof rtcp.cname !== 'string')
        throw new TypeError('invalid rtcp.cname');
    // reducedSize is optional. If unset set it to true.
    if (!rtcp.reducedSize || typeof rtcp.reducedSize !== 'boolean')
        rtcp.reducedSize = true;
}
exports.validateRtcpParameters = validateRtcpParameters;
/**
 * Validates SctpCapabilities. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateSctpCapabilities(caps) {
    if (typeof caps !== 'object')
        throw new TypeError('caps is not an object');
    // numStreams is mandatory.
    if (!caps.numStreams || typeof caps.numStreams !== 'object')
        throw new TypeError('missing caps.numStreams');
    validateNumSctpStreams(caps.numStreams);
}
exports.validateSctpCapabilities = validateSctpCapabilities;
/**
 * Validates NumSctpStreams. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateNumSctpStreams(numStreams) {
    if (typeof numStreams !== 'object')
        throw new TypeError('numStreams is not an object');
    // OS is mandatory.
    if (typeof numStreams.OS !== 'number')
        throw new TypeError('missing numStreams.OS');
    // MIS is mandatory.
    if (typeof numStreams.MIS !== 'number')
        throw new TypeError('missing numStreams.MIS');
}
exports.validateNumSctpStreams = validateNumSctpStreams;
/**
 * Validates SctpParameters. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateSctpParameters(params) {
    if (typeof params !== 'object')
        throw new TypeError('params is not an object');
    // port is mandatory.
    if (typeof params.port !== 'number')
        throw new TypeError('missing params.port');
    // OS is mandatory.
    if (typeof params.OS !== 'number')
        throw new TypeError('missing params.OS');
    // MIS is mandatory.
    if (typeof params.MIS !== 'number')
        throw new TypeError('missing params.MIS');
    // maxMessageSize is mandatory.
    if (typeof params.maxMessageSize !== 'number')
        throw new TypeError('missing params.maxMessageSize');
}
exports.validateSctpParameters = validateSctpParameters;
/**
 * Validates SctpStreamParameters. It may modify given data by adding missing
 * fields with default values.
 * It throws if invalid.
 */
function validateSctpStreamParameters(params) {
    if (typeof params !== 'object')
        throw new TypeError('params is not an object');
    // streamId is mandatory.
    if (typeof params.streamId !== 'number')
        throw new TypeError('missing params.streamId');
    // ordered is optional.
    let orderedGiven = false;
    if (typeof params.ordered === 'boolean')
        orderedGiven = true;
    else
        params.ordered = true;
    // maxPacketLifeTime is optional.
    if (params.maxPacketLifeTime && typeof params.maxPacketLifeTime !== 'number')
        throw new TypeError('invalid params.maxPacketLifeTime');
    // maxRetransmits is optional.
    if (params.maxRetransmits && typeof params.maxRetransmits !== 'number')
        throw new TypeError('invalid params.maxRetransmits');
    if (params.maxPacketLifeTime && params.maxRetransmits)
        throw new TypeError('cannot provide both maxPacketLifeTime and maxRetransmits');
    if (orderedGiven &&
        params.ordered &&
        (params.maxPacketLifeTime || params.maxRetransmits)) {
        throw new TypeError('cannot be ordered with maxPacketLifeTime or maxRetransmits');
    }
    else if (!orderedGiven && (params.maxPacketLifeTime || params.maxRetransmits)) {
        params.ordered = false;
    }
    // priority is optional.
    if (params.priority && typeof params.priority !== 'string')
        throw new TypeError('invalid params.priority');
    // label is optional.
    if (params.label && typeof params.label !== 'string')
        throw new TypeError('invalid params.label');
    // protocol is optional.
    if (params.protocol && typeof params.protocol !== 'string')
        throw new TypeError('invalid params.protocol');
}
exports.validateSctpStreamParameters = validateSctpStreamParameters;
/**
 * Generate extended RTP capabilities for sending and receiving.
 */
function getExtendedRtpCapabilities(localCaps, remoteCaps) {
    const extendedRtpCapabilities = {
        codecs: [],
        headerExtensions: []
    };
    // Match media codecs and keep the order preferred by remoteCaps.
    for (const remoteCodec of remoteCaps.codecs || []) {
        if (isRtxCodec(remoteCodec))
            continue;
        const matchingLocalCodec = (localCaps.codecs || [])
            .find((localCodec) => (matchCodecs(localCodec, remoteCodec, { strict: true, modify: true })));
        if (!matchingLocalCodec)
            continue;
        const extendedCodec = {
            mimeType: matchingLocalCodec.mimeType,
            kind: matchingLocalCodec.kind,
            clockRate: matchingLocalCodec.clockRate,
            channels: matchingLocalCodec.channels,
            localPayloadType: matchingLocalCodec.preferredPayloadType,
            localRtxPayloadType: undefined,
            remotePayloadType: remoteCodec.preferredPayloadType,
            remoteRtxPayloadType: undefined,
            localParameters: matchingLocalCodec.parameters,
            remoteParameters: remoteCodec.parameters,
            rtcpFeedback: reduceRtcpFeedback(matchingLocalCodec, remoteCodec)
        };
        extendedRtpCapabilities.codecs.push(extendedCodec);
    }
    // Match RTX codecs.
    for (const extendedCodec of extendedRtpCapabilities.codecs) {
        const matchingLocalRtxCodec = localCaps.codecs
            .find((localCodec) => (isRtxCodec(localCodec) &&
            localCodec.parameters.apt === extendedCodec.localPayloadType));
        const matchingRemoteRtxCodec = remoteCaps.codecs
            .find((remoteCodec) => (isRtxCodec(remoteCodec) &&
            remoteCodec.parameters.apt === extendedCodec.remotePayloadType));
        if (matchingLocalRtxCodec && matchingRemoteRtxCodec) {
            extendedCodec.localRtxPayloadType = matchingLocalRtxCodec.preferredPayloadType;
            extendedCodec.remoteRtxPayloadType = matchingRemoteRtxCodec.preferredPayloadType;
        }
    }
    // Match header extensions.
    for (const remoteExt of remoteCaps.headerExtensions) {
        const matchingLocalExt = localCaps.headerExtensions
            .find((localExt) => matchHeaderExtensions(localExt, remoteExt));
        if (!matchingLocalExt)
            continue;
        // TODO: Must do stuff for encrypted extensions.
        const extendedExt = {
            kind: remoteExt.kind,
            uri: remoteExt.uri,
            sendId: matchingLocalExt.preferredId,
            recvId: remoteExt.preferredId,
            encrypt: matchingLocalExt.preferredEncrypt,
            direction: 'sendrecv'
        };
        switch (remoteExt.direction) {
            case 'sendrecv':
                extendedExt.direction = 'sendrecv';
                break;
            case 'recvonly':
                extendedExt.direction = 'sendonly';
                break;
            case 'sendonly':
                extendedExt.direction = 'recvonly';
                break;
            case 'inactive':
                extendedExt.direction = 'inactive';
                break;
        }
        extendedRtpCapabilities.headerExtensions.push(extendedExt);
    }
    return extendedRtpCapabilities;
}
exports.getExtendedRtpCapabilities = getExtendedRtpCapabilities;
/**
 * Generate RTP capabilities for receiving media based on the given extended
 * RTP capabilities.
 */
function getRecvRtpCapabilities(extendedRtpCapabilities) {
    const rtpCapabilities = {
        codecs: [],
        headerExtensions: []
    };
    for (const extendedCodec of extendedRtpCapabilities.codecs) {
        const codec = {
            mimeType: extendedCodec.mimeType,
            kind: extendedCodec.kind,
            preferredPayloadType: extendedCodec.remotePayloadType,
            clockRate: extendedCodec.clockRate,
            channels: extendedCodec.channels,
            parameters: extendedCodec.localParameters,
            rtcpFeedback: extendedCodec.rtcpFeedback
        };
        rtpCapabilities.codecs.push(codec);
        // Add RTX codec.
        if (!extendedCodec.remoteRtxPayloadType)
            continue;
        const rtxCodec = {
            mimeType: `${extendedCodec.kind}/rtx`,
            kind: extendedCodec.kind,
            preferredPayloadType: extendedCodec.remoteRtxPayloadType,
            clockRate: extendedCodec.clockRate,
            channels: 1,
            parameters: {
                apt: extendedCodec.remotePayloadType
            },
            rtcpFeedback: []
        };
        rtpCapabilities.codecs.push(rtxCodec);
        // TODO: In the future, we need to add FEC, CN, etc, codecs.
    }
    for (const extendedExtension of extendedRtpCapabilities.headerExtensions) {
        // Ignore RTP extensions not valid for receiving.
        if (extendedExtension.direction !== 'sendrecv' &&
            extendedExtension.direction !== 'recvonly') {
            continue;
        }
        const ext = {
            kind: extendedExtension.kind,
            uri: extendedExtension.uri,
            preferredId: extendedExtension.recvId,
            preferredEncrypt: extendedExtension.encrypt,
            direction: extendedExtension.direction
        };
        rtpCapabilities.headerExtensions.push(ext);
    }
    return rtpCapabilities;
}
exports.getRecvRtpCapabilities = getRecvRtpCapabilities;
/**
 * Generate RTP parameters of the given kind for sending media.
 * Just the first media codec per kind is considered.
 * NOTE: mid, encodings and rtcp fields are left empty.
 */
function getSendingRtpParameters(kind, extendedRtpCapabilities) {
    const rtpParameters = {
        mid: undefined,
        codecs: [],
        headerExtensions: [],
        encodings: [],
        rtcp: {}
    };
    for (const extendedCodec of extendedRtpCapabilities.codecs) {
        if (extendedCodec.kind !== kind)
            continue;
        const codec = {
            mimeType: extendedCodec.mimeType,
            payloadType: extendedCodec.localPayloadType,
            clockRate: extendedCodec.clockRate,
            channels: extendedCodec.channels,
            parameters: extendedCodec.localParameters,
            rtcpFeedback: extendedCodec.rtcpFeedback
        };
        rtpParameters.codecs.push(codec);
        // Add RTX codec.
        if (extendedCodec.localRtxPayloadType) {
            const rtxCodec = {
                mimeType: `${extendedCodec.kind}/rtx`,
                payloadType: extendedCodec.localRtxPayloadType,
                clockRate: extendedCodec.clockRate,
                channels: 1,
                parameters: {
                    apt: extendedCodec.localPayloadType
                },
                rtcpFeedback: []
            };
            rtpParameters.codecs.push(rtxCodec);
        }
        // NOTE: We assume a single media codec plus an optional RTX codec.
        break;
    }
    for (const extendedExtension of extendedRtpCapabilities.headerExtensions) {
        // Ignore RTP extensions of a different kind and those not valid for sending.
        if ((extendedExtension.kind && extendedExtension.kind !== kind) ||
            (extendedExtension.direction !== 'sendrecv' &&
                extendedExtension.direction !== 'sendonly')) {
            continue;
        }
        const ext = {
            uri: extendedExtension.uri,
            id: extendedExtension.sendId,
            encrypt: extendedExtension.encrypt,
            parameters: {}
        };
        rtpParameters.headerExtensions.push(ext);
    }
    return rtpParameters;
}
exports.getSendingRtpParameters = getSendingRtpParameters;
/**
 * Generate RTP parameters of the given kind suitable for the remote SDP answer.
 */
function getSendingRemoteRtpParameters(kind, extendedRtpCapabilities) {
    const rtpParameters = {
        mid: undefined,
        codecs: [],
        headerExtensions: [],
        encodings: [],
        rtcp: {}
    };
    for (const extendedCodec of extendedRtpCapabilities.codecs) {
        if (extendedCodec.kind !== kind)
            continue;
        const codec = {
            mimeType: extendedCodec.mimeType,
            payloadType: extendedCodec.localPayloadType,
            clockRate: extendedCodec.clockRate,
            channels: extendedCodec.channels,
            parameters: extendedCodec.remoteParameters,
            rtcpFeedback: extendedCodec.rtcpFeedback
        };
        rtpParameters.codecs.push(codec);
        // Add RTX codec.
        if (extendedCodec.localRtxPayloadType) {
            const rtxCodec = {
                mimeType: `${extendedCodec.kind}/rtx`,
                payloadType: extendedCodec.localRtxPayloadType,
                clockRate: extendedCodec.clockRate,
                channels: 1,
                parameters: {
                    apt: extendedCodec.localPayloadType
                },
                rtcpFeedback: []
            };
            rtpParameters.codecs.push(rtxCodec);
        }
        // NOTE: We assume a single media codec plus an optional RTX codec.
        break;
    }
    for (const extendedExtension of extendedRtpCapabilities.headerExtensions) {
        // Ignore RTP extensions of a different kind and those not valid for sending.
        if ((extendedExtension.kind && extendedExtension.kind !== kind) ||
            (extendedExtension.direction !== 'sendrecv' &&
                extendedExtension.direction !== 'sendonly')) {
            continue;
        }
        const ext = {
            uri: extendedExtension.uri,
            id: extendedExtension.sendId,
            encrypt: extendedExtension.encrypt,
            parameters: {}
        };
        rtpParameters.headerExtensions.push(ext);
    }
    // Reduce codecs' RTCP feedback. Use Transport-CC if available, REMB otherwise.
    if (rtpParameters.headerExtensions.some((ext) => (ext.uri === 'http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01'))) {
        for (const codec of rtpParameters.codecs) {
            codec.rtcpFeedback = (codec.rtcpFeedback || [])
                .filter((fb) => fb.type !== 'goog-remb');
        }
    }
    else if (rtpParameters.headerExtensions.some((ext) => (ext.uri === 'http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time'))) {
        for (const codec of rtpParameters.codecs) {
            codec.rtcpFeedback = (codec.rtcpFeedback || [])
                .filter((fb) => fb.type !== 'transport-cc');
        }
    }
    else {
        for (const codec of rtpParameters.codecs) {
            codec.rtcpFeedback = (codec.rtcpFeedback || [])
                .filter((fb) => (fb.type !== 'transport-cc' &&
                fb.type !== 'goog-remb'));
        }
    }
    return rtpParameters;
}
exports.getSendingRemoteRtpParameters = getSendingRemoteRtpParameters;
/**
 * Create RTP parameters for a Consumer for the RTP probator.
 */
function generateProbatorRtpParameters(videoRtpParameters) {
    // This may throw.
    validateRtpParameters(videoRtpParameters);
    const rtpParameters = {
        mid: undefined,
        codecs: [],
        headerExtensions: [],
        encodings: [],
        rtcp: {
            cname: 'probator'
        }
    };
    rtpParameters.codecs.push(videoRtpParameters.codecs[0]);
    rtpParameters.headerExtensions = videoRtpParameters.headerExtensions
        .filter((ext) => (ext.uri === 'http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time' ||
        ext.uri === 'http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01'));
    rtpParameters.encodings.push({ ssrc: PROBATOR_SSRC });
    return rtpParameters;
}
exports.generateProbatorRtpParameters = generateProbatorRtpParameters;
/**
 * Whether media can be sent based on the given RTP capabilities.
 */
function canSend(kind, extendedRtpCapabilities) {
    return extendedRtpCapabilities.codecs.
        some((codec) => codec.kind === kind);
}
exports.canSend = canSend;
/**
 * Whether the given RTP parameters can be received with the given RTP
 * capabilities.
 */
function canReceive(rtpParameters, extendedRtpCapabilities) {
    // This may throw.
    validateRtpParameters(rtpParameters);
    if (rtpParameters.codecs.length === 0)
        return false;
    const firstMediaCodec = rtpParameters.codecs[0];
    return extendedRtpCapabilities.codecs
        .some((codec) => codec.remotePayloadType === firstMediaCodec.payloadType);
}
exports.canReceive = canReceive;
function isRtxCodec(codec) {
    return /.+\/rtx$/i.test(codec.mimeType);
}
function matchCodecs(aCodec, bCodec, { strict = false, modify = false } = {}) {
    const aMimeType = aCodec.mimeType.toLowerCase();
    const bMimeType = bCodec.mimeType.toLowerCase();
    if (aMimeType !== bMimeType)
        return false;
    if (aCodec.clockRate !== bCodec.clockRate)
        return false;
    if (aCodec.channels !== bCodec.channels)
        return false;
    // Per codec special checks.
    switch (aMimeType) {
        case 'video/h264':
            {
                const aPacketizationMode = aCodec.parameters['packetization-mode'] || 0;
                const bPacketizationMode = bCodec.parameters['packetization-mode'] || 0;
                if (aPacketizationMode !== bPacketizationMode)
                    return false;
                // If strict matching check profile-level-id.
                if (strict) {
                    if (!h264.isSameProfile(aCodec.parameters, bCodec.parameters))
                        return false;
                    let selectedProfileLevelId;
                    try {
                        selectedProfileLevelId =
                            h264.generateProfileLevelIdForAnswer(aCodec.parameters, bCodec.parameters);
                    }
                    catch (error) {
                        return false;
                    }
                    if (modify) {
                        if (selectedProfileLevelId)
                            aCodec.parameters['profile-level-id'] = selectedProfileLevelId;
                        else
                            delete aCodec.parameters['profile-level-id'];
                    }
                }
                break;
            }
        case 'video/vp9':
            {
                // If strict matching check profile-id.
                if (strict) {
                    const aProfileId = aCodec.parameters['profile-id'] || 0;
                    const bProfileId = bCodec.parameters['profile-id'] || 0;
                    if (aProfileId !== bProfileId)
                        return false;
                }
                break;
            }
    }
    return true;
}
function matchHeaderExtensions(aExt, bExt) {
    if (aExt.kind && bExt.kind && aExt.kind !== bExt.kind)
        return false;
    if (aExt.uri !== bExt.uri)
        return false;
    return true;
}
function reduceRtcpFeedback(codecA, codecB) {
    const reducedRtcpFeedback = [];
    for (const aFb of codecA.rtcpFeedback || []) {
        const matchingBFb = (codecB.rtcpFeedback || [])
            .find((bFb) => (bFb.type === aFb.type &&
            (bFb.parameter === aFb.parameter || (!bFb.parameter && !aFb.parameter))));
        if (matchingBFb)
            reducedRtcpFeedback.push(matchingBFb);
    }
    return reducedRtcpFeedback;
}

},{"h264-profile-level-id":38}],66:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const ScalabilityModeRegex = new RegExp('^[LS]([1-9]\\d{0,1})T([1-9]\\d{0,1})');
function parse(scalabilityMode) {
    const match = ScalabilityModeRegex.exec(scalabilityMode);
    if (match) {
        return {
            spatialLayers: Number(match[1]),
            temporalLayers: Number(match[2])
        };
    }
    else {
        return {
            spatialLayers: 1,
            temporalLayers: 1
        };
    }
}
exports.parse = parse;

},{}],67:[function(require,module,exports){
"use strict";
function __export(m) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}
Object.defineProperty(exports, "__esModule", { value: true });
__export(require("./Device"));
__export(require("./Transport"));
__export(require("./Producer"));
__export(require("./Consumer"));
__export(require("./DataProducer"));
__export(require("./DataConsumer"));
__export(require("./handlers/HandlerInterface"));
__export(require("./errors"));

},{"./Consumer":39,"./DataConsumer":40,"./DataProducer":41,"./Device":42,"./Producer":45,"./Transport":46,"./errors":47,"./handlers/HandlerInterface":54}],68:[function(require,module,exports){
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * Clones the given object/array.
 *
 * @param {Object|Array} obj
 *
 * @returns {Object|Array}
 */
function clone(obj) {
    if (typeof obj !== 'object')
        return {};
    return JSON.parse(JSON.stringify(obj));
}
exports.clone = clone;
/**
 * Generates a random positive integer.
 */
function generateRandomNumber() {
    return Math.round(Math.random() * 10000000);
}
exports.generateRandomNumber = generateRandomNumber;

},{}],69:[function(require,module,exports){
// shim for using process in browser
var process = module.exports = {};

// cached from whatever global is present so that test runners that stub it
// don't break things.  But we need to wrap it in a try catch in case it is
// wrapped in strict mode code which doesn't define any globals.  It's inside a
// function because try/catches deoptimize in certain engines.

var cachedSetTimeout;
var cachedClearTimeout;

function defaultSetTimout() {
    throw new Error('setTimeout has not been defined');
}
function defaultClearTimeout () {
    throw new Error('clearTimeout has not been defined');
}
(function () {
    try {
        if (typeof setTimeout === 'function') {
            cachedSetTimeout = setTimeout;
        } else {
            cachedSetTimeout = defaultSetTimout;
        }
    } catch (e) {
        cachedSetTimeout = defaultSetTimout;
    }
    try {
        if (typeof clearTimeout === 'function') {
            cachedClearTimeout = clearTimeout;
        } else {
            cachedClearTimeout = defaultClearTimeout;
        }
    } catch (e) {
        cachedClearTimeout = defaultClearTimeout;
    }
} ());
function runTimeout(fun) {
    if (cachedSetTimeout === setTimeout) {
        //normal enviroments in sane situations
        return setTimeout(fun, 0);
    }
    // if setTimeout wasn't available but was latter defined
    if ((cachedSetTimeout === defaultSetTimout || !cachedSetTimeout) && setTimeout) {
        cachedSetTimeout = setTimeout;
        return setTimeout(fun, 0);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedSetTimeout(fun, 0);
    } catch(e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't trust the global object when called normally
            return cachedSetTimeout.call(null, fun, 0);
        } catch(e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error
            return cachedSetTimeout.call(this, fun, 0);
        }
    }


}
function runClearTimeout(marker) {
    if (cachedClearTimeout === clearTimeout) {
        //normal enviroments in sane situations
        return clearTimeout(marker);
    }
    // if clearTimeout wasn't available but was latter defined
    if ((cachedClearTimeout === defaultClearTimeout || !cachedClearTimeout) && clearTimeout) {
        cachedClearTimeout = clearTimeout;
        return clearTimeout(marker);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedClearTimeout(marker);
    } catch (e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't  trust the global object when called normally
            return cachedClearTimeout.call(null, marker);
        } catch (e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error.
            // Some versions of I.E. have different rules for clearTimeout vs setTimeout
            return cachedClearTimeout.call(this, marker);
        }
    }



}
var queue = [];
var draining = false;
var currentQueue;
var queueIndex = -1;

function cleanUpNextTick() {
    if (!draining || !currentQueue) {
        return;
    }
    draining = false;
    if (currentQueue.length) {
        queue = currentQueue.concat(queue);
    } else {
        queueIndex = -1;
    }
    if (queue.length) {
        drainQueue();
    }
}

function drainQueue() {
    if (draining) {
        return;
    }
    var timeout = runTimeout(cleanUpNextTick);
    draining = true;

    var len = queue.length;
    while(len) {
        currentQueue = queue;
        queue = [];
        while (++queueIndex < len) {
            if (currentQueue) {
                currentQueue[queueIndex].run();
            }
        }
        queueIndex = -1;
        len = queue.length;
    }
    currentQueue = null;
    draining = false;
    runClearTimeout(timeout);
}

process.nextTick = function (fun) {
    var args = new Array(arguments.length - 1);
    if (arguments.length > 1) {
        for (var i = 1; i < arguments.length; i++) {
            args[i - 1] = arguments[i];
        }
    }
    queue.push(new Item(fun, args));
    if (queue.length === 1 && !draining) {
        runTimeout(drainQueue);
    }
};

// v8 likes predictible objects
function Item(fun, array) {
    this.fun = fun;
    this.array = array;
}
Item.prototype.run = function () {
    this.fun.apply(null, this.array);
};
process.title = 'browser';
process.browser = true;
process.env = {};
process.argv = [];
process.version = ''; // empty string to avoid regexp issues
process.versions = {};

function noop() {}

process.on = noop;
process.addListener = noop;
process.once = noop;
process.off = noop;
process.removeListener = noop;
process.removeAllListeners = noop;
process.emit = noop;
process.prependListener = noop;
process.prependOnceListener = noop;

process.listeners = function (name) { return [] };

process.binding = function (name) {
    throw new Error('process.binding is not supported');
};

process.cwd = function () { return '/' };
process.chdir = function (dir) {
    throw new Error('process.chdir is not supported');
};
process.umask = function() { return 0; };

},{}],70:[function(require,module,exports){
var grammar = module.exports = {
  v: [{
    name: 'version',
    reg: /^(\d*)$/
  }],
  o: [{
    // o=- 20518 0 IN IP4 203.0.113.1
    // NB: sessionId will be a String in most cases because it is huge
    name: 'origin',
    reg: /^(\S*) (\d*) (\d*) (\S*) IP(\d) (\S*)/,
    names: ['username', 'sessionId', 'sessionVersion', 'netType', 'ipVer', 'address'],
    format: '%s %s %d %s IP%d %s'
  }],
  // default parsing of these only (though some of these feel outdated)
  s: [{ name: 'name' }],
  i: [{ name: 'description' }],
  u: [{ name: 'uri' }],
  e: [{ name: 'email' }],
  p: [{ name: 'phone' }],
  z: [{ name: 'timezones' }], // TODO: this one can actually be parsed properly...
  r: [{ name: 'repeats' }],   // TODO: this one can also be parsed properly
  // k: [{}], // outdated thing ignored
  t: [{
    // t=0 0
    name: 'timing',
    reg: /^(\d*) (\d*)/,
    names: ['start', 'stop'],
    format: '%d %d'
  }],
  c: [{
    // c=IN IP4 10.47.197.26
    name: 'connection',
    reg: /^IN IP(\d) (\S*)/,
    names: ['version', 'ip'],
    format: 'IN IP%d %s'
  }],
  b: [{
    // b=AS:4000
    push: 'bandwidth',
    reg: /^(TIAS|AS|CT|RR|RS):(\d*)/,
    names: ['type', 'limit'],
    format: '%s:%s'
  }],
  m: [{
    // m=video 51744 RTP/AVP 126 97 98 34 31
    // NB: special - pushes to session
    // TODO: rtp/fmtp should be filtered by the payloads found here?
    reg: /^(\w*) (\d*) ([\w/]*)(?: (.*))?/,
    names: ['type', 'port', 'protocol', 'payloads'],
    format: '%s %d %s %s'
  }],
  a: [
    {
      // a=rtpmap:110 opus/48000/2
      push: 'rtp',
      reg: /^rtpmap:(\d*) ([\w\-.]*)(?:\s*\/(\d*)(?:\s*\/(\S*))?)?/,
      names: ['payload', 'codec', 'rate', 'encoding'],
      format: function (o) {
        return (o.encoding)
          ? 'rtpmap:%d %s/%s/%s'
          : o.rate
            ? 'rtpmap:%d %s/%s'
            : 'rtpmap:%d %s';
      }
    },
    {
      // a=fmtp:108 profile-level-id=24;object=23;bitrate=64000
      // a=fmtp:111 minptime=10; useinbandfec=1
      push: 'fmtp',
      reg: /^fmtp:(\d*) ([\S| ]*)/,
      names: ['payload', 'config'],
      format: 'fmtp:%d %s'
    },
    {
      // a=control:streamid=0
      name: 'control',
      reg: /^control:(.*)/,
      format: 'control:%s'
    },
    {
      // a=rtcp:65179 IN IP4 193.84.77.194
      name: 'rtcp',
      reg: /^rtcp:(\d*)(?: (\S*) IP(\d) (\S*))?/,
      names: ['port', 'netType', 'ipVer', 'address'],
      format: function (o) {
        return (o.address != null)
          ? 'rtcp:%d %s IP%d %s'
          : 'rtcp:%d';
      }
    },
    {
      // a=rtcp-fb:98 trr-int 100
      push: 'rtcpFbTrrInt',
      reg: /^rtcp-fb:(\*|\d*) trr-int (\d*)/,
      names: ['payload', 'value'],
      format: 'rtcp-fb:%d trr-int %d'
    },
    {
      // a=rtcp-fb:98 nack rpsi
      push: 'rtcpFb',
      reg: /^rtcp-fb:(\*|\d*) ([\w-_]*)(?: ([\w-_]*))?/,
      names: ['payload', 'type', 'subtype'],
      format: function (o) {
        return (o.subtype != null)
          ? 'rtcp-fb:%s %s %s'
          : 'rtcp-fb:%s %s';
      }
    },
    {
      // a=extmap:2 urn:ietf:params:rtp-hdrext:toffset
      // a=extmap:1/recvonly URI-gps-string
      // a=extmap:3 urn:ietf:params:rtp-hdrext:encrypt urn:ietf:params:rtp-hdrext:smpte-tc 25@600/24
      push: 'ext',
      reg: /^extmap:(\d+)(?:\/(\w+))?(?: (urn:ietf:params:rtp-hdrext:encrypt))? (\S*)(?: (\S*))?/,
      names: ['value', 'direction', 'encrypt-uri', 'uri', 'config'],
      format: function (o) {
        return (
          'extmap:%d' +
          (o.direction ? '/%s' : '%v') +
          (o['encrypt-uri'] ? ' %s' : '%v') +
          ' %s' +
          (o.config ? ' %s' : '')
        );
      }
    },
    {
      // a=extmap-allow-mixed
      name: 'extmapAllowMixed',
      reg: /^(extmap-allow-mixed)/
    },
    {
      // a=crypto:1 AES_CM_128_HMAC_SHA1_80 inline:PS1uQCVeeCFCanVmcjkpPywjNWhcYD0mXXtxaVBR|2^20|1:32
      push: 'crypto',
      reg: /^crypto:(\d*) ([\w_]*) (\S*)(?: (\S*))?/,
      names: ['id', 'suite', 'config', 'sessionConfig'],
      format: function (o) {
        return (o.sessionConfig != null)
          ? 'crypto:%d %s %s %s'
          : 'crypto:%d %s %s';
      }
    },
    {
      // a=setup:actpass
      name: 'setup',
      reg: /^setup:(\w*)/,
      format: 'setup:%s'
    },
    {
      // a=connection:new
      name: 'connectionType',
      reg: /^connection:(new|existing)/,
      format: 'connection:%s'
    },
    {
      // a=mid:1
      name: 'mid',
      reg: /^mid:([^\s]*)/,
      format: 'mid:%s'
    },
    {
      // a=msid:0c8b064d-d807-43b4-b434-f92a889d8587 98178685-d409-46e0-8e16-7ef0db0db64a
      name: 'msid',
      reg: /^msid:(.*)/,
      format: 'msid:%s'
    },
    {
      // a=ptime:20
      name: 'ptime',
      reg: /^ptime:(\d*(?:\.\d*)*)/,
      format: 'ptime:%d'
    },
    {
      // a=maxptime:60
      name: 'maxptime',
      reg: /^maxptime:(\d*(?:\.\d*)*)/,
      format: 'maxptime:%d'
    },
    {
      // a=sendrecv
      name: 'direction',
      reg: /^(sendrecv|recvonly|sendonly|inactive)/
    },
    {
      // a=ice-lite
      name: 'icelite',
      reg: /^(ice-lite)/
    },
    {
      // a=ice-ufrag:F7gI
      name: 'iceUfrag',
      reg: /^ice-ufrag:(\S*)/,
      format: 'ice-ufrag:%s'
    },
    {
      // a=ice-pwd:x9cml/YzichV2+XlhiMu8g
      name: 'icePwd',
      reg: /^ice-pwd:(\S*)/,
      format: 'ice-pwd:%s'
    },
    {
      // a=fingerprint:SHA-1 00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33
      name: 'fingerprint',
      reg: /^fingerprint:(\S*) (\S*)/,
      names: ['type', 'hash'],
      format: 'fingerprint:%s %s'
    },
    {
      // a=candidate:0 1 UDP 2113667327 203.0.113.1 54400 typ host
      // a=candidate:1162875081 1 udp 2113937151 192.168.34.75 60017 typ host generation 0 network-id 3 network-cost 10
      // a=candidate:3289912957 2 udp 1845501695 193.84.77.194 60017 typ srflx raddr 192.168.34.75 rport 60017 generation 0 network-id 3 network-cost 10
      // a=candidate:229815620 1 tcp 1518280447 192.168.150.19 60017 typ host tcptype active generation 0 network-id 3 network-cost 10
      // a=candidate:3289912957 2 tcp 1845501695 193.84.77.194 60017 typ srflx raddr 192.168.34.75 rport 60017 tcptype passive generation 0 network-id 3 network-cost 10
      push:'candidates',
      reg: /^candidate:(\S*) (\d*) (\S*) (\d*) (\S*) (\d*) typ (\S*)(?: raddr (\S*) rport (\d*))?(?: tcptype (\S*))?(?: generation (\d*))?(?: network-id (\d*))?(?: network-cost (\d*))?/,
      names: ['foundation', 'component', 'transport', 'priority', 'ip', 'port', 'type', 'raddr', 'rport', 'tcptype', 'generation', 'network-id', 'network-cost'],
      format: function (o) {
        var str = 'candidate:%s %d %s %d %s %d typ %s';

        str += (o.raddr != null) ? ' raddr %s rport %d' : '%v%v';

        // NB: candidate has three optional chunks, so %void middles one if it's missing
        str += (o.tcptype != null) ? ' tcptype %s' : '%v';

        if (o.generation != null) {
          str += ' generation %d';
        }

        str += (o['network-id'] != null) ? ' network-id %d' : '%v';
        str += (o['network-cost'] != null) ? ' network-cost %d' : '%v';
        return str;
      }
    },
    {
      // a=end-of-candidates (keep after the candidates line for readability)
      name: 'endOfCandidates',
      reg: /^(end-of-candidates)/
    },
    {
      // a=remote-candidates:1 203.0.113.1 54400 2 203.0.113.1 54401 ...
      name: 'remoteCandidates',
      reg: /^remote-candidates:(.*)/,
      format: 'remote-candidates:%s'
    },
    {
      // a=ice-options:google-ice
      name: 'iceOptions',
      reg: /^ice-options:(\S*)/,
      format: 'ice-options:%s'
    },
    {
      // a=ssrc:2566107569 cname:t9YU8M1UxTF8Y1A1
      push: 'ssrcs',
      reg: /^ssrc:(\d*) ([\w_-]*)(?::(.*))?/,
      names: ['id', 'attribute', 'value'],
      format: function (o) {
        var str = 'ssrc:%d';
        if (o.attribute != null) {
          str += ' %s';
          if (o.value != null) {
            str += ':%s';
          }
        }
        return str;
      }
    },
    {
      // a=ssrc-group:FEC 1 2
      // a=ssrc-group:FEC-FR 3004364195 1080772241
      push: 'ssrcGroups',
      // token-char = %x21 / %x23-27 / %x2A-2B / %x2D-2E / %x30-39 / %x41-5A / %x5E-7E
      reg: /^ssrc-group:([\x21\x23\x24\x25\x26\x27\x2A\x2B\x2D\x2E\w]*) (.*)/,
      names: ['semantics', 'ssrcs'],
      format: 'ssrc-group:%s %s'
    },
    {
      // a=msid-semantic: WMS Jvlam5X3SX1OP6pn20zWogvaKJz5Hjf9OnlV
      name: 'msidSemantic',
      reg: /^msid-semantic:\s?(\w*) (\S*)/,
      names: ['semantic', 'token'],
      format: 'msid-semantic: %s %s' // space after ':' is not accidental
    },
    {
      // a=group:BUNDLE audio video
      push: 'groups',
      reg: /^group:(\w*) (.*)/,
      names: ['type', 'mids'],
      format: 'group:%s %s'
    },
    {
      // a=rtcp-mux
      name: 'rtcpMux',
      reg: /^(rtcp-mux)/
    },
    {
      // a=rtcp-rsize
      name: 'rtcpRsize',
      reg: /^(rtcp-rsize)/
    },
    {
      // a=sctpmap:5000 webrtc-datachannel 1024
      name: 'sctpmap',
      reg: /^sctpmap:([\w_/]*) (\S*)(?: (\S*))?/,
      names: ['sctpmapNumber', 'app', 'maxMessageSize'],
      format: function (o) {
        return (o.maxMessageSize != null)
          ? 'sctpmap:%s %s %s'
          : 'sctpmap:%s %s';
      }
    },
    {
      // a=x-google-flag:conference
      name: 'xGoogleFlag',
      reg: /^x-google-flag:([^\s]*)/,
      format: 'x-google-flag:%s'
    },
    {
      // a=rid:1 send max-width=1280;max-height=720;max-fps=30;depend=0
      push: 'rids',
      reg: /^rid:([\d\w]+) (\w+)(?: ([\S| ]*))?/,
      names: ['id', 'direction', 'params'],
      format: function (o) {
        return (o.params) ? 'rid:%s %s %s' : 'rid:%s %s';
      }
    },
    {
      // a=imageattr:97 send [x=800,y=640,sar=1.1,q=0.6] [x=480,y=320] recv [x=330,y=250]
      // a=imageattr:* send [x=800,y=640] recv *
      // a=imageattr:100 recv [x=320,y=240]
      push: 'imageattrs',
      reg: new RegExp(
        // a=imageattr:97
        '^imageattr:(\\d+|\\*)' +
        // send [x=800,y=640,sar=1.1,q=0.6] [x=480,y=320]
        '[\\s\\t]+(send|recv)[\\s\\t]+(\\*|\\[\\S+\\](?:[\\s\\t]+\\[\\S+\\])*)' +
        // recv [x=330,y=250]
        '(?:[\\s\\t]+(recv|send)[\\s\\t]+(\\*|\\[\\S+\\](?:[\\s\\t]+\\[\\S+\\])*))?'
      ),
      names: ['pt', 'dir1', 'attrs1', 'dir2', 'attrs2'],
      format: function (o) {
        return 'imageattr:%s %s %s' + (o.dir2 ? ' %s %s' : '');
      }
    },
    {
      // a=simulcast:send 1,2,3;~4,~5 recv 6;~7,~8
      // a=simulcast:recv 1;4,5 send 6;7
      name: 'simulcast',
      reg: new RegExp(
        // a=simulcast:
        '^simulcast:' +
        // send 1,2,3;~4,~5
        '(send|recv) ([a-zA-Z0-9\\-_~;,]+)' +
        // space + recv 6;~7,~8
        '(?:\\s?(send|recv) ([a-zA-Z0-9\\-_~;,]+))?' +
        // end
        '$'
      ),
      names: ['dir1', 'list1', 'dir2', 'list2'],
      format: function (o) {
        return 'simulcast:%s %s' + (o.dir2 ? ' %s %s' : '');
      }
    },
    {
      // old simulcast draft 03 (implemented by Firefox)
      //   https://tools.ietf.org/html/draft-ietf-mmusic-sdp-simulcast-03
      // a=simulcast: recv pt=97;98 send pt=97
      // a=simulcast: send rid=5;6;7 paused=6,7
      name: 'simulcast_03',
      reg: /^simulcast:[\s\t]+([\S+\s\t]+)$/,
      names: ['value'],
      format: 'simulcast: %s'
    },
    {
      // a=framerate:25
      // a=framerate:29.97
      name: 'framerate',
      reg: /^framerate:(\d+(?:$|\.\d+))/,
      format: 'framerate:%s'
    },
    {
      // RFC4570
      // a=source-filter: incl IN IP4 239.5.2.31 10.1.15.5
      name: 'sourceFilter',
      reg: /^source-filter: *(excl|incl) (\S*) (IP4|IP6|\*) (\S*) (.*)/,
      names: ['filterMode', 'netType', 'addressTypes', 'destAddress', 'srcList'],
      format: 'source-filter: %s %s %s %s %s'
    },
    {
      // a=bundle-only
      name: 'bundleOnly',
      reg: /^(bundle-only)/
    },
    {
      // a=label:1
      name: 'label',
      reg: /^label:(.+)/,
      format: 'label:%s'
    },
    {
      // RFC version 26 for SCTP over DTLS
      // https://tools.ietf.org/html/draft-ietf-mmusic-sctp-sdp-26#section-5
      name: 'sctpPort',
      reg: /^sctp-port:(\d+)$/,
      format: 'sctp-port:%s'
    },
    {
      // RFC version 26 for SCTP over DTLS
      // https://tools.ietf.org/html/draft-ietf-mmusic-sctp-sdp-26#section-6
      name: 'maxMessageSize',
      reg: /^max-message-size:(\d+)$/,
      format: 'max-message-size:%s'
    },
    {
      // RFC7273
      // a=ts-refclk:ptp=IEEE1588-2008:39-A7-94-FF-FE-07-CB-D0:37
      push:'tsRefClocks',
      reg: /^ts-refclk:([^\s=]*)(?:=(\S*))?/,
      names: ['clksrc', 'clksrcExt'],
      format: function (o) {
        return 'ts-refclk:%s' + (o.clksrcExt != null ? '=%s' : '');
      }
    },
    {
      // RFC7273
      // a=mediaclk:direct=963214424
      name:'mediaClk',
      reg: /^mediaclk:(?:id=(\S*))? *([^\s=]*)(?:=(\S*))?(?: *rate=(\d+)\/(\d+))?/,
      names: ['id', 'mediaClockName', 'mediaClockValue', 'rateNumerator', 'rateDenominator'],
      format: function (o) {
        var str = 'mediaclk:';
        str += (o.id != null ? 'id=%s %s' : '%v%s');
        str += (o.mediaClockValue != null ? '=%s' : '');
        str += (o.rateNumerator != null ? ' rate=%s' : '');
        str += (o.rateDenominator != null ? '/%s' : '');
        return str;
      }
    },
    {
      // a=keywds:keywords
      name: 'keywords',
      reg: /^keywds:(.+)$/,
      format: 'keywds:%s'
    },
    {
      // a=content:main
      name: 'content',
      reg: /^content:(.+)/,
      format: 'content:%s'
    },
    // BFCP https://tools.ietf.org/html/rfc4583
    {
      // a=floorctrl:c-s
      name: 'bfcpFloorCtrl',
      reg: /^floorctrl:(c-only|s-only|c-s)/,
      format: 'floorctrl:%s'
    },
    {
      // a=confid:1
      name: 'bfcpConfId',
      reg: /^confid:(\d+)/,
      format: 'confid:%s'
    },
    {
      // a=userid:1
      name: 'bfcpUserId',
      reg: /^userid:(\d+)/,
      format: 'userid:%s'
    },
    {
      // a=floorid:1
      name: 'bfcpFloorId',
      reg: /^floorid:(.+) (?:m-stream|mstrm):(.+)/,
      names: ['id', 'mStream'],
      format: 'floorid:%s mstrm:%s'
    },
    {
      // any a= that we don't understand is kept verbatim on media.invalid
      push: 'invalid',
      names: ['value']
    }
  ]
};

// set sensible defaults to avoid polluting the grammar with boring details
Object.keys(grammar).forEach(function (key) {
  var objs = grammar[key];
  objs.forEach(function (obj) {
    if (!obj.reg) {
      obj.reg = /(.*)/;
    }
    if (!obj.format) {
      obj.format = '%s';
    }
  });
});

},{}],71:[function(require,module,exports){
var parser = require('./parser');
var writer = require('./writer');

exports.write = writer;
exports.parse = parser.parse;
exports.parseParams = parser.parseParams;
exports.parseFmtpConfig = parser.parseFmtpConfig; // Alias of parseParams().
exports.parsePayloads = parser.parsePayloads;
exports.parseRemoteCandidates = parser.parseRemoteCandidates;
exports.parseImageAttributes = parser.parseImageAttributes;
exports.parseSimulcastStreamList = parser.parseSimulcastStreamList;

},{"./parser":72,"./writer":73}],72:[function(require,module,exports){
var toIntIfInt = function (v) {
  return String(Number(v)) === v ? Number(v) : v;
};

var attachProperties = function (match, location, names, rawName) {
  if (rawName && !names) {
    location[rawName] = toIntIfInt(match[1]);
  }
  else {
    for (var i = 0; i < names.length; i += 1) {
      if (match[i+1] != null) {
        location[names[i]] = toIntIfInt(match[i+1]);
      }
    }
  }
};

var parseReg = function (obj, location, content) {
  var needsBlank = obj.name && obj.names;
  if (obj.push && !location[obj.push]) {
    location[obj.push] = [];
  }
  else if (needsBlank && !location[obj.name]) {
    location[obj.name] = {};
  }
  var keyLocation = obj.push ?
    {} :  // blank object that will be pushed
    needsBlank ? location[obj.name] : location; // otherwise, named location or root

  attachProperties(content.match(obj.reg), keyLocation, obj.names, obj.name);

  if (obj.push) {
    location[obj.push].push(keyLocation);
  }
};

var grammar = require('./grammar');
var validLine = RegExp.prototype.test.bind(/^([a-z])=(.*)/);

exports.parse = function (sdp) {
  var session = {}
    , media = []
    , location = session; // points at where properties go under (one of the above)

  // parse lines we understand
  sdp.split(/(\r\n|\r|\n)/).filter(validLine).forEach(function (l) {
    var type = l[0];
    var content = l.slice(2);
    if (type === 'm') {
      media.push({rtp: [], fmtp: []});
      location = media[media.length-1]; // point at latest media line
    }

    for (var j = 0; j < (grammar[type] || []).length; j += 1) {
      var obj = grammar[type][j];
      if (obj.reg.test(content)) {
        return parseReg(obj, location, content);
      }
    }
  });

  session.media = media; // link it up
  return session;
};

var paramReducer = function (acc, expr) {
  var s = expr.split(/=(.+)/, 2);
  if (s.length === 2) {
    acc[s[0]] = toIntIfInt(s[1]);
  } else if (s.length === 1 && expr.length > 1) {
    acc[s[0]] = undefined;
  }
  return acc;
};

exports.parseParams = function (str) {
  return str.split(/;\s?/).reduce(paramReducer, {});
};

// For backward compatibility - alias will be removed in 3.0.0
exports.parseFmtpConfig = exports.parseParams;

exports.parsePayloads = function (str) {
  return str.toString().split(' ').map(Number);
};

exports.parseRemoteCandidates = function (str) {
  var candidates = [];
  var parts = str.split(' ').map(toIntIfInt);
  for (var i = 0; i < parts.length; i += 3) {
    candidates.push({
      component: parts[i],
      ip: parts[i + 1],
      port: parts[i + 2]
    });
  }
  return candidates;
};

exports.parseImageAttributes = function (str) {
  return str.split(' ').map(function (item) {
    return item.substring(1, item.length-1).split(',').reduce(paramReducer, {});
  });
};

exports.parseSimulcastStreamList = function (str) {
  return str.split(';').map(function (stream) {
    return stream.split(',').map(function (format) {
      var scid, paused = false;

      if (format[0] !== '~') {
        scid = toIntIfInt(format);
      } else {
        scid = toIntIfInt(format.substring(1, format.length));
        paused = true;
      }

      return {
        scid: scid,
        paused: paused
      };
    });
  });
};

},{"./grammar":70}],73:[function(require,module,exports){
var grammar = require('./grammar');

// customized util.format - discards excess arguments and can void middle ones
var formatRegExp = /%[sdv%]/g;
var format = function (formatStr) {
  var i = 1;
  var args = arguments;
  var len = args.length;
  return formatStr.replace(formatRegExp, function (x) {
    if (i >= len) {
      return x; // missing argument
    }
    var arg = args[i];
    i += 1;
    switch (x) {
    case '%%':
      return '%';
    case '%s':
      return String(arg);
    case '%d':
      return Number(arg);
    case '%v':
      return '';
    }
  });
  // NB: we discard excess arguments - they are typically undefined from makeLine
};

var makeLine = function (type, obj, location) {
  var str = obj.format instanceof Function ?
    (obj.format(obj.push ? location : location[obj.name])) :
    obj.format;

  var args = [type + '=' + str];
  if (obj.names) {
    for (var i = 0; i < obj.names.length; i += 1) {
      var n = obj.names[i];
      if (obj.name) {
        args.push(location[obj.name][n]);
      }
      else { // for mLine and push attributes
        args.push(location[obj.names[i]]);
      }
    }
  }
  else {
    args.push(location[obj.name]);
  }
  return format.apply(null, args);
};

// RFC specified order
// TODO: extend this with all the rest
var defaultOuterOrder = [
  'v', 'o', 's', 'i',
  'u', 'e', 'p', 'c',
  'b', 't', 'r', 'z', 'a'
];
var defaultInnerOrder = ['i', 'c', 'b', 'a'];


module.exports = function (session, opts) {
  opts = opts || {};
  // ensure certain properties exist
  if (session.version == null) {
    session.version = 0; // 'v=0' must be there (only defined version atm)
  }
  if (session.name == null) {
    session.name = ' '; // 's= ' must be there if no meaningful name set
  }
  session.media.forEach(function (mLine) {
    if (mLine.payloads == null) {
      mLine.payloads = '';
    }
  });

  var outerOrder = opts.outerOrder || defaultOuterOrder;
  var innerOrder = opts.innerOrder || defaultInnerOrder;
  var sdp = [];

  // loop through outerOrder for matching properties on session
  outerOrder.forEach(function (type) {
    grammar[type].forEach(function (obj) {
      if (obj.name in session && session[obj.name] != null) {
        sdp.push(makeLine(type, obj, session));
      }
      else if (obj.push in session && session[obj.push] != null) {
        session[obj.push].forEach(function (el) {
          sdp.push(makeLine(type, obj, el));
        });
      }
    });
  });

  // then for each media line, follow the innerOrder
  session.media.forEach(function (mLine) {
    sdp.push(makeLine('m', grammar.m[0], mLine));

    innerOrder.forEach(function (type) {
      grammar[type].forEach(function (obj) {
        if (obj.name in mLine && mLine[obj.name] != null) {
          sdp.push(makeLine(type, obj, mLine));
        }
        else if (obj.push in mLine && mLine[obj.push] != null) {
          mLine[obj.push].forEach(function (el) {
            sdp.push(makeLine(type, obj, el));
          });
        }
      });
    });
  });

  return sdp.join('\r\n') + '\r\n';
};

},{"./grammar":70}]},{},[3]);
