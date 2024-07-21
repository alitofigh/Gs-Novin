package org.gsstation.novin.core.dao.domain;

import lombok.Data;
import org.jpos.iso.ISOMsg;

import javax.persistence.*;

@Data
@Entity
@Table(name = "daily_transaction_record")
public class DailyTransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "gs_id")
    private String gsId;
    @Column(name = "pt_id")
    private String ptId;
    @Column(name = "shift_no")
    private String shiftNo;
    @Column(name = "daily_no")
    private String dailyNo;
    @Column(name = "fuel_ttc")
    private String fuelTtc;
    @Column(name = "epurse_ttc")
    private String epurseTtc;
    @Column(name = "fuel_time")
    private String fuelTime;
    @Column(name = "epurse_time")
    private String epurseTime;
    @Column(name = "fuel_type")
    private String fuelType;
    @Column(name = "trans_type")
    private String transType;
    @Column(name = "nozzle_id")
    private String nozzleId;
    @Column(name = "usercard_id")
    private String userCardId;
    @Column(name = "fuel_sam_id")
    private String fuelSamId;
    @Column(name = "total_amount")
    private String totalAmount;
    @Column(name = "N")
    private String n;
    @Column(name = "fuel_status")
    private String fuelStatus;
    @Column(name = "X")
    private String x;
    @Column(name = "X1")
    private String x1;
    @Column(name = "X2")
    private String x2;
    @Column(name = "X3")
    private String x3;
    @Column(name = "R")
    private String r;
    @Column(name = "R1")
    private String r1;
    @Column(name = "R2")
    private String r2;
    @Column(name = "R3")
    private String r3;
    @Column(name = "FTC")
    private String FTC;
    @Column(name = "payment_sam_id")
    private String paymentSamId;
    @Column(name = "total_cost")
    private String totalCost;
    @Column(name = "C")
    private String c;
    @Column(name = "C1")
    private String c1;
    @Column(name = "C2")
    private String c2;
    @Column(name = "C3")
    private String c3;
    @Column(name = "P")
    private String p;
    @Column(name = "P1")
    private String p1;
    @Column(name = "P2")
    private String p2;
    @Column(name = "P3")
    private String p3;
    @Column(name = "cash_payment")
    private String cashPayment;
    @Column(name = "card_payment")
    private String cardPayment;
    @Column(name = "ctc")
    private String ctc;
    @Column(name = "TAC")
    private String TAC;
    @Column(name = "before_balance")
    private String beforeBalance;
    @Column(name = "after_balance")
    private String afterBalance;
    @Column(name = "RFU")
    private String RFU;
    @Column(name = "upload_flag")
    private String uploadFlag;
}
