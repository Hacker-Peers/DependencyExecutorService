package org.hackerpeers.dependencyexecutorservice;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
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
}