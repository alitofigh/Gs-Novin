package org.gsstation.novin.core.common;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public abstract class ProtocolRulesBase {
    public static final int MESSAGE_ARRIVAL_TIMESTAMP_FIELD = 111;
    public static final String MESSAGE_ARRIVAL_TIMESTAMP = "arrival-timestamp";
    public static final int LISTENER_MODULE_FIELD = 110;
    public static final String LISTENER_MODULE = "listener-module";
    public static final String REMOTE_ENDPOINT = "remote-endpoint";
    public static final String DEFAULT_ORIGINAL_MESSAGE_KEY = "MSG_Key";
    public static final String ORIGINAL_MESSAGE_KEY = "original-message-key";
    public static final String DEFAULT_SPACE_URI = "tspace:default";
    public static final String SPACE_URI = "space-uri";
    public static final String INPUT_QUEUE = "input-queue";
    public static final String OUTPUT_QUEUE = "output-queue";
    public static final int DEFAULT_IN_SPACE_TIMEOUT = 15;
    public static final String IN_SPACE_TIMEOUT = "in-space-timeout";
    public static final String CONTEXT_ADDITIONAL_DATA = "context-additional-data";
    public static final String INVALID_REQUEST_TRANSACTION_GROUP_NAME =
            "invalid-request-transaction";
    public static final String INVALID_RESPONSE_TRANSACTION_GROUP_NAME = "invalid-response-transaction";
    public static final String MAIN_LOGGER_QBEAN_NAME = "main-logger";
    public static final String MAIN_LOGGER_QBEAN_LEGACY_NAME = "Q2";
    public static final String STANDARD_TIME_FORMAT = "HH:mm:ss.SSS";
    public static final String INTEGER_NUMBER_REGEXP =
            "[0-9]+";
}
