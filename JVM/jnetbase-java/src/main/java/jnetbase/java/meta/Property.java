package jnetbase.java.meta;

import java.lang.reflect.Method;

public final class Property {

	private final String name;
	private final Method get;
	private final Method set;

	public Property(String name, Method get, Method set) {
		this.name = name;
		this.get = get;
		this.set = set;
	}

	public String Name() {
		return name;
	}

	public Method Get() {
		return get;
	}

	public Method Set() {
		return set;
	}
}
