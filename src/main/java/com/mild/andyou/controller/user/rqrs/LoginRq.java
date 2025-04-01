package com.mild.andyou.controller.user.rqrs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRq {
    private String id;
    private String gender;
    private String birthday;
    private String accessToken;
}
