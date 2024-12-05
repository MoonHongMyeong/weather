package com.portfolio.weather.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Slf4j
@MapperScan({"com.portfolio.weather.api.mapper", "com.portfolio.weather.scheduler.mapper"})
public class MybatisConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        sessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml")
        );

        org.apache.ibatis.session.Configuration configuration = 
                new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setCallSettersOnNulls(true);
        configuration.setCacheEnabled(true);
        configuration.setDefaultStatementTimeout(30);
        configuration.setMultipleResultSetsEnabled(true);
        configuration.setUseGeneratedKeys(true);
        configuration.setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
        
        // 로깅 설정 추가
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        configuration.setLogPrefix("MyBatis SQL ==> ");
        
        // DatabaseIdProvider 설정
        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("H2", "h2");
        properties.setProperty("PostgreSQL", "postgresql");
        databaseIdProvider.setProperties(properties);
        
        // 현재 데이터베이스 ID 확인을 위한 로깅
        String databaseId = databaseIdProvider.getDatabaseId(dataSource);
        log.info("Current Database ID: {}", databaseId);
        
        sessionFactory.setDatabaseIdProvider(databaseIdProvider);
        
        sessionFactory.setConfiguration(configuration);
        return sessionFactory.getObject();
    }
}
