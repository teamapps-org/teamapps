package testapp.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Method;

import static org.teamapps.commons.util.ReflectionUtil.findMethods;
import static org.teamapps.commons.util.ReflectionUtil.invokeMethod;

public class ReflectionUtil {

	public static String toStringUsingReflection(Object o) {
		if (o == null) {
			return "";
		}
		Method toString = findMethods(o.getClass(), method -> method.getName().equals("toString") && method.getParameterTypes().length == 0).stream()
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
}
