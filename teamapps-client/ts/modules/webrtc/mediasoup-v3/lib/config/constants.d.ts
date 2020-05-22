export declare enum ACTION {
    GET_SERVER_CONFIGS = "getServerConfigs",
    CREATE_TRANSPORT = "createTransport",
    CONNECT_TRANSPORT = "connectTransport",
    CLOSE_TRANSPORT = "closeTransport",
    PRODUCE = "produce",
    CONSUME = "consume",
    RESUME_CONSUMER = "resumeConsumer",
    PAUSE_CONSUMER = "pauseConsumer",
    CLOSE_CONSUMER = "closeConsumer",
    RESUME_PRODUCER = "resumeProducer",
    PAUSE_PRODUCER = "pauseProducer",
    CLOSE_PRODUCER = "closeProducer",
    FILE_STREAMING = "fileStreaming",
    STOP_FILE_STREAMING = "stopFileStreaming",
    START_RECORDING = "startRecording",
    STOP_RECORDING = "stopRecording",
    CREATE_PIPE_TRANSPORT = "createPipeTransport",
    CONNECT_PIPE_TRANSPORT = "connectPipeTransport",
    SET_PREFERRED_LAYERS = "setPreferredLayers",
    SET_MAX_INCOMING_BITRATE = "setMaxIncomingBitrate",
    PRODUCERS_STATS = "producersStats",
    CONSUMERS_STATS = "consumersStats",
    TRANSPORT_STATS = "transportStats",
    PIPE_TO_REMOTE_PRODUCER = "pipeToRemoteProducer",
    PIPE_FROM_REMOTE_PRODUCER = "pipeFromRemoteProducer",
    WORKER_LOAD = "workerLoad",
    NUM_WORKERS = "numWorkers",
    RECORDED_STREAMS = "recordedStreams",
    STREAM_RECORDINGS = "streamRecordings",
    DELETE_STREAM_RECORDINGS = "deleteStreamRecordings",
    DELETE_RECORDING = "deleteRecording",
    PUSH_TO_SERVER_INPUTS = "pushToServerInputs",
    PULL_FROM_SERVER_INPUTS = "pullFromServerInputs",
    KINDS_BY_FILE = "kindsByFile"
}
export declare enum PATH {
    RECORDINGS = "recordings",
    MEDIASOUP = "mediasoup",
    FRONT = "front",
    API_DOCS = "api-docs"
}
export declare enum ERROR {
    UNKNOWN = 500,
    UNAUTHORIZED = 401,
    INVALID_TRANSPORT = 530,
    INVALID_PRODUCER = 531,
    INVALID_CONSUMER = 532,
    INVALID_STREAM = 533,
    INVALID_OPERATION = 534,
    INVALID_WORKER = 535
}
export declare enum API_OPERATION {
    SUBSCRIBE = 0,
    PUBLISH = 1,
    RECORDING = 2,
    STREAMING = 3
}
export declare const API_OPERATION_BY_ACTION: {
    produce: API_OPERATION;
    recordedStreams: API_OPERATION;
    streamRecordings: API_OPERATION;
    deleteStreamRecordings: API_OPERATION;
    startRecording: API_OPERATION;
    stopRecording: API_OPERATION;
    fileStreaming: API_OPERATION;
    stopFileStreaming: API_OPERATION;
};
