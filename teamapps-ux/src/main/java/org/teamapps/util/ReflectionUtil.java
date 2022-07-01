/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReflectionUtil {

	public static List<Field> findFields(Class<?> clazz, Predicate<Field> predicate) {
		List<Field> matchingFields = new ArrayList<>();
		Class<?> c = clazz;
		while (c != null) {
			for (Field field : c.getDeclaredFields()) {
				if (predicate.test(field)) {
					matchingFields.add(field);
				}
			}
			c = c.getSuperclass();
		}
		return matchingFields;
	}

	public static Field findField(Class<?> clazz, Predicate<Field> predicate) {
		List<Field> fields = findFields(clazz, predicate);
		return fields.size() > 0 ? fields.get(0) : null;
	}

	public static Field findField(Class<?> clazz, String fieldName) {
		return findField(clazz, field -> field.getName().equals(fieldName));
	}

	public static List<Method> findMethods(Class<?> clazz, Predicate<Method> predicate) {
		List<Method> matchingMethods = new ArrayList<>();

		List<Class<?>> hierarchy = getAllExtendedOrImplementedTypesRecursively(clazz);
		for (Class<?> c : hierarchy) {
			for (Method method : c.getDeclaredMethods()) {
				if (predicate.test(method)) {
					matchingMethods.add(method);
				}
			}
		}
		return matchingMethods;
	}

	public static List<Class<?>> getAllExtendedOrImplementedTypesRecursively(Class<?> clazz) {
		List<Class<?>> res = new ArrayList<>();

		do {
			res.add(clazz);

			Class<?>[] interfaces = clazz.getInterfaces();
			for (Class<?> interf : interfaces) {
				res.add(interf);
				res.addAll(getAllExtendedOrImplementedTypesRecursively(interf));
			}

			clazz = clazz.getSuperclass();
		} while (clazz != null);

		return res.stream().distinct().collect(Collectors.toList());
	}

	public static Method findMethod(Class<?> clazz, Predicate<Method> predicate) {
		List<Method> methods = findMethods(clazz, predicate);
		return methods.size() > 0 ? methods.get(0) : null;
	}

	public static Method findMethod(Class<?> clazz, String methodName) {
		return findMethod(clazz, method -> method.getName().equals(methodName));
	}

	public static Method findMethodByName(Class<?> clazz, String methodName) {
		return findMethod(clazz, method -> method.getName().equals(methodName));
	}

	public static Method findGetter(Class<?> clazz, String propertyName) {
		String methodName = "get" + StringUtils.capitalize(propertyName);
		return findMethods(clazz,
				method -> (method.getName().equals(methodName) || method.getName().equals("is" + StringUtils.capitalize(propertyName))) && method.getParameterCount() == 0)
				.stream()
				.findFirst().orElse(null);
	}

	public static Method findSetter(Class<?> clazz, String propertyName) {
		String methodName = "set" + StringUtils.capitalize(propertyName);
		return findMethods(clazz, method -> method.getName().equals(methodName) && method.getParameterCount() == 1)
				.stream()
				.findFirst().orElse(null);
	}

	public static <V> V getPropertyValue(Object o, String propertyName) {
		return (V) invokeMethod(o, findGetter(o.getClass(), propertyName));
	}

	public static <V> void setProperty(Object o, String propertyName, V value) {
		invokeMethod(o, findSetter(o.getClass(), propertyName), value);
	}


	public static String toStringUsingReflection(Object o) {
		if (o == null) {
			return "";
		}
		Method toString = ReflectionUtil.findMethods(o.getClass(), method -> method.getName().equals("toString") && method.getParameterTypes().length == 0).stream()
				.findFirst().orElse(null);
		if (toString.getDeclaringClass() != Object.class) {
			return "" + o;
		}

		return ToStringBuilder.reflectionToString(o, new ToStringStyle() {
			{
				setUseShortClassName(true);
				setUseIdentityHashCode(false);
				this.setContentStart(" {");
				this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
				this.setFieldSeparatorAtStart(true);
				this.setContentEnd(SystemUtils.LINE_SEPARATOR + "}");
			}

			@Override
			public void appendSuper(StringBuffer buffer, String superToString) {
				super.appendSuper(buffer, superToString);

			}
		});
	}

	public static <RECORD> Object invokeMethod(RECORD object, Method method, Object... parameters) {
		try {
			return method.invoke(object, parameters);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Exception while invoking method " + method.getName(), e);
		} catch (IllegalArgumentException e) {
			String expectedParameterTypesString = Arrays.stream(method.getParameterTypes())
					.map(paramClass -> paramClass.getSimpleName())
					.collect(Collectors.joining(", "));
			String actualParameterTypesString = Arrays.stream(parameters)
					.map(p -> p == null ? "null" : p.getClass().getSimpleName())
					.collect(Collectors.joining(", "));
			throw new IllegalArgumentException("Could not invoke method " + method.getName() + "(" + expectedParameterTypesString + ") with given parameter types: " + actualParameterTypesString, e);
		}
	}

	public static Object readField(Object object, Field field, boolean makeAccessibleIfNecessary) {
		try {
			if (makeAccessibleIfNecessary && !field.canAccess(object)) {
				field.setAccessible(true);
			}
			return field.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void readField(Object object, String fieldName, boolean makeAccessibleIfNecessary)  {
		Field field = ReflectionUtil.findField(object.getClass(), fieldName);
		ReflectionUtil.readField(object, field, makeAccessibleIfNecessary);
	}

	public static void setField(Object object, Field field, Object value, boolean makeAccessibleIfNecessary) {
		try {
			if (makeAccessibleIfNecessary && !field.canAccess(object)) {
				field.setAccessible(true);
			}
			field.set(object, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setField(Object object, String fieldName, Object value, boolean makeAccessibleIfNecessary)  {
		Field field = ReflectionUtil.findField(object.getClass(), fieldName);
		ReflectionUtil.setField(object, field, value, makeAccessibleIfNecessary);
	}
}
