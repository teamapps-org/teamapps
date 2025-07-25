

package testapp.test;

import org.teamapps.projector.animation.AnimationEasing;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.absolutelayout.AbsoluteLayout;
import org.teamapps.projector.component.core.absolutelayout.AbsolutePosition;
import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.composite.CompositeIcon;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbsoluteLayoutTest extends AbstractComponentTest<AbsoluteLayout> {

	private final List<Map<Component, AbsolutePosition>> layouts = new ArrayList<>();
	private int currentLayoutIndex = 0;


	public AbsoluteLayoutTest(ComponentTestContext testContext) {
		super(testContext);

		Component component1 = new DummyComponent();
		Component component2 = new DummyComponent();
		Component component3 = new DummyComponent();
		Component component4 = new DummyComponent();

		Map<Component, AbsolutePosition> componentPositions1 = new HashMap<>();
		componentPositions1.put(component1, AbsolutePosition.fullSizeAsRelativeDimensions(0));
		componentPositions1.put(component2, AbsolutePosition.fromRelativeTopRightBottomLeft(60, 0, 0, 60, 1));
		layouts.add(componentPositions1);

		Map<Component, AbsolutePosition> componentPositions2 = new HashMap<>();
		componentPositions2.put(component1, AbsolutePosition.fullSizeAsRelativeDimensions(0));
		componentPositions2.put(component2, AbsolutePosition.fromRelativeDimensions(70, 4, 44, 35, 1));
		componentPositions2.put(component3, AbsolutePosition.fromRelativeDimensions(70, 52, 44, 35, 1));
		layouts.add(componentPositions2);

		Map<Component, AbsolutePosition> componentPositions3 = new HashMap<>();
		componentPositions3.put(component1, AbsolutePosition.fromRelativeDimensions(0, 0, 50, 50, 1));
		componentPositions3.put(component2, AbsolutePosition.fromRelativeDimensions(50, 0, 50, 50, 1));
		componentPositions3.put(component3, AbsolutePosition.fromRelativeDimensions(0, 50, 50, 50, 1));
		componentPositions3.put(component4, AbsolutePosition.fromRelativeDimensions(50, 50, 50, 50, 1));
		layouts.add(componentPositions3);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration");

		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(this.getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Animation duration", fieldGenerator.createNumberField("animationDuration", 0, 0, 2000, false));
		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Animation easing", fieldGenerator.createComboBoxForEnum("animationEasing", AnimationEasing.class));

		Button previousButton = Button.create(MaterialIcon.HELP, "Previous");
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Previous", previousButton);
		previousButton.onClick.addListener(() -> {
			Map<Component, AbsolutePosition> layout = layouts.get(--currentLayoutIndex % layouts.size());
			getComponent().putComponents(layout, true);
		});

		Button nextButton = Button.create(MaterialIcon.HELP, "Next");
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Next", nextButton);
		nextButton.onClick.addListener(() -> {
			Map<Component, AbsolutePosition> layout = layouts.get(++currentLayoutIndex % layouts.size());
			getComponent().putComponents(layout, true);
		});
	}

	@Override
	public AbsoluteLayout createComponent() {
		AbsoluteLayout absoluteLayout = new AbsoluteLayout();
		absoluteLayout.putComponents(layouts.get(currentLayoutIndex), true);
		return absoluteLayout;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return null;
	}
}
