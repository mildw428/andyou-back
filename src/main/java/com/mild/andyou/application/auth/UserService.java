package com.mild.andyou.application.auth;

import com.mild.andyou.controller.user.rqrs.LoginRq;
import com.mild.andyou.domain.user.User;
import com.mild.andyou.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

}
