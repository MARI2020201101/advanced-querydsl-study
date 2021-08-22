package com.mari.querydsl.repository;

import com.mari.querydsl.dto.MemberApiDto;
import com.mari.querydsl.dto.MemberSearchCondition;
import com.mari.querydsl.dto.QMemberApiDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.mari.querydsl.entity.QMember.member;
import static com.mari.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<MemberApiDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberApiDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamname")
                ))
                .from(member)
                .join(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamnameEq(condition.getTeamname()),
                        ageGoe(condition.getGoeAge()),
                        ageLoe(condition.getLoeAge()))
                .fetch();
    }

    private BooleanExpression ageLoe(Integer loeAge) {
        return loeAge!=null? member.age.loe(loeAge):null;
    }

    private BooleanExpression ageGoe(Integer goeAge) {
        return goeAge!=null? member.age.goe(goeAge):null;
    }

    private BooleanExpression teamnameEq(String teamname) {
        return hasText(teamname)? team.name.eq(teamname):null;
    }

    private BooleanExpression usernameEq(String username) {
        return hasText(username)? member.username.eq(username): null;
    }
}
