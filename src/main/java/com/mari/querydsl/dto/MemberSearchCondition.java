package com.mari.querydsl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSearchCondition {
    private String username;
    private String teamname;
    private Integer goeAge;
    private Integer loeAge;

}


