package org.stool.finance.account.service;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stool.finance.account.dao.AccountMapper;
import org.stool.finance.account.domain.Account;
import org.stool.finance.account.domain.Item;
import org.stool.finance.account.domain.Saving;
import org.stool.finance.user.dao.UserMapper;
import org.stool.finance.user.domain.User;
import org.stool.myserver.core.Future;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AccountService {

    private final static Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private SqlSessionFactory sqlSessionFactory;

    public AccountService(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void findAccountByName(Future<Account> future, String accountName) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);

            Account account = accountMapper.findAccountByName(accountName);

            if (account == null) {
                future.tryFail("failure");
                return ;
            }

            Saving saving = accountMapper.findSavingByAccountId(account.getId());
            List<Item> incomes = accountMapper.findIncomesByAccountId(account.getId());
            List<Item> expenses = accountMapper.findExpensesByAccountId(account.getId());
            account.setSaving(saving);
            account.setIncomes(incomes);
            account.setExpenses(expenses);
            future.tryComplete(account);

        } catch (Exception e) {
            LOG.error("findAccountByName error: {}", e);
            future.tryFail(e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    public void createAccountByUser(Future<String> future, User user) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

            if (userMapper.findUserByUsername(user.getUsername()) == null) {
                userMapper.createUser(user);
            }

            Account account = new Account();
            account.setName(user.getUsername());
            account.setLastSeen(new Date());

            accountMapper.createAccount(account);

            long accountId = accountMapper.findAccountByName(user.getUsername()).getId();

            Saving saving = new Saving();
            saving.setAmount(new BigDecimal(0));
            saving.setInterest(new BigDecimal(0));
            saving.setDeposit(false);
            saving.setCapitalization(false);
            saving.setAccountId(accountId);

            accountMapper.createSaving(saving);

            sqlSession.commit();

            future.tryComplete("success");

        } catch (Exception e) {
            LOG.error("createAccountByUser error: {}", e);
            future.tryFail(e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    public void saveAccount(Future<String> future, Account account) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            AccountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);

            Account oldAccount = accountMapper.findAccountByName(account.getName());
            account.setId(oldAccount.getId());
            accountMapper.updateAccount(account);

            Saving saving = account.getSaving();
            if (saving != null) {
                saving.setAccountId(oldAccount.getId());
                accountMapper.updateSaving(saving);
            }

            List<Item> incomes = account.getIncomes();
            if (incomes != null) {
                for (Item item : incomes) {
                    item.setType(0);
                    item.setAccountId(oldAccount.getId());
                    if (accountMapper.findItem(item) == null) {
                        accountMapper.createItem(item);
                    } else {
                        accountMapper.updateItem(item);
                    }

                }
            }

            List<Item> expenses = account.getExpenses();
            if (expenses != null) {
                for (Item item : expenses) {
                    item.setType(1);
                    item.setAccountId(oldAccount.getId());
                    if (accountMapper.findItem(item) == null) {
                        accountMapper.createItem(item);
                    } else {
                        accountMapper.updateItem(item);
                    }
                }
            }

            sqlSession.commit();

            future.tryComplete("success");

        } catch (Exception e) {
            LOG.error("saveAccount error: {}", e);
            future.tryFail(e);
            if (sqlSession != null) {
                sqlSession.rollback();
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}
