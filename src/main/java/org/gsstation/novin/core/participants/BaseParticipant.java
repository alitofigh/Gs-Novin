package org.gsstation.novin.core.participants;

import lombok.SneakyThrows;
import org.gsstation.novin.core.common.Constants;
import org.gsstation.novin.core.dao.domain.*;
import org.gsstation.novin.core.logging.MainLogger;
import org.jdom2.Element;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.core.XmlConfigurable;
import org.jpos.iso.ISOMsg;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import org.jpos.transaction.Context;
import org.jpos.transaction.TransactionParticipant;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static org.gsstation.novin.core.dao.domain.EntityTypes.DAILY_TRANSACTION_RECORD;
import static org.gsstation.novin.core.dao.domain.EntityTypes.TRANSACTION_RECORD;

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

    @SneakyThrows
    protected BaseEntityTrx convertIso(ISOMsg isoMsg, EntityTypes entityType) {
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BaseEntityTrx entity = null;
        if (entityType == DAILY_TRANSACTION_RECORD) {
            entity = new DailyTransactionRecord();
            ((DailyTransactionRecord)entity).setGsId(isoMsg.getString(2));
            ((DailyTransactionRecord)entity).setPtId(isoMsg.getString(3));
            entity.setShiftNo(isoMsg.getString(4));
            entity.setDailyNo(isoMsg.getString(5));
            ((DailyTransactionRecord)entity).setFuelTtc(Integer.parseInt(isoMsg.getString(6)));
            entity.setEpurseTtc(Integer.parseInt(isoMsg.getString(7)));
            entity.setFuelTime(sourceFormat.parse(isoMsg.getString(8)));
            entity.setEpurseTime(sourceFormat.parse(isoMsg.getString(9)));
            entity.setFuelType(isoMsg.getString(10));
            entity.setTransType(isoMsg.getString(11));
            entity.setNozzleId(isoMsg.getString(12));
            entity.setUserCardId(isoMsg.getString(13));
            entity.setFuelSamId(isoMsg.getString(14));
            entity.setTotalAmount(Double.parseDouble(isoMsg.getString(15)));
            entity.setPaymentSamId(isoMsg.getString(27));
            entity.setP(Integer.parseInt(isoMsg.getString(33)));
            entity.setP1(Integer.parseInt(isoMsg.getString(34)));
            entity.setP2(Integer.parseInt(isoMsg.getString(35)));
            entity.setP3(Integer.parseInt(isoMsg.getString(36)));
            return entity;
        } else if (entityType == TRANSACTION_RECORD) {
            entity = new TransactionRecord();
            TransactionKey transactionKey = new TransactionKey(
                    isoMsg.getString(2),
                    isoMsg.getString(3),
                    Integer.parseInt(isoMsg.getString(6)));
            ((TransactionRecord)entity).setId(transactionKey);
            entity.setShiftNo(isoMsg.getString(4));
            entity.setDailyNo(isoMsg.getString(5));
            entity.setEpurseTtc(Integer.parseInt(isoMsg.getString(7)));
            entity.setFuelTime(sourceFormat.parse(isoMsg.getString(8)));
            entity.setEpurseTime(sourceFormat.parse(isoMsg.getString(9)));
            entity.setFuelType(isoMsg.getString(10));
            entity.setTransType(isoMsg.getString(11));
            entity.setNozzleId(isoMsg.getString(12));
            entity.setUserCardId(isoMsg.getString(13));
            entity.setFuelSamId(isoMsg.getString(14));
            entity.setTotalAmount(Double.parseDouble(isoMsg.getString(15)));
            entity.setPaymentSamId(isoMsg.getString(27));
            entity.setP(Integer.parseInt(isoMsg.getString(33)));
            entity.setP1(Integer.parseInt(isoMsg.getString(34)));
            entity.setP2(Integer.parseInt(isoMsg.getString(35)));
            entity.setP3(Integer.parseInt(isoMsg.getString(36)));
        }
        return entity;
    }
}
