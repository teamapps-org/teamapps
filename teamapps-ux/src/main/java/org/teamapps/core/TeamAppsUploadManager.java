package org.teamapps.core;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeamAppsUploadManager {

	private final Map<String, File> uploadedFilesByUuid = new ConcurrentHashMap<>();

	public void addUploadedFile(File file, String uuid) {
		this.uploadedFilesByUuid.put(uuid, file);
	}

	public File getUploadedFile(String uuid) {
		return this.uploadedFilesByUuid.get(uuid);
	}

}
