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
import {TeamAppsEvent} from "./TeamAppsEvent";

export class FileUploader {


	public readonly onProgress: TeamAppsEvent<number> = new TeamAppsEvent();
	public readonly onSuccess: TeamAppsEvent<string> = new TeamAppsEvent();
	public readonly onError: TeamAppsEvent<void> = new TeamAppsEvent();
	public readonly onComplete: TeamAppsEvent<void> = new TeamAppsEvent();

	private xhr: JQuery.jqXHR;

	public upload(file: File, url: string, fileFormDataName = "files") {
		const formData = new FormData();
		formData.append(fileFormDataName, file, file.name);

		this.xhr = $.ajax({
			url: url,
			data: formData,
			processData: false,
			contentType: false,
			type: 'POST',
			xhr: () => {
				const xhr = $.ajaxSettings.xhr();
				if (xhr.upload) {
					xhr.upload.addEventListener('progress', (event) => {
						let progress: number;
						if (event.lengthComputable) {
							const position = event.loaded || (event as any).position; // event.position is deprecated
							const total = event.total;
							progress = position / total;
						} else {
							progress = -1;
							console.warn("Cannot calculate percentage progress of file upload!");
						}
						this.onProgress.fire(progress);
					}, false);
				}
				return xhr;
			},
			success: (fileUuids: string[]) => {
				for (let j = 0; j < fileUuids.length; j++) { // it's actually only one uuid, since we upload the files sequentially!
					const fileUuid = fileUuids[j];
					this.onSuccess.fire(fileUuid);
				}
			},
			error: (e) => {
				this.onError.fire(null);
				this.onComplete.fire(null);
			},
			complete: () => {
				this.xhr = null;
				this.onComplete.fire(null);
			}
		});
	}

	abort() {
		this.xhr.abort();
	}
}

export function humanReadableFileSize(bytes: number, decimalK = true) {
	const thresh = decimalK ? 1000 : 1024;
	if (Math.abs(bytes) < thresh) {
		return bytes + ' B';
	}
	const units = decimalK
		? ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
		: ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
	let u = -1;
	do {
		bytes /= thresh;
		++u;
	} while (Math.abs(bytes) >= thresh && u < units.length - 1);
	return bytes.toFixed(1) + ' ' + units[u];
}
