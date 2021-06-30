package com.wefox.kafka.consumerapi.enums;

import lombok.Getter;

@Getter
public enum ErrorTypes {
    DATABASE("database"),
    NETWORK("network"),
    OTHERS("others");
    private String value;

    ErrorTypes(String value) {
        this.value =value;
    }

    public static ErrorTypes getEnum(String value) {
        for (ErrorTypes v : values()) if (v.getValue().equals(value)) return v;
        throw new IllegalArgumentException();
    }
}
