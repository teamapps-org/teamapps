package org.teamapps.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;

public class ExceptionUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ExceptionUtil() {}

	public static void softenExceptions(RunnableWithExceptions runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			throw softenedException(e);
		}
	}

	public static <V> V softenExceptions(Callable<V> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw softenedException(e);
		}
	}

	public static <T extends RuntimeException> T softenedException(final Throwable e) {
		return uncheck(e);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> T uncheck(Throwable throwable) throws T {
		throw (T) throwable;
	}

}
