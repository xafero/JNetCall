package jnetbase.java;

import java.lang.reflect.Method;

public record Property(String Name, Method Get, Method Set) {
}
