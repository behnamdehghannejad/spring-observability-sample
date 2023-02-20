package com.example.springobservabiltysample;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    private final SampleService service;
    private final ObservationRegistry registry; // sample #1 just need this
    private final MeterRegistry meterRegistry;  // sample #4

    public SampleController(SampleService service, MeterRegistry meterRegistry) {
        this.service = service;

//        this.registry = registry; // sample #1

//        this.registry = ObservationRegistry.create();    // sample #2 (can add your Option hear)

        this.registry = ObservationRegistry.create();    // sample #3
        registry.observationConfig().observationHandler(new SimpleHandler()); // for connecting to SimpleHandler

        this.meterRegistry = meterRegistry; // sample #4
    }

    @PostMapping("/save-user")
    public void saveUser(@RequestBody Users users) {
        service.saveUser(users);
    }

    /**   .contextualName("sample-service") // this is a tag
     * .lowCardinalityKeyValue("lowTag", "lowTagValue") // this is a tag
     * .highCardinalityKeyValue("highTag", "highTagValue") // this is a tag
     * can add any value in tags
     */
    /*** the simplest way to observe rest-endpoint*/
    @PostMapping("/save-user-sample1") // sample #1
    public void saveUserSample1(@RequestBody Users users) {
        Observation.createNotStarted("sample1", registry) // span name
                .contextualName("sample-service") // this is a tag
                .lowCardinalityKeyValue("lowTag", "lowTagValue") // this is a tag
                .highCardinalityKeyValue("highTag", "highTagValue") // this is a tag
                .observe(
                        () -> service.saveUser(users) // the methode to observe
                        //    service::saveUser   // can use like this if method has not any input
                );
    }

    /*** If you want to have more observation options (such as metrics and tracing out of the box plus
     * anything else you will plug in) then you’ll need to rewrite that code to use the Observation API.*/
    @PostMapping("/save-user-sample2") // sample #2
    public void saveUserSample2(@RequestBody Users users) {
        Observation.createNotStarted("sample2", registry)
                .contextualName("sample-service")
                .observe(
                        () -> service.saveUser(users)
                );
    }

    /*** Based on this, we can implement a simple handler that lets the users know about its invocations
     * by printing them out to stdout.
     * You can use the observe method to instrument your codebase.*/
    @PostMapping("/save-user-sample3-part1") // sample #3 part1
    public void saveUserSample3Part1(@RequestBody Users users) {
        Observation.Context context = new Observation.Context().put(String.class, "test");
        Observation.createNotStarted("sample3Part1", () -> context, registry)
                .observe(
                        () -> service.saveUser(users)
                );
    }

    /***You can also take full control of the scoping mechanism.*/
    @PostMapping("/save-user-sample3-part2") // sample #3 part2
    public void saveUserSample3Part2(@RequestBody Users users) {
        Observation.Context context = new Observation.Context().put(String.class, "test");
        // using a context is optional, so you can call start without it:
        // Observation.start(name, registry)
        Observation observation = Observation.start("Sample3part2", () -> context, registry);
        try (Observation.Scope scope = observation.openScope()) {
            service.saveUser(users);
        }
        catch (Exception ex) {
            observation.error(ex); // and don't forget to handle exceptions
            throw ex;
        }
        finally {
            observation.stop();
        }
    }

    /***A popular way to record them is storing the start state in a Timer.
     * Sample instance and stopping it when the event has ended.
     * Recording such measurements could look like this:*/
    @PostMapping("/save-user-sample4") // sample #4
    public void saveUserSample4(@RequestBody Users users) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            service.saveUser(users);
        } finally {
            sample.stop(Timer.builder("Sample4").register(meterRegistry));
        }
    }
}
