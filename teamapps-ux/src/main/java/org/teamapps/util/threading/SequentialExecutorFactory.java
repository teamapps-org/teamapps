package org.teamapps.util.threading;

import java.util.concurrent.ExecutorService;

/**
 * Creates {@link ExecutorService}s that will guarantee the sequential execution of submitted tasks.
 * Note that this guarantee is <b>NOT</b> given with {@link java.util.concurrent.ThreadPoolExecutor}.
 * <p>
 * The most naive implementation will just return {@link java.util.concurrent.Executors#newSingleThreadExecutor()},
 * but most operating systems have limits on the number of threads allowed, so this is not a good solution,
 * and not a good idea for other reasons, too.
 */
public interface SequentialExecutorFactory {

	ExecutorService createExecutor();

}
