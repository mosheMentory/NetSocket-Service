package com.redice.vertx.handlers;

import com.redice.vertx.consts.VertxConsts;
import com.redice.vertx.domain.UserConnectionDetails;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

public class SocketClientHandler extends SocketBaseHandler implements Handler<Message<JsonObject>> {

    private static final Logger logger = LoggerFactory.getLogger(SocketClientHandler.class);

    private String userId;

    public SocketClientHandler(Context context, Vertx vertx, NetSocket ns) {
        super(context, vertx, ns);
    }

    @Override
    public void handle(Message<JsonObject> jsonObjectMessage) {

    }

    @Override
    public void stop() {

    }

    public void handleDataEvent(Buffer event, Map<String, ActionDataHandler> actionsMap) {
        final JsonObject json = new JsonObject(event.toString());
        final String action = json.getString(VertxConsts.JSON_KEY_ACTION);

        if (action.equals(VertxConsts.LOGIN_ACTION)) {
            final String userId = json.getString(VertxConsts.JSON_KEY_USER_ID);
            final String token = json.getString(VertxConsts.JSON_KEY_TOKEN);
            this.userId = userId;

            /*validateUser(ns, userId, token, deviceId);

            vertx.eventBus().send(
                    VertxConsts.EVENTBUS_REDIS_CHANNEL,
                    new JsonObject().put("action", RedisEventBusHandler.ACTION_USER_ACCEPT).put(VertxConsts.JSON_KEY_USER, userId)
                            .put(VertxConsts.JSON_KEY_PLATFORM, platform).put(VertxConsts.JSON_KEY_DEVICE_ID, deviceId)
                            .put(VertxConsts.JSON_KEY_TIME_ZONE, timeZone)
                            .put(VertxConsts.JSON_KEY_SOCIAL_ID, socialId));*/

            UserConnectionDetails userConnectionDetails = new UserConnectionDetails(ns.writeHandlerID());
            vertx.sharedData().getLocalMap(VertxConsts.PLATFORM).put(userId, userConnectionDetails);

            final JsonObject res = new JsonObject().put(VertxConsts.JSON_KEY_ACTION, "CONNECTED_TO_PLATFORM");
            ns.write(res.toString() + "\n");
            return;
        }

        if (userId != null) {
            json.put(VertxConsts.JSON_KEY_USER_ID, userId);
            if (!vertx.sharedData().getLocalMap(VertxConsts.PLATFORM).containsKey(userId)) {
                logger.warn(String.format("message recieved from undefined user: %s", userId));
                ns.close();
                /*vertx.eventBus().send(VertxConsts.EVENTBUS_REDIS_CHANNEL,
                        json.put(VertxConsts.JSON_KEY_ACTION, RedisEventBusHandler.ACTION_USER_DISPOSE));*/
                return;
            }

            if (actionsMap.containsKey(action.toLowerCase())) {
                actionsMap.get(action.toLowerCase()).handleAction(json, ns);
            } else {
                logger.warn("No action found for json: " + json);
            }
        } else {
            logger.warn(String.format("User:%s tried to do actions and is not logged-in"));
        }
    }

    public void handleCloseEvent() {

    }

    public void handleExceptionEvent(Throwable event) {
        logger.error("Exception in socket: " + event.getMessage());
        logger.error(Arrays.toString(event.getStackTrace()));
    }
}
