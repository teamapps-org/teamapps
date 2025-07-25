package testapp.test;

import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.mediasoupclient.*;
import org.teamapps.projector.component.mediasoupclient.apiclient.MediaSoupV3TokenGenerator;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.util.DemoComponentsGenerator;

import java.time.Duration;

public class MediaSoupV3WebRtcClientTest extends AbstractComponentTest<MediaSoupV3WebRtcClient> {

	private String url = "https://conference8.bgf.center";
	private int worker = 0;
	private String mediaServerSecret = "RoylsIcsgromalb3liaDrep";
	private String streamUuid = "myStream";
	private long minBitrate = 1000;
	private long maxBitrate = 2_000_000;
	private boolean simulcast = true;
	private boolean audioEnabled = true;
	private boolean videoEnabled = true;
	private boolean screenSharing = false;
	private int channelCount = 1;
	private boolean autoGainControl = true;
	private boolean echoCancellation = true;
	private boolean noiseSuppression = true;
	private int width = 800;
	private int height = 600;
	private VideoFacingMode facingMode = VideoFacingMode.USER;
	private int frameRate = 20;

	private boolean recording;


	public MediaSoupV3WebRtcClientTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		var clientFieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "activityLineVisible", clientFieldGenerator.createCheckBox("activityLineVisible"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "activityInactiveColor", clientFieldGenerator.createColorPicker("activityInactiveColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "activityActiveColor", clientFieldGenerator.createColorPicker("activityActiveColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "icons", clientFieldGenerator.createTagComboBoxForIcons("icons"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "caption", clientFieldGenerator.createTextField("caption"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "noVideoImageUrl", clientFieldGenerator.createTextField("noVideoImageUrl"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "bitrateDisplayEnabled", clientFieldGenerator.createCheckBox("bitrateDisplayEnabled"));

		var testFieldGenerator = new ConfigurationFieldGenerator<>(this, getTestContext());
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "url", testFieldGenerator.createTextField("url"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "worker", testFieldGenerator.createNumberField("worker", 0, 0, 31, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "streamUuid", testFieldGenerator.createTextField("streamUuid"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.VPN_KEY, "Media Server SECRET", testFieldGenerator.createTextField("mediaServerSecret"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "minBitrate", testFieldGenerator.createNumberField("minBitrate", 0, 10, 10_000_000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "maxBitrate", testFieldGenerator.createNumberField("maxBitrate", 0, 10, 10_000_000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "simulcast", testFieldGenerator.createCheckBox("simulcast"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "channelCount", testFieldGenerator.createNumberField("channelCount", 0, 1, 2, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "autoGainControl", testFieldGenerator.createCheckBox("autoGainControl"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "echoCancellation", testFieldGenerator.createCheckBox("echoCancellation"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "noiseSuppression", testFieldGenerator.createCheckBox("noiseSuppression"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "video width", testFieldGenerator.createNumberField("width", 0, 320, 1920, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "video height", testFieldGenerator.createNumberField("height", 0, 240, 1080, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "video facingMode", testFieldGenerator.createComboBoxForEnum("facingMode"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "video frameRate", testFieldGenerator.createNumberField("frameRate", 0, 1, 120, false));

		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "Audio", testFieldGenerator.createCheckBox("audioEnabled"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "Video", testFieldGenerator.createCheckBox("videoEnabled"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "Screen sharing", testFieldGenerator.createCheckBox("screenSharing"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "Recording", testFieldGenerator.createCheckBox("recording"));

		var publishButton = Button.create("publish");
		publishButton.onClick.addListener(aBoolean -> {
			AudioTrackConstraints audioConstraints = audioEnabled ? new AudioTrackConstraints(channelCount, autoGainControl, echoCancellation, noiseSuppression) : null;
			VideoTrackConstraints videoConstraints = videoEnabled ? new VideoTrackConstraints(width, height, facingMode, frameRate) : null;
			System.out.println("PUBLISH PRESSED: " + (audioConstraints != null) + ", " + (videoConstraints != null));
			getComponent().publish(
					streamUuid, url, worker,
					MediaSoupV3TokenGenerator.generatePublishJwtToken(streamUuid, mediaServerSecret, Duration.ofHours(6)),
					audioConstraints,
					videoConstraints,
					screenSharing ? new ScreenSharingConstraints() : null,
					maxBitrate,
					simulcast,
					2500);
		});
		var playButton = Button.create("play");
		playButton.onClick.addListener(aBoolean -> {
			getComponent().play(
					streamUuid,
					new MediaSoupServerUrlAndToken(url, worker, MediaSoupV3TokenGenerator.generateSubscribeJwtToken(streamUuid, mediaServerSecret, Duration.ofHours(1))),
					audioEnabled,
					videoEnabled,
					minBitrate,
					maxBitrate
			);
		});
		var stopButton = Button.create("stop");
		stopButton.onClick.addListener(aBoolean -> {
			getComponent().stop();
		});
		responsiveFormLayout.addLabelAndComponent(publishButton);
		responsiveFormLayout.addLabelAndComponent(playButton);
		responsiveFormLayout.addLabelAndComponent(stopButton);
	}

	@Override
	protected MediaSoupV3WebRtcClient createComponent() {
		MediaSoupV3WebRtcClient webRtcClient = new MediaSoupV3WebRtcClient();
		webRtcClient.setNoVideoImageUrl("/static-resources/video.jpg");

		var contextMenuItemView = DemoComponentsGenerator.createDummyItemView();
		webRtcClient.setContextMenuProvider(() -> contextMenuItemView);

		webRtcClient.onVoiceActivityChanged.addListener(webRtcClient::setActive);
		return webRtcClient;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getWorker() {
		return worker;
	}

	public void setWorker(int worker) {
		this.worker = worker;
	}

	public String getStreamUuid() {
		return streamUuid;
	}

	public void setStreamUuid(String streamUuid) {
		this.streamUuid = streamUuid;
	}

	public String getMediaServerSecret() {
		return mediaServerSecret;
	}

	public void setMediaServerSecret(String mediaServerSecret) {
		this.mediaServerSecret = mediaServerSecret;
	}

	public long getMinBitrate() {
		return minBitrate;
	}

	public void setMinBitrate(long minBitrate) {
		this.minBitrate = minBitrate;
	}

	public long getMaxBitrate() {
		return maxBitrate;
	}

	public void setMaxBitrate(long maxBitrate) {
		this.maxBitrate = maxBitrate;
	}

	public boolean isAudioEnabled() {
		return audioEnabled;
	}

	public void setAudioEnabled(boolean audioEnabled) {
		this.audioEnabled = audioEnabled;
	}

	public boolean isVideoEnabled() {
		return videoEnabled;
	}

	public void setVideoEnabled(boolean videoEnabled) {
		this.videoEnabled = videoEnabled;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	public boolean isAutoGainControl() {
		return autoGainControl;
	}

	public void setAutoGainControl(boolean autoGainControl) {
		this.autoGainControl = autoGainControl;
	}

	public boolean isEchoCancellation() {
		return echoCancellation;
	}

	public void setEchoCancellation(boolean echoCancellation) {
		this.echoCancellation = echoCancellation;
	}

	public boolean isNoiseSuppression() {
		return noiseSuppression;
	}

	public void setNoiseSuppression(boolean noiseSuppression) {
		this.noiseSuppression = noiseSuppression;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public VideoFacingMode getFacingMode() {
		return facingMode;
	}

	public void setFacingMode(VideoFacingMode facingMode) {
		this.facingMode = facingMode;
	}

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	public boolean isScreenSharing() {
		return screenSharing;
	}

	public void setScreenSharing(boolean screenSharing) {
		this.screenSharing = screenSharing;
	}

	public boolean isSimulcast() {
		return simulcast;
	}

	public void setSimulcast(boolean simulcast) {
		this.simulcast = simulcast;
	}

	public boolean isRecording() {
		return recording;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}
}
