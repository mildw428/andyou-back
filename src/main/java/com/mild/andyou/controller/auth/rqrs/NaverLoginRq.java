package com.mild.andyou.controller.auth.rqrs;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverLoginRq {
    private String code;
    private String state;
}