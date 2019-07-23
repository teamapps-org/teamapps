package org.teamapps.ux.servlet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.uisession.SessionClosingReason;
import org.teamapps.uisession.TeamAppsUiSessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LeaveBeaconServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServletRequestListener.class);
	private final TeamAppsUiSessionManager uiSessionManager;

	public LeaveBeaconServlet(TeamAppsUiSessionManager uiSessionManager) {
		this.uiSessionManager = uiSessionManager;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sessionId = IOUtils.toString(request.getReader());
		LOGGER.info("Got leaving beacon for SessionId: " + sessionId);
		uiSessionManager.closeSession(new QualifiedUiSessionId(request.getSession().getId(), sessionId), SessionClosingReason.TERMINATED_BY_CLIENT);
	}
}
