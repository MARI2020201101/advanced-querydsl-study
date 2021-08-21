package com.mari.querydsl;

import com.mari.querydsl.entity.Member;
import com.mari.querydsl.entity.QMember;
import com.mari.querydsl.entity.Team;
import com.querydsl.core.QueryFactory;
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

@SpringBootTest
@Transactional
public class QuerysdslTest {

    @Autowired
    private EntityManager em;

    @BeforeEach
    void insert(){
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
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;

        Member result = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        Assertions.assertEquals(result.getAge(),20);
    }

}
