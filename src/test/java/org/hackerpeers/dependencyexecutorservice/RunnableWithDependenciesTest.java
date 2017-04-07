package org.hackerpeers.dependencyexecutorservice;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.Future;

import static org.mockito.Mockito.inOrder;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author @sberthiaume
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
    public void waitsForDependenciesBeforeCallingDelegateAndReturnProperValue() throws Exception {
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
}