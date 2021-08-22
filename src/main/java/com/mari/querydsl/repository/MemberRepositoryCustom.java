package com.mari.querydsl.repository;

import com.mari.querydsl.dto.MemberApiDto;
import com.mari.querydsl.dto.MemberSearchCondition;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberApiDto> search(MemberSearchCondition condition);
}
