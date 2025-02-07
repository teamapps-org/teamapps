package org.teamapps.projector.dto.protocol.server;

public enum SessionClosingReason {
		SESSION_NOT_FOUND,
		SESSION_TIMEOUT,
		TERMINATED_BY_CLIENT,
		SERVER_SIDE_ERROR,
		COMMANDS_OVERFLOW,
		REINIT_COMMAND_ID_NOT_FOUND,
		CMD_REQUEST_TOO_LARGE,
		WRONG_TEAMAPPS_VERSION,
		TERMINATED_BY_APPLICATION;

    @com.fasterxml.jackson.annotation.JsonValue
    public int jsonValue() {
        return ordinal();
    }
}