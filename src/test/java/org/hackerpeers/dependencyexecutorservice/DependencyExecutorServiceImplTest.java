package org.hackerpeers.dependencyexecutorservice;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
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
}