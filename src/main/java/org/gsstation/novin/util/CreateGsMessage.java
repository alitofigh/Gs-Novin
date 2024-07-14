package org.gsstation.novin.util;

import org.gsstation.novin.TransactionData;
import org.gsstation.novin.packager.GsPackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

/**
 * Created by A_Tofigh at 7/14/2024
 */

public class CreateGsMessage {

    ISOMsg gsMessage;

    public String CreateGsMessage(TransactionData transactionData) throws ISOException {
        gsMessage = new ISOMsg();
        gsMessage.setPackager(new GsPackager());
        gsMessage.setMTI("0100");
        gsMessage.set(2, transactionData.getGsId());
        gsMessage.set(3, transactionData.getPtId());
        gsMessage.set(4, transactionData.getShiftNo());
        gsMessage.set(5, transactionData.getDailyNo());
        gsMessage.set(6, "" + transactionData.getFuelTtc());
        gsMessage.set(7, "" + transactionData.getEpurseTtc());
        gsMessage.set(8, "" + transactionData.getFuelTime());
        gsMessage.set(9, "" + transactionData.getEpurseTime());
        gsMessage.set(10, transactionData.getFuelType());
        gsMessage.set(11, "" + transactionData.getTransType());
        gsMessage.set(12, transactionData.getNozzleId());
        gsMessage.set(13, transactionData.getUserCardId());
        gsMessage.set(14, transactionData.getFuelSamId());
        gsMessage.set(15, "" + transactionData.getTotalAmount());
        gsMessage.set(16, "" + transactionData.getN());
        gsMessage.set(17, "" + transactionData.getFuelStatus());
        gsMessage.set(18, "" + transactionData.getX());
        gsMessage.set(19, "" + transactionData.getX1());
        gsMessage.set(20, "" + transactionData.getX2());
        gsMessage.set(21, "" + transactionData.getX3());
        gsMessage.set(22, "" + transactionData.getR());
        gsMessage.set(23, "" + transactionData.getR1());
        gsMessage.set(24, "" + transactionData.getR2());
        gsMessage.set(25, "" + transactionData.getR3());
        gsMessage.set(26, "" + transactionData.getFTC());
        gsMessage.set(27, transactionData.getPaymentSamId());
        gsMessage.set(28, "" + transactionData.getTotalCost());
        gsMessage.set(29, "" + transactionData.getC());
        gsMessage.set(30, "" + transactionData.getC1());
        gsMessage.set(31, "" + transactionData.getC2());
        gsMessage.set(32, "" + transactionData.getC3());
        gsMessage.set(33, "" + transactionData.getP());
        gsMessage.set(34, "" + transactionData.getP1());
        gsMessage.set(35, "" + transactionData.getP2());
        gsMessage.set(36, "" + transactionData.getP3());
        gsMessage.set(37, "" + transactionData.getCashPayment());
        gsMessage.set(38, "" + transactionData.getCardPayment());
        gsMessage.set(39, "" + transactionData.getCtc());
        gsMessage.set(40, "" + transactionData.getTAC());
        gsMessage.set(41, "" + transactionData.getBeforeBalance());
        gsMessage.set(42, "" + transactionData.getAfterBalance());
        gsMessage.set(43, "" + transactionData.getRFU());
        gsMessage.set(44, "" + transactionData.getUploadFlag());
        return new String(gsMessage.pack());
    }
}
