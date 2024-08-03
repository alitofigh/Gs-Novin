package org.gsstation.novin.core.common;

import org.gsstation.novin.core.exception.UnknownResponseCodeException;

import static org.gsstation.novin.util.validation.StringUtil.fixWidthZeroPad;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public enum ResponseCodeV87 implements ResponseCode{
    TRANSACTION_SUCCEEDED(
            "00", "Transaction completed successfully",
            "تراکنش با موفقیت انجام شد"),
    TRANSACTION_NOT_COMPLETED(
            "80", "Transaction was not performed",
            "تراکنش انجام نشد"),
    SYSTEM_ERROR_OCCURRED(
            "96", "System error occurred while doing the transaction",
            "بروز خطای سیستمی در انجام تراکنش"),
    DUPLICATE_TRANSACTION(
            "94", "Duplicate transaction was sent",
            "ارسال تراکنش تکراری");

    private String code;
    private String descriptionEn;
    private String descriptionFa;
    private ResponseCodeV87(
            String responseCode, String descriptionEn,
            String descriptionFa) {
        this.code = responseCode;
        this.descriptionEn = descriptionEn;
        this.descriptionFa = descriptionFa;
    }

    public static ResponseCode fromCode(String code)
            throws UnknownResponseCodeException {
        if (code == null || code.length() < 2)
            return TRANSACTION_NOT_COMPLETED;
        for (ResponseCodeV87 responseCodeV87 : ResponseCodeV87.values()) {
            if (responseCodeV87.code.equals(code))
                return responseCodeV87;
        }
        return SYSTEM_ERROR_OCCURRED;
    }

    public static ResponseCode fromCode(int code)
            throws UnknownResponseCodeException {
        return fromCode(fixWidthZeroPad(String.valueOf(code), 2));
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String description() {
        return descriptionEn;
    }
}
