package com.mild.andyou.application.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {
    USER("user"),
    MODEL("model"),
    ;
    private String value;
}
