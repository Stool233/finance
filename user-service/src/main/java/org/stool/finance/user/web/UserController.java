package org.stool.finance.user.web;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stool.finance.user.dao.UserMapper;
import org.stool.finance.user.domain.User;
import org.stool.finance.user.service.UserService;
import org.stool.myserver.route.RoutingContext;
import org.stool.myserver.session.impl.SessionImpl;

public class UserController {

    private final static Logger LOG = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void checkUser(RoutingContext routingContext) {
        String username = routingContext.request().getParam("username");
        String password = routingContext.request().getParam("password");

        routingContext.context().<String>executeBlocking(future -> {
            userService.checkUser(future, username, password);
        }, asyncResult -> {
            if (asyncResult.succeeded()) {
                routingContext.response().end(asyncResult.result());
            } else {
                routingContext.response().end(asyncResult.cause().getMessage());
            }
        });
    }

    public void checkLogin(RoutingContext routingContext) {
        String username = routingContext.request().getParam("username");
        if (routingContext.session().get("username") != null && routingContext.session().get("username").equals(username)) {
            routingContext.response().end("true");
        } else {
            routingContext.response().end("false");
        }
    }

    public void login(RoutingContext routingContext) {
        String username = routingContext.request().getParam("username");
        String password = routingContext.request().getParam("password");

        routingContext.context().<String>executeBlocking(future -> {
            userService.checkUser(future, username, password);
        }, asyncResult -> {
            if (asyncResult.succeeded()) {
                routingContext.session().put("username", username);
                routingContext.response().end("success");
            } else {
                routingContext.response().end(asyncResult.cause().getMessage());
            }
        });
    }

    public void logout(RoutingContext routingContext) {
        routingContext.session().remove("username");
        routingContext.response().end("success");
    }

    public void createUser(RoutingContext routingContext) {
        String username = routingContext.request().getParam("username");
        String password = routingContext.request().getParam("password");
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        routingContext.context().<String>executeBlocking(future -> {
            userService.createUser(future, user);
        }, asyncResult -> {
            if (asyncResult.succeeded()) {
                routingContext.response().end(asyncResult.result());
            } else {
                routingContext.response().end(asyncResult.cause().getMessage());
            }
        });
    }

}
