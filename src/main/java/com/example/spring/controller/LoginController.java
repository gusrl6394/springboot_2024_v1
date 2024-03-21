package com.example.spring.controller;

import com.example.spring.dto.MemberDto;
import com.example.spring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final MemberService memberService;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> memberLogin(@RequestBody MemberDto memberDto) {
        MemberDto member = memberService.findMemberByEmail(memberDto.getEmail());
        Map<String, Object> result = new HashMap<>();
        result.put("data", member);
        return ResponseEntity.ok(result);
    }
}
