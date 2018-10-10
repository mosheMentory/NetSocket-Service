package com.redice.vertx.config;

import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ConfigHolderImpl implements ConfigHolder {

    @Value("${app.vertx.config}")
    private String vertxConfigFile;

    @Value("${app.vertx.enabled}")
    private boolean vertxEnabled;

    @Value("${app.vertx.config.default}")
    private boolean defaultConfig;

    protected JsonObject config;

    @Override
    public JsonObject getConfig() {
        return config;
    }

    @Override
    @PostConstruct
    public void init() throws URISyntaxException, IOException {
        if (vertxEnabled) {
            if (defaultConfig) {
                final URI configUri = this.getClass().getClassLoader().getResource(vertxConfigFile).toURI();
                config = new JsonObject(new String(Files.readAllBytes(Paths.get(configUri))));
            } else {
                InputStream is = new FileInputStream(vertxConfigFile);
                StringWriter writer = new StringWriter();
                IOUtils.copy(is, writer, "UTF-8");
                config = new JsonObject(writer.toString());
            }
        }
    }
}
