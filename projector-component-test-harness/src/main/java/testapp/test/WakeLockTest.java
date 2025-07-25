package testapp.test;

import org.teamapps.commons.databinding.TwoWayBindableValue;
import org.teamapps.commons.databinding.TwoWayBindableValueImpl;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.DisplayField;
import org.teamapps.projector.component.field.FieldEditingMode;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.session.WakeLock;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;

public class WakeLockTest extends AbstractComponentTest<DisplayField> {

	private final TwoWayBindableValue<WakeLock> wakeLock = new TwoWayBindableValueImpl<>();

	public WakeLockTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		var requestWakeLockButton = Button.create("request");
		requestWakeLockButton.onClick.addListener(() -> {
			getSessionContext().requestWakeLock().handle((wLock, throwable) -> {
				if (throwable != null) {
					getTestContext().printLineToConsole("Could not acquire wake lock!");
				} else {
					getTestContext().printLineToConsole("Successfully acquired wake lock!");
					this.wakeLock.set(wLock);
				}
				return null;
			});
		});
		responsiveFormLayout.addLabelField(requestWakeLockButton);
		wakeLock.bindWritingTo(wl -> requestWakeLockButton.setEditingMode(wl == null ? FieldEditingMode.EDITABLE : FieldEditingMode.DISABLED));

		var releaseWakeLockButton = Button.create("release");
		releaseWakeLockButton.onClick.addListener(() -> {
			wakeLock.get().release();
			wakeLock.set(null);
			getTestContext().printLineToConsole("Wake lock released.");
		});
		responsiveFormLayout.addLabelField(releaseWakeLockButton);
		wakeLock.bindWritingTo(wl -> releaseWakeLockButton.setEditingMode(wl != null ? FieldEditingMode.EDITABLE : FieldEditingMode.DISABLED));
	}

	@Override
	public DisplayField createComponent() {
		DisplayField displayField = new DisplayField();
		displayField.setValue("See configurations section for buttons. Also check log window.");
		return displayField;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/WakeLock.html";
	}

}
