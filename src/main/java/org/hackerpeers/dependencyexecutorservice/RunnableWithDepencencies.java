package org.hackerpeers.dependencyexecutorservice;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author @sberthiaume
 */
class RunnableWithDepencencies implements Runnable {
    private final Runnable delegate;
    private final Future<?>[] dependencies;

    RunnableWithDepencencies(Runnable delegate, Future<?> ... dependencies) {
        this.delegate = delegate;
        this.dependencies = dependencies;
    }

    @Override
    public void run() {
        try {
            for (Future<?> dependency : dependencies) {
                dependency.get();
            }
        } catch (InterruptedException|ExecutionException e) {
            throw new RuntimeException("Error waiting for dependant jobs", e);
        }
        delegate.run();
    }

    Runnable getDelegate() {
        return delegate;
    }
}
