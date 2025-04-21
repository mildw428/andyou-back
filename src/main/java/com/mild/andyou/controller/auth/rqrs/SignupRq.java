package com.mild.andyou.controller.auth.rqrs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRq {
    private Long id;
    private Boolean privacyAgreed;
    private Boolean termsAgreed;
    private String birthYear;
    private String gender;
}
