package com.mild.andyou.controller.auth.rqrs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginRq {
    private String code;
}
