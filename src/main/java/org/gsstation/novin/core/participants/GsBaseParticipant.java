package org.gsstation.novin.core.participants;

import lombok.SneakyThrows;
import org.gsstation.novin.core.dao.domain.*;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.gsstation.novin.core.dao.domain.EntityTypes.DAILY_TRANSACTION_RECORD;
import static org.gsstation.novin.core.dao.domain.EntityTypes.TRANSACTION_RECORD;
import static org.gsstation.novin.util.security.SecurityUtil.decryptCredentialAllParamsPredefined;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public abstract class GsBaseParticipant extends BaseParticipant {

    Map<String, String> macKeys = new HashMap<>();

    private void loadKeys() {
        Properties keysProperties = new Properties();
        try {
            File terminalKeysFile = new File("terminal-keys.properties");
            InputStream inputStream = new FileInputStream(terminalKeysFile);
            keysProperties.load(inputStream);
            inputStream.close();
            keysProperties.stringPropertyNames().forEach(propertyKey -> {
                String key;
                try {
                    key = decryptCredentialAllParamsPredefined(keysProperties.getProperty(propertyKey));
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
                macKeys.put(propertyKey, key);
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    protected BaseEntityTrx convertIso(ISOMsg isoMsg, EntityTypes entityType) {
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BaseEntityTrx entity = null;
        if (entityType == DAILY_TRANSACTION_RECORD) {
            entity = new DailyTransactionRecord();
            ((DailyTransactionRecord) entity).setGsId(isoMsg.getString(2));
            ((DailyTransactionRecord) entity).setPtId(isoMsg.getString(3));
            entity.setShiftNo(isoMsg.getString(4));
            entity.setDailyNo(isoMsg.getString(5));
            ((DailyTransactionRecord) entity).setFuelTtc(Integer.parseInt(isoMsg.getString(6)));
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
            ((TransactionRecord) entity).setId(transactionKey);
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

    @Override
    public int prepare(long id, Serializable serializable) {
        int parentPreparationResult = super.prepare(id, serializable);
        loadKeys(); // TODO: this method will be loaded every time.
        if (parentPreparationResult != PREPARED)
            return parentPreparationResult;
        else
            return PREPARED;
    }
}
