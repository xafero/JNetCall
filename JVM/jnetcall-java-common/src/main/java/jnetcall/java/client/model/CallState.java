package jnetcall.java.client.model;

import jnetbase.java.threads.ManualResetEvent;

public class CallState {

    public ManualResetEvent SyncWait;

    public ManualResetEvent AsyncWait;

    public Object Result;

    public void set() {
        if (SyncWait != null) SyncWait.set();
        if (AsyncWait != null) AsyncWait.set();
    }
}
