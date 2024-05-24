package org.teamapps.dto.protocol.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.teamapps.projector.dto.JsonWrapper;

public class LocationWrapper extends JsonWrapper {

    public static final String TYPE_ID = "Location";

    public LocationWrapper(ObjectMapper objectMapper, JsonNode jsonNode) {
        super(objectMapper, jsonNode);
    }

    public String getHref() {
        var node = jsonNode.get("href");
        if (node == null || node.isNull()) {
            return null;
        }
        return node.textValue();

    }


    public String getOrigin() {
        var node = jsonNode.get("origin");
        if (node == null || node.isNull()) {
            return null;
        }
        return node.textValue();

    }


    public String getProtocol() {
        var node = jsonNode.get("protocol");
        if (node == null || node.isNull()) {
            return null;
        }
        return node.textValue();

    }


    public String getHost() {
        var node = jsonNode.get("host");
        if (node == null || node.isNull()) {
            return null;
        }
        return node.textValue();

    }


    public String getHostname() {
        var node = jsonNode.get("hostname");
        if (node == null || node.isNull()) {
            return null;
        }
        return node.textValue();

    }


    public Integer getPort() {
        var node = jsonNode.get("port");
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asInt();

    }


    public String getPathname() {
        var node = jsonNode.get("pathname");
        if (node == null || node.isNull()) {
            return null;
        }
        return node.textValue();

    }


    public String getSearch() {
        var node = jsonNode.get("search");
        if (node == null || node.isNull()) {
            return null;
        }
        return node.textValue();

    }


    public String getHash() {
        var node = jsonNode.get("hash");
        if (node == null || node.isNull()) {
            return null;
        }
        return node.textValue();

    }

}