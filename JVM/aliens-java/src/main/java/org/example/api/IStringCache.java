package org.example.api;

public interface IStringCache {

    void set(int key, String value);
    String get(int key) throws UnsupportedOperationException;
    void delete(int key);
    int getSize();
}