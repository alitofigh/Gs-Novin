package org.gsstation.novin;

import lombok.Data;
import org.gsstation.novin.packager.GsPackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import sun.plugin.com.Packager;

import java.util.Date;

/**
 * Created by A_Tofigh at 7/13/2024
 */

@Data
public class TransactionData {

    String gsId;
    String ptId;
    String shiftNo;
    String dailyNo;
    int fuelTtc;
    int epurseTtc;
    Date fuelTime;
    Date epurseTime;
    String fuelType;
    char transType;
    String nozzleId;
    String userCardId;
    String fuelSamId;
    double totalAmount;
    double n;
    char fuelStatus;
    double x;
    double x1;
    double x2;
    double x3;
    double r;
    double r1;
    double r2;
    double r3;
    int FTC;
    String paymentSamId;
    int totalCost;
    int c;
    int c1;
    int c2;
    int c3;
    int p;
    int p1;
    int p2;
    int p3;
    int cashPayment;
    int cardPayment;
    int ctc;
    int TAC;
    int beforeBalance;
    int afterBalance;
    int RFU;
    char uploadFlag;
    ISOMsg gsMessage;

    public TransactionData(byte[] receivedStream) throws ISOException {
        gsMessage = new ISOMsg();
        gsMessage.setPackager(new GsPackager());
        gsMessage.unpack(receivedStream);
    }
}
