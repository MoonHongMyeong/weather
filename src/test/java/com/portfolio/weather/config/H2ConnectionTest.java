package com.portfolio.weather.config;

import com.portfolio.weather.api.mapper.TestMapper;
import com.portfolio.weather.common.config.MybatisConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@JdbcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional(propagation = Propagation.NEVER)
@ActiveProfiles("local")
@Import(MybatisConfig.class)
public class H2ConnectionTest {
    
    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestMapper testMapper;

    @Test
    @DisplayName("H2 연결 테스트")
    void H2ConnectionTest(){
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(1)).isTrue();

            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("=== H2 Connection Info ===");
            System.out.println("Database: " + metaData.getDatabaseProductName());
            System.out.println("Version: " + metaData.getDatabaseProductVersion());
            System.out.println("URL: " + metaData.getURL());

        } catch (SQLException e) {
            fail("H2 연결 실패: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("H2 statement 쿼리 실행 테스트")
    void h2QueryTest() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            // when
            ResultSet resultSet = statement.executeQuery("SELECT 1");

            // then
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getInt(1)).isEqualTo(1);

            // DB 타입 확인
            String dbType = connection.getMetaData().getDatabaseProductName();
            assertThat(dbType.toLowerCase()).contains("h2");

        } catch (SQLException e) {
            fail("H2 쿼리 실행 실패: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Mybatis 연결 테스트")
    void mybatisConnectionTest() throws Exception {
        int testCase = 1;
        int result = testMapper.select(testCase);
        assertThat(result).isEqualTo(testCase);
    }
}
