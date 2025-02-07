package org.teamapps.projector.dsl.generate;

import java.io.IOException;

class ErrorMessageUtil {

	public static void runWithExceptionMessagePrefix(RunnableWithException runnable, String errorMessageContext) throws IOException {
		try {
			runnable.run();
		} catch (Exception e) {
			throw new DtoGeneratorException(errorMessageContext + ": " + e.getMessage(), e);
		}
	}

	public interface RunnableWithException {
		void run() throws Exception;
	}

}
