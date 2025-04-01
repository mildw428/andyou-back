package com.mild.andyou.domain.user;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "unique_social", columnNames = {"socialType", "socialId"})
})
@Getter @Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType;

    @Column(nullable = false)
    private String socialId;

    @Column
    private String nickname;

    @Column(length = 500)
    private String profileImage;

    private String refreshToken;

    private LocalDateTime refreshExp;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum SocialType {
        KAKAO, NAVER
    }

    public User (Long id) {
        this.id = id;
    }

    public User (SocialType type, String socialId) {
        this.socialType = type;
        this.socialId = socialId;
    }

    public void updateRefresh(String refreshToken, Date refreshTokenExp) {
        this.refreshToken = refreshToken;
        this.refreshExp = refreshTokenExp.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    public Boolean isExp() {
        return LocalDateTime.now().isAfter(refreshExp);
    }
}
