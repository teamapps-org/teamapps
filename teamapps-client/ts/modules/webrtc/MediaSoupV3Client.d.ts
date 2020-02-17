interface MediaSoupV3Client {
	constructor(serverUrl: string, options?: {
		/**
		 * Callback for changes in the connection state to the mediasoup server (websocket connection).
		 * NOTE: This connection may be shared between clients if necessary.
		 */
		onConnectionChange?: (connected: boolean) => void
	});

	/**
	 * Publishes the given media stream (audio and/or video, depending on which channels the mediaStream contains).
	 *
	 * If publishing was successful, the returned promise resolves normally, otherwise it rejects.
	 *
	 * If publishing was initially successful but got interrupted (by network problems),
	 * the client will try to re-connect until the stream is published again
	 * or the stop() method got called.
	 *
	 * @param mediaStream The mediaStream to publish.
	 * @param streamUuid The UUID of the stream to get published.
	 * @param authToken The JWT token for authorization at the server.
	 * @param params Additional parameters
	 * @return Promise resolving on successful publishing, rejecting on error while publishing.
	 */
	publish(mediaStream: MediaStream, streamUuid: string, authToken: string, params: PublishingParameters): Promise<void>;

	/**
	 * Attempts to playback the stream with the given streamUuid. If no such stream exists, this method waits for the specified
	 * amount of time (initialTimeoutSeconds) until such a stream is available (got published).
	 *
	 * If the initial playback initialization fails for any reason (timeout, communication error, etc.) the returned promise rejects.
	 * Otherwise, the returned promise resolves to the received mediaStream.
	 *
	 * If playback was initially successful but got (by network problems, or because the published stream is no more available),
	 * the client will try to re-connect until the playback is re-established or the stop() method got called.
	 *
	 * @param streamUuid The UUID of the stream to play back
	 * @param authToken
	 * @param originServer The URL of the origin server for this stream.
	 * @param originAuthToken
	 * @param initialTimeoutSeconds The number of seconds to wait until there is a stream with the streamUuid.
	 * @param params Additional parameters
	 */
	playback(streamUuid: string, authToken: string,
	         originServer: string | null, originAuthToken: string | null,
	         initialTimeoutSeconds: number, params: PlaybackParameters): Promise<MediaStream>;

	/**
	 * Changes the (playback) bitrateConstraints while playing.
	 *
	 * @param bitrateConstraints
	 */
	changePlaybackBitrateConstraints(bitrateConstraints: PlaybackBitrateConstraints): void;

	/**
	 * Stops either publish or playback. (Both are not possible at the same time.)
	 */
	stop(): Promise<void>;
}

export interface PublishingParameters {

	/**
	 * The minimum audio bitrate to send.
	 */
	minAudioBitrate?: number;
	/**
	 * The minimum video bitrate to send.
	 */
	minVideoBitrate?: number;
	/**
	 * The maximum audio bitrate to send.
	 */
	maxAudioBitrate?: number;
	/**
	 * The maximum video bitrate to send.
	 */
	maxVideoBitrate?: number;

	simulcast: boolean;

	/**
	 * Callback called whenever the streaming status of audio/video changes. The boolean flags denote whether
	 * audio/video is currently actively streaming to the server (media data gets sent).
	 *
	 * E.g.: When audio starts streaming and video does not actively stream,
	 * the callback gets called with {audioStreaming: true, videoStreaming: false}.
	 * When audio streaming stops but video is active,
	 * the callback gets called with {audioStreaming: false, videoStreaming: true}.
	 * And so on.
	 *
	 * @param audioStreaming true iff audio data is being sent to the mediasoup server
	 * @param videoStreaming true iff video data is being sent to the mediasoup server
	 */
	onStreamingStatusChange?: (audioStreaming: boolean, videoStreaming: boolean) => void;

	/**
	 * Callback called with the current actual bitrate sent by media type (audio/video).
	 */
	onBitrate?: (mediaType: "audio" | "video", bitrate: number) => void;
}

export interface PlaybackParameters {
	/**
	 * Receive audio?
	 */
	audio: boolean;
	/**
	 * Receive video?
	 */
	video: boolean;

	/**
	 * Constraints that limit the choice of simulcast quality profiles.
	 */
	bitrateConstraints?: PlaybackBitrateConstraints;

	/**
	 * Callback called whenever the played back simulcast profile changes.
	 * @param profile The identifier of the profile
	 * @param audioBitrate The audio bitrate of the profile
	 * @param videoBitrate The video bitrate of the profile
	 */
	onProfileChange: (profile: string, audioBitrate: number, videoBitrate: number) => void;

	/**
	 * Callback called whenever the streaming status of audio/video changes. The boolean flags denote whether
	 * audio/video is currently actively being received from the server (media data gets received).
	 *
	 * E.g.: When audio starts streaming and video does not actively stream,
	 * the callback gets called with {audioStreaming: true, videoStreaming: false}.
	 * When audio streaming stops but video is active,
	 * the callback gets called with {audioStreaming: false, videoStreaming: true}.
	 * And so on.
	 *
	 * @param audioStreaming true iff audio data is being received from the mediasoup server
	 * @param videoStreaming true iff video data is being received from the mediasoup server
	 */
	onStreamingStatusChange?: (audioStreaming: boolean, videoStreaming: boolean) => void;

	/**
	 * Callback called with the current actual bitrate received by media type (audio/video).
	 *
	 * @param mediaType
	 * @param bitrate
	 */
	onBitrate?: (mediaType: "audio" | "video", bitrate: number) => void;
}

interface PlaybackBitrateConstraints {
	/**
	 * The minimum audio bitrate to receive. (For manual adjustment)
	 */
	minAudioBitrate?: number;
	/**
	 * The minimum video bitrate to receive. (For manual adjustment)
	 */
	minVideoBitrate?: number;
	/**
	 * The maximum audio bitrate to receive. (For manual adjustment)
	 */
	maxAudioBitrate?: number;
	/**
	 * The maximum video bitrate to receive. (For manual adjustment)
	 */
	maxVideoBitrate?: number;
}
