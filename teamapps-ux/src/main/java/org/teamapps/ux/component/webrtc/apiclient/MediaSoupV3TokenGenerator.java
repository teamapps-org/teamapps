package org.teamapps.ux.component.webrtc.apiclient;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.teamapps.ux.component.webrtc.apiclient.MediaSoupV3ApiOperation.*;

public class MediaSoupV3TokenGenerator {

	public static String generatePublishJwtToken(String streamUuid, String secret, Duration tokenValidityDuration) {
		return generateJwtToken(secret, PUBLISH, streamUuid, tokenValidityDuration  /*PUBLISH*/);
	}

	public static String generateSubscribeJwtToken(String streamUuid, String secret, Duration tokenValidityDuration) {
		return generateJwtToken(secret, SUBSCRIBE, streamUuid, tokenValidityDuration  /*SUBSCRIBE*/);
	}

	public static String generateRecordingJwtToken(String secret, Duration tokenValidityDuration) {
		return generateJwtToken(secret, RECORDING, null, tokenValidityDuration);
	}

	public static String generateStreamingJwtToken(String streamUuid, String secret, Duration tokenValidityDuration) {
		return generateJwtToken(secret, STREAMING, streamUuid, tokenValidityDuration);
	}

	public static String generateMixerJwtToken(String secret, Duration tokenValidityDuration) {
		return generateJwtToken(secret, MIXER, null, tokenValidityDuration);
	}

	public static String generateGeneralApiToken(String secret, Duration tokenValidityDuration) {
		return generateJwtToken(secret, null, null, tokenValidityDuration);
	}

	public static String generateJwtToken(String secret, MediaSoupV3ApiOperation operation, String streamUuid, Duration tokenValidityDuration) {
		if (secret == null) {
			return "";
		}
		try {
			Algorithm algorithm = Algorithm.HMAC512(secret);
			JWTCreator.Builder builder = JWT.create();
			if (operation != null) {
				builder = builder.withClaim("operation", operation.ordinal());
			}
			if (streamUuid != null) {
				builder = builder.withClaim("stream", streamUuid);
			}
			if (tokenValidityDuration != null) {
				builder = builder.withExpiresAt(new Date(Instant.now().plus(tokenValidityDuration).toEpochMilli()));
			}
			return builder.sign(algorithm);
		} catch (JWTCreationException exception) {
			throw new RuntimeException("Could not create auth token - this should never happen!");
		}
	}

}
