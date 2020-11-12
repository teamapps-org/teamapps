package org.teamapps.icons.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.icons.IconLoaderContext;
import org.teamapps.icons.IconResource;
import org.teamapps.icons.IconType;
import org.teamapps.icons.spi.IconLoader;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.stream.Stream;

public class CompositeIconLoader implements IconLoader<CompositeIcon, Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final PngIconComposer pngIconComposer = new PngIconComposer();
	private final SvgIconComposer svgIconComposer = new SvgIconComposer();

	@Override
	public IconResource loadIcon(CompositeIcon icon, int size, IconLoaderContext context) {
		IconResource baseIconResource = context.loadIcon(icon.getBaseIcon(), size);
		if (baseIconResource == null) {
			return null;
		}

		IconResource bottomRightResource = icon.getBottomRightIcon() != null? context.loadIcon(icon.getBottomRightIcon(), size / 2) : null;
		IconResource bottomLeftResource = icon.getBottomLeftIcon() != null? context.loadIcon(icon.getBottomLeftIcon(), size / 2) : null;
		IconResource topLeftResource = icon.getTopLeftIcon() != null? context.loadIcon(icon.getTopLeftIcon(), size / 2) : null;
		IconResource topRightResource = icon.getTopRightIcon() != null? context.loadIcon(icon.getTopRightIcon(), size / 2) : null;

		boolean iconsCompatible = Stream.of(bottomRightResource, bottomLeftResource, topLeftResource, topRightResource)
				.filter(Objects::nonNull)
				.allMatch(iconResource -> iconResource.getIconType().equals(baseIconResource.getIconType()));

		if (!iconsCompatible) {
			LOGGER.error("Sub-icons do not have the same type as base icon: " + baseIconResource.getIconType());
			return baseIconResource;
		}

		if (baseIconResource.getIconType() == IconType.SVG) {
			byte[] composedIconBytes = svgIconComposer.compose(
					baseIconResource.getBytes(),
					bottomRightResource != null ? bottomRightResource.getBytes() : null,
					bottomLeftResource != null ? bottomLeftResource.getBytes() : null,
					topLeftResource != null ? topLeftResource.getBytes() : null,
					topRightResource != null ? topRightResource.getBytes() : null
			);
			return new IconResource(composedIconBytes, IconType.SVG);
		} else if (baseIconResource.getIconType() == IconType.PNG) {
			byte[] composedIconBytes = pngIconComposer.compose(
					size,
					baseIconResource.getBytes(),
					bottomRightResource != null ? bottomRightResource.getBytes() : null,
					bottomLeftResource != null ? bottomLeftResource.getBytes() : null,
					topLeftResource != null ? topLeftResource.getBytes() : null,
					topRightResource != null ? topRightResource.getBytes() : null
			);
			return new IconResource(composedIconBytes, IconType.PNG);
		} else {
			return baseIconResource;
		}
	}
}
