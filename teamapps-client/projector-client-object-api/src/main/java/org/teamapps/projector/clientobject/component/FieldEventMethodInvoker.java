package org.teamapps.projector.clientobject.component;

import org.teamapps.projector.clientobject.AbstractClientObjectEventMethodInvoker;
import org.teamapps.projector.dto.JsonWrapper;

import java.lang.reflect.Method;
import java.util.List;

public class FieldEventMethodInvoker extends AbstractClientObjectEventMethodInvoker {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public FieldEventMethodInvoker(Object targetObject) {
		super(targetObject);
	}

	@Override
	protected void invokeHandlerMethod(Method method, String name, List<JsonWrapper> parameters) throws Exception {
		switch (name) {
			case "valueChanged" -> method.invoke(targetObject, parameters.get(0));
			case "focus" -> method.invoke(targetObject);
			case "blur" -> method.invoke(targetObject);
			default -> LOGGER.warn("No information on how to invoke this event handler method: {}", method.getName());
		}
	}

}

