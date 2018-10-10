package com.redice.vertx.redis;

import com.redice.vertx.consts.VertxConsts;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClusteredRedisConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ClusteredRedisConnectionFactory.class);

    private JedisCluster jedisCluster;

    private ClusteredRedisConnectionFactory() {
    }

    private static class Holder {
        public static ClusteredRedisConnectionFactory instance = new ClusteredRedisConnectionFactory();
    }

    public static ClusteredRedisConnectionFactory getInstance() {
        return Holder.instance;
    }

    public boolean init(final JsonObject redisConfig) {
        logger.info("Creating clustered redis connection pool.");

        final Set<String> clusterNodes = new HashSet<>(redisConfig.getJsonArray(VertxConsts.CONFIG_REDIS_CLUSTER_NODES).getList());
        if (clusterNodes.size() == 0) {
            logger.error("Cluster nodes list is missing.");
            return false;
        }

        final JedisPoolConfig jedisConfig = new JedisPoolConfig();
        jedisConfig.setMaxTotal(redisConfig.getInteger(VertxConsts.CONFIG_REDIS_MAX_TOTAL_CONNECTIONS));

        try {
            Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
            clusterNodes.forEach(hostAndPort -> {
                String[] hostAndPortArray = hostAndPort.split(":");
                String host = hostAndPortArray[0];
                int port = Integer.parseInt(hostAndPortArray[1]);
                jedisClusterNodes.add(new HostAndPort(host, port));
            });
            jedisCluster = new JedisCluster(jedisClusterNodes, jedisConfig);
        } catch (Exception e) {
            logger.error("Could not initialize Jedis Cluster", e);
        }
        return true;
    }

    public JedisCluster getResource() throws Exception {
        if (jedisCluster != null) {
            return jedisCluster;
        } else {
            throw new Exception("Jedis cluster was not initialized successfully");
        }
    }

    public void close() throws IOException {
        if (jedisCluster != null) {
            jedisCluster.close();
        }
    }

}
