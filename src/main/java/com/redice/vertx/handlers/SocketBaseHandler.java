package com.redice.vertx.handlers;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

public abstract class SocketBaseHandler extends BaseHandler {

    protected NetSocket ns;

    public SocketBaseHandler(final Context context, final Vertx vertx, final NetSocket ns) {
        super(context, vertx);
        this.ns = ns;
    }

}
