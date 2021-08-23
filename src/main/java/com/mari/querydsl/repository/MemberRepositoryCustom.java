package com.mari.querydsl.repository;

import com.mari.querydsl.dto.MemberApiDto;
import com.mari.querydsl.dto.MemberSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberApiDto> search(MemberSearchCondition condition);
    Page<MemberApiDto> searchSimplePage(MemberSearchCondition condition, Pageable pageable);
    Page<MemberApiDto> searchComplexPage(MemberSearchCondition condition, Pageable pageable);
}
