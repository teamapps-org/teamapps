/*
 * Copyright (c) 2019 teamapps.org (see code comments for author's name)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.teamapps.ux.component.field.upload.simple;

/**
 * @author Yann Massard (yamass@gmail.com)
 */
public enum FileItemState {

	INITIATING,
	TOO_LARGE,
	UPLOADING,
	CANCELED,
	FAILED,
	DONE

}