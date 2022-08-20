package org.teamapps.ux.servlet.component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamapps.ux.component.ComponentLibrary;
import org.teamapps.ux.resource.Resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentLibraryResourceServlet extends HttpServlet {

	private final Map<String, ComponentLibrary> componentLibraryById = new ConcurrentHashMap<>();
	private final Map<String, String> hashByPath = new ConcurrentHashMap<>();
	private final Map<String, String> pathByResourceHash = new ConcurrentHashMap<>();


	public void registerComponentLibrary(String libraryId, ComponentLibrary componentLibrary) {
		componentLibraryById.put(libraryId, componentLibrary);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo.startsWith("/")) {
			pathInfo = pathInfo.substring(1);
		}
		String componentLibraryId;
		String resourcePath;
		if (pathInfo.contains("/")) {
			componentLibraryId = pathInfo.substring(0, pathInfo.indexOf('/'));
			resourcePath = pathInfo.substring(pathInfo.indexOf('/') + 1);
		} else {
			componentLibraryId = pathInfo;
			resourcePath = null;
		}

		ComponentLibrary componentLibrary = componentLibraryById.get(componentLibraryId);
		if (componentLibrary == null) {
			resp.setStatus(404);
			return;
		}

		if (resourcePath == null) {
			componentLibrary.getMainJsResource().getInputStream().transferTo(resp.getOutputStream());
		}

		String hash = hashByPath.computeIfAbsent(pathInfo, s -> {
			Resource resource = componentLibrary.getResource(resourcePath);
			if (resource == null) {
				return null;
			} else {
				return getResourceHash(resource);
			}
		});

		pathByResourceHash.computeIfAbsent(hash, s -> {
			return s;
		})
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
