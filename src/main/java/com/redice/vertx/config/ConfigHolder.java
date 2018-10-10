package com.redice.vertx.config;

import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.net.URISyntaxException;

public interface ConfigHolder {
    JsonObject getConfig();

    void init() throws URISyntaxException, IOException;
}
