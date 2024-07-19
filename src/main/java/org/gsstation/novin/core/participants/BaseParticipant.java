package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.common.Constants;
import org.gsstation.novin.core.logging.MainLogger;
import org.jdom2.Element;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.core.XmlConfigurable;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import org.jpos.transaction.Context;
import org.jpos.transaction.TransactionParticipant;

import java.io.Serializable;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public abstract class BaseParticipant
        implements TransactionParticipant, Configurable, XmlConfigurable {

    protected Space interconnectSpace;
    protected Configuration configuration;

    public abstract void doCommit(Context ctx);

    @Override
    public void commit(long id, Serializable context) {
        if (checkPreviousResult(context))
            doCommit((Context) context);
        else {
            MainLogger.log("previousResult was not successful.");
        }
    }

    @Override
    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        this.configuration = configuration;
    }

    @Override
    public void setConfiguration(Element element) throws ConfigurationException {

    }

    @Override
    public int prepare(long l, Serializable serializable) {
        interconnectSpace = SpaceFactory.getSpace();
        return PREPARED;
    }

    public boolean checkPreviousResult(Serializable context) {
        return (Boolean) ((Context) context).get(Constants.PREVIOUS_RESULT);
    }
}
