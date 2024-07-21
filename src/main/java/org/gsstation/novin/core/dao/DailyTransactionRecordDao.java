package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.dao.domain.DailyTransactionRecord;

/**
 * Created by A_Tofigh at 07/22/2024
 */
public class DailyTransactionRecordDao extends JpaBaseDao<DailyTransactionRecord>{
    public DailyTransactionRecordDao(String databaseInstanceName) {
        super(DailyTransactionRecord.class, databaseInstanceName);
    }
    public DailyTransactionRecordDao(
            Class<DailyTransactionRecord> entityType,
            String databaseInstanceName) {
        super(entityType, databaseInstanceName);
    }
}
