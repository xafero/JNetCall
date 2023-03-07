package org.example.api;

import java.time.LocalDateTime;

public final class ThresholdEventArgs {

	private final int threshold;
	private final LocalDateTime timeReached;

	public ThresholdEventArgs(int threshold, LocalDateTime timeReached) {
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
