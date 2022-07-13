package jnetbase.java.meta;

import java.lang.reflect.Method;

public record Property(String Name, Method Get, Method Set) {
}
