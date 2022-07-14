package jnetcall.java.api.flow;

public enum MethodStatus {
    Unknown(0),
    ClassNotFound(404),
    MethodNotFound(406),
    MethodFailed(500),
    Continue(100),
    Ok(200);

    private final short value;

    MethodStatus(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }
}