package org.teamapps.server.uisession.messagebuffer;

public record ServerMessageBufferMessage(int sequenceNumber, String message) {
}
