package org.traccar.broker;

public interface IPooledObjectPolicy<T> {
    T create();
    boolean pool(T obj);
    T get();
}
