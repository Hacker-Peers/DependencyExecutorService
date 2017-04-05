package org.hackerpeers.dependencyexecutorservice;

import java.util.concurrent.ExecutorService;

/**
 * @author @sberthiaume
 */
public class DependencyExecutors {
    public static DependencyExecutorService newDependencyExecutorWithDelegate(ExecutorService delegate) {
        return new DependencyExecutorServiceImpl(delegate);
    }
}
