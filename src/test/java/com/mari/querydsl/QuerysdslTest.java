package com.mari.querydsl;

import com.mari.querydsl.dto.MemberDto;
import com.mari.querydsl.dto.QMemberDto;
import com.mari.querydsl.dto.UserDto;
import com.mari.querydsl.entity.Member;
import com.mari.querydsl.entity.QMember;
import com.mari.querydsl.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

import static com.mari.querydsl.entity.QMember.member;

import static com.mari.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerysdslTest {

    @Autowired
    private EntityManager em;
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    @BeforeEach
    void insert(){
        queryFactory = new JPAQueryFactory(em);

        Team team1 = new Team("team1");
        Team team2 = new Team("team2");

        em.persist(team1);
        em.persist(team2);

        Member member1 = new Member("member1",20,team1);
        Member member2 = new Member("member2",30,team1);
        Member member3 = new Member("member3",25,team2);
        Member member4 = new Member("member4",35,team2);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();
    }

    @Test
    void jpql(){
       Member member =  em.createQuery("select m from Member m where m.username=:username",Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertEquals(member.getAge(),20);

    }
    @Test
    void queryDsl(){
        Member result = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        Assertions.assertEquals(result.getAge(),20);
    }

    @Test
    void search(){
        Member result = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),
                        member.age.eq(20))
                .fetchOne();

        Team result2 = queryFactory
                .selectFrom(team)
                .where(team.name.eq("team1"))
                .fetchOne();

        Assertions.assertEquals(result.getTeam().getName(),result2.getName());
    }


    @Test
    void searchList(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();
        Assertions.assertEquals(result.size(),4);

        QueryResults<Member> result2 = queryFactory
                .selectFrom(member)
                .fetchResults();
        Assertions.assertEquals(result2.getTotal(),4);
    }

    @Test
    void sort(){
        em.persist(new Member("member5",100,null));
        em.persist(new Member("member6",100,null));
        em.persist(new Member(null,40));
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc().nullsLast(),member.age.desc())
                .fetch();
        result.stream().forEach(System.out::println);
        Assertions.assertEquals(result.get(0).getAge(),100);

    }

    @Test
    void paging(){
        QueryResults<Member> result = queryFactory.selectFrom(member)
                .orderBy(member.id.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        assertThat(result.getTotal()).isEqualTo(4);
        assertThat(result.getLimit()).isEqualTo(2);
        assertThat(result.getOffset()).isEqualTo(1);
        result.getResults().stream().forEach(System.out::println);
    }

    @Test
    void group(){
        List<Tuple> result = queryFactory.select(
                member.count()
                ,member.age.max()
                ,member.age.min())
                .from(member)
                .fetch();
       // result.stream().forEach(System.out::println);
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.max())).isEqualTo(35);
        assertThat(tuple.get(member.age.min())).isEqualTo(20);

    }
    @Test
    void joinAndGroupBy(){
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.id)
                .fetch();
        result.stream().forEach(System.out::println);
        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);

    }
    @Test
    void join(){
        List<Member> result = queryFactory.select(member)
                .from(member)
                .join(member.team , team)
                .where(team.name.eq("team1"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1","member2");
    }

    @Test
    void theta_join(){
        em.persist(new Member("team1"));
        em.persist(new Member("team2"));
        em.persist(new Member("team3"));

        List<Member> result = queryFactory
                .select(member)
                .from(member,team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("team1","team2");
    }
    @Test
    void left_join(){
        em.persist(new Member("member5",100,null));
        em.persist(new Member("member6",100,null));
        List<Tuple> result = queryFactory.select(member,team)
                .from(member)
                .leftJoin(member.team , team)
               // .where(team.name.eq("team1"))
                .fetch();

        result.stream().forEach(System.out::println);
    }
    @Test
    void left_join_on(){
        em.persist(new Member("member5",100,null));
        em.persist(new Member("member6",100,null));
        List<Tuple> result = queryFactory.select(member,team)
                .from(member)
                .leftJoin(member.team , team)
                .on(team.name.eq("team1"))
                .fetch();

        result.stream().forEach(System.out::println);
    }

    @Test
    void join_where(){
        em.persist(new Member("member5",100,null));
        em.persist(new Member("member6",100,null));
        List<Tuple> result = queryFactory.select(member,team)
                .from(member)
                .join(member.team , team)
                .where(team.name.eq("team1"))
                .fetch();

        result.stream().forEach(System.out::println);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void join_where_on(){
        em.persist(new Member("member5",100,null));
        em.persist(new Member("member6",100,null));
        List<Tuple> result = queryFactory.select(member,team)
                .from(member)
                .join(member.team , team)
                .on(team.name.eq("team1"))
                .fetch();

        result.stream().forEach(System.out::println);
        //assertThat(result.size()).isEqualTo(2);
    }

    @Autowired
    private EntityManagerFactory emf;
    @Test
    void not_fetch_join(){
        em.flush();
        em.clear();
        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1"))
                .join(member.team , team)
                .fetchOne();
        boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(isLoaded).as("non_fetch_join").isEqualTo(false);
    }

    @Test
    void fetch_join(){
        em.flush();
        em.clear();
        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1"))
                .join(member.team , team).fetchJoin()
                .fetchOne();
        boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(isLoaded).as("fetch_join").isEqualTo(true);
    }

    @Test
    void sub_query(){
        QMember subMember = new QMember("subMember");
        List<Member> findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.age.eq(
                                JPAExpressions
                                        .select(subMember.age.max())
                                        .from(subMember)))
                .fetch();
        assertThat(findMember.get(0).getAge()).isEqualTo(35);
    }
    @Test
    void sub_query_goe(){
        QMember subMember = new QMember("subMember");
        List<Member> findMembers = queryFactory
                .selectFrom(member)
                .where(
                        member.age.goe(
                                JPAExpressions
                                        .select(subMember.age.avg())
                                        .from(subMember)))
                .fetch();

    }

    @Test
    void sub_query_in(){
        QMember subMember = new QMember("subMember");
        List<Member> findMembers = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(subMember.age)
                                .from(subMember)
                .where(subMember.age.goe(30))))
                .fetch();
        assertThat(findMembers.size()).isEqualTo(2);
        assertThat(findMembers).extracting("age").containsExactly(30,35);
    }

    @Test
    void sub_query_select(){
        QMember subMember = new QMember("subMember");
        List<Tuple> findMembers =
                queryFactory
                .select(member.username,
                        JPAExpressions.select(subMember.age.min()).from(subMember))
                .from(member)
                .fetch();
        findMembers.forEach(System.out::println);
    }

    @Test
    void simple_case(){
    List<String> result = queryFactory
            .select(
                member.age
                        .when(20).then("스무살")
                        .when(30).then("서른살")
                        .otherwise("기타")
            ).from(member)
            .fetch();
        result.forEach(r-> System.out.println("member : "+ r));
    }

    @Test
    void expressions(){
        List<Tuple> result = queryFactory.select(member, Expressions.constant("A"))
                .from(member)
                .fetch();
        result.forEach(r-> System.out.println("member : "+ r));
    }

    @Test
    void concat(){
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch();
        result.forEach(r-> System.out.println("member : "+ r));
    }


    @Test
    void select_as_tuple(){
        List<Tuple> result = queryFactory
                .select(member.username, member.id)
                .from(member)
                .fetch();
        for (Tuple tuple : result){
            System.out.println("member : " + tuple.get(member.username));
            System.out.println("member : " + tuple.get(member.id));
        }
    }
    @Test
    void select_as_tuple2(){
        List<Tuple> result = queryFactory
                .select(member.username, member.id)
                .from(member)
                .fetch();
        for (Tuple tuple : result){
            System.out.println("member : " + tuple.get(0,String.class));
            System.out.println("member : " + tuple.get(1,Long.class));
        }
    }

    @Test
    void projection_jpql(){
        List<MemberDto> result = em.createQuery("select new com.mari.querydsl.dto.MemberDto(m.username, m.age) from Member m ")
                .getResultList();
        for (MemberDto memberDto : result) {
            System.out.println(memberDto);
        }
    }

    @Test
    void projection_querydsl(){
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println(memberDto);
        }
    }
    @Test
    void projection_querydsl2(){
        List<UserDto> result = queryFactory
                .select(Projections.bean(UserDto.class,
                        (member.username).as("name"),
                        member.age))
                .from(member)
                .fetch();
        for (UserDto memberDto : result) {
            System.out.println(memberDto);
        }
    }

    @Test
    void projection_querydsl3(){
        List<UserDto> result = queryFactory
                .select(Projections.bean(UserDto.class,
                        (member.username).as("name"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(member.age.avg().intValue())
                                        .from(member), "age")
                                ))
                .from(member)
                .fetch();
        for (UserDto memberDto : result) {
            System.out.println(memberDto);
        }
    }
    @Test
    void query_projection(){
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();
        for (MemberDto memberDto : result) {
            System.out.println(memberDto);
        }
    }

    @Test
    void boolean_builder() {
        String username = "member2";
        int age = 30;
        List<Member> result = selectWithUsernameAndAge(username, age);
        for (Member member1 : result) {
            System.out.println(
                    member1
            );
        }
    }

    private List<Member> selectWithUsernameAndAge(String username,int age){
            BooleanBuilder builder = new BooleanBuilder();
            if(StringUtils.isNotBlank(username)){
                builder.and(member.username.eq(username));
            }
            if(age!=0){
                builder.or(member.age.goe(age));
            }
           return queryFactory.select(member)
                    .from(member)
                    .where(builder)
                    .fetch();
    }

    @Test
    void condition_where(){
        String username = "member2";
        int age = 30;
        List<Member> result = selectWithWithBooleanEx(username, age);
        for (Member member1 : result) {
            System.out.println(
                    member1
            );
        }
    }

    private List<Member> selectWithWithBooleanEx(String username, int age) {
        return queryFactory.select(member)
                .from(member)
                .where(eqUsernameOrGoeAge(username,age))
                .fetch();
    }

    private BooleanExpression isUserParam(String username){
        if(StringUtils.isNotBlank(username)){
            return member.username.eq(username);
        }
        else return null;
    }
    private BooleanExpression isAgeParam(int age){
        if(age!=0){
            return member.age.goe(age);
        }
        else return null;
    }

    private BooleanExpression eqUsernameOrGoeAge(String username, int age){
        return isUserParam(username).or(isAgeParam(age));
    }

    @Test
    @Commit
    void bulkUpdate(){
        queryFactory.update(member)
                .set(member.age,member.age.add(1))
                .where(member.age.lt(30))
                .execute();

        em.flush();
        em.clear();
    }

    @Test
    @Commit
    void bulkDelete(){
        queryFactory.delete(member)
                .where(member.age.lt(30))
                .execute();
    }
}
