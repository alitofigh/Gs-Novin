package org.gsstation.novin.core.participants;

import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import static org.gsstation.novin.core.common.ProtocolRulesBase.extractIsoMsg;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public class MessageDataValidation extends BaseParticipant {
    @Override
    public void doCommit(Context context) {
        ISOMsg isoMessage = extractIsoMsg(context);
        System.out.println("message is here -------------" + isoMessage.getString(2));

    }
}
