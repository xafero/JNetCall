package jnetcall.java.api.flow;

import jnetbase.java.meta.ParamName;

public final class MethodCall implements ICall {

	private final short i;
	private final String c;
	private final String m;
	private final Object[] a;

	public MethodCall(@ParamName("I") short i, @ParamName("C") String c, @ParamName("M") String m, @ParamName("A") Object[] a) {
		this.i = i;
		this.c = c;
		this.m = m;
		this.a = a;
	}

	@Override
	public short I() {
		return i;
	}

	public String C() {
		return c;
	}
	
	public String M() {
		return m;
	}
	
	public Object[] A() {
		return a;
	}
}
