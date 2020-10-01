/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.icons.systemprovider;

import org.teamapps.icons.api.*;
import org.teamapps.icons.provider.IconProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SystemIconProvider {

	private IconProvider standardIconProvider;
	private final Map<String, IconProvider> iconProviderByStyleId = new HashMap<>();
	private final Map<String, IconProvider> iconProviderByLibraryId = new HashMap<>();
	private final Map<String, CustomIconStyle> customIconStyleByStyleId = new HashMap<>();

	private final CachingIconProvider cachingIconProvider;
	private final ResizingIconProvider resizingIconProvider = new ResizingIconProvider();
	private final StylingIconProvider stylingIconProvider = new StylingIconProvider();
	private final CompositeIconProvider compositeIconProvider = new CompositeIconProvider();
	private final CompositeSvgProvider compositeSvgProvider = new CompositeSvgProvider();

	public SystemIconProvider() {
		cachingIconProvider = new CachingIconProvider();
	}

	public SystemIconProvider(File cachingDirectory) {
		cachingIconProvider = new CachingIconProvider(cachingDirectory);
	}

	public IconResource getIcon(int size, String qualifiedIconId) {
		boolean isSVG = qualifiedIconId.endsWith(".svg");
		byte[] bytes = getIconBytes(size, isSVG, qualifiedIconId);
		if (bytes != null) {
			String mimeType = isSVG ? "image/svg+xml" : "image/png";
			return new IconResource(bytes, mimeType);
		} else {
			return null;
		}
	}

	public byte[] getIconBytes(int size, boolean isSVG, String qualifiedIconId) {
		try {
			boolean composed = StyledCompositeIcon.isComposedIcon(qualifiedIconId);
			if (composed) {
				return getComposedIcon(size, qualifiedIconId, isSVG);
			} else {
				return getSingleIcon(size, qualifiedIconId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] getSingleIcon(int size, StyledIcon styledIcon) {
		if (styledIcon == null) {
			return null;
		}
		return getSingleIcon(size, styledIcon.getQualifiedIconId());
	}

	private byte[] getSingleIcon(int size, String qualifiedIconId) {
		byte[] cachedIcon = cachingIconProvider.getCachedIcon(size, qualifiedIconId);
		if (cachedIcon != null) {
			return cachedIcon;
		}
		StyledIcon styledIcon = StyledIcon.parseIcon(qualifiedIconId);
		if (styledIcon == null) {
			return null;
		}
		String iconLibraryId = styledIcon.getIconLibraryId();
		String styleId = styledIcon.getStyleId();
		String iconName = styledIcon.getIconName();

		String providerStyleId = styleId;
		CustomIconStyle customIconStyle = customIconStyleByStyleId.get(styleId);
		if (customIconStyle != null) {
			providerStyleId = customIconStyle.getBaseStyleId();
		}

		IconProvider iconProvider = getIconProvider(iconLibraryId, providerStyleId);

		if (iconProvider == null) {
			return null;
		}

		byte[] iconBytes = null;
		boolean convertedIcon = false;

		if (iconProvider.getAvailableIconSizes() != null && !iconProvider.getAvailableIconSizes().isEmpty() && !iconProvider.getAvailableIconSizes().contains(size)) {
			convertedIcon = true;
			int resizeSize = calculateResizeSize(size, iconProvider.getAvailableIconSizes());
			byte[] icon = iconProvider.getIcon(providerStyleId, resizeSize, iconName);
			iconBytes = resizingIconProvider.resizeIcon(icon, size);
		} else {
			iconBytes = iconProvider.getIcon(providerStyleId, size, iconName);
		}

		if (customIconStyle != null && iconBytes != null && !qualifiedIconId.contains(".flag_")) {
			convertedIcon = true;
			iconBytes = stylingIconProvider.styleIcon(customIconStyle, iconBytes);
		}

		if (convertedIcon) {
			cachingIconProvider.puIconInCache(size, qualifiedIconId, iconBytes);
		}
		return iconBytes;
	}

	private IconProvider getIconProvider(String iconLibraryId, String styleId) {
		IconProvider iconProvider = null;
		if (standardIconProvider.getIconLibraryId().equals(iconLibraryId)) {
			iconProvider = standardIconProvider;
		}
		if (iconProvider == null) {
			iconProvider = iconProviderByLibraryId.get(iconLibraryId);
		}
		if (iconProvider == null) {
			iconProvider = iconProviderByStyleId.get(styleId);
		}
		return iconProvider;
	}


	private int calculateResizeSize(int requiredSize, Set<Integer> availableSizes) {
		int bestFit = calculateResizeSize(requiredSize, availableSizes, 2, 4, 3, 1.5d);
		if (bestFit > 0) {
			return bestFit;
		}
		for (Integer size : availableSizes) {
			if (bestFit == 0) {
				bestFit = size;
			} else if (bestFit < requiredSize * 2 && size > bestFit) {
				bestFit = size;
			}
		}
		return bestFit;
	}

	private int calculateResizeSize(int requiredSize, Set<Integer> availableSizes, double ... factors) {
		for (double factor : factors) {
			if (availableSizes.contains((int) (requiredSize * factor))) {
				return (int) (requiredSize * factor);
			}
		}
		return 0;
	}

	private byte[] getComposedSvgIcon(int size, String qualifiedIconId) {
		byte[] cachedIcon = cachingIconProvider.getCachedIcon(size, qualifiedIconId);
		if (cachedIcon != null) {
			return cachedIcon;
		}
		StyledCompositeIcon composedIcon = StyledCompositeIcon.parse(qualifiedIconId);
		if (composedIcon == null) {
			return null;
		}


		byte[] iconBytes = null;

		cachingIconProvider.puIconInCache(size, qualifiedIconId, iconBytes);

		return iconBytes;
	}

	private byte[] getComposedIcon(int size, String qualifiedIconId, boolean isSVG) {
		byte[] cachedIcon = cachingIconProvider.getCachedIcon(size, qualifiedIconId);
		if (cachedIcon != null) {
			return cachedIcon;
		}
		StyledCompositeIcon composedIcon = StyledCompositeIcon.parse(qualifiedIconId);
		if (composedIcon == null) {
			return null;
		}

		byte[] baseIcon = getSingleIcon(size, composedIcon.getBaseIcon());
		byte[] bottomRight = getSingleIcon(size / 2, composedIcon.getBottomRightIcon());
		byte[] bottomLeft = getSingleIcon(size / 2, composedIcon.getBottomLeftIcon());
		byte[] topLeft = getSingleIcon(size / 2, composedIcon.getTopLeftIcon());
		byte[] topRight = getSingleIcon(size / 2, composedIcon.getTopRightIcon());

		byte[] iconBytes = null;
		if (isSVG) {
			iconBytes = compositeSvgProvider.createComposedIcon(size, baseIcon, bottomRight, bottomLeft, topLeft, topRight);
		} else {
			iconBytes = compositeIconProvider.createComposedIcon(size, baseIcon, bottomRight, bottomLeft, topLeft, topRight);
		}

		cachingIconProvider.puIconInCache(size, qualifiedIconId, iconBytes);

		return iconBytes;
	}

	public IconProvider getStandardIconProvider() {
		return standardIconProvider;
	}

	public void registerStandardIconProvider(IconProvider iconProvider) {
		standardIconProvider = iconProvider;
		registerCustomIconProvider(iconProvider);
	}

	public void registerCustomIconProvider(IconProvider<?> iconProvider) {
		for (IconStyle iconStyle : iconProvider.getAvailableIconStyles()) {
			if (iconStyle instanceof CustomIconStyle) {
				customIconStyleByStyleId.put(iconStyle.getStyleId(), (CustomIconStyle) iconStyle);
			} else {
				iconProviderByStyleId.put(iconStyle.getStyleId(), iconProvider);
			}
		}
		iconProviderByLibraryId.put(iconProvider.getIconLibraryId(), iconProvider);
	}

	public void registerCustomIconStyle(CustomIconStyle customIconStyle) {
		customIconStyleByStyleId.put(customIconStyle.getStyleId(), customIconStyle);
	}


}
