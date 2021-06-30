package com.wefox.kafka.consumerapi.enums;

import lombok.Getter;

@Getter
public enum  PaymentType {
    ONLINE("online"),
    OFFLINE("offline");
    private String value;

    PaymentType(String value) {
        this.value =value;
    }

    public static PaymentType getEnum(String value) {
          for (PaymentType v : values()) if (v.getValue().equals(value)) return v;
        throw new IllegalArgumentException();
     }
}
