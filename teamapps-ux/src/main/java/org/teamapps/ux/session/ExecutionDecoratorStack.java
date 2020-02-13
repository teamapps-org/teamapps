package org.teamapps.ux.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExecutionDecoratorStack {

	private final List<ExecutionDecorator> decorators = Collections.synchronizedList(new ArrayList<>());

	public void addOuterDecorator(ExecutionDecorator decorator) {
		decorators.add(decorator);
	}

	public void addInnerDecorator(ExecutionDecorator decorator) {
		decorators.add(0, decorator);
	}

	public void removeDecorator(ExecutionDecorator decorator) {
		decorators.remove(decorator);
	}

	public Runnable createWrappedRunnable(Runnable r) {
		if (decorators.isEmpty()) {
			return r;
		}
		synchronized (decorators) {
			Runnable outerRunnable = r;
			for (ExecutionDecorator decorator : decorators) {
				final Runnable innerRunnable = outerRunnable;
				outerRunnable = () -> {
					decorator.wrapExecution(innerRunnable);
				};
			}
			return outerRunnable;
		}
	}

}
