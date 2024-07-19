package org.gsstation.novin.core.dao;

import lombok.Getter;
import org.gsstation.novin.core.common.configuration.ConfigurationUpdateListener;
import org.gsstation.novin.core.common.system.DeploymentUpdateNotifier;
import org.gsstation.novin.core.exception.InvalidConfigurationException;
import org.gsstation.novin.core.logging.GsLogger;
import org.gsstation.novin.core.logging.MainLogger;

import java.beans.IntrospectionException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.gsstation.novin.core.dao.DbmsType.ORACLE;
import static org.gsstation.novin.core.dao.DbmsType.SQLSERVER;

/**
 * Created by A_Tofigh at 07/19/2024
 */
public class DbConfigurationReader implements Cloneable {
    private static final String THIS_CLASS_NAME = "db-configuration-reader";
    static final String DEFAULT_INSTANCE_NAME = "main";
    static final int NOT_PRESENT_CONFIG_VALUE = -1;
    private static final String CONFIG_FILE_NAME = "db.properties";
    private static final String LEGACY_CONFIG_FILE_NAME = "Settings.properties";
    private static final String TARGET_DBMS_KEY = "target.dbms";
    private static final String HOST_KEY = "connection.host";
    private static final String PORT_KEY = "connection.port";
    private static final String SID_KEY = "connection.sid";
    private static final String SERVICE_NAME_KEY = "connection.service.name";
    private static final String CONNECTION_INSTANCE_NAME_KEY =
            "connection.instance.name";
    private static final String TNS_DESCRIPTOR_KEY =
            "connection.tns.descriptor";
    private static final String DATABASE_NAME_KEY = "connection.database.name";
    private static final String USERNAME_KEY = "connection.username";
    private static final String PASSWORD_KEY = "connection.password";
    private static final String MIN_POOL_SIZE_JPA_KEY =
            "connection.pool.size.min.jpa";
    private static final String MAX_POOL_SIZE_JPA_KEY =
            "connection.pool.size.max.jpa";
    private static final String MIN_POOL_SIZE_JDBC_KEY =
            "connection.pool.size.min.jdbc";
    private static final String MAX_POOL_SIZE_JDBC_KEY =
            "connection.pool.size.max.jdbc";
    private static final String MIN_POOL_SIZE_KEY =
            "connection.pool.size.min";
    private static final String MAX_POOL_SIZE_KEY =
            "connection.pool.size.max";
    private static final String QUERY_EXECUTION_TIMEOUT_KEY =
            "query.execution.timeout";
    private static final String CONNECTION_WAIT_TIMEOUT_KEY =
            "connection.wait.timeout";
    private static final String RECONNECT_TIMEOUT_KEY =
            "connection.reconnect.timeout";
    private static final String INACTIVE_CONNECTION_TIMEOUT_KEY =
            "connection.inactive.timeout";
    private static final String TIMEOUT_CHECK_INTERVAL_KEY =
            "connection.timeout.check.interval";
    private static final String VALIDATION_ON_BORROW_KEY =
            "connection.validation.on.borrow";
    private static final String POOL_NAME_KEY = "connection.pool.name";
    private static final String PERSISTENCE_PROVIDER_KEY =
            "persistence.provider";
    private static final String LOGGING_LEVEL_KEY = "logging.level";
    private static final String DB_CONFIGURATION_FILE_NOT_FOUND_MESSAGE =
            "Could not make a connection to database because the "
                    + "configuration file not found; expected: %s";
    private static final String DB_CONFIG_PROPERTY_MISSING_MESSAGE =
            "A property necessary to connect to database is missing from "
                    + "the configuration file; required property: %s";
    private static final String DB_CONFIG_PROPERTY_INVALID_MESSAGE =
            "A property necessary to connect to database has an invalid value "
                    + "in the configuration file; "
                    + "property: %s, invalid value: %s";

    @Getter
    private static Map<String, DbConfigurationReader>
            databaseConfigurationReaders = new HashMap<>();
    private static Map<String, List<ConfigurationUpdateListener<DbConfigurationReader>>>
            configurationUpdateListeners = new HashMap<>();
    private static ConfigurationUpdateListener<Path> dbConfigFileChangeListener;

    private String databaseInstanceName;
    @Getter
    private DbmsType targetDbms = ORACLE;
    @Getter
    private String host;
    @Getter
    private String port;
    @Getter
    private String sid;
    private String serviceName;
    private String connectionInstanceName;
    @Getter
    private String databaseName;
    @Getter
    private String username;
    @Getter
    private String password;
    private int minPoolSizeJpa;
    private int minPoolSizeJdbc;
    private int maxPoolSizeJpa;
    private int maxPoolSizeJdbc;
    private int minPoolSize;
    private int maxPoolSize;
    @Getter
    private int queryExecutionTimeout;
    @Getter
    private int connectionWaitTimeout;
    @Getter
    private int reconnectTimeout;
    @Getter
    private int inactiveConnectionTimeout;
    @Getter
    private int timeoutCheckInterval;
    private boolean validationOnBorrow;
    @Getter
    private String dbPoolName;
    @Getter
    private String persistenceProvider;
    @Getter
    private String loggingLevel;
    @Getter
    private String jdbcUrl;
    @Getter
    private String jdbcDriver;
    private String configFileName;
    private String tnsDescriptor;

    private DbConfigurationReader(String databaseInstanceName) {
        this.databaseInstanceName = databaseInstanceName;
        try {
            loadConfig();
            //observeDatabaseConfigurationFile();
        } catch (Exception e) {
            if (e instanceof InvalidConfigurationException)
                throw (InvalidConfigurationException) e;
            else
                throw new InvalidConfigurationException(e);
        }
    }

    public static DbConfigurationReader getInstance() {
        return getInstance("");
    }

    public static DbConfigurationReader getInstance(
            String databaseInstanceName) {
        if (databaseInstanceName == null || databaseInstanceName.isEmpty())
            databaseInstanceName = DEFAULT_INSTANCE_NAME;
        DbConfigurationReader instance =
                databaseConfigurationReaders.get(databaseInstanceName);
        if (instance != null)
            return instance;
        synchronized (DbConfigurationReader.class) {
            instance = databaseConfigurationReaders.get(databaseInstanceName);
            if (instance != null)
                return instance;
            instance = new DbConfigurationReader(databaseInstanceName);
            databaseConfigurationReaders.put(databaseInstanceName, instance);
        }
        return instance;
    }

    private void observeDatabaseConfigurationFile() throws IOException {
        if (dbConfigFileChangeListener != null)
            return;
        final Path configFilePath = Paths.get(configFileName).toAbsolutePath();
        dbConfigFileChangeListener = context -> {
            StringBuilder activitiesBuilder = new StringBuilder();
            try {
                @SuppressWarnings("UnnecessaryLocalVariable")
                Path path = context;
                if (!configFilePath.equals(path)) {
                    activitiesBuilder
                            .append("Irrelevant change to database ")
                            .append("configuration, ignored OS file ")
                            .append("system notification of change");
                    return;
                }
                for (Map.Entry<String, List<ConfigurationUpdateListener
                        <DbConfigurationReader>>> listener
                        : configurationUpdateListeners.entrySet()) {
                    if (activitiesBuilder.length() > 0)
                        activitiesBuilder.append("\n");
                    activitiesBuilder
                            .append("'").append(listener.getKey())
                            .append("' database configuration ")
                            .append("possible change detected...");
                    DbConfigurationReader currentConfigData =
                            DbConfigurationReader
                                    .getInstance(listener.getKey());
                    DbConfigurationReader oldConfigData =
                            (DbConfigurationReader)
                                    currentConfigData.clone();
                    currentConfigData.loadConfig();
                    activitiesBuilder.append("\nUpdated '")
                            .append(listener.getKey())
                            .append("' database configuration");
                    if (currentConfigData.equals(oldConfigData)) {
                        activitiesBuilder
                                .append("\nSame configuration ")
                                .append("values as before for '")
                                .append(listener.getKey())
                                .append("' database, ignored notification");
                        continue;
                    }
                    for (ConfigurationUpdateListener
                            <DbConfigurationReader> cul
                            : listener.getValue()) {
                        activitiesBuilder
                                .append("\nNotifying dependent module of '")
                                .append(listener.getKey())
                                .append("' database configuration ")
                                .append("changes; dependent module: ")
                                .append(cul);
                        // Log activities thus far before other
                        // module's log for better clarity of log
                        MainLogger.log(
                                activitiesBuilder.toString(), THIS_CLASS_NAME);
                        activitiesBuilder = new StringBuilder();
                        try {
                            cul.configurationUpdated(oldConfigData);
                        } catch (Exception e) {
                            GsLogger.log(e, THIS_CLASS_NAME);
                        }
                    }
                }
            } catch (Exception e) {
                GsLogger.log(e, THIS_CLASS_NAME);
            } finally {
                if (activitiesBuilder.length() > 0)
                    MainLogger.log(
                            activitiesBuilder.toString(), THIS_CLASS_NAME);
            }
        };
        DeploymentUpdateNotifier.getInstance().watch(CONFIG_FILE_NAME,
                dbConfigFileChangeListener);
    }

    private void loadConfig() {
        String configPrefix = databaseInstanceName.isEmpty()
                ? "" : databaseInstanceName + ".";
        InputStream configInputStream = null;
        try {
            Properties config = new Properties();
            // gets the file relative to where jvm started (working dir)
            String dbConfigurationFilePath =
                    new File(".").getAbsolutePath() + File.separator
                            + CONFIG_FILE_NAME;
            if (new File(dbConfigurationFilePath).exists()) {
                configFileName = CONFIG_FILE_NAME;
                configInputStream =
                        new FileInputStream(dbConfigurationFilePath);
                config.load(configInputStream);
            }
        } catch (Exception e) {
            if (!(e instanceof InvalidConfigurationException))
                throw new InvalidConfigurationException(e);
            throw (InvalidConfigurationException) e;
        } finally {
            try {
                if (configInputStream != null)
                    configInputStream.close();
            } catch (Exception ignored) {
            }
        }
        }

    public void addConfigurationUpdateListener(
            ConfigurationUpdateListener<DbConfigurationReader> listener) {
        configurationUpdateListeners.putIfAbsent(
                databaseInstanceName, new ArrayList<>());
        if (!configurationUpdateListeners
                .get(databaseInstanceName).contains(listener))
            configurationUpdateListeners
                    .get(databaseInstanceName).add(listener);
    }

    @SuppressWarnings("unused")
    public void removeConfigurationUpdateListener(
            ConfigurationUpdateListener<DbConfigurationReader> listener) {
        List<ConfigurationUpdateListener<DbConfigurationReader>>
                thisDatabaseListeners =
                configurationUpdateListeners.get(databaseInstanceName);
        if (thisDatabaseListeners == null)
            return;
        thisDatabaseListeners.remove(listener);
    }

    @SuppressWarnings("unused")
    public String getDatabaseInstanceName() {
        return databaseInstanceName;
    }

    public int getMinPoolSizeJpa() {
        return minPoolSizeJpa == NOT_PRESENT_CONFIG_VALUE
                ? minPoolSize : minPoolSizeJpa;
    }

    public int getMaxPoolSizeJpa() {
        return maxPoolSizeJpa == NOT_PRESENT_CONFIG_VALUE
                ? maxPoolSize : maxPoolSizeJpa;
    }

    public int getMinPoolSizeJdbc() {
        return minPoolSizeJdbc == NOT_PRESENT_CONFIG_VALUE
                ? minPoolSize : minPoolSizeJdbc;
    }

    public int getMaxPoolSizeJdbc() {
        return maxPoolSizeJdbc == NOT_PRESENT_CONFIG_VALUE
                ? maxPoolSize : maxPoolSizeJdbc;
    }

    public boolean getValidateOnBorrow() {
        return validationOnBorrow;
    }

    @SuppressWarnings("unused")
    public String getConfigFileName() {
        return configFileName;
    }

    /*@Override
    public boolean equals(Object other) {
        try {
            return ReflectionUtil.contentEquals(this, other);
        } catch (IntrospectionException | InvocationTargetException
                 | IllegalAccessException e) {
            GsLogger.log(e, THIS_CLASS_NAME);
            return false;
        }
    }*/
}
