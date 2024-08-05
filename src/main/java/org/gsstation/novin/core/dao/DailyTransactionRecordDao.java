package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.dao.domain.BaseEntityTrx;
import org.gsstation.novin.core.dao.domain.DailyTransactionRecord;

/**
 * Created by A_Tofigh at 07/22/2024
 */
public class DailyTransactionRecordDao extends JpaBaseDao<BaseEntityTrx>{
    public DailyTransactionRecordDao(String databaseInstanceName) {
        super(BaseEntityTrx.class, databaseInstanceName);
    }
    public DailyTransactionRecordDao(Class<BaseEntityTrx> entityType, String databaseInstanceName) {
        super(entityType, databaseInstanceName);
    }


}
