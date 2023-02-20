package com.example.springobservabiltysample;

import io.micrometer.observation.ObservationTextPublisher;
import io.micrometer.observation.aop.ObservedAspect;
import io.micrometer.observation.tck.TestObservationRegistry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static io.micrometer.observation.tck.TestObservationRegistryAssert.assertThat;

@SpringBootTest
@Disabled
class SpringObservabilitySampleApplicationTests {

    @Autowired
    SampleService sampleService;
    @Autowired
    TestObservationRegistry observationRegistry; // type #1

    @TestConfiguration
    static class ObservationTestConfiguration { // type #1

        @Bean
        TestObservationRegistry observationRegistry() {
            return TestObservationRegistry.create();
        }
    }

    @Test
    void test1() { // type #1
        assertThat(observationRegistry)
                .hasObservationWithNameEqualTo("save-user")
                .that()
                .hasBeenStarted()
                .hasBeenStopped();
    }

    @Test
    void test2() { // type #2
        // create a test registry
        TestObservationRegistry registry = TestObservationRegistry.create();
        // add a system out printing handler
        registry.observationConfig().observationHandler(new ObservationTextPublisher());

        // create a proxy around the observed service
        AspectJProxyFactory pf = new AspectJProxyFactory(sampleService);
        pf.addAspect(new ObservedAspect(registry));

        // make a call
        SampleService service = pf.getProxy();
        service.saveUser(new Users());

        // assert that observation has been properly created
        assertThat(registry)
                .hasSingleObservationThat()
                .hasBeenStopped()
                .hasNameEqualTo("save-user")
                .hasContextualNameEqualTo("save#user")
                .hasLowCardinalityKeyValue("abc", "123")
                .hasLowCardinalityKeyValue("test", "42")
                .hasLowCardinalityKeyValue("class", SampleService.class.getName())
                .hasLowCardinalityKeyValue("method", "call").doesNotHaveError();
    }

}
