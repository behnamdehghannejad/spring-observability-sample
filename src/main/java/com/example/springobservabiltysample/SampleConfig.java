package com.example.springobservabiltysample;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.*;
import io.micrometer.observation.aop.ObservedAspect;
import net.ttddyy.observation.boot.autoconfigure.ProxyDataSourceBuilderCustomizer;
import net.ttddyy.observation.tracing.JdbcObservationDocumentation;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.reactive.ServerHttpObservationFilter;

import java.util.logging.Logger;

@Configuration
public class SampleConfig {

    /*** To have the @Observed support we need to register this aspect*/
    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    /***it registers ObservationHandler beans at the ObservationRegistry. We only need to provide the beans:*/
//    private static final Logger log = LoggerFactory.getLogger(ObservationTextPublisherConfiguration.class);
//
//    @Bean
//    public ObservationHandler<Observation.Context> observationTextPublisher() {
//        return new ObservationTextPublisher(log::info);
//    }



    /***
     * For MVC and WebFlux, there are Filters that can be used for HTTP server observations:
     *
     * org.springframework.web.filter.ServerHttpObservationFilter for Spring MVC
     * org.springframework.web.filter.reactive.ServerHttpObservationFilter for WebFlux
     *
     * When Actuator is part of our application, those filters are already registered and active  !!!important
     * */
    @ConditionalOnBean(ObservationRegistry.class) // if an ObservationRegistry is configured
    @ConditionalOnMissingBean(ServerHttpObservationFilter.class) // if we do not use Actuator
    @Bean
    public ServerHttpObservationFilter observationFilter(ObservationRegistry registry) {
        return new ServerHttpObservationFilter(registry);
    }


/***DataBase config (optional) ==========================================*/
//    @Bean
//    public ProxyDataSourceBuilderCustomizer myCustomizer(){
//        return (builder, dataSource, beanName, dataSourceName) -> {
//            builder.name("MyAppDataSource");
//            builder.asJson();
//            builder.countQuery();
//        };
//    }
//
//    @Bean
//    public ObservationFilter observationFilter() {
//        return (context) -> {
//            String tagKey = JdbcObservationDocumentation.QueryHighCardinalityKeyNames.QUERY.name();
//            KeyValue tag = context.getHighCardinalityKeyValue(tagKey);
//            if(tag != null) {
//                String query = tag.getValue();
//                String modifiedQuery = query.replace("t1_0", "tas");
//                System.out.println(modifiedQuery);
//                context.addHighCardinalityKeyValue(KeyValue.of(tagKey, modifiedQuery));
//            }
//            return context;
//        };
//    }
}
