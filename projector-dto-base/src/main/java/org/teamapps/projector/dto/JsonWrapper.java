package org.teamapps.projector.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Function;

/**
 * A wrapper around a JSON node sent by the client.
 * <p>
 * There are three main requirements driving the decision for this class:
 * <ul>
 *     <li>Many client messages are polymorphic. E.g., a component event may contain a list of objects of an abstract type.
 *     In order to instantiate them, Jackson would need a type ID. However, TeamApps can handle several versions of the same component library at the same time.
 *     If we used type IDs for deserializing, every component library version would have to introduce its own set of unique type ids. Additionally, type ids
 *     would have to be unique between different libraries.</li>
 *     <li>We don't want to force any JSON structure on messages sent from the client to the server. If a client component needs to send an esoteric message to it's server-side component, so be it.
 *     Also, we don't want to force a specific representation of, say, LocalDateTime. The Component developer knows much better how it should be represented.</li>
 *     <li>Still, we want to comfortably access types defined in DTO definitions.</li>
 * </ul>
 */
public class JsonWrapper {

	protected final JsonNode jsonNode;

	public JsonWrapper(JsonNode jsonNode) {
		this.jsonNode = jsonNode;
	}

	/**
	 * Get the underlying JSON node.
	 */
	public JsonNode getJsonNode() {
		return jsonNode;
	}

	public String getTypeId() {
		return jsonNode.get("_type").textValue();
	}

	public <W extends JsonWrapper> W as(Function<JsonNode, W> constructor) {
		return constructor.apply(jsonNode);
	}
}