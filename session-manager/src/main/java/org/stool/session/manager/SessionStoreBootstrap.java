package org.stool.session.manager;

import org.stool.myserver.core.EntryPoint;
import org.stool.myserver.core.http.HttpMethod;
import org.stool.myserver.core.http.HttpServer;
import org.stool.myserver.route.RouteHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionStoreBootstrap {

    private static ConcurrentMap<String, String> store = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        EntryPoint entryPoint = EntryPoint.entryPoint();
        HttpServer httpServer = entryPoint.createHttpServer();

        RouteHandler routeHandler = RouteHandler.create(entryPoint);
        routeHandler.route(HttpMethod.GET, "/sessionManager/").handler(routingContext -> {
            String sessionId = routingContext.request().getParam("sessionId");
            String response = store.get(sessionId);
            routingContext.response().end(response);
        });
        routeHandler.route(HttpMethod.POST, "/sessionManager/").handler(routingContext -> {
            String sessionId = routingContext.request().getParam("sessionId");
            String session = routingContext.request().getParam("session");
            store.put(sessionId, session);
            routingContext.response().end(sessionId);
        });

        httpServer.requestHandler(routeHandler).listen(9000);
    }

}
