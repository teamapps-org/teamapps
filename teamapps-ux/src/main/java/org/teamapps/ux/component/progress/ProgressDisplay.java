package org.teamapps.ux.component.progress;

import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiProgressDisplay;
import org.teamapps.event.Event;
import org.teamapps.event.EventListener;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.task.ObservableProgress;
import org.teamapps.ux.task.ProgressStatus;
import org.teamapps.ux.task.TaskFailureInfo;

/**
 * This component displays progress information.
 * <p>
 * It is typically used to visualize the progress of an {@link ObservableProgress}.
 * However, it can also be used manually, without attaching a progress.
 */
public class ProgressDisplay extends AbstractComponent {

	public final Event<Void> onClicked = new Event<>();
	public final Event<Void> onCancelButtonClicked = new Event<>();

	private Icon icon;
	private String taskName;
	private String statusString;
	private double progress;
	private ProgressStatus status = ProgressStatus.NOT_YET_STARTED;
	private boolean cancelable;

	private ObservableProgress observedProgress;
	private final EventListener<Void> observedProgressStartedHandler = v -> {
		System.out.println("from listener: RUNNING");
		setStatus(ProgressStatus.RUNNING);
	};
	private final EventListener<Double> observedProgressChangedListener = this::setProgress;
	private final EventListener<String> observedProgressStatusStringChangedListener = this::setStatusString;
	private final EventListener<Void> observedProgressCompletedListener = x -> setStatus(ProgressStatus.COMPLETE);
	private final EventListener<Void> observedProgressCancelationRequestedListener = x -> setStatus(ProgressStatus.CANCELLATION_REQUESTED);
	private final EventListener<Void> observedProgressCanceledListener = x -> setStatus(ProgressStatus.CANCELED);
	private final EventListener<TaskFailureInfo> observedProgressFailedListener = taskFailureInfo -> {
		this.statusString = taskFailureInfo.getMessage();
		this.status = ProgressStatus.FAILED;
		this.updateUi();
	};

	public ProgressDisplay() {
	}

	public ProgressDisplay(ObservableProgress progress) {
		setObservedProgress(progress);
	}

	@Override
	public UiProgressDisplay createUiComponent() {
		UiProgressDisplay ui = new UiProgressDisplay();
		mapAbstractUiComponentProperties(ui);
		ui.setIcon(getSessionContext().resolveIcon(icon));
		ui.setTaskName(taskName);
		ui.setStatusString(statusString);
		ui.setProgress(progress);
		ui.setStatus(status.toUiProgressStatus());
		ui.setCancelable(cancelable);
		return ui;
	}

	private void updateUi() {
		queueCommandIfRendered(() -> new UiProgressDisplay.UpdateCommand(getId(), createUiComponent()));
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_PROGRESS_DISPLAY_CLICKED: {
				UiProgressDisplay.ClickedEvent clickedEvent = (UiProgressDisplay.ClickedEvent) event;
				this.onClicked.fire(null);
				break;
			}
			case UI_PROGRESS_DISPLAY_CANCEL_BUTTON_CLICKED: {
				UiProgressDisplay.CancelButtonClickedEvent cancelButtonClickedEvent = (UiProgressDisplay.CancelButtonClickedEvent) event;
				if (this.observedProgress != null) {
					this.observedProgress.requestCancellation();
				}
				this.onCancelButtonClicked.fire(null);
				break;
			}

		}

	}

	public void setObservedProgress(ObservableProgress observableProgress) {
		if (this.observedProgress != null) {
			this.observedProgress.onStarted().removeListener(observedProgressStartedHandler);
			this.observedProgress.onProgressChanged().removeListener(observedProgressChangedListener);
			this.observedProgress.onStatusStringChanged().removeListener(observedProgressStatusStringChangedListener);
			this.observedProgress.onCompleted().removeListener(observedProgressCompletedListener);
			this.observedProgress.onCancellationRequested().removeListener(observedProgressCancelationRequestedListener);
			this.observedProgress.onCanceled().removeListener(observedProgressCanceledListener);
			this.observedProgress.onFailed().removeListener(observedProgressFailedListener);
		}

		this.observedProgress = observableProgress;

		if (observableProgress != null) {
			observableProgress.onStarted().addListener(observedProgressStartedHandler);
			observableProgress.onProgressChanged().addListener(observedProgressChangedListener);
			observableProgress.onStatusStringChanged().addListener(observedProgressStatusStringChangedListener);
			observableProgress.onCompleted().addListener(observedProgressCompletedListener);
			observableProgress.onCancellationRequested().addListener(observedProgressCancelationRequestedListener);
			observableProgress.onCanceled().addListener(observedProgressCanceledListener);
			observableProgress.onFailed().addListener(observedProgressFailedListener);

			this.icon = observableProgress.getIcon();
			this.taskName = observableProgress.getTaskName();
			this.statusString = observableProgress.getStatusString();
			this.progress = observableProgress.getProgress();
			this.status = observableProgress.getStatus();
			System.out.println("after registration: " + this.status);
			this.cancelable = observableProgress.isCancelable();
			this.updateUi();
		}
	}

	public ObservableProgress getObservedProgress() {
		return observedProgress;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		updateUi();
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
		updateUi();
	}

	public String getStatusString() {
		return statusString;
	}

	public void setStatusString(String statusString) {
		this.statusString = statusString;
		updateUi();
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
		updateUi();
	}

	public ProgressStatus getStatus() {
		return status;
	}

	public void setStatus(ProgressStatus status) {
		this.status = status;
		updateUi();
	}

	public boolean isCancelable() {
		return cancelable;
	}

	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
		updateUi();
	}


}
