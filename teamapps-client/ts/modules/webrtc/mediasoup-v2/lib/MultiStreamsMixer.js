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
var MultiStreamsMixer = /** @class */ (function () {
    function MultiStreamsMixer(inputMediaStreams, frameRate) {
        if (frameRate === void 0) { frameRate = 10; }
        this.isStopDrawingFrames = false;
        this.frameInterval = 1000 / frameRate;
        this.inputMediaStreams = inputMediaStreams;
        this.canvas = document.createElement('canvas');
        this.context = this.canvas.getContext('2d');
        this.canvas.style.position = 'absolute';
        this.canvas.style.left = '-100000px';
        this.canvas.style.top = '-100000px';
        (document.body || document.documentElement).appendChild(this.canvas);
    }
    MultiStreamsMixer.prototype.getMixedStream = function () {
        return __awaiter(this, void 0, void 0, function () {
            var mixedVideoStream_1, mixedAudioStream;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!(this.outputMediaStream == null)) return [3 /*break*/, 2];
                        if (!(this.inputMediaStreams.length > 0)) return [3 /*break*/, 2];
                        return [4 /*yield*/, this.getMixedVideoStream()];
                    case 1:
                        mixedVideoStream_1 = _a.sent();
                        mixedAudioStream = this.getMixedAudioStream();
                        if (mixedAudioStream) {
                            mixedAudioStream.getTracks().filter(function (t) {
                                return t.kind === 'audio';
                            }).forEach(function (track) {
                                mixedVideoStream_1.addTrack(track);
                            });
                        }
                        this.drawVideosToCanvas();
                        this.outputMediaStream = mixedVideoStream_1;
                        _a.label = 2;
                    case 2: return [2 /*return*/, this.outputMediaStream];
                }
            });
        });
    };
    MultiStreamsMixer.prototype.getInputMediaStreams = function () {
        return this.inputMediaStreams;
    };
    MultiStreamsMixer.prototype.close = function () {
        this.inputMediaStreams.forEach(function (ms) { return ms.mediaStream.getTracks().forEach(function (t) { return t.stop(); }); });
        this.canvas.remove();
    };
    ;
    MultiStreamsMixer.prototype.drawVideosToCanvas = function () {
        var _this = this;
        if (this.isStopDrawingFrames) {
            return;
        }
        var fullcanvasVideo = this.inputMediaStreams.filter(function (ims) { return ims.mixSizingInfo.fullcanvas && ims.mediaStream.getVideoTracks().length > 0; })[0];
        var remainingVideos = this.inputMediaStreams.filter(function (ims) { return ims !== fullcanvasVideo && ims.mediaStream.getVideoTracks().length > 0; });
        if (fullcanvasVideo && this.canvas) {
            this.canvas.width = fullcanvasVideo.mixSizingInfo.width || 0;
            this.canvas.height = fullcanvasVideo.mixSizingInfo.height || 0;
        }
        else if (remainingVideos.length) {
            var videosLength = this.inputMediaStreams.filter(function (ims) { return ims.video != null; }).length;
            this.canvas.width = videosLength > 1 ? (remainingVideos[0].mixSizingInfo.width || 0) * 2 : remainingVideos[0].mixSizingInfo.width || 0;
            var height = 1;
            if (videosLength === 3 || videosLength === 4) {
                height = 2;
            }
            if (videosLength === 5 || videosLength === 6) {
                height = 3;
            }
            if (videosLength === 7 || videosLength === 8) {
                height = 4;
            }
            if (videosLength === 9 || videosLength === 10) {
                height = 5;
            }
            this.canvas.height = (remainingVideos[0].mixSizingInfo.height || 0) * height;
        }
        else {
            this.canvas.width = 360;
            this.canvas.height = 240;
        }
        if (fullcanvasVideo && fullcanvasVideo.video instanceof HTMLVideoElement) {
            this.drawImage(fullcanvasVideo.video, fullcanvasVideo.mixSizingInfo);
        }
        remainingVideos.forEach(function (video) {
            if (video.video) {
                _this.drawImage(video.video, video.mixSizingInfo);
            }
        });
        setTimeout(function () { return _this.drawVideosToCanvas(); }, this.frameInterval);
    };
    MultiStreamsMixer.prototype.drawImage = function (video, mixSizingInfo) {
        if (this.isStopDrawingFrames) {
            return;
        }
        var x = 0;
        var y = 0;
        var width = video.width;
        var height = video.height;
        if (mixSizingInfo.left != null) {
            x = mixSizingInfo.left;
        }
        if (mixSizingInfo.top != null) {
            y = mixSizingInfo.top;
        }
        if (mixSizingInfo.width != null) {
            width = mixSizingInfo.width;
        }
        if (mixSizingInfo.height != null) {
            height = mixSizingInfo.height;
        }
        if (this.context) {
            this.context.drawImage(video, x, y, width, height);
            if (typeof mixSizingInfo.onRender === 'function') {
                mixSizingInfo.onRender(this.context, video, mixSizingInfo, x, y, width, height);
            }
        }
    };
    MultiStreamsMixer.prototype.getMixedVideoStream = function () {
        return __awaiter(this, void 0, void 0, function () {
            var videoStream, capturedStream;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.resetVideoStreams()];
                    case 1:
                        _a.sent();
                        videoStream = new MediaStream();
                        capturedStream = (this.canvas.captureStream && this.canvas.captureStream())
                            || (this.canvas.mozCaptureStream && this.canvas.mozCaptureStream());
                        capturedStream.getTracks().filter(function (t) {
                            return t.kind === 'video';
                        }).forEach(function (track) {
                            videoStream.addTrack(track);
                        });
                        this.canvas.stream = videoStream;
                        return [2 /*return*/, videoStream];
                }
            });
        });
    };
    MultiStreamsMixer.prototype.getMixedAudioStream = function () {
        var _this = this;
        this.audioContext = new (window.AudioContext || window.webkitAudioContext || window.mozAudioContext)();
        if (!this.audioContext) {
            return;
        }
        var audioSources = [];
        var audioTracksLength = 0;
        this.inputMediaStreams.forEach(function (stream) {
            if (!stream.mediaStream.getTracks().filter(function (t) {
                return t.kind === 'audio';
            }).length) {
                return;
            }
            audioTracksLength++;
            if (!_this.audioContext) {
                return;
            }
            var audioSource = _this.audioContext.createMediaStreamSource(stream.mediaStream);
            audioSources.push(audioSource);
        });
        if (!audioTracksLength) {
            return;
        }
        this.audioDestination = this.audioContext.createMediaStreamDestination();
        audioSources.forEach(function (audioSource) {
            if (!_this.audioDestination) {
                return;
            }
            audioSource.connect(_this.audioDestination);
        });
        return this.audioDestination.stream;
    };
    MultiStreamsMixer.createVideo = function (stream) {
        return __awaiter(this, void 0, void 0, function () {
            var video, size;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        video = document.createElement('video');
                        video.srcObject = stream.mediaStream;
                        video.muted = true;
                        video.volume = 0;
                        video.play();
                        if (!(stream.mixSizingInfo.width != null && stream.mixSizingInfo.height != null)) return [3 /*break*/, 1];
                        video.width = stream.mixSizingInfo.width;
                        video.height = stream.mixSizingInfo.height;
                        return [3 /*break*/, 3];
                    case 1: return [4 /*yield*/, determineVideoSize(stream.mediaStream)];
                    case 2:
                        size = _a.sent();
                        video.width = size.width;
                        video.height = size.height;
                        _a.label = 3;
                    case 3: return [2 /*return*/, video];
                }
            });
        });
    };
    MultiStreamsMixer.prototype.resetVideoStreams = function () {
        return __awaiter(this, void 0, void 0, function () {
            var _i, _a, stream, _b;
            return __generator(this, function (_c) {
                switch (_c.label) {
                    case 0:
                        _i = 0, _a = this.inputMediaStreams;
                        _c.label = 1;
                    case 1:
                        if (!(_i < _a.length)) return [3 /*break*/, 4];
                        stream = _a[_i];
                        delete stream.video;
                        if (stream.mediaStream.getVideoTracks().length == 0) {
                            return [3 /*break*/, 3];
                        }
                        _b = stream;
                        return [4 /*yield*/, MultiStreamsMixer.createVideo(stream)];
                    case 2:
                        _b.video = _c.sent();
                        _c.label = 3;
                    case 3:
                        _i++;
                        return [3 /*break*/, 1];
                    case 4: return [2 /*return*/];
                }
            });
        });
    };
    return MultiStreamsMixer;
}());
exports.MultiStreamsMixer = MultiStreamsMixer;
function determineVideoSize(mediaStream, timeout) {
    if (timeout === void 0) { timeout = 1000; }
    return __awaiter(this, void 0, void 0, function () {
        return __generator(this, function (_a) {
            if (mediaStream.getVideoTracks().length == 0) {
                return [2 /*return*/, { width: 0, height: 0 }];
            }
            return [2 /*return*/, new Promise(function (resolve, reject) {
                    var video = document.createElement('video');
                    video.style.position = 'absolute';
                    video.style.left = '-100000px';
                    video.style.top = '-100000px';
                    document.body.appendChild(video);
                    video.srcObject = mediaStream;
                    video.muted = true;
                    video.onloadedmetadata = function (e) {
                        resolve({ width: video.videoWidth, height: video.videoHeight });
                        video.remove();
                    };
                    setTimeout(function () {
                        reject();
                        video.remove();
                    }, timeout);
                })];
        });
    });
}
exports.determineVideoSize = determineVideoSize;
//# sourceMappingURL=MultiStreamsMixer.js.map