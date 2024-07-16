package org.gsstation.novin.util;

import org.gsstation.novin.TransactionData;
import org.gsstation.novin.packager.GsPackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

/**
 * Created by A_Tofigh at 7/14/2024
 */

public class CreateGsMessage {

    ISOMsg message;

    public String createGsMessage(TransactionData transactionData) throws ISOException {
        message = new ISOMsg();
        message.setPackager(new GsPackager());
        message.setMTI("0100");
        message.set(2, transactionData.getGsId());
        message.set(3, transactionData.getPtId());
        if (!transactionData.getShiftNo().isEmpty())
            message.set(4, transactionData.getShiftNo());
        if (transactionData.getDailyNo() != null && !transactionData.getDailyNo().isEmpty())
            message.set(5, transactionData.getDailyNo());
        message.set(6, transactionData.getFuelTtc());
        if (!transactionData.getEpurseTtc().isEmpty())
            message.set(7, transactionData.getEpurseTtc());
        if (!transactionData.getFuelTime().isEmpty())
            message.set(8, transactionData.getFuelTime());
        if (!transactionData.getEpurseTime().isEmpty())
            message.set(9, transactionData.getEpurseTime());
        message.set(10, transactionData.getFuelType());
        message.set(11, transactionData.getTransType());
        message.set(12, transactionData.getNozzleId());
        if (!transactionData.getUserCardId().isEmpty())
            message.set(13, transactionData.getUserCardId());
        if (!transactionData.getFuelSamId().isEmpty())
            message.set(14, transactionData.getFuelSamId());
        if (!transactionData.getTotalAmount().isEmpty())
            message.set(15, transactionData.getTotalAmount());
        if (!transactionData.getN().isEmpty())
            message.set(16, transactionData.getN());
        message.set(17, transactionData.getFuelStatus());
        if (!transactionData.getX().isEmpty())
            message.set(18, transactionData.getX());
        if (!transactionData.getX1().isEmpty())
            message.set(19, transactionData.getX1());
        if (!transactionData.getX2().isEmpty())
            message.set(20, transactionData.getX2());
        if (!transactionData.getX3().isEmpty())
            message.set(21, transactionData.getX3());
        if (!transactionData.getR().isEmpty())
            message.set(22, transactionData.getR());
        if (!transactionData.getR1().isEmpty())
            message.set(23, transactionData.getR1());
        if (!transactionData.getR2().isEmpty())
            message.set(24, transactionData.getR2());
        if (!transactionData.getR3().isEmpty())
            message.set(25, transactionData.getR3());
        if (transactionData.getFTC() != null && !transactionData.getFTC().isEmpty())
            message.set(26, transactionData.getFTC());
        if (!transactionData.getPaymentSamId().isEmpty())
            message.set(27, transactionData.getPaymentSamId());
        if (!transactionData.getTotalCost().isEmpty())
            message.set(28, transactionData.getTotalCost());
        if (!transactionData.getC().isEmpty())
            message.set(29, transactionData.getC());
        if (!transactionData.getC1().isEmpty())
            message.set(30, transactionData.getC1());
        if (!transactionData.getC2().isEmpty())
            message.set(31, transactionData.getC2());
        if (!transactionData.getC3().isEmpty())
            message.set(32, transactionData.getC3());
        if (!transactionData.getP().isEmpty())
            message.set(33, transactionData.getP());
        if (!transactionData.getP1().isEmpty())
            message.set(34, transactionData.getP1());
        if (!transactionData.getP2().isEmpty())
            message.set(35, transactionData.getP2());
        if (!transactionData.getP3().isEmpty())
            message.set(36, transactionData.getP3());
        if (!transactionData.getCashPayment().isEmpty())
            message.set(37, transactionData.getCashPayment());
        if (!transactionData.getCardPayment().isEmpty())
            message.set(38, transactionData.getCardPayment());
        if (!transactionData.getCtc().isEmpty())
            message.set(39, transactionData.getCtc());
        if (!transactionData.getTAC().isEmpty())
            message.set(40, transactionData.getTAC());
        if (!transactionData.getBeforeBalance().isEmpty())
            message.set(41, transactionData.getBeforeBalance());
        if (!transactionData.getAfterBalance().isEmpty())
            message.set(42, transactionData.getAfterBalance());
        if (!transactionData.getRFU().isEmpty())
            message.set(43, transactionData.getRFU());
        message.set(44, transactionData.getUploadFlag());
        return new String(message.pack());
    }
}
