package testapp;

import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;

public class ComponentTestTreeNode extends BaseTemplateRecord<Void> {

	private final String basePathName;
	private final ComponentTest<?> componentTest;

	public ComponentTestTreeNode(Icon icon, String caption, String description, String basePathName, ComponentTest<?> componentTest) {
		super(icon, caption, description);
		this.basePathName = basePathName;
		this.componentTest = componentTest;
	}

	public String getBasePathName() {
		return basePathName;
	}

	public ComponentTest<?> getComponentTest() {
		return componentTest;
	}
}
