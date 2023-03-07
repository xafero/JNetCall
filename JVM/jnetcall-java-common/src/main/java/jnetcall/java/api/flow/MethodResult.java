package jnetcall.java.api.flow;

import jnetbase.java.meta.ParamName;

public final class MethodResult implements ICall {

	private final short i;
	private final Object r;
	private final short s;

	public MethodResult(@ParamName("I") short i, @ParamName("R") Object r, @ParamName("S") short s) {
		this.i = i;
		this.r = r;
		this.s = s;
	}

	@Override
	public short I() {
		return i;
	}
	
	public Object R() {
		return r;
	}
	
	public short S() {
		return s;
	}
}
