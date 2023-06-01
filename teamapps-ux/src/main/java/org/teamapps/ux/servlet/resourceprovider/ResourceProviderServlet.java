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
package org.teamapps.ux.servlet.resourceprovider;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.ux.resource.Resource;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ResourceProviderServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final long ONE_SECOND_IN_MILLIS = TimeUnit.SECONDS.toMillis(1);
	private static final String ETAG = "W/\"%s-%s\"";
	private static final Pattern RANGE_PATTERN = Pattern.compile("^bytes=[0-9]*-[0-9]*(,[0-9]*-[0-9]*)*$");
	private static final String MULTIPART_BOUNDARY = UUID.randomUUID().toString();
	private static final String CONTENT_DISPOSITION_HEADER = "%s;filename=\"%2$s\"; filename*=UTF-8''%2$s";

	private final ResourceProvider resourceProvider;

	public ResourceProviderServlet(ResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response, true);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response, false);
	}

	private void doRequest(HttpServletRequest request, HttpServletResponse response, boolean head) throws IOException {
		response.reset();

		Resource resource;
		try {
			resource = resourceProvider.getResource(request.getServletPath(), request.getPathInfo(), request.getSession().getId());
		} catch (IllegalArgumentException e) {
			LOGGER.info("Got an IllegalArgumentException from ResourceProvider. Interpreting it as 400 Bad Request. "
					+ request.getServletPath() + request.getPathInfo() + " for HTTP session " + request.getSession().getId(), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (resource == null) {
			handleNotFound(request, response);
			return;
		}

		if (preconditionFailed(request, resource)) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
			return;
		}

		setCacheHeaders(response, resource, resource.getExpires().getTime());

		if (notModified(request, resource)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		List<Range> ranges = getRanges(request, resource);

		if (ranges == null) {
			response.setHeader("Content-Range", "bytes */" + resource.getLength());
			response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
			return;
		}

		if (!ranges.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		} else {
			ranges.add(new Range(0, resource.getLength() - 1)); // Full content.
		}

		String contentType = setContentHeaders(response, resource, ranges);

		if (head) {
			return;
		}

		writeContent(response, resource, ranges, contentType);
	}

	/**
	 * Handles the case when the file is not found.
	 * <p>
	 * The default implementation sends a HTTP 404 error.
	 *
	 * @param request  The involved HTTP servlet request.
	 * @param response The involved HTTP servlet response.
	 * @throws IOException When something fails at I/O level.
	 */
	protected void handleNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	/**
	 * Returns the file name to be used in <code>Content-Disposition</code> header.
	 * This does not need to be URL-encoded as this will be taken care of.
	 * <p>
	 * The default implementation returns {@link File#getName()}.
	 *
	 * @param request The involved HTTP servlet request.
	 * @param file    The involved file.
	 * @return The file name to be used in <code>Content-Disposition</code> header.
	 */
	protected String getAttachmentName(HttpServletRequest request, File file) {
		return file.getName();
	}

	/**
	 * Returns true if it's a conditional request which must return 412.
	 */
	private boolean preconditionFailed(HttpServletRequest request, Resource resource) {
		String match = request.getHeader("If-Match");
		long unmodified = request.getDateHeader("If-Unmodified-Since");
		return (match != null) ? !matches(match, getETag(resource)) : (unmodified != -1 && modified(unmodified, resource.getLastModified().getTime()));
	}

	private void setCacheHeaders(HttpServletResponse response, Resource resource, long expires) {
		setCacheHeaders(response, expires);
		response.setHeader("ETag", getETag(resource));
		response.setDateHeader("Last-Modified", resource.getLastModified().getTime());
	}

	/**
	 * Returns true if it's a conditional request which must return 304.
	 */
	private boolean notModified(HttpServletRequest request, Resource resource) {
		String noMatch = request.getHeader("If-None-Match");
		long modified = request.getDateHeader("If-Modified-Since");
		return (noMatch != null) ? matches(noMatch, getETag(resource)) : (modified != -1 && !modified(modified, resource.getLastModified().getTime()));
	}

	/**
	 * Get requested ranges. If this is null, then we must return 416. If this is empty, then we must return full file.
	 */
	private List<Range> getRanges(HttpServletRequest request, Resource resource) {
		List<Range> ranges = new ArrayList<>(1);
		String rangeHeader = request.getHeader("Range");

		if (rangeHeader == null) {
			return ranges;
		} else if (!RANGE_PATTERN.matcher(rangeHeader).matches()) {
			return null; // Syntax error.
		}

		String ifRange = request.getHeader("If-Range");

		if (ifRange != null && !ifRange.equals(getETag(resource))) {
			try {
				long ifRangeTime = request.getDateHeader("If-Range");

				if (ifRangeTime != -1 && modified(ifRangeTime, resource.getLastModified().getTime())) {
					return ranges;
				}
			} catch (IllegalArgumentException ifRangeHeaderIsInvalid) {
				LOGGER.debug("If-Range header is invalid. Let's just return full file then.", ifRangeHeaderIsInvalid);
				return ranges;
			}
		}

		for (String rangeHeaderPart : rangeHeader.split("=")[1].split(",")) {
			Range range = parseRange(rangeHeaderPart, resource.getLength());

			if (range == null) {
				return null; // Logic error.
			}

			ranges.add(range);
		}

		return ranges;
	}

	/**
	 * Parse range header part. Returns null if there's a logic error (i.e. start after end).
	 */
	private Range parseRange(String range, long length) {
		long start = parseLong(range, 0, range.indexOf('-'));
		long end = parseLong(range, range.indexOf('-') + 1, range.length());

		if (start == -1) {
			start = length - end;
			end = length - 1;
		} else if (end == -1 || end > length - 1) {
			end = length - 1;
		}

		if (start > end) {
			return null; // Logic error.
		}

		return new Range(start, end);
	}

	private String setContentHeaders(HttpServletResponse response, Resource resource, List<Range> ranges) {
		String contentType = resource.getMimeType();
		String filename = resource.getName();
		response.setHeader("Content-Disposition", String.format(CONTENT_DISPOSITION_HEADER, (resource.isAttachment() ? "attachment" : "inline"), encodeURI(filename)));
		response.setHeader("Accept-Ranges", "bytes");

		if (ranges.size() == 1) {
			Range range = ranges.get(0);
			response.setContentType(contentType);
			response.setHeader("Content-Length", String.valueOf(range.length));

			if (response.getStatus() == HttpServletResponse.SC_PARTIAL_CONTENT) {
				response.setHeader("Content-Range", "bytes " + range.start + "-" + range.end + "/" + resource.getLength());
			}
		} else {
			response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
		}

		return contentType;
	}

	/**
	 * Write given file to response with given content type and ranges.
	 */
	private void writeContent(HttpServletResponse response, Resource resource, List<Range> ranges, String contentType) throws IOException {
		ServletOutputStream output = response.getOutputStream();

		if (ranges.size() == 1) {
			Range range = ranges.get(0);
			IOUtils.copyLarge(resource.getInputStream(), output, range.start, range.length);
		} else {
			// Copy multi part range.
			for (Range r : ranges) {
				// Add multipart boundary and header fields for every range.
				output.println();
				output.println("--" + MULTIPART_BOUNDARY);
				output.println("Content-Type: " + contentType);
				output.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + resource.getLength());

				// Copy single part range of multi part range.
				IOUtils.copyLarge(resource.getInputStream(), output, r.start, r.length);
			}
			// End with multipart boundary.
			output.println();
			output.println("--" + MULTIPART_BOUNDARY + "--");
		}
	}

	/**
	 * URL-encode the given string using UTF-8.
	 *
	 * @param string The string to be URL-encoded using UTF-8.
	 * @return The given string, URL-encoded using UTF-8, or <code>null</code> if <code>null</code> was given.
	 * @throws UnsupportedOperationException When this platform does not support UTF-8.
	 */
	public static String encodeURL(String string) {
		if (string == null) {
			return null;
		}
		return URLEncoder.encode(string, UTF_8);
	}

	/**
	 * URI-encode the given string using UTF-8. URIs (paths and filenames) have different encoding rules as compared to
	 * URL query string parameters. {@link URLEncoder} is actually only for www (HTML) form based query string parameter
	 * values (as used when a webbrowser submits a HTML form). URI encoding has a lot in common with URL encoding, but
	 * the space has to be %20 and some chars doesn't necessarily need to be encoded.
	 *
	 * @param string The string to be URI-encoded using UTF-8.
	 * @return The given string, URI-encoded using UTF-8, or <code>null</code> if <code>null</code> was given.
	 * @throws UnsupportedOperationException When this platform does not support UTF-8.
	 */
	public static String encodeURI(String string) {
		if (string == null) {
			return null;
		}

		return encodeURL(string)
				.replace("+", "%20")
				.replace("%21", "!")
				.replace("%27", "'")
				.replace("%28", "(")
				.replace("%29", ")")
				.replace("%7E", "~");
	}

	/**
	 * <p>Set the cache headers. If the <code>expires</code> argument is larger than 0 seconds, then the following headers
	 * will be set:
	 * <ul>
	 * <li><code>Cache-Control: public,max-age=[expiration time in seconds],must-revalidate</code></li>
	 * <li><code>Expires: [expiration date of now plus expiration time in seconds]</code></li>
	 * </ul>
	 * <p>Else the method will delegate to {@link #setNoCacheHeaders(HttpServletResponse)}.
	 *
	 * @param response The HTTP servlet response to set the headers on.
	 * @param expires  The expire time in seconds (not milliseconds!).
	 */
	public static void setCacheHeaders(HttpServletResponse response, long expires) {
		if (expires > 0) {
			response.setHeader("Cache-Control", "public,max-age=" + expires + ",must-revalidate");
			response.setDateHeader("Expires", System.currentTimeMillis() + SECONDS.toMillis(expires));
			response.setHeader("Pragma", ""); // Explicitly set pragma to prevent container from overriding it.
		} else {
			setNoCacheHeaders(response);
		}
	}

	/**
	 * <p>Set the no-cache headers. The following headers will be set:
	 * <ul>
	 * <li><code>Cache-Control: no-cache,no-store,must-revalidate</code></li>
	 * <li><code>Expires: [expiration date of 0]</code></li>
	 * <li><code>Pragma: no-cache</code></li>
	 * </ul>
	 * Set the no-cache headers.
	 *
	 * @param response The HTTP servlet response to set the headers on.
	 */
	public static void setNoCacheHeaders(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma", "no-cache"); // Backwards compatibility for HTTP 1.0.
	}

	private String getETag(Resource resource) {
		return String.format(ETAG, encodeURL(resource.getName()), resource.getLastModified());
	}

	private static boolean matches(String matchHeader, String eTag) {
		String[] matchValues = matchHeader.split("\\s*,\\s*");
		Arrays.sort(matchValues);
		return Arrays.binarySearch(matchValues, eTag) > -1
				|| Arrays.binarySearch(matchValues, "*") > -1;
	}

	private static boolean modified(long modifiedHeader, long lastModified) {
		return (modifiedHeader + ONE_SECOND_IN_MILLIS <= lastModified); // That second is because the header is in seconds, not millis.
	}

	private static long parseLong(String value, int beginIndex, int endIndex) {
		String substring = value.substring(beginIndex, endIndex);
		return substring.isEmpty() ? -1 : Long.parseLong(substring);
	}

	private static boolean accepts(String acceptHeader, String toAccept) {
		String[] acceptValues = acceptHeader.split("\\s*([,;])\\s*");
		Arrays.sort(acceptValues);
		return Arrays.binarySearch(acceptValues, toAccept) > -1
				|| Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
				|| Arrays.binarySearch(acceptValues, "*/*") > -1;
	}

	private static class Range {
		private final long start;
		private final long end;
		private final long length;

		public Range(long start, long end) {
			this.start = start;
			this.end = end;
			length = end - start + 1;
		}

	}
}

