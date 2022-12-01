package org.teamapps.ux.servlet.resourceprovider;

import java.util.Arrays;
import java.util.List;

public interface DirectoryResolutionStrategy {

	List<String> resolveDirectory(String directoryPath);

	static DirectoryResolutionStrategy index(String... indexFileNames) {
		return directoryPath -> Arrays.stream(indexFileNames).map(fileName -> ResourceProviderUtil.concatPaths(directoryPath, fileName)).toList();
	}

	static DirectoryResolutionStrategy empty() {
		return directoryPath -> List.of();
	}


}
