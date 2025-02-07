package org.teamapps.projector.clientobject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.commons.util.ExceptionUtil;
import org.teamapps.commons.util.ReflectionUtil;
import org.teamapps.projector.dto.JsonWrapper;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractClientObjectQueryMethodInvoker {

	record ClassAndMethodName(Class<?> clazz, String methodName) {}

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final Map<ClassAndMethodName, Method> cachedMethods = new HashMap<>();

	protected final Object targetObject;

	public AbstractClientObjectQueryMethodInvoker(Object targetObject) {
		this.targetObject = targetObject;
	}

	public Object handleQuery(String name, List<JsonWrapper> parameters) {
		Method method = getMethod(name);
		if (method == null) {
			throw new UnsupportedOperationException("Could not find method to call for query: " + name);
		}

		return ExceptionUtil.runWithSoftenedExceptions(() -> invokeHandlerMethod(method, name, parameters));
	}

	abstract protected Object invokeHandlerMethod(Method method, String name, List<JsonWrapper> parameters) throws Exception;

	protected Method getMethod(String name) {
		String methodName = "handle" + StringUtils.capitalize(name);
		return cachedMethods.computeIfAbsent(new ClassAndMethodName(targetObject.getClass(), methodName),
				classAndMethodName -> ReflectionUtil.findMethod(classAndMethodName.clazz, classAndMethodName.methodName));
	}

}
