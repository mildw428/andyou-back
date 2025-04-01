package com.mild.andyou.config.filter;

import com.mild.andyou.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    private Long userId;

    public UserContext(User user) {
        this.userId = user.getId();
    }
}