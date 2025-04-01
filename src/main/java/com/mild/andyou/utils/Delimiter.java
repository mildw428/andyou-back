package com.mild.andyou.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Delimiter {

    DOT("."),
    COMMA(","),
    SLASH("/"),
    COLON(":"),
    HYPHEN("-"),
    SPACE(" "),
    UNDER_BAR("_");

    private final String value;

    public String getRegex() {
        return "\\" + this.getValue();
    }

    public String join(String... strings) {
        return String.join(this.getValue(), strings);
    }

}