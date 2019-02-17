package org.stool.database;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.stool.myserver.example.database.BlogMapper;

import javax.sql.DataSource;

public class SqlSessionFactoryConfiguration {

    public static SqlSessionFactory sqlSessionFactory(Class<?> ... classes) {
        DataSource dataSource = new PooledDataSource("com.mysql.jdbc.Driver",
                "jdbc:mysql://rm-wz9twin0w997453c0no.mysql.rds.aliyuncs.com:3306/finance",
                "root",
                "shishen1=1=2");
        Environment environment = new Environment("development", new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(environment);
        for (Class<?> clazz : classes) {
            configuration.addMapper(clazz);
        }
        return new SqlSessionFactoryBuilder().build(configuration);
    }

}
