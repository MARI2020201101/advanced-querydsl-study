package com.mari.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberApiDto {
    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String name;

    @QueryProjection
    public MemberApiDto(Long memberId, String username, int age, Long teamId, String name) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.name = name;
    }
}
