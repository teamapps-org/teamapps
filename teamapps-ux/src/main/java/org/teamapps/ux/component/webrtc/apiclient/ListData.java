package org.teamapps.ux.component.webrtc.apiclient;

import java.util.List;

public class ListData {

	private List<String> list;

	public ListData(List<String> list) {
		this.list = list;
	}

	public ListData() {
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}
}
