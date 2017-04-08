package org.hackerpeers.dependencyexecutorservice;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;

/**
 * @author @sberthiaume
 */
public class DependencyExecutorsIT{
    private static final int NB_THREADS = 10;
    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String D = "D";
    private static final String E = "E";
    private static final String F = "F";
    private static final String G = "G";
    private static final String H = "H";
    private static final String I = "I";
    private static final String J = "J";
    private static final String K = "K";
    private static final String L = "L";
    private static final String M = "M";


    @DataProvider(name = "dependencyExecutorServiceProvider")
    public Object[][] dependencyExecutorServiceProvider() {
        List<Object[]> dependencyServices = new ArrayList<>(6);

        dependencyServices.add(new Object[] {"newFixedThreadPool(int)",                 DependencyExecutors.newFixedThreadPool(NB_THREADS), new CountDownLatch(1)});
        dependencyServices.add(new Object[] {"newFixedThreadPool(int, ThreadFactory)",  DependencyExecutors.newFixedThreadPool(NB_THREADS, new TestThreadFactory("newFixedThreadPool")), new CountDownLatch(1)});
        dependencyServices.add(new Object[] {"newSingleThreadExecutor()",               DependencyExecutors.newSingleThreadExecutor(), new CountDownLatch(1)});
        dependencyServices.add(new Object[] {"newSingleThreadExecutor(ThreadFactory)",  DependencyExecutors.newSingleThreadExecutor(new TestThreadFactory("newSingleThreadExecutor")), new CountDownLatch(1)});
        dependencyServices.add(new Object[] {"newCachedThreadPool()",                   DependencyExecutors.newCachedThreadPool(), new CountDownLatch(1)});
        dependencyServices.add(new Object[] {"newCachedThreadPool(ThreadFactory)",      DependencyExecutors.newCachedThreadPool(new TestThreadFactory("newCachedThreadPool")), new CountDownLatch(1)});

        return dependencyServices.toArray(new Object[dependencyServices.size()][]);
    }

    /**
     * Build a graph like what is illustrated below and make sure nodes are executes in the right order.
     * <br/> <img src="../../../../resources/DAG.png" />
     * @throws InterruptedException Should not happen.
     */
    @Test(dataProvider = "dependencyExecutorServiceProvider")
    public void testScenario(@SuppressWarnings("unused") String testName, DependencyExecutorService subject, CountDownLatch latch) throws InterruptedException {
        // Given
        final Queue<String> appendTo = new ConcurrentLinkedQueue<>();

        // When
        Future<?> kJob = subject.submit(new TestJob(latch, appendTo, K));
        Future<?> lJob = subject.submit(new TestJob(latch, appendTo, L));
        Future<?> mJob = subject.submit(new TestJob(latch, appendTo, M));

        Future<?> fJob = subject.submitWithDependencies(new TestJob(latch, appendTo, F));
        Future<?> gJob = subject.submitWithDependencies(new TestJob(latch, appendTo, G), kJob);
        Future<?> hJob = subject.submitWithDependencies(new TestJob(latch, appendTo, H), kJob);
        Future<?> iJob = subject.submitWithDependencies(new TestJob(latch, appendTo, I), lJob, mJob);
        Future<?> jJob = subject.submitWithDependencies(new TestJob(latch, appendTo, J), mJob);

        Future<?> cJob = subject.submitWithDependencies(new TestJob(latch, appendTo, C), fJob, gJob, hJob);
        Future<?> dJob = subject.submitWithDependencies(new TestJob(latch, appendTo, D), iJob);
        Future<?> eJob = subject.submitWithDependencies(new TestJob(latch, appendTo, E), hJob, iJob, jJob);

        subject.submitWithDependencies(new TestJob(latch, appendTo, A), cJob, dJob);
        subject.submitWithDependencies(new TestJob(latch, appendTo, B), eJob);

        // Latch serves as a way to make sure no job completes before all jobs are created; just a way to enforce some sort of concurrency.
        latch.countDown();
        subject.shutdown();
        assertThat("Thread pool termination timeout", subject.awaitTermination(30, TimeUnit.SECONDS), is(true));
        List<String> results = Arrays.asList(appendTo.toArray(new String[appendTo.size()]));

        // Then
        assertThat(results, hasSize(13));
        assertThat(results, containsInAnyOrder(A, B, C, D, E, F, G, H, I, J, K, L, M));

        assertThat(results.indexOf(K), is(lessThan(results.indexOf(G))));
        assertThat(results.indexOf(K), is(lessThan(results.indexOf(H))));
        assertThat(results.indexOf(L), is(lessThan(results.indexOf(I))));
        assertThat(results.indexOf(M), is(lessThan(results.indexOf(I))));
        assertThat(results.indexOf(M), is(lessThan(results.indexOf(J))));

        assertThat(results.indexOf(F), is(lessThan(results.indexOf(C))));
        assertThat(results.indexOf(G), is(lessThan(results.indexOf(C))));
        assertThat(results.indexOf(H), is(lessThan(results.indexOf(C))));
        assertThat(results.indexOf(H), is(lessThan(results.indexOf(E))));
        assertThat(results.indexOf(I), is(lessThan(results.indexOf(D))));
        assertThat(results.indexOf(I), is(lessThan(results.indexOf(E))));
        assertThat(results.indexOf(J), is(lessThan(results.indexOf(E))));

        assertThat(results.indexOf(C), is(lessThan(results.indexOf(A))));
        assertThat(results.indexOf(D), is(lessThan(results.indexOf(A))));
        assertThat(results.indexOf(E), is(lessThan(results.indexOf(B))));

        // System.out.println(Arrays.toString(results.toArray()));
    }


    private static final class TestJob implements Runnable {
        private final CountDownLatch latch;
        private final Queue<String> appendTo;
        private final String appendValue;

        TestJob(CountDownLatch latch, Queue<String> appendTo, String appendValue) {
            this.latch = latch;
            this.appendTo = appendTo;
            this.appendValue = appendValue;
        }

        @Override
        public void run() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException("Error during latch wait", e);
            }
            appendTo.add(appendValue);
        }
    }

    private static final class TestThreadFactory implements ThreadFactory {
        private int counter = 0;
        private String prefix = "";

        TestThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, prefix + "-" + counter++);
        }
    }
}