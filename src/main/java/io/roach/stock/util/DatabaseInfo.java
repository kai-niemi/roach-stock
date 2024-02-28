package io.roach.stock.util;

import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class DatabaseInfo {
    private DatabaseInfo() {
    }

    public static String databaseVersion(DataSource dataSource) {
        try {
            return new JdbcTemplate(dataSource).queryForObject("select version()", String.class);
        } catch (DataAccessException e) {
            return "unknown";
        }
    }

    public static boolean isCockroachDB(DataSource dataSource) {
        return databaseVersion(dataSource).contains("CockroachDB");
    }

    public static void inspectDatabaseMetadata(DataSource dataSource, BiConsumer<String, Object> callback) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("databaseVersion", databaseVersion(dataSource));

        Connection connection = null;
        try {
            connection = DataSourceUtils.doGetConnection(dataSource);
            DatabaseMetaData metaData = connection.getMetaData();

            map.put("URL", connection.getMetaData().getURL());
            map.put("databaseProductName", metaData.getDatabaseProductName());
            map.put("databaseMajorVersion", metaData.getDatabaseMajorVersion());
            map.put("databaseMinorVersion", metaData.getDatabaseMinorVersion());
            map.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            map.put("driverMajorVersion", metaData.getDriverMajorVersion());
            map.put("driverMinorVersion", metaData.getDriverMinorVersion());
            map.put("driverName", metaData.getDriverName());
            map.put("driverVersion", metaData.getDriverVersion());
            map.put("maxConnections", metaData.getMaxConnections());
            map.put("defaultTransactionIsolation", metaData.getDefaultTransactionIsolation());
            map.put("transactionIsolation", connection.getTransactionIsolation());
            map.put("transactionIsolationName",
                    ConnectionProviderInitiator.toIsolationNiceName(connection.getTransactionIsolation()));

            map.forEach(callback);
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
