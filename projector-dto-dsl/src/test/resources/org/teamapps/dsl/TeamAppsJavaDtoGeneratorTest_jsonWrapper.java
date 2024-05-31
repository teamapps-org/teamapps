package org.teamapps.dto222;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.dto.DtoObject;
import org.teamapps.projector.session.SessionContext;

import org.teamapps.commons.util.ExceptionUtil;

/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */

import org.teamapps.projector.clientobject.ClientObject;

public class AWrapper extends JsonWrapper {

    public static final String TYPE_ID = "A";

    public AWrapper(JsonNode jsonNode) {
        super(jsonNode);
    }

    public A unwrap() {
        return ExceptionUtil.runWithSoftenedExceptions(() -> SessionContext.current().getObjectMapper().treeToValue(jsonNode, A.class));
    }

    public A unwrap(ObjectMapper objectMapper) {
        return ExceptionUtil.runWithSoftenedExceptions(() -> objectMapper.treeToValue(jsonNode, A.class));
    }

    public int getI() {
        var node = jsonNode.get("i");
        if (node == null || node.isNull()) {
            return 0;
        }
        return node.asInt();

    }

    public String getS() {
        var node = jsonNode.get("s");
        if (node == null || node.isNull()) {
            return null;
        }
        return node.textValue();

    }

    public AWrapper getA() {
        var node = jsonNode.get("a");
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isObject()) {
            throw new IllegalArgumentException("node must be an object!");
        }
        // A
        return new AWrapper(node);

    }

    public List<String> getL() {
        var node = jsonNode.get("l");
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

    public Map<String, AWrapper> getD() {
        var node = jsonNode.get("d");
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
                    if (!nodeX.isObject()) {
                        throw new IllegalArgumentException("nodeX must be an object!");
                    }
                    // A
                    return new AWrapper(nodeX);

                }));

    }
}