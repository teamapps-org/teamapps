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
                        params = { track: track };
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
                                                    target.close();
                                                    target.emit('close');
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
//# sourceMappingURL=conference-api.js.map