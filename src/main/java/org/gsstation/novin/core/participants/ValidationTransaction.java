package org.gsstation.novin.core.participants;

import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import static org.gsstation.novin.core.common.ProtocolRulesBase.ORIGINAL_MESSAGE_KEY;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public class ValidationTransaction extends BaseParticipant {
    @Override
    public void doCommit(Context context) {
        ISOMsg isoMessage = (ISOMsg) context.get(context.get(ORIGINAL_MESSAGE_KEY));
        System.out.println("message is here -------------" + isoMessage.getString(2));

    }
}
