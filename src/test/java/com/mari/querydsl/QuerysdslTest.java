package com.mari.querydsl;

import com.mari.querydsl.entity.Member;
import com.mari.querydsl.entity.QMember;
import com.mari.querydsl.entity.Team;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
}
