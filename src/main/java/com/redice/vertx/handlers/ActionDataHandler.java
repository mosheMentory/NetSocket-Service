package com.redice.vertx.handlers;

import com.redice.vertx.consts.VertxConsts;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;

public abstract class ActionDataHandler {

    protected Context context;
    protected Vertx vertx;

    public ActionDataHandler(Vertx vertx, Context context) {
        this.context = context;
        this.vertx = vertx;
    }

    public abstract void handleAction(final JsonObject json, final NetSocket ns);

    protected void ack(final JsonObject json, final NetSocket ns) {
        ns.write(json.put("timestamp", System.currentTimeMillis())
                .put(VertxConsts.JSON_KEY_ACTION, "ACK").toString() + "\n");
    }
}
