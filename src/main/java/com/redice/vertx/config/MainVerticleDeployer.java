package com.redice.vertx.config;

import com.redice.vertx.verticles.NetSocketServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class MainVerticleDeployer {

    private static final Logger logger = LoggerFactory.getLogger(MainVerticleDeployer.class);

    @Value("${app.vertx.config}")
    private String vertxConfigFile;

    @Value("${app.vertx.instances}")
    private String instances;

    @Value("${app.vertx.enabled}")
    private boolean vertxEnabled;

    @Autowired
    private Vertx vertx;

    @Autowired
    private ConfigHolder configHolder;

    @PostConstruct
    public void init() throws URISyntaxException, IOException {
        // load vert.x config
        if (vertxEnabled) {
            final DeploymentOptions config = new DeploymentOptions()
                    .setConfig(configHolder.getConfig()).setInstances(Integer.valueOf(instances));

            vertx.deployVerticle(NetSocketServer.class.getName(), config);
            logger.info("deployed main verticle.");
        }
    }
}
