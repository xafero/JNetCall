package org.example.api;

import java.time.LocalDateTime;

public interface ITriggering extends AutoCloseable {

    interface PCallBack {
        boolean invoke(int hWnd, String lParam);
    }

    boolean enumWindows(PCallBack callback, int count);

    void addThresholdReached(ThresholdHandler handler);
    void removeThresholdReached(ThresholdHandler handler);

    void startPub(int count);

    record ThresholdEventArgs(int Threshold, LocalDateTime TimeReached) {
    }

    interface ThresholdHandler {
        void invoke(Object sender, ThresholdEventArgs e);
    }
}
