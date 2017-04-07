package org.hackerpeers.dependencyexecutorservice;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author @sberthiaume
 */
class CallableWithDepencencies<T> implements Callable<T> {
    private final Callable<T> delegate;
    private final Future<?>[] dependencies;

    CallableWithDepencencies(Callable<T> delegate, Future<?> ... dependencies) {
        this.delegate = delegate;
        this.dependencies = dependencies;
    }

    @Override
    public T call() throws Exception {
        for (Future<?> dependency : dependencies) {
            dependency.get();
        }
        return delegate.call();
    }

    Callable<T> getDelegate() {
        return delegate;
    }
}
