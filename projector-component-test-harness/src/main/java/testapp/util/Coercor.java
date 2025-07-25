package testapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Coercor {

	private static final Logger LOGGER = LoggerFactory.getLogger(Coercor.class);

	public static <T> T coerce(Object value, Class<T> targetType) {
		if (value == null) {
			return null;
		} else if (targetType.isAssignableFrom(value.getClass())) {
			return (T) value;
		} else if (isNumberType(targetType) && value instanceof Number) {
			if (targetType == Byte.class || targetType == byte.class) {
				return (T) (Byte) ((Number) value).byteValue();
			} else if (targetType == Short.class || targetType == short.class) {
				return (T) (Short) ((Number) value).shortValue();
			} else if (targetType == Integer.class || targetType == int.class) {
				return (T) (Integer) ((Number) value).intValue();
			} else if (targetType == Long.class || targetType == long.class) {
				return (T) (Long) ((Number) value).longValue();
			} else if (targetType == Float.class || targetType == float.class) {
				return (T) (Float) ((Number) value).floatValue();
			} else if (targetType == Double.class || targetType == double.class) {
				return (T) (Double) ((Number) value).doubleValue();
			}
		} else if (targetType == String.class) {
			return (T) value.toString();
		}
		return (T) value;
	}

	private static <T> boolean isNumberType(Class<T> targetType) {
		return Number.class.isAssignableFrom(targetType) ||
				targetType == byte.class ||
				targetType == short.class ||
				targetType == int.class ||
				targetType == long.class ||
				targetType == float.class ||
				targetType == double.class;
	}

}