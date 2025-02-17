/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.i18n;

public enum TeamAppsDictionary {

	LOGIN("login"),
	PASSWORD("password"),
	RESET_PASSWORD("resetPassword"),
	WRONG_USER_NAME_OR_PASSWORD("wrongUserNameOrPassword"),
	USER_NAME("userName"),
	USER_I_D("userID"),
	ADD("add"),
	ADD_RECORD("addRecord"),
	EDIT("edit"),
	REMOVE("remove"),
	REMOVE_SELECTION("removeSelection"),
	REMOVE_ALL_FILTERS("removeAllFilters"),
	DELETE("delete"),
	DELETE_RECORD("deleteRecord"),
	SAVE("save"),
	SAVE_CHANGES("saveChanges"),
	SAVE_AND_CLOSE("saveAndClose"),
	REVERT("revert"),
	REVERT_CHANGES("revertChanges"),
	ERROR("error"),
	OK("ok"),
	CANCEL("cancel"),
	CANCEL_AND_CLOSE("cancelAndClose"),
	PRINT("print"),
	HELP("help"),
	UPLOAD("upload"),
	DOWNLOAD("download"),
	REGISTER("register"),
	ROTATE("rotate"),
	ROTATE_PICTURE("rotatePicture"),
	CROP_IMAGE("cropImage"),
	YEAR("year"),
	MONTH("month"),
	WEEK("week"),
	DAY("day"),
	GROUP("group"),
	GROUPING("grouping"),
	BY_FULL_VALUE("byFullValue"),
	BY_WORDS("byWords"),
	BY_YEAR("byYear"),
	BY_QUARTER("byQuarter"),
	BY_MONTH("byMonth"),
	BY_WEEK("byWeek"),
	BY_Day("byDay"),
	TODAY("today"),
	NOW("now"),
	PREVIOUS("previous"),
	NEXT("next"),
	BACK("back"),
	CREATION_DATE("creationDate"),
	MODIFICATION_DATE("modificationDate"),
	DELETION_DATE("deletionDate"),
	RESTORE_DATE("restoreDate"),
	CREATED_BY("createdBy"),
	MODIFIED_BY("modifiedBy"),
	DELETED_BY("deletedBy"),
	RESTORED_BY("restoredBy"),
	SEARCH("search"),
	SEARCH___("search___"),
	VALUES("values"),
	COUNT("count"),
	FILTER("filter"),
	EMPTY("empty"),
	SELECT("select"),
	SELECT_AREA("selectArea"),
	REQUIRED_FIELD("requiredField"),
	RECORD_SUCCESSFULLY_SAVED("recordSuccessfullySaved"),
	RECORD_SUCCESSFULLY_DELETED("recordSuccessfullyDeleted"),
	FILE_TOO_LARGE_MESSAGE("fileTooLargeMsg"),
	FILE_TOO_LARGE_SHORT_MESSAGE("fileTooLargeShortMsg"),
	UPLOAD_ERROR_MESSAGE("uploadErrorMsg"),
	UPLOAD_ERROR_SHORT_MESSAGE("uploadErrorShortMsg"),
	NO_RUNNING_TASKS("noRunningTasks"),

	;

	private final String key;

	TeamAppsDictionary(String key) {
		this.key = key;
	}

	public String getKey() {
		return "teamapps.common." + key;
	}
}
