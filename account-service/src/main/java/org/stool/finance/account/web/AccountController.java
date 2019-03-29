package org.stool.finance.account.web;

import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.stool.finance.account.domain.Account;
import org.stool.finance.account.service.AccountService;
import org.stool.finance.user.domain.User;
import org.stool.myserver.core.Future;
import org.stool.myserver.core.http.HttpClient;
import org.stool.myserver.core.net.Buffer;
import org.stool.myserver.route.RoutingContext;

import java.nio.charset.Charset;
import java.util.Date;


public class AccountController {

    private AccountService accountService;
    private HttpClient httpClient;

    public AccountController(AccountService accountService, HttpClient httpClient) {
        this.accountService = accountService;
        this.httpClient = httpClient;
    }

    public void findAccountByName(RoutingContext routingContext) {

        if (routingContext.getSession() == null) {
            routingContext.response().setStatusCode(404).end();
            return;
        }

        String username = routingContext.session().get("username");
        if (routingContext.session().get("username") == null) {
            routingContext.response().setStatusCode(404).end();
            return;
        }

        routingContext.context().<Account>executeBlocking(future -> {
            accountService.findAccountByName(future, username);
        }, asyncResult -> {
            if (asyncResult.succeeded()) {
                routingContext.response().headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
                routingContext.response().end(JSON.toJSONString(asyncResult.result()));
            } else {
                routingContext.response().setStatusCode(404).end("failure");
            }
        });
    }

    public void createAccountByUser(RoutingContext routingContext) {
        String username = routingContext.request().getParam("username");
        String password = routingContext.request().getParam("password");
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        routingContext.context().<String>executeBlocking(future -> {
            accountService.createAccountByUser(future, user);
        }, asyncResult -> {
            if (asyncResult.succeeded()) {
                routingContext.response().end(asyncResult.result());
            } else {
                routingContext.response().end(asyncResult.cause().getMessage());
            }
        });

    }

    public void saveAccount(RoutingContext routingContext) {
        if (!HttpHeaderValues.APPLICATION_JSON
                .contentEqualsIgnoreCase(routingContext.request().headers().get(HttpHeaderNames.CONTENT_TYPE))) {
            routingContext.response().setStatusCode(400);
            routingContext.response().end("Bad Request");
            return;
        }

        routingContext.request().bodyHandler(totalBuffer -> {

            convertAccount(totalBuffer, routingContext)
                .next(account -> saveAccount(account, routingContext))
                .setHandler(ar -> {
                    if (ar.succeeded()) {
                        routingContext.response().end(ar.result());
                    } else {
                        routingContext.response().setStatusCode(400).end();
                    }
                });

        });

    }

    private Future<Account> convertAccount(Buffer totalBuffer, RoutingContext routingContext) {
        Future<Account> future = Future.future();
        routingContext.context().executeAsync(v -> {
            String json = totalBuffer.getByteBuf().toString(Charset.forName("utf-8"));
            Account account = JSON.parseObject(json, Account.class);

            String username = routingContext.session().get("username");
            account.setName(username);
            account.setLastSeen(new Date());

            future.complete(account);
        });
        return future;
    }

    private Future<String> saveAccount(Account account, RoutingContext routingContext) {
        Future<String> finalFuture = Future.future();
        routingContext.context().<String>executeBlocking(future -> {
            accountService.saveAccount(future, account);
        }, asyncResult -> {
            if (asyncResult.succeeded()) {
                finalFuture.complete(asyncResult.result());
            } else {
                finalFuture.fail(asyncResult.cause());
            }
        });
        return finalFuture;
    }
}
