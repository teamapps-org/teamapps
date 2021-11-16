package org.teamapps.ux.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IndexHtmlHeaderFilter extends HttpFilter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		if (servletResponse instanceof HttpServletResponse) {
			HttpServletResponse r = (HttpServletResponse) servletResponse;
			r.setHeader("cache-control", "max-age=0, no-cache, no-store, must-revalidate");
			r.setHeader("expires", "Thu, 01 Jan 1970 00:00:00 GMT");
			r.setHeader("pragma", "no-cache");
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}
}
