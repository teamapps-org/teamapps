package testapp.util;


import com.akamai.edgeauth.EdgeAuth;
import com.akamai.edgeauth.EdgeAuthBuilder;
import com.akamai.edgeauth.EdgeAuthException;

import java.net.URL;

public class AkamaiTokenUrlCreator {

	private final EdgeAuth edgeAuth;

	public AkamaiTokenUrlCreator(String encryptionKey) {
		if (encryptionKey.length() % 2 != 0
				|| !encryptionKey.matches("[0-9a-fA-F]+")) {
			throw new IllegalArgumentException("Encryption key must be hexadecimal digit string with even length.");
		}

		try {
			edgeAuth = new EdgeAuthBuilder()
					.tokenName("token")
					.key(encryptionKey)
					.windowSeconds(10_000_000L)
					.build();
		} catch (EdgeAuthException e) {
			throw new RuntimeException(e);
		}
	}

	public String createAclUrlString(String url, String acl) {
		try {
			String token = edgeAuth.generateACLToken(acl);
			return String.format("%s?%s=%s", url, edgeAuth.getTokenName(), token);
		} catch (EdgeAuthException e) {
			throw new RuntimeException(e);
		}
	}

	public String createAclUrlString(String urlString) {
		try {
			URL url = new URL(urlString);
			String path = url.getPath();
			String token = edgeAuth.generateACLToken(path);
			return String.format("%s?%s=%s", url, edgeAuth.getTokenName(), token);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private static void createAclQueryString(String hostname, EdgeAuth ea) throws EdgeAuthException {
		String path = "/dash/devito720p.mp4*";
		String token = ea.generateACLToken(path);
		String url = String.format("https://%s%s?%s=%s", hostname, "/dash/devito720p.mp4/manifest.mpd", ea.getTokenName(), token);
		// If url has a query string which isn't for the token, be aware of the string formatter and symbol(? and &).
		// => Link or Request "url" /w Query string
		System.out.println("printAclQueryString:");
		System.out.println(url);
		System.out.println("===");
	}


	private static void printUrlCookie(String hostname, EdgeAuth ea) throws EdgeAuthException {
		String path = "/akamai/edgeauth";
		String token = ea.generateURLToken(path);
		String url = String.format("http(s)://%s%s", hostname, path);
		String cookie = String.format("%s=%s", ea.getTokenName(), token);
		// => Link or Request "url" /w "cookie"
		System.out.println("printUrlCookie:");
		System.out.println(url);
		System.out.println("    With Cookie: " + cookie);
		System.out.println("===");
	}

	private static void printUrlQueryString(String hostname, EdgeAuth ea) throws EdgeAuthException {
		String path = "/akamai/edgeauth";
		String token = ea.generateURLToken(path);
		String url = String.format("http(s)://%s%s?%s=%s", hostname, "/akamai/edgeauth", ea.getTokenName(), token);
		// If url has a query string which isn't for the token, be aware of the string formatter and symbol(? and &).
		// => Link or Request "url" /w Query string
		System.out.println("printUrlQueryString:");
		System.out.println(url);
		System.out.println("===");
	}

	private static void printAclQueryHeader(String hostname, EdgeAuth ea) throws EdgeAuthException {
		String acl = "/akamai/edgeauth/list/*"; // NOTICE the wildcard!
		String token = ea.generateACLToken(acl);

		String url;
		url = String.format("http(s)://%s%s", hostname, "/akamai/edgeauth/list/something");
		String header = String.format("%s: %s", ea.getTokenName(), token);
		// => Link or Request "url" /w "header"
		System.out.println("printAclQueryHeader:");
		System.out.println(url);
		System.out.println("    With header: " + header);
		System.out.println("===");
	}

	private static void printAclCookie(String hostname, EdgeAuth ea) throws EdgeAuthException {
		String acl2[] = {"/akamai/edgeauth", "/akamai/edgeauth/list/*"};
		String token = ea.generateACLToken(acl2);
		String url = String.format("http(s)://%s%s", hostname, "/akamai/edgeauth/list/something2");
		String cookie = String.format("%s=%s", ea.getTokenName(), token);
		// => Link or Request "url" /w "cookie"
		System.out.println("printAclCookie:");
		System.out.println(url);
		System.out.println("    With Cookie: " + cookie);
		System.out.println("===");
	}
}