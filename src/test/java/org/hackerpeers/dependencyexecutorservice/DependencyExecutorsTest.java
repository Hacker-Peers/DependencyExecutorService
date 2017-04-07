package org.hackerpeers.dependencyexecutorservice;

import org.mockito.Mock;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author @sberthiaume
 */
public class DependencyExecutorsTest {
    @Mock
    private ExecutorService mockExecutorService;

    @BeforeTest
    private void setup() {
        initMocks(this);
    }

    @Test
    public void createRightClassWithRightDelegate() throws Exception {
        // Given
        // When
        DependencyExecutorService actual = DependencyExecutors.newDependencyExecutorWithDelegate(mockExecutorService);

        // Then
        assertThat(actual, is(instanceOf(DependencyExecutorServiceImpl.class)));
        assertThat(((DependencyExecutorServiceImpl) actual).getDelegate(), is(sameInstance(mockExecutorService)));
    }
}