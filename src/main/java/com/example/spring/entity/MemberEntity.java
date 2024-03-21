package com.example.spring.entity;

import com.example.spring.constant.MemberStatus;
import com.example.spring.constant.RoleType;
import jakarta.persistence.*;
import lombok.*;

@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "member")
public class MemberEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "pawword", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name="role", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @Builder
    private MemberEntity(Long id, String name, String email, String password, RoleType roleType, MemberStatus memberStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleType = roleType;
        this.memberStatus = memberStatus;
    }
}
