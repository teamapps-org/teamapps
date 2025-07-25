

package testapp.test;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.shakaplayer.PosterImageSize;
import org.teamapps.projector.component.shakaplayer.ShakaPlayer;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.util.AkamaiTokenUrlCreator;

public class ShakaPlayerTest extends AbstractComponentTest<ShakaPlayer> {

	public ShakaPlayerTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "HLS URL", fieldGenerator.createTextField("hlsUrl"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Dash URL", fieldGenerator.createTextField("dashUrl"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Poster image URL", fieldGenerator.createTextField("posterImageUrl"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Poster image size", fieldGenerator.createComboBoxForEnum("posterImageSize"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Background color", fieldGenerator.createColorPicker("backgroundColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "timeUpdateEventThrottleMillis", fieldGenerator.createNumberField("timeUpdateEventThrottleMillis", 0, 0, 60_000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "trackLabelFormat", fieldGenerator.createComboBoxForEnum("trackLabelFormat"));

		Button setTimeButton = Button.create("Set time to 30s");
		setTimeButton.onClick.addListener(() -> getComponent().jumpTo(30_000));
		responsiveFormLayout.addLabelAndField("Set time to 30s", setTimeButton);
	}

	@Override
	public ShakaPlayer createComponent() {
		ShakaPlayer.setDistinctManifestAudioTracksFixEnabled(true);
		ShakaPlayer shakaPlayer = new ShakaPlayer();
		shakaPlayer.setHlsUrl(new AkamaiTokenUrlCreator("1234").createAclUrlString("http://192.168.0.184:8080/hls/result.mp4/master.m3u8"));
		shakaPlayer.setDashUrl(new AkamaiTokenUrlCreator("1234").createAclUrlString("http://192.168.0.184:8080/dash/devito,360p.mp4,480p.mp4,720p.mp4,.urlset/manifest.mpd"));
		shakaPlayer.setPosterImageUrl(new AkamaiTokenUrlCreator("1234").createAclUrlString("http://192.168.0.184:8080/thumb/devito720p.mp4/thumb-2000.jpg"));
		shakaPlayer.setPosterImageSize(PosterImageSize.CONTAIN);
		shakaPlayer.setTimeUpdateEventThrottleMillis(1000);
		shakaPlayer.setBackgroundColor(Color.fromRgbValue(0x686868));
		return shakaPlayer;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/ShakaPlayer.html";
	}
}
