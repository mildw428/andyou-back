package com.mild.andyou.application.auth.dto;

import lombok.Data;

@Data
public class KakaoUserResponse {
    private String id;
    private KakaoAccount kakao_account;

    @Data
    public static class KakaoAccount {
        private Profile profile;
        private String email;
    }

    @Data
    public static class Profile {
        private String nickname;
        private String profile_image_url;
    }
}
