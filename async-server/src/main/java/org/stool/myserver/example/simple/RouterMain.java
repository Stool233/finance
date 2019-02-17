package org.stool.myserver.example.simple;

import org.stool.myserver.core.EntryPoint;
import org.stool.myserver.core.http.HttpMethod;
import org.stool.myserver.core.http.HttpServer;
import org.stool.myserver.route.RouteHandler;

public class RouterMain {

    public static void main(String[] args) {
        EntryPoint entryPoint = EntryPoint.entryPoint();
        HttpServer httpServer = entryPoint.createHttpServer();

        RouteHandler routeHandler = RouteHandler.create(entryPoint);

        routeHandler.route(HttpMethod.GET, "/a/*").handler(routingContext -> {
           routingContext.response().end("a");
        });

        routeHandler.route(HttpMethod.GET, "/b/*").handler(routingContext -> {
            routingContext.response().end("b");
        });

        httpServer.requestHandler(routeHandler).listen(8080);
    }
}
