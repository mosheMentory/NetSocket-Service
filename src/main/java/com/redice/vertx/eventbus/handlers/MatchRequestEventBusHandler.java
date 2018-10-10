package com.redice.vertx.eventbus.handlers;

import com.redice.vertx.consts.VertxConsts;
import com.redice.vertx.handlers.BaseHandler;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class MatchRequestEventBusHandler extends BaseHandler implements Handler<Message<JsonObject>> {

    private static final Logger logger = LoggerFactory.getLogger(MatchRequestEventBusHandler.class);

    private Producer<String, String> producer;
    private String matchRequestTopicX;
    private String matchRequestTopicY;

    public MatchRequestEventBusHandler(Context context, Vertx vertx) throws Exception {
        super(context, vertx);
        initProducer();
    }

    @Override
    public void stop() {
        if (producer != null) {
            producer.close();
        }
    }

    @Override
    public void handle(Message<JsonObject> msg) {
        producer.send(new ProducerRecord<>(matchRequestTopicX, msg.body().toString()));
        producer.send(new ProducerRecord<>(matchRequestTopicY, msg.body().toString()));
    }

    private void initProducer() throws Exception {
        final JsonObject kafkaConfig = context.config().getJsonObject(VertxConsts.CONFIG_KAFKA);
        if (kafkaConfig == null) {
            throw new Exception("Can't find kafka section in config");
        }
        matchRequestTopicX = kafkaConfig.getString(VertxConsts.CONFIG_KAFKA_MATCH_REQUEST_TOPIC_X);
        matchRequestTopicY = kafkaConfig.getString(VertxConsts.CONFIG_KAFKA_MATCH_REQUEST_TOPIC_Y);
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaConfig.getString(VertxConsts.CONFIG_KAFKA_BOOTSTRAP_SERVERS));
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
        logger.info("Producer configured");
    }
}
