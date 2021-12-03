package x;

import org.junit.Test;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.IconLibraryRegistry;
import org.teamapps.icons.IconProvider;
import org.teamapps.icons.IconResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IconProviderTest {

	@Test
	public void blah() throws IOException {
		IconProvider iconProvider = new IconProvider(new IconLibraryRegistry());

		MaterialIcon icon = MaterialIcon.HELP;
//		icon = icon.withStyle(MaterialIconStyles.GRADIENT_OUTLINE_ORANGE);

		String encodedIcon = iconProvider.encodeIcon(icon);

		System.out.println(encodedIcon);

		IconResource iconResource = iconProvider.loadIcon(icon, 32);

		Files.write(Path.of("icon.svg"), iconResource.getBytes());


	}
}