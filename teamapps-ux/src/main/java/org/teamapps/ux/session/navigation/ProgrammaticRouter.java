package org.teamapps.ux.session.navigation;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.uri.UriTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgrammaticRouter implements HierarchicalRouter {

	private static final String PATH_REMAINDER_VARNAME = "_remainder";
	private static final String PATH_REMAINDER_SUFFIX = "{" + PATH_REMAINDER_VARNAME + ":.*}";

	protected final UriTemplate uriTemplate;
	protected final RoutingSideEffect sideEffect;

	private final List<Router> subRouters = new ArrayList<>();

	public ProgrammaticRouter(String pathTemplate) {
		this(pathTemplate, (path, pathParams, queryParams) -> {}, true);
	}

	public ProgrammaticRouter(String pathTemplate, RoutingSideEffect sideEffect) {
		this(pathTemplate, sideEffect, true);
	}

	public ProgrammaticRouter(String pathTemplate, RoutingSideEffect sideEffect, boolean wildcardSuffix) {
		if (wildcardSuffix) {
			pathTemplate = pathTemplate + PATH_REMAINDER_SUFFIX;
		}
		this.uriTemplate = new UriTemplate(pathTemplate);
		this.sideEffect = sideEffect;
	}

	@Override
	public boolean route(String path, Map<String, String> queryParams) {
		RoutingUtil.MatchingResult result = RoutingUtil.match(uriTemplate, path);
		if (result.isMatch()) {
			String pathRemainder = result.getPathParams().remove(PATH_REMAINDER_VARNAME);

			sideEffect.apply(path, result.getPathParams(), queryParams);

			if (StringUtils.isNotBlank(pathRemainder) && !pathRemainder.startsWith("/")) {
				pathRemainder = "/" + pathRemainder;
			}
			invokeSubRouters(queryParams, pathRemainder);
		}
		return result.isMatch();
	}

	private void invokeSubRouters(Map<String, String> queryParams, String pathRemainder) {
		if (StringUtils.isNotBlank(pathRemainder)) {
			for (Router subRouter : subRouters) {
				boolean matched = subRouter.route(pathRemainder, queryParams);
				if (matched) {
					break;
				}
			}
		}
	}

	@Override
	public void addSubRouter(Router router) {
		subRouters.add(router);
	}

	public List<Router> getSubRouters() {
		return List.copyOf(subRouters);
	}
}
