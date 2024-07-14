package org.gsstation.novin.packager;

import org.jpos.iso.*;

/**
 * Created by A_Tofigh at 7/14/2024
 */

public class GsPackager extends ISOBasePackager {
    protected ISOFieldPackager fld[] = {
            new IFA_NUMERIC (4, "Message Type Indicator"),
            new IFA_BITMAP( 16, "Bitmap"),
            new IF_CHAR(4, "gs_id"),
            new IF_CHAR(2, "pt_id"),
            new IF_CHAR(10, "shift_no"),
            new IF_CHAR(10, "daily_no"),
            new IFA_NUMERIC(10, "fuel_ttc"),
            new IFA_NUMERIC(10, "epurse_tcc"),
            new IFA_NUMERIC(10, "fuel_time"),
            new IFA_NUMERIC(10, "epurse_time"),
            new IF_CHAR(2, "fuel_type"),
            new IF_CHAR(1, "trans_type"),
            new IF_CHAR(2, "nozzle_id"),
            new IF_CHAR(16, "userCard_id"),
            new IF_CHAR(12, "fuel_sam_id"),
            new IFA_NUMERIC(20, "total_amount"),
            new IFA_NUMERIC(20, "N"),
            new IF_CHAR(1, "fuel_status"),
            new IF_CHAR(20, "X"),
            new IF_CHAR(20, "X1"),
            new IF_CHAR(20, "X2"),
            new IF_CHAR(20, "X3"),
            new IF_CHAR(20, "R"),
            new IF_CHAR(20, "R1"),
            new IF_CHAR(20, "R2"),
            new IF_CHAR(20, "R3"),
            new IFA_NUMERIC(10, "FTC"),
            new IF_CHAR(12, "payment_sam_id"),
            new IFA_NUMERIC(10, "total_cost"),
            new IFA_NUMERIC(10, "C"),
            new IFA_NUMERIC(10, "C1"),
            new IFA_NUMERIC(10, "C2"),
            new IFA_NUMERIC(10, "C3"),
            new IFA_NUMERIC(10, "P"),
            new IFA_NUMERIC(10, "P1"),
            new IFA_NUMERIC(10, "P2"),
            new IFA_NUMERIC(10, "P3"),
            new IFA_NUMERIC(10, "cash_payment"),
            new IFA_NUMERIC(10, "card_payment"),
            new IFA_NUMERIC(10, "ctc"),
            new IFA_NUMERIC(10, "TAC"),
            new IFA_NUMERIC(10, "before_balance"),
            new IFA_NUMERIC(10, "after_balance"),
            new IFA_NUMERIC(10, "RFU"),
            new IF_CHAR(1, "upload_flag")
    };

    public GsPackager() {
        super();
        setFieldPackager(fld);
    }
}
