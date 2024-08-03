package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.common.ResponseCode;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import static org.gsstation.novin.core.common.Constants.RESPONSE_CODE_KEY;
import static org.gsstation.novin.core.common.Constants.RESPONSE_MESSAGE_KEY;
import static org.gsstation.novin.core.common.ProtocolRulesBase.extractIsoMsg;
import static org.gsstation.novin.core.common.ResponseCodeV87.TRANSACTION_NOT_COMPLETED;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public class ResponsePreparing extends GsBaseParticipant {

    public static final int[] COMMON_RESPONSE_FIELDS =
            new int[]{0, 2, 3, 4, 7, 11, 12, 13, 32, 37, 41, 42, 48, 53, 62};

    @Override
    public void doCommit(Context context) throws Exception {
        ISOMsg requestMessage = extractIsoMsg(context);
        ISOMsg responseMessage;
        ResponseCode responseCode =
                (ResponseCode) context.get(RESPONSE_CODE_KEY);
        if (responseCode == null)
            responseCode = TRANSACTION_NOT_COMPLETED;
        responseMessage = (ISOMsg) requestMessage
                .clone(COMMON_RESPONSE_FIELDS);
        responseMessage.setResponseMTI();
        responseMessage.set(39, responseCode.code());
        propagateResult(RESPONSE_MESSAGE_KEY, responseMessage);
    }
}
