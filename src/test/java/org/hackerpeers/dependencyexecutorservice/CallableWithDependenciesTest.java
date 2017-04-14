package org.hackerpeers.dependencyexecutorservice;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.fail;

/**
 * @author sberthiaume
 */
public class CallableWithDependenciesTest {
    @Mock
    private Future<Object> mockFuture1, mockFuture2;
    @Mock
    private Callable<Object> mockCallable;
    private CallableWithDependencies subject;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        initMocks(this);

        subject = new CallableWithDependencies(mockCallable, mockFuture1, mockFuture2);
    }

    @Test
    public void waitsForDependenciesBeforeCallingDelegateAndReturnProperValue() throws Exception {
        // Given
        final Object expected = new Object(){};
        InOrder inOrder = inOrder(mockFuture1, mockFuture2, mockCallable);
        doReturn(expected).when(mockCallable).call();

        // When
        Object actual = subject.call();

        // Then
        assertThat(actual, is(sameInstance(expected)));
        inOrder.verify(mockFuture1, times(1)).get();
        inOrder.verify(mockFuture2, times(1)).get();
        inOrder.verify(mockCallable, times(1)).call();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void stopsProcessingOnExceptionAndThrowProperException() throws Exception {
        // Given
        final Exception expected = new ExecutionException("Test ExecutionException", new BogusTestException());
        InOrder inOrder = inOrder(mockFuture1, mockFuture2, mockCallable);
        doThrow(expected).when(mockFuture1).get();

        // When
        try {
            subject.call();
            fail("Should have thrown an Exception");
        } catch (Exception e) {
            // Then
            assertThat(e, is(sameInstance(expected)));
            inOrder.verify(mockFuture1, times(1)).get();
            inOrder.verify(mockFuture2, times(0)).get();
            inOrder.verify(mockCallable, times(0)).call();
            inOrder.verifyNoMoreInteractions();
        }
    }
}