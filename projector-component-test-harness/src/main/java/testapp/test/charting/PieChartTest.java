

package testapp.test.charting;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.chart.pie.NamedDataPoint;
import org.teamapps.projector.component.chart.pie.PieChart;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PieChartTest extends AbstractComponentTest<PieChart> {

	private static final List<Color> COLORS = Arrays.asList(
			RgbaColor.MATERIAL_RED_400,
			RgbaColor.MATERIAL_GREEN_400,
			RgbaColor.MATERIAL_BLUE_400,
			RgbaColor.MATERIAL_YELLOW_400,
			RgbaColor.MATERIAL_GREY_400,
			RgbaColor.MATERIAL_ORANGE_400,
			RgbaColor.MATERIAL_CYAN_400,
			RgbaColor.MATERIAL_BROWN_400,
			RgbaColor.MATERIAL_PURPLE_400,
			RgbaColor.MATERIAL_AMBER_400
	);

	public PieChartTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "animationDuration", fieldGenerator.createNumberField("animationDuration", 0, 0, 5000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "legendStyle", fieldGenerator.createComboBoxForEnum("legendStyle"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "dataPointWeighting", fieldGenerator.createComboBoxForEnum("dataPointWeighting"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "rotation3D", fieldGenerator.createNumberField("rotation3D", 0, 0, 90, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "height3D", fieldGenerator.createNumberField("height3D", 0, 0, 200, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "rotationClockwise", fieldGenerator.createNumberField("rotationClockwise", 0, 0, 360, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "innerRadiusProportion", fieldGenerator.createNumberField("innerRadiusProportion", 2, 0, 1, false));

		Button dataPointsButton = Button.create(MaterialIcon.REFRESH, "Set Data Points");
		dataPointsButton.onClick.addListener(aBoolean -> {
			getComponent().setDataPoints(createDataPoints());
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Refresh", dataPointsButton);
	}

	@Override
	public PieChart createComponent() {
		PieChart pieChart = new PieChart(createDataPoints());
		return pieChart;
	}

	public List<NamedDataPoint> createDataPoints() {
		Random random = new Random();
		return IntStream.range(0, 7)
				.mapToObj(i -> new NamedDataPoint("DataPoint " + i, 0.03d * (i + 1) * random.nextDouble(), COLORS.get(i % COLORS.size())))
				.collect(Collectors.toList());
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/Panel.html";
	}
}
