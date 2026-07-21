package com.samdasu.dodoong.domain.auth.entity;

import com.samdasu.dodoong.global.entity.BaseTimeEntity;
import com.samdasu.dodoong.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "refresh_tokens",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refresh_token_member",
                        columnNames = "member_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 500)
    private String token;

    private RefreshToken(Member member, String token) {
        this.member = member;
        this.token = token;
    }

    public static RefreshToken create(Member member, String token) {
        return new RefreshToken(member, token);
    }

    public void updateToken(String token) {
        this.token = token;
    }
}