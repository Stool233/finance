package org.stool.myserver.example.simple;

import org.stool.myserver.core.EntryPoint;
import org.stool.myserver.staticfile.StaticHandler;
import org.stool.myserver.core.http.HttpServer;
import org.stool.myserver.route.RouteHandler;

public class StaticMain {

    public static void main(String[] args) {
        String dir = "/html";

        EntryPoint entryPoint = EntryPoint.entryPoint();

        RouteHandler routeHandler = RouteHandler.create(entryPoint);
        routeHandler.route("/static/*").handler(StaticHandler.create(dir));

        entryPoint.createHttpServer()
                .requestHandler(routeHandler)
                .listen(8080);
    }
}
