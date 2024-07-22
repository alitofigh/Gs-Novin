package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.dao.DailyTransactionRecordDao;
import org.gsstation.novin.core.logging.MainLogger;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import java.io.Serializable;

import static org.gsstation.novin.core.common.ProtocolRulesBase.ORIGINAL_MESSAGE_KEY;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public class StoreValidation extends BaseParticipant {

    @Override
    public void doCommit(Context context) {
        ISOMsg isoMessage = (ISOMsg) context.get(ORIGINAL_MESSAGE_KEY);
        MainLogger.log("message received here -" + isoMessage.getString(3));
        DailyTransactionRecordDao dao = new DailyTransactionRecordDao("");

        //dao.store();

    }
}
