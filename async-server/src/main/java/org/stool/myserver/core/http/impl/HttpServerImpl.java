package org.stool.myserver.core.http.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stool.myserver.core.*;
import org.stool.myserver.core.http.HttpConnection;
import org.stool.myserver.core.http.HttpServer;
import org.stool.myserver.core.http.HttpServerRequest;
import org.stool.myserver.core.impl.ContextImpl;
import org.stool.myserver.core.net.impl.*;
import org.stool.myserver.core.proxy.ElasticHandler;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpServerImpl implements HttpServer {

    static final Logger log = LoggerFactory.getLogger(HttpServerImpl.class);

    private static final Handler<Throwable> DEFAULT_EXCEPTION_HANDLER = t -> log.trace("Connection failure", t);
    private final Map<Channel, BaseConnection> connectionMap = new ConcurrentHashMap<>();
    private Handler<HttpServerRequest> requestHandler;
    private Handler<HttpConnection> connectionHandler;

    private volatile boolean listening;

    private EntryPoint entryPoint;
    private ServerBootstrap bootstrap;
    private int actualPort;


    public HttpServerImpl(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Override
    public HttpServer requestHandler(Handler<HttpServerRequest> handler) {
        this.requestHandler = handler;
        return this;
    }

    @Override
    public Handler<HttpServerRequest> requestHandler() {
        return requestHandler;
    }

    @Override
    public synchronized HttpServer connectionHandler(Handler<HttpConnection> handler) {
        if (listening) {
            throw new IllegalStateException();
        }
        connectionHandler = handler;
        return this;
    }

    @Override
    public HttpServer exceptionHandler(Handler<Throwable> handler) {
        return null;
    }

    @Override
    public HttpServer listen() {
        return null;
    }

    @Override
    public HttpServer listen(int port) {

        bootstrap = new ServerBootstrap();
        bootstrap.group(entryPoint.getAcceptorEventLoopGroup(), entryPoint.getIOWorkerEventLoopGroup());

        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                handleHttp(ch);
            }
        });

        bootstrap.channelFactory(NioServerSocketChannel::new);
        InetSocketAddress t = new InetSocketAddress(port);
        final ChannelFuture bindFuture = bootstrap.bind(t);

        bindFuture.addListener(f -> {
            if (f.isSuccess()) {
                Channel serverChannel = bindFuture.channel();
                if (serverChannel.localAddress() instanceof InetSocketAddress) {
                    HttpServerImpl.this.actualPort = ((InetSocketAddress)serverChannel.localAddress()).getPort();
                } else {
                    HttpServerImpl.this.actualPort = port;
                }
            } else {
                listening = false;
                log.error(f.cause().getMessage());
            }
        });

        return this;
    }

    private void handleHttp(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("httpDecoder", new HttpRequestDecoder());
        pipeline.addLast("httpEncoder", new HttpResponseEncoder());

        Context context = entryPoint.getOrCreateContext();
        MyNettyHandler<HttpServerConnection> handler = MyNettyHandler.create(context, chctx ->
                new HttpServerConnection(entryPoint, chctx, context, new HttpHandlers(
                        requestHandler,
                        connectionHandler,
                        DEFAULT_EXCEPTION_HANDLER
                ))
        );
        handler.addHandler(conn -> {
            connectionMap.put(pipeline.channel(), conn);
        });
        handler.removeHandler(conn -> {
            connectionMap.remove(pipeline.channel());
        });
        pipeline.addLast("handler", handler);
    }

    @Override
    public HttpServer listen(String host, int port) {
        return null;
    }

    @Override
    public HttpServer listen(Handler<AsyncResult<HttpServer>> listenHandler) {
        return null;
    }

    @Override
    public HttpServer listen(int port, Handler<AsyncResult<HttpServer>> listenHandler) {
        return null;
    }

    @Override
    public HttpServer listen(String host, int port, Handler<AsyncResult<HttpServer>> listenHandler) {
        return null;
    }

    @Override
    public void close() {
        for (BaseConnection conn : connectionMap.values()) {
            conn.close();
        }
    }

    @Override
    public void close(Handler<AsyncResult<Void>> completionHandler) {

    }

    @Override
    public int actualPort() {
        return actualPort;
    }

    @Override
    public HttpServer elastic(int serverPortSize) {
        this.requestHandler(ElasticHandler.create(entryPoint, serverPortSize, this.requestHandler()));
        return this;
    }
}
