package com.mild.andyou.config.filter;

import com.mild.andyou.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserContext {
    private Long userId;
    private String ip;

    public UserContext(String ip) {
        this.ip = ip;
    }

    public UserContext(User user, String ip) {
        this.userId = user.getId();
        this.ip = ip;
    }
}