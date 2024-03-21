환경변수
Oracle OpenJDK 17
Gradle 8.6
Spring boot 3.2.3
Spring security 6.2.2
jjwt 0.12.3
Mariadb 10.11.2

개발자 툴
Intellij IDEA
HeidiSQL 11.3.0.6295
PostMan

참고사이트
1) https://www.youtube.com/@xxxjjhhh
2) https://velog.io/@wonizizi99/Spring-QueryDsl-gradle-%EC%84%A4%EC%A0%95-Spring-boot-3.0-%EC%9D%B4%EC%83%81

해당 프로젝트는 2개 기능을 연습했습니다
1) 스프링 부트 + 시큐리티 + JWT 토큰
   - 아래 java 파일을 제외한 모든 파일
     
2) JPA + QueryDsl
   - QuerydslConfig.java
   - MemberStatus.java
   - RoleType.java
   - MemberController.java
   - BaseDTO.java
   - MemberDto.java
   - BaseEntity.java
   - MemberEntity.java
   - MemberRepository.java
   - MemberService.java
  * 엔티티 클래스를 Q클래스를 만들어야 하므로 gardle 를 통해 clean -> compile java 거쳐야됨
  * 스프링 부트 시작시 엔티티 클래스에 데이터베이스는 자동으로 생성(spring.jpa.hibernate.ddl-auto 옵션 참고)
         

패키지 트리
+---main
|   +---java
|   |   \---com
|   |       \---example
|   |           \---spring
|   |               |   Application.java
|   |               |
|   |               +---config
|   |               |       CorsMvcConfig.java
|   |               |       QuerydslConfig.java
|   |               |       SecurityConfig.java
|   |               |
|   |               +---constant
|   |               |       MemberStatus.java
|   |               |       RoleType.java
|   |               |
|   |               +---controller
|   |               |       AdminController.java
|   |               |       JoinController.java
|   |               |       LoginController.java
|   |               |       MainController.java
|   |               |       MemberController.java
|   |               |       ReissueController.java
|   |               |
|   |               +---dto
|   |               |       BaseDTO.java
|   |               |       CustomUserDetails.java
|   |               |       JoinDTO.java
|   |               |       LoginDTO.java
|   |               |       MemberDto.java
|   |               |
|   |               +---entity
|   |               |       BaseEntity.java
|   |               |       MemberEntity.java
|   |               |       RefreshEntity.java
|   |               |       UserEntity.java
|   |               |
|   |               +---jwt
|   |               |       CustomLogoutFilter.java
|   |               |       JWTFilter.java
|   |               |       JWTUtil.java
|   |               |       LoginFilter.java
|   |               |
|   |               +---repository
|   |               |       MemberRepository.java
|   |               |       RefreshRepository.java
|   |               |       UserRepository.java
|   |               |
|   |               \---service
|   |                       CustomUserDetailsService.java
|   |                       JoinService.java
|   |                       MemberService.java
|   |
|   \---resources
|       |   application.properties
|       |
|       +---static
|       \---templates
\---test
    \---java
        \---com
            \---example
                \---spring
                        ApplicationTests.java

결과물

1) 스프링 부트 + 시큐리티 + JWT 토큰
- 로그인후 JWT토큰 발급 (Refresh 토큰과 Access 토큰)
![로그인후 JWT토큰 발급 (Refresh 토큰과 Access 토큰)](https://github.com/gusrl6394/springboot_2024_v1/assets/20663508/e6df49dc-9c4c-4193-9727-a3811cc5fe70)

- Refresh 토큰 DB 저장
![Refresh 토큰 DB 저장](https://github.com/gusrl6394/springboot_2024_v1/assets/20663508/43b8f605-4234-4b04-b4c4-0f0853291086)

- Access 토큰을 통해 API 조회
![Access 토큰을 통해 API 조회](https://github.com/gusrl6394/springboot_2024_v1/assets/20663508/21ec2ef1-dde8-4b5f-ab7f-71f5cb03014b)


2) JPA + QueryDsl
- JPA 를 통한 Member 데이터 생성
![Member 생성 DB](https://github.com/gusrl6394/springboot_2024_v1/assets/20663508/adffa0c8-2f5c-450b-a0cb-5ee418198de8)

- QueryDsl 를 활용한 Member 데이터 검색 (다중 조건)
![Member 조건 검색 1](https://github.com/gusrl6394/springboot_2024_v1/assets/20663508/aa592d05-adda-4d6f-9266-313980b16121)

- QueryDsl 를 활용한 Member 데이터 검색 2 (다중 조건)
![Member 조건 검색 2](https://github.com/gusrl6394/springboot_2024_v1/assets/20663508/7303db26-f873-4557-a1bf-fd76c44ea63b)
