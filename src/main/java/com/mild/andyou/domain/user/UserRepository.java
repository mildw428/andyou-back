package com.mild.andyou.domain.user;

import com.mild.andyou.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialTypeAndSocialId(User.SocialType type, String socialId);

    Optional<User> findByRefreshToken(String refreshToken);

}
