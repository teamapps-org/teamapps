package org.teamapps.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;
	private final boolean deamon;

	public NamedThreadFactory(String name, boolean deamon) {
		namePrefix = name + "-";
		this.deamon = deamon;
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(null, r, namePrefix + threadNumber.getAndIncrement());
		t.setDaemon(deamon);
		return t;
	}
}