package org.teamapps.projector.server.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.commons.util.ExceptionUtil;
import org.teamapps.projector.clientobject.ClientObjectLibrary;
import org.teamapps.projector.clientobject.ComponentLibraryRegistry;
import org.teamapps.projector.resource.Resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ComponentLibraryResourceServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final ComponentLibraryRegistry componentLibraryRegistry;
	private final Map<String, String> hashByPath = new ConcurrentHashMap<>();
	private final Map<String, String> pathByResourceHash = new ConcurrentHashMap<>();

	public ComponentLibraryResourceServlet(ComponentLibraryRegistry componentLibraryRegistry) {
		this.componentLibraryRegistry = componentLibraryRegistry;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			LOGGER.info("{} {}", req.getMethod(), req.getPathInfo());
			String pathInfo = req.getPathInfo();
			String componentLibraryId;
			String resourcePath;
			String pathInfoWithoutLeadingSlash = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
			if (pathInfoWithoutLeadingSlash.contains("/")) {
				componentLibraryId = pathInfoWithoutLeadingSlash.substring(0, pathInfoWithoutLeadingSlash.indexOf('/'));
				resourcePath = pathInfoWithoutLeadingSlash.substring(pathInfoWithoutLeadingSlash.indexOf('/'));
			} else {
				throw new NotFoundException();
			}

			ClientObjectLibrary componentLibrary = componentLibraryRegistry.getComponentLibraryById(componentLibraryId);
			if (componentLibrary == null) {
				throw new NotFoundException();
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
		} catch (NotFoundException notFoundException) {
			resp.setStatus(404);
		}
	}

	private void streamUniqueResource(String fullPath, Supplier<Resource> inputStreamSupplier, HttpServletResponse resp) throws IOException {
		String hash = hashByPath.computeIfAbsent(fullPath, s -> {
			Resource resource = inputStreamSupplier.get();
			if (resource == null) {
				throw new NotFoundException();
			} else {
				return getResourceHash(resource);
			}
		});

		String existingFullPath = pathByResourceHash.computeIfAbsent(hash, s -> fullPath);

		if (!fullPath.equals(existingFullPath)) {
			resp.sendRedirect(existingFullPath);
		} else {
			Resource resource = inputStreamSupplier.get();
			try(BufferedInputStream bis = new BufferedInputStream(resource.getInputStream())) {
				resp.setContentType(resource.getMimeType());
				bis.transferTo(resp.getOutputStream());
			} catch (Exception e) {
				throw new IOException("Cannot read resource " + fullPath, e);
			}
		}
	}

	private static String getResourceHash(Resource resource) {
		MessageDigest digest = createDigest();
		byte[] buffer= new byte[8192];
		try (InputStream inputStream = resource.getInputStream()) {
			if (inputStream == null) {
				throw new NotFoundException("Not found: " + resource);
			}
			try(BufferedInputStream bis = new BufferedInputStream(inputStream)) {
				int count;
				while ((count = bis.read(buffer)) > 0) {
					digest.update(buffer, 0, count);
				}
			}
			return bytesToHex(digest.digest());
		}  catch (IOException e) {
			throw ExceptionUtil.softenException(e);
		}
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
