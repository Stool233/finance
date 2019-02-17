package org.stool.finance.account.service;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.stool.database.SqlSessionFactoryConfiguration;
import org.stool.finance.account.dao.AccountMapper;
import org.stool.finance.account.domain.Account;
import org.stool.finance.account.web.AccountController;
import org.stool.finance.user.dao.UserMapper;
import org.stool.finance.user.service.UserService;
import org.stool.finance.user.web.UserController;
import org.stool.myserver.cookie.CookieHandler;
import org.stool.myserver.core.EntryPoint;
import org.stool.myserver.core.Future;
import org.stool.myserver.core.http.HttpClient;
import org.stool.myserver.core.http.HttpMethod;
import org.stool.myserver.core.http.HttpServer;
import org.stool.myserver.route.RouteHandler;
import org.stool.myserver.session.SessionHandler;
import org.stool.myserver.session.impl.LocalSessionStore;

import java.util.Date;

public class AccountServiceTest {

    @Test
    public void test() {


        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryConfiguration.sqlSessionFactory(AccountMapper.class, UserMapper.class);

        AccountService accountService = new AccountService(sqlSessionFactory);

        Account account = new Account();
        account.setId(1);
        account.setLastSeen(new Date());
        account.setName("aa");
        account.setNote("note");

        accountService.saveAccount(Future.future(), account);
    }
}
