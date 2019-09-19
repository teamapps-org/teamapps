package org.teamapps.ux.task;

import org.jetbrains.annotations.NotNull;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.ux.task.function.ProgressReportingBiConsumer;
import org.teamapps.ux.task.function.ProgressReportingBiFunction;
import org.teamapps.ux.task.function.ProgressReportingConsumer;
import org.teamapps.ux.task.function.ProgressReportingFunction;
import org.teamapps.ux.task.function.ProgressReportingRunnable;
import org.teamapps.ux.task.function.ProgressReportingSupplier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ProgressCompletableFuture<T> extends CompletableFuture<T> {

	// basically copy and paste from CompletableFuture...
	private static final boolean USE_COMMON_POOL = (ForkJoinPool.getCommonPoolParallelism() > 1);
	// basically copy and paste from CompletableFuture...
	public static final Executor ASYNC_POOL = USE_COMMON_POOL ? ForkJoinPool.commonPool() : r -> new Thread(r).start();

	private final Progress progress;

	public ProgressCompletableFuture() {
		this(new Progress());
	}

	private ProgressCompletableFuture(Progress progress) {
		this.progress = progress;
	}

	public ObservableProgress getProgress() {
		return progress;
	}

	@Override
	public <U> ProgressCompletableFuture<U> newIncompleteFuture() {
		return new ProgressCompletableFuture<>();
	}

	// ========== static =========================


	public static <U> ProgressCompletableFuture<U> supplyAsync(ProgressReportingSupplier<U> supplier) {
		return supplyAsync(supplier, ASYNC_POOL);
	}

	public static <U> ProgressCompletableFuture<U> supplyAsync(ProgressReportingSupplier<U> supplier, Executor executor) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		executor.execute(() -> {
			future.progress.start();
			try {
				U result = supplier.get(future.progress);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(result);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
		});
		return future;
	}

	public static ProgressCompletableFuture<Void> runAsync(ProgressReportingRunnable runnable) {
		return runAsync(runnable, ASYNC_POOL);
	}

	public static ProgressCompletableFuture<Void> runAsync(ProgressReportingRunnable runnable, Executor executor) {
		final ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		executor.execute(runWrapped(runnable, future));
		return future;
	}


	// ========== with session context ===========

	public <U> ProgressCompletableFuture<U> thenApplyWithSessionContext(Function<? super T, ? extends U> fn) {
		SessionContext sessionContext = CurrentSessionContext.get();
		return thenApply(t -> {
			Object[] result = new Object[1];
			sessionContext.runWithContext(() -> result[0] = fn.apply(t));
			return (U) result[0];
		});
	}

	public ProgressCompletableFuture<Void> thenAcceptWithCurrentSessionContext(Consumer<? super T> action) {
		SessionContext sessionContext = CurrentSessionContext.get();
		return thenAccept(t -> sessionContext.runWithContext(() -> action.accept(t)));
	}

	public ProgressCompletableFuture<Void> thenRunWithCurrentSessionContext(Runnable action) {
		SessionContext sessionContext = CurrentSessionContext.get();
		return thenRun(() -> sessionContext.runWithContext(action));
	}

	public ProgressCompletableFuture<T> whenCompleteWithCurrentSessionContext(BiConsumer<? super T, ? super Throwable> action) {
		SessionContext sessionContext = CurrentSessionContext.get();
		return whenComplete((t, throwable) -> sessionContext.runWithContext(() -> action.accept(t, throwable)));
	}

	public <U> ProgressCompletableFuture<U> handleWithCurrentSessionContext(BiFunction<? super T, Throwable, ? extends U> fn) {
		SessionContext sessionContext = CurrentSessionContext.get();
		return handle((t, throwable) -> {
			Object[] result = new Object[1];
			sessionContext.runWithContext(() -> result[0] = fn.apply(t, throwable));
			return (U) result[0];
		});
	}

	public ProgressCompletableFuture<T> exceptionallyWithCurrentSessionContext(Function<Throwable, ? extends T> fn) {
		SessionContext sessionContext = CurrentSessionContext.get();
		return exceptionally(throwable -> {
			Object[] result = new Object[1];
			sessionContext.runWithContext(() -> result[0] = fn.apply(throwable));
			return (T) result[0];
		});
	}

	// ========= simple overrides =============

	@Override
	public <U> ProgressCompletableFuture<U> thenApply(Function<? super T, ? extends U> fn) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.thenApply(applyWrapped(fn, future));
		return future;
	}

	@Override
	public <U> ProgressCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.thenApplyAsync(applyWrapped(fn, future));
		return future;
	}

	@Override
	public <U> ProgressCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.thenApplyAsync(applyWrapped(fn, future), executor);
		return future;
	}

	@NotNull
	private <U> Function<T, Object> applyWrapped(Function<? super T, ? extends U> fn, ProgressCompletableFuture<U> future) {
		return t -> {
			future.progress.start();
			try {
				U result = fn.apply(t);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(result);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
			return null;
		};
	}

	@Override
	public ProgressCompletableFuture<Void> thenAccept(Consumer<? super T> action) {
		ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenAccept(acceptWrapped(action, future));
		return future;
	}

	@Override
	public ProgressCompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
		ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenAcceptAsync(acceptWrapped(action, future));
		return future;
	}

	@Override
	public ProgressCompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
		ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenAcceptAsync(acceptWrapped(action, future), executor);
		return future;
	}

	private Consumer<T> acceptWrapped(Consumer<? super T> action, ProgressCompletableFuture<Void> future) {
		return t -> {
			future.progress.start();
			try {
				action.accept(t);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(null);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
		};
	}

	public ProgressCompletableFuture<Void> thenRun(Runnable action) {
		final ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenRun(runWrapped(action, future));
		return future;
	}

	public ProgressCompletableFuture<Void> thenRunAsync(Runnable action) {
		final ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenRunAsync(runWrapped(action, future));
		return future;
	}

	public ProgressCompletableFuture<Void> thenRunAsync(Runnable action, Executor executor) {
		final ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenRunAsync(runWrapped(action, future), executor);
		return future;
	}

	private static Runnable runWrapped(Runnable action, ProgressCompletableFuture<Void> future) {
		return () -> {
			future.progress.start();
			try {
				action.run();
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(null);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
		};
	}

	public ProgressCompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
		final ProgressCompletableFuture<T> future = new ProgressCompletableFuture<>();
		super.whenComplete(whenCompleteWrapped(action, future));
		return future;
	}

	public ProgressCompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
		final ProgressCompletableFuture<T> future = new ProgressCompletableFuture<>();
		super.whenCompleteAsync(whenCompleteWrapped(action, future));
		return future;
	}

	public ProgressCompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
		final ProgressCompletableFuture<T> future = new ProgressCompletableFuture<>();
		super.whenCompleteAsync(whenCompleteWrapped(action, future), executor);
		return future;
	}

	private BiConsumer<T, Throwable> whenCompleteWrapped(BiConsumer<? super T, ? super Throwable> action, ProgressCompletableFuture<T> future) {
		return (t, throwable) -> {
			future.progress.start();
			try {
				action.accept(t, throwable);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
			} catch (Exception e) {
				future.progress.markFailed();
			} finally {
				if (throwable != null) {
					future.completeExceptionally(throwable);
				} else {
					future.complete(t);
				}
			}
		};
	}

	public <U> ProgressCompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.handle(handleWrapped(fn, future));
		return future;
	}

	public <U> ProgressCompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.handleAsync(handleWrapped(fn, future));
		return future;
	}

	public <U> ProgressCompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.handleAsync(handleWrapped(fn, future), executor);
		return future;
	}

	private <U> BiFunction<T, Throwable, U> handleWrapped(BiFunction<? super T, Throwable, ? extends U> fn, ProgressCompletableFuture<U> future) {
		return (t, throwable) -> {
			future.progress.start();
			try {
				U result = fn.apply(t, throwable);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(result);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
			return null;
		};
	}

	@Override
	public ProgressCompletableFuture<T> toCompletableFuture() {
		return (ProgressCompletableFuture<T>) super.toCompletableFuture();
	}

	public ProgressCompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
		final ProgressCompletableFuture<T> future = new ProgressCompletableFuture<>();
		super.exceptionally(throwable -> {
			future.progress.start();
			try {
				T result = fn.apply(throwable);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(result);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
			return null;
		});
		return future;
	}

	public ProgressCompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor) {
		return (ProgressCompletableFuture<T>) super.completeAsync(completeWrapped(supplier), executor);
	}

	public ProgressCompletableFuture<T> completeAsync(Supplier<? extends T> supplier) {
		return (ProgressCompletableFuture<T>) super.completeAsync(completeWrapped(supplier));
	}

	private Supplier<T> completeWrapped(Supplier<? extends T> supplier) {
		return () -> {
			this.progress.start();
			try {
				T result = supplier.get();
				this.progress.markCompleted(); // will get ignored if the progress is already in a final state
				this.complete(result);
			} catch (Exception e) {
				this.progress.markFailed();
				this.completeExceptionally(e);
			}
			return null;
		};
	}

	@Override
	public ProgressCompletableFuture<T> orTimeout(long timeout, TimeUnit unit) {
		return (ProgressCompletableFuture<T>) super.orTimeout(timeout, unit);
	}

	@Override
	public ProgressCompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit) {
		return (ProgressCompletableFuture<T>) super.completeOnTimeout(value, timeout, unit);
	}


	// ========== overloaded methods with progress ==================

	public <U> ProgressCompletableFuture<U> thenApply(ProgressReportingFunction<? super T, ? extends U> fn) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.thenApply(applyWrapped(fn, future));
		return future;
	}

	public <U> ProgressCompletableFuture<U> thenApplyAsync(ProgressReportingFunction<? super T, ? extends U> fn) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.thenApplyAsync(applyWrapped(fn, future));
		return future;
	}

	public <U> ProgressCompletableFuture<U> thenApplyAsync(ProgressReportingFunction<? super T, ? extends U> fn, Executor executor) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.thenApplyAsync(applyWrapped(fn, future), executor);
		return future;
	}

	private <U> Function<T, Object> applyWrapped(ProgressReportingFunction<? super T, ? extends U> fn, ProgressCompletableFuture<U> future) {
		return t -> {
			future.progress.start();
			try {
				U result = fn.apply(t, future.progress);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(result);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
			return null;
		};
	}

	public ProgressCompletableFuture<Void> thenAccept(ProgressReportingConsumer<? super T> action) {
		final ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenAccept(acceptWrapped(action, future));
		return future;
	}

	public ProgressCompletableFuture<Void> thenAcceptAsync(ProgressReportingConsumer<? super T> action) {
		final ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenAcceptAsync(acceptWrapped(action, future));
		return future;
	}

	public ProgressCompletableFuture<Void> thenAcceptAsync(ProgressReportingConsumer<? super T> action, Executor executor) {
		final ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenAcceptAsync(acceptWrapped(action, future), executor);
		return future;
	}

	private Consumer<T> acceptWrapped(ProgressReportingConsumer<? super T> action, ProgressCompletableFuture<Void> future) {
		return t -> {
			future.progress.start();
			try {
				action.accept(t, future.progress);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(null);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
		};
	}

	public ProgressCompletableFuture<Void> thenRun(ProgressReportingRunnable action) {
		final ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenRun(runWrapped(action, future));
		return future;
	}

	public ProgressCompletableFuture<Void> thenRunAsync(ProgressReportingRunnable action) {
		final ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenRunAsync(runWrapped(action, future));
		return future;
	}

	public ProgressCompletableFuture<Void> thenRunAsync(ProgressReportingRunnable action, Executor executor) {
		final ProgressCompletableFuture<Void> future = new ProgressCompletableFuture<>();
		super.thenRunAsync(runWrapped(action, future), executor);
		return future;
	}

	private static Runnable runWrapped(ProgressReportingRunnable action, ProgressCompletableFuture<Void> future) {
		return () -> {
			future.progress.start();
			try {
				action.run(future.progress);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(null);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
		};
	}

	public ProgressCompletableFuture<T> whenComplete(ProgressReportingBiConsumer<? super T, ? super Throwable> action) {
		final ProgressCompletableFuture<T> future = new ProgressCompletableFuture<>();
		super.whenComplete(whenCompleteWrapped(action, future));
		return future;
	}

	public ProgressCompletableFuture<T> whenCompleteAsync(ProgressReportingBiConsumer<? super T, ? super Throwable> action) {
		final ProgressCompletableFuture<T> future = new ProgressCompletableFuture<>();
		super.whenCompleteAsync(whenCompleteWrapped(action, future));
		return future;
	}

	public ProgressCompletableFuture<T> whenCompleteAsync(ProgressReportingBiConsumer<? super T, ? super Throwable> action, Executor executor) {
		final ProgressCompletableFuture<T> future = new ProgressCompletableFuture<>();
		super.whenCompleteAsync(whenCompleteWrapped(action, future), executor);
		return future;
	}

	private BiConsumer<T, Throwable> whenCompleteWrapped(ProgressReportingBiConsumer<? super T, ? super Throwable> action, ProgressCompletableFuture<T> future) {
		return (t, throwable) -> {
			future.progress.start();
			try {
				action.accept(t, throwable, future.progress);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
			} catch (Exception e) {
				future.progress.markFailed();
			} finally {
				if (throwable != null) {
					future.completeExceptionally(throwable);
				} else {
					future.complete(t);
				}
			}
		};
	}

	public <U> ProgressCompletableFuture<U> handle(ProgressReportingBiFunction<? super T, Throwable, ? extends U> fn) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.handle(handleWrapped(fn, future));
		return future;
	}

	public <U> ProgressCompletableFuture<U> handleAsync(ProgressReportingBiFunction<? super T, Throwable, ? extends U> fn) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.handleAsync(handleWrapped(fn, future));
		return future;
	}

	public <U> ProgressCompletableFuture<U> handleAsync(ProgressReportingBiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
		final ProgressCompletableFuture<U> future = new ProgressCompletableFuture<>();
		super.handleAsync(handleWrapped(fn, future), executor);
		return future;
	}

	private <U> BiFunction<T, Throwable, U> handleWrapped(ProgressReportingBiFunction<? super T, Throwable, ? extends U> fn, ProgressCompletableFuture<U> future) {
		return (t, throwable) -> {
			future.progress.start();
			try {
				U result = fn.apply(t, throwable, future.progress);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(result);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
			return null;
		};
	}

	public ProgressCompletableFuture<T> exceptionally(ProgressReportingFunction<Throwable, ? extends T> fn) {
		final ProgressCompletableFuture<T> future = new ProgressCompletableFuture<>();
		super.exceptionally(throwable -> {
			future.progress.start();
			try {
				T result = fn.apply(throwable, future.progress);
				future.progress.markCompleted(); // will get ignored if the progress is already in a final state
				future.complete(result);
			} catch (Exception e) {
				future.progress.markFailed();
				future.completeExceptionally(e);
			}
			return null;
		});
		return future;
	}

	public ProgressCompletableFuture<T> completeAsync(ProgressReportingSupplier<? extends T> supplier, Executor executor) {
		return (ProgressCompletableFuture<T>) super.completeAsync(completeWrapped(supplier), executor);
	}

	public ProgressCompletableFuture<T> completeAsync(ProgressReportingSupplier<? extends T> supplier) {
		return (ProgressCompletableFuture<T>) super.completeAsync(completeWrapped(supplier));
	}

	private Supplier<T> completeWrapped(ProgressReportingSupplier<? extends T> supplier) {
		return () -> {
			this.progress.start();
			try {
				T result = supplier.get(this.progress);
				this.progress.markCompleted(); // will get ignored if the progress is already in a final state
				this.complete(result);
			} catch (Exception e) {
				this.progress.markFailed();
				this.completeExceptionally(e);
			}
			return null;
		};
	}

	/* ========== TODO ==============
		public <U, V> ProgressingCompletableFuture<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn)
		public <U, V> ProgressingCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn)
		public <U, V> ProgressingCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor)
		public <U> ProgressingCompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action)
		public <U> ProgressingCompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action)
		public <U> ProgressingCompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor)
		public ProgressingCompletableFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action)
		public ProgressingCompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action)
		public ProgressingCompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor)
		public <U> ProgressingCompletableFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn)
		public <U> ProgressingCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn)
		public <U> ProgressingCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor)
		public ProgressingCompletableFuture<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action)
		public ProgressingCompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action)
		public ProgressingCompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor)
		public ProgressingCompletableFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action)
		public ProgressingCompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action)
		public ProgressingCompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor)
		public <U> ProgressingCompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn)
		public <U> ProgressingCompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn)
		public <U> ProgressingCompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor)
	 */
}
