package org.stool.myserver.staticfile;

import org.stool.myserver.core.Handler;
import org.stool.myserver.route.RoutingContext;

import java.io.FileNotFoundException;

public class StaticHandler implements Handler<RoutingContext>{

    private final String dir;

    public static StaticHandler create(String dir) {
        return new StaticHandler(dir);
    }

    public StaticHandler(String dir) {
        this.dir = dir;
    }

    @Override
    public void handle(RoutingContext routingContext) {

        String fileName;
        if (routingContext.currentRoute().isExactPath()) {
            int index = routingContext.request().path().lastIndexOf("/");
            fileName = routingContext.request().path().substring(index, routingContext.request().path().length());
        } else {
            int index = routingContext.currentRoute().path().length();
            fileName = routingContext.request().path().substring(index, routingContext.request().path().length());
        }

        String localPath;
        if (dir != null) {
            localPath = dir + "/" + fileName;
        } else {
            localPath = fileName;
        }

        routingContext.response().sendFile(localPath, ar -> {
            if (ar.failed() && ar.cause() instanceof FileNotFoundException) {
                routingContext.response().setStatusCode(404).end("file not found");
            }
        });

    }
}
