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
    function MediasoupRestApi(url, token, log) {
        this.timeouts = [];
        this.url = url;
        this.token = token;
        this.log = log || console.log;
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
    MediasoupRestApi.prototype.workerLoad = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.WORKER_LOAD)];
                    case 1: return [2 /*return*/, (_a.sent())];
                }
            });
        });
    };
    MediasoupRestApi.prototype.numWorkers = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.NUM_WORKERS)];
                    case 1: return [2 /*return*/, (_a.sent())];
                }
            });
        });
    };
    MediasoupRestApi.prototype.pipeToRemoteProducer = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.PIPE_TO_REMOTE_PRODUCER, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.pipeFromRemoteProducer = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.PIPE_FROM_REMOTE_PRODUCER, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
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
    MediasoupRestApi.prototype.recordedStreams = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.RECORDED_STREAMS)];
                    case 1: return [2 /*return*/, _a.sent()];
                }
            });
        });
    };
    MediasoupRestApi.prototype.streamRecordings = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.STREAM_RECORDINGS, json)];
                    case 1: return [2 /*return*/, _a.sent()];
                }
            });
        });
    };
    MediasoupRestApi.prototype.deleteStreamRecordings = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.DELETE_STREAM_RECORDINGS, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.deleteRecording = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.DELETE_RECORDING, json)];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        });
    };
    MediasoupRestApi.prototype.pushToServerInputs = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.PUSH_TO_SERVER_INPUTS, json)];
                    case 1: return [2 /*return*/, _a.sent()];
                }
            });
        });
    };
    MediasoupRestApi.prototype.pullFromServerInputs = function (json) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.request(constants_1.ACTION.PULL_FROM_SERVER_INPUTS, json)];
                    case 1: return [2 /*return*/, _a.sent()];
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
                        this.log('sent message', action, JSON.stringify(json));
                        _a.label = 1;
                    case 1:
                        _a.trys.push([1, 3, , 8]);
                        return [4 /*yield*/, axios_1.default.post(this.url + "/" + constants_1.PATH.MEDIASOUP + "/" + action, json, {
                                headers: { 'Content-Type': 'application/json', "Authorization": "Bearer " + this.token },
                            })];
                    case 2:
                        data = (_a.sent()).data;
                        this.log('got message', action, JSON.stringify(data));
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
                    case 6: throw { errorId: e_1.response.status };
                    case 7: return [3 /*break*/, 8];
                    case 8: return [2 /*return*/];
                }
            });
        });
    };
    return MediasoupRestApi;
}());
exports.MediasoupRestApi = MediasoupRestApi;
//# sourceMappingURL=mediasoup-rest-api.js.map