package com.mari.querydsl.repository;

import com.mari.querydsl.dto.MemberApiDto;
import com.mari.querydsl.dto.MemberDto;
import com.mari.querydsl.dto.MemberSearchCondition;
import com.mari.querydsl.dto.QMemberApiDto;
import com.mari.querydsl.entity.Member;
import com.mari.querydsl.entity.QMember;
import com.mari.querydsl.entity.QTeam;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;

import java.util.List;

import static com.mari.querydsl.entity.QMember.*;
import static com.mari.querydsl.entity.QTeam.*;
import static org.springframework.util.StringUtils.*;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

   private final JPAQueryFactory queryFactory;

   public List<MemberApiDto> selectMember(){
      return queryFactory
              .select(new QMemberApiDto(
                      member.id.as("memberId"),
                      member.username,
                      member.age,
                      team.id.as("teamId"),
                      team.name
              ))
              .from(member)
              .join(member.team, team)
              .fetch();
   }

   public List<MemberApiDto> selectMemberWithCond(MemberSearchCondition condition){
      BooleanBuilder builder = new BooleanBuilder();
      if(hasText(condition.getUsername())){
         builder.and(member.username.eq(condition.getUsername()));
      }
      if(hasText(condition.getTeamname())){
         builder.and(team.name.eq(condition.getTeamname()));
      }
      if(condition.getGoeAge()!=null){
         builder.and(member.age.goe(condition.getGoeAge()));
      }
      if(condition.getLoeAge()!=null){
         builder.and(member.age.loe(condition.getLoeAge()));
      }

      return queryFactory
              .select(new QMemberApiDto(
                      member.id.as("memberId"),
                      member.username,
                      member.age,
                      team.id.as("teamId"),
                      team.name
              ))
              .from(member)
              .join(member.team, team)
              .where(builder)
              .fetch();
   }

   public List<MemberApiDto> searchWithCondition(MemberSearchCondition condition){
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
