package org.teamapps.ux.component.webrtc.apiclient;

public class WorkerLoadData {

	private double currentLoad;

	public WorkerLoadData(double currentLoad) {
		this.currentLoad = currentLoad;
	}

	public WorkerLoadData() {
	}

	public double getCurrentLoad() {
		return currentLoad;
	}

	public void setCurrentLoad(double currentLoad) {
		this.currentLoad = currentLoad;
	}
}
