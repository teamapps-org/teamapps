package org.teamapps.ux.i18n;

public enum  TeamAppsDictionary {

	LOGIN("login"),
	PASSWORD("password"),
	RESET_PASSWORD("resetPassword"),
	WRONG_USER_NAME_OR_PASSWORD("wrongUserNameOrPassword"),
	USER_NAME("userName"),
	USER_I_D("userID"),
	ADD("add"),
	EDIT("edit"),
	REMOVE("remove"),
	DELETE("delete"),
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
	TODAY("today"),
	NOW("now"),
	PREVIOUS("previous"),
	NEXT("next"),
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
	REQUIRED_FIELD("requiredField"),
	RECORD_SUCCESSFULLY_SAVE("recordSuccessfullySave"),
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
