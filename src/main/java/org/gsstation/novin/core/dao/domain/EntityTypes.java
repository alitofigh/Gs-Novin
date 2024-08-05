package org.gsstation.novin.core.dao.domain;

/**
 * Created by A_Tofigh at 7/25/2024
 */

public enum EntityTypes {
    TRANSACTION_RECORD(TransactionRecord.class),
    DAILY_TRANSACTION_RECORD(DailyTransactionRecord.class),
    SHIFT_TRANSACTION_RECORD(DailyTransactionRecord.class);


    Class c;

    EntityTypes (Class c) {
        this.c = c;
    }


}
