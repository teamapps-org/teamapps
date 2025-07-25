

package testapp.test;

import org.teamapps.projector.animation.PageTransition;
import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.field.DisplayField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.mobilelayout.MobileLayout;
import org.teamapps.projector.component.mobilelayout.NavigationBar;
import org.teamapps.projector.component.mobilelayout.NavigationBarButton;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.util.DemoDataGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.teamapps.projector.animation.PageTransition.*;

public class MobileLayoutTest extends AbstractComponentTest<MobileLayout> {

	private final List<PageTransition> forwardTransitions = Arrays.asList(
			MOVE_TO_LEFT_VS_MOVE_FROM_RIGHT,
			MOVE_TO_TOP_VS_MOVE_FROM_BOTTOM,
			FADE_VS_MOVE_FROM_RIGHT,
			FADE_VS_MOVE_FROM_BOTTOM,
			MOVE_TO_LEFT_FADE_VS_MOVE_FROM_RIGHT_FADE,
			MOVE_TO_TOP_FADE_VS_MOVE_FROM_BOTTOM_FADE,
			SCALE_DOWN_VS_MOVE_FROM_RIGHT,
			SCALE_DOWN_VS_MOVE_FROM_BOTTOM,
			SCALE_DOWN_VS_SCALE_UP_DOWN,
			MOVE_TO_LEFT_VS_SCALE_UP,
			MOVE_TO_TOP_VS_SCALE_UP,
			SCALE_DOWN_CENTER_VS_SCALE_UP_CENTER,
			ROTATE_RIGHT_SIDE_FIRST_VS_MOVE_FROM_RIGHT,
			ROTATE_TOP_SIDE_FIRST_VS_MOVE_FROM_TOP,
			FLIP_OUT_RIGHT_VS_FLIP_IN_LEFT,
			FLIP_OUT_TOP_VS_FLIP_IN_BOTTOM,
			ROTATE_FALL_VS_SCALE_UP,
			ROTATE_OUT_NEWSPAPER_VS_ROTATE_IN_NEWSPAPER,
			ROTATE_PUSH_RIGHT_VS_MOVE_FROM_LEFT,
			ROTATE_PUSH_BOTTOM_VS_MOVE_FROM_TOP,
			ROTATE_PUSH_RIGHT_VS_ROTATE_PULL_LEFT,
			ROTATE_PUSH_BOTTOM_VS_ROTATE_PULL_TOP,
			ROTATE_FOLD_RIGHT_VS_MOVE_FROM_LEFT_FADE,
			ROTATE_FOLD_BOTTOM_VS_MOVE_FROM_TOP_FADE,
			MOVE_TO_LEFT_FADE_VS_ROTATE_UNFOLD_RIGHT,
			MOVE_TO_TOP_FADE_VS_ROTATE_UNFOLD_BOTTOM,
			ROTATE_ROOM_RIGHT_OUT_VS_ROTATE_ROOM_RIGHT_IN,
			ROTATE_ROOM_TOP_OUT_VS_ROTATE_ROOM_TOP_IN,
			ROTATE_CUBE_RIGHT_OUT_VS_ROTATE_CUBE_RIGHT_IN,
			ROTATE_CUBE_BOTTOM_OUT_VS_ROTATE_CUBE_BOTTOM_IN,
			ROTATE_CAROUSEL_RIGHT_OUT_VS_ROTATE_CAROUSEL_RIGHT_IN,
			ROTATE_CAROUSEL_BOTTOM_OUT_VS_ROTATE_CAROUSEL_BOTTOM_IN,
			ROTATE_SIDES_OUT_VS_ROTATE_SIDES_IN,
			ROTATE_SLIDE_OUT_VS_ROTATE_SLIDE_IN
	);
	private final List<PageTransition> backwardTransitions = Arrays.asList(
			MOVE_TO_RIGHT_VS_MOVE_FROM_LEFT,
			MOVE_TO_BOTTOM_VS_MOVE_FROM_TOP,
			FADE_VS_MOVE_FROM_LEFT,
			FADE_VS_MOVE_FROM_TOP,
			MOVE_TO_RIGHT_FADE_VS_MOVE_FROM_LEFT_FADE,
			MOVE_TO_BOTTOM_FADE_VS_MOVE_FROM_TOP_FADE,
			SCALE_DOWN_VS_MOVE_FROM_LEFT,
			SCALE_DOWN_VS_MOVE_FROM_TOP,
			SCALE_DOWN_UP_VS_SCALE_UP,
			MOVE_TO_RIGHT_VS_SCALE_UP,
			MOVE_TO_BOTTOM_VS_SCALE_UP,
			SCALE_DOWN_CENTER_VS_SCALE_UP_CENTER,
			ROTATE_LEFT_SIDE_FIRST_VS_MOVE_FROM_LEFT,
			ROTATE_BOTTOM_SIDE_FIRST_VS_MOVE_FROM_BOTTOM,
			FLIP_OUT_LEFT_VS_FLIP_IN_RIGHT,
			FLIP_OUT_BOTTOM_VS_FLIP_IN_TOP,
			ROTATE_FALL_VS_SCALE_UP,
			ROTATE_OUT_NEWSPAPER_VS_ROTATE_IN_NEWSPAPER,
			ROTATE_PUSH_LEFT_VS_MOVE_FROM_RIGHT,
			ROTATE_PUSH_TOP_VS_MOVE_FROM_BOTTOM,
			ROTATE_PUSH_LEFT_VS_ROTATE_PULL_RIGHT,
			ROTATE_PUSH_TOP_VS_ROTATE_PULL_BOTTOM,
			ROTATE_FOLD_LEFT_VS_MOVE_FROM_RIGHT_FADE,
			ROTATE_FOLD_TOP_VS_MOVE_FROM_BOTTOM_FADE,
			MOVE_TO_RIGHT_FADE_VS_ROTATE_UNFOLD_LEFT,
			MOVE_TO_BOTTOM_FADE_VS_ROTATE_UNFOLD_TOP,
			ROTATE_ROOM_LEFT_OUT_VS_ROTATE_ROOM_LEFT_IN,
			ROTATE_ROOM_BOTTOM_OUT_VS_ROTATE_ROOM_BOTTOM_IN,
			ROTATE_CUBE_LEFT_OUT_VS_ROTATE_CUBE_LEFT_IN,
			ROTATE_CUBE_TOP_OUT_VS_ROTATE_CUBE_TOP_IN,
			ROTATE_CAROUSEL_LEFT_OUT_VS_ROTATE_CAROUSEL_LEFT_IN,
			ROTATE_CAROUSEL_TOP_OUT_VS_ROTATE_CAROUSEL_TOP_IN,
			ROTATE_SIDES_OUT_VS_ROTATE_SIDES_IN,
			ROTATE_SLIDE_OUT_VS_ROTATE_SLIDE_IN
	);

	private int contentIndex = 0;
	private int transitionIndex = 0;

	public MobileLayoutTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

	}

	@Override
	public MobileLayout createComponent() {
		MobileLayout mobileLayout = new MobileLayout();
		List<Component> contents = IntStream.rangeClosed(1, DemoDataGenerator.FOREGROUND_COLORS.size()).mapToObj(i -> createDisplayField(i)).collect(Collectors.toList());
		mobileLayout.setContent(contents.get(0));

		NavigationBar navigationBar = new NavigationBar();
		navigationBar.setBackgroundColor(RgbaColor.MATERIAL_GREY_100);
		navigationBar.setBorderColor(RgbaColor.MATERIAL_GREY_300);

		NavigationBarButton backButton = NavigationBarButton.create(MaterialIcon.ARROW_BACK);
		backButton.onClick.addListener(() -> {
			PageTransition transition = backwardTransitions.get(Math.floorMod(transitionIndex--, backwardTransitions.size()));
			mobileLayout.setContent(contents.get(Math.floorMod(--contentIndex, contents.size())), transition, 500);
			getTestContext().printLineToConsole("Transition: " + transition);
		});
		navigationBar.addButton(backButton);
		NavigationBarButton forwardButton = NavigationBarButton.create(MaterialIcon.ARROW_FORWARD);
		forwardButton.onClick.addListener(() -> {
			PageTransition transition = forwardTransitions.get(Math.floorMod(++transitionIndex, forwardTransitions.size()));
			mobileLayout.setContent(contents.get(Math.floorMod(++contentIndex, contents.size())), transition, 500);
			getTestContext().printLineToConsole("Transition: " + transition);
		});
		navigationBar.addButton(forwardButton);

		mobileLayout.setNavigationBar(navigationBar);

		return mobileLayout;
	}

	private DisplayField createDisplayField(final int i) {
		DisplayField field = new DisplayField();
		field.setShowHtml(true);
		Color color = DemoDataGenerator.FOREGROUND_COLORS.get(i % DemoDataGenerator.FOREGROUND_COLORS.size());
		field.setValue("<div style=\"height: 100%; display: flex; align-items: center; justify-content: center; background-color: " + color.toHtmlColorString() + "; background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAIElEQVQYV2NkYGDgYcAEX9CFGIeIQix+wfQgyDODXSEAFqcFR3lM5YsAAAAASUVORK5CYII=); background-size: 40px 40px; background-repeat: repeat\">Content" + i + "</div>");
		field.setCssStyle(".UiDisplayField", "height", "100%");
		return field;
	}


	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/NavigationBar.html";
	}

}
