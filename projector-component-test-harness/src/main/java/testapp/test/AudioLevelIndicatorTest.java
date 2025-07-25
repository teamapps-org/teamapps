package testapp.test;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.projector.component.audiolevel.AudioLevelIndicator;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.mediasoupclient.DtoMediaDeviceInfo;
import org.teamapps.projector.component.mediasoupclient.MediaDeviceKind;
import org.teamapps.projector.component.mediasoupclient.MediaSoupV3WebRtcClient;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AudioLevelIndicatorTest extends AbstractComponentTest<AudioLevelIndicator> {

	private List<DtoMediaDeviceInfo> deviceInfos;

	public AudioLevelIndicatorTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.MIC, "Settings");

		MediaSoupV3WebRtcClient.enumerateDevices().thenAccept(uiMediaDeviceInfos -> this.deviceInfos = uiMediaDeviceInfos);;
		ComboBox<DtoMediaDeviceInfo> deviceComboBox = createMediaDeviceInfoComboBox(() -> deviceInfos, MediaDeviceKind.AUDIO_INPUT);
		deviceComboBox.setClearButtonEnabled(true);
		deviceComboBox.onValueChanged.addListener(uiMediaDeviceInfo -> getComponent().setDeviceId(uiMediaDeviceInfo != null ? uiMediaDeviceInfo.getDeviceId() : null));
		responsiveFormLayout.addLabelAndField(MaterialIcon.MIC, "Device", deviceComboBox);
	}

	@Override
	protected AudioLevelIndicator createComponent() {
		return new AudioLevelIndicator();
	}

	public static ComboBox<DtoMediaDeviceInfo> createMediaDeviceInfoComboBox(Supplier<List<DtoMediaDeviceInfo>> deviceInfos, MediaDeviceKind kind) {
		final ComboBox<DtoMediaDeviceInfo> micField;
		micField = new ComboBox<>();
		micField.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		micField.setPropertyProvider((deviceInfo, prop) -> Map.of(
				BaseTemplates.PROPERTY_ICON, MaterialIcon.MIC,
				BaseTemplates.PROPERTY_CAPTION, deviceInfo.getLabel() != null ? deviceInfo.getLabel() : deviceInfo.getKind() + " " + deviceInfo.getDeviceId()
		));
		micField.setRecordToStringFunction(deviceInfo -> deviceInfo.getLabel() != null ? deviceInfo.getLabel() : deviceInfo.getKind() + " " + deviceInfo.getDeviceId());
		micField.setModel(s -> deviceInfos.get().stream()
				.filter(deviceInfo -> deviceInfo.getKind() == kind)
				.filter(deviceInfo -> StringUtils.isBlank(s) || deviceInfo.getLabel().toLowerCase().contains(s.toLowerCase()))
				.collect(Collectors.toList()));
		return micField;
	}
}
