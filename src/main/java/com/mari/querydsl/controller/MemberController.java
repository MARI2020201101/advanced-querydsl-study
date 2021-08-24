package com.mari.querydsl.controller;

import com.mari.querydsl.dto.MemberApiDto;
import com.mari.querydsl.dto.MemberSearchCondition;
import com.mari.querydsl.repository.MemberJpaRepository;
import com.mari.querydsl.repository.MemberRepository;
import com.mari.querydsl.repository.MemberRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @GetMapping("/v2/members")
    public Page<MemberApiDto> searchMember2(MemberSearchCondition condition, Pageable pageable){
        return repository.searchSimplePage(condition,pageable);
    }
    @GetMapping("/v3/members")
    public Page<MemberApiDto> searchMember3(MemberSearchCondition condition, Pageable pageable){
        return repository.searchComplexPage(condition, pageable);
    }
}
