package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.exception.InvalidMacException;
import org.gsstation.novin.util.security.SecurityUtil;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.transaction.Context;

import java.util.Arrays;

import static org.gsstation.novin.core.common.ProtocolRulesBase.extractIsoMsg;

/**
 * Created by A_Tofigh at 08/03/2024
 */
public class MacValidation extends GsBaseParticipant {

    @Override
    public void doCommit(Context context) throws Exception {
        ISOMsg isoMessage = extractIsoMsg(context);
        byte[] mac = isoMessage.getBytes(64);
        byte[] computedMacBytes =
                SecurityUtil.computeGsMessageMac(isoMessage, macKeys.get("key3"));
        if (!Arrays.equals(computedMacBytes, mac))
            throw new InvalidMacException();


    }
}
