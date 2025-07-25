package testapp;

import org.teamapps.commons.util.ReflectionUtil;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.gridform.AbstractGridForm;
import org.teamapps.projector.component.gridform.ResponsiveForm;
import org.teamapps.projector.component.gridform.ResponsiveFormConfigurationTemplate;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.session.SessionContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static testapp.util.ReflectionUtil.toStringUsingReflection;

public abstract class AbstractComponentTest<C extends Component> implements ComponentTest<C> {
	private final SessionContext sessionContext;
	private final ComponentTestContext testContext;
	private AbstractGridForm<?> parametersForm;
	private C component;
	private Component wrappedComponent;

	public AbstractComponentTest(ComponentTestContext testContext) {
		this.sessionContext = testContext.getSessionContext();
		this.testContext = testContext;
	}

	@Override
	public Component getParametersComponent() {
		if (parametersForm == null) {
			this.parametersForm = this.createParametersForm();
		}
		return this.parametersForm;
	}

	protected AbstractGridForm<?> createParametersForm() {
		ResponsiveFormConfigurationTemplate defaultTwoColumnTemplate = ResponsiveFormConfigurationTemplate.createDefaultTwoColumnTemplate(200, 0);
		ResponsiveForm<HashMap> form = new ResponsiveForm<>(defaultTwoColumnTemplate);
		ResponsiveFormLayout responsiveFormLayout = form.addResponsiveFormLayout(400);

		responsiveFormLayout.addSection(MaterialIcon.HELP, "General").setGridGap(5)
				.setCollapsed(true);
		ConfigurationFieldGenerator<Component> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Visible", fieldGenerator.createCheckBox("visible"));
		addFieldsToParametersForm(responsiveFormLayout);

		return form;
	}

	protected abstract void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout);

	public SessionContext getSessionContext() {
		return sessionContext;
	}

	public ComponentTestContext getTestContext() {
		return testContext;
	}

	protected void printLineToConsole(String s) {
		getTestContext().printLineToConsole(s);
	}

	protected void printInvocationToConsole(String methodName, Object... args) {
		getTestContext().printInvocationToConsole(methodName, args);
	}

	@Override
	public C getComponent() {
		if (component == null) {
			component = createComponent();
			wrappedComponent = wrapComponent(this.component);
			getComponentsToMonitor().forEach((displayName, component) -> {
				ReflectionUtil.findFields(component.getClass(), field -> ProjectorEvent.class.isAssignableFrom(field.getType()))
						.forEach(field -> {
							((ProjectorEvent) ReflectionUtil.readField(component, field, true)).addListener(o -> {
								printLineToConsole(displayName + " EVENT: " + field.getName() + "(" + toStringUsingReflection(o) + ")");
							});
						});
			});
		}
		return component;
	}

	@Override
	public final Component getWrappedComponent() {
		if (wrappedComponent == null) {
			getComponent();
		}
		return wrappedComponent;
	}

	protected abstract C createComponent();

	protected Component wrapComponent(C component) {
		return component;
	}

	protected Map<String, Component> getComponentsToMonitor() {
		return Collections.singletonMap("Component", getComponent());
	}
}
