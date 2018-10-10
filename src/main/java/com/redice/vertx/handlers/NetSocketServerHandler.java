package com.redice.vertx.handlers;

import com.redice.vertx.consts.VertxConsts;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.core.shareddata.LocalMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class NetSocketServerHandler extends BaseHandler implements Handler<NetSocket> {

    public static final String ACTIONS = "actions";

    private static final Logger logger = LoggerFactory.getLogger(NetSocketServerHandler.class);
    Map<String, ActionDataHandler> actionsMap = new HashMap<>();

    public NetSocketServerHandler(Context context, Vertx vertx) {
        super(context, vertx);
        try {
            loadActionHandlers();
        } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void handle(NetSocket ns) {
        int socketWriteQueueMaxSize = 50000;
        final JsonObject clientPipeConfig = context.config().getJsonObject(VertxConsts.CONFIG_CLIENT_PIPE);
        if (clientPipeConfig == null) {
            logger.error("Client pipe config configuration section not found!");
            vertx.close();
        } else {
            socketWriteQueueMaxSize = clientPipeConfig.getInteger(VertxConsts.CONFIG_CLIENT_PIPE_SOCKET_QUEUE_MAX_SIZE);
        }
        ns.setWriteQueueMaxSize(socketWriteQueueMaxSize);

        final LocalMap<String, Object> systemState = vertx.sharedData().getLocalMap("system");
        if (systemState != null && systemState.size() > 0) {

            final Boolean redis = (boolean) vertx.sharedData().getLocalMap("system").get("redis");
            if (redis == null || !redis) {
                ns.write(new JsonObject().put("status", "JOIN_FAILED").put("msg", "internal server error: redis is down").toString() + "\n");
                ns.close();
                return;
            }
        }

        try {
            registerSocket(ns);
        } catch (Exception e) {
            logger.warn("Could not register socket for user: " + " cause: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void registerSocket(NetSocket ns) {
        final SocketClientHandler handler = new SocketClientHandler(context, vertx, ns);

        ns.handler(RecordParser.newDelimited("\n", new Handler<Buffer>() {

            @Override
            public void handle(Buffer event) {
                handler.handleDataEvent(event, actionsMap);
            }
        }));

        ns.closeHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                handler.handleCloseEvent();
            }
        });

        ns.exceptionHandler(new Handler<Throwable>() {

            @Override
            public void handle(Throwable event) {
                handler.handleExceptionEvent(event);
            }
        });
    }

    private void loadActionHandlers() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final JsonObject config = context.config();
        final JsonObject actions = config.getJsonObject(ACTIONS);
        if (actions != null && actions.size() > 0) {
            for (final String action : actions.fieldNames()) {
                final String handlerClassName = actions.getString(action);
                final Class<ActionDataHandler> handlerClass = (Class<ActionDataHandler>) Class.forName(handlerClassName);
                final Constructor<ActionDataHandler> ctor = handlerClass.getDeclaredConstructor(Vertx.class, Context.class);

                actionsMap.put(action.toLowerCase(), ctor.newInstance(vertx, context));
            }
        }
    }
}
