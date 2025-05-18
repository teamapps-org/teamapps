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
import {ProjectorEvent} from "./ProjectorEvent";

export class FileUploader {
	public readonly onProgress: ProjectorEvent<number> = new ProjectorEvent();
	public readonly onSuccess: ProjectorEvent<string> = new ProjectorEvent();
	public readonly onError: ProjectorEvent<string> = new ProjectorEvent();
	public readonly onComplete: ProjectorEvent<void> = new ProjectorEvent();

	private xhr: XMLHttpRequest | null = null;

	public upload(file: File, url: string, fileFormDataName = "files"): void {
		const formData = new FormData();
		formData.append(fileFormDataName, file, file.name);

		this.xhr = new XMLHttpRequest();

		this.xhr.upload.addEventListener("progress", (event) => {
			let progress: number;
			if (event.lengthComputable) {
				const position = event.loaded;
				const total = event.total;
				progress = position / total;
			} else {
				progress = -1;
				console.warn("Cannot calculate percentage progress of file upload!");
			}
			this.onProgress.fire(progress);
		}, false);

		this.xhr.addEventListener("load", () => {
			if (this.xhr) {
				if (this.xhr.status >= 200 && this.xhr.status < 300) {
					try {
						const fileUuids: string[] = JSON.parse(this.xhr.responseText);
						for (let j = 0; j < fileUuids.length; j++) { // it's actually only one uuid, since we upload the files sequentially!
							const fileUuid = fileUuids[j];
							this.onSuccess.fire(fileUuid);
						}
					} catch (error) {
						this.onError.fire("" + error);
					}
				} else {
					this.onError.fire(`Upload failed with status ${this.xhr.status}: ${this.xhr.statusText}`);
				}
			}
		});

		this.xhr.addEventListener("error", () => {
			this.onError.fire("An unknown error occurred during upload.");
			this.onComplete.fire();
		});

		this.xhr.addEventListener("abort", () => {
			// TODO anything to do here?
		});

		this.xhr.addEventListener("loadend", () => {
			// This event fires after load, error, abort, or timeout
			this.xhr = null;
			this.onComplete.fire();
		});

		this.xhr.open("POST", url);
		// this.xhr.setRequestHeader('Custom-Header', 'blah');
		this.xhr.send(formData);
	}

	public abort(): void {
		if (this.xhr) {
			this.xhr.abort();
		}
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
