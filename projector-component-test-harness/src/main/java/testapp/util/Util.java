

package testapp.util;

import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

public class Util {

	private final static Logger LOGGER = LoggerFactory.getLogger(Util.class);

	public static String readResourceToString(String resourceName) {
		URL url;
		try {
			url = Resources.getResource(resourceName);
		} catch (Exception e) {
			LOGGER.warn("Could not find classpath resource: " + resourceName);
			return null;
		}
		try {
			return Resources.toString(url, Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static File download(String url) {
		try {
			File tempFile = File.createTempFile("tmp-", ".tmp");
			tempFile.deleteOnExit();
			ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
			new FileOutputStream(tempFile).getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			return tempFile;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
