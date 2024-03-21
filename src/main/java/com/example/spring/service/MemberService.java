package com.example.spring.service;

import com.example.spring.constant.MemberStatus;
import com.example.spring.constant.RoleType;
import com.example.spring.dto.MemberDto;
import com.example.spring.entity.MemberEntity;
import com.example.spring.repository.MemberRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.spring.entity.QMemberEntity.memberEntity;

@Service
public class MemberService extends QuerydslRepositorySupport {

    @Autowired
    EntityManager em;

    private final MemberRepository memberRepository;
    private final JPAQueryFactory queryFactory;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 에러 메시지 상수
    private static final String MEMBER_NOT_FOUND_MSG = "일치된 회원정보가 존재하지 않습니다";
    private static final String MEMBER_INFO_UPDATED_MSG = "회원정보가 변경되었습니다";
    private static final String ALREADY_REGISTERED_EMAIL_MSG = "이미 가입된 이메일입니다";
    private static final String REGISTRATION_SUCCESS_MSG = "가입 성공";
    private static final String UNKNOWN_SERVER_ERROR_MSG = "알 수 없는 서버 문제로 가입 실패";

    public MemberService(MemberRepository memberRepository, JPAQueryFactory queryFactory, BCryptPasswordEncoder bCryptPasswordEncoder) {
        super(MemberEntity.class);
        this.memberRepository = memberRepository;
        this.queryFactory = queryFactory;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<MemberDto> getAllMembers() {
        return queryFactory
                .selectFrom(memberEntity)
                .fetch()
                .stream()
                .map(memberEntityEntity -> new MemberDto().toDto(memberEntityEntity))
                .collect(Collectors.toList());
    }

    public MemberDto getMemberById(Long id) {
        MemberEntity res = queryFactory
                .selectFrom(memberEntity)
                .where(memberEntity.id.eq(id))
                .fetchOne();
        return res != null ? new MemberDto().toDto(res) : null;
    }

    // query
    // name, email, startDate ~ endDate search
    // lc1, lc2 is Logical Connectives, The currently implemented function is and, or
    // getSearch -> addKeywordConditions -> addDateConditions -> return
    public List<MemberDto> getSearch(final Map<String, String> searchCondition) {
        BooleanBuilder builder = new BooleanBuilder();
        String value1 = (String) searchCondition.getOrDefault("name", null);
        String lc1 = (String) searchCondition.getOrDefault("lc1", null);
        String value2 = (String) searchCondition.getOrDefault("email", null);
        String lc2 = (String) searchCondition.getOrDefault("lc2", null);
        LocalDateTime startDate = parseDateTime(searchCondition.get("startDate"));
        LocalDateTime endDate = parseDateTime(searchCondition.get("endDate"));

        // 키워드에 따른 조건 추가
        if (value1 != null || value2 != null) {
            addKeywordConditions(builder, value1, lc1, value2);
        }
        // 시작날짜와 종료날짜에 따른 조건 추가
        addDateConditions(builder, startDate, endDate, lc2);

        return queryFactory
                .select(Projections.fields(MemberDto.class,
                        memberEntity.id,
                        memberEntity.name,
                        memberEntity.email,
                        memberEntity.createdAt,
                        memberEntity.updatedAt,
                        memberEntity.createdAtString))
                .from(memberEntity)
                .where(builder)
                .fetch();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr != null) {
            try {
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e) {
                // 파싱 실패 시 null 반환
                return null;
            }
        }
        return null;
    }

    // BooleanBuilder
    private void addKeywordConditions(BooleanBuilder builder, String name, String lc1, String email){
        if(name != null){
            BooleanExpression namePredicate = memberEntity.name.containsIgnoreCase(name);
            builder.or(namePredicate);
        }
        if(email != null){
            BooleanExpression emailPredicate = memberEntity.email.containsIgnoreCase(email);
            if(lc1 == null || lc1.equalsIgnoreCase("or")) builder.or(emailPredicate);
            else builder.and(emailPredicate);
        }
    }

    // 시작날짜와 종료날짜에 따른 조건 추가 메서드
    private void addDateConditions(BooleanBuilder builder, LocalDateTime startDate, LocalDateTime endDate, String lc2) {
        // 시작날짜만 있는 경우
        if (startDate != null && endDate == null) {
            if(lc2 == null || lc2.equalsIgnoreCase("or")) builder.or(memberEntity.createdAt.goe(startDate));
            else builder.and(memberEntity.createdAt.goe(startDate));
        }
        // 종료날짜만 있는 경우
        else if (startDate == null && endDate != null) {
            if(lc2 == null || lc2.equalsIgnoreCase("or")) builder.or(memberEntity.createdAt.loe(endDate));
            else builder.and(memberEntity.createdAt.loe(endDate));
        }
        // 시작날짜와 종료날짜 모두 있는 경우
        else if (startDate != null && endDate != null) {
            if(lc2 == null || lc2.equalsIgnoreCase("or")) builder.or(memberEntity.createdAt.between(startDate, endDate));
            else builder.and(memberEntity.createdAt.between(startDate, endDate));
        }
    }

    @Transactional
    public MemberDto createMember(MemberDto memberDTO) {
        // 이미 가입된 이메일인지 확인
        MemberDto existingMember = findMemberByEmail(memberDTO.getEmail());
        if (existingMember != null) {
            memberDTO.setPassword(null);
            memberDTO.setMsg(ALREADY_REGISTERED_EMAIL_MSG);
            return memberDTO;
        }

        // 새 사용자 등록
        try {
            memberDTO.setRoleType(RoleType.ROLE_USER);
            memberDTO.setMemberStatus(MemberStatus.Y);
            MemberEntity memberEntity = memberDTO.toEntity();
            memberEntity.setPassword(bCryptPasswordEncoder.encode(memberDTO.getPassword()));
            memberRepository.save(memberEntity);
            memberDTO.setPassword(null);
            memberDTO.setMsg(REGISTRATION_SUCCESS_MSG);
        } catch (DataAccessException e) {
            memberDTO.setMsg(UNKNOWN_SERVER_ERROR_MSG);
        }
        return memberDTO;
    }



    @Transactional
    public MemberDto updateMember(Long id, MemberDto memberDTO) {
        // 유저 DTO를 엔티티로 변환
        MemberEntity memberEntity = memberDTO.toEntity();

        // 사용자 쿼리
        MemberDto res = findMemberByEmail(memberDTO.getEmail());

        // 사용자가 없으면 메시지 설정 후 반환
        if (res == null) {
            memberDTO.setMsg(MEMBER_NOT_FOUND_MSG);
            return memberDTO;
        }

        // 비밀번호 확인
        if (!isPasswordMatch(memberDTO.getPassword(), res.getPassword())) {
            memberDTO.setMsg(MEMBER_NOT_FOUND_MSG);
            return memberDTO;
        }

        // 사용자 정보 업데이트
        updateMemberInformation(memberDTO);

        // 성공 메시지 설정 후 반환
        memberDTO.setMsg(MEMBER_INFO_UPDATED_MSG);
        return memberDTO;
    }

    public MemberDto findMemberByEmail(String email) {
        return queryFactory
                .select(Projections.fields(MemberDto.class,
                        memberEntity.id,
                        memberEntity.name,
                        memberEntity.email,
                        memberEntity.createdAt,
                        memberEntity.updatedAt,
                        memberEntity.createdAtString))
                .from(memberEntity)
                .where(memberEntity.email.eq(email))
                .fetchOne();
    }

    private boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return rawPassword.equalsIgnoreCase(encodedPassword);
    }

    private void updateMemberInformation(MemberDto memberDTO) {
        queryFactory
                .update(memberEntity)
                .set(memberEntity.name, memberDTO.getName())
                .where(memberEntity.email.eq(memberDTO.getEmail()))
                .execute();
        em.flush();
        em.clear();
    }

    @Transactional
    public long deleteMemberById(Long id) {
        return queryFactory
                .delete(memberEntity)
                .where(memberEntity.id.eq(id))
                .execute();
    }
}
