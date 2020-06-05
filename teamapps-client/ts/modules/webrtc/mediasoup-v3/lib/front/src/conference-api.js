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
var debug_1 = require("debug");
var ConferenceApi = /** @class */ (function (_super) {
    __extends(ConferenceApi, _super);
    function ConferenceApi(configs) {
        var _this = _super.call(this) || this;
        _this.connectors = new Map();
        _this.layers = new Map();
        _this.timeouts = [];
        _this.configs = __assign({ url: location.protocol + "//" + location.host + "/0", kinds: ['video', 'audio'], maxIncomingBitrate: 0, timeout: {
                stats: 1000,
                transport: 3000,
                consumer: 5000
            }, retryConsumerTimeout: 1000 }, configs);
        _this.log = debug_1.debug("conference-api [" + _this.configs.stream + "]:");
        _this.api = new mediasoup_rest_api_1.MediasoupRestApi(_this.configs.url, _this.configs.token, _this.log);
        _this.device = new mediasoup_client_1.Device();
        return _this;
    }
    ConferenceApi.prototype.setPreferredLayers = function (layers) {
        return __awaiter(this, void 0, void 0, function () {
            var kind, consumer, e_1;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!(this.operation === constants_1.API_OPERATION.SUBSCRIBE)) return [3 /*break*/, 4];
                        kind = 'video';
                        this.layers.set(kind, layers);
                        consumer = this.connectors.get(kind);
                        if (!(consumer && consumer !== true)) return [3 /*break*/, 4];
                        _a.label = 1;
                    case 1:
                        _a.trys.push([1, 3, , 4]);
                        return [4 /*yield*/, this.api.setPreferredLayers({ consumerId: consumer.id, layers: layers })];
                    case 2:
                        _a.sent();
                        return [3 /*break*/, 4];
                    case 3:
                        e_1 = _a.sent();
                        return [3 /*break*/, 4];
                    case 4: return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.addTrack = function (track) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        this.log('addTrack', track);
                        if (!(this.operation === constants_1.API_OPERATION.PUBLISH && this.mediaStream)) return [3 /*break*/, 2];
                        this.mediaStream.addTrack(track);
                        this.emit("addtrack", new MediaStreamTrackEvent("addtrack", { track: track }));
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
            var producer;
            return __generator(this, function (_a) {
                this.log('removeTrack', track);
                if (this.operation === constants_1.API_OPERATION.PUBLISH && this.mediaStream) {
                    this.mediaStream.removeTrack(track);
                    this.emit("removetrack", new MediaStreamTrackEvent("removetrack", { track: track }));
                    producer = this.connectors.get(track.kind);
                    if (producer && producer !== true) {
                        producer.close();
                        producer.emit('close');
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
            var oldKinds, _i, oldKinds_1, kind, connector, promises, _a, kinds_1, kind;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        if (!(this.operation === constants_1.API_OPERATION.SUBSCRIBE)) return [3 /*break*/, 2];
                        this.log('updateKinds', kinds);
                        oldKinds = this.configs.kinds;
                        this.configs.kinds = kinds;
                        for (_i = 0, oldKinds_1 = oldKinds; _i < oldKinds_1.length; _i++) {
                            kind = oldKinds_1[_i];
                            if (!kinds.includes(kind)) {
                                connector = this.connectors.get(kind);
                                if (connector) {
                                    if (connector !== true) {
                                        connector.close();
                                        connector.emit('close');
                                    }
                                    else {
                                        this.connectors.delete(kind);
                                    }
                                }
                            }
                        }
                        promises = [];
                        for (_a = 0, kinds_1 = kinds; _a < kinds_1.length; _a++) {
                            kind = kinds_1[_a];
                            if (!this.connectors.get(kind)) {
                                promises.push(this.subscribeTrack(kind));
                            }
                        }
                        return [4 /*yield*/, Promise.all(promises)];
                    case 1:
                        _b.sent();
                        _b.label = 2;
                    case 2: return [2 /*return*/];
                }
            });
        });
    };
    ConferenceApi.prototype.init = function (operation) {
        return __awaiter(this, void 0, void 0, function () {
            var _a, routerRtpCapabilities, iceServers, simulcast, timeout;
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
                        _a = _b.sent(), routerRtpCapabilities = _a.routerRtpCapabilities, iceServers = _a.iceServers, simulcast = _a.simulcast, timeout = _a.timeout;
                        if (routerRtpCapabilities.headerExtensions) {
                            routerRtpCapabilities.headerExtensions = routerRtpCapabilities.headerExtensions.
                                filter(function (ext) { return ext.uri !== 'urn:3gpp:video-orientation'; });
                        }
                        return [4 /*yield*/, this.device.load({ routerRtpCapabilities: routerRtpCapabilities })];
                    case 2:
                        _b.sent();
                        this.iceServers = iceServers;
                        this.simulcast = simulcast;
                        this.configs.timeout = __assign(__assign({}, this.configs.timeout), timeout);
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
            var api, onClose, consumer;
            var _this = this;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        api = this;
                        this.connectors.set(kind, true);
                        onClose = function () { return __awaiter(_this, void 0, void 0, function () {
                            var _consumer, e_2;
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (this.mediaStream) {
                                            consumer.track.stop();
                                            this.mediaStream.removeTrack(consumer.track);
                                            this.emit("removetrack", new MediaStreamTrackEvent("removetrack", { track: consumer.track }));
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
                                        e_2 = _a.sent();
                                        return [3 /*break*/, 4];
                                    case 4:
                                        if (!(_consumer && _consumer !== true && consumer.id === _consumer.id)) return [3 /*break*/, 6];
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
                        return [4 /*yield*/, this.consume(this.transport, this.configs.stream, kind)];
                    case 1:
                        consumer = _a.sent();
                        consumer.on('close', onClose);
                        if (!(this.connectors.get(kind) === true)) return [3 /*break*/, 3];
                        this.connectors.set(kind, consumer);
                        this.emit('newConsumerId', { id: consumer.id, kind: kind });
                        this.listenStats(consumer, 'inbound-rtp');
                        return [4 /*yield*/, api.api.resumeConsumer({ consumerId: consumer.id })];
                    case 2:
                        _a.sent();
                        if (this.mediaStream) {
                            this.mediaStream.addTrack(consumer.track);
                            this.emit("addtrack", new MediaStreamTrackEvent("addtrack", { track: consumer.track }));
                        }
                        return [3 /*break*/, 4];
                    case 3:
                        consumer.close();
                        consumer.emit('close');
                        _a.label = 4;
                    case 4: return [2 /*return*/];
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
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0: return [4 /*yield*/, this.removeTrack(track)];
                                    case 1:
                                        _a.sent();
                                        return [2 /*return*/];
                                }
                            });
                        }); });
                        params = { track: track, stopTracks: !!this.configs.stopTracks };
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
                        producer.on('close', function () { return __awaiter(_this, void 0, void 0, function () {
                            var producer, e_3;
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        producer = this.connectors.get(kind);
                                        if (!(producer && producer !== true)) return [3 /*break*/, 4];
                                        this.connectors.delete(kind);
                                        _a.label = 1;
                                    case 1:
                                        _a.trys.push([1, 3, , 4]);
                                        return [4 /*yield*/, this.api.closeProducer({ producerId: producer.id })];
                                    case 2:
                                        _a.sent();
                                        return [3 /*break*/, 4];
                                    case 3:
                                        e_3 = _a.sent();
                                        return [3 /*break*/, 4];
                                    case 4: return [2 /*return*/];
                                }
                            });
                        }); });
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
            var rtpCapabilities, consumeData, data, layers, e_4, e_5, timeout_1;
            var _this = this;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        rtpCapabilities = this.device.rtpCapabilities;
                        _a.label = 1;
                    case 1:
                        _a.trys.push([1, 7, , 11]);
                        consumeData = { rtpCapabilities: rtpCapabilities, stream: stream, kind: _kind, transportId: transport.id };
                        if (this.configs.origin && this.configs.url !== this.configs.origin.url) {
                            consumeData.origin = ConferenceApi.originOptions(this.configs.url, this.configs.token, this.configs.origin);
                        }
                        return [4 /*yield*/, this.api.consume(consumeData)];
                    case 2:
                        data = _a.sent();
                        layers = this.layers.get(_kind);
                        if (!layers) return [3 /*break*/, 6];
                        _a.label = 3;
                    case 3:
                        _a.trys.push([3, 5, , 6]);
                        return [4 /*yield*/, this.api.setPreferredLayers({ consumerId: data.id, layers: layers })];
                    case 4:
                        _a.sent();
                        return [3 /*break*/, 6];
                    case 5:
                        e_4 = _a.sent();
                        return [3 /*break*/, 6];
                    case 6: return [2 /*return*/, transport.consume(data)];
                    case 7:
                        e_5 = _a.sent();
                        if (!e_5) return [3 /*break*/, 10];
                        if (!(e_5.errorId === constants_1.ERROR.INVALID_STREAM)) return [3 /*break*/, 9];
                        return [4 /*yield*/, new Promise(function (resolve) {
                                timeout_1 = setTimeout(resolve, _this.configs.retryConsumerTimeout);
                                _this.timeouts.push(timeout_1);
                            })];
                    case 8:
                        _a.sent();
                        if (!this.timeouts.includes(timeout_1)) {
                            throw e_5;
                        }
                        return [2 /*return*/, this.consume(transport, stream, _kind)];
                    case 9:
                        if (e_5.errorId === constants_1.ERROR.INVALID_TRANSPORT) {
                            this.restartAll().then(function () { }).catch(function () { });
                        }
                        _a.label = 10;
                    case 10: throw e_5;
                    case 11: return [2 /*return*/];
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
                                if (i_1 === stats['size']) {
                                    if (alive_1) {
                                        deadTime = 0;
                                    }
                                    else {
                                        _this.emit('bitRate', { bitRate: 0, kind: target.kind });
                                        if (type === 'inbound-rtp') {
                                            deadTime++;
                                            if (deadTime > (_this.configs.timeout.consumer / _this.configs.timeout.stats)) {
                                                try {
                                                    _this.log('restart by no stats');
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
                            if (stats['size']) {
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
            var transportId, e_6, t;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!this.transport) return [3 /*break*/, 5];
                        if (!this.transport.closed && hard) {
                            this.transport.close();
                        }
                        transportId = this.transport.id;
                        delete this.transport;
                        _a.label = 1;
                    case 1:
                        _a.trys.push([1, 3, , 4]);
                        return [4 /*yield*/, this.api.closeTransport({ transportId: transportId })];
                    case 2:
                        _a.sent();
                        return [3 /*break*/, 4];
                    case 3:
                        e_6 = _a.sent();
                        return [3 /*break*/, 4];
                    case 4:
                        this.emit('connectionstatechange', { state: 'disconnected' });
                        _a.label = 5;
                    case 5:
                        if (hard && this.mediaStream && this.configs.stopTracks) {
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
                switch (_a.label) {
                    case 0:
                        if (!this.connectors.size) return [3 /*break*/, 2];
                        return [4 /*yield*/, new Promise(function (resolve) {
                                _this.connectors.forEach(function (connector, kind) {
                                    _this.connectors.delete(kind);
                                    try {
                                        if (connector && connector !== true) {
                                            connector.close();
                                            connector.emit('close');
                                        }
                                    }
                                    catch (e) { }
                                    if (!_this.connectors.size) {
                                        resolve();
                                    }
                                });
                            })];
                    case 1:
                        _a.sent();
                        _a.label = 2;
                    case 2: return [2 /*return*/];
                }
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
                                        }); }, this.configs.timeout.transport);
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
    ConferenceApi.originOptions = function (url, token, origin) {
        if (origin.token) {
            token = origin.token;
        }
        var data = {
            token: token,
            to: url,
            from: origin.url
        };
        if (origin.origin && origin.origin.url !== origin.url) {
            data.origin = ConferenceApi.originOptions(origin.url, token, origin.origin);
        }
        return data;
    };
    return ConferenceApi;
}(events_1.EventEmitter));
exports.ConferenceApi = ConferenceApi;
//# sourceMappingURL=conference-api.js.map
