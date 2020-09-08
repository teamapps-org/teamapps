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

package org.teamapps.ux.component.webrtc.apiclient;

import com.fasterxml.jackson.databind.JsonNode;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MediaSoupV3RestClient implements MediaSoupV3ApiClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final WebTarget webTarget;
	private final String serverSecret;

	public MediaSoupV3RestClient(String serverUrl, String serverSecret) {
		webTarget = JerseyClientBuilder.createClient()
				.target(serverUrl)
				.path("api");
		this.serverSecret = serverSecret;
	}

	@Override
	public CompletableFuture<Void> startFileStreaming(int workerId, StreamFileRequest streamFileRequest) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		webTarget.path(workerId + "/fileStreaming")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateStreamingJwtToken(streamFileRequest.getStreamUuid(), serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(streamFileRequest), new InvocationCallback<Void>() {
					@Override
					public void completed(Void s) {
						future.complete(s);
					}

					@Override
					public void failed(Throwable throwable) {
						future.completeExceptionally(throwable);
					}
				});
		return future;
	}


	public CompletableFuture<Void> stopFileStreaming(int workerId, String streamUuid) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		webTarget.path(workerId + "/stopFileStreaming")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateStreamingJwtToken(streamUuid, serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(new StreamData(streamUuid)),
						new InvocationCallback<Void>() {
							@Override
							public void completed(Void s) {
								future.complete(s);
							}

							@Override
							public void failed(Throwable throwable) {
								future.completeExceptionally(throwable);
							}
						});
		return future;
	}

	public CompletableFuture<Void> startRecording(String streamUuid) {
		return startRecording(streamUuid, null);
	}

	public CompletableFuture<Void> startRecording(String streamUuid, Set<MediaKind> kinds) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		webTarget.path("0/startRecording")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(new StreamAndKinds(streamUuid, kinds)), new InvocationCallback<Void>() {
					@Override
					public void completed(Void s) {
						future.complete(s);
					}

					@Override
					public void failed(Throwable throwable) {
						future.completeExceptionally(throwable);
					}
				});
		return future;
	}

	public CompletableFuture<Void> stopRecording(String streamUuid) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		webTarget.path("0/stopRecording")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(new StreamAndKinds(streamUuid, Set.of(MediaKind.AUDIO, MediaKind.VIDEO))), new InvocationCallback<Void>() {
					@Override
					public void completed(Void s) {
						future.complete(s);
					}

					@Override
					public void failed(Throwable throwable) {
						future.completeExceptionally(throwable);
					}
				});
		return future;
	}

	public CompletableFuture<JsonNode> transportStats(List<String> ids) {
		CompletableFuture<JsonNode> future = new CompletableFuture<>();
		webTarget
				.path("0/transportStats")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateGeneralApiToken(serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(new StatsInput(ids)), new InvocationCallback<JsonNode>() {
					@Override
					public void completed(JsonNode s) {
						future.complete(s);
					}

					@Override
					public void failed(Throwable throwable) {
						future.completeExceptionally(throwable);
					}
				});
		return future;
	}

	@Override
	public CompletableFuture<Double> getWorkerLoad(int workerId) {
		CompletableFuture<Double> future = new CompletableFuture<>();
		webTarget.path("0/workerLoad")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateGeneralApiToken(serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(null), new InvocationCallback<WorkerLoadData>() {
					@Override
					public void completed(WorkerLoadData s) {
						future.complete(s.getCurrentLoad());
					}

					@Override
					public void failed(Throwable throwable) {
						future.completeExceptionally(throwable);
					}
				});
		return future;
	}

	@Override
	public CompletableFuture<Integer> getNumberOfWorkers() {
		CompletableFuture<Integer> future = new CompletableFuture<>();
		webTarget.path("0/numWorkers")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateGeneralApiToken(serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(null), new InvocationCallback<NumWorkersData>() {
					@Override
					public void completed(NumWorkersData s) {
						future.complete(s.getNum());
					}

					@Override
					public void failed(Throwable throwable) {
						future.completeExceptionally(throwable);
					}
				});
		return future;
	}

	@Override
	public CompletableFuture<List<String>> getRecordedStreamUuids() {
		CompletableFuture<List<String>> future = new CompletableFuture<>();
		webTarget.path("0/recordedStreams")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(null), new InvocationCallback<ListData>() {
					@Override
					public void completed(ListData s) {
						future.complete(s.getList());
					}

					@Override
					public void failed(Throwable throwable) {
						future.completeExceptionally(throwable);
					}
				});
		return future;
	}

	@Override
	public CompletableFuture<List<String>> getStreamRecordingsForUuid(String streamUuid) {
		CompletableFuture<List<String>> future = new CompletableFuture<>();
		webTarget.path("0/streamRecordings")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(new StreamData(streamUuid)),
						new InvocationCallback<ListData>() {
							@Override
							public void completed(ListData s) {
								future.complete(s.getList());
							}

							@Override
							public void failed(Throwable throwable) {
								future.completeExceptionally(throwable);
							}
						});
		return future;
	}

	public CompletableFuture<Void> deleteStreamRecordings(String streamUuid) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		webTarget.path("0/deleteStreamRecordings")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(new StreamData(streamUuid)),
						new InvocationCallback<Void>() {
							@Override
							public void completed(Void s) {
								future.complete(s);
							}

							@Override
							public void failed(Throwable throwable) {
								future.completeExceptionally(throwable);
							}
						});
		return future;
	}

	public CompletableFuture<Void> deleteRecording(String filePathInput) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		webTarget.path("0/deleteRecording")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateRecordingJwtToken(serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(new FilePathInput(filePathInput)),
						new InvocationCallback<Void>() {
							@Override
							public void completed(Void s) {
								future.complete(s);
							}

							@Override
							public void failed(Throwable throwable) {
								future.completeExceptionally(throwable);
							}
						});
		return future;
	}


	public CompletableFuture<KindsOptionsData> kindsByFile(String filePath, boolean relativePath) {
		CompletableFuture<KindsOptionsData> future = new CompletableFuture<>();
		webTarget.path("0/kindsByFile")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + MediaSoupV3TokenGenerator.generateGeneralApiToken(serverSecret, Duration.ofDays(365)))
				.async()
				.post(Entity.json(new KindsByFileInput(filePath, relativePath)),
						new InvocationCallback<KindsOptionsData>() {
							@Override
							public void completed(KindsOptionsData s) {
								future.complete(s);
							}

							@Override
							public void failed(Throwable throwable) {
								future.completeExceptionally(throwable);
							}
						});
		return future;
	}


	// public CompletableFuture<Void> resumeConsumer(ConsumerData consumerData) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("resumeConsumer").request(MediaType.APPLICATION_JSON).async().post(Entity.json(consumerData), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> pauseConsumer(ConsumerData consumerData) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("pauseConsumer").request(MediaType.APPLICATION_JSON).async().post(Entity.json(consumerData), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> setPreferredLayers(ConsumerPreferredLayers consumerPreferredLayers) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("setPreferredLayers").request(MediaType.APPLICATION_JSON).async().post(Entity.json(consumerPreferredLayers), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> resumeProducer(ProducerData producerData) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("resumeProducer").request(MediaType.APPLICATION_JSON).async().post(Entity.json(producerData), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> pauseProducer(ProducerData producerData) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("pauseProducer").request(MediaType.APPLICATION_JSON).async().post(Entity.json(producerData), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> closeProducer(ProducerData producerData) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("closeProducer").request(MediaType.APPLICATION_JSON).async().post(Entity.json(producerData), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> liveStreaming(LiveStreamRequest liveStreamRequest) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("liveStreaming").request(MediaType.APPLICATION_JSON).async().post(Entity.json(liveStreamRequest), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> liveToHls(LiveToHlsRequest liveToHlsRequest) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("liveToHls").request(MediaType.APPLICATION_JSON).async().post(Entity.json(liveToHlsRequest), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	// public CompletableFuture<PushStreamInputsResponse> pushToServerInputs(PushStreamInputsRequest pushStreamInputsRequest) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("pushToServerInputs").request(MediaType.APPLICATION_JSON).async().post(Entity.json(pushStreamInputsRequest),
	// 			new InvocationCallback<PushStreamInputsResponse>() {
	// 				@Override
	// 				public void completed(PushStreamInputsResponse s) {
	// 					future.complete(s);
	// 				}
	//
	// 				@Override
	// 				public void failed(Throwable throwable) {
	// 					future.completeExceptionally(throwable);
	// 				}
	// 			});
	// 	return future;
	// }
	//
	// public CompletableFuture<PushStreamOptionsResponse> pushToServerOptions(PushStreamOptionsRequest pushStreamOptionsRequest) {
	// 	CompletableFuture<PushStreamInputsResponse> future = new CompletableFuture<>();
	// 	webTarget.path("pushToServerOptions").request(MediaType.APPLICATION_JSON).async().post(Entity.json(pushStreamOptionsRequest),
	// 			new InvocationCallback<PushStreamOptionsResponse>() {
	// 				@Override
	// 				public void completed(PushStreamOptionsResponse s) {
	// 					future.complete(s);
	// 				}
	//
	// 				@Override
	// 				public void failed(Throwable throwable) {
	// 					future.completeExceptionally(throwable);
	// 				}
	// 			});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> pushToServer(PushStreamRequest pushStreamRequest) {
	// 	CompletableFuture<PushStreamOptionsResponse> future = new CompletableFuture<>();
	// 	webTarget.path("pushToServer").request(MediaType.APPLICATION_JSON).async().post(Entity.json(pushStreamRequest), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<PullStreamInputsResponse> pullFromServerInputs(PullStreamInputsRequest pullStreamInputsRequest) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("pullFromServerInputs").request(MediaType.APPLICATION_JSON).async().post(Entity.json(pullStreamInputsRequest),
	// 			new InvocationCallback<PullStreamInputsResponse>() {
	// 				@Override
	// 				public void completed(PullStreamInputsResponse s) {
	// 					future.complete(s);
	// 				}
	//
	// 				@Override
	// 				public void failed(Throwable throwable) {
	// 					future.completeExceptionally(throwable);
	// 				}
	// 			});
	// 	return future;
	// }
	// public CompletableFuture<Void> requestKeyframe(ConsumerData consumerData) {
	// 	CompletableFuture<KindsOptionsData> future = new CompletableFuture<>();
	// 	webTarget.path("requestKeyframe").request(MediaType.APPLICATION_JSON).async().post(Entity.json(consumerData), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }

	// public CompletableFuture<MixerInput> mixerStart(MixerCreateOptions mixerCreateOptions) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("mixerStart").request(MediaType.APPLICATION_JSON).async().post(Entity.json(mixerCreateOptions), new InvocationCallback<MixerInput>() {
	// 		@Override
	// 		public void completed(MixerInput s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> mixerClose(MixerInput mixerInput) {
	// 	CompletableFuture<MixerInput> future = new CompletableFuture<>();
	// 	webTarget.path("mixerClose").request(MediaType.APPLICATION_JSON).async().post(Entity.json(mixerInput), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> mixerAdd(MixerAddAudioData mixerAddAudioData|MixerAddVideoData) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("mixerAdd").request(MediaType.APPLICATION_JSON).async().post(Entity.json(mixerAddAudioData), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> mixerUpdate(MixerUpdateData mixerUpdateData) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("mixerUpdate").request(MediaType.APPLICATION_JSON).async().post(Entity.json(mixerUpdateData), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> mixerRemove(MixerRemoveData mixerRemoveData) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("mixerRemove").request(MediaType.APPLICATION_JSON).async().post(Entity.json(mixerRemoveData), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<MixerPipeInput> mixerPipeStart(MixerPipeLiveData|MixerPipeRecordingData|MixerPipeRtmpData|MixerPipeHlsData mixerPipeLiveData) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("mixerPipeStart").request(MediaType.APPLICATION_JSON).async().post(Entity.json(mixerPipeLiveData), new InvocationCallback<MixerPipeInput>() {
	// 		@Override
	// 		public void completed(MixerPipeInput s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	//
	// public CompletableFuture<Void> mixerPipeStop(MixerPipeStopInput mixerPipeStopInput) {
	// 	CompletableFuture<MixerPipeInput> future = new CompletableFuture<>();
	// 	webTarget.path("mixerPipeStop").request(MediaType.APPLICATION_JSON).async().post(Entity.json(mixerPipeStopInput), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }
	// public CompletableFuture<Void> setMaxIncomingBitrate(TransportBitrateData transportBitrateData) {
	// 	CompletableFuture<Void> future = new CompletableFuture<>();
	// 	webTarget.path("setMaxIncomingBitrate").request(MediaType.APPLICATION_JSON).async().post(Entity.json(transportBitrateData), new InvocationCallback<Void>() {
	// 		@Override
	// 		public void completed(Void s) {
	// 			future.complete(s);
	// 		}
	//
	// 		@Override
	// 		public void failed(Throwable throwable) {
	// 			future.completeExceptionally(throwable);
	// 		}
	// 	});
	// 	return future;
	// }

}
