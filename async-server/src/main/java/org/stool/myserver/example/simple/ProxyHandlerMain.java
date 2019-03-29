package org.stool.myserver.example.simple;

import org.stool.myserver.core.EntryPoint;
import org.stool.myserver.core.http.HttpServer;
import org.stool.myserver.core.proxy.ProxyHandler;
import org.stool.myserver.route.RouteHandler;

public class ProxyHandlerMain {

    public static void main(String[] args) {

        EntryPoint entryPoint = EntryPoint.entryPoint();
        RouteHandler routeHandler = RouteHandler.create(entryPoint);
        routeHandler.route("/proxy/*").handler(ProxyHandler.create(entryPoint)
                .addRemoteServer("192.168.1.2", 8081)
                .addRemoteServer("192.168.1.3", 8082));

        entryPoint.createHttpServer()
                .requestHandler(routeHandler)
                .listen(8080);
    }

}
