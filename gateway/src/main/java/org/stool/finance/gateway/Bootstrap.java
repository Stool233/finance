package org.stool.finance.gateway;

import org.stool.myserver.cookie.CookieHandler;
import org.stool.myserver.core.EntryPoint;
import org.stool.myserver.core.http.HttpMethod;
import org.stool.myserver.core.http.HttpServer;
import org.stool.myserver.core.proxy.ProxyHandler;
import org.stool.myserver.route.RouteHandler;
import org.stool.myserver.staticfile.StaticHandler;

import java.io.File;
import java.net.URL;

public class Bootstrap {

    public static void main(String[] args) {

        ClassLoader classLoader = Bootstrap.class.getClassLoader();
        File file = new File(classLoader.getResource("static").getFile());
        String dir = file.getAbsolutePath() + "/";

        EntryPoint entryPoint = EntryPoint.entryPoint();
        HttpServer httpServer = entryPoint.createHttpServer();

        RouteHandler routeHandler = RouteHandler.create(entryPoint);

        routeHandler.route(HttpMethod.GET, "/static/*").handler(StaticHandler.create(dir));

        ProxyHandler accountProxyHandler = ProxyHandler.create(entryPoint).addRemoteServer("127.0.0.1", 8081);
        routeHandler.route("/user/*").handler(accountProxyHandler);
        routeHandler.route("/account/*").handler(accountProxyHandler);

        httpServer.requestHandler(routeHandler).listen(8080);
    }

}
