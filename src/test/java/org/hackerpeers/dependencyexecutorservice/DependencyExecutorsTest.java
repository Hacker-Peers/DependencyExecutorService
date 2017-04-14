package org.hackerpeers.dependencyexecutorservice;

import org.mockito.Mock;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author sberthiaume
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

    @Test
    public void constructorIsPrivate() throws NoSuchMethodException {
        // Given
        Constructor<DependencyExecutors> constructor = DependencyExecutors.class.getDeclaredConstructor();

        // Then
        assertThat(Modifier.isPrivate(constructor.getModifiers()), is(true));
    }

    // Probably overkill, but also simple to "hack" to get better coverage.
    @Test
    public void testPrivateConstructor() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // Given
        Constructor<DependencyExecutors> constructor = DependencyExecutors.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // When
        DependencyExecutors actual = constructor.newInstance();

        // Then
        assertThat(actual, is(notNullValue()));
    }
}