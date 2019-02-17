package org.stool.finance.user;

import org.apache.ibatis.session.SqlSessionFactory;
import org.stool.database.SqlSessionFactoryConfiguration;
import org.stool.finance.user.dao.UserMapper;
import org.stool.finance.user.service.UserService;
import org.stool.finance.user.web.UserController;
import org.stool.myserver.cookie.CookieHandler;
import org.stool.myserver.core.EntryPoint;
import org.stool.myserver.core.http.HttpMethod;
import org.stool.myserver.core.http.HttpServer;
import org.stool.myserver.route.RouteHandler;
import org.stool.myserver.session.SessionHandler;
import org.stool.myserver.session.SessionStore;
import org.stool.myserver.session.impl.LocalSessionStore;

public class Bootstrap {

    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryConfiguration.sqlSessionFactory(UserMapper.class);
        UserService userService = new UserService(sqlSessionFactory);
        UserController userController = new UserController(userService);


        EntryPoint entryPoint = EntryPoint.entryPoint();
        HttpServer httpServer = entryPoint.createHttpServer();

        RouteHandler routeHandler = RouteHandler.create(entryPoint);
        routeHandler.route().handler(CookieHandler.create());
        routeHandler.route().handler(SessionHandler.create(LocalSessionStore.create()));

        routeHandler.route(HttpMethod.POST, "/user/checkLogin").handler(userController::checkLogin);
        routeHandler.route(HttpMethod.POST, "/user/login").handler(userController::login);
        routeHandler.route(HttpMethod.POST, "/user/logout").handler(userController::logout);
        routeHandler.route(HttpMethod.POST, "/user/").handler(userController::createUser);

        httpServer.requestHandler(routeHandler).listen(8081);
    }
}
