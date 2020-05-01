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
    ACTION["PIPE_TO_REMOTE_PRODUCER"] = "pipeToRemoteProducer";
    ACTION["PIPE_FROM_REMOTE_PRODUCER"] = "pipeFromRemoteProducer";
    ACTION["WORKER_LOAD"] = "workerLoad";
    ACTION["NUM_WORKERS"] = "numWorkers";
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
    ERROR[ERROR["INVALID_WORKER"] = 535] = "INVALID_WORKER";
})(ERROR = exports.ERROR || (exports.ERROR = {}));
var API_OPERATION;
(function (API_OPERATION) {
    API_OPERATION[API_OPERATION["SUBSCRIBE"] = 0] = "SUBSCRIBE";
    API_OPERATION[API_OPERATION["PUBLISH"] = 1] = "PUBLISH";
})(API_OPERATION = exports.API_OPERATION || (exports.API_OPERATION = {}));
//# sourceMappingURL=constants.js.map