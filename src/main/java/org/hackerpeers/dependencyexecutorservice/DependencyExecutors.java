package org.hackerpeers.dependencyexecutorservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

/**
 * Factory utility class for new DependencyExecutorServices
 * @author @sberthiaume
 */
@SuppressWarnings("WeakerAccess") // This is a self-contained lib, nothing is outside the package itself.
public class DependencyExecutors {
    private DependencyExecutors(){}

    /**
     * Create a new {@link DependencyExecutorService} based on the provided {@link ExecutorService}. It would be advisable to only use ExecutorServices relying on FIFO queues (such as
     * {@link LinkedBlockingQueue}) to prevent thread starvation; most ExecutorServices returned by {@link Executors#newCachedThreadPool()},
     * {@link java.util.concurrent.Executors#newFixedThreadPool(int)} and their overloaded variants are perfectly fine.
     * @param delegate the ExecutorService to build the DependencyExecutorService on top of
     * @return a new DependencyExecutorService
     */
    static DependencyExecutorService newDependencyExecutorWithDelegate(ExecutorService delegate) {
        return new DependencyExecutorServiceImpl(delegate);
    }

    /**
     * @see Executors#newFixedThreadPool(int)
     */
    public static DependencyExecutorService newFixedThreadPool(int nThreads) {
        return newDependencyExecutorWithDelegate(Executors.newFixedThreadPool(nThreads));
    }

    /**
     * @see Executors#newFixedThreadPool(int, ThreadFactory)
     */
    public static DependencyExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return newDependencyExecutorWithDelegate(Executors.newFixedThreadPool(nThreads, threadFactory));
    }

    /**
     * @see Executors#newSingleThreadExecutor()
     */
    public static DependencyExecutorService newSingleThreadExecutor() {
        return newDependencyExecutorWithDelegate(Executors.newSingleThreadExecutor());
    }

    /**
     * @see Executors#newSingleThreadExecutor(ThreadFactory)
     */
    public static DependencyExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        return newDependencyExecutorWithDelegate(Executors.newSingleThreadExecutor(threadFactory));
    }

    /**
     * @see Executors#newCachedThreadPool()
     */
    public static DependencyExecutorService newCachedThreadPool() {
        return newDependencyExecutorWithDelegate(Executors.newCachedThreadPool());
    }

    /**
     * @see Executors#newCachedThreadPool(ThreadFactory)
     */
    public static DependencyExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return newDependencyExecutorWithDelegate(Executors.newCachedThreadPool(threadFactory));
    }
}
