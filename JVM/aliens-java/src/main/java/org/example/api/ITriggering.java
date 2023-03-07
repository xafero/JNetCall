package org.example.api;

public interface ITriggering extends AutoCloseable {

	interface PCallBack {
		boolean invoke(int hWnd, String lParam);
	}

	boolean enumWindows(PCallBack callback, int count);

	void addThresholdReached(ThresholdHandler handler);

	void removeThresholdReached(ThresholdHandler handler);

	interface ThresholdHandler {
		void invoke(Object sender, ThresholdEventArgs e);
	}

	void startPub(int count);
}
