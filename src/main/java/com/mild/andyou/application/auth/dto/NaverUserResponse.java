package com.mild.andyou.application.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverUserResponse {
    private String resultcode;
    private String message;
    private Response response;
    
    @Getter
    @NoArgsConstructor
    public static class Response {
        private String id;
        private String nickname;
        private String name;
        private String email;
        private String gender;
        private String birthyear;
        private String birthday;
        private String profile_image;
    }
}