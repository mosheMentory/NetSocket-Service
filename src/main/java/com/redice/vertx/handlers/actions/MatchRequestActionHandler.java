package com.redice.vertx.handlers.actions;

import com.redice.vertx.consts.VertxConsts;
import com.redice.vertx.handlers.ActionDataHandler;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchRequestActionHandler extends ActionDataHandler {

    private static final Logger logger = LoggerFactory.getLogger(MatchRequestActionHandler.class);

    public MatchRequestActionHandler(Vertx vertx, Context context) {
        super(vertx, context);
    }

    @Override
    public void handleAction(JsonObject json, NetSocket ns) {
        json.put(VertxConsts.JSON_KEY_SOCKET_ID, ns.writeHandlerID());
        vertx.eventBus().send(VertxConsts.EVENT_BUS_MATCH_REQUEST_CHANNEL, json);
        final String seq = "match_request";
        ack(new JsonObject().put("seq", seq), ns);
    }
}
