package org.teamapps.projector.server.uisession.messagebuffer;

public record ServerMessageBufferMessage(int sequenceNumber, String message) {
}
