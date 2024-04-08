package pro.paulek.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class MySQL extends Database {
    private final static Logger logger = LoggerFactory.getLogger(MySQL.class);

    private HikariDataSource dataSource;

    private Map<String, String> credentials;

    public MySQL(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    @Override
    public void init(){
        HikariConfig config = new HikariConfig();

        Map<String, String> linkMap = credentials;
        String link = linkMap.get("jdbcUrl");

        config.setJdbcUrl(link.replace("{host}", linkMap.get("host")).replace("{port}", linkMap.get("port")).replace("{database-name}", linkMap.get("database-name")));
        config.setUsername(credentials.get("user"));
        config.setPassword(credentials.get("password"));
        config.setMaximumPoolSize(Integer.parseInt(linkMap.get("pool-size")));
        config.setConnectionTimeout(30000);

        this.dataSource = new HikariDataSource(config);
        logger.info(config.getJdbcUrl());

        dataSource.getConnectionTestQuery();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
