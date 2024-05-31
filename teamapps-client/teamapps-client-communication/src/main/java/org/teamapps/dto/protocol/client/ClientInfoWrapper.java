package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.teamapps.projector.dto.JsonWrapper;

import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClientInfoWrapper extends JsonWrapper {

	public static final String TYPE_ID = "ClientInfo";

	public ClientInfoWrapper(JsonNode jsonNode) {
		super(jsonNode);
	}

	public int getScreenWidth() {
		var node = jsonNode.get("screenWidth");
		if (node == null || node.isNull()) {
			return 0;
		}
		return node.asInt();
	}

	public int getScreenHeight() {
		var node = jsonNode.get("screenHeight");
		if (node == null || node.isNull()) {
			return 0;
		}
		return node.asInt();
	}

	public int getViewPortWidth() {
		var node = jsonNode.get("viewPortWidth");
		if (node == null || node.isNull()) {
			return 0;
		}
		return node.asInt();
	}

	public int getViewPortHeight() {
		var node = jsonNode.get("viewPortHeight");
		if (node == null || node.isNull()) {
			return 0;
		}
		return node.asInt();
	}

	public boolean getHighDensityScreen() {
		var node = jsonNode.get("highDensityScreen");
		if (node == null || node.isNull()) {
			return false;
		}
		return node.asBoolean();
	}

	public String getTimezoneIana() {
		var node = jsonNode.get("timezoneIana");
		if (node == null || node.isNull()) {
			return null;
		}
		return node.textValue();
	}

	public int getTimezoneOffsetMinutes() {
		var node = jsonNode.get("timezoneOffsetMinutes");
		if (node == null || node.isNull()) {
			return 0;
		}
		return node.asInt();
	}


	public List<String> getClientTokens() {
		var node = jsonNode.get("clientTokens");
		if (node == null || node.isNull()) {
			return null;
		}
		ArrayNode nodeArrayNode = ((ArrayNode) node);
		return StreamSupport.stream(Spliterators.spliterator(nodeArrayNode.elements(), nodeArrayNode.size(), Spliterator.ORDERED), false)
				.map(nodeX -> {
					if (nodeX == null || nodeX.isNull()) {
						return null;
					}
					return nodeX.textValue();

				})
				.collect(Collectors.toList());
	}


	public String getLocation() {
		var node = jsonNode.get("location");
		if (node == null || node.isNull()) {
			return null;
		}
		return node.textValue();
	}


	public Map<String, String> getClientParameters() {
		var node = jsonNode.get("clientParameters");
		if (node == null || node.isNull()) {
			return null;
		}
		ObjectNode nodeArrayNode = ((ObjectNode) node);
		return StreamSupport.stream(Spliterators.spliterator(nodeArrayNode.fields(), nodeArrayNode.size(), Spliterator.ORDERED), false)
				.collect(Collectors.toMap(e -> e.getKey(), nodeField -> {
					JsonNode nodeX = nodeField.getValue();
					if (nodeX == null || nodeX.isNull()) {
						return null;
					}
					return nodeX.textValue();

				}));
	}


	public String getTeamAppsVersion() {
		var node = jsonNode.get("teamAppsVersion");
		if (node == null || node.isNull()) {
			return null;
		}
		return node.textValue();
	}

}