package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.module.ContextWrapper;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;
import org.jpos.transaction.GroupSelector;

import java.io.Serializable;

import static org.gsstation.novin.core.common.ProtocolRulesBase.INVALID_RESPONSE_TRANSACTION_GROUP_NAME;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public class TransactionGroupSelector implements GroupSelector, Configurable {
    Configuration configuration;

    @Override
    public String select(long id, Serializable serializable) {
        Context context = null;
        ISOMsg isoMessage;
        String targetTransactionGroup;
        try {
            if (serializable instanceof Context) {
                context = (Context) serializable;
                isoMessage = (ISOMsg) context.get(ContextWrapper.getOriginalMessageKey());
            } else if (serializable instanceof ISOMsg) {
                isoMessage = (ISOMsg) serializable;
            } else {
                return null;
            }
            boolean isRequest = isoMessage.isIncoming();
            String mti = isoMessage.getMTI();
            if ("0100".equals(mti)) {
                targetTransactionGroup = "ips-transaction";
            } else {
                targetTransactionGroup = INVALID_RESPONSE_TRANSACTION_GROUP_NAME;
            }
        } catch (ISOException e) {
            throw new RuntimeException(e);
        }
        context.put("transaction-group", targetTransactionGroup);
        return targetTransactionGroup;
    }

    @Override
    public int prepare(long id, Serializable serializable) {
        return PREPARED;
    }

    @Override
    public void commit(long id, Serializable serializable) {
        System.out.println("ooooooooooooooooooooooooooo");
    }

    @Override
    public void abort(long id, Serializable serializable) {
    }

    @Override
    public void setConfiguration(Configuration configuration)
            throws ConfigurationException {
        this.configuration = configuration;
    }
}
