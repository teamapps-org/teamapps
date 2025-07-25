package testapp;

import org.teamapps.projector.session.SessionContext;

import java.util.Arrays;
import java.util.stream.Collectors;

import static testapp.util.ReflectionUtil.toStringUsingReflection;

public interface ComponentTestContext {

	SessionContext getSessionContext();

	void printLineToConsole(String s);

	default void printInvocationToConsole(String methodName, Object... args) {
		printLineToConsole("COMPONENT INVOCATION: " + methodName + "(" + Arrays.stream(args)
				.map(o -> toStringUsingReflection(o))
				.collect(Collectors.joining(", ")) + ")");
	}
}
