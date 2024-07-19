package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.logging.MainLogger;
import org.jpos.transaction.Context;

import java.io.Serializable;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public class StoreValidation extends BaseParticipant {

    @Override
    public void doCommit(Context context) {
        MainLogger.log("message received here -");

    }
}
