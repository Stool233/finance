package org.stool.finance.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.stool.finance.user.domain.User;

import java.util.List;

public interface UserMapper {

    @Select("select * from finance_user")
    List<User> queryAllUsers();

    @Select("select * from finance_user where username = #{username}")
    User findUserByUsername(@Param("username") String username);

    @Insert({"insert into finance_user(username, password)",
             "values(#{username}, #{password})"})
    int createUser(User user);
}
