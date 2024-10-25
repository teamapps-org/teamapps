package org.teamapps.uisession.messagebuffer;

public record ServerMessageBufferMessage(int sequenceNumber, String message) {
}
