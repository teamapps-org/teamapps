package org.teamapps.app.multi;

public class ApplicationHandlerCaptions {

	private String applications = "Applications";
	private String logout = "Logout";
	private String home = "Home";
	private String search = "Search";

	public ApplicationHandlerCaptions() {
	}

	public ApplicationHandlerCaptions(String applications, String logout, String home, String search) {
		this.applications = applications;
		this.logout = logout;
		this.home = home;
		this.search = search;
	}

	public String getApplications() {
		return applications;
	}

	public void setApplications(String applications) {
		this.applications = applications;
	}

	public String getLogout() {
		return logout;
	}

	public void setLogout(String logout) {
		this.logout = logout;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
}
