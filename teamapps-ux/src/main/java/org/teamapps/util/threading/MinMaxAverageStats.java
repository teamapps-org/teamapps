package org.teamapps.util.threading;

/**
 * Caution: You should use {@link java.util.LongSummaryStatistics} whenever you can.
 * The only advantage of this class is it being immutable.
 */
public class MinMaxAverageStats {
	private final long min;
	private final long max;
	private final long total;
	private final long count;

	public MinMaxAverageStats() {
		this.min = Long.MAX_VALUE;
		this.max = 0;
		this.total = 0;
		this.count = 0;
	}

	public MinMaxAverageStats(long min, long max, long total, long count) {
		this.min = min;
		this.max = max;
		this.total = total;
		this.count = count;
	}

	public MinMaxAverageStats push(long time) {
		return new MinMaxAverageStats(Math.min(time, min), Math.max(time, max), this.total + time, this.count + 1);
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	public long getAvg() {
		return total / count;
	}

	public long getCount() {
		return count;
	}
}