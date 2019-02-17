package org.stool.finance.user.service;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stool.finance.user.dao.UserMapper;
import org.stool.finance.user.domain.User;
import org.stool.finance.user.web.UserController;
import org.stool.myserver.core.Future;

public class UserService {

    private final static Logger LOG = LoggerFactory.getLogger(UserService.class);

    private SqlSessionFactory sqlSessionFactory;

    public UserService(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void checkUser(Future<String> future, String username, String password) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.findUserByUsername(username);

            if (user == null) {
                future.tryComplete("false");
                return ;
            }

            if (user.getPassword().equals(password)) {
                future.tryComplete("true");
            } else {
                future.tryComplete("false");
            }

        } catch (Exception e) {
            LOG.error("check user error: {}", e);
            future.tryFail(e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    public void createUser(Future<String> future, User user) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession(true);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int result = userMapper.createUser(user);
            if (result > 0) {
                future.tryComplete("success");
            } else {
                future.tryComplete("failure");
            }

        } catch (Exception e) {
            LOG.error("create user error: {}", e);
            future.tryFail(e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

}
