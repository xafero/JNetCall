package org.example.api;

import java.time.LocalDateTime;

public final class ThresholdEventArgs {

	private final int threshold;
	private final LocalDateTime timeReached;

	public ThresholdEventArgs(@Deprecated(since = "Threshold") int threshold, @Deprecated(since = "TimeReached") LocalDateTime timeReached) {
		this.threshold = threshold;
		this.timeReached = timeReached;
	}

	public int Threshold() {
		return threshold;
	}

	public LocalDateTime TimeReached() {
		return timeReached;
	}
}
