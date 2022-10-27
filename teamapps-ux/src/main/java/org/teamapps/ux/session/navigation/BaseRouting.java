/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.ux.session.navigation;

import jakarta.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.uri.UriTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.teamapps.ux.session.navigation.RoutingUtil.concatenatePaths;
import static org.teamapps.ux.session.navigation.RoutingUtil.normalizePath;

public class BaseRouting implements Router {

	static final String PATH_REMAINDER_VARNAME = "_remainder";
	static final String PATH_REMAINDER_SUFFIX = "{" + PATH_REMAINDER_VARNAME + ":(/.*)?}";

	private final String pathPrefix;
	private final ParamConverterProvider paramConverterProvider;
	private final List<UriTemplateAndHandler> handlers = new CopyOnWriteArrayList<>(); // handler execution might add more handlers => prevent ConcurrentModificationException
	private Location currentLocation;

	public BaseRouting(String pathPrefix, ParamConverterProvider paramConverterProvider, Location location) {
		this.pathPrefix = normalizePath(pathPrefix);
		this.paramConverterProvider = paramConverterProvider;
		this.currentLocation = location;
	}

	@Override
	public RoutingHandlerRegistration registerRoutingHandler(String pathTemplate, boolean exact, RoutingHandler handler, boolean applyImmediately) {
		var uriTemplate = new UriTemplate(concatenatePaths(pathPrefix, pathTemplate) + (exact ? "" : PATH_REMAINDER_SUFFIX));
		UriTemplateAndHandler templateAndRouter = new UriTemplateAndHandler(uriTemplate, handler);
		handlers.add(templateAndRouter);
		if (applyImmediately) {
			routeInternal(currentLocation, List.of(templateAndRouter));
		}
		return new RoutingHandlerRegistration() {
			@Override
			public String createPath(Map<String, String> params) {
				return uriTemplate.createURI(params);
			}

			@Override
			public void dispose() {
				handlers.remove(templateAndRouter);
			}
		};
	}

	@Override
	public Router createSubRouter(String relativePath) {
		return new SubRouter(concatenatePaths(pathPrefix, relativePath));
	}

	public void route(Location location) {
		currentLocation = location;
		routeInternal(location, handlers);
	}

	private void routeInternal(Location location, List<UriTemplateAndHandler> handlers) {
		String relativePath = normalizePath(location.getPathname());
		Map<String, String> queryParams = RoutingUtil.parseQueryParams(location.getSearch());

		for (UriTemplateAndHandler uriTemplateAndHandler : handlers) {
			HashMap<String, String> pathParams = new HashMap<>();
			boolean matches = uriTemplateAndHandler.getUriTemplate().match(relativePath, pathParams);
			if (matches) {
				uriTemplateAndHandler.getHandler().handle(relativePath, pathParams, queryParams);
			}
		}
	}

	public ParamConverterProvider getParamConverterProvider() {
		return paramConverterProvider;
	}

	private static class UriTemplateAndHandler {
		private final UriTemplate uriTemplate;
		private final RoutingHandler handler;

		public UriTemplateAndHandler(UriTemplate uriTemplate, RoutingHandler handler) {
			this.uriTemplate = uriTemplate;
			this.handler = handler;
		}

		public UriTemplate getUriTemplate() {
			return uriTemplate;
		}

		public RoutingHandler getHandler() {
			return handler;
		}
	}
}
