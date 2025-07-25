

package testapp.test;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.videoplayer.PosterImageSize;
import org.teamapps.projector.component.videoplayer.PreloadMode;
import org.teamapps.projector.component.videoplayer.VideoPlayer;
import org.teamapps.projector.icon.composite.CompositeIcon;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VideoPlayerTest extends AbstractComponentTest<VideoPlayer> {

	public VideoPlayerTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		ComboBox<String> urlComboBox = fieldGenerator.createFreeTextComboBox("url",
				"http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",
				"http://localhost:9000/resources/Fathers.mp4"
//				getSessionContext().createFileLink(new File("/Users/yamass/Downloads/video.mp4")),
//				getSessionContext().createFileLink(new File("/Users/yamass/Downloads/video-bgf.mp4"))
		);
		urlComboBox.setFreeTextEnabled(true);
		urlComboBox.setClearButtonEnabled(true);
		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Video URL", urlComboBox);

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Poster image URL", fieldGenerator.createTextField("posterImageUrl"));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Poster image size", fieldGenerator.createComboBoxForEnum("posterImageSize"));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Autoplay", fieldGenerator.createCheckBox("autoplay"));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show controls", fieldGenerator.createCheckBox("showControls"));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Preload", fieldGenerator.createComboBoxForEnum("preloadMode", PreloadMode.class));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Progress events every X seconds", fieldGenerator.createNumberField("sendPlayerProgressEventsEachXSeconds", 0, 1, 60, false));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Background color", fieldGenerator.createColorPicker("backgroundColor"));

		Button playButton = Button.create("Play");
		playButton.onClick.addListener(() -> {
			CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> {
				getSessionContext().runWithContext(() -> {
					getComponent().play();
				});
			});
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.PLAY_ARROW, "Play", playButton);


	}

	@Override
	public VideoPlayer createComponent() {
		VideoPlayer videoPlayer = new VideoPlayer();
		videoPlayer.setUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
		videoPlayer.setAutoplay(false);
		videoPlayer.setControlsVisible(true);
		videoPlayer.setPreloadMode(PreloadMode.METADATA);
		videoPlayer.setPosterImageUrl("https://192.168.0.184:8443/thumb/devito720p.mp4/thumb-2000.jpg");
		videoPlayer.setPosterImageSize(PosterImageSize.CONTAIN);
		videoPlayer.setPlayerProgressIntervalSeconds(1);
		videoPlayer.setBackgroundColor(Color.fromRgbValue(0x686868));
		return videoPlayer;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/VideoPlayer.html";
	}
}
