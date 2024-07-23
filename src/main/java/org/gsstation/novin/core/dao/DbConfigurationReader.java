package org.gsstation.novin.core.dao;

import lombok.Getter;
import org.gsstation.novin.core.common.configuration.ConfigurationUpdateListener;
import org.gsstation.novin.core.common.system.DeploymentUpdateNotifier;
import org.gsstation.novin.core.exception.InvalidConfigurationException;
import org.gsstation.novin.core.logging.GsLogger;
import org.gsstation.novin.core.logging.MainLogger;
import org.gsstation.novin.util.security.CryptoUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.gsstation.novin.core.common.ProtocolRulesBase.INTEGER_NUMBER_REGEXP;
import static org.gsstation.novin.core.dao.DbmsType.ORACLE;
import static org.gsstation.novin.core.dao.DbmsType.SQLSERVER;
import static org.gsstation.novin.util.security.SecurityUtil.decryptCredentialAllParamsPredefined;

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
        boolean tryBothDefaultAndMainPrefixes = databaseInstanceName.isEmpty()
                || DEFAULT_INSTANCE_NAME.equalsIgnoreCase(databaseInstanceName);
        String configPrefix = tryBothDefaultAndMainPrefixes
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
                /*// gets the file relative to execution classpath
                InputStream configInputStream =
                        Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream(CONFIG_FILE_NAME);
                if (configInputStream == null)
                    throw new FileNotFoundException(
                            DB_CONFIGURATION_FILE_NOT_FOUND_MESSAGE);*/
                config.load(configInputStream);
                if (tryBothDefaultAndMainPrefixes)
                    targetDbms = DbmsType.fromName(
                            config.getProperty(TARGET_DBMS_KEY,
                                    config.getProperty(DEFAULT_INSTANCE_NAME
                                            + "." + HOST_KEY)));
                else
                    targetDbms = DbmsType.fromName(
                            config.getProperty(configPrefix + TARGET_DBMS_KEY));
                if (tryBothDefaultAndMainPrefixes)
                    host = config.getProperty(HOST_KEY,
                            config.getProperty(DEFAULT_INSTANCE_NAME
                                    + "." + HOST_KEY));
                else
                    host = config.getProperty(configPrefix + HOST_KEY);
                if (host == null)
                    throw new InvalidConfigurationException(String.format(
                            DB_CONFIG_PROPERTY_MISSING_MESSAGE,
                            configPrefix + HOST_KEY));
                if (host.isEmpty())
                    throw new InvalidConfigurationException(String.format(
                            DB_CONFIG_PROPERTY_INVALID_MESSAGE,
                            configPrefix + HOST_KEY, host));
                if (tryBothDefaultAndMainPrefixes)
                    port = config.getProperty(PORT_KEY,
                            config.getProperty(DEFAULT_INSTANCE_NAME
                                    + "." + PORT_KEY));
                else
                    port = config.getProperty(configPrefix + PORT_KEY);
                // For SQL Server the port can be default and not specified
                if (SQLSERVER != targetDbms) {
                    if (port == null)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_MISSING_MESSAGE,
                                configPrefix + PORT_KEY));
                    if (port.isEmpty() || !port.matches(INTEGER_NUMBER_REGEXP))
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_INVALID_MESSAGE,
                                configPrefix + PORT_KEY, port));
                }
                if (tryBothDefaultAndMainPrefixes)
                    sid = config.getProperty(SID_KEY,
                            config.getProperty(DEFAULT_INSTANCE_NAME
                                    + "." + SID_KEY));
                else
                    sid = config.getProperty(configPrefix + SID_KEY);
                // SID is mandatory just for Oracle database
                if (ORACLE == targetDbms) {
                    if (sid == null)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_MISSING_MESSAGE,
                                configPrefix + SID_KEY));
                    if (sid.isEmpty())
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_INVALID_MESSAGE,
                                configPrefix + SID_KEY, sid));
                }
                if (tryBothDefaultAndMainPrefixes)
                    databaseName = config.getProperty(DATABASE_NAME_KEY,
                            config.getProperty(DEFAULT_INSTANCE_NAME
                                    + "." + DATABASE_NAME_KEY));
                else
                    databaseName = config.getProperty(
                            configPrefix + DATABASE_NAME_KEY);
                String encryptedUsername;
                if (tryBothDefaultAndMainPrefixes)
                    encryptedUsername = config.getProperty(USERNAME_KEY,
                            config.getProperty(DEFAULT_INSTANCE_NAME
                                    + "." + USERNAME_KEY));
                else
                    encryptedUsername =
                            config.getProperty(configPrefix + USERNAME_KEY);
                // For SQL Server integrated security is an option (no username)
                if (ORACLE == targetDbms) {
                    if (encryptedUsername == null)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_MISSING_MESSAGE,
                                configPrefix + USERNAME_KEY));
                    if (encryptedUsername.isEmpty()
                        /*|| encryptedUsername.length() % 8 != 0*/)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_INVALID_MESSAGE,
                                configPrefix + USERNAME_KEY,
                                encryptedUsername));
                }
                if (encryptedUsername != null)
                    username = decryptCredentialAllParamsPredefined(
                            encryptedUsername);
                String encryptedPassword;
                if (tryBothDefaultAndMainPrefixes)
                    encryptedPassword = config.getProperty(PASSWORD_KEY,
                            config.getProperty(DEFAULT_INSTANCE_NAME
                                    + "." + PASSWORD_KEY));
                else
                    encryptedPassword =
                            config.getProperty(configPrefix + PASSWORD_KEY);
                if (ORACLE == targetDbms) {
                    if (encryptedPassword == null)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_MISSING_MESSAGE,
                                configPrefix + PASSWORD_KEY));
                    if (encryptedPassword.isEmpty()
                        /*|| encryptedPassword.length() % 8 != 0*/)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_INVALID_MESSAGE,
                                configPrefix + PASSWORD_KEY,
                                encryptedPassword));
                }
                if (encryptedPassword != null)
                    password = decryptCredentialAllParamsPredefined(
                            encryptedPassword);
                if (tryBothDefaultAndMainPrefixes)
                    minPoolSize = Integer.parseInt(
                            config.getProperty(MIN_POOL_SIZE_KEY,
                                    config.getProperty(DEFAULT_INSTANCE_NAME
                                                    + "." + MIN_POOL_SIZE_KEY,
                                            "" + NOT_PRESENT_CONFIG_VALUE)));
                else
                    minPoolSize = Integer.parseInt(config.getProperty(
                            configPrefix + MIN_POOL_SIZE_KEY,
                            "" + NOT_PRESENT_CONFIG_VALUE));
                if (tryBothDefaultAndMainPrefixes)
                    maxPoolSize = Integer.parseInt(
                            config.getProperty(MAX_POOL_SIZE_KEY,
                                    config.getProperty(DEFAULT_INSTANCE_NAME
                                                    + "." + MAX_POOL_SIZE_KEY,
                                            "" + NOT_PRESENT_CONFIG_VALUE)));
                else
                    maxPoolSize = Integer.parseInt(config.getProperty(
                            configPrefix + MAX_POOL_SIZE_KEY,
                            "" + NOT_PRESENT_CONFIG_VALUE));
                if (tryBothDefaultAndMainPrefixes)
                    queryExecutionTimeout = (int) (Double.parseDouble(
                            config.getProperty(QUERY_EXECUTION_TIMEOUT_KEY,
                                    config.getProperty(DEFAULT_INSTANCE_NAME
                                                    + "." + QUERY_EXECUTION_TIMEOUT_KEY,
                                            "" + NOT_PRESENT_CONFIG_VALUE)))
                            * 1000);
                else
                    queryExecutionTimeout = (int) (Double.parseDouble(
                            config.getProperty(
                                    configPrefix + QUERY_EXECUTION_TIMEOUT_KEY,
                                    "" + NOT_PRESENT_CONFIG_VALUE)) * 1000);
                if (tryBothDefaultAndMainPrefixes)
                    connectionWaitTimeout = (int) (Double.parseDouble(
                            config.getProperty(CONNECTION_WAIT_TIMEOUT_KEY,
                                    config.getProperty(DEFAULT_INSTANCE_NAME
                                                    + "." + CONNECTION_WAIT_TIMEOUT_KEY,
                                            "" + NOT_PRESENT_CONFIG_VALUE)))
                            * 1000);
                else
                    connectionWaitTimeout = (int) (Double.parseDouble(
                            config.getProperty(
                                    configPrefix + CONNECTION_WAIT_TIMEOUT_KEY,
                                    "" + NOT_PRESENT_CONFIG_VALUE)) * 1000);
                if (tryBothDefaultAndMainPrefixes)
                    reconnectTimeout = (int) (Double.parseDouble(
                            config.getProperty(RECONNECT_TIMEOUT_KEY,
                                    config.getProperty(DEFAULT_INSTANCE_NAME
                                                    + "." + RECONNECT_TIMEOUT_KEY,
                                            "" + NOT_PRESENT_CONFIG_VALUE)))
                            * 1000);
                else
                    reconnectTimeout = (int) (Double.parseDouble(
                            config.getProperty(
                                    configPrefix + RECONNECT_TIMEOUT_KEY,
                                    "" + NOT_PRESENT_CONFIG_VALUE)) * 1000);
                if (tryBothDefaultAndMainPrefixes)
                    inactiveConnectionTimeout = (int) (Double.parseDouble(
                            config.getProperty(INACTIVE_CONNECTION_TIMEOUT_KEY,
                                    config.getProperty(DEFAULT_INSTANCE_NAME
                                                    + "." + INACTIVE_CONNECTION_TIMEOUT_KEY,
                                            "" + NOT_PRESENT_CONFIG_VALUE)))
                            * 1000);
                else
                    inactiveConnectionTimeout = (int) (Double.parseDouble(
                            config.getProperty(configPrefix
                                            + INACTIVE_CONNECTION_TIMEOUT_KEY,
                                    "" + NOT_PRESENT_CONFIG_VALUE)) * 1000);
                if (tryBothDefaultAndMainPrefixes)
                    timeoutCheckInterval = (int) (Double.parseDouble(
                            config.getProperty(TIMEOUT_CHECK_INTERVAL_KEY,
                                    config.getProperty(DEFAULT_INSTANCE_NAME
                                                    + "." + TIMEOUT_CHECK_INTERVAL_KEY,
                                            "" + NOT_PRESENT_CONFIG_VALUE)))
                            * 1000);
                else
                    timeoutCheckInterval = (int) (Double.parseDouble(
                            config.getProperty(
                                    configPrefix + TIMEOUT_CHECK_INTERVAL_KEY,
                                    "" + NOT_PRESENT_CONFIG_VALUE)) * 1000);
                if (tryBothDefaultAndMainPrefixes)
                    validationOnBorrow = Boolean.parseBoolean(
                            config.getProperty(VALIDATION_ON_BORROW_KEY,
                                    config.getProperty(DEFAULT_INSTANCE_NAME
                                                    + "." + VALIDATION_ON_BORROW_KEY,
                                            "false")));
                else
                    validationOnBorrow = Boolean.parseBoolean(
                            config.getProperty(
                                    configPrefix + VALIDATION_ON_BORROW_KEY,
                                    "false"));
                if (tryBothDefaultAndMainPrefixes)
                    dbPoolName = config.getProperty(POOL_NAME_KEY,
                            config.getProperty(DEFAULT_INSTANCE_NAME
                                            + "." + POOL_NAME_KEY,
                                    databaseInstanceName + "-connection-pool"));
                else
                    dbPoolName = config.getProperty(
                            configPrefix + POOL_NAME_KEY,
                            databaseInstanceName + "-connection-pool");
                if (tryBothDefaultAndMainPrefixes)
                    loggingLevel = config.getProperty(LOGGING_LEVEL_KEY,
                            config.getProperty(DEFAULT_INSTANCE_NAME
                                    + "." + LOGGING_LEVEL_KEY, "INFO"));
                else
                    loggingLevel = config.getProperty(
                            configPrefix + LOGGING_LEVEL_KEY, "INFO");
                if (tryBothDefaultAndMainPrefixes)
                    persistenceProvider = config.getProperty(
                            PERSISTENCE_PROVIDER_KEY,
                            config.getProperty(DEFAULT_INSTANCE_NAME
                                            + "." + PERSISTENCE_PROVIDER_KEY,
                                    "eclipselink"));
                else
                    persistenceProvider = config.getProperty(
                            configPrefix + PERSISTENCE_PROVIDER_KEY,
                            "eclipselink");
            } else {
                String legacyDbConfigurationFilePath =
                        new File(".").getAbsolutePath() + File.separator
                                + LEGACY_CONFIG_FILE_NAME;
                if (new File(legacyDbConfigurationFilePath).exists()) {
                    configFileName = LEGACY_CONFIG_FILE_NAME;
                    configInputStream =
                            new FileInputStream(legacyDbConfigurationFilePath);
                    config.load(configInputStream);
                    host = config.getProperty("HostName");
                    if (host == null)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_MISSING_MESSAGE,
                                "HostName"));
                    if (host.isEmpty())
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_INVALID_MESSAGE,
                                "HostName", host));
                    port = config.getProperty("OraclePort", "1521");
                    if (port == null)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_MISSING_MESSAGE,
                                "OraclePort"));
                    if (port.isEmpty() || !port.matches(INTEGER_NUMBER_REGEXP))
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_INVALID_MESSAGE,
                                "OraclePort", port));
                    sid = config.getProperty("SID");
                    if (sid != null && sid.startsWith(":"))
                        sid = sid.substring(1);
                    if (sid == null)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_MISSING_MESSAGE,
                                "SID"));
                    if (sid.isEmpty())
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_INVALID_MESSAGE,
                                "SID", sid));
                    this.databaseName = config.getProperty(DATABASE_NAME_KEY);
                    String encryptedUsername = config.getProperty("UserName");
                    if (encryptedUsername == null)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_MISSING_MESSAGE,
                                "UserName"));
                    if (encryptedUsername.isEmpty()
                        /*|| encryptedUsername.length() % 8 != 0*/)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_INVALID_MESSAGE,
                                "UserName", encryptedUsername));
                    username = CryptoUtil.DecryptUserPass(encryptedUsername);
                    String encryptedPassword = config.getProperty("Password");
                    if (encryptedPassword == null)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_MISSING_MESSAGE,
                                "Password"));
                    if (encryptedPassword.isEmpty()
                        /*|| encryptedPassword.length() % 8 != 0*/)
                        throw new InvalidConfigurationException(String.format(
                                DB_CONFIG_PROPERTY_INVALID_MESSAGE,
                                "Password", encryptedPassword));
                    password = CryptoUtil.DecryptUserPass(encryptedPassword);
                    minPoolSize = Integer.valueOf(
                            config.getProperty("PoolSize"));
                    maxPoolSize = Integer.valueOf(
                            config.getProperty("MaxPoolSize"));
                    reconnectTimeout = Integer.valueOf(
                            config.getProperty("waitForReconnect", "1")) * 1000;
                    dbPoolName = config.getProperty("PoolName");
                } else {
                    throw new FileNotFoundException(String.format(
                            DB_CONFIGURATION_FILE_NOT_FOUND_MESSAGE,
                            dbConfigurationFilePath));
                }
            }
            // Default dbms is Oracle
            if (ORACLE == targetDbms) {
                jdbcUrl = "jdbc:oracle:thin:@" + host + ":" + port + "/" + sid;
                jdbcDriver = "oracle.jdbc.pool.OracleDataSource";
                // jdbcDriver = "oracle.jdbc.OracleDriver"
            } else if (SQLSERVER == targetDbms) {
                jdbcUrl = "jdbc:sqlserver://" + host;
                if (sid != null && !sid.isEmpty())
                    jdbcUrl += "\\" + sid;
                if (port != null && !port.isEmpty())
                    jdbcUrl += ":" + port;
                if (databaseName != null && !databaseName.isEmpty())
                    jdbcUrl += ";databaseName=" + databaseName;
                jdbcDriver =
                        "com.microsoft.sqlserver.jdbc.SQLServerDataSource";
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
