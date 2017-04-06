package org.hackerpeers.dependencyexecutorservice;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author @sberthiaume
 */
public class DependencyExecutorServiceImplTest {
    private DependencyExecutorServiceImpl subject;

    @Mock
    private ExecutorService mockExecutorService;

    @BeforeMethod
    public void beforeEachTest() throws Exception {
        initMocks(this);
        subject = new DependencyExecutorServiceImpl(mockExecutorService);
    }


    @Test
    public void shutdownCallsDelegate() {
        // Given
        // When
        subject.shutdown();

        // Then
        verify(mockExecutorService, times(1)).shutdown();
    }

    @Test
    public void shutdownNowCallsDelegateAndReturnSameValue() {
        // Given
        final List<Runnable> expected = new LinkedList<>();
        doReturn(expected).when(mockExecutorService).shutdownNow();

        // When
        List<Runnable> actual = subject.shutdownNow();

        // Then
        verify(mockExecutorService, times(1)).shutdownNow();
        assertThat(actual, is(sameInstance(expected)));
    }

    @Test
    public void isShutdownCallsDelegateAndReturnSameValue() {
        // Given
        doReturn(false).when(mockExecutorService).isShutdown();

        // When
        boolean actual = subject.isShutdown();

        // Then
        verify(mockExecutorService, times(1)).isShutdown();
        assertThat(actual, is(false));
    }

    @Test
    public void isTerminatedCallsDelegateAndReturnSameValue() {
        // Given
        doReturn(false).when(mockExecutorService).isTerminated();

        // When
        boolean actual = subject.isTerminated();

        // Then
        verify(mockExecutorService, times(1)).isTerminated();
        assertThat(actual, is(false));
    }

    @Test
    public void awaitTerminationCallsDelegateWithProperParametersAndReturnSameValue() throws InterruptedException {
        // Given
        doReturn(false).when(mockExecutorService).awaitTermination(any(Long.class), any(TimeUnit.class));

        // When
        boolean actual = subject.awaitTermination(12345, TimeUnit.MINUTES);

        // Then
        verify(mockExecutorService, times(1)).awaitTermination(12345, TimeUnit.MINUTES);
        assertThat(actual, is(false));
    }

    @Test
    public void submitCallableCallsDelegateWithProperParametersAndReturnSameValue() throws InterruptedException {
        // Given
        final Future expected = mock(Future.class);
        final Callable param = mock(Callable.class);
        doReturn(expected).when(mockExecutorService).submit(any(Callable.class));

        // When
        Future actual = subject.submit(param);

        // Then
        verify(mockExecutorService, times(1)).submit(param);
        assertThat(actual, is(sameInstance(expected)));
    }

    @Test
    public void submitRunnableWithResultCallsDelegateWithProperParametersAndReturnSameValue() throws InterruptedException {
        // Given
        final Future expected = mock(Future.class);
        final Runnable param = mock(Runnable.class);
        final Object result = new Object(){};
        doReturn(expected).when(mockExecutorService).submit(any(Runnable.class), anyObject());

        // When
        Future actual = subject.submit(param, result);

        // Then
        verify(mockExecutorService, times(1)).submit(param, result);
        assertThat(actual, is(sameInstance(expected)));
    }

    @Test
    public void submitRunnableWithoutResultCallsDelegateWithProperParametersAndReturnSameValue() throws InterruptedException {
        // Given
        final Future expected = mock(Future.class);
        final Runnable param = mock(Runnable.class);
        doReturn(expected).when(mockExecutorService).submit(any(Runnable.class));

        // When
        Future actual = subject.submit(param);

        // Then
        verify(mockExecutorService, times(1)).submit(param);
        assertThat(actual, is(sameInstance(expected)));
    }

    @Test
    public void executeCallsDelegateWithProperParameters() throws InterruptedException {
        // Given
        final Runnable param = mock(Runnable.class);

        // When
        subject.execute(param);

        // Then
        verify(mockExecutorService, times(1)).execute(param);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invokeAllCallsDelegateWithProperParametersAndReturnSameValue() throws InterruptedException {
        // Given
        final Collection<Future<Object>> expected = Collections.singletonList((Future<Object>) mock(Future.class));
        final List<Callable<Object>> param = Collections.singletonList((Callable<Object>) mock(Callable.class));
        doReturn(expected).when(mockExecutorService).invokeAll(any(Collection.class));

        // When
        Collection<Future<Object>> actual = subject.invokeAll(param);

        // Then
        verify(mockExecutorService, times(1)).invokeAll(param);
        assertThat(actual, is(sameInstance(expected)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invokeAllWithTimeoutCallsDelegateWithProperParametersAndReturnSameValue() throws InterruptedException {
        // Given
        final Collection<Future<Object>> expected = Collections.singletonList((Future<Object>) mock(Future.class));
        final List<Callable<Object>> param = Collections.singletonList((Callable<Object>) mock(Callable.class));
        doReturn(expected).when(mockExecutorService).invokeAll(any(Collection.class), any(Long.class), any(TimeUnit.class));

        // When
        Collection<Future<Object>> actual = subject.invokeAll(param, 12345, TimeUnit.MINUTES);

        // Then
        verify(mockExecutorService, times(1)).invokeAll(param,12345, TimeUnit.MINUTES);
        assertThat(actual, is(sameInstance(expected)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invokeAnyCallsDelegateWithProperParametersAndReturnSameValue() throws InterruptedException, ExecutionException {
        // Given
        final Object expected = new Object(){};
        final List<Callable<Object>> param = Collections.singletonList((Callable<Object>) mock(Callable.class));
        doReturn(expected).when(mockExecutorService).invokeAny(any(Collection.class));

        // When
        Object actual = subject.invokeAny(param);

        // Then
        verify(mockExecutorService, times(1)).invokeAny(param);
        assertThat(actual, is(sameInstance(expected)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invokeAnyWithTimeoutCallsDelegateWithProperParametersAndReturnSameValue() throws InterruptedException, TimeoutException, ExecutionException {
        // Given
        final Object expected = new Object(){};
        final List<Callable<Object>> param = Collections.singletonList((Callable<Object>) mock(Callable.class));
        doReturn(expected).when(mockExecutorService).invokeAny(any(Collection.class), any(Long.class), any(TimeUnit.class));

        // When
        Object actual = subject.invokeAny(param, 12345, TimeUnit.MINUTES);

        // Then
        verify(mockExecutorService, times(1)).invokeAny(param, 12345, TimeUnit.MINUTES);
        assertThat(actual, is(sameInstance(expected)));
    }
}