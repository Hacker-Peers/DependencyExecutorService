package org.hackerpeers.dependencyexecutorservice;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;

/**
 * @author @sberthiaume
 */
public class DependencyExecutorsIT {
    private static final long JOB_DELAY = 500L;
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

    private DependencyExecutorService subject;
    private Future<?> initialDelay;

    @BeforeMethod
    public void setUp() throws Exception {
        subject = DependencyExecutors.newDependencyExecutorWithDelegate(Executors.newFixedThreadPool(NB_THREADS));
        initialDelay = subject.submit(new DelayJob(JOB_DELAY));
    }

    /**
     * Build a graph like what is illustrated below and make sure nodes are executes in the right order.
     * <br/> <img src="../../../../resources/DAG.png" />
     * @throws InterruptedException Should not happen.
     */
    @Test
    public void completeTest() throws InterruptedException {
        // Given
        final Queue<String> appendTo = new ConcurrentLinkedQueue<>();

        // When
        Future<?> kJob = subject.submit(new TestJob(appendTo, K), initialDelay);
        Future<?> lJob = subject.submit(new TestJob(appendTo, L), initialDelay);
        Future<?> mJob = subject.submit(new TestJob(appendTo, M), initialDelay);

        Future<?> fJob = subject.submit(new TestJob(appendTo, F), initialDelay);
        Future<?> gJob = subject.submit(new TestJob(appendTo, G), initialDelay, kJob);
        Future<?> hJob = subject.submit(new TestJob(appendTo, H), initialDelay, kJob);
        Future<?> iJob = subject.submit(new TestJob(appendTo, I), initialDelay, lJob, mJob);
        Future<?> jJob = subject.submit(new TestJob(appendTo, J), initialDelay, mJob);

        Future<?> cJob = subject.submit(new TestJob(appendTo, C), initialDelay, fJob, gJob, hJob);
        Future<?> dJob = subject.submit(new TestJob(appendTo, D), initialDelay, iJob);
        Future<?> eJob = subject.submit(new TestJob(appendTo, E), initialDelay, hJob, iJob, jJob);

        subject.submit(new TestJob(appendTo, A), initialDelay, cJob, dJob);
        subject.submit(new TestJob(appendTo, B), initialDelay, eJob);

        subject.shutdown();
        assertThat(subject.awaitTermination(JOB_DELAY*4, TimeUnit.MILLISECONDS), is(true));
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
        private final Queue<String> appendTo;
        private final String appendValue;

        TestJob(Queue<String> appendTo, String appendValue) {
            this.appendTo = appendTo;
            this.appendValue = appendValue;
        }

        @Override
        public void run() {
            appendTo.add(appendValue);
        }
    }

    private static final class DelayJob implements Runnable {
        private final long delay;

        DelayJob(long delay) {
            this.delay = delay;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error during thread sleep", e);
            }
        }
    }
}