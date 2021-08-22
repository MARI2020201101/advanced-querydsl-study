package com.mari.querydsl.controller;

import com.mari.querydsl.dto.MemberApiDto;
import com.mari.querydsl.dto.MemberSearchCondition;
import com.mari.querydsl.repository.MemberJpaRepository;
import com.mari.querydsl.repository.MemberRepository;
import com.mari.querydsl.repository.MemberRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository repository;

    @GetMapping("/v1/members")
    public List<MemberApiDto> searchMember(MemberSearchCondition condition){
        return repository.search(condition);
    }
}
