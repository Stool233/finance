package org.stool.finance.account;

import org.apache.ibatis.session.SqlSessionFactory;
import org.stool.database.SqlSessionFactoryConfiguration;

import org.stool.finance.account.dao.AccountMapper;
import org.stool.finance.account.service.AccountService;
import org.stool.finance.account.web.AccountController;
import org.stool.finance.user.dao.UserMapper;
import org.stool.finance.user.service.UserService;
import org.stool.finance.user.web.UserController;
import org.stool.myserver.cookie.CookieHandler;
import org.stool.myserver.core.EntryPoint;
import org.stool.myserver.core.http.HttpClient;
import org.stool.myserver.core.http.HttpMethod;
import org.stool.myserver.core.http.HttpServer;
import org.stool.myserver.route.RouteHandler;
import org.stool.myserver.session.SessionHandler;
import org.stool.myserver.session.impl.LocalSessionStore;

public class Bootstrap {

    public static void main(String[] args) {

        EntryPoint entryPoint = EntryPoint.entryPoint();
        HttpClient httpClient = entryPoint.createHttpClient();

        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryConfiguration.sqlSessionFactory(AccountMapper.class, UserMapper.class);

        AccountService accountService = new AccountService(sqlSessionFactory);
        AccountController accountController = new AccountController(accountService, httpClient);

        UserService userService = new UserService(sqlSessionFactory);
        UserController userController = new UserController(userService);

        RouteHandler routeHandler = RouteHandler.create(entryPoint);
        routeHandler.route().handler(CookieHandler.create());
        routeHandler.route().handler(SessionHandler.create(LocalSessionStore.create()));

        routeHandler.route(HttpMethod.POST, "/user/checkLogin").handler(userController::checkLogin);
        routeHandler.route(HttpMethod.POST, "/user/login").handler(userController::login);
        routeHandler.route(HttpMethod.POST, "/user/logout").handler(userController::logout);
        routeHandler.route(HttpMethod.POST, "/user/").handler(userController::createUser);
        routeHandler.route(HttpMethod.GET, "/account/").handler(accountController::findAccountByName);
        routeHandler.route(HttpMethod.POST, "/account/").handler(accountController::createAccountByUser);
        routeHandler.route(HttpMethod.PUT, "/account/").handler(accountController::saveAccount);

        entryPoint.createHttpServer()
                .requestHandler(routeHandler)
                .listen(8081);
    }
}
