package com.example.spring.controller;

import com.example.spring.dto.MemberDto;
import com.example.spring.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/member")
@ResponseBody
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMembers() {
        List<MemberDto> Members = memberService.getAllMembers();
        Map<String, Object> result = new HashMap<>();
        result.put("data", Members);
        result.put("count", Members.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMemberById(@PathVariable Long id) {
        MemberDto member = memberService.getMemberById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("data", member);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> getMemberBySearch(@RequestBody @Valid Map<String, String> searchCondition) {
        List<MemberDto> member = memberService.getSearch(searchCondition);
        Map<String, Object> result = new HashMap<>();
        result.put("data", member);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createMember(@RequestBody @Valid MemberDto memberDTO, BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        try {
            MemberDto createdMember = memberService.createMember(memberDTO);
            result.put("data", createdMember);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e){
            if(bindingResult.getErrorCount() > 0){
                result.put("error", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            } else {
                result.put("error", e.getMessage());
            }
            return ResponseEntity.ok(result);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMember(@PathVariable Long id, @RequestBody @Valid MemberDto memberDTO) {
        MemberDto updatedMember = memberService.updateMember(id, memberDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("data", updatedMember);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMember(@PathVariable Long id) {
        long count = memberService.deleteMemberById(id);
        Map<String, Object> result = new HashMap<>();
        if(count > 0){
            result.put("msg", "삭제 완료");
        } else {
            result.put("msg", "삭제 실패");
        }
        return ResponseEntity.ok(result);
//        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
       return  ResponseEntity.ok("test");
    }
}
