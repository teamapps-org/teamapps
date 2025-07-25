package testapp.test;

import org.teamapps.projector.component.core.div.Div;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;

public class SessionContextTest extends AbstractComponentTest<Div> {

	public SessionContextTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.CALL_TO_ACTION, "Actions");

		TextField urlField = new TextField();
		responsiveFormLayout.addLabelAndField(MaterialIcon.LINK, "URL", urlField);
		Button navigationPushButton = Button.create("Push to navigation history entry");
		responsiveFormLayout.addLabelAndField(navigationPushButton);
		navigationPushButton.onClick.addListener(() -> {
		   getSessionContext().pushNavigationHistoryState(urlField.getValue(), true);
		});
		Button backButton = Button.create("Navigate back");
		responsiveFormLayout.addLabelAndField(backButton);
		backButton.onClick.addListener(() -> {
		   getSessionContext().navigateBack(1);
		});
		Button forwardButton = Button.create("Navigate forward");
		responsiveFormLayout.addLabelAndField(forwardButton);
		forwardButton.onClick.addListener(() -> {
			getSessionContext().navigateForward(1);
		});
	}

	@Override
	protected Div createComponent() {
		getSessionContext().onNavigationStateChange.addListener((historyElement, disposable) -> {
			printLineToConsole("onUserNavigation: " + historyElement);
		});
		return new Div();
	}
}
