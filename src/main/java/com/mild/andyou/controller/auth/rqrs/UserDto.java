package com.mild.andyou.controller.auth.rqrs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Boolean isNewUser;
    private Long id;
    private String nickname;
    private String birthYear;
    private String gender;
}
