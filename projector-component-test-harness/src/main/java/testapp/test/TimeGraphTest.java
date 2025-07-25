

package testapp.test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.ibm.icu.util.ULocale;
import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.core.NumberFieldSliderMode;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.ColorPicker;
import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.field.FieldEditingMode;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.timegraph.*;
import org.teamapps.projector.component.timegraph.datapoints.*;
import org.teamapps.projector.component.timegraph.graph.*;
import org.teamapps.projector.component.timegraph.model.AbstractIncidentGraphModel;
import org.teamapps.projector.component.timegraph.model.AggregatingLineGraphModel;
import org.teamapps.projector.component.timegraph.model.AggregationType;
import org.teamapps.projector.component.timegraph.model.DelegatingHoseModel;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimeGraphTest extends AbstractComponentTest<TimeGraph> {

	private static final RgbaColor[] LINE_COLORS = new RgbaColor[]{
			Color.MATERIAL_GREEN_300,
			Color.MATERIAL_BLUE_600,
			Color.MATERIAL_PURPLE_300,
			Color.MATERIAL_ORANGE_300,
			Color.MATERIAL_RED_500,
			Color.MATERIAL_BROWN_600,
			Color.MATERIAL_CYAN_500,
			Color.MATERIAL_GREY_400,
//			Color.MATERIAL_DEEP_ORANGE_400,
//			Color.MATERIAL_INDIGO_500,
//			Color.MATERIAL_LIME_400,
//			Color.MATERIAL_PINK_500,
//			Color.MATERIAL_YELLOW_400
	};
	private static final int MAX_NUMBER_OF_SERIES = LINE_COLORS.length;
	public static final int END = 30 * 3600_000;
	public static final int START = 0;
	public static final int NUMBER_OF_DATA_POINTS = 100_000;

	private long intervalYMin = 0;
	private long intervalYMax = 6000;
	private int numberOfSeries = 1;
	private LineChartMouseScrollZoomPanMode mouseScrollZoomPanMode = LineChartMouseScrollZoomPanMode.ENABLED;
	private final HoseGraph hoseGraph;
	private final IncidentGraph incidentGraph;
	private final GraphGroup graphGroup;
	private boolean showLineChartHose;
	private boolean showIncidentGraph;
	private boolean showDisplayGroup;

	private final List<LineGraph> lines = new ArrayList<>();

	public TimeGraphTest(ComponentTestContext testContext) {
		super(testContext);

		List<Function<Double, Double>> functions = Arrays.asList(
				aDouble -> (double) Instant.ofEpochMilli(aDouble.longValue()).atZone(ZoneOffset.UTC).getHour() - 12,
				aDouble -> Math.sin(aDouble / 2E2) / 2 + .5,
				aDouble -> (aDouble / 10000_000_000d) * (aDouble / 10000_000_000d),
				aDouble -> Math.cos(aDouble / 2E10) / 2 + .5,
				aDouble -> Math.pow(1 - aDouble, 2),
				aDouble -> 1d,
				aDouble -> aDouble + (aDouble / 4) * Math.sin(aDouble),
				aDouble -> (double) Instant.ofEpochMilli(aDouble.longValue() - (6 * 3600_000)).atZone(ZoneOffset.UTC).getHour()
		);

		Table<Integer, AggregationType, AggregatingLineGraphModel> models = HashBasedTable.create();
		for (int i = 0; i < MAX_NUMBER_OF_SERIES; i++) {
			models.put(i, AggregationType.AVERAGE, new AggregatingLineGraphModel(createLineGraphData(functions.get(i % functions.size())), AggregationType.AVERAGE));
			models.put(i, AggregationType.MIN, new AggregatingLineGraphModel(createLineGraphData(functions.get(i % functions.size())), AggregationType.MIN));
			models.put(i, AggregationType.MAX, new AggregatingLineGraphModel(createLineGraphData(functions.get(i % functions.size())), AggregationType.MAX));
		}

		for (int i = 0; i < MAX_NUMBER_OF_SERIES; i++) {
			final LineGraph lineGraph = new LineGraph(
					models.get(i, AggregationType.AVERAGE),
					LineChartCurveType.values()[i % LineChartCurveType.values().length],
					i % 3 + 2,
					LINE_COLORS[i % LINE_COLORS.length],
					LINE_COLORS[i % LINE_COLORS.length],
					LINE_COLORS[i % LINE_COLORS.length].withAlpha(0),
					LINE_COLORS[i % LINE_COLORS.length].withAlpha(0)
			);
			lineGraph.setYAxisColor(LINE_COLORS[i % LINE_COLORS.length]);
			lineGraph.setYScaleZoomMode(LineChartYScaleZoomMode.DYNAMIC);
			lineGraph.setYScaleType(ScaleType.LINEAR);
			lineGraph.setDisplayedIntervalY(new Interval(intervalYMin, intervalYMax));
			lineGraph.setYAxisLabel("Graph " + i);
			lineGraph.setMaxTickDigits((i % 3) + 2);
			lines.add(lineGraph);
		}

		final HoseGraph hoseGraph = new HoseGraph(new DelegatingHoseModel(
				models.get(6, AggregationType.MIN),
				models.get(6, AggregationType.AVERAGE),
				models.get(6, AggregationType.MAX)
		), LineChartCurveType.MONOTONE, 3, RgbaColor.MATERIAL_RED_900, RgbaColor.MATERIAL_RED_900.withAlpha(.2f));

//		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(()->models.get(6, AggregationType.MIN).onDataChanged.fire(), 1, 1, TimeUnit.SECONDS);

		hoseGraph.setYAxisColor(RgbaColor.MATERIAL_RED_900);
		this.hoseGraph = hoseGraph;

		final GraphGroup graphGroup = new GraphGroup(lines.get(0), lines.get(5), lines.get(7));
		graphGroup.setYAxisColor(RgbaColor.MATERIAL_BLUE_800);
		this.graphGroup = graphGroup;

		final IncidentGraph incidentGraph = new IncidentGraph(new AbstractIncidentGraphModel() {
			@Override
			public Interval getDomainX() {
				return new Interval(START, END);
			}

			@Override
			public IncidentGraphData getData(TimePartitioning zoomLevel, ZoneId zoneId, Interval neededInterval, Interval displayedInterval) {
				return new IncidentGraphData() {

					private final List<IncidentGraphDataPoint> data = List.of(
							new IncidentGraphDataPoint(3_600_000, 2 * 7_200_000, 5, Color.fromHtmlString("#ffbf00"), "<b>WAF_ASDF_0</b>: 2021-07-20 17:35 - 2021-07-20 18:00"),
							new IncidentGraphDataPoint(7_200_000, 7_200_000, 10, Color.fromHtmlString("#ff6b00"), "<b>WAF_ASDF_1</b>: 2021-07-20 17:35 - 2021-07-20 18:00"),
							new IncidentGraphDataPoint(30_000_000, 40_000_000, 10, Color.fromHtmlString("#ff6b00"), "<b>WAF_ASDF_2</b>: 2021-07-20 17:35 - 2021-07-20 18:00"),
							new IncidentGraphDataPoint(20_000_000, 45_000_000, 15, Color.fromHtmlString("#f50000"), "<b>WAF_ASDF_3</b>: 2021-07-20 17:35 - 2021-07-20 18:00")
					);

					@Override
					public int size() {
						return data.size();
					}

					@Override
					public IncidentGraphDataPoint getDataPoint(int index) {
						return data.get(index);
					}

					@Override
					public Interval getInterval() {
						return new Interval(
								data.stream().mapToLong(incidentGraphDataPoint -> (long) incidentGraphDataPoint.getX1()).min().orElse(0),
								data.stream().mapToLong(incidentGraphDataPoint -> (long) incidentGraphDataPoint.getX2()).max().orElse(0) + 1
						);
					}
				};
			}
		});
		incidentGraph.setYAxisVisible(false);
		this.incidentGraph = incidentGraph;
	}

	private LineGraphData createLineGraphData(Function<Double, Double> function) {
		double pointDistance = ((double) (long) END - (long) START) / NUMBER_OF_DATA_POINTS;
		final List<LineGraphDataPoint> dataPoints = IntStream.range(0, NUMBER_OF_DATA_POINTS)
				.mapToObj(i -> {
					double x = (long) START + i * pointDistance;
					double y = function.apply(x);
					return new LineGraphDataPoint(x, y);
				})
				.collect(Collectors.toList());
		return new ListLineGraphData(dataPoints, Interval.empty());
	}


	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		var fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Zoom levels", fieldGenerator.createTagComboBoxForEnum("zoomLevels", TimePartitioningUnit.class));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Max pixels between data points",
				fieldGenerator.createNumberField("maxPixelsBetweenDataPoints", 0, 1, 500, false));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "locale",
				fieldGenerator.createComboBoxForList("uLocale", Arrays.asList(ULocale.GERMANY, ULocale.US)));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Time Zone",
				fieldGenerator.createComboBoxForList("timeZoneId", Arrays.asList(ZoneId.of("UTC"), ZoneId.of("Europe/Berlin"), ZoneId.of("America/New_York"))));

		NumberField numberOfSeriesField = new NumberField(0);
		numberOfSeriesField.setMinValue(0);
		numberOfSeriesField.setMaxValue(MAX_NUMBER_OF_SERIES);
		numberOfSeriesField.setSliderMode(NumberFieldSliderMode.VISIBLE);
		numberOfSeriesField.setSliderStep(1);
		numberOfSeriesField.onValueChanged.addListener(numberOfSeries -> {
			this.numberOfSeries = numberOfSeries.intValue();
			updateLineDefinitions(getComponent());
		});
		numberOfSeriesField.setValue(numberOfSeries);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Number of series", numberOfSeriesField);


		ComboBox<LineChartMouseScrollZoomPanMode> mouseScrollZoomPanModeComboBox = ComboBox.createForEnum(LineChartMouseScrollZoomPanMode.class
		);
		mouseScrollZoomPanModeComboBox.onValueChanged.addListener(mouseScrollZoomPanMode -> {
			this.mouseScrollZoomPanMode = mouseScrollZoomPanMode;
			printInvocationToConsole("setMouseScrollZoomPanMode", mouseScrollZoomPanMode);
			getComponent().setMouseScrollZoomPanMode(mouseScrollZoomPanMode);
		});
		mouseScrollZoomPanModeComboBox.setValue(this.mouseScrollZoomPanMode);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Mouse zoom/pan mode", mouseScrollZoomPanModeComboBox);

		CheckBox showHoseGraphCheckBox = new CheckBox();
		showHoseGraphCheckBox.onValueChanged.addListener(checked -> {
			showLineChartHose = checked;
			updateLineDefinitions(getComponent());
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINE_STYLE, "Show Hose Graph", showHoseGraphCheckBox);

		CheckBox showIncidentGraphCheckBox = new CheckBox();
		showIncidentGraphCheckBox.onValueChanged.addListener(checked -> {
			showIncidentGraph = checked;
			updateLineDefinitions(getComponent());
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINE_STYLE, "Show Incident Graph", showIncidentGraphCheckBox);

		CheckBox showDisplayGroupCheckbox = new CheckBox();
		showDisplayGroupCheckbox.onValueChanged.addListener(checked -> {
			showDisplayGroup = checked;
			updateLineDefinitions(getComponent());
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINE_STYLE, "Show Display Group", showDisplayGroupCheckbox);

		for (int i = 0; i < MAX_NUMBER_OF_SERIES; i++) {
			responsiveFormLayout.addSection(MaterialIcon.HELP, "Line " + (i + 1 + " format"))
					.setCollapsed(true);
			AbstractGraph lineFormat = lines.get(i);

			// Color lineColorScaleMin = new Color(73, 128, 192);
			// Color lineColorScaleMax = new Color(73, 128, 192);
			// Color areaColorScaleMin = new Color(255, 255, 255, 0);
			// Color areaColorScaleMax = new Color(255, 255, 255, 0);

			if (lineFormat instanceof LineGraph) {
				LineGraph line = (LineGraph) lineFormat;

				ComboBox<LineChartCurveType> graphTypeComboBox = ComboBox.createForEnum(LineChartCurveType.class);
				graphTypeComboBox.onValueChanged.addListener(graphType -> {
					line.setGraphType(graphType);
				});
				graphTypeComboBox.setValue(line.getGraphType());
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Line type", graphTypeComboBox);

				NumberField dataDotRadiusNumberField = new NumberField(0)
						.setSliderMode(NumberFieldSliderMode.VISIBLE)
						.setMinValue(0)
						.setMaxValue(50);
				dataDotRadiusNumberField.onValueChanged.addListener(dataDotRadius -> {
					line.setDataDotRadius(dataDotRadius.floatValue());
				});
				dataDotRadiusNumberField.setValue(line.getDataDotRadius());
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Data dot radius", dataDotRadiusNumberField);

				ColorPicker lineColorScaleMinPicker = new ColorPicker();
				lineColorScaleMinPicker.onValueChanged.addListener(color -> {
					line.setLineColorScaleMin(color);
				});
				lineColorScaleMinPicker.setValue(line.getLineColorScaleMin());
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Line color (min)", lineColorScaleMinPicker);

				ColorPicker lineColorScaleMaxPicker = new ColorPicker();
				lineColorScaleMaxPicker.onValueChanged.addListener(color -> {
					line.setLineColorScaleMax(color);
				});
				lineColorScaleMaxPicker.setValue(line.getLineColorScaleMax());
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Line color (max)", lineColorScaleMaxPicker);

				ColorPicker areaColorScaleMinPicker = new ColorPicker();
				areaColorScaleMinPicker.onValueChanged.addListener(color -> {
					line.setAreaColorScaleMin(color);
				});
				areaColorScaleMinPicker.setValue(line.getAreaColorScaleMin());
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Area color (min)", areaColorScaleMinPicker);

				ColorPicker areaColorScaleMaxPicker = new ColorPicker();
				areaColorScaleMaxPicker.onValueChanged.addListener(color -> {
					line.setAreaColorScaleMax(color);
				});
				areaColorScaleMaxPicker.setValue(line.getAreaColorScaleMax());
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Area color (max)", areaColorScaleMaxPicker);

				var lineFieldGenerator = new ConfigurationFieldGenerator<>(lineFormat, getTestContext());

				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Y-axis color", lineFieldGenerator.createColorPicker("yAxisColor"));

				NumberField intervalYMinNumberField = new NumberField(0)
						.setSliderMode(NumberFieldSliderMode.VISIBLE)
						.setMinValue(-10_000)
						.setMaxValue(10_000);
				NumberField intervalYMaxNumberField = new NumberField(0)
						.setSliderMode(NumberFieldSliderMode.VISIBLE)
						.setMinValue(-10_000)
						.setMaxValue(10_000);

				ComboBox<LineChartYScaleZoomMode> yScaleZoomModeComboBox = lineFieldGenerator.createComboBoxForEnum("yScaleZoomMode", LineChartYScaleZoomMode.class);
				yScaleZoomModeComboBox.onValueChanged.addListener(yScaleZoomMode -> {
					boolean dynamicYScaleZooming = isDynamicYScaleZooming(yScaleZoomMode);
					intervalYMinNumberField.setEditingMode(dynamicYScaleZooming ? FieldEditingMode.DISABLED : FieldEditingMode.EDITABLE);
					intervalYMaxNumberField.setEditingMode(dynamicYScaleZooming ? FieldEditingMode.DISABLED : FieldEditingMode.EDITABLE);
				});
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Y scale zoom mode", yScaleZoomModeComboBox);

				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Y scale zoom mode", lineFieldGenerator.createComboBoxForEnum("yScaleType"));

				intervalYMinNumberField.onValueChanged.addListener(intervalYMin -> {
					this.intervalYMin = intervalYMin.longValue();
					Interval intervalY = new Interval(this.intervalYMin, this.intervalYMax);
					printInvocationToConsole("setIntervalY", intervalY);
					line.setDisplayedIntervalY(intervalY);

					if (this.intervalYMin > this.intervalYMax) {
						this.intervalYMax = this.intervalYMin + 100;
					}
				});
				intervalYMinNumberField.setValue(this.intervalYMin);
				intervalYMinNumberField.setEditingMode(isDynamicYScaleZooming(lineFormat.getYScaleZoomMode()) ? FieldEditingMode.DISABLED : FieldEditingMode.EDITABLE);
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Interval y (min)", intervalYMinNumberField);


				intervalYMaxNumberField.onValueChanged.addListener(intervalYMax -> {
					this.intervalYMax = intervalYMax.longValue();
					Interval intervalY = new Interval(this.intervalYMin, this.intervalYMax);
					printInvocationToConsole("setIntervalY", intervalY);
					line.setDisplayedIntervalY(intervalY);

					if (this.intervalYMin > this.intervalYMax) {
						this.intervalYMin = this.intervalYMax - 100;
					}
				});
				intervalYMaxNumberField.setValue(this.intervalYMax);
				intervalYMaxNumberField.setEditingMode(isDynamicYScaleZooming(lineFormat.getYScaleZoomMode()) ? FieldEditingMode.DISABLED : FieldEditingMode.EDITABLE);
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Interval y (max)", intervalYMaxNumberField);

				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Y zero line visible", lineFieldGenerator.createCheckBox("yZeroLineVisible"));
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "maxTickDigits", lineFieldGenerator.createNumberField("maxTickDigits", 0, 1, 20, false));
				responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "yAxisLabel", lineFieldGenerator.createTextField("yAxisLabel"));
			}
		}

	}

	private boolean isDynamicYScaleZooming(LineChartYScaleZoomMode yScaleZoomMode) {
		return yScaleZoomMode == LineChartYScaleZoomMode.DYNAMIC || yScaleZoomMode == LineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO;
	}

	@Override
	public TimeGraph createComponent() {
		TimeGraph timeGraph = new TimeGraph();
		timeGraph.setMaxPixelsBetweenDataPoints(50);
		updateLineDefinitions(timeGraph);
		return timeGraph;
	}

	private void updateLineDefinitions(TimeGraph timeGraph) {
		List<AbstractGraph<?, ?>> lineFormats = new ArrayList<>(lines.subList(0, numberOfSeries));
		if (showLineChartHose) {
			lineFormats.add(hoseGraph);
		}
		if (showIncidentGraph) {
			lineFormats.add(incidentGraph);
		}
		if (showDisplayGroup) {
			lineFormats.add(graphGroup);
		}
		printInvocationToConsole("setLines", lineFormats);
		timeGraph.setGraphs(lineFormats);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/TimeGraph.html";
	}

	public boolean isShowLineChartHose() {
		return showLineChartHose;
	}

	public void setShowLineChartHose(boolean showLineChartHose) {
		this.showLineChartHose = showLineChartHose;
	}

	public boolean isShowDisplayGroup() {
		return showDisplayGroup;
	}

	public void setShowDisplayGroup(boolean showDisplayGroup) {
		this.showDisplayGroup = showDisplayGroup;
	}
}
