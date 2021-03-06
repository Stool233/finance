package org.stool.myserver.route.impl;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.cookie.Cookie;
import org.stool.myserver.core.Context;
import org.stool.myserver.core.Handler;
import org.stool.myserver.core.http.HttpServerRequest;
import org.stool.myserver.core.http.HttpServerResponse;
import org.stool.myserver.route.Route;
import org.stool.myserver.route.RouteHandler;
import org.stool.myserver.route.RoutingContext;
import org.stool.myserver.session.Session;
import org.stool.myserver.session.SessionStore;
import org.stool.myserver.session.impl.SessionImpl;

import java.util.*;

public class RoutingContextImpl implements RoutingContext {

    private HttpServerRequest request;
    private HttpServerResponse response;
    private RouteHandler routeHandler;
    private List<Route> routes;
    private Route currentRoute;

    private Iterator<Route> iterator;

    private Map<String, Cookie> cookies;
    private List<Handler<Void>> headersEndHandlers;

    private Session session;
    private boolean matched;

    private SessionStore sessionStore;
    private boolean sessionRemoved;
    private boolean sessionAdded;


    public RoutingContextImpl(HttpServerRequest request, RouteHandler routeHandler, List<Route> routes) {
        this.request = request;
        this.response = request.response();
        this.routeHandler = routeHandler;
        this.routes = routes;
        this.iterator = routes.iterator();
    }


    @Override
    public HttpServerRequest request() {
        return request;
    }

    @Override
    public HttpServerResponse response() {
        return response;
    }

    @Override
    public void start() {
        while (iterator.hasNext()) {
            Route route = iterator.next();
            currentRoute = route;
            if (route.matches(this)) {
                route.handleContext(this);
            }
        }
        if (!matched) {
            response()
                    .setStatusCode(404)
                    .putHeader("content-type", "text/plain")
                    .end("Resource Not Found");
        }
    }

    @Override
    public Route currentRoute() {
        return currentRoute;
    }

    @Override
    public Context context() {
        return request.context();
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookiesMap().put(cookie.name(), cookie);
    }

    @Override
    public void addHeadersEndHandler(Handler<Void> handler) {
        getHeadersEndHandlers().add(handler);
    }

    @Override
    public Map<String, Cookie> cookies() {
        return cookiesMap();
    }

    @Override
    public Session session() {
        if (session == null) {
            session = new SessionImpl();
            sessionAdded = true;
        }
        return session;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void session(Session session) {
        this.session = session;
    }

    @Override
    public boolean sessionAdded() {
        return sessionAdded;
    }

    @Override
    public void matched() {
        this.matched = true;
    }

    @Override
    public void sessionStore(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Override
    public void removeSession() {
        if (session != null) {
            sessionStore.remove(session);
            sessionRemoved = true;
        }
    }

    @Override
    public boolean sessionRemoved() {
        return sessionRemoved;
    }


    private List<Handler<Void>> getHeadersEndHandlers() {
        if (headersEndHandlers == null) {
            headersEndHandlers = new ArrayList<>();
            response().headersEndHandler(v -> {
                // 倒序执行
                for (int i = headersEndHandlers.size() - 1; i >= 0; i--) {
                    headersEndHandlers.get(i).handle(null);
                }
            });
        }
        return headersEndHandlers;
    }

    private Map<String, Cookie> cookiesMap() {
        if (cookies == null) {
            cookies = new HashMap<>();
        }
        return cookies;
    }

}
