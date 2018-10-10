package com.redice.vertx.consts;

public class VertxConsts {
    public static final String CONFIG_CLIENT_PIPE = "client.pipe.config";
    public static final String CONFIG_CLIENT_PIPE_SOCKET_QUEUE_MAX_SIZE = "socket.write.queue.max.size";

    public static final String CONFIG_REDIS_CLUSTER_NODES = "clusterNodes";
    public static final String CONFIG_REDIS_MAX_TOTAL_CONNECTIONS = "maxTotalConnections";
    public static final String CONFIG_REDIS = "redis";

    public static final String CONFIG_KAFKA = "kafka";
    public static final String CONFIG_KAFKA_BOOTSTRAP_SERVERS = "bootstrap.servers";
    public static final String CONFIG_KAFKA_MATCH_REQUEST_TOPIC_X = "matchingx.topic";
    public static final String CONFIG_KAFKA_MATCH_REQUEST_TOPIC_Y = "matchingy.topic";

    public static final String EVENT_BUS_MATCH_REQUEST_CHANNEL = "eventbus.channel.match.request";

    public static final String JSON_KEY_ACTION = "action";
    public static final String JSON_KEY_USER_ID = "user_id";
    public static final String JSON_KEY_TOKEN = "token";
    public static final String JSON_KEY_SOCKET_ID = "socket_id";

    public static final String LOGIN_ACTION = "login";

    public static final String PLATFORM = "platform";
}
