package org.hackerpeers.dependencyexecutorservice;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.fail;

/**
 * @author sberthiaume
 */
public class RunnableWithDependenciesTest {
    @Mock
    private Future<Object> mockFuture1, mockFuture2;
    @Mock
    private Runnable mockRunnable;
    private RunnableWithDependencies subject;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);

        subject = new RunnableWithDependencies(mockRunnable, mockFuture1, mockFuture2);
    }

    @Test
    public void waitsForDependenciesBeforeCallingDelegate() throws Exception {
        // Given
        InOrder inOrder = inOrder(mockFuture1, mockFuture2, mockRunnable);

        // When
        subject.run();

        // Then
        inOrder.verify(mockFuture1).get();
        inOrder.verify(mockFuture2).get();
        inOrder.verify(mockRunnable).run();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void stopsProcessingOnExceptionAndThrowsExceptionWithRightCause() throws Exception {
        // Given
        final Exception expected = new ExecutionException("Test ExecutionException", new BogusTestException());
        InOrder inOrder = inOrder(mockFuture1, mockFuture2, mockRunnable);
        doThrow(expected).when(mockFuture1).get();

        // When
        try {
            subject.run();
            fail("Should have thrown an Exception");
        } catch (Exception e) {
            // Then
            assertThat(e.getCause(), is(instanceOf(ExecutionException.class)));
            assertThat((ExecutionException)e.getCause(), is(sameInstance(expected)));
            inOrder.verify(mockFuture1, times(1)).get();
            inOrder.verify(mockFuture2, times(0)).get();
            inOrder.verify(mockRunnable, times(0)).run();
            inOrder.verifyNoMoreInteractions();
        }
    }
}