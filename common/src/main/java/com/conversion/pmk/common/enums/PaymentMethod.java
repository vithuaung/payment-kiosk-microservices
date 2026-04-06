package com.conversion.pmk.common.enums;

// Supported payment methods and their groupings
public enum PaymentMethod {

    NETS_CARD("NETS_CARD", "CARD"),
    NETS_QR("NETS_QR", "CARD"),
    NETS_FLASH("NETS_FLASH", "CARD"),
    CASH("CASH", "CASH"),
    CARD_UOB("CARD_UOB", "CARD"),
    CARD_OCBC("CARD_OCBC", "CARD"),
    CARD_UNION("CARD_UNION", "CARD"),
    ZERO_BILL("ZERO_BILL", "ZERO");

    private final String code;
    private final String group;

    PaymentMethod(String code, String group) {
        this.code = code;
        this.group = group;
    }

    public String getCode() {
        return code;
    }

    public String getGroup() {
        return group;
    }

    public boolean isCash() {
        return "CASH".equals(this.group);
    }

    public boolean isCard() {
        return "CARD".equals(this.group);
    }

    public boolean isZero() {
        return "ZERO".equals(this.group);
    }
}
