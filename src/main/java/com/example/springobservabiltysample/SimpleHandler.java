package com.example.springobservabiltysample;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;

/***The data-collecting code is implemented as an ObservationHandler. This handler gets notified about the Observation‘s
 *  lifecycle events and therefore provides callback methods*/
public class SimpleHandler implements ObservationHandler<Observation.Context> {

    @Override
    public void onStart(Observation.Context context) {
        System.out.println("START " + "context.getName(): " + context.getName());
        System.out.println("START " + "context.getContextualName(): " + context.getContextualName());
        System.out.println("START " + "context.getAllKeyValues(): " + context.getAllKeyValues());
        System.out.println("START " + "context.getHighCardinalityKeyValues(): " + context.getHighCardinalityKeyValues());
        System.out.println("START " + "context.getLowCardinalityKeyValues(): " + context.getLowCardinalityKeyValues());
        System.out.println("START " + "context.getParentObservation(): " + context.getParentObservation());
    }

    @Override
    public void onError(Observation.Context context) {
        System.out.println("ERROR " + "data: " + context.get(String.class) + ", error: " + context.getError());
    }

    @Override
    public void onEvent(Observation.Event event, Observation.Context context) {
        System.out.println("EVENT " + "event: " + event + " data: " + context.get(String.class));
    }

    @Override
    public void onStop(Observation.Context context) {
        System.out.println("STOP  " + "data: " + context.get(String.class));
    }

    @Override
    public boolean supportsContext(Observation.Context handlerContext) {
        // you can decide if your handler should be invoked for this context object or
        // not
        return true;
    }

}
