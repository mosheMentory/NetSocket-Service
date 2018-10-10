package com.redice.vertx.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
public class VertxInitializer {

    @Bean
    public Vertx createVertx() throws InterruptedException {
        final VertxOptions vertxOptions = new VertxOptions().setClustered(false);
        /*.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));*/

        return Vertx.vertx(vertxOptions);
    }

}
