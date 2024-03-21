package com.example.spring.dto;

import com.example.spring.constant.MemberStatus;
import com.example.spring.constant.RoleType;
import com.example.spring.entity.MemberEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MemberDto extends BaseDTO{
    private Long id;

    @NotNull(message = "이름이 비어 있습니다")
    private String name;

    @NotNull(message = "이메일이 비어 있습니다")
    @Email(message = "이메일을 양식을 지켜주세요")
    private String email;

    @NotNull
    @NotNull(message = "패스워드가 비어 있습니다")
    private String password;
    private String newPassword;
    private String emailValid;
    private String msg;
    private RoleType roleType;
    private MemberStatus memberStatus;

    @Builder
    public MemberDto(Long id, String name, String email, String password, String emailValid, String msg, LocalDateTime createdAt, String createdAtString, LocalDateTime updatedAt, RoleType roleType, MemberStatus memberStatus){
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.emailValid = emailValid;
        this.msg = msg;
        this.roleType = roleType;
        this.memberStatus = memberStatus;
    }

    public MemberEntity toEntity() {
        return MemberEntity.builder()
                .name(name)
                .email(email)
                .password(password)
                .roleType(roleType)
                .memberStatus(memberStatus)
                .build();
    }

    public MemberDto toDto(MemberEntity memberEntity){
        return MemberDto.builder()
                .id(memberEntity.getId())
                .name(memberEntity.getName())
                .email(memberEntity.getEmail())
                .roleType(memberEntity.getRoleType())
                .memberStatus(memberEntity.getMemberStatus())
                .build();
    }
}
