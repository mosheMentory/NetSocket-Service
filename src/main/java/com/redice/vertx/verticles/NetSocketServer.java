package com.redice.vertx.verticles;

import com.redice.vertx.consts.VertxConsts;
import com.redice.vertx.eventbus.handlers.MatchRequestEventBusHandler;
import com.redice.vertx.handlers.NetSocketServerHandler;
import com.redice.vertx.redis.ClusteredRedisConnectionFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetSocketServer extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(NetSocketServer.class);

    private NetSocketServerHandler netSocketServerHandler;
    public static ClusteredRedisConnectionFactory redisFactory = ClusteredRedisConnectionFactory.getInstance();

    private MatchRequestEventBusHandler matchingEventBusHandler;

    @Override
    public void start() throws Exception {
        initRedisPool();
        registerEventBusHandlers();

        netSocketServerHandler = new NetSocketServerHandler(context, vertx);
        NetServer netServer = vertx.createNetServer();
        netServer.connectHandler(netSocketServerHandler);
        netServer.listen(10000);
    }

    private void registerEventBusHandlers() throws Exception {
        matchingEventBusHandler = new MatchRequestEventBusHandler(context, vertx);
        vertx.eventBus().consumer(VertxConsts.EVENT_BUS_MATCH_REQUEST_CHANNEL, matchingEventBusHandler);
    }

    private void initRedisPool() {
        logger.info("Initializing Redis connection pool");

        final JsonObject redisConfig = context.config().getJsonObject(VertxConsts.CONFIG_REDIS);
        if (redisConfig == null) {
            logger.error("Redis configuration section not found!");
            vertx.close();
        }

        if (!redisFactory.init(redisConfig)) {
            logger.error("Redis Connection Pool failed to initialized!");
            vertx.close();
        }

        vertx.sharedData().getLocalMap("system").put("redis", true);
    }
}
