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
import java.lang.reflect.Method;
import org.teamapps.projector.clientobject.AbstractClientObjectEventMethodInvoker;
import java.util.function.BiFunction;

public class AEventMethodInvoker extends AbstractClientObjectEventMethodInvoker {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public AEventMethodInvoker(Object targetObject) {
		super(targetObject);
	}

	@Override
	protected void invokeHandlerMethod(Method method, String name, JsonWrapper eventObject) throws Exception {
		switch (name) {
		    case "e" -> 
		    method.invoke(targetObject, eventObject.as(A.EEventWrapper::new));

			default -> LOGGER.warn("No information on how to invoke this event handler method: {}", method.getName());
		}
	}
}