package org.teamapps.ux.servlet.component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.clientobject.ComponentLibrary;
import org.teamapps.projector.clientobject.ComponentLibraryRegistry;
import org.teamapps.projector.resource.Resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ComponentLibraryResourceServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final String NOT_FOUND_HASH = "?NOT:FOUND";

	private final ComponentLibraryRegistry componentLibraryRegistry;
	private final Map<String, String> hashByPath = new ConcurrentHashMap<>();
	private final Map<String, String> pathByResourceHash = new ConcurrentHashMap<>();

	public ComponentLibraryResourceServlet(ComponentLibraryRegistry componentLibraryRegistry) {
		this.componentLibraryRegistry = componentLibraryRegistry;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("{}", req.getPathInfo());
		String pathInfo = req.getPathInfo();
		String componentLibraryId;
		String resourcePath;
		String pathInfoWithoutLeadingSlash =  pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
		if (pathInfoWithoutLeadingSlash.contains("/")) {
			componentLibraryId = pathInfoWithoutLeadingSlash.substring(0, pathInfoWithoutLeadingSlash.indexOf('/'));
			resourcePath = pathInfoWithoutLeadingSlash.substring(pathInfoWithoutLeadingSlash.indexOf('/'));
		} else {
			resp.setStatus(404);
			return;
		}

		ComponentLibrary componentLibrary = componentLibraryRegistry.getComponentLibraryById(componentLibraryId);
		if (componentLibrary == null) {
			resp.setStatus(404);
			return;
		}

		String fullPath = req.getServletPath() + req.getPathInfo();

		if ("/".equals(resourcePath)) {
			streamUniqueResource(fullPath, componentLibrary::getMainJsResource, resp);
			return;
		} else if (resourcePath.equals("/" + componentLibraryId + ".css")) {
			streamUniqueResource(fullPath, componentLibrary::getMainCssResource, resp);
			return;
		}

		streamUniqueResource(fullPath, () -> componentLibrary.getResource(resourcePath), resp);
	}

	private void streamUniqueResource(String fullPath, Supplier<Resource> inputStreamSupplier, HttpServletResponse resp) throws IOException {
		String hash = hashByPath.computeIfAbsent(fullPath, s -> {
			Resource resource = inputStreamSupplier.get();
			if (resource == null) {
				return NOT_FOUND_HASH;
			} else {
				return getResourceHash(resource);
			}
		});

		if (hash.equals(NOT_FOUND_HASH)) {
			resp.sendError(404);
		}

		String existingFullPath = pathByResourceHash.computeIfAbsent(hash, s -> fullPath);

		if (!fullPath.equals(existingFullPath)) {
			resp.sendRedirect(existingFullPath);
		} else {
			Resource resource = inputStreamSupplier.get();
			try(BufferedInputStream bis = new BufferedInputStream(resource.getInputStream())) {
				resp.setContentType(resource.getMimeType());
				bis.transferTo(resp.getOutputStream());
			}
		}
	}

	private static String getResourceHash(Resource resource) {
		MessageDigest digest = createDigest();
		byte[] buffer= new byte[8192];
		try(BufferedInputStream bis = new BufferedInputStream(resource.getInputStream())) {
			int count;
			while ((count = bis.read(buffer)) > 0) {
				digest.update(buffer, 0, count);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return bytesToHex(digest.digest());
	}


	private static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static void main(String[] args) {

	}

	private static MessageDigest createDigest() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
