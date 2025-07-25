

package testapp.test;

import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.panel.Panel;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.progress.Progress;
import org.teamapps.projector.component.progress.ProgressDisplay;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;

public class ProgressDisplayTest extends AbstractComponentTest<ProgressDisplay> {

	private final Progress progress = new Progress();

	public ProgressDisplayTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.STAR, "Icon", fieldGenerator.createComboBoxForIcon("icon"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.TEXT_FIELDS, "Task Name", fieldGenerator.createTextField("taskName"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.TEXT_FIELDS, "Status String", fieldGenerator.createTextField("statusMessage"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.ROTATE_RIGHT, "Status", fieldGenerator.createComboBoxForEnum("status"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.PLAY_ARROW, "Progress", fieldGenerator.createNumberField("progress", 2, -0.01, 1, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.DELETE, "Cancelable", fieldGenerator.createCheckBox("cancelable"));

		Button startButton = Button.create("executeBackgroundTask()");
		startButton.onClick.addListener(aBoolean -> {
			new Thread(() -> {
				int numberOfIterations = 5;
				for (int i = 0; i < numberOfIterations; i++) {
					if (progress.isCancellationRequested()) {
						progress.markCanceled();
						return;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progress.setProgress((double) (i + 1) / numberOfIterations, "Calculating " + (i + 1) + " of " + numberOfIterations + "...");
				}
			}).start();
			getComponent().setProgress(progress);
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.ADD, "SessionContext.executeBackgroundTask", startButton);

		Button cancelButton = Button.create("requestCancellation()");
		cancelButton.onClick.addListener(aBoolean -> {
			if (progress != null) {
				progress.requestCancellation();
			}
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.ADD, "ObservableProgress.requestCancellation()", cancelButton);
	}

	@Override
	public ProgressDisplay createComponent() {
		return new ProgressDisplay(MaterialIcon.ALARM, "My Task", progress);
	}

	@Override
	protected Component wrapComponent(ProgressDisplay component) {
		Panel panel = new Panel(null, null, component);
		panel.setTitleBarHidden(true);
		panel.setContentStretchingEnabled(false);
		panel.setPadding(10);
		return panel;
	}
}
