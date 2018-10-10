package com.redice.vertx.domain;

import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.shareddata.Shareable;

public class UserConnectionDetails implements Shareable {

    private String socketId;
    private MessageConsumer messageConsumer;

    public UserConnectionDetails(String socketId) {
        this.socketId = socketId;
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }

    public void setMessageConsumer(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }
}
