package org.gsstation.novin.core.dao;

import oracle.ucp.UniversalConnectionPoolException;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.gsstation.novin.core.exception.GeneralDatabaseException;
import org.gsstation.novin.core.exception.InvalidConfigurationException;
import org.gsstation.novin.core.logging.GsLogEvent;
import org.gsstation.novin.core.logging.GsLogger;
import org.gsstation.novin.core.logging.MainLogger;
import org.jpos.util.LogEvent;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.util.logging.Level.CONFIG;
import static org.gsstation.novin.core.dao.DbConfigurationReader.DEFAULT_INSTANCE_NAME;
import static org.gsstation.novin.core.dao.DbConfigurationReader.NOT_PRESENT_CONFIG_VALUE;
import static org.gsstation.novin.core.dao.DbmsType.ORACLE;
import static org.gsstation.novin.core.dao.DbmsType.SQLSERVER;

/**
 * Created by A_Tofigh at 07/19/2024
 */
public class DbConnectionFactory {
    private static final String THIS_CLASS_NAME = "db-connection-factory";

    private static final int DEFAULT_MIN_POOL_SIZE = 5;
    private static final int DEFAULT_MAX_POOL_SIZE = 20;
    private static final int DEFAULT_INACTIVE_CONNECTION_TIMEOUT = 15 * 60;
    private static final int DEFAULT_TIMEOUT_CHECK_INTERVAL = 5 * 60;
    private static final int DEFAULT_CONNECTION_WAIT_TIMEOUT = 5;
    private static final int DEFAULT_RECONNECT_TIMEOUT = 5;
    private static final int SOCKET_TIMEOUT_LATENCY = 1000; // in milliseconds
    @SuppressWarnings("unused")
    public static final ExecutorService
            TIMEOUT_EXECUTOR = Executors.newSingleThreadExecutor();

    private static Map<String, DbConnectionFactory>
            databaseConnectionFactories = new HashMap<>();
    private static UniversalConnectionPoolManager poolManager;

    private String databaseInstanceName;
    private DbConfigurationReader dbConfigurationReader;
    private PoolDataSource poolDataSource;

    private DbConnectionFactory(String databaseInstanceName)
            throws GeneralDatabaseException {
        long startTimestamp = System.currentTimeMillis();
        GsLogEvent logEvent = MainLogger.createLogEvent(
                THIS_CLASS_NAME, databaseInstanceName);
        this.databaseInstanceName = databaseInstanceName;
        try {
            dbConfigurationReader =
                    DbConfigurationReader.getInstance(databaseInstanceName);
            if (poolManager == null)
                poolManager = UniversalConnectionPoolManagerImpl
                        .getUniversalConnectionPoolManager();
            updatePoolDataSource();
            MainLogger.addMessage(logEvent, CONFIG, "Successfully built '"
                    + databaseInstanceName + "' database connection pool\n"
                    + "JDBC connection pool properties - "
                    + "initial: " + poolDataSource.getInitialPoolSize()
                    + ", min: " + poolDataSource.getMinPoolSize()
                    + ", max: " + poolDataSource.getMaxPoolSize());
        } catch (Exception e) {
            throw new GeneralDatabaseException(e);
        } finally {
            MainLogger.addMessage(logEvent, CONFIG,
                    "Database instance construction time '"
                            + (System.currentTimeMillis() - startTimestamp)
                            + "'ms");
            MainLogger.log(logEvent);
        }
    }

    public static DbConnectionFactory getInstance(String instanceName)
            throws GeneralDatabaseException {
        if (instanceName == null || instanceName.isEmpty())
            instanceName = DEFAULT_INSTANCE_NAME;
        DbConnectionFactory instance =
                databaseConnectionFactories.get(instanceName);
        if (instance != null)
            return instance;
        try {
            synchronized (DbConnectionFactory.class) {
                /* Maybe in the meanwhile some other concurrent thread has
                initialized the pool so first check it */
                instance = databaseConnectionFactories.get(instanceName);
                if (instance != null)
                    return instance;
                instance = new DbConnectionFactory(instanceName);
                databaseConnectionFactories.put(instanceName, instance);
            }
            return instance;
        } catch (GeneralDatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralDatabaseException(e);
        }
    }

    public static DbConnectionFactory getInstance()
            throws GeneralDatabaseException {
        return getInstance("");
    }

    public static Connection newConnection(
            String instanceName, Object requesterInfo)
            throws GeneralDatabaseException {
        long startTimestamp = System.currentTimeMillis();
        GsLogEvent logEvent = MainLogger.createLogEvent(
                THIS_CLASS_NAME, requesterInfo == null
                        ? instanceName : requesterInfo.toString());
        if (instanceName == null || instanceName.isEmpty())
            instanceName = DEFAULT_INSTANCE_NAME;
        DbConnectionFactory instance;
        try {
            instance = DbConnectionFactory.getInstance(instanceName);
            MainLogger.addMessage(logEvent, CONFIG,
                    "JDBC connection pool stats - in use: "
                            + instance.poolDataSource
                            .getBorrowedConnectionsCount()
                            + ", available: "
                            + instance.poolDataSource
                            .getAvailableConnectionsCount());
            return obtainHealthyConnection(instance);
        } catch (GeneralDatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralDatabaseException(e);
        } finally {
            MainLogger.addMessage(logEvent, CONFIG,
                    "Database connection acquiring time '"
                            + (System.currentTimeMillis() - startTimestamp)
                            + "'ms");
            MainLogger.log(logEvent);
        }
    }

    public static void close() throws GeneralDatabaseException {
        if (poolManager == null)
            return;
        try {
            Stream.of(poolManager.getConnectionPoolNames()).forEach(
                    poolName -> {
                        try {
                            poolManager.stopConnectionPool(poolName);
                            poolManager.destroyConnectionPool(poolName);
                        } catch (Exception e) {
                            GsLogger.log(e, poolName, THIS_CLASS_NAME);
                        }
                    });
        } catch (Exception e) {
            throw new GeneralDatabaseException(e);
        }
    }

    private static Connection obtainHealthyConnection(
            DbConnectionFactory instance)
            throws SQLException {
        DbConfigurationReader dbConfig = instance.dbConfigurationReader;
        //noinspection unused
        int socketReadTimeout = dbConfig.getQueryExecutionTimeout() != 0
                ? dbConfig.getQueryExecutionTimeout() + SOCKET_TIMEOUT_LATENCY
                : 0;
        //noinspection UnnecessaryLocalVariable
        Connection healthyConnection =
                instance.poolDataSource.getConnection();
        // Below works just for jdbc +4.1 (make sure of your driver)
        /*try {
            healthyConnection.setNetworkTimeout(
                    TIMEOUT_EXECUTOR, socketReadTimeout);
        } catch (Throwable e) {
            CmLogger.log(e, THIS_CLASS_NAME);
        }*/
        return healthyConnection;
    }

    private void updatePoolDataSource()
            throws SQLException, InvalidConfigurationException,
            UniversalConnectionPoolException {
        poolDataSource = PoolDataSourceFactory.getPoolDataSource();
        dbConfigurationReader =
                DbConfigurationReader.getInstance(databaseInstanceName);
        String jdbcUrl = dbConfigurationReader.getJdbcUrl();
        if (ORACLE == dbConfigurationReader.getTargetDbms()) {
            poolDataSource.setConnectionFactoryClassName(
                    dbConfigurationReader.getJdbcDriver());
            poolDataSource.setUser(dbConfigurationReader.getUsername());
            poolDataSource.setPassword(dbConfigurationReader.getPassword());
        } else if (SQLSERVER == dbConfigurationReader.getTargetDbms()) {
            poolDataSource.setConnectionFactoryClassName(
                    dbConfigurationReader.getJdbcDriver());
            poolDataSource.setUser(dbConfigurationReader.getUsername());
            if (dbConfigurationReader.getPassword() != null &&
                    !dbConfigurationReader.getPassword().isEmpty())
                poolDataSource.setPassword(dbConfigurationReader.getPassword());
            // port MUST not be set for some SQL Servers to connect to!
            if (dbConfigurationReader.getPort() != null &&
                    !dbConfigurationReader.getPort().isEmpty())
                poolDataSource.setPortNumber(
                        Integer.parseInt(dbConfigurationReader.getPort()));
            if (dbConfigurationReader.getDatabaseName() != null &&
                    !dbConfigurationReader.getDatabaseName().isEmpty())
                poolDataSource.setDatabaseName(
                        dbConfigurationReader.getDatabaseName());
            poolDataSource.setSQLForValidateConnection("select 1");
        }
        poolDataSource.setURL(jdbcUrl);
        int minPoolSize = dbConfigurationReader.getMinPoolSizeJdbc()
                != NOT_PRESENT_CONFIG_VALUE
                ? dbConfigurationReader.getMinPoolSizeJdbc()
                : DEFAULT_MIN_POOL_SIZE;
        int maxPoolSize = dbConfigurationReader.getMaxPoolSizeJdbc()
                != NOT_PRESENT_CONFIG_VALUE
                ? dbConfigurationReader.getMaxPoolSizeJdbc()
                : DEFAULT_MAX_POOL_SIZE;
        int connectionWaitTimeout =
                dbConfigurationReader.getConnectionWaitTimeout()
                        != NOT_PRESENT_CONFIG_VALUE
                        ? dbConfigurationReader.getConnectionWaitTimeout()
                        : DEFAULT_CONNECTION_WAIT_TIMEOUT * 1000;
        @SuppressWarnings("unused")
        int reconnectTimeout = dbConfigurationReader.getReconnectTimeout()
                != NOT_PRESENT_CONFIG_VALUE
                ? dbConfigurationReader.getReconnectTimeout()
                : DEFAULT_RECONNECT_TIMEOUT * 1000;
        int inactiveConnectionTimeout =
                dbConfigurationReader.getInactiveConnectionTimeout()
                        != NOT_PRESENT_CONFIG_VALUE
                        ? dbConfigurationReader.getInactiveConnectionTimeout()
                        : DEFAULT_INACTIVE_CONNECTION_TIMEOUT * 1000;
        int timeoutCheckInterval =
                dbConfigurationReader.getTimeoutCheckInterval()
                        != NOT_PRESENT_CONFIG_VALUE
                        ? dbConfigurationReader.getTimeoutCheckInterval()
                        : DEFAULT_TIMEOUT_CHECK_INTERVAL * 1000;
        String poolName = dbConfigurationReader.getDbPoolName() != null
                ? dbConfigurationReader.getDbPoolName()
                : databaseInstanceName + "-connection-pool";
        if (Arrays.asList(poolManager.getConnectionPoolNames())
                .contains(poolName))
            poolManager.destroyConnectionPool(poolName);
        poolDataSource.setConnectionPoolName(poolName);
        poolDataSource.setMinPoolSize(minPoolSize);
        poolDataSource.setMaxPoolSize(maxPoolSize);
        // Times below are set as seconds (as per UCP documentation)
        poolDataSource.setInactiveConnectionTimeout(
                inactiveConnectionTimeout / 1000);
        poolDataSource.setTimeoutCheckInterval(timeoutCheckInterval / 1000);
        poolDataSource.setConnectionWaitTimeout(connectionWaitTimeout / 1000);
        poolDataSource.setValidateConnectionOnBorrow(
                dbConfigurationReader.getValidateOnBorrow());
        /* Actual connection and response (read) timeout forcing is carried out
         * by the two properties below */
        /* Connect timeout property controls how long at most will it take to
         * establish a socket to destination, as opposed to the read timeout
         * property below which determines how long at most we will wait for
         * the response from the socket after the socket has been established */
        /* Set socket read timeout in addition to query timeout, query timeout
         * is for graceful cancelling of the work by instructing DBMS to do so,
         * on the other hand socket timeout is for network failure conditions
         * where query timeout is known not to work because it cannot reach
         * DBMS server, besides query timeout may take long if no response
         * received from database, to enforce a timeout rely on socket timeout
         * Allow for an extra 1000ms to ensure query timeout had enough time to
         * cancel the statement (socket timeout should be greater than query's) */
        DbmsType targetDbms = dbConfigurationReader.getTargetDbms();
        int socketConnectTimeout =
                dbConfigurationReader.getConnectionWaitTimeout();
        int socketReadTimeout =
                dbConfigurationReader.getQueryExecutionTimeout() != 0
                        ? dbConfigurationReader.getQueryExecutionTimeout()
                        + SOCKET_TIMEOUT_LATENCY
                        : 0;
        switch (targetDbms) {
            case ORACLE:
                poolDataSource.setConnectionProperty(
                        "oracle.net.CONNECT_TIMEOUT",
                        "" + socketConnectTimeout);
                poolDataSource.setConnectionProperty(
                        "oracle.jdbc.ReadTimeout",
                        "" + socketReadTimeout);
                break;
            case SQLSERVER:
                poolDataSource.setConnectionProperty(
                        "loginTimeout", "" + socketConnectTimeout);
                poolDataSource.setConnectionProperty(
                        "socketTimeout", "" + socketReadTimeout);
                break;
            case MYSQL:
                poolDataSource.setConnectionProperty(
                        "connectTimeout", "" + socketConnectTimeout);
                poolDataSource.setConnectionProperty(
                        "socketTimeout", "" + socketReadTimeout);
                break;
        }
        poolDataSource.setLoginTimeout(connectionWaitTimeout);
        /* This is where actual connections are made to database
        (as many as specified initial size) */
        poolDataSource.setInitialPoolSize(minPoolSize);
    }

    @SuppressWarnings("unused")
    public DbConfigurationReader getDbConfigurationReader() {
        return dbConfigurationReader;
    }
}
