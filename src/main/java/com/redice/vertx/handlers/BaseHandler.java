package com.redice.vertx.handlers;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

public abstract class BaseHandler {

    protected static final String ACTION_TICK = "TICK";
    protected Context context;
    protected Vertx vertx;

    public BaseHandler(final Context context, final Vertx vertx) {
        this.context = context;
        this.vertx = vertx;
    }

    public abstract void stop();

    protected long startTickTimer(final long period, Handler<Message<JsonObject>> handler) {
        // create unique channel for this handler
        final String thisChannel = UUID.randomUUID().toString();
        vertx.eventBus().consumer(thisChannel, handler);

        final long timerId = vertx.setPeriodic(period, new Handler<Long>() {

            @Override
            public void handle(Long event) {
                vertx.eventBus().send(thisChannel, new JsonObject().put("action", ACTION_TICK));
            }
        });

        return timerId;
    }

}
