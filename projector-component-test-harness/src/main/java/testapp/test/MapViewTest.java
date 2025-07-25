

package testapp.test;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.core.NumberFieldSliderMode;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.mapview.Location;
import org.teamapps.projector.component.mapview.MapView;
import org.teamapps.projector.component.mapview.Marker;
import org.teamapps.projector.component.mapview.shape.*;
import org.teamapps.projector.icon.composite.CompositeIcon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.VerticalElementAlignment;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapViewTest extends AbstractComponentTest<MapView<BaseTemplateRecord>> {

	private MapPolyline polyline;

	public MapViewTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Style Url", fieldGenerator.createComboBoxForList("styleUrl", Arrays.asList(
				"https://maps.teamportal.org/styles/klokantech-basic/style.json",
				"https://maps.teamportal.org/styles/osm-bright/style.json",
				"https://maps.teamportal.org/styles/dark-matter/style.json"
		)));


		NumberField zoomLevelField = fieldGenerator.createNumberField("zoomLevel", 0, 0, 20, false);
		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Zoom level", zoomLevelField);
		getComponent().onZoomLevelChanged.addListener(zoomLevel -> zoomLevelField.setValue(zoomLevel));

		NumberField latitudeField = new NumberField(2);
		latitudeField.setMinValue(-90);
		latitudeField.setMaxValue(90);
		latitudeField.setSliderMode(NumberFieldSliderMode.VISIBLE);
		latitudeField.onValueChanged.addListener(number -> {
			printInvocationToConsole("setLatitude", number);
			getComponent().setLatitude(number.doubleValue());
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Latitude", latitudeField);
		NumberField longitudeField = new NumberField(2);
		longitudeField.setMinValue(-180);
		longitudeField.setMaxValue(180);
		longitudeField.setSliderMode(NumberFieldSliderMode.VISIBLE);
		longitudeField.onValueChanged.addListener(number -> {
			printInvocationToConsole("setLongitude", number);
			getComponent().setLongitude(number.doubleValue());
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Longitude", longitudeField);
		getComponent().onLocationChanged.addListener(location -> {
			latitudeField.setValue(location.getCenter().getLatitude());
			longitudeField.setValue(location.getCenter().getLongitude());
		});
		latitudeField.setValue(getComponent().getLocation().getLatitude());
		longitudeField.setValue(getComponent().getLocation().getLongitude());

		Button markerButton = Button.create("Add Marker");
		markerButton.onClick.addListener(() -> {
			Location location = getComponent().getLocation();
			getComponent().addMarker(new Marker<>(new Location(location.getLatitude(), location.getLongitude()),
					new BaseTemplateRecord<>(MaterialIcon.PIN_DROP, "My marker"), -15, -10));
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, null, markerButton);

		Button clearMarkersButton = Button.create("Clear Markers");
		clearMarkersButton.onClick.addListener(() -> {
			getComponent().clearMarkers();
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, null, clearMarkersButton);

		Button addToPolyLineButton = Button.create("Add Point to PolyLine");
		addToPolyLineButton.onClick.addListener(() -> {
			Location location = getComponent().getLocation();
			polyline.addPoint(location);
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, null, addToPolyLineButton);

		Button clearShapesButton = Button.create("Clear shapes");
		clearShapesButton.onClick.addListener(() -> {
			getComponent().clearShapes();
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, null, clearShapesButton);

		Button clearClusterButton = Button.create("Clear cluster");
		clearClusterButton.onClick.addListener(() -> {
			getComponent().clearMarkerCluster();
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, null, clearClusterButton);

		Button setLocationButton = Button.create("setLocation to Berlin");
		setLocationButton.onClick.addListener(() -> {
			getComponent().setLocation(new Location(52.3, 13.2), 1000, 13);
		});
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, null, setLocationButton);




//
//		ComboBox<MapShapeType> shapeSelectionComboBox = ComboBox.createForEnum(MapShapeType.class);
//		shapeSelectionComboBox.setValue(MapShapeType.RECTANGLE);
//		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Drawing shape", shapeSelectionComboBox);
//
//		Button startDrawingButton = Button.create("Start drawing");
//		startDrawingButton.onClick.addListener(() -> {
//			getComponent().startDrawingShape(shapeSelectionComboBox.getValue(), new ShapeProperties(RgbaColor.RED));
//		});
//		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, null, startDrawingButton);
//
//		Button stopDrawingButton = Button.create("Stop drawing");
//		stopDrawingButton.onClick.addListener(() -> {
//			getComponent().stopDrawingShape();
//		});
//		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, null, stopDrawingButton);
//
//		getComponent().onShapeDrawn.addListener(this.drawnShapes::add);
//		Button removeLastDrawnButton = Button.create("Remove last drawn shape");
//		removeLastDrawnButton.onClick.addListener(() -> {
//			AbstractMapShape shape = drawnShapes.get(drawnShapes.size() - 1);
//			drawnShapes.remove(shape);
//			shape.remove();
//		});
//		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, null, removeLastDrawnButton);
	}

	@Override
	public MapView<BaseTemplateRecord> createComponent() {
		MapView<BaseTemplateRecord> mapView = new MapView<>("https://maps.teamportal.org/", null, "https://maps.teamportal.org/styles/klokantech-basic/style.json");
		Template testTpl = BaseTemplates.createListStyleIconTwoLinesBadgeTemplate(50, VerticalElementAlignment.TOP, 200, 5);
		mapView.setDefaultMarkerTemplate(testTpl);

		polyline = new MapPolyline(Arrays.asList(
				new Location(-1, -1),
				new Location(1, 1),
				new Location(-1, 1),
				new Location(1, -1)
		), new ShapeProperties(RgbaColor.RED, 2));
		mapView.addPolyLine(polyline);

		mapView.addShape(new MapCircle(new Location(0, 0), 10, Color.RED, Color.GREEN, 3));

		mapView.addShape(new MapPolygon(Arrays.asList(
				new Location(-1, -1),
				new Location(1, 0),
				new Location(2, 2)
		), new ShapeProperties(Color.BLUE, 3, Color.WHITE.withAlpha(.5f))));

		mapView.addShape(new MapRectangle(new Location(-2, -2), new Location(-1, -1.5), new ShapeProperties(Color.GREEN, 3, Color.YELLOW.withAlpha(.5f))));

		mapView.onMapClicked.addListener(e -> {
			getComponent().addMarker(new Marker<>(new Location(e.getLatitude(), e.getLongitude()),
					new BaseTemplateRecord<>(MaterialIcon.PIN_DROP, "My marker"), -30, 13));
		});

		mapView.setMarkerCluster(IntStream.range(0, 100)
				.mapToObj(i -> new Marker<>(new Location(Math.random(), Math.random()), new BaseTemplateRecord(MaterialIcon.PIN_DROP, "My marker"), -30, 13))
				.collect(Collectors.toList()));

		return mapView;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/MapView.html";
	}

}
