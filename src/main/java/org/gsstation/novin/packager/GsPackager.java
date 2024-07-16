package org.gsstation.novin.packager;

import org.jpos.iso.*;

/**
 * Created by A_Tofigh at 7/14/2024
 */

public class GsPackager extends ISOBasePackager {
    protected ISOFieldPackager fld[] = {
            /*000*/ new IFA_NUMERIC(4, "Message Type Indicator"),
            /*001*/ new IFA_BITMAP(16, "Bitmap"),
            /*002*/ new IF_CHAR(4, "gs_id"),
            /*003*/ new IF_CHAR(2, "pt_id"),
            /*004*/ new IFA_LLCHAR(99, "shift_no"),
            /*005*/ new IFA_LLCHAR(99, "daily_no"),
            /*006*/ new IFA_LLCHAR(99, "fuel_ttc"),
            /*007*/ new IFA_LLCHAR(99, "epurse_tcc"),
            /*008*/ new IFA_LLCHAR(99, "fuel_time"),
            /*009*/ new IFA_LLCHAR(99, "epurse_time"),
            /*010*/ new IF_CHAR(2, "fuel_type"),
            /*011*/ new IF_CHAR(1, "trans_type"),
            /*012*/ new IF_CHAR(2, "nozzle_id"),
            /*013*/ new IFA_LLCHAR(99, "userCard_id"),
            /*014*/ new IFA_LLCHAR(99, "fuel_sam_id"),
            /*015*/ new IFA_LLCHAR(99, "total_amount"),
            /*016*/ new IFA_LLCHAR(99, "N"),
            /*017*/ new IF_CHAR(1, "fuel_status"),
            /**/new IFA_LLCHAR(99, "X"),
            /**/new IFA_LLCHAR(99, "X1"),
            /**/new IFA_LLCHAR(99, "X2"),
            /**/new IFA_LLCHAR(99, "X3"),
            /**/new IFA_LLCHAR(99, "R"),
            /**/new IFA_LLCHAR(99, "R1"),
            /**/new IFA_LLCHAR(99, "R2"),
            /**/new IFA_LLCHAR(99, "R3"),
            /**/new IFA_LLCHAR(99, "FTC"),
            /**/new IFA_LLCHAR(99, "payment_sam_id"),
            /**/new IFA_LLCHAR(99, "total_cost"),
            /**/new IFA_LLCHAR(99, "C"),
            /**/new IFA_LLCHAR(99, "C1"),
            /**/new IFA_LLCHAR(99, "C2"),
            /**/new IFA_LLCHAR(99, "C3"),
            /**/new IFA_LLCHAR(99, "P"),
            /**/new IFA_LLCHAR(99, "P1"),
            /**/new IFA_LLCHAR(99, "P2"),
            /**/new IFA_LLCHAR(99, "P3"),
            /**/new IFA_LLCHAR(99, "cash_payment"),
            /**/new IFA_LLCHAR(99, "card_payment"),
            /**/new IFA_LLCHAR(99, "ctc"),
            /**/new IFA_LLCHAR(99, "TAC"),
            /**/new IFA_LLCHAR(99, "before_balance"),
            /**/new IFA_LLCHAR(99, "after_balance"),
            /**/new IFA_LLCHAR(99, "RFU"),
            /**/new IF_CHAR(1, "upload_flag")
    };

    public GsPackager() {
        super();
        setFieldPackager(fld);
    }
}
