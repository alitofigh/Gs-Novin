package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.dao.DailyTransactionRecordDao;
import org.gsstation.novin.core.dao.domain.BaseEntityTrx;
import org.gsstation.novin.core.dao.domain.DailyTransactionRecord;
import org.gsstation.novin.core.logging.MainLogger;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.gsstation.novin.core.common.ProtocolRulesBase.ORIGINAL_MESSAGE_KEY;
import static org.gsstation.novin.core.common.ProtocolRulesBase.extractIsoMsg;
import static org.gsstation.novin.core.dao.domain.EntityTypes.*;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public class StoreValidation extends GsBaseParticipant {

    @Override
    public void doCommit(Context context) {
        ISOMsg isoMessage = extractIsoMsg(context);
        DailyTransactionRecordDao dao = new DailyTransactionRecordDao("");
        List<BaseEntityTrx> entities = new ArrayList<>();
        entities.set(0 ,convertIso(isoMessage, DAILY_TRANSACTION_RECORD));
        entities.set(1 ,convertIso(isoMessage, TRANSACTION_RECORD));
        entities.set(2 ,convertIso(isoMessage, SHIFT_TRANSACTION_RECORD));
        MainLogger.log("done store 2");
    }
}
