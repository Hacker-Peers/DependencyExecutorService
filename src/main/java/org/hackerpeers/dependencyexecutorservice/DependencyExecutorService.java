package org.hackerpeers.dependencyexecutorservice;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Variant of the native {@link ExecutorService} that simplify the creation of tasks with dependencies from one another.
 * @author @sberthiaume
 * @see ExecutorService
 */
public interface DependencyExecutorService extends ExecutorService {
    /**
     * Same as {@link ExecutorService#submit(Callable)}, but will wait for all dependent futures to be completed before running the task.
     * @param task the task to submit
     * @param dependsOn (optional) one or many tasks that must complete before this task can run
     * @param <T> The result type returned by this task
     * @return a Future representing pending completion of the task
     */
    <T> Future<T> submit(Callable<T> task, Future<?> ... dependsOn);

    /**
     * Same as {@link ExecutorService#submit(Runnable)}, but will wait for all dependent futures to be completed before running the task.
     * @param task the task to submit
     * @param dependsOn (optional) one or many tasks that must complete before this task can run
     * @return a Future representing pending completion of the task
     */
    Future<?> submit(Runnable task, Future<?> ... dependsOn);

    /**
     * Same as {@link ExecutorService#submit(Runnable, Object)}, but will wait for all dependent futures to be completed before running the task.
     * @param task the task to submit
     * @param result the result to return
     * @param dependsOn a Future representing pending completion of the task
     * @param <T> The result type given in parameter
     * @return
     */
    <T> Future<T> submit(Runnable task, T result, Future<?> ... dependsOn);

}
